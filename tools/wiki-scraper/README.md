# BI Wiki scraper

Scrapes server-config documentation from [community.bistudio.com](https://community.bistudio.com/wiki/)
and writes a local AI-friendly database to `docs/wiki-db/`.

**Output:**
- `docs/wiki-db/params/<game>.json` — structured param records for exact lookup
- `docs/wiki-db/guides/<slug>.md` — cleaned Markdown guides for context injection

## Prerequisites

Python 3.10+, internet access.

```bash
cd tools/wiki-scraper
python -m venv .venv
source .venv/bin/activate    # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

## Usage

```bash
# Smoke test — one page, verify UA works (expect HTTP 200)
python scrape.py --game arma3 --only "Arma 3: Server Config File"

# One game
python scrape.py --game arma3

# Multiple games
python scrape.py --game arma3 dayz

# All games (full refresh)
python scrape.py

# Force fresh fetch (bypass 15-min cache)
python scrape.py --no-cache
```

## Adding pages

Edit `manifest.yaml`. Each entry:

```yaml
- title: "Arma 3: My New Page"    # exact wiki page title
  slug: arma3-my-new-page          # output filename (no .md/.json extension)
  file: server.cfg                 # which config file params belong to
  extractor: table                 # table | deflist | codeblock | skip
```

Extractor hints:
- `table` — params in wikitable rows (most common on BI wiki)
- `deflist` — params as `; name : description` definition lists
- `codeblock` — params extracted from `name = value; // comment` code blocks
- `skip` — guide only, no param extraction (narrative/overview pages)

## Troubleshooting

**HTTP 403 on content requests:** BI wiki blocks some user-agents on content
endpoints. The scraper uses a browser-like UA string. If 403 persists:
1. Check `API_URL` in `scrape.py` — wiki may have moved the API endpoint.
2. Try the Special:Export fallback: `action=query&export=1&titles=<title>` gives
   XML wikitext for any page, often less restricted.
3. If all API paths are blocked, switch to direct HTML scrape (same UA) and
   disable the `api.php` fetch — replace `prop=text` HTML source with raw page HTML.

**Low param count warning:** The automatic extractor may miss params on
atypically structured pages. Inspect the page's wikitext, pick the right
`extractor` hint in the manifest, or file an issue.

**Page not found (missingtitle):** The wiki page title changed. Check
`community.bistudio.com/wiki/<Title>` and update `manifest.yaml`.

## Cache

Responses cached in `.cache/` (15 min TTL). Safe to delete; next run refetches.
`.cache/` is gitignored.
