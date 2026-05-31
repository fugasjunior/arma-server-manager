# BI Wiki server-config database

Auto-generated from [Bohemia Interactive community wiki](https://community.bistudio.com/wiki/).
Do not edit manually — re-run `tools/wiki-scraper/scrape.py` to refresh.

**Last scraped:** 2026-05-31 11:45 UTC
**Params:** 546 across 4 game(s)
**Guide files:** 13

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
grep -rl "mpmissions\|keysFolder" docs/wiki-db/guides/
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
