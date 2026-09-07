# ActiveHub Sports Centre Management System

DIT2123 Object Oriented Modelling — Group Assignment (complete Java console solution)

## How to run (IntelliJ)

1. Open the `OOM` project in IntelliJ IDEA.
2. Ensure JDK 21+ is configured (Project Structure → SDK).
3. Run `src/Main.java`.
4. Text data files are stored in the `data/` folder (created next to the working directory).

## How to run (Terminal)

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
cd /Users/carine/IdeaProjects/OOM
javac -d out/classes src/Main.java src/activehub/*.java
java -cp out/classes Main
```

## Main menu

```
===== ACTIVEHUB SYSTEM =====
1. Facility Booking Module
2. View Rental Catalogue
3. Create Rental Transaction
4. Apply Promotion
5. Make Payment
6. Daily Report
7. Exit
```

## Suggested demo flow (for screenshots)

1. **Menu 2** — show the 8 catalogue items.
2. **Menu 1 → Create Booking** — weekday off-peak, e.g. `2026-11-03` (Tuesday) `10:00`, 10 participants, facility `F01`.
3. **Menu 3 → Create Transaction** — link booking `B001`, add `C01` x1, `E01` x2, `E02` x1, then `DONE`.
4. **Menu 4** — apply promotion (system picks best of A/B/C; for this demo expect **B**).
5. **Menu 5** — pay by Cash / Card / E-Wallet.
6. **Menu 6** — daily report.
7. **Menu 7** — exit (saves text files).

## Catalogue (8 items)

| Code | Name | Category | Price (RM) |
|------|------|----------|------------|
| E01 | Badminton Racquet | Equipment | 8.00 |
| E02 | Shuttlecock Set | Equipment | 5.00 |
| E03 | Basketball | Equipment | 6.00 |
| E04 | Futsal Ball | Equipment | 6.00 |
| C01 | Badminton Court Package (1hr) | Facility | 40.00 |
| C02 | Futsal Court Package (1hr) | Facility | 80.00 |
| A01 | Towel Set | Accessory | 3.00 |
| A02 | Locker Service | Accessory | 4.00 |

## Submission checklist

- [ ] Report PDF/Word (see `docs/ActiveHub_Group_Report.md`)
- [ ] UML class diagram exported from `docs/ActiveHub_UML_Class_Diagram.puml` (PlantUML / draw.io / Lucidchart)
- [ ] All `.java` source files under `src/`
- [ ] Text data files under `data/`
- [ ] Academic integrity + contribution form signed
- [ ] Part D reflections filled by each member
