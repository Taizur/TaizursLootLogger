# CSV Lifetime Drop Ledger

CSV Lifetime Drop Ledger is a RuneLite plugin that tracks lifetime NPC loot drops.

The plugin records dropped items, total quantities, tradeable status, and Grand Exchange prices in a local CSV file. 
This makes it easy to keep a long-term record of loot across multiple accounts outside a single RuneLite session.

## Features

- Currently tracks NPC loot received through RuneLite’s NPC loot events. 
- Loot from chests, caskets, skilling, and similar sources is not yet supported
- Stores lifetime item quantities by item ID
- Records item name, tradeable status, total quantity, and GE price
- Saves loot data to a local CSV file for easy view in spreadsheets
- Loads existing loot data when RuneLite starts
- Skips malformed CSV rows instead of failing to start

Note: The plugin exports standard CSV data only.
       Spreadsheet setup, formulas, and custom integrations are left up to the user

## Data Storage

Loot data is stored locally in RuneLite's directory:

```text
.runelite/taizurs-loot-logger/loot-log.csv
```