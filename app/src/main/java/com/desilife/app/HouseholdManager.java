package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class HouseholdManager {

    public interface MoneyListener {
        void onMoneyChanged(long newMoney);
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener listener;

    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(
                    new Locale("en", "IN"));

    private long cash;

    private long rent;
    private long maintenance;
    private long electricity;
    private long water;
    private long internet;
    private long groceries;
    private long transport;
    private long medical;
    private long propertyTax;
    private long monthlyIncome;

    private long totalExpenses;
    private long totalTax;
    private long emergencyExpenses;

    private String homeType = "Rented";
    private String jobStatus = "Employed";

    private LinearLayout content;

    private static final String PREF =
            "desilife_household_data";

    public HouseholdManager(Activity activity,
                            long currentCash,
                            MoneyListener listener) {

        this.activity = activity;
        this.cash = currentCash;
        this.listener = listener;

        prefs = activity.getSharedPreferences(
                PREF, Activity.MODE_PRIVATE);

        loadData();
    }

    // ---------------- LOAD AND SAVE ----------------

    private void loadData() {

        rent = prefs.getLong("rent", 8000);
        maintenance = prefs.getLong("maintenance", 1000);
        electricity = prefs.getLong("electricity", 1500);
        water = prefs.getLong("water", 300);
        internet = prefs.getLong("internet", 800);
        groceries = prefs.getLong("groceries", 5000);
        transport = prefs.getLong("transport", 2000);
        medical = prefs.getLong("medical", 1000);
        propertyTax = prefs.getLong("property_tax", 0);

        monthlyIncome = prefs.getLong("monthly_income", 0);

        totalExpenses = prefs.getLong("total_expenses", 0);
        totalTax = prefs.getLong("total_tax", 0);

        emergencyExpenses =
                prefs.getLong("emergency_expenses", 0);

        homeType = prefs.getString("home_type", "Rented");
        jobStatus = prefs.getString("job_status", "Employed");
    }

    private void saveData() {

        prefs.edit()
                .putLong("rent", rent)
                .putLong("maintenance", maintenance)
                .putLong("electricity", electricity)
                .putLong("water", water)
                .putLong("internet", internet)
                .putLong("groceries", groceries)
                .putLong("transport", transport)
                .putLong("medical", medical)
                .putLong("property_tax", propertyTax)
                .putLong("monthly_income", monthlyIncome)
                .putLong("total_expenses", totalExpenses)
                .putLong("total_tax", totalTax)
                .putLong("emergency_expenses", emergencyExpenses)
                .putString("home_type", homeType)
                .putString("job_status", jobStatus)
                .apply();
    }

    // ---------------- BASIC METHODS ----------------

    private String money(long amount) {
        return currency.format(amount);
    }

    private void updateCash(long amount) {

        cash = Math.max(0, amount);

        if (listener != null) {
            listener.onMoneyChanged(cash);
        }
    }

    public void setCash(long amount) {
        cash = Math.max(0, amount);
    }

    public long getCash() {
        return cash;
    }

    public long getMonthlyExpenses() {

        return rent + maintenance + electricity
                + water + internet + groceries
                + transport + medical;
    }

    public long getAnnualExpenses() {
        return getMonthlyExpenses() * 12;
    }

    public long getTotalExpenses() {
        return totalExpenses;
    }

    public long getTotalTax() {
        return totalTax;
    }

    public void setMonthlyIncome(long income) {

        monthlyIncome = Math.max(0, income);
        saveData();
    }

    public void setJobStatus(String status) {

        jobStatus = status;
        saveData();
    }

    private void toast(String message) {

        Toast.makeText(
                activity,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    // ---------------- UI HELPERS ----------------

    private TextView text(String value,
                          int size,
                          boolean bold) {

        TextView t = new TextView(activity);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    private GradientDrawable background(int color) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(20);

        return drawable;
    }

    private void addSpace(int height) {

        View space = new View(activity);

        content.addView(
                space,
                new LinearLayout.LayoutParams(1, height)
        );
    }

    private void addCard(String title, String value) {

        LinearLayout card = new LinearLayout(activity);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);

        card.setBackground(background(
                Color.rgb(39, 48, 65)));

        card.addView(text(title, 14, false));
        card.addView(text(value, 21, true));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = 10;

        content.addView(card, params);
    }

    private void addButton(String label,
                           View.OnClickListener action) {

        Button button = new Button(activity);

        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);

        button.setBackground(background(
                Color.rgb(54, 91, 143)));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = 8;

        content.addView(button, params);

        button.setOnClickListener(action);
    }

    // ---------------- MAIN SCREEN ----------------

    public void show() {

        ScrollView scroll = new ScrollView(activity);

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 20, 20, 30);

        scroll.addView(content);

        content.addView(text(
                "HOUSEHOLD & EXPENSES",
                24,
                true
        ));

        content.addView(text(
                "Manage your home and everyday living costs",
                14,
                false
        ));

        addSpace(18);

        addCard("Available Cash", money(cash));

        addCard(
                "Monthly Living Expenses",
                money(getMonthlyExpenses())
        );

        addCard(
                "Estimated Annual Expenses",
                money(getAnnualExpenses())
        );

        addCard(
                "Total Expenses Paid",
                money(totalExpenses)
        );

        addSpace(12);

        addCard("Home Type", homeType);
        addCard("Employment Status", jobStatus);

        addSpace(12);

        addButton(
                "View Monthly Expenses",
                v -> showExpenseBreakdown()
        );

        addButton(
                "Change Expense Settings",
                v -> showExpenseSettings()
        );

        addButton(
                "Set Employment Status",
                v -> showEmploymentMenu()
        );

        addButton(
                "Change Home Type",
                v -> showHomeMenu()
        );

        addButton(
                "Pay Emergency Expense",
                v -> showEmergencyDialog()
        );

        addButton(
                "View Expense History",
                v -> showHistory()
        );

        new AlertDialog.Builder(activity)
                .setTitle("Household")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- EXPENSE BREAKDOWN ----------------

    private void showExpenseBreakdown() {

        String details =
                "Rent: " + money(rent)
                + "\nMaintenance: " + money(maintenance)
                + "\nElectricity: " + money(electricity)
                + "\nWater: " + money(water)
                + "\nInternet & Mobile: " + money(internet)
                + "\nGroceries: " + money(groceries)
                + "\nTransport: " + money(transport)
                + "\nMedical: " + money(medical)
                + "\nProperty Tax: " + money(propertyTax)
                + "\n\nTotal monthly expenses: "
                + money(getMonthlyExpenses())
                + "\nAnnual property tax: "
                + money(propertyTax);

        new AlertDialog.Builder(activity)
                .setTitle("Monthly Expenses")
                .setMessage(details)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- EDIT EXPENSES ----------------

    private void showExpenseSettings() {

        String[] options = {
                "House Rent",
                "Maintenance",
                "Electricity",
                "Water",
                "Internet & Mobile",
                "Groceries",
                "Transport",
                "Medical",
                "Property Tax"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Select Expense to Edit")
                .setItems(options, (dialog, which) -> {

                    editExpense(which, options[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void editExpense(int type, String name) {

        long current;

        switch (type) {

            case 0:
                current = rent;
                break;

            case 1:
                current = maintenance;
                break;

            case 2:
                current = electricity;
                break;

            case 3:
                current = water;
                break;

            case 4:
                current = internet;
                break;

            case 5:
                current = groceries;
                break;

            case 6:
                current = transport;
                break;

            case 7:
                current = medical;
                break;

            default:
                current = propertyTax;
                break;
        }

        EditText input = new EditText(activity);

        input.setHint("Enter amount");
        input.setInputType(2);
        input.setText(String.valueOf(current));

        new AlertDialog.Builder(activity)
                .setTitle("Edit " + name)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {

                    long value = readAmount(input);

                    if (value < 0 || value > 10000000) {
                        toast("Enter a valid amount");
                        return;
                    }

                    switch (type) {

                        case 0:
                            rent = value;
                            break;

                        case 1:
                            maintenance = value;
                            break;

                        case 2:
                            electricity = value;
                            break;

                        case 3:
                            water = value;
                            break;

                        case 4:
                            internet = value;
                            break;

                        case 5:
                            groceries = value;
                            break;

                        case 6:
                            transport = value;
                            break;

                        case 7:
                            medical = value;
                            break;

                        case 8:
                            propertyTax = value;
                            break;
                    }

                    saveData();

                    toast(name + " updated");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- EMPLOYMENT ----------------

    private void showEmploymentMenu() {

        String[] options = {
                "Employed",
                "Unemployed",
                "Self-employed",
                "Retired"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Employment Status")
                .setItems(options, (dialog, which) -> {

                    jobStatus = options[which];

                    if (jobStatus.equals("Unemployed")) {
                        monthlyIncome = 0;
                    }

                    saveData();

                    toast("Status changed to " + jobStatus);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- HOME TYPE ----------------

    private void showHomeMenu() {

        String[] options = {
                "Rented",
                "Owned",
                "Mortgage"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Home Type")
                .setItems(options, (dialog, which) -> {

                    homeType = options[which];

                    if (!homeType.equals("Rented")) {
                        rent = 0;
                    }

                    saveData();

                    toast("Home type updated");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- EMERGENCY EXPENSE ----------------

    private void showEmergencyDialog() {

        EditText input = new EditText(activity);

        input.setHint("Emergency expense amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Emergency Expense")
                .setMessage("Available cash: " + money(cash))
                .setView(input)
                .setPositiveButton("Pay", (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount <= 0) {
                        toast("Enter a valid amount");
                        return;
                    }

                    if (amount > cash) {
                        toast("Not enough cash");
                        return;
                    }

                    updateCash(cash - amount);

                    emergencyExpenses += amount;
                    totalExpenses += amount;

                    addHistory(
                            "Emergency expense: " + money(amount)
                    );

                    saveData();

                    toast("Emergency expense paid");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- MONTHLY SIMULATION ----------------

    /*
     * Processes exactly ONE month of household expenses.
     *
     * chargeAnnualTax:
     * true  = also charge annual property tax this month.
     * false = charge only monthly living expenses.
     *
     * The aging system should pass true only once
     * during a 12-month yearly cycle.
     */

    public void applyMonthlyExpenses(boolean chargeAnnualTax) {

        long monthly = getMonthlyExpenses();

        long taxThisMonth =
                chargeAnnualTax ? propertyTax : 0;

        long due = safeAdd(monthly, taxThisMonth);

        long paid = Math.min(cash, due);

        updateCash(cash - paid);

        // Allocate payments to monthly expenses first.
        long monthlyPaid = Math.min(paid, monthly);

        long taxPaid = Math.max(
                0,
                paid - monthlyPaid
        );

        totalExpenses = safeAdd(
                totalExpenses,
                monthlyPaid
        );

        totalTax = safeAdd(
                totalTax,
                taxPaid
        );

        if (monthlyPaid == monthly) {

            addHistory(
                    "Monthly household bills paid: "
                            + money(monthlyPaid)
            );

        } else {

            addHistory(
                    "Household bills partially paid: "
                            + money(monthlyPaid)
                            + " out of " + money(monthly)
            );
        }

        if (taxThisMonth > 0) {

            if (taxPaid == taxThisMonth) {

                addHistory(
                        "Annual property tax paid: "
                                + money(taxPaid)
                );

            } else {

                addHistory(
                        "Property tax partially paid: "
                                + money(taxPaid)
                                + " out of " + money(taxThisMonth)
                );
            }
        }

        if (paid < due) {

            addHistory(
                    "Household expenses unpaid: "
                            + money(due - paid)
            );
        }

        saveData();
    }

    /*
     * Convenience method for a normal month.
     * Does not charge annual property tax.
     */

    public void applyMonthlyExpenses() {
        applyMonthlyExpenses(false);
    }

    // ---------------- ANNUAL SIMULATION ----------------

    /*
     * Compatibility method.
     * Processes 12 individual monthly cycles.
     * Property tax is charged only in the first cycle.
     */

    public void applyAnnualExpenses() {

        long startingExpenses = totalExpenses;
        long startingTax = totalTax;

        for (int month = 0; month < 12; month++) {

            applyMonthlyExpenses(month == 0);
        }

        long expensesPaid =
                totalExpenses - startingExpenses;

        long taxPaid =
                totalTax - startingTax;

        addHistory(
                "Annual household expenses processed: "
                        + money(safeAdd(expensesPaid, taxPaid))
        );

        saveData();
    }

    // ---------------- INCOME TAX ----------------

    public long calculateEstimatedAnnualIncomeTax(
            long annualIncome) {

        if (annualIncome <= 0) {
            return 0;
        }

        // Simplified fictional game tax calculation.
        // This is not an actual Indian tax calculator.

        long tax = 0;

        if (annualIncome > 400000) {

            tax += (
                    Math.min(annualIncome, 800000)
                            - 400000
            ) * 5 / 100;
        }

        if (annualIncome > 800000) {

            tax += (
                    Math.min(annualIncome, 1200000)
                            - 800000
            ) * 10 / 100;
        }

        if (annualIncome > 1200000) {

            tax += (
                    Math.min(annualIncome, 1600000)
                            - 1200000
            ) * 15 / 100;
        }

        if (annualIncome > 1600000) {

            tax += (
                    annualIncome - 1600000
            ) * 20 / 100;
        }

        return tax;
    }

    // ---------------- HISTORY ----------------

    private void addHistory(String message) {

        String old = prefs.getString("history", "");

        String updated = message + "\n" + old;

        String[] lines = updated.split("\n");

        StringBuilder limited = new StringBuilder();

        for (int i = 0; i < Math.min(50, lines.length); i++) {

            limited.append(lines[i]).append("\n");
        }

        prefs.edit()
                .putString("history", limited.toString())
                .apply();
    }

    private void showHistory() {

        String history = prefs.getString(
                "history",
                "No expense history available."
        );

        TextView view = text(history, 14, false);

        view.setPadding(20, 20, 20, 20);

        ScrollView scroll = new ScrollView(activity);

        scroll.addView(view);

        new AlertDialog.Builder(activity)
                .setTitle("Expense History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- SAFE CALCULATION ----------------

    private long safeAdd(long a, long b) {

        if (b > 0 && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }

        if (b < 0 && a < Long.MIN_VALUE - b) {
            return Long.MIN_VALUE;
        }

        return a + b;
    }

    // ---------------- INPUT ----------------

    private long readAmount(EditText input) {

        try {

            return Long.parseLong(
                    input.getText().toString().trim()
            );

        } catch (Exception e) {

            return -1;
        }
    }
}
