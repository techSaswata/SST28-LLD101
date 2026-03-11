# Ex2 — My Notes (SRP Fix: Campus Cafeteria Billing)

---

## What the problem asks to do

A college cafeteria generates bills for students. The flow is:
1. Look up the menu to find prices
2. Calculate subtotal from items ordered
3. Apply tax (based on customer type)
4. Apply discount (based on customer type and order size)
5. Format the invoice and print it
6. Save the invoice to a store

The problem says: **`CafeteriaSystem.checkout` does all of this in one big method**. Fix it.

---

## The concept being used: SRP (Single Responsibility Principle)

Imagine a restaurant kitchen:
- One cook handles the pricing
- One cashier calculates tax
- One manager applies discounts
- One printer prints the receipt
- One filing person stores the invoice

If the cashier is also filing invoices, changing tax rules, AND printing — that's chaos. If tax changes, you have to mess with the whole cashier.

**SRP says:** Each class should have only one reason to change.

---

## What was broken (Before the fix)

`CafeteriaSystem.checkout` was doing everything:
- Reading the menu to find item prices
- Calculating subtotals
- Applying tax rules (hard-coded percentages per customer type)
- Applying discount rules (hard-coded conditions)
- Building the invoice string with `StringBuilder`
- Printing it
- Saving it to `FileStore`

**The problem:** Tax rule changed? Edit this method. Discount changed? Edit this method. Invoice format changed? Edit this method. Too many reasons to change one class.

---

## Steps to identify and understand what to do

**Step 1 — Spot all the different jobs in `checkout`**

Read the method. You'll see: menu lookup, subtotal math, tax logic, discount logic, string formatting, print, save. That's 7 jobs.

We read through `CafeteriaSystem.checkout` and found menu price lookups, subtotal loops, hardcoded tax percentages per customer type, hardcoded discount conditions, a `StringBuilder` building the invoice string, a `System.out.println`, and a `FileStore.save()` call — all in one method.

**Step 2 — Move tax and discount behind interfaces**

- `TaxPolicy` interface → `DefaultTaxPolicy` (knows the tax % per customer type)
- `DiscountPolicy` interface → `DefaultDiscountPolicy` (knows the discount amount)

Now if tax rules change, you only change `DefaultTaxPolicy`.

We created `TaxPolicy` and `DiscountPolicy` interfaces. `DefaultTaxPolicy` stores tax rates per customer type. `DefaultDiscountPolicy` stores discount amounts based on customer type and order size. The concrete logic moved out of the checkout method into these classes.

**Step 3 — Move pricing to its own calculator**

`PricingCalculator` interface → `DefaultPricingCalculator` takes order lines + menu catalog, returns an `InvoicePricing` object (subtotal, lines with names and totals, count).

We created `PricingCalculator` interface and `DefaultPricingCalculator`. The calculator takes the order lines and the menu catalog, looks up each item's price, and returns an `InvoicePricing` object with all line totals and the subtotal — the checkout method no longer does any price math.

**Step 4 — Move formatting to `InvoiceFormatter`**

`InvoiceFormatter.format(InvoiceData)` just builds the invoice string. It doesn't know about taxes or discounts — it just gets all the numbers already calculated and formats them.

We created `InvoiceFormatter` with a `format(InvoiceData)` method. It uses a `StringBuilder` internally to build the receipt string. `CafeteriaSystem` passes a fully-populated `InvoiceData` object and gets back a formatted string to print.

**Step 5 — Move store behind an interface**

`InvoiceStore` interface → `FileStore` implements it. Now `CafeteriaSystem` doesn't know it's a file store.

We changed `CafeteriaSystem` to take an `InvoiceStore` in its constructor. The existing `FileStore` was made to implement this interface — no logic changed in `FileStore`, just added `implements InvoiceStore`.

**Step 6 — `CafeteriaSystem` becomes an orchestrator**

It just calls: calculate pricing → get tax → get discount → create InvoiceData → format → print → save. Clean.

We rewrote `checkout` to call each collaborator in sequence, collecting results into an `InvoiceData` object, then passing it to the formatter and then to the store. The method shrank to under 15 lines.

---

## UML Diagram

```
              +----------------------+
              |   CafeteriaSystem    |
              |----------------------|
              | - menu: MenuCatalog  |
              | - pricingCalc        |---> PricingCalculator (interface)
              | - taxPolicy          |---> TaxPolicy (interface)
              | - discountPolicy     |---> DiscountPolicy (interface)
              | - formatter          |---> InvoiceFormatter
              | - store              |---> InvoiceStore (interface)
              |----------------------|
              | + checkout(...)      |  ← only orchestrates
              +----------------------+

PricingCalculator (interface)             TaxPolicy (interface)
        ^                                        ^
        |                                        |
DefaultPricingCalculator               DefaultTaxPolicy

DiscountPolicy (interface)             InvoiceStore (interface)
        ^                                        ^
        |                                        |
DefaultDiscountPolicy                       FileStore

                  +----------------+
                  |  InvoiceData   |  ← a bag of all invoice numbers
                  |----------------|
                  | invoiceId      |
                  | lines          |
                  | subtotal       |
                  | taxPercent     |
                  | taxAmount      |
                  | discount       |
                  | total          |
                  +----------------+
                         |
                  fed to v
                  +----------------+
                  | InvoiceFormatter|
                  |----------------|
                  | format(data)   |  ← only job: build the string
                  +----------------+
```

---

## The story in one paragraph

`CafeteriaSystem.checkout` was doing everything from menu lookup to tax to discount to printing to saving. We broke it into specialists. `DefaultPricingCalculator` figures out line totals and subtotals. `DefaultTaxPolicy` knows the tax %. `DefaultDiscountPolicy` knows the discount. `InvoiceFormatter` formats the final string. `FileStore` saves it. `CafeteriaSystem` just calls them in order. Now if someone changes the discount rule — only `DefaultDiscountPolicy` changes. Nothing else is touched. That's SRP.
