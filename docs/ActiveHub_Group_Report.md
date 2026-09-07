# DIT2123 Object Oriented Modelling
## Coursework 1 – Group Assignment Report
### ActiveHub Sports Facility Booking, Equipment Rental & Promotion System

**Programme:** Diploma in Information Technology  
**Subject Code:** DIT2123  
**Subject Name:** Object Oriented Modelling  
**Study Period:** AUG 2026 – DEC 2026  
**Deadline:** 06-11-2026 (Week 10)

| No | Student ID | Student Name | Signature | Date |
|----|------------|--------------|-----------|------|
| 1 | *(fill in)* | *(fill in)* | | |
| 2 | *(fill in)* | *(fill in)* | | |
| 3 | *(fill in)* | *(fill in)* | | |
| 4 | *(fill in)* | *(fill in)* | | |

---

## Group Member Contribution Form

| Student Name | Signature | Contribution |
|--------------|-----------|--------------|
| *(Member 1)* | | UML design, Booking module, documentation |
| *(Member 2)* | | Transaction & catalogue modules, coding |
| *(Member 3)* | | Promotion engine, payment polymorphism |
| *(Member 4)* | | File I/O, daily report, testing, report writing |

*(Adjust roles to match actual work. All members must sign.)*

---

## 1. Introduction

ActiveHub Sports Centre currently manages court bookings, equipment rentals, and payments manually. This causes double-bookings, unclear rental slips, slow billing, and weak end-of-day reporting.

This project analyses the scenario, designs a UML class diagram, and implements a **Java console application** that supports:

1. Facility booking (with conflict checking using `java.time`)
2. Rental catalogue (8 items)
3. Rental transactions (create / update, multi-item)
4. Rule-based promotions (A, B, C — best single discount)
5. Payment (cash, card, e-wallet)
6. Daily summary report
7. Text-file data storage

---

## PART A – UML Class Diagram

### A.1 Diagram

The complete UML Class Diagram is provided in:

- Source: `docs/ActiveHub_UML_Class_Diagram.puml`
- **Paste the exported PNG/PDF diagram here in the final Word/PDF report.**

How to export:

1. Open https://www.plantuml.com/plantuml/uml  
2. Paste the contents of `ActiveHub_UML_Class_Diagram.puml`  
3. Download the image and insert it in this section  
   **or** redraw the same classes in draw.io / Lucidchart using the structure below.

### A.2 Classes identified from the scenario

| Class / Interface | Responsibility |
|-------------------|----------------|
| `Customer` | Stores customer name and contact number |
| `Facility` | Sports court / room that can be booked |
| `Booking` | Facility booking with date, time, participants (`LocalDate`/`LocalTime`) |
| `RentalItem` | Catalogue item (Equipment / Facility / Accessory) |
| `TransactionItem` | Quantity of one catalogue item inside a transaction |
| `Transaction` | Rental bill: items, discount, service charge, payment |
| `Promotion` | Interface for discount rules (polymorphism) |
| `OffPeakSaver` | Promotion A |
| `TeamBookingReward` | Promotion B |
| `EquipmentBundleDiscount` | Promotion C |
| `PromotionEngine` | Evaluates all promotions and picks the best one |
| `Payment` | Interface for payment processing (polymorphism) |
| `CashPayment` / `CardPayment` / `EWalletPayment` | Concrete payment methods |
| `FileManager` | Load/save text files |
| `DailyReport` | End-of-day manager summary |
| `ActiveHubSystem` | Console menus and module orchestration |
| `Main` | Program entry point |

### A.3 Relationships used

| Relationship | Example in this system | Multiplicity |
|--------------|------------------------|--------------|
| **Association** | `Customer` places `Booking`; `Booking` reserves `Facility` | Customer 1 — * Booking; Facility 1 — * Booking |
| **Composition** | `Transaction` *contains* `TransactionItem` (items do not exist without the transaction) | Transaction 1 ◆— 1..* TransactionItem |
| **Aggregation** | `ActiveHubSystem` holds catalogues, bookings, transactions; `PromotionEngine` holds `Promotion` list | System ◇— * Booking / RentalItem / Transaction |
| **Inheritance / Realization** | `OffPeakSaver`, `TeamBookingReward`, `EquipmentBundleDiscount` implement `Promotion`; payment classes implement `Payment` | Interface ◄‥ concrete classes |
| **Polymorphism classes** | `Promotion` hierarchy and `Payment` hierarchy | Runtime method dispatch via interfaces |

### A.4 Mapping UML → Java

Every class in the UML diagram has a matching `.java` file under `src/activehub/` (plus `src/Main.java`). Attributes and methods in the diagram match the implemented fields and methods.

---

## PART B – Java Programming

### B.1 System overview

Programming language: **Java** (console only).  
Data storage: **text files** in `data/` (`catalogue.txt`, `facilities.txt`, `bookings.txt`, `transactions.txt`).

### B.2 Module behaviour

#### Facility Booking Module
- Staff enter customer name, contact, date (`LocalDate`), time (`LocalTime`), participants, and facility ID.
- System rejects duplicate bookings for the **same facility + same date + same time**.
- Staff can view all bookings and cancel a booking (status → `CANCELLED`).

#### Rental Catalogue Module
Eight items designed by the group:

| Code | Item | Category | Price (RM) |
|------|------|----------|------------|
| E01 | Badminton Racquet | Equipment | 8.00 |
| E02 | Shuttlecock Set | Equipment | 5.00 |
| E03 | Basketball | Equipment | 6.00 |
| E04 | Futsal Ball | Equipment | 6.00 |
| C01 | Badminton Court Package (1hr) | Facility | 40.00 |
| C02 | Futsal Court Package (1hr) | Facility | 80.00 |
| A01 | Towel Set | Accessory | 3.00 |
| A02 | Locker Service | Accessory | 4.00 |

#### Rental Transaction Module
- Create a transaction for a walk-in customer or link an existing booking.
- Add multiple catalogue items with quantities.
- Update an existing pending transaction by adding more items.
- Subtotal = Σ (price × quantity).
- Final bill includes **10% service charge** after discount.

#### Promotion Module
Whenever promotion is applied, the system evaluates:

- **A Off-Peak Saver:** Mon–Fri, start time ≥ 09:00 and &lt; 16:00 → 15% of facility charges only  
- **B Team Booking Reward:** ≥ 8 participants → RM5 × pax, max RM60, cannot exceed facility charge  
- **C Equipment Bundle Discount:** ≥ 1 facility item AND equipment quantity ≥ 3 → fixed RM20  

**Selection rule:** compute saving for every eligible promotion; apply **only the one with the greatest saving**. If tied, choose the lower alphabetical code (A before B before C). Promotions cannot be combined.

#### Payment Module
Customer chooses Cash, Credit/Debit Card, or E-Wallet. Processing uses polymorphism via the `Payment` interface.

#### Reporting Module
Displays:
- total active facility bookings  
- total completed rental transactions  
- total daily revenue  
- most frequently rented item  
- payment method summary  

### B.3 Sample calculation (for report screenshots)

Booking: Tuesday 2026-11-03 at 10:00, 10 participants, Court F01.  
Items: C01×1 (RM40) + E01×2 (RM16) + E02×1 (RM5) → **Subtotal RM61**.

| Promotion | Eligible? | Saving |
|-----------|-----------|--------|
| A Off-Peak Saver | Yes (weekday 10:00) | 15% × 40 = **RM6.00** |
| B Team Booking Reward | Yes (10 pax) | min(50, 60, 40) = **RM40.00** |
| C Equipment Bundle | Yes (facility + 3 equipment) | **RM20.00** |

Selected: **B (RM40.00)**  
Amount after discount: 61 − 40 = 21  
Service charge 10%: 2.10  
**Final payable: RM23.10**

*(Insert console screenshots for menus 1–6 here.)*

### B.4 How to compile and run

See `README.md`. In IntelliJ, run `Main`. Working directory should be the project root so that the `data/` folder is used.

---

## PART C – Object-Oriented Concepts Discussion

### C.1 Encapsulation

**Classes involved:** `Customer`, `Booking`, `Transaction`, `RentalItem`, and others.

**How used:** Attributes are declared `private`. Outside classes access data only through getters/setters or controlled methods such as `Booking.cancel()` and `Transaction.addItem(...)`. This protects business rules (for example, only `cancel()` can change status to cancelled).

**Example:** In `Transaction`, `discountAmount` and `finalAmount` are private. Callers must use `setSelectedPromotion(...)` and `calculateFinalAmount()` instead of changing totals directly, so the 10% service charge logic stays consistent.

### C.2 Inheritance / Interface realization

**Classes involved:**  
- `Promotion` ← `OffPeakSaver`, `TeamBookingReward`, `EquipmentBundleDiscount`  
- `Payment` ← `CashPayment`, `CardPayment`, `EWalletPayment`

**How used:** Common behaviour is declared in interfaces. Concrete classes provide their own implementation of eligibility/saving or payment processing.

**Example:** All promotions implement `isEligible(Transaction)` and `calculateSaving(Transaction)` but each uses different rules (time window vs participant count vs equipment quantity).

### C.3 Polymorphism

**Classes involved:** `PromotionEngine` with `List<Promotion>`; `Transaction` with `Payment`.

**How used:** The engine stores promotions as the interface type and calls the same methods. At runtime, each concrete class responds differently. Payment works the same way when `processPayment(amount)` is called.

**Example:**

```java
for (Promotion promo : promotions) {
    if (promo.isEligible(transaction)) {
        double saving = promo.calculateSaving(transaction);
        // ...
    }
}
```

The loop does not contain `if (promo instanceof OffPeakSaver)` checks; behaviour is selected automatically.

### C.4 Abstraction

**Classes involved:** `Promotion`, `Payment`, and high-level controllers such as `ActiveHubSystem` / `PromotionEngine`.

**How used:** Interfaces hide internal calculation details. Staff using the menu only choose “Apply Promotion”; they do not need to know Off-Peak formulae. `PromotionEngine` abstracts the “pick best promotion” policy (max saving, then alphabetical tie-break).

**Example:** `Payment` exposes only `processPayment` and `getPaymentMethod`. Card last-four digits and e-wallet brand names stay inside concrete classes.

### C.5 Association

**Classes involved:** `Customer`–`Booking`, `Facility`–`Booking`, `Customer`–`Transaction`, `Booking`–`Transaction`, `RentalItem`–`TransactionItem`.

**How used:** Association models a lasting link between independent objects. A customer can have many bookings; a facility can appear in many bookings.

**Example:** `Booking` stores references to one `Customer` and one `Facility`. Deleting a booking conceptually does not destroy the customer record model (customer data is still a separate class).

### C.6 Composition

**Classes involved:** `Transaction` and `TransactionItem`.

**How used:** `TransactionItem` objects are created inside `Transaction.addItem(...)` and live in the transaction’s private list. They represent parts of that bill. If the transaction is discarded before payment, its line items have no separate business meaning outside it.

**Example:** UML shows `Transaction` ◆—— `TransactionItem` (filled diamond). In Java, `items` is an `ArrayList<TransactionItem>` owned by `Transaction`.

### C.7 Aggregation

**Classes involved:** `ActiveHubSystem` with `Facility`, `RentalItem`, `Booking`, `Transaction`; `PromotionEngine` with `Promotion`.

**How used:** The system “has” many catalogue items and bookings, but those objects can be loaded/saved independently via `FileManager` and conceptually exist as part of the centre’s data, not only inside one short-lived screen action.

**Example:** UML shows `ActiveHubSystem` ◇—— `Booking` (hollow diamond). Bookings persist in `bookings.txt` even after the menu method returns.

### C.8 Design justification summary

| Requirement | OOP design choice |
|-------------|-------------------|
| Three different promotions, one selected | `Promotion` interface + `PromotionEngine` |
| Three payment methods | `Payment` interface + concrete classes |
| Multi-item bills | Composition of `TransactionItem` |
| Avoid double booking using real dates/times | `Booking` with `LocalDate` / `LocalTime` / `DayOfWeek` |
| Keep menu code manageable | `ActiveHubSystem` orchestrates modules; domain classes hold logic |

---

## PART D – Personal Reflection

*(Each member must write their own short reflection. Replace the placeholders below.)*

### Member 1 – *(Name / Student ID)*

**Challenges faced and how overcome:**  
*(Example: Understanding multiplicity and composition vs aggregation was confusing at first. We overcame this by redrawing the UML together and matching each relationship to a Java field.)*

**What was learned:**  
*(Example: Learned how `java.time` is better than strings for comparing booking schedules and off-peak rules.)*

**Contributions:**  
*(List your tasks, e.g. UML diagram, booking module, Part C write-up.)*

**Most useful OOP concept and why:**  
*(Example: Polymorphism — one promotion loop handles A/B/C without messy if-else chains.)*

---

### Member 2 – *(Name / Student ID)*

**Challenges faced and how overcome:**  
*(fill in)*

**What was learned:**  
*(fill in)*

**Contributions:**  
*(fill in)*

**Most useful OOP concept and why:**  
*(fill in)*

---

### Member 3 – *(Name / Student ID)*

**Challenges faced and how overcome:**  
*(fill in)*

**What was learned:**  
*(fill in)*

**Contributions:**  
*(fill in)*

**Most useful OOP concept and why:**  
*(fill in)*

---

### Member 4 – *(Name / Student ID)* *(delete if group has only 3 members)*

**Challenges faced and how overcome:**  
*(fill in)*

**What was learned:**  
*(fill in)*

**Contributions:**  
*(fill in)*

**Most useful OOP concept and why:**  
*(fill in)*

---

## SDS Academic Integrity Statement

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

### A. Source file list

```
src/Main.java
src/activehub/ActiveHubSystem.java
src/activehub/Booking.java
src/activehub/Customer.java
src/activehub/Facility.java
src/activehub/RentalItem.java
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
```

### B. Data files

```
data/catalogue.txt
data/facilities.txt
data/bookings.txt
data/transactions.txt
```

### C. Report formatting reminder (for final submission)

- Font: Times New Roman, size 12  
- Line spacing: 1.5  
- Softcopy only: Report (PDF/Word) + `.java` files + text data files  
- Insert UML image and console screenshots before converting to PDF  

---

*End of Report*
