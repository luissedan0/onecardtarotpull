#!/usr/bin/env python3
"""
Scrapes all 78 tarot card meanings from labyrinthos.co and outputs tarot_deck.json.
Run from the repo root:
    python3 scripts/scrape_tarot.py
"""

import json, re, time, urllib.request, urllib.error, sys

BASE = "https://labyrinthos.co"

# Ordered card slugs — 22 Major Arcana then 56 Minor Arcana (4 suits × 14)
CARDS = [
    # ── Major Arcana (id 0-21) ──────────────────────────────────────────────
    (0,  "The Fool",             True,  None,         0,  "the-fool-meaning-major-arcana-tarot-card-meanings"),
    (1,  "The Magician",         True,  None,         1,  "the-magician-meaning-major-arcana-tarot-card-meanings"),
    (2,  "The High Priestess",   True,  None,         2,  "the-high-priestess-meaning-major-arcana-tarot-card-meanings"),
    (3,  "The Empress",          True,  None,         3,  "the-empress-meaning-major-arcana-tarot-card-meanings"),
    (4,  "The Emperor",          True,  None,         4,  "the-emperor-meaning-major-arcana-tarot-card-meanings"),
    (5,  "The Hierophant",       True,  None,         5,  "the-hierophant-meaning-major-arcana-tarot-card-meanings"),
    (6,  "The Lovers",           True,  None,         6,  "the-lovers-meaning-major-arcana-tarot-card-meanings"),
    (7,  "The Chariot",          True,  None,         7,  "the-chariot-meaning-major-arcana-tarot-card-meanings"),
    (8,  "Strength",             True,  None,         8,  "strength-meaning-major-arcana-tarot-card-meanings"),
    (9,  "The Hermit",           True,  None,         9,  "the-hermit-meaning-major-arcana-tarot-card-meanings"),
    (10, "Wheel of Fortune",     True,  None,         10, "the-wheel-of-fortune-meaning-major-arcana-tarot-card-meanings"),
    (11, "Justice",              True,  None,         11, "justice-meaning-major-arcana-tarot-card-meanings"),
    (12, "The Hanged Man",       True,  None,         12, "the-hanged-man-meaning-major-arcana-tarot-card-meanings"),
    (13, "Death",                True,  None,         13, "death-meaning-major-arcana-tarot-card-meanings"),
    (14, "Temperance",           True,  None,         14, "temperance-meaning-major-arcana-tarot-card-meanings"),
    (15, "The Devil",            True,  None,         15, "the-devil-meaning-major-arcana-tarot-card-meanings"),
    (16, "The Tower",            True,  None,         16, "the-tower-meaning-major-arcana-tarot-card-meanings"),
    (17, "The Star",             True,  None,         17, "the-star-meaning-major-arcana-tarot-card-meanings"),
    (18, "The Moon",             True,  None,         18, "the-moon-meaning-major-arcana-tarot-card-meanings"),
    (19, "The Sun",              True,  None,         19, "the-sun-meaning-major-arcana-tarot-card-meanings"),
    (20, "Judgement",            True,  None,         20, "judgement-meaning-major-arcana-tarot-card-meanings"),
    (21, "The World",            True,  None,         21, "the-world-meaning-major-arcana-tarot-card-meanings"),
    # ── Wands (id 22-35) ────────────────────────────────────────────────────
    (22, "Ace of Wands",         False, "WANDS",      1,  "ace-of-wands-meaning-tarot-card-meanings"),
    (23, "Two of Wands",         False, "WANDS",      2,  "two-of-wands-meaning-tarot-card-meanings"),
    (24, "Three of Wands",       False, "WANDS",      3,  "three-of-wands-meaning-tarot-card-meanings"),
    (25, "Four of Wands",        False, "WANDS",      4,  "four-of-wands-meaning-tarot-card-meanings"),
    (26, "Five of Wands",        False, "WANDS",      5,  "five-of-wands-meaning-tarot-card-meanings"),
    (27, "Six of Wands",         False, "WANDS",      6,  "six-of-wands-meaning-tarot-card-meanings"),
    (28, "Seven of Wands",       False, "WANDS",      7,  "seven-of-wands-meaning-tarot-card-meanings"),
    (29, "Eight of Wands",       False, "WANDS",      8,  "eight-of-wands-meaning-tarot-card-meanings"),
    (30, "Nine of Wands",        False, "WANDS",      9,  "nine-of-wands-meaning-tarot-card-meanings"),
    (31, "Ten of Wands",         False, "WANDS",      10, "ten-of-wands-meaning-tarot-card-meanings"),
    (32, "Page of Wands",        False, "WANDS",      11, "page-of-wands-meaning-tarot-card-meanings"),
    (33, "Knight of Wands",      False, "WANDS",      12, "knight-of-wands-meaning-tarot-card-meanings"),
    (34, "Queen of Wands",       False, "WANDS",      13, "queen-of-wands-meaning-tarot-card-meanings"),
    (35, "King of Wands",        False, "WANDS",      14, "king-of-wands-meaning-tarot-card-meanings"),
    # ── Cups (id 36-49) ─────────────────────────────────────────────────────
    (36, "Ace of Cups",          False, "CUPS",       1,  "ace-of-cups-meaning-tarot-card-meanings"),
    (37, "Two of Cups",          False, "CUPS",       2,  "two-of-cups-meaning-tarot-card-meanings"),
    (38, "Three of Cups",        False, "CUPS",       3,  "three-of-cups-meaning-tarot-card-meanings"),
    (39, "Four of Cups",         False, "CUPS",       4,  "four-of-cups-meaning-tarot-card-meanings"),
    (40, "Five of Cups",         False, "CUPS",       5,  "five-of-cups-meaning-tarot-card-meanings"),
    (41, "Six of Cups",          False, "CUPS",       6,  "six-of-cups-meaning-tarot-card-meanings"),
    (42, "Seven of Cups",        False, "CUPS",       7,  "seven-of-cups-meaning-tarot-card-meanings"),
    (43, "Eight of Cups",        False, "CUPS",       8,  "eight-of-cups-meaning-tarot-card-meanings"),
    (44, "Nine of Cups",         False, "CUPS",       9,  "nine-of-cups-meaning-tarot-card-meanings"),
    (45, "Ten of Cups",          False, "CUPS",       10, "ten-of-cups-meaning-tarot-card-meanings"),
    (46, "Page of Cups",         False, "CUPS",       11, "page-of-cups-meaning-tarot-card-meanings"),
    (47, "Knight of Cups",       False, "CUPS",       12, "knight-of-cups-meaning-tarot-card-meanings"),
    (48, "Queen of Cups",        False, "CUPS",       13, "queen-of-cups-meaning-tarot-card-meanings"),
    (49, "King of Cups",         False, "CUPS",       14, "king-of-cups-meaning-tarot-card-meanings"),
    # ── Swords (id 50-63) ───────────────────────────────────────────────────
    (50, "Ace of Swords",        False, "SWORDS",     1,  "ace-of-swords-meaning-tarot-card-meanings"),
    (51, "Two of Swords",        False, "SWORDS",     2,  "two-of-swords-meaning-tarot-card-meanings"),
    (52, "Three of Swords",      False, "SWORDS",     3,  "three-of-swords-meaning-tarot-card-meanings"),
    (53, "Four of Swords",       False, "SWORDS",     4,  "four-of-swords-meaning-tarot-card-meanings"),
    (54, "Five of Swords",       False, "SWORDS",     5,  "five-of-swords-meaning-tarot-card-meanings"),
    (55, "Six of Swords",        False, "SWORDS",     6,  "six-of-swords-meaning-tarot-card-meanings"),
    (56, "Seven of Swords",      False, "SWORDS",     7,  "seven-of-swords-meaning-tarot-card-meanings"),
    (57, "Eight of Swords",      False, "SWORDS",     8,  "eight-of-swords-meaning-tarot-card-meanings"),
    (58, "Nine of Swords",       False, "SWORDS",     9,  "nine-of-swords-meaning-tarot-card-meanings"),
    (59, "Ten of Swords",        False, "SWORDS",     10, "ten-of-swords-meaning-tarot-card-meanings"),
    (60, "Page of Swords",       False, "SWORDS",     11, "page-of-swords-meaning-tarot-card-meanings"),
    (61, "Knight of Swords",     False, "SWORDS",     12, "knight-of-swords-meaning-tarot-card-meanings"),
    (62, "Queen of Swords",      False, "SWORDS",     13, "queen-of-swords-meaning-tarot-card-meanings"),
    (63, "King of Swords",       False, "SWORDS",     14, "king-of-swords-meaning-tarot-card-meanings"),
    # ── Pentacles (id 64-77) ────────────────────────────────────────────────
    (64, "Ace of Pentacles",     False, "PENTACLES",  1,  "ace-of-pentacles-meaning-tarot-card-meanings"),
    (65, "Two of Pentacles",     False, "PENTACLES",  2,  "two-of-pentacles-meaning-tarot-card-meanings"),
    (66, "Three of Pentacles",   False, "PENTACLES",  3,  "three-of-pentacles-meaning-tarot-card-meanings"),
    (67, "Four of Pentacles",    False, "PENTACLES",  4,  "four-of-pentacles-meaning-tarot-card-meanings"),
    (68, "Five of Pentacles",    False, "PENTACLES",  5,  "five-of-pentacles-meaning-tarot-card-meanings"),
    (69, "Six of Pentacles",     False, "PENTACLES",  6,  "six-of-pentacles-meaning-tarot-card-meanings"),
    (70, "Seven of Pentacles",   False, "PENTACLES",  7,  "seven-of-pentacles-meaning-tarot-card-meanings"),
    (71, "Eight of Pentacles",   False, "PENTACLES",  8,  "eight-of-pentacles-meaning-tarot-card-meanings"),
    (72, "Nine of Pentacles",    False, "PENTACLES",  9,  "nine-of-pentacles-meaning-tarot-card-meanings"),
    (73, "Ten of Pentacles",     False, "PENTACLES",  10, "ten-of-pentacles-meaning-tarot-card-meanings"),
    (74, "Page of Pentacles",    False, "PENTACLES",  11, "page-of-pentacles-meaning-tarot-card-meanings"),
    (75, "Knight of Pentacles",  False, "PENTACLES",  12, "knight-of-pentacles-meaning-tarot-card-meanings"),
    (76, "Queen of Pentacles",   False, "PENTACLES",  13, "queen-of-pentacles-meaning-tarot-card-meanings"),
    (77, "King of Pentacles",    False, "PENTACLES",  14, "king-of-pentacles-meaning-tarot-card-meanings"),
]


def fetch(url, retries=3):
    for attempt in range(retries):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
            with urllib.request.urlopen(req, timeout=20) as resp:
                return resp.read().decode("utf-8", errors="replace")
        except Exception as e:
            if attempt < retries - 1:
                time.sleep(2)
            else:
                raise e


def clean_text(html_chunk):
    text = re.sub(r'<[^>]+>', ' ', html_chunk)
    text = re.sub(r'&nbsp;', ' ', text)
    text = re.sub(r'&#\d+;', '', text)
    text = re.sub(r'&[a-z]+;', '', text)
    text = re.sub(r'\s+', ' ', text).strip()
    return text


def extract_after_h2(html, pattern):
    m = re.search(pattern, html, re.IGNORECASE | re.DOTALL)
    if not m:
        return ""
    rest = html[m.end():]
    next_h2 = re.search(r'<h2', rest)
    chunk = rest[:next_h2.start()] if next_h2 else rest[:4000]
    return clean_text(chunk)


def extract_keywords(html, reversed_=False):
    """Extract keyword list from the keywords table."""
    tbl = re.search(r'<table[^>]*>.*?</table>', html, re.DOTALL)
    if not tbl:
        return []
    rows = re.findall(r'<tr[^>]*>(.*?)</tr>', tbl.group(), re.DOTALL)
    for row in rows:
        cells = re.findall(r'<td[^>]*>(.*?)</td>', row, re.DOTALL)
        if len(cells) >= 2:
            # Skip header row (contains <strong> or <b>)
            if '<strong>' in cells[0] or '<b>' in cells[0]:
                continue
            col = cells[1] if reversed_ else cells[0]
            text = clean_text(col)
            if text:
                return [k.strip() for k in text.split(',') if k.strip()]
    return []


def scrape_card(slug):
    url = f"{BASE}/blogs/tarot-card-meanings-list/{slug}"
    html = fetch(url)
    keywords = extract_keywords(html, reversed_=False)
    keywords_rev = extract_keywords(html, reversed_=True)
    upright = extract_after_h2(html, r'<h2[^>]*>[^<]*[Uu]pright[^<]*[Mm]eaning[^<]*</h2>')
    reversed_ = extract_after_h2(html, r'<h2[^>]*>[^<]*[Rr]evers(?:ed|al)[^<]*[Mm]eaning[^<]*</h2>')
    return keywords, keywords_rev, upright, reversed_


def main():
    output = []
    failed = []
    total = len(CARDS)

    for i, (cid, name, is_major, suit, number, slug) in enumerate(CARDS, 1):
        print(f"[{i:2}/{total}] {name} ...", end=" ", flush=True)
        try:
            kw, kw_rev, upright, rev = scrape_card(slug)
            entry = {
                "id": cid,
                "name": name,
                "isMajorArcana": is_major,
                "number": number,
                "keywords": kw,
                "keywordsReversed": kw_rev,
                "meaningUpright": upright,
                "meaningReversed": rev,
            }
            if suit:
                entry["suit"] = suit
            output.append(entry)
            print(f"✓  kw={len(kw)} kw_rev={len(kw_rev)} up={len(upright)} rev={len(rev)}")
        except Exception as e:
            print(f"✗  ERROR: {e}")
            failed.append((cid, name, slug, str(e)))
        # Polite crawl delay
        time.sleep(0.4)

    out_path = "composeApp/src/commonMain/composeResources/files/tarot_deck.json"
    import os
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(output, f, ensure_ascii=False, indent=2)

    print(f"\n✅  Wrote {len(output)} cards to {out_path}")
    if failed:
        print(f"⚠️  {len(failed)} failures:")
        for cid, name, slug, err in failed:
            print(f"   id={cid} {name}: {err}")


if __name__ == "__main__":
    main()
