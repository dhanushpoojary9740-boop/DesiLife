package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class TaxManager {

    public interface TaxCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final TaxCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;

    private long salaryIncome;
    private long businessIncome;
    private long rentalIncome;
    private long investmentGains;
    private long otherIncome;

    private long deductions;
    private long totalTaxPaid;
    private long pendingTax;
    private long totalRefunds;
    private long taxPenalties;

    private boolean taxFiled;
    private boolean auditPending;

    private int taxYear;
    private int auditCount;

    private final ArrayList<String> history = new ArrayList<>();

    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 190, 185);

    public TaxManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            TaxCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        loadData();
    }

    // ---------------- LOAD DATA ----------------

    private void loadData() {

        salaryIncome = save.getLong("tax_salary_income", 0);
        businessIncome = save.getLong("tax_business_income", 0);
        rentalIncome = save.getLong("tax_rental_income", 0);
        investmentGains = save.getLong("tax_investment_gains", 0);
        otherIncome = save.getLong("tax_other_income", 0);

        deductions = save.getLong("tax_deductions", 0);
        totalTaxPaid = save.getLong("tax_total_paid", 0);
        pendingTax = save.getLong("tax_pending", 0);
        totalRefunds = save.getLong("tax_refunds", 0);
        taxPenalties = save.getLong("tax_penalties", 0);

        taxFiled = save.getBoolean("tax_filed", false);
        auditPending = save.getBoolean("tax_audit_pending", false);

        taxYear = save.getInt("tax_year", 1);
        auditCount = save.getInt("tax_audit_count", 0);

        int count = save.getInt("tax_history_count", 0);

        for (int i = 0; i < count; i++) {

            String item = save.getString("tax_history_" + i, null);

            if (item != null) {
                history.add(item);
            }
        }
    }

    // ---------------- SAVE DATA ----------------

    private void saveData() {

        SharedPreferences.Editor editor = save.edit();

        editor.putLong("tax_salary_income", salaryIncome);
        editor.putLong("tax_business_income", businessIncome);
        editor.putLong("tax_rental_income", rentalIncome);
        editor.putLong("tax_investment_gains", investmentGains);
        editor.putLong("tax_other_income", otherIncome);

        editor.putLong("tax_deductions", deductions);
        editor.putLong("tax_total_paid", totalTaxPaid);
        editor.putLong("tax_pending", pendingTax);
        editor.putLong("tax_refunds", totalRefunds);
        editor.putLong("tax_penalties", taxPenalties);

        editor.putBoolean("tax_filed", taxFiled);
        editor.putBoolean("tax_audit_pending", auditPending);

        editor.putInt("tax_year", taxYear);
        editor.putInt("tax_audit_count", auditCount);

        editor.putInt("tax_history_count", history.size());

        for (int i = 0; i < history.size(); i++) {
            editor.putString("tax_history_" + i, history.get(i));
        }

        editor.apply();
    }

    // ---------------- BASIC HELPERS ----------------

    private String money(long amount) {
        return currency.format(amount);
    }

    private long safeAdd(long a, long b) {

        if (b > 0 && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }

        if (b < 0 && a < Long.MIN_VALUE - b) {
            return Long.MIN_VALUE;
        }

        return a + b;
    }

    private long totalIncome() {

        long total = 0;

        total = safeAdd(total, salaryIncome);
        total = safeAdd(total, businessIncome);
        total = safeAdd(total, rentalIncome);
        total = safeAdd(total, investmentGains);
        total = safeAdd(total, otherIncome);

        return Math.max(0, total);
    }

    private long taxableIncome() {
        return Math.max(0, totalIncome() - deductions);
    }

    private void addHistory(String description) {

        history.add(0, description);

        while (history.size() > 50) {
            history.remove(history.size() - 1);
        }

        saveData();
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    // ---------------- INCOME RECORDING ----------------

    public void addSalaryIncome(long amount) {

        if (amount <= 0) return;

        salaryIncome = safeAdd(salaryIncome, amount);
        taxFiled = false;

        saveData();
    }

    public void addBusinessIncome(long amount) {

        if (amount <= 0) return;

        businessIncome = safeAdd(businessIncome, amount);
        taxFiled = false;

        saveData();
    }

    public void addRentalIncome(long amount) {

        if (amount <= 0) return;

        rentalIncome = safeAdd(rentalIncome, amount);
        taxFiled = false;

        saveData();
    }

    public void addInvestmentGains(long amount) {

        if (amount <= 0) return;

        investmentGains = safeAdd(investmentGains, amount);
        taxFiled = false;

        saveData();
    }

    public void addOtherIncome(long amount) {

        if (amount <= 0) return;

        otherIncome = safeAdd(otherIncome, amount);
        taxFiled = false;

        saveData();
    }

    // ---------------- TAX CALCULATION ----------------

    /*
     * Simplified fictional game tax slabs.
     * These are not official Indian tax rates.
     */

    private long calculateIncomeTax(long income) {

        long tax = 0;
        long remaining = Math.max(0, income);

        long[] limits = {
                400000,
                400000,
                400000,
                400000,
                400000
        };

        double[] rates = {
                0.05,
                0.10,
                0.15,
                0.20,
                0.25
        };

        long previousLimit = 0;

        for (int i = 0; i < limits.length; i++) {

            long taxablePart = Math.min(remaining, limits[i]);

            tax += Math.round(taxablePart * rates[i]);

            remaining -= taxablePart;

            if (remaining <= 0) break;

            previousLimit += limits[i];
        }

        if (remaining > 0) {
            tax += Math.round(remaining * 0.30);
        }

        return tax;
    }

    private long calculateTotalTax() {

        long baseTax = calculateIncomeTax(taxableIncome());

        long cess = Math.round(baseTax * 0.04);

        return safeAdd(baseTax, cess);
    }

    public long getEstimatedTax() {
        return calculateTotalTax();
    }

    public long getTotalIncome() {
        return totalIncome();
    }

    public long getTaxableIncome() {
        return taxableIncome();
    }

    // ---------------- DASHBOARD ----------------

    public void open() {
        showDashboard();
    }

    public void showDashboard() {

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 25, 24, 30);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);

        TextView title = new TextView(activity);
        title.setText("💰 TAXATION SYSTEM");
        title.setTextColor(gold);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);

        root.addView(title);

        addInfo(root, "Financial Year", "Year " + taxYear);
        addInfo(root, "Total Income", money(totalIncome()));
        addInfo(root, "Taxable Income", money(taxableIncome()));
        addInfo(root, "Estimated Income Tax", money(calculateTotalTax()));
        addInfo(root, "Deductions", money(deductions));
        addInfo(root, "Tax Already Paid", money(totalTaxPaid));
        addInfo(root, "Pending Tax", money(pendingTax));
        addInfo(root, "Total Refunds", money(totalRefunds));
        addInfo(root, "Tax Penalties", money(taxPenalties));

        addDivider(root);

        addButton(root, "📊 View Income Breakdown", this::showIncomeBreakdown);
        addButton(root, "➕ Record Additional Income", this::recordIncome);
        addButton(root, "📉 Claim Tax Deductions", this::showDeductions);
        addButton(root, "🧾 File Income Tax Return", this::fileTaxReturn);
        addButton(root, "💳 Pay Pending Tax", this::payPendingTax);
        addButton(root, "💵 Check Tax Refund", this::checkRefund);
        addButton(root, "🔍 Tax Audit & Notices", this::showAudit);
        addButton(root, "📜 Tax History", this::showHistory);
        addButton(root, "⬅ Back to Game", activity::recreate);

        activity.setContentView(scroll);
    }

    private void addInfo(
            LinearLayout root,
            String label,
            String value
    ) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 12, 16, 12);
        card.setBackground(background(Color.rgb(23, 39, 35), 14));

        TextView heading = new TextView(activity);
        heading.setText(label);
        heading.setTextColor(muted);
        heading.setTextSize(12);

        TextView data = new TextView(activity);
        data.setText(value);
        data.setTextColor(white);
        data.setTextSize(17);
        data.setTypeface(null, Typeface.BOLD);
        data.setPadding(0, 4, 0, 0);

        card.addView(heading);
        card.addView(data);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 4, 0, 4);

        root.addView(card, params);
    }

    private void addButton(
            LinearLayout root,
            String label,
            Runnable action
    ) {

        TextView button = new TextView(activity);

        button.setText(label);
        button.setTextColor(dark);
        button.setTextSize(16);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(14, 17, 14, 17);
        button.setBackground(background(gold, 14));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);

        root.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void addDivider(LinearLayout root) {

        TextView divider = new TextView(activity);
        divider.setBackgroundColor(Color.rgb(65, 75, 68));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 1);

        params.setMargins(0, 18, 0, 12);

        root.addView(divider, params);
    }

    private GradientDrawable background(int color, int radius) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        return drawable;
    }

    // ---------------- INCOME BREAKDOWN ----------------

    private void showIncomeBreakdown() {

        String details =
                "Salary Income: " + money(salaryIncome)
                        + "\nBusiness Income: " + money(businessIncome)
                        + "\nRental Income: " + money(rentalIncome)
                        + "\nInvestment Gains: " + money(investmentGains)
                        + "\nOther Income: " + money(otherIncome)
                        + "\n\nTotal Income: " + money(totalIncome())
                        + "\nDeductions: " + money(deductions)
                        + "\nTaxable Income: " + money(taxableIncome())
                        + "\nEstimated Tax: " + money(calculateTotalTax());

        showMessage("Income Breakdown", details);
    }

    // ---------------- RECORD INCOME ----------------

    private void recordIncome() {

        String[] types = {
                "Salary Income",
                "Business Income",
                "Rental Income",
                "Investment Gains",
                "Other Income"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Income Type")
                .setItems(types, (dialog, which) -> {
                    showIncomeAmount(types[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showIncomeAmount(String type) {

        EditText input = new EditText(activity);
        input.setHint("Enter income amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle(type)
                .setView(input)
                .setPositiveButton("Add Income", (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount <= 0) {
                        toast("Enter a valid amount");
                        return;
                    }

                    if (type.equals("Salary Income")) {
                        addSalaryIncome(amount);
                    } else if (type.equals("Business Income")) {
                        addBusinessIncome(amount);
                    } else if (type.equals("Rental Income")) {
                        addRentalIncome(amount);
                    } else if (type.equals("Investment Gains")) {
                        addInvestmentGains(amount);
                    } else {
                        addOtherIncome(amount);
                    }

                    addHistory("Recorded " + type + ": " + money(amount));

                    toast("Income recorded");
                    showDashboard();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- DEDUCTIONS ----------------

    private void showDeductions() {

        String[] options = {
                "Life Insurance - ₹50,000",
                "Health Insurance - ₹25,000",
                "Retirement Savings - ₹1,00,000",
                "Home Loan Interest - ₹2,00,000",
                "Education Loan Interest - ₹50,000",
                "Custom Deduction"
        };

        long[] amounts = {
                50000,
                25000,
                100000,
                200000,
                50000,
                0
        };

        new AlertDialog.Builder(activity)
                .setTitle("Claim Tax Deductions")
                .setItems(options, (dialog, which) -> {

                    if (which == 5) {
                        customDeduction();
                    } else {
                        addDeduction(amounts[which], options[which]);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void customDeduction() {

        EditText input = new EditText(activity);
        input.setHint("Enter deduction amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Custom Deduction")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount <= 0) {
                        toast("Enter a valid amount");
                        return;
                    }

                    addDeduction(amount, "Custom Deduction");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addDeduction(long amount, String description) {

        if (deductions >= 1000000) {
            toast("Maximum game deduction limit reached");
            return;
        }

        long allowed = Math.min(amount, 1000000 - deductions);

        deductions += allowed;
        taxFiled = false;

        addHistory(
                description + " claimed: " + money(allowed)
        );

        saveData();

        toast("Deduction recorded: " + money(allowed));
        showDashboard();
    }

    // ---------------- FILE TAX RETURN ----------------

    private void fileTaxReturn() {

        if (taxFiled) {
            showMessage(
                    "Already Filed",
                    "Your tax return for this financial year has already been filed."
            );
            return;
        }

        long estimated = calculateTotalTax();

        long balance = Math.max(0, estimated - totalTaxPaid);

        long refund = Math.max(0, totalTaxPaid - estimated);

        String details =
                "Total Income: " + money(totalIncome())
                        + "\nTaxable Income: " + money(taxableIncome())
                        + "\nEstimated Tax: " + money(estimated)
                        + "\nTax Already Paid: " + money(totalTaxPaid)
                        + "\n\nPending Tax: " + money(balance)
                        + "\nPotential Refund: " + money(refund)
                        + "\n\nSubmit your tax return?";

        new AlertDialog.Builder(activity)
                .setTitle("File Income Tax Return")
                .setMessage(details)
                .setPositiveButton("File Return", (dialog, which) -> {

                    taxFiled = true;
                    pendingTax = balance;

                    if (refund > 0) {
                        totalRefunds = safeAdd(totalRefunds, refund);

                        addHistory(
                                "Tax refund approved: " + money(refund)
                        );

                        // FIX: safely convert long refund total to int money.
                        money = (int) Math.min(
                                Integer.MAX_VALUE,
                                Math.max(0L, safeAdd((long) money, refund))
                        );

                        totalTaxPaid = 0;
                        pendingTax = 0;

                        if (callback != null) {
                            callback.onUpdate(
                                    money,
                                    health,
                                    happiness,
                                    "Your tax return was filed. You received a refund of " +
                                            money(refund) + "."
                            );
                        }

                    } else {

                        addHistory(
                                "Tax return filed. Pending tax: " + money(balance)
                        );
                    }

                    saveData();

                    showDashboard();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- PAY TAX ----------------

    private void payPendingTax() {

        if (pendingTax <= 0) {
            showMessage(
                    "No Pending Tax",
                    "You do not have any pending tax payments."
            );
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Pay Pending Tax")
                .setMessage(
                        "Pending Tax: " + money(pendingTax)
                                + "\nAvailable Cash: ₹" + money(money)
                                + "\n\nPay your outstanding tax?"
                )
                .setPositiveButton("Pay Tax", (dialog, which) -> {

                    if (money < pendingTax) {
                        toast("Insufficient money to pay tax");
                        return;
                    }

                    long paid = pendingTax;

                    money -= (int) Math.min(Integer.MAX_VALUE, paid);

                    totalTaxPaid = safeAdd(totalTaxPaid, paid);
                    pendingTax = 0;

                    addHistory("Income tax paid: " + money(paid));

                    if (callback != null) {
                        callback.onUpdate(
                                money,
                                health,
                                happiness,
                                "You paid your pending income tax of " +
                                        money(paid) + "."
                        );
                    }

                    saveData();
                    showDashboard();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- REFUNDS ----------------

    private void checkRefund() {

        long estimated = calculateTotalTax();
        long refund = Math.max(0, totalTaxPaid - estimated);

        showMessage(
                "Tax Refund Status",
                "Tax Already Paid: " + money(totalTaxPaid)
                        + "\nEstimated Tax: " + money(estimated)
                        + "\nPotential Refund: " + money(refund)
                        + "\n\nFile your return to process an eligible refund."
        );
    }

    // ---------------- AUDIT SYSTEM ----------------

    private void showAudit() {

        if (auditPending) {

            new AlertDialog.Builder(activity)
                    .setTitle("Tax Audit Notice")
                    .setMessage(
                            "The tax department has requested an audit of your financial records."
                    )
                    .setPositiveButton("Submit Records", (dialog, which) -> {
                        resolveAudit();
                    })
                    .setNegativeButton("Ignore Notice", (dialog, which) -> {
                        ignoreAudit();
                    })
                    .show();

            return;
        }

        String[] options = {
                "Check Audit Risk",
                "Request a Voluntary Audit"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Tax Department")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        checkAuditRisk();
                    } else {
                        requestAudit();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkAuditRisk() {

        int risk = 5;

        if (totalIncome() > 1000000) {
            risk += 20;
        }

        if (deductions > 300000) {
            risk += 20;
        }

        if (pendingTax > 0) {
            risk += 25;
        }

        if (!taxFiled && totalIncome() > 500000) {
            risk += 25;
        }

        risk = Math.min(95, risk);

        showMessage(
                "Audit Risk",
                "Estimated Audit Risk: " + risk + "%"
                        + "\n\nKeep accurate financial records and file your returns on time."
        );
    }

    private void requestAudit() {

        auditPending = true;

        addHistory("Voluntary tax audit requested.");

        saveData();

        showMessage(
                "Audit Requested",
                "You requested a voluntary tax audit. Submit your records to complete the process."
        );

        showAudit();
    }

    private void resolveAudit() {

        auditPending = false;
        auditCount++;

        boolean clean = random.nextInt(100) < 75;

        if (clean) {

            reputationBoost();

            addHistory("Tax audit completed successfully. No major issue found.");

            showMessage(
                    "Audit Completed",
                    "Your financial records passed the audit. No additional penalty was imposed."
            );

        } else {

            long penalty = Math.max(1000, calculateTotalTax() / 10);

            taxPenalties = safeAdd(taxPenalties, penalty);
            pendingTax = safeAdd(pendingTax, penalty);

            addHistory(
                    "Audit found discrepancies. Penalty: " + money(penalty)
            );

            showMessage(
                    "Audit Findings",
                    "The audit found discrepancies in your records."
                            + "\nAdditional penalty: " + money(penalty)
                            + "\nThe penalty has been added to pending tax."
            );
        }

        saveData();
        showDashboard();
    }

    private void ignoreAudit() {

        auditPending = false;

        long penalty = Math.max(5000, calculateTotalTax() / 5);

        taxPenalties = safeAdd(taxPenalties, penalty);
        pendingTax = safeAdd(pendingTax, penalty);

        happiness = Math.max(0, happiness - 5);

        addHistory(
                "Ignored tax audit notice. Penalty: " + money(penalty)
        );

        if (callback != null) {
            callback.onUpdate(
                    money,
                    health,
                    happiness,
                    "You ignored a tax audit notice and received a penalty of " +
                            money(penalty) + "."
            );
        }

        saveData();
        showDashboard();
    }

    private void reputationBoost() {
        // Successful audits do not change player money.
    }

    // ---------------- ANNUAL TAX UPDATE ----------------

    public void applyAnnualTax() {

        taxYear++;

        taxFiled = false;
        auditPending = false;

        salaryIncome = 0;
        businessIncome = 0;
        rentalIncome = 0;
        investmentGains = 0;
        otherIncome = 0;
        deductions = 0;

        pendingTax = 0;
        totalTaxPaid = 0;

        addHistory("Started financial year " + taxYear);

        saveData();
    }

    // ---------------- TAX HISTORY ----------------

    private void showHistory() {

        if (history.isEmpty()) {
            showMessage("Tax History", "No tax records available.");
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String item : history) {

            builder.append("• ")
                    .append(item)
                    .append("\n\n");
        }

        TextView view = new TextView(activity);
        view.setText(builder.toString());
        view.setTextColor(white);
        view.setTextSize(14);
        view.setPadding(20, 20, 20, 20);

        ScrollView scroll = new ScrollView(activity);
        scroll.addView(view);

        new AlertDialog.Builder(activity)
                .setTitle("Tax History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- MESSAGE ----------------

    private void showMessage(String title, String message) {

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // ---------------- INPUT ----------------

    private long readAmount(EditText input) {

        try {
            return Long.parseLong(
                    input.getText().toString().trim()
            );
        } catch (Exception e) {
            return 0;
        }
    }
}
