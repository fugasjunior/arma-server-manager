#!/usr/bin/env python3
"""
BI Wiki scraper — builds docs/wiki-db/ from community.bistudio.com.

Usage:
  python scrape.py                              # all games
  python scrape.py --game arma3                # one game
  python scrape.py --game arma3 --only "Arma 3: Server Config File"  # one page (smoke test)
  python scrape.py --no-cache                  # force fresh fetch

Output: ../../docs/wiki-db/params/<game>.json + ../../docs/wiki-db/guides/*.md
"""

import argparse
import hashlib
import json
import logging
import os
import re
import sys
import time
from datetime import datetime, timezone
from pathlib import Path
from typing import Optional

import mwparserfromhell
import requests
import yaml
from markdownify import markdownify as md

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
SCRIPT_DIR = Path(__file__).parent.resolve()
REPO_ROOT = SCRIPT_DIR.parent.parent
OUT_ROOT = REPO_ROOT / "docs" / "wiki-db"
CACHE_DIR = SCRIPT_DIR / ".cache"

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------
API_URL = "https://community.bistudio.com/wikidata/api.php"
WIKI_BASE = "https://community.bistudio.com/wiki"
USER_AGENT = (
    "Mozilla/5.0 (compatible; arma-server-manager-wiki-scraper/1.0; "
    "+https://github.com/fugasjunior/arma-server-manager; mail@martinfunda.cz)"
)
RATE_LIMIT_S = 1.2          # seconds between API requests
CACHE_TTL_S = 15 * 60       # 15 minutes

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# HTTP / caching
# ---------------------------------------------------------------------------
_session: Optional[requests.Session] = None


def session() -> requests.Session:
    global _session
    if _session is None:
        _session = requests.Session()
        _session.headers.update({"User-Agent": USER_AGENT})
    return _session


def _cache_path(key: str) -> Path:
    return CACHE_DIR / (hashlib.sha256(key.encode()).hexdigest() + ".json")


def fetch_api(params: dict, use_cache: bool = True) -> dict:
    """Fetch MediaWiki API, return parsed JSON. Caches responses 15 min."""
    cache_key = json.dumps(params, sort_keys=True)
    cp = _cache_path(cache_key)

    if use_cache and cp.exists():
        age = time.time() - cp.stat().st_mtime
        if age < CACHE_TTL_S:
            log.debug("Cache hit (%ds old): %s", int(age), params.get("page", ""))
            return json.loads(cp.read_text())

    params = {**params, "format": "json", "formatversion": "2"}
    try:
        resp = session().get(API_URL, params=params, timeout=30)
    except requests.RequestException as e:
        log.error("Request failed: %s", e)
        raise

    if resp.status_code == 403:
        log.error(
            "HTTP 403 Forbidden — user-agent not accepted. "
            "Try a different UA or check if the wiki requires login."
        )
        resp.raise_for_status()
    if resp.status_code == 429:
        retry = int(resp.headers.get("Retry-After", 30))
        log.warning("Rate limited, sleeping %ds", retry)
        time.sleep(retry)
        return fetch_api(params, use_cache)
    resp.raise_for_status()

    data = resp.json()
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    cp.write_text(json.dumps(data))
    time.sleep(RATE_LIMIT_S)
    return data


# ---------------------------------------------------------------------------
# Guide extraction (HTML → Markdown)
# ---------------------------------------------------------------------------
_STRIP_PATTERNS = [
    re.compile(r'<div[^>]+class="[^"]*(?:noprint|toc|mw-editsection)[^"]*"[^>]*>.*?</div>', re.DOTALL | re.IGNORECASE),
    re.compile(r'<table[^>]+class="[^"]*(?:navbox|toc)[^"]*"[^>]*>.*?</table>', re.DOTALL | re.IGNORECASE),
]


def extract_guide(html: str) -> str:
    """Convert rendered wiki HTML to clean Markdown."""
    for pat in _STRIP_PATTERNS:
        html = pat.sub("", html)
    result = md(html, heading_style="ATX", bullets="-", code_language="")
    # Collapse 3+ blank lines to 2
    result = re.sub(r'\n{3,}', '\n\n', result)
    return result.strip()


# ---------------------------------------------------------------------------
# Param extraction (wikitext → structured records)
# ---------------------------------------------------------------------------

# BI wiki wraps param names in <syntaxhighlight lang="cpp" inline>paramName = value;</syntaxhighlight>
_SH_TAG = re.compile(r'<syntaxhighlight[^>]*>(.*?)</syntaxhighlight>', re.DOTALL | re.IGNORECASE)
# Match: identifier (optionally followed by [] for arrays) = anything
_PARAM_ASSIGN = re.compile(r'^([A-Za-z_][A-Za-z0-9_]*)(\[\])?\s*=')
# {{GVI|arma3|1.70}} → "1.70"
_GVI = re.compile(r'\{\{GVI\|[^|]+\|([^}|]+)', re.IGNORECASE)
# {{n/a}} or {{n/a|...}}
_NA = re.compile(r'\{\{n/a[^}]*\}\}', re.IGNORECASE)


def _strip_syntaxhighlight(text: str) -> str:
    """Replace <syntaxhighlight> tags and {{hl|c=...}} templates with their inner text."""
    text = _SH_TAG.sub(lambda m: m.group(1), text)
    # {{hl|c= paramName = value}} → paramName = value
    text = re.sub(r'\{\{hl\|c=\s*([^}]+)\}\}', lambda m: m.group(1).strip(), text, flags=re.IGNORECASE)
    # {{hl|paramName}} → paramName
    text = re.sub(r'\{\{hl\|([^}|]+)\}\}', lambda m: m.group(1).strip(), text, flags=re.IGNORECASE)
    return text


def _parse_since(raw: str) -> Optional[str]:
    m = _GVI.search(raw)
    if m:
        return m.group(1).strip()
    if _NA.search(raw):
        return None
    cleaned = _clean_wikitext_value(raw)
    return cleaned if cleaned else None


def extract_params_table(wikitext: str, game: str, file: str, source_url: str) -> list[dict]:
    """
    Extract params from BI wiki wikitables.

    BI wiki format (server.cfg pages):
        {| class="wikitable ..."
        ! Parameter !! Default !! Description !! Since
        |-
        | <syntaxhighlight lang="cpp" inline>paramName = value;</syntaxhighlight> || default || desc || {{GVI|...}}
        |}

    Cells may be || separated on one line, or on separate lines starting with |.
    """
    params = []
    seen: set[str] = set()

    # Find all wikitable blocks
    table_blocks = re.findall(r'\{\|.*?\|\}', wikitext, re.DOTALL)

    for block in table_blocks:
        # Split into rows on |- (row separator)
        rows = re.split(r'^\s*\|-', block, flags=re.MULTILINE)

        for row in rows:
            # Skip header rows (lines starting with !)
            if re.match(r'\s*!', row):
                continue

            # Collect cell lines: lines starting with | (but not ||)
            # First, try inline format: | cell1 || cell2 || cell3 ...
            # Find the first content line starting with |
            cell_line_match = re.search(r'^\s*\|(.+)', row, re.MULTILINE)
            if not cell_line_match:
                continue

            raw_line = cell_line_match.group(1)
            # Split on || to get inline cells
            raw_cells = [c.strip() for c in re.split(r'\|\|', raw_line)]

            # Also collect multi-line cells (subsequent lines starting with |)
            extra_lines = re.findall(r'^\s*\|([^|][^\n]*)', row, re.MULTILINE)
            if len(extra_lines) > 1:
                raw_cells = [c.strip() for c in extra_lines]

            if not raw_cells:
                continue

            # Cell 0: param definition — strip syntaxhighlight, extract name
            cell0 = _strip_syntaxhighlight(raw_cells[0])
            m = _PARAM_ASSIGN.match(cell0.strip())
            if not m:
                continue
            name = m.group(1)
            is_array = m.group(2) is not None

            if name in seen:
                continue
            seen.add(name)

            # Cell 1: default value
            default_raw = raw_cells[1] if len(raw_cells) > 1 else ""
            default = _clean_wikitext_value(_strip_syntaxhighlight(default_raw)) or None

            # Cell 2: description
            desc_raw = raw_cells[2] if len(raw_cells) > 2 else ""
            desc = _clean_wikitext_value(_strip_syntaxhighlight(desc_raw))

            # Cell 3: since (version)
            since_raw = raw_cells[3] if len(raw_cells) > 3 else ""
            since = _parse_since(since_raw)

            # Build example from cell0 text (the full assignment expression)
            example_match = re.search(r'([A-Za-z_][A-Za-z0-9_]*\s*(?:\[\])?\s*=\s*[^;]+;?)', cell0)
            example = example_match.group(1).strip() if example_match else None

            type_ = "array" if is_array else _infer_type(default, example)

            params.append(_make_param(
                name, game, file, source_url,
                description=desc,
                default=default,
                type_=type_,
                example=example,
                since=since,
            ))

    return params


def _infer_type(default: Optional[str], example: Optional[str]) -> Optional[str]:
    """Guess param type from default value or example."""
    candidates = [s for s in [default, example] if s]
    for s in candidates:
        s = s.strip().strip('"').strip("'")
        if s in ("0", "1", "true", "false"):
            return "boolean"
        try:
            int(s)
            return "integer"
        except ValueError:
            pass
        try:
            float(s)
            return "number"
        except ValueError:
            pass
        if s.startswith('"') or s.startswith("'"):
            return "string"
    return None


def extract_params_sections(wikitext: str, game: str, file: str, source_url: str) -> list[dict]:
    """
    Extract params from section-per-param pages (BI wiki startup params and Reforger config).

    Pattern:
        === paramName ===
        Description prose.
        {{GVI|game|version}}  (since)

    Top-level (==) and truly structural sections (e.g. "Summary", "Root") are skipped.
    Only 3-level (===) sections where the name looks like a param identifier are collected.

    BI wiki startup params also use {{ArgTitle|3|paramName|{{GVI|...|ver}}}} to introduce
    sub-params within a section — those are also extracted.
    """
    params = []
    seen: set[str] = set()

    # Structural/category headers to ignore (case-insensitive)
    SKIP_SECTIONS = {
        "steam", "windows", "linux", "path definition", "spaces", "relative vs absolute",
        "display options", "game loading speedup", "profile options", "misc.", "misc",
        "client network options", "server options", "performance", "developer options",
        "summary", "root", "game", "operating", "a2s", "rcon", "gameproperties",
        "mod", "mods", "modids", "admins", "game properties", "operating", "template",
        "example", "examples", "notes", "see also", "references", "changelog", "history",
        "overview", "details", "introduction", "description",
    }

    # Split by 3-level (===) sections; also capture 2-level for context
    # Strategy: find all === X === blocks with their content
    section_re = re.compile(
        r'={3}\s*(?P<name>[^=\n]+?)\s*={3}\s*\n(?P<body>.*?)(?=\n={2,}|\Z)',
        re.DOTALL,
    )

    for m in section_re.finditer(wikitext):
        raw_name = m.group("name").strip()
        body = m.group("body")

        # Strip leading -/ from startup params (e.g. -window, -config)
        name = raw_name.lstrip("-")
        # Must look like an identifier
        if not re.match(r'^[A-Za-z_][A-Za-z0-9_]*$', name):
            continue
        if name.lower() in SKIP_SECTIONS:
            continue

        desc, since = _extract_section_desc_and_since(body)

        if name not in seen:
            seen.add(name)
            params.append(_make_param(name, game, file, source_url, description=desc, since=since))

    # Also extract {{ArgTitle|3|paramName|{{GVI|...|ver}}}} sub-param introductions
    argtitle_re = re.compile(
        r'\{\{ArgTitle\|3\|([A-Za-z_][A-Za-z0-9_]*)\|(?:\{\{GVI\|[^|]+\|([^}|]+))?',
        re.IGNORECASE,
    )
    for m in argtitle_re.finditer(wikitext):
        name = m.group(1).strip()
        since = m.group(2).strip() if m.group(2) else None
        if name not in seen and name.lower() not in SKIP_SECTIONS:
            seen.add(name)
            params.append(_make_param(name, game, file, source_url, since=since))

    return params


def _extract_section_desc_and_since(body: str) -> tuple[str, Optional[str]]:
    """Pull first prose paragraph and {{GVI}} version from a section body."""
    # Extract since from GVI template first
    since = _parse_since(body)

    # Remove Feature/note templates
    body_clean = re.sub(r'\{\{Feature\|[^}]+(?:\}\}[^{]*)*\}\}', '', body, flags=re.DOTALL)
    body_clean = re.sub(r'\{\{(?:Feature|ArgTitle|GVI|hl|Link|Icon|arma\w*)[^}]*\}\}', '', body_clean)
    # Remove ; Example blocks
    body_clean = re.sub(r';\s*Example.*?(?=\n\n|\Z)', '', body_clean, flags=re.DOTALL)
    # Remove code blocks
    body_clean = re.sub(r'<syntaxhighlight.*?</syntaxhighlight>', '', body_clean, flags=re.DOTALL | re.IGNORECASE)
    # Remove wiki links, keep text
    body_clean = re.sub(r'\[\[(?:[^|\]]+\|)?([^\]]+)\]\]', r'\1', body_clean)
    body_clean = re.sub(r'\[http[^\s\]]+ ([^\]]+)\]', r'\1', body_clean)
    # Remove markup
    body_clean = re.sub(r"'''?([^']+)'''?", r'\1', body_clean)
    body_clean = re.sub(r'<[^>]+>', '', body_clean)
    body_clean = re.sub(r'\{\{[^}]+\}\}', '', body_clean)

    # Take first non-empty paragraph (≥ 20 chars)
    desc = ""
    for para in re.split(r'\n{2,}', body_clean):
        p = para.strip().lstrip(":*#;").strip()
        if len(p) >= 20:
            desc = p
            break

    return desc, since


def extract_params_codeblock(wikitext: str, game: str, file: str, source_url: str) -> list[dict]:
    """
    Extract params from <syntaxhighlight lang="cpp"> blocks.

    DayZ / similar pages embed the full config as a code example with inline comments:
        hostname = "EXAMPLE NAME";   // Server name
        maxPlayers = 60;             // Maximum amount of players

    Each syntaxhighlight block is scanned. Lines that do NOT start with // and contain
    an assignment are treated as param definitions.
    """
    params = []
    seen: set[str] = set()

    # Extract all syntaxhighlight blocks (wikitext has raw <syntaxhighlight> tags)
    blocks = re.findall(r'<syntaxhighlight[^>]*>(.*?)</syntaxhighlight>', wikitext, re.DOTALL | re.IGNORECASE)

    # Also try extracting from raw wikitext if no blocks found (fallback)
    if not blocks:
        blocks = [wikitext]

    line_re = re.compile(
        r'^([A-Za-z_][A-Za-z0-9_]*)(\[\])?\s*=\s*([^;/\n]+?)\s*;?\s*(?://\s*(.*))?$'
    )

    for block in blocks:
        for line in block.splitlines():
            line = line.strip()
            if not line or line.startswith("//"):
                continue
            m = line_re.match(line)
            if not m:
                continue
            name = m.group(1)
            is_array = m.group(2) is not None
            raw_default = m.group(3).strip() if m.group(3) else None
            desc = m.group(4).strip() if m.group(4) else ""

            if name in seen:
                continue
            seen.add(name)

            # Clean default: strip trailing comment cruft
            default = raw_default.split("//")[0].strip().rstrip(";").strip() if raw_default else None
            type_ = "array" if is_array else _infer_type(default, None)

            params.append(_make_param(
                name, game, file, source_url,
                description=desc,
                default=default,
                type_=type_,
            ))

    return params


def extract_params_deflist(wikitext: str, game: str, file: str, source_url: str) -> list[dict]:
    """
    Extract params from definition-list style: ; paramName : description
    """
    params = []
    for m in re.finditer(r'^;\s*([A-Za-z_][A-Za-z0-9_]*)\s*\n?:([^\n]+)', wikitext, re.MULTILINE):
        name = m.group(1).strip()
        desc = _clean_wikitext_value(m.group(2))
        params.append(_make_param(name, game, file, source_url, description=desc))
    return params


def _clean_wikitext_value(raw: str) -> str:
    """Strip common wiki markup to plain text."""
    if not raw:
        return ""
    # Remove [[link|text]] → text, [[link]] → link
    raw = re.sub(r'\[\[(?:[^|\]]+\|)?([^\]]+)\]\]', r'\1', raw)
    # Remove {{ic|text}} → text (inline code template)
    raw = re.sub(r'\{\{ic\|([^}]+)\}\}', r'\1', raw, flags=re.IGNORECASE)
    # Remove other {{templates}}
    raw = re.sub(r'\{\{[^}]+\}\}', '', raw)
    # Remove '''bold''' and ''italic''
    raw = re.sub(r"'''?([^']+)'''?", r'\1', raw)
    # Remove <ref>...</ref>
    raw = re.sub(r'<ref[^>]*>.*?</ref>', '', raw, flags=re.DOTALL)
    # Remove remaining HTML tags
    raw = re.sub(r'<[^>]+>', '', raw)
    return raw.strip()


def _make_param(
    name: str,
    game: str,
    file: str,
    source_url: str,
    *,
    description: str = "",
    default: Optional[str] = None,
    type_: Optional[str] = None,
    example: Optional[str] = None,
    since: Optional[str] = None,
    deprecated: bool = False,
    confidence: str = "auto",
) -> dict:
    return {
        "name": name,
        "game": game,
        "file": file,
        "type": type_,
        "default": default,
        "description": description,
        "example": example,
        "since": since,
        "deprecated": deprecated,
        "confidence": confidence,
        "source": source_url,
    }


# ---------------------------------------------------------------------------
# Core scrape logic
# ---------------------------------------------------------------------------
def scrape_page(entry: dict, game: str, use_cache: bool = True) -> tuple[Optional[str], list[dict]]:
    """
    Scrape one page entry from the manifest.
    Returns (guide_markdown, params_list).
    """
    title = entry["title"]
    slug = entry["slug"]
    file = entry.get("file", "unknown")
    extractor = entry.get("extractor", "table")
    source_url = f"{WIKI_BASE}/{title.replace(' ', '_')}"

    log.info("Fetching: %s", title)

    data = fetch_api({
        "action": "parse",
        "page": title,
        "prop": "text|wikitext|sections",
    }, use_cache=use_cache)

    if "error" in data:
        code = data["error"].get("code", "unknown")
        info = data["error"].get("info", "")
        if code == "missingtitle":
            log.warning("Page not found (missingtitle): %s — check manifest title", title)
        else:
            log.error("API error for '%s': %s — %s", title, code, info)
        return None, []

    parse = data.get("parse", {})
    html = parse.get("text", "")
    wikitext = parse.get("wikitext", "")
    scraped_at = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

    # --- Guide ---
    guide_body = extract_guide(html)
    frontmatter = (
        f"---\n"
        f"game: {game}\n"
        f"slug: {slug}\n"
        f"file: {file}\n"
        f"source: {source_url}\n"
        f"scraped: {scraped_at}\n"
        f"---\n\n"
    )
    guide_md = frontmatter + guide_body

    # --- Params ---
    params: list[dict] = []
    if extractor == "table":
        params = extract_params_table(wikitext, game, file, source_url)
    elif extractor == "sections":
        params = extract_params_sections(wikitext, game, file, source_url)
    elif extractor == "deflist":
        params = extract_params_deflist(wikitext, game, file, source_url)
    elif extractor == "codeblock":
        params = extract_params_codeblock(wikitext, game, file, source_url)
    elif extractor == "skip":
        log.info("  Extractor=skip for '%s', guide only.", title)
    else:
        log.warning("  Unknown extractor '%s' for '%s', skipping params.", extractor, title)

    log.info("  → guide: %d chars, params: %d extracted", len(guide_md), len(params))
    if params and len(params) < 3:
        log.warning("  Low param count (%d) for '%s' — extraction may need tuning", len(params), title)

    return guide_md, params


def run(games_filter: Optional[list[str]], only_title: Optional[str], use_cache: bool) -> None:
    manifest_path = SCRIPT_DIR / "manifest.yaml"
    with open(manifest_path) as f:
        manifest = yaml.safe_load(f)

    all_games: dict = manifest.get("games", {})
    if games_filter:
        all_games = {g: v for g, v in all_games.items() if g in games_filter}
        unknown = set(games_filter) - set(all_games)
        if unknown:
            log.error("Unknown game(s): %s. Available: %s", unknown, list(manifest["games"]))
            sys.exit(1)

    out_params = OUT_ROOT / "params"
    out_guides = OUT_ROOT / "guides"
    out_params.mkdir(parents=True, exist_ok=True)
    out_guides.mkdir(parents=True, exist_ok=True)

    totals = {"pages": 0, "params": 0, "errors": 0}

    for game, entries in all_games.items():
        game_params: list[dict] = []

        # Merge with existing params file (so --game or --only don't wipe other games' data)
        existing_params_path = out_params / f"{game}.json"
        existing_by_name: dict[str, dict] = {}
        if existing_params_path.exists():
            try:
                existing = json.loads(existing_params_path.read_text())
                existing_by_name = {p["name"]: p for p in existing}
            except Exception:
                pass

        for entry in entries:
            if only_title and entry["title"] != only_title:
                continue

            try:
                guide_md, params = scrape_page(entry, game, use_cache=use_cache)
            except Exception as e:
                log.error("Failed to scrape '%s': %s", entry["title"], e)
                totals["errors"] += 1
                continue

            totals["pages"] += 1

            if guide_md is not None:
                guide_path = out_guides / f"{entry['slug']}.md"
                guide_path.write_text(guide_md, encoding="utf-8")

            for p in params:
                existing_by_name[p["name"]] = p
                totals["params"] += 1

        # Rebuild merged list sorted by name
        merged = sorted(existing_by_name.values(), key=lambda p: p["name"].lower())
        existing_params_path.write_text(
            json.dumps(merged, indent=2, ensure_ascii=False),
            encoding="utf-8",
        )
        log.info("Wrote %s (%d params)", existing_params_path.relative_to(REPO_ROOT), len(merged))

    _update_readme(totals)
    log.info(
        "Done. Pages: %d, params: %d, errors: %d",
        totals["pages"], totals["params"], totals["errors"],
    )


def _update_readme(totals: dict) -> None:
    readme_path = OUT_ROOT / "README.md"
    scraped_at = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")

    # Count actual output files
    params_files = list((OUT_ROOT / "params").glob("*.json"))
    guides_files = list((OUT_ROOT / "guides").glob("*.md"))

    total_params = 0
    for pf in params_files:
        try:
            total_params += len(json.loads(pf.read_text()))
        except Exception:
            pass

    content = f"""# BI Wiki server-config database

Auto-generated from [Bohemia Interactive community wiki](https://community.bistudio.com/wiki/).
Do not edit manually — re-run `tools/wiki-scraper/scrape.py` to refresh.

**Last scraped:** {scraped_at}
**Params:** {total_params} across {len(params_files)} game(s)
**Guide files:** {len(guides_files)}

## License

Source content © Bohemia Interactive, licensed under
[CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).
Scraper output under same license.

## Usage

**Param lookup (exact):**
```bash
# Find all params for a specific config key
grep -r "maxPlayers" docs/wiki-db/params/

# Get the full record
cat docs/wiki-db/params/arma3.json | python -c "import json,sys; [print(json.dumps(p, indent=2)) for p in json.load(sys.stdin) if p['name']=='maxPlayers']"
```

**Narrative guide lookup:**
```bash
grep -rl "mpmissions\\|keysFolder" docs/wiki-db/guides/
```

**Context injection (AI agent):** read the relevant guide MD + params JSON slice directly.

## Files

```
docs/wiki-db/
├── README.md         ← this file
├── params/
│   ├── arma3.json    ← Arma 3 config params (server.cfg, basic.cfg, startup)
│   ├── dayz.json     ← DayZ server params
│   ├── reforger.json ← Reforger server params
│   └── reforger-exp.json
└── guides/           ← one Markdown file per wiki page
```

## Refresh

```bash
cd tools/wiki-scraper
python -m venv .venv && . .venv/bin/activate
pip install -r requirements.txt

# Smoke test (one page):
python scrape.py --game arma3 --only "Arma 3: Server Config File"

# Full refresh:
python scrape.py
```
"""
    readme_path.write_text(content, encoding="utf-8")


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------
def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--game", nargs="+", metavar="GAME", help="Restrict to one or more games (arma3, dayz, reforger, reforger-exp)")
    parser.add_argument("--only", metavar="TITLE", help="Scrape only the page with this exact title (for smoke testing)")
    parser.add_argument("--no-cache", action="store_true", help="Bypass on-disk response cache")
    args = parser.parse_args()

    run(
        games_filter=args.game,
        only_title=args.only,
        use_cache=not args.no_cache,
    )


if __name__ == "__main__":
    main()
