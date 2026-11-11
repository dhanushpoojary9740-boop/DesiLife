package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BankManager {

    public interface MoneyListener {
        void onMoneyChanged(long newMoney);
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener listener;

    private long cash;

    private long savingsBalance;
    private long currentBalance;

    private long fdPrincipal;
    private double fdRate;
    private int fdMonths;
    private int fdElapsed;

    private long rdMonthly;
    private long rdBalance;
    private double rdRate;
    private int rdMonths;
    private int rdElapsed;

    private long totalInterest;
    private long totalCharges;

    private final ArrayList<String> history = new ArrayList<>();

    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private LinearLayout content;

    private static final String PREF = "desilife_bank_data";

    public BankManager(Activity activity, long currentCash,
                       MoneyListener listener) {

        this.activity = activity;
        this.cash = currentCash;
        this.listener = listener;

        prefs = activity.getSharedPreferences(
                PREF, Activity.MODE_PRIVATE);

        loadData();
    }

    private void loadData() {

        savingsBalance = prefs.getLong("savings", 0);
        currentBalance = prefs.getLong("current", 0);

        fdPrincipal = prefs.getLong("fd_principal", 0);
        fdRate = Double.longBitsToDouble(
                prefs.getLong("fd_rate", Double.doubleToLongBits(7.0)));

        fdMonths = prefs.getInt("fd_months", 0);
        fdElapsed = prefs.getInt("fd_elapsed", 0);

        rdMonthly = prefs.getLong("rd_monthly", 0);
        rdBalance = prefs.getLong("rd_balance", 0);

        rdRate = Double.longBitsToDouble(
                prefs.getLong("rd_rate", Double.doubleToLongBits(6.5)));

        rdMonths = prefs.getInt("rd_months", 0);
        rdElapsed = prefs.getInt("rd_elapsed", 0);

        totalInterest = prefs.getLong("total_interest", 0);
        totalCharges = prefs.getLong("total_charges", 0);

        int count = prefs.getInt("history_count", 0);

        for (int i = 0; i < count; i++) {
            String item = prefs.getString("history_" + i, null);
            if (item != null) history.add(item);
        }
    }

    private void saveData() {

        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("savings", savingsBalance);
        editor.putLong("current", currentBalance);

        editor.putLong("fd_principal", fdPrincipal);
        editor.putLong("fd_rate",
                Double.doubleToLongBits(fdRate));
        editor.putInt("fd_months", fdMonths);
        editor.putInt("fd_elapsed", fdElapsed);

        editor.putLong("rd_monthly", rdMonthly);
        editor.putLong("rd_balance", rdBalance);
        editor.putLong("rd_rate",
                Double.doubleToLongBits(rdRate));
        editor.putInt("rd_months", rdMonths);
        editor.putInt("rd_elapsed", rdElapsed);

        editor.putLong("total_interest", totalInterest);
        editor.putLong("total_charges", totalCharges);

        editor.putInt("history_count", history.size());

        for (int i = 0; i < history.size(); i++) {
            editor.putString("history_" + i, history.get(i));
        }

        editor.apply();
    }

    private String money(long amount) {
        return currency.format(amount);
    }

    private void addHistory(String message) {

        history.add(0, message);

        while (history.size() > 50) {
            history.remove(history.size() - 1);
        }

        saveData();
    }

    private void updateCash(long newCash) {

        cash = newCash;

        if (listener != null) {
            listener.onMoneyChanged(cash);
        }
    }

    public void setCash(long newCash) {
        cash = newCash;
    }

    public long getCash() {
        return cash;
    }

    public long getTotalBankBalance() {
        return savingsBalance + currentBalance
                + fdPrincipal + rdBalance;
    }

    public long getSavingsBalance() {
        return savingsBalance;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }

    // ---------------- MAIN SCREEN ----------------

    public void show() {

        ScrollView scroll = new ScrollView(activity);

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 20, 20, 30);

        scroll.addView(content);

        TextView heading = text("BANK & FINANCE", 25, true);
        content.addView(heading);

        TextView subtitle = text(
                "Manage your money, accounts and deposits", 14, false);
        content.addView(subtitle);

        addSpace(18);

        addCard("Available Cash", money(cash));
        addCard("Total Bank Balance", money(getTotalBankBalance()));

        addSpace(12);

        addAccountCard("Savings Account",
                savingsBalance,
                "Interest: 3.5% annually");

        addAccountCard("Current Account",
                currentBalance,
                "No interest | Minimum balance: ₹5,000");

        addSpace(15);

        addButton("Deposit Money", v -> showDepositDialog());

        addButton("Withdraw Money", v -> showWithdrawDialog());

        addButton("Fixed Deposit (FD)", v -> showFDMenu());

        addButton("Recurring Deposit (RD)", v -> showRDMenu());

        addButton("Transaction History", v -> showHistory());

        addButton("Bank Account Settings", v -> showAccountInfo());

        addSpace(20);

        TextView note = text(
                "Bank interest and charges are calculated during Age Up.",
                12, false);
        content.addView(note);

        new AlertDialog.Builder(activity)
                .setTitle("Bank & Finance")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- UI HELPERS ----------------

    private TextView text(String value, int size, boolean bold) {

        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    private void addSpace(int height) {

        View space = new View(activity);
        content.addView(space,
                new LinearLayout.LayoutParams(
                        1, height));
    }

    private GradientDrawable background(int color) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(22);
        return drawable;
    }

    private void addCard(String title, String value) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackground(background(Color.rgb(39, 48, 65)));

        TextView name = text(title, 14, false);
        TextView amount = text(value, 22, true);

        card.addView(name);
        card.addView(amount);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1, -2);

        params.bottomMargin = 12;
        content.addView(card, params);
    }

    private void addAccountCard(String name,
                                long balance,
                                String description) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackground(background(Color.rgb(31, 65, 67)));

        card.addView(text(name, 17, true));
        card.addView(text(money(balance), 23, true));
        card.addView(text(description, 12, false));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = 12;
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

    private void toast(String message) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show();
    }

    // ---------------- DEPOSIT ----------------

    private void showDepositDialog() {

        EditText amountInput = new EditText(activity);
        amountInput.setHint("Enter amount");
        amountInput.setInputType(2);

        LinearLayout layout = new LinearLayout(activity);
        layout.setPadding(25, 15, 25, 5);
        layout.addView(amountInput);

        new AlertDialog.Builder(activity)
                .setTitle("Deposit Money")
                .setMessage("Available cash: " + money(cash))
                .setView(layout)
                .setItems(new String[]{
                        "Savings Account",
                        "Current Account"
                }, (dialog, which) -> {

                    String value = amountInput.getText()
                            .toString().trim();

                    // The account selection dialog is handled below.
                    showDepositAmountDialog(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDepositAmountDialog(int account) {

        EditText input = new EditText(activity);
        input.setHint("Enter deposit amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle(account == 0
                        ? "Deposit to Savings"
                        : "Deposit to Current Account")
                .setMessage("Available cash: " + money(cash))
                .setView(input)
                .setPositiveButton("Deposit", (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount <= 0) {
                        toast("Enter a valid amount");
                        return;
                    }

                    if (amount > cash) {
                        toast("Not enough available cash");
                        return;
                    }

                    if (account == 0) {
                        savingsBalance += amount;
                    } else {
                        currentBalance += amount;
                    }

                    updateCash(cash - amount);

                    addHistory("Deposited " + money(amount)
                            + " into "
                            + (account == 0
                            ? "Savings Account"
                            : "Current Account"));

                    saveData();
                    toast("Deposit successful");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- WITHDRAW ----------------

    private void showWithdrawDialog() {

        new AlertDialog.Builder(activity)
                .setTitle("Withdraw Money")
                .setItems(new String[]{
                        "Savings Account",
                        "Current Account"
                }, (dialog, which) -> {

                    showWithdrawAmountDialog(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showWithdrawAmountDialog(int account) {

        EditText input = new EditText(activity);
        input.setHint("Enter withdrawal amount");
        input.setInputType(2);

        long balance = account == 0
                ? savingsBalance : currentBalance;

        new AlertDialog.Builder(activity)
                .setTitle("Withdraw")
                .setMessage("Account balance: " + money(balance))
                .setView(input)
                .setPositiveButton("Withdraw", (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount <= 0) {
                        toast("Enter a valid amount");
                        return;
                    }

                    if (amount > balance) {
                        toast("Insufficient bank balance");
                        return;
                    }

                    if (account == 0) {
                        savingsBalance -= amount;
                    } else {
                        currentBalance -= amount;
                    }

                    updateCash(cash + amount);

                    addHistory("Withdrawn " + money(amount)
                            + " from "
                            + (account == 0
                            ? "Savings Account"
                            : "Current Account"));

                    saveData();
                    toast("Withdrawal successful");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private long readAmount(EditText input) {

        try {
            return Long.parseLong(
                    input.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // ---------------- FIXED DEPOSIT ----------------

    private void showFDMenu() {

        String[] options = {
                "Open Fixed Deposit",
                "View Fixed Deposit"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Fixed Deposit")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        openFD();
                    } else {
                        viewFD();
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void openFD() {

        if (fdPrincipal > 0) {
            toast("You already have an active FD");
            return;
        }

        EditText amount = new EditText(activity);
        amount.setHint("FD amount");
        amount.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Open Fixed Deposit")
                .setMessage("Available cash: " + money(cash)
                        + "\nInterest: 7% annually")
                .setView(amount)
                .setPositiveButton("Next", (dialog, which) -> {

                    long value = readAmount(amount);

                    if (value < 1000 || value > cash) {
                        toast("Minimum FD is ₹1,000");
                        return;
                    }

                    chooseFDTenure(value);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseFDTenure(long amount) {

        String[] durations = {
                "6 Months",
                "1 Year",
                "2 Years",
                "3 Years",
                "5 Years"
        };

        int[] months = {6, 12, 24, 36, 60};

        new AlertDialog.Builder(activity)
                .setTitle("Select FD Duration")
                .setItems(durations, (dialog, which) -> {

                    fdPrincipal = amount;
                    fdRate = 7.0;
                    fdMonths = months[which];
                    fdElapsed = 0;

                    updateCash(cash - amount);

                    addHistory("Opened FD: " + money(amount)
                            + " for " + fdMonths + " months");

                    saveData();
                    toast("Fixed Deposit opened");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void viewFD() {

        if (fdPrincipal <= 0) {
            toast("No active Fixed Deposit");
            return;
        }

        double estimated = fdPrincipal *
                Math.pow(1 + fdRate / 1200,
                        fdMonths);

        new AlertDialog.Builder(activity)
                .setTitle("Your Fixed Deposit")
                .setMessage(
                        "Principal: " + money(fdPrincipal)
                        + "\nInterest rate: " + fdRate + "%"
                        + "\nDuration: " + fdMonths + " months"
                        + "\nCompleted: " + fdElapsed + " months"
                        + "\nEstimated maturity: "
                        + money((long) estimated))
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- RECURRING DEPOSIT ----------------

    private void showRDMenu() {

        String[] options = {
                "Open Recurring Deposit",
                "View Recurring Deposit"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Recurring Deposit")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        openRD();
                    } else {
                        viewRD();
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void openRD() {

        if (rdMonthly > 0) {
            toast("You already have an active RD");
            return;
        }

        EditText amount = new EditText(activity);
        amount.setHint("Monthly installment");
        amount.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Open Recurring Deposit")
                .setMessage("Monthly installment starts from next Age Up."
                        + "\nInterest: 6.5% annually")
                .setView(amount)
                .setPositiveButton("Next", (dialog, which) -> {

                    long value = readAmount(amount);

                    if (value < 500 || value > 100000) {
                        toast("Enter ₹500 to ₹1,00,000");
                        return;
                    }

                    chooseRDTenure(value);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseRDTenure(long monthly) {

        String[] durations = {
                "6 Months",
                "1 Year",
                "2 Years",
                "3 Years",
                "5 Years"
        };

        int[] months = {6, 12, 24, 36, 60};

        new AlertDialog.Builder(activity)
                .setTitle("Select RD Duration")
                .setItems(durations, (dialog, which) -> {

                    rdMonthly = monthly;
                    rdBalance = 0;
                    rdRate = 6.5;
                    rdMonths = months[which];
                    rdElapsed = 0;

                    addHistory("Opened RD: "
                            + money(monthly)
                            + " monthly for "
                            + rdMonths + " months");

                    saveData();
                    toast("Recurring Deposit opened");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void viewRD() {

        if (rdMonthly <= 0) {
            toast("No active Recurring Deposit");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Your Recurring Deposit")
                .setMessage(
                        "Monthly installment: " + money(rdMonthly)
                        + "\nDeposited amount: " + money(rdBalance)
                        + "\nInterest rate: " + rdRate + "%"
                        + "\nDuration: " + rdMonths + " months"
                        + "\nCompleted: " + rdElapsed + " months")
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- ANNUAL BANK SIMULATION ----------------

    public void applyAnnualBanking() {

        for (int month = 0; month < 12; month++) {

            // Savings account interest: 3.5% annually.
            if (savingsBalance > 0) {

                long interest = Math.round(
                        savingsBalance * 0.035 / 12.0);

                savingsBalance += interest;
                totalInterest += interest;
            }

            // Current account minimum balance.
            if (currentBalance < 5000) {

                long charge = 100;

                if (currentBalance >= charge) {
                    currentBalance -= charge;
                    totalCharges += charge;

                    addHistory("Current account maintenance charge: "
                            + money(charge));
                }
            }

            // Fixed Deposit monthly compounding.
            if (fdPrincipal > 0 && fdElapsed < fdMonths) {

                long interest = Math.round(
                        fdPrincipal * fdRate / 1200.0);

                fdPrincipal += interest;
                totalInterest += interest;
                fdElapsed++;

                if (fdElapsed >= fdMonths) {

                    savingsBalance += fdPrincipal;

                    addHistory("FD matured: "
                            + money(fdPrincipal));

                    fdPrincipal = 0;
                    fdElapsed = 0;
                    fdMonths = 0;
                }
            }

            // Recurring Deposit installments.
            if (rdMonthly > 0 && rdElapsed < rdMonths) {

                if (cash >= rdMonthly) {

                    updateCash(cash - rdMonthly);

                    rdBalance += rdMonthly;

                    long interest = Math.round(
                            rdBalance * rdRate / 1200.0);

                    rdBalance += interest;
                    totalInterest += interest;

                    rdElapsed++;

                    if (rdElapsed >= rdMonths) {

                        savingsBalance += rdBalance;

                        addHistory("RD matured: "
                                + money(rdBalance));

                        rdBalance = 0;
                        rdMonthly = 0;
                        rdElapsed = 0;
                        rdMonths = 0;
                    }

                } else {
                    addHistory("RD installment missed: "
                            + money(rdMonthly));
                }
            }
        }

        addHistory("Annual banking interest and charges processed");

        saveData();
    }

    // ---------------- ACCOUNT INFORMATION ----------------

    private void showAccountInfo() {

        new AlertDialog.Builder(activity)
                .setTitle("Bank Account Information")
                .setMessage(
                        "Savings minimum balance: ₹0"
                        + "\nSavings interest: 3.5% annually"
                        + "\nCurrent minimum balance: ₹5,000"
                        + "\nCurrent account charge: ₹100/month"
                        + "\n\nTotal interest earned: "
                        + money(totalInterest)
                        + "\nTotal maintenance charges: "
                        + money(totalCharges))
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- TRANSACTION HISTORY ----------------

    private void showHistory() {

        if (history.isEmpty()) {
            toast("No transactions yet");
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String item : history) {
            builder.append("• ")
                    .append(item)
                    .append("\n\n");
        }

        TextView view = text(builder.toString(), 14, false);
        view.setPadding(20, 20, 20, 20);

        ScrollView scroll = new ScrollView(activity);
        scroll.addView(view);

        new AlertDialog.Builder(activity)
                .setTitle("Transaction History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }
}
