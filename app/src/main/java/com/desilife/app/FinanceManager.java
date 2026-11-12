package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public class FinanceManager {

    public interface MoneyListener {
        void onMoneyChanged(long newMoney);
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener externalListener;

    private long cash;

    private BankManager bankManager;
    private LoanManager loanManager;
    private HouseholdManager householdManager;
    private InsuranceManager insuranceManager;
    private InvestmentManager investmentManager;

    private LinearLayout content;
    private TransactionListener transactionListener;
    private boolean processingMonthly = false;

    public interface TransactionListener {
        void onTransaction(String category, long amount, long balance);
    }

    public void setTransactionListener(TransactionListener listener) {
        this.transactionListener = listener;
    }

    private void recordDelta(String category, long before) {
        long delta = cash - before;

        if (delta != 0 && transactionListener != null) {
            transactionListener.onTransaction(category, delta, cash);
        }
    }

    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(
                    new Locale("en", "IN"));

    private static final String PREF =
            "desilife_finance_data";

    public FinanceManager(Activity activity,
                          long currentCash,
                          MoneyListener listener) {

        this.activity = activity;
        this.cash = currentCash;
        this.externalListener = listener;

        prefs = activity.getSharedPreferences(
                PREF, Activity.MODE_PRIVATE);

        createManagers();
    }

    // ---------------- CREATE MANAGERS ----------------

    private void createManagers() {

        bankManager = new BankManager(
                activity,
                cash,
                this::updateCash
        );

        loanManager = new LoanManager(
                activity,
                cash,
                this::updateCash
        );

        householdManager = new HouseholdManager(
                activity,
                cash,
                this::updateCash
        );

        insuranceManager = new InsuranceManager(
                activity,
                cash,
                this::updateCash
        );

        investmentManager = new InvestmentManager(
                activity,
                cash,
                this::updateCash
        );

        syncCash();
    }

    // ---------------- CASH SYNCHRONIZATION ----------------

    private void updateCash(long newCash) {

        long previousCash = cash;
        cash = Math.max(0, newCash);

        syncCash();

        prefs.edit()
                .putLong("last_cash", cash)
                .apply();

        if (externalListener != null) {
            externalListener.onMoneyChanged(cash);
        }

        if (!processingMonthly && transactionListener != null) {

            long delta = cash - previousCash;

            if (delta != 0) {
                transactionListener.onTransaction(
                        "Manual finance transaction",
                        delta,
                        cash);
            }
        }
    }

    private void syncCash() {

        if (bankManager != null) {
            bankManager.setCash(cash);
        }

        if (loanManager != null) {
            loanManager.setCash(cash);
        }

        if (householdManager != null) {
            householdManager.setCash(cash);
        }

        if (insuranceManager != null) {
            insuranceManager.setCash(cash);
        }

        if (investmentManager != null) {
            investmentManager.setCash(cash);
        }
    }

    public void setCash(long newCash) {

        cash = Math.max(0, newCash);
        syncCash();

        prefs.edit()
                .putLong("last_cash", cash)
                .apply();
    }

    public long getCash() {
        return cash;
    }

    // ---------------- MAIN FINANCE SCREEN ----------------

    public void show() {

        syncCash();

        ScrollView scroll = new ScrollView(activity);

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 20, 20, 30);

        scroll.addView(content);

        content.addView(text("BANK & FINANCE", 26, true));

        content.addView(text(
                "Your complete financial life",
                14, false));

        addSpace(18);

        addCard("Available Cash", money(cash));

        addCard("Total Bank Balance",
                money(bankManager.getTotalBankBalance()));

        addCard("Outstanding Loan",
                money(loanManager.getOutstanding()));

        addCard("Investment Portfolio",
                money(investmentManager.getPortfolioValue()));

        addCard("Credit Score",
                loanManager.getCreditScore() + " / 900");

        addSpace(15);

        addButton("Bank & Savings",
                v -> openBank());

        addButton("Loans & Credit",
                v -> openLoans());

        addButton("Household Expenses",
                v -> openHousehold());

        addButton("Insurance",
                v -> openInsurance());

        addButton("Investments",
                v -> openInvestments());

        addButton("📒 Transaction History",
                v -> showTransactionHistory());

        addSpace(18);

        content.addView(text(
                "Financial systems are simulated during Age Up.",
                12, false));

        new AlertDialog.Builder(activity)
                .setTitle("Financial Life")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- TRANSACTION HISTORY ----------------

    private void showTransactionHistory() {

        SharedPreferences gameSave =
                activity.getSharedPreferences(
                        "DesiLifeSave",
                        Activity.MODE_PRIVATE);

        String history =
                gameSave.getString("transaction_history", "");

        // Support both actual newlines and older literal \n separators.
        history = history.replace("\\n", "\n").trim();

        LinearLayout historyLayout = new LinearLayout(activity);
        historyLayout.setOrientation(LinearLayout.VERTICAL);
        historyLayout.setPadding(16, 12, 16, 12);
        historyLayout.setBackgroundColor(Color.WHITE);

        if (history.isEmpty()) {

            TextView empty = new TextView(activity);
            empty.setText(
                    "No financial transactions recorded yet.\n\n" +
                    "Advance one in-game month to begin tracking."
            );
            empty.setTextColor(Color.rgb(75, 75, 75));
            empty.setTextSize(15);
            empty.setPadding(12, 20, 12, 20);

            historyLayout.addView(empty);

        } else {

            String[] entries = history.split("\n");

            // Display newest transaction first.
            for (int i = entries.length - 1; i >= 0; i--) {

                String entry = entries[i].trim();

                if (entry.isEmpty()) {
                    continue;
                }

                TextView transaction = new TextView(activity);

                transaction.setText(entry);
                transaction.setTextSize(14);
                transaction.setTextColor(Color.rgb(40, 48, 55));
                transaction.setPadding(10, 12, 10, 12);

                historyLayout.addView(
                        transaction,
                        new LinearLayout.LayoutParams(-1, -2)
                );

                View divider = new View(activity);
                divider.setBackgroundColor(
                        Color.rgb(225, 225, 225));

                historyLayout.addView(
                        divider,
                        new LinearLayout.LayoutParams(-1, 1)
                );
            }
        }

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.WHITE);
        scroll.addView(historyLayout);

        int maxHeight = (int) (
                activity.getResources()
                        .getDisplayMetrics().heightPixels * 0.68f
        );

        scroll.setLayoutParams(
                new LinearLayout.LayoutParams(-1, maxHeight)
        );

        new AlertDialog.Builder(activity)
                .setTitle("📒 Transaction History")
                .setView(scroll)
                .setPositiveButton("CLOSE", null)
                .show();
    }

    // ---------------- OPEN SECTIONS ----------------

    public void openBank() {
        syncCash();
        bankManager.show();
    }

    public void openLoans() {
        syncCash();
        loanManager.show();
    }

    public void openHousehold() {
        syncCash();
        householdManager.show();
    }

    public void openInsurance() {
        syncCash();
        insuranceManager.show();
    }

    public void openInvestments() {
        syncCash();
        investmentManager.show();
    }

    // ---------------- MONTHLY FINANCE PROCESSING ----------------

    /*
     * Processes exactly ONE in-game month.
     *
     * Order:
     * 1. Household monthly expenses.
     * 2. Loan EMI and monthly interest.
     * 3. Bank interest, charges, FD and RD.
     * 4. Investment market movement and SIP.
     *
     * Annual property tax is charged only when
     * chargeAnnualPropertyTax is true.
     */

    public void applyMonthlyFinance(
            boolean chargeAnnualPropertyTax) {

        syncCash();
        processingMonthly = true;

        try {

            long before = cash;

            householdManager.applyMonthlyExpenses(
                    chargeAnnualPropertyTax);

            syncCash();

            recordDelta(
                    "Household expenses and property tax",
                    before);

            before = cash;

            loanManager.applyMonthlyLoanPayment();

            syncCash();

            recordDelta(
                    "Loan EMI, interest and penalties",
                    before);

            before = cash;

            bankManager.applyMonthlyBanking();

            syncCash();

            recordDelta(
                    "Bank interest, fees and deposits",
                    before);

            before = cash;

            investmentManager.applyMonthlyInvestmentUpdate();

            syncCash();

            recordDelta(
                    "Investments and monthly SIP",
                    before);

        } finally {
            processingMonthly = false;
        }

        prefs.edit()
                .putLong("last_cash", cash)
                .apply();
    }

    // ---------------- ANNUAL FINANCE PROCESSING ----------------

    /*
     * Compatibility method for existing callers.
     *
     * Processes 12 individual monthly cycles.
     * Annual property tax is charged in the first month.
     * Annual insurance premiums are charged once after
     * all 12 monthly cycles.
     */

    public void applyAnnualFinance() {

        syncCash();

        for (int month = 0; month < 12; month++) {

            applyMonthlyFinance(month == 0);
        }

        // Insurance premiums are annual, not monthly.
        syncCash();

        long beforeInsurance = cash;

        processingMonthly = true;

        try {

            insuranceManager.applyAnnualPremiums();

            syncCash();

        } finally {
            processingMonthly = false;
        }

        recordDelta(
                "Annual insurance premiums",
                beforeInsurance);

        prefs.edit()
                .putLong("last_cash", cash)
                .apply();
    }

    // ---------------- ACCESS TO MANAGERS ----------------

    public BankManager getBankManager() {
        return bankManager;
    }

    public LoanManager getLoanManager() {
        return loanManager;
    }

    public HouseholdManager getHouseholdManager() {
        return householdManager;
    }

    public InsuranceManager getInsuranceManager() {
        return insuranceManager;
    }

    public InvestmentManager getInvestmentManager() {
        return investmentManager;
    }

    // ---------------- UI HELPERS ----------------

    private String money(long amount) {
        return currency.format(amount);
    }

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
                new LinearLayout.LayoutParams(1, height));
    }

    private void addCard(String title, String value) {

        LinearLayout card = new LinearLayout(activity);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);

        card.setBackground(background(
                Color.rgb(39, 48, 65)));

        card.addView(text(title, 14, false));
        card.addView(text(value, 22, true));

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
}
