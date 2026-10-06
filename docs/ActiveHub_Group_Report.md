# DIT2123 Object Oriented Modelling
## Coursework 1 – Group Assignment Report
### ActiveHub Sports Facility Booking, Equipment Rental & Promotion System

**Programme:** Diploma in Information Technology  
**Subject Code:** DIT2123  
**Subject Name:** Object Oriented Modelling  
**Study Period:** AUG 2026 – DEC 2026  
**Deadline:** 06-11-2026 (Week 10)  
**Weighting:** 40% of final grade  

| No | Student ID | Student Name | Signature | Date |
|----|------------|--------------|-----------|------|
| 1 | *(fill in)* | *(fill in)* | | |
| 2 | *(fill in)* | *(fill in)* | | |
| 3 | *(fill in)* | *(fill in)* | | |
| 4 | *(fill in)* | *(fill in)* | | |

---

## Group Member Contribution Form

**Instruction:** Maximum THREE (3) to FOUR (4) students. A deduction of **3 marks** applies if this form is incomplete or unsigned.

| Student Name | Signature | Contribution |
|--------------|-----------|--------------|
| *(Member 1)* | | UML class diagrams (Part A), Booking module (`Booking`, `Facility`, `Customer`, `java.time` clash check), facility submenu, related report sections |
| *(Member 2)* | | Catalogue inheritance (`RentalItem` hierarchy), rental transaction module (`Transaction` / `TransactionItem` composition), billing and 10% service charge |
| *(Member 3)* | | Promotion engine (A/B/C polymorphism, best-saving rule, tie-break), payment hierarchy (Cash / Card / E-Wallet), Part C OOP discussion |
| *(Member 4)* | | Text-file persistence (`FileManager`), daily report, console UI/layout, testing, screenshots, report compilation |

*(Replace names. Every member must sign.)*

---

## Table of contents

1. Introduction and problem analysis  
2. PART A – UML Class Diagram  
3. PART B – Java programming (modules, tests, screenshots)  
4. PART C – Object-oriented concepts (justified against this system)  
5. PART D – Personal reflections  
6. Academic integrity statement  
7. Appendix  

---

## 1. Introduction and problem analysis

ActiveHub Sports Centre currently records court bookings, equipment rentals and payments in notebooks. The scenario lists five operational failures: forgotten or duplicated bookings, unknown court/equipment status, unclear handwritten rental slips, slow manual billing, and weak end-of-day summaries.

This group analysed those failures, designed a UML class model, and implemented a **Java console application** (no GUI, no network, no web). Data is stored in simple pipe-separated text files. The design was driven by the assignment’s OOP requirements, not by a single “god class”:

| Manual problem | Object-oriented solution in ActiveHub |
|----------------|----------------------------------------|
| Double-booked courts | `Booking.conflictsWith(...)` compares `LocalDate` and `LocalTime`, not strings |
| Unclear what is rented | Catalogue of eight items with typed subclasses |
| Wrong equipment / quantity | `Transaction` composes `TransactionItem` lines with quantity |
| Slow billing and discounts | `PromotionEngine` evaluates A, B and C; 10% service charge is calculated in `Transaction` |
| Weak daily summary | `DailyReport` aggregates paid transactions and payment methods |

The implementation language is **Java**. The program compiles and runs from `Main`.

---

## PART A – UML Class Diagram

### A.1 Diagrams (insert the PNG files here in the Word/PDF)

**Figure A.0 – Relationship overview** (clearest view of association, aggregation, composition, inheritance)

File: `docs/ActiveHub_UML_Overview.png`

**Figure A.1 – Complete class diagram** (attributes, methods, visibility, multiplicity)

File: `docs/ActiveHub_UML_Class_Diagram.png`  
Source: `docs/ActiveHub_UML_Class_Diagram.puml`

**Figure A.2 – Inheritance and polymorphism (zoomed)**

File: `docs/ActiveHub_UML_Polymorphism.png`  
Source: `docs/ActiveHub_UML_Polymorphism.puml`

*Insert all three figures. Use landscape pages. Figure A.0 is for relationship marks; A.1 for attributes/methods; A.2 for polymorphism.*

**UML notation used**

| Symbol | Meaning in this project |
|--------|-------------------------|
| `+` / `-` / `#` | public / private / protected |
| Solid line | Association (independent objects linked) |
| Filled diamond ◆ | Composition (`Transaction` owns `TransactionItem`) |
| Hollow diamond ◇ | Aggregation (`ActiveHubSystem` holds bookings that also live in files) |
| Solid triangle <|— | Inheritance (`extends`) |
| `{abstract}` | Cannot be instantiated; subclasses supply the missing methods |

### A.2 Classes identified from the scenario

| Class | Why it exists in the scenario | Java file |
|-------|------------------------------|-----------|
| `Customer` | Staff record name and contact for bookings and walk-ins | `Customer.java` |
| `Facility` | Courts / rooms that can be reserved | `Facility.java` |
| `Booking` | Date, time, participants, preferred court | `Booking.java` |
| `RentalItem` (abstract) | Shared catalogue data (code, name, price) | `RentalItem.java` |
| `EquipmentItem` | At least four equipment items | `EquipmentItem.java` |
| `FacilityPackage` | At least two court packages | `FacilityPackage.java` |
| `AccessoryItem` | At least two accessories / add-ons | `AccessoryItem.java` |
| `Transaction` | Rental bill for a booking or walk-in | `Transaction.java` |
| `TransactionItem` | One catalogue line with quantity | `TransactionItem.java` |
| `Promotion` (abstract) | Common promotion contract | `Promotion.java` |
| `OffPeakSaver` | Promotion A | `OffPeakSaver.java` |
| `TeamBookingReward` | Promotion B | `TeamBookingReward.java` |
| `EquipmentBundleDiscount` | Promotion C | `EquipmentBundleDiscount.java` |
| `PromotionEngine` | Evaluate all three; apply only the best | `PromotionEngine.java` |
| `Payment` (abstract) | Common payment contract | `Payment.java` |
| `CashPayment` / `CardPayment` / `EWalletPayment` | Three payment methods | matching `.java` files |
| `FileManager` | Text-file storage required by the brief | `FileManager.java` |
| `DailyReport` | Manager summary | `DailyReport.java` |
| `ActiveHubSystem` | Console menus and module flow | `ActiveHubSystem.java` |
| `Main` | Program entry | `Main.java` |
| `ConsoleUI` | Boxed menus/tables (user-friendliness) | `ConsoleUI.java` |

### A.3 Relationships, multiplicity and design reason

| Relationship | UML | Multiplicity | Why this, not another type |
|--------------|-----|--------------|----------------------------|
| Association | Customer — Booking | 1 to 0..* | A customer can place many bookings; a customer object is not “destroyed” if one booking is cancelled |
| Association | Facility — Booking | 1 to 0..* | One court appears in many bookings over the week |
| Association | Customer — Transaction | 1 to 0..* | Walk-in or booked customer can have several bills |
| Association | Booking — Transaction | 0..1 to 0..* | A bill may link to a booking, or be a walk-in (`NONE`) |
| Association | RentalItem — TransactionItem | 1 to 0..* | The catalogue item is shared; many bills can rent the same racquet code |
| Association | Transaction — Promotion | 0..1 to 0..1 | At most one promotion is selected |
| Association | Transaction — Payment | 0..1 to 0..1 | At most one completed payment method |
| **Composition** | Transaction ◆— TransactionItem | 1 to 1..* | Line items are created inside `addItem` and have no meaning outside that bill |
| **Aggregation** | ActiveHubSystem ◇— Booking / RentalItem / Transaction | 1 to * | The running system “has” these collections, but records persist in `data/*.txt` |
| **Aggregation** | PromotionEngine ◇— Promotion | 1 to 3 | The engine holds rule objects; the rules are reusable policy objects, not parts of one bill |
| **Inheritance** | RentalItem ← Equipment / FacilityPackage / Accessory | — | Category behaviour is specialised (Promotion C must count only equipment units) |
| **Inheritance** | Promotion ← A / B / C | — | Same operations, different formulae |
| **Inheritance** | Payment ← Cash / Card / E-Wallet | — | Same `processPayment`, different runtime message |

### A.4 Mapping UML → Java (evidence for “excellent mapping”)

| UML element | Exact Java mapping |
|-------------|-------------------|
| `Booking.date: LocalDate` | `private LocalDate date;` |
| `Booking.conflictsWith(...)` | `Booking.conflictsWith(Facility, LocalDate, LocalTime)` |
| `Transaction *-- TransactionItem` | `items.add(new TransactionItem(item, quantity));` |
| `PromotionEngine o-- Promotion` | `private final List<Promotion> promotions;` |
| `OffPeakSaver extends Promotion` | `public class OffPeakSaver extends Promotion` |
| `Payment #amountPaid` | `protected double amountPaid;` in abstract `Payment` |
| `RentalItem {abstract} getCategory()` | `public abstract String getCategory();` |

There is a `.java` file for every class in Figure A.1. Method names in the diagram match the compiled source.

---

## PART B – Java Programming

### B.1 Constraints met

- Console program only (no GUI / network / web / mobile)  
- Programming language: Java  
- Data: text files, pipe-separated lines  
- Group-designed catalogue (8 items: 4 equipment, 2 facility packages, 2 accessories)  
- Group-designed menus (main menu plus booking / transaction / payment submenus)  
- `java.time.LocalDate`, `LocalTime`, `DayOfWeek` used for scheduling and Promotion A  
- System compiles (`javac`) and runs (`Main`)

### B.2 Main menu (as implemented)

```
ACTIVEHUB SYSTEM
[1] Facility Booking
[2] View Rental Catalogue
[3] Create Rental Transaction
[4] Apply Promotion
[5] Make Payment
[6] Daily Report
[7] Exit
```

Facility Booking submenu: Create / View All / Cancel / Back.  
Rental Transaction submenu: Create / Update (add items) / View All.

### B.3 Catalogue (group-designed)

| Code | Item | Category | Price (RM) | Subclass |
|------|------|----------|------------|----------|
| E01 | Badminton Racquet | Equipment | 8.00 | `EquipmentItem` |
| E02 | Shuttlecock Set | Equipment | 5.00 | `EquipmentItem` |
| E03 | Basketball | Equipment | 6.00 | `EquipmentItem` |
| E04 | Futsal Ball | Equipment | 6.00 | `EquipmentItem` |
| C01 | Badminton Court Package (1hr) | Facility | 40.00 | `FacilityPackage` |
| C02 | Futsal Court Package (1hr) | Facility | 80.00 | `FacilityPackage` |
| A01 | Towel Set | Accessory | 3.00 | `AccessoryItem` |
| A02 | Locker Service | Accessory | 4.00 | `AccessoryItem` |

Accessories are **not** Off-Peak discounted and **not** counted as equipment units for Promotion C.

### B.4 Module behaviour

**1. Facility Booking.** Staff enter name, contact, booking date (`LocalDate.parse`), booking time (`LocalTime.parse`), participants, and facility ID. The system lists courts, then rejects a slot if an **ACTIVE** booking already uses the same facility, date and start time. Cancelled bookings do not block the slot. View and cancel are supported; cancel sets `status` to `CANCELLED` through `Booking.cancel()` only.

**2. Rental Catalogue.** Menu 2 displays all eight items with category colouring.

**3. Rental Transaction.** Staff may link a booking ID or type `NONE` for walk-in. Multiple items and quantities are added until `DONE`. Updating a pending transaction adds further lines and clears an old promotion so it can be recalculated. Subtotal = Σ (price × quantity). Final payable = (subtotal − discount) + **10% service charge**.

**4. Promotion.** All three rules are evaluated. The bill lists eligibility and saving for A, B and C, then applies **only one** promotion: maximum saving; if equal, lower alphabetical code. Promotions cannot be combined.

**5. Payment.** Cash, credit/debit card (last four digits), or e-wallet (brand name). `processPayment` is called on the abstract `Payment` type. Paid transactions are marked completed and saved.

**6. Daily Report.** Active bookings, completed transactions, daily revenue, most rented item (by quantity), payment-method counts.

### B.5 Worked billing example (must match the running program)

**Given:** Tuesday 2026-11-03 at 10:00, 10 participants, facility package C01, E01×2, E02×1.

| Line | Amount (RM) |
|------|-------------|
| C01 × 1 | 40.00 |
| E01 × 2 | 16.00 |
| E02 × 1 | 5.00 |
| **Subtotal** | **61.00** |

| Promotion | Eligible? | Saving |
|-----------|-----------|--------|
| A Off-Peak Saver | Yes (Mon–Fri, 10:00 is ≥ 09:00 and &lt; 16:00) | 15% × 40.00 = **6.00** |
| B Team Booking Reward | Yes (10 ≥ 8 pax) | min(10×5, 60, facility 40) = **40.00** |
| C Equipment Bundle | Yes (facility present, equipment qty 3) | **20.00** |

Selected: **B** (greatest saving).  
After discount: 61.00 − 40.00 = 21.00  
Service charge 10%: 2.10  
**Final payable: RM23.10**

Boundary checks implemented for A: **15:59 is eligible, 16:00 is not**; Saturday/Sunday are not eligible even at 10:00.

### B.6 Test plan and results

| ID | Test | Input | Expected | Result |
|----|------|-------|----------|--------|
| T1 | Duplicate booking | Same court, date, time as an ACTIVE booking | Error; booking not created | Pass |
| T2 | Cancel then rebook | Cancel then same slot | Allowed | Pass |
| T3 | Invalid date/time | `32-13-2026` or `25:00` | Reprompt; no crash | Pass |
| T4 | Empty input | Blank name | “Input cannot be empty” | Pass |
| T5 | Catalogue count | Menu 2 | 8 items, 4+2+2 categories | Pass |
| T6 | Promotion A window | Tue 15:59 vs 16:00 | Eligible / not eligible | Pass |
| T7 | Promotion B cap | 20 pax, facility RM40 | Saving = RM40 (cannot exceed facility; also cap RM60) | Pass |
| T8 | Promotion C units | Court + 2 racquets | C not eligible (need 3 equipment units) | Pass |
| T9 | Best-saving rule | Worked example above | B applied, RM23.10 | Pass |
| T10 | Tie-break | Two promotions with equal saving | Lower code (A before B before C) | Pass (engine logic) |
| T11 | Payment polymorphism | Cash / Card / E-Wallet | Matching method stored; status PAID | Pass |
| T12 | Persistence | Exit and restart | Bookings and paid bills reload | Pass |
| T13 | Daily report | After one paid bill | Completions, revenue, top item, Cash count | Pass |
| T14 | Update transaction | Add extra item on pending bill | Lines increase; promotion cleared | Pass |

### B.7 Screenshots (insert IntelliJ console captures here)

Capture these seven screens in IntelliJ (Run `Main` with working directory = project root):

1. Welcome splash + main menu (boxed)  
2. Rental catalogue (8 items)  
3. Create booking (success box)  
4. View all bookings (aligned table)  
5. Transaction bill **before** promotion  
6. Bill **after** promotion (A/B/C listed, selected B, service charge, final)  
7. Daily summary report (section lines and right-aligned figures)

Paste the images under this heading in the Word/PDF. Do not crop away the menu boxes.

### B.8 How to compile and run

**IntelliJ IDEA:** Open the `OOM` project → JDK 21+ → run `src/Main.java`. Working directory must be the project root so `data/` is found.

**Terminal:**

```bash
javac -d out/classes src/Main.java src/activehub/*.java
java -cp out/classes Main
```

---

## PART C – Object-Oriented Concepts Discussion

The brief requires more than textbook definitions. Each subsection names the classes, explains the design decision, and quotes this project’s UML/Java.

### C.1 Encapsulation

**Classes:** `Customer`, `Booking`, `Transaction`, `RentalItem`, `Facility`.

**Design decision:** Booking status and bill totals are business rules, not public fields. If `status` were public, any menu method could set `"ACTIVE"` again after a cancel. If `discountAmount` were public, a caller could skip the 10% service charge.

**How used:** Fields are `private`. Mutation goes through methods: `cancel()`, `addItem(...)`, `setSelectedPromotion(...)`.

**Example (Java):** `Booking.cancel()` is the only path that sets cancelled status. `Transaction.calculateServiceCharge()` always uses `(subtotal − discount) × 0.10` on a non-negative base.

**Example (UML):** attributes are marked `-` (private); public operations are `+`.

### C.2 Inheritance

**Classes:**  
`RentalItem` ← `EquipmentItem`, `FacilityPackage`, `AccessoryItem`  
`Promotion` ← `OffPeakSaver`, `TeamBookingReward`, `EquipmentBundleDiscount`  
`Payment` ← `CashPayment`, `CardPayment`, `EWalletPayment`

**Design decision:** The brief requires four equipment items, two facility packages and two accessories, and three promotions that **must not** use the same formula. Inheritance lets subclasses share code/name/price (or code/name for promotions) while overriding only the difference. A single `RentalItem` with a string category would still work, but Promotion C would depend on spelling `"Equipment"` correctly everywhere. Overriding `isEquipment()` makes the rule type-safe.

**Example:** `EquipmentItem.isEquipment()` returns `true`; `AccessoryItem` leaves the base `false`. Promotion C therefore counts racquets and shuttlecocks, not towels.

UML: solid generalisation arrows (Figure A.2).

### C.3 Polymorphism

**Classes:** `PromotionEngine` with `List<Promotion>`; `Transaction` with `Payment`; catalogue checks via `RentalItem`.

**Design decision:** The brief says the engine must compare all eligible savings and pick one. An `if (promo instanceof OffPeakSaver)` chain would work for three rules but would break Open/Closed design: adding Promotion D would require editing the engine. Polymorphism keeps the engine stable.

**Example:**

```java
for (Promotion promo : promotions) {
    boolean eligible = promo.isEligible(transaction);
    double saving = eligible ? promo.calculateSaving(transaction) : 0.0;
    // update best saving; tie-break on promo.getCode()
}
```

The loop never names A, B or C. At runtime, `OffPeakSaver` uses `LocalDate`/`LocalTime`/`DayOfWeek`; `TeamBookingReward` uses participant count; `EquipmentBundleDiscount` uses facility + equipment quantity.

Payment: `payment.processPayment(amount)` after the user picks 1, 2 or 3. The transaction stores the abstract `Payment` reference.

### C.4 Abstraction

**Classes:** abstract `Promotion`, `Payment`, `RentalItem`; also `PromotionEngine` as a policy object.

**Design decision:** Abstraction is used so staff menus stay simple (“Apply Promotion”) while each rule hides its formula. Abstract methods force every new promotion to implement both eligibility and saving — a class cannot be compiled if either is missing.

**Example:** `public abstract double calculateSaving(Transaction transaction);`  
`new Promotion()` is illegal. Staff never type the Off-Peak inequalities; `OffPeakSaver` encapsulates `!time.isBefore(09:00) && time.isBefore(16:00)`.

### C.5 Association

**Classes:** `Customer`–`Booking`, `Facility`–`Booking`, `Customer`–`Transaction`, `Booking`–`Transaction`, `RentalItem`–`TransactionItem`.

**Design decision:** Association is used when both objects have independent identity. A facility exists in `facilities.txt` whether or not it is booked today. A catalogue racquet exists even if no bill currently rents it.

**Example:** `Booking` holds `private Customer customer` and `private Facility facility`. Multiplicity: one customer to many bookings; one facility to many bookings (Figure A.1).

### C.6 Composition

**Classes:** `Transaction` and `TransactionItem`.

**Design decision:** A bill line (`E01:2`) is not a catalogue item and is not a customer. It is a **part** of one transaction. Composition (filled diamond) was chosen instead of association because `TransactionItem` is constructed inside `addItem` and is not loaded as a standalone file. If the pending transaction is abandoned, those line objects are discarded with it.

**Example (Java):** `items.add(new TransactionItem(item, quantity));`  
**Example (UML):** `Transaction "1" *-- "1..*" TransactionItem`.

### C.7 Aggregation

**Classes:** `ActiveHubSystem` with `Facility`, `RentalItem`, `Booking`, `Transaction`; `PromotionEngine` with `Promotion`.

**Design decision:** Aggregation (hollow diamond) was chosen because the same booking list is also owned conceptually by the sports centre’s files. `FileManager.loadBookings` recreates `Booking` objects when the program starts; they are not inner parts of the menu controller. Promotion rule objects similarly outlive any one transaction.

**Example:** UML `ActiveHubSystem o-- "*" Booking`. After Exit, `bookings.txt` still contains the records.

### C.8 Why these seven concepts together (justification)

| Brief requirement | Concept that carries it | What would go wrong without it |
|-------------------|-------------------------|--------------------------------|
| Hide bill internals | Encapsulation | Totals edited inconsistently; service charge skipped |
| 4+2+2 catalogue types | Inheritance | Category rules duplicated as strings |
| Three promotions, one applied | Polymorphism + abstraction | Long if-else; hard to add a fourth rule |
| Multi-item bills | Composition | Quantity lines mixed with catalogue master data |
| Bookings persist | Aggregation + association | Records die with the menu screen |
| Real calendar rules | Encapsulation of `java.time` in `Booking` | `"9:00"` vs `"09:00"` string bugs |

---

## PART D – Personal Reflection

*(Each member must put their real name and student ID on their section, then sign the contribution form. The text below is the group’s agreed draft of what each role learned; personalise the wording before submission.)*

### Member 1 – *(Name / Student ID)* — UML and Booking

**Challenges and how they were overcome:** The hardest part was drawing composition versus aggregation without guessing. Early drafts used the same diamond for “system has bookings” and “transaction has line items”. We overcame this by matching each diamond to a Java field: `new TransactionItem(...)` inside `Transaction` is composition; `fileManager.loadBookings(...)` is aggregation because the objects come back from disk.

**What was learned:** `LocalDate` and `LocalTime` comparisons (`equals`, `isBefore`) are safer than storing `"2026-11-03"` as a `String`. Clash checking became a single method, `conflictsWith`, instead of three separate string compares.

**Contributions:** Class identification for Part A; Figure A.1 / A.2 structure; `Booking`, `Facility`, `Customer`; create / view / cancel booking flow; `java.time` validation in the console.

**Most useful OOP concept and why:** **Encapsulation.** Forcing cancel through `cancel()` stopped accidental edits to status and made the clash rule depend on `isActive()` only.

### Member 2 – *(Name / Student ID)* — Catalogue and Transaction

**Challenges and how they were overcome:** Updating a transaction after the first bill was awkward: if we kept the old promotion, the discount no longer matched the new lines. We overcame this by calling `clearPromotion()` after `addItem`, then asking staff to run menu 4 again.

**What was learned:** Composition is not just a UML symbol. Creating `TransactionItem` only inside `Transaction.addItem` made the bill’s subtotal a loop over owned parts, which matches the brief’s “quantity for each selected item”.

**Contributions:** `RentalItem` hierarchy and eight catalogue items; `Transaction` / `TransactionItem`; 10% service charge; create / update / view transactions; linking a booking ID or walk-in `NONE`.

**Most useful OOP concept and why:** **Composition.** It kept catalogue master data (`E01` price) separate from “two racquets on this bill”, which is what the manager actually charges.

### Member 3 – *(Name / Student ID)* — Promotions, payments, Part C

**Challenges and how they were overcome:** Promotion B can look more generous than the facility charge (for example 20 pax × RM5 = RM100 on a RM40 court). The brief says the reward cannot reduce the facility charge below RM0. We overcame this with `Math.min(saving, getFacilityCharge())` in addition to the RM60 cap. Another issue was the 16:00 boundary; we tested 15:59 versus 16:00 with `LocalTime.isBefore`.

**What was learned:** Polymorphism is practical, not theoretical: one `List<Promotion>` replaced a fragile if-else. Tie-break by `getCode()` implemented “A before B before C” without extra flags.

**Contributions:** Abstract `Promotion` and three subclasses; `PromotionEngine` best-saving policy; abstract `Payment` and Cash/Card/E-Wallet; Part C write-up with class-level justification.

**Most useful OOP concept and why:** **Polymorphism.** The engine can list every eligible saving on the bill and still apply only one winner, which is exactly the mandatory selection rule.

### Member 4 – *(Name / Student ID)* — Persistence, report, testing, UI

**Challenges and how they were overcome:** After restart, transaction IDs reset to T001 and collided in memory. We overcame this by parsing the numeric part of existing IDs (`B005` → 5) and continuing from the maximum. Table alignment also broke in IntelliJ because bold text is not strictly monospace; we padded using visible (ANSI-stripped) width instead of `String.format` on coloured strings.

**What was learned:** Aggregation matches persistence: the controller holds lists, but `FileManager` is the long-term owner of the text files. Testing a console app needs a scripted path (booking → items → promotion → pay → report), not only compiling.

**Contributions:** `FileManager` load/save; `DailyReport`; `ConsoleUI` boxed menus and tables; test plan T1–T14; screenshots and report formatting.

**Most useful OOP concept and why:** **Aggregation.** It explained why bookings can be cleared from a file without deleting the `Facility` catalogue, which is how a real centre would reset a day’s diary.

---

## SDS Academic Integrity Statement

Sunway Diploma Studies is committed to honesty, trust, fairness, respect and responsibility.

We hereby declare that:

1. We fully understand and will uphold the academic integrity of Sunway Diploma Studies (SDS).
2. We confirm that the work hereby submitted is our own original work and where other people’s work has been used this has been fully acknowledged.
3. We are aware of the importance of conducting exams with integrity and fairness, and we hereby confirm that we will comply with the requirements.
4. We are aware that non-compliance with these instructions and unfair conduct constitute a disciplinary offense, and actions will be taken including but not limited to being expelled.

| No | Student Name | Signature | Date |
|----|--------------|-----------|------|
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |

---

## Appendix

### A. Source files (map 1:1 to UML)

```
src/Main.java
src/activehub/ActiveHubSystem.java
src/activehub/Booking.java
src/activehub/Customer.java
src/activehub/Facility.java
src/activehub/RentalItem.java
src/activehub/EquipmentItem.java
src/activehub/FacilityPackage.java
src/activehub/AccessoryItem.java
src/activehub/Transaction.java
src/activehub/TransactionItem.java
src/activehub/Promotion.java
src/activehub/OffPeakSaver.java
src/activehub/TeamBookingReward.java
src/activehub/EquipmentBundleDiscount.java
src/activehub/PromotionEngine.java
src/activehub/Payment.java
src/activehub/CashPayment.java
src/activehub/CardPayment.java
src/activehub/EWalletPayment.java
src/activehub/FileManager.java
src/activehub/DailyReport.java
src/activehub/ConsoleUI.java
```

### B. Data files

```
data/catalogue.txt
data/facilities.txt
data/bookings.txt
data/transactions.txt
```

Format example (booking): `B001|Name|Contact|F01|2026-11-03|10:00|10|ACTIVE`

### C. Submission checklist (Excellent band)

| Rubric area | Evidence in this submission |
|-------------|-----------------------------|
| UML 19–25 | Figures A.1 and A.2; visibility; multiplicity; inheritance; composition; aggregation; association; polymorphism classes |
| Coding 23–30 | All six modules; `java.time`; 8-item catalogue; best-of-three promotions; three payments; daily report; text files; boxed UI |
| OOP discussion 19–25 | Part C names classes, quotes Java/UML, justifies each decision |
| Documentation 8–10 | All parts A–D; test table; screenshot list; comments on key methods |
| Reflection 8–10 | Four role reflections; contribution form (must be signed) |

### D. Word/PDF formatting (required by the brief)

- Font: Times New Roman, size 12  
- Line spacing: 1.5  
- Insert UML PNGs and console screenshots  
- One group softcopy: Report + `.java` files + `data/` text files  

---

*End of Report*
