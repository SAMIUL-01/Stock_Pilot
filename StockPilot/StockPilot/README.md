# StockPilot — Intelligent Inventory & Business Analytics System

Part I — Final Capstone Project Proposal and Prototyping (ICT 3108, Software Development and Project Management)

## Contents
- `docs/proposal.pdf` / `docs/proposal.tex` — full proposal (process model, schedule, Gantt chart, 6 UML diagrams)
- `docs/diagrams/` — DFD, Use Case, Activity, ERD, Class, Object diagrams (PNG + Graphviz source)
- `src/main/java/stockpilot/` — Java prototype (Swing GUI, 7 domain/service classes)

## Run (JDK 21)
```bash
mkdir out
javac -d out src/main/java/stockpilot/*.java
java -cp out stockpilot.MainApp
```

## Features
Dashboard KPIs, product add with validation, cart + invoice, all-or-nothing stock deduction, low-stock alerts, Smart Restock advice, stock-movement audit trail.
