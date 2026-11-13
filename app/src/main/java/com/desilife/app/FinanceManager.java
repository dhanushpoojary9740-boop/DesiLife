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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private void recordExpense(String category, long amount) {
        if (amount <= 0 || transactionListener == null) return;
        long actual = Math.min(amount, cash);
        // The caller updates cash first; reconstruct the balance immediately
        // before this item so each ledger entry has its own running balance.
        transactionListener.onTransaction(category, -actual, cash);
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
                        "Manual finance transaction", delta, cash);
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

        SharedPreferences gameSave = activity.getSharedPreferences(
                "DesiLifeSave", Activity.MODE_PRIVATE);

        String history = gameSave.getString("transaction_history", "");

        history = history.replace("\\\\n", "\n")
                .replace("\\n", "\n")
                .trim();

        final ArrayList<String> entries = new ArrayList<>();

        if (!history.isEmpty()) {
            String[] savedEntries = history.split("\\n");

            for (String entry : savedEntries) {
                if (entry != null && !entry.trim().isEmpty()) {
                    entries.add(entry.trim());
                }
            }
        }

        long totalIncome = 0;
        long totalExpense = 0;

        LinkedHashMap<String, long[]> monthlyTotals =
                new LinkedHashMap<>();

        for (String entry : entries) {

            String[] parts = entry.split("\\|");

            if (parts.length < 4) {
                continue;
            }

            String ageLabel = parts[0].trim();
            String direction = parts[1].trim()
                    .toUpperCase(Locale.ROOT);

            long amount = extractTransactionAmount(parts[3]);

            long[] totals = monthlyTotals.get(ageLabel);

            if (totals == null) {
                totals = new long[]{0, 0};
                monthlyTotals.put(ageLabel, totals);
            }

            if (direction.contains("INCOME")) {
                totalIncome += amount;
                totals[0] += amount;

            } else if (direction.contains("EXPENSE")) {
                totalExpense += amount;
                totals[1] += amount;
            }
        }

        LinearLayout screen = new LinearLayout(activity);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(14, 12, 14, 12);
        screen.setBackgroundColor(Color.WHITE);

        TextView summaryTitle = new TextView(activity);
        summaryTitle.setText("FINANCIAL SUMMARY");
        summaryTitle.setTextColor(Color.rgb(25, 75, 110));
        summaryTitle.setTextSize(15);
        summaryTitle.setTypeface(null, Typeface.BOLD);
        summaryTitle.setPadding(4, 4, 4, 8);
        screen.addView(summaryTitle);

        TextView summary = new TextView(activity);

        long net = totalIncome - totalExpense;

        summary.setText(
                "Total Income: +" + money(totalIncome) +
                "\nTotal Expenses: -" + money(totalExpense) +
                "\nNet Cash Flow: " + (net >= 0 ? "+" : "-") +
                money(Math.abs(net)) +
                "\nTransactions recorded: " + entries.size()
        );

        summary.setTextColor(Color.rgb(45, 55, 65));
        summary.setTextSize(14);
        summary.setLineSpacing(3, 1.0f);
        summary.setPadding(12, 10, 12, 10);
        summary.setBackground(background(Color.rgb(239, 244, 247)));
        screen.addView(summary);

        TextView monthlyTitle = new TextView(activity);
        monthlyTitle.setText("MONTHLY TOTALS");
        monthlyTitle.setTextColor(Color.rgb(25, 75, 110));
        monthlyTitle.setTextSize(14);
        monthlyTitle.setTypeface(null, Typeface.BOLD);
        monthlyTitle.setPadding(4, 14, 4, 6);
        screen.addView(monthlyTitle);

        LinearLayout monthlyList = new LinearLayout(activity);
        monthlyList.setOrientation(LinearLayout.VERTICAL);

        if (monthlyTotals.isEmpty()) {

            TextView noMonths = new TextView(activity);
            noMonths.setText("No monthly totals available yet.");
            noMonths.setTextColor(Color.DKGRAY);
            noMonths.setTextSize(13);

            monthlyList.addView(noMonths);

        } else {

            ArrayList> monthEntries =
                    new ArrayList<>(monthlyTotals.entrySet());

            for (int i = monthEntries.size() - 1; i >= 0; i--) {

                Map.Entry<String, long[]> item = monthEntries.get(i);
                long[] values = item.getValue();

                TextView month = new TextView(activity);

                month.setText(
                        item.getKey() +
                        "\nIncome: +" + money(values[0]) +
                        "     Expenses: -" + money(values[1]) +
                        "\nNet: " +
                        (values[0] - values[1] >= 0 ? "+" : "-") +
                        money(Math.abs(values[0] - values[1]))
                );

                month.setTextColor(Color.rgb(55, 65, 75));
                month.setTextSize(13);
                month.setPadding(10, 9, 10, 9);
                month.setBackground(
                        background(Color.rgb(247, 248, 249)));

                LinearLayout.LayoutParams monthParams =
                        new LinearLayout.LayoutParams(-1, -2);

                monthParams.bottomMargin = 5;

                monthlyList.addView(month, monthParams);
            }
        }

        ScrollView monthlyScroll = new ScrollView(activity);
        monthlyScroll.setFillViewport(false);
        monthlyScroll.setVerticalScrollBarEnabled(true);
        monthlyScroll.addView(monthlyList);

        int monthlyHeight = (int) (
                activity.getResources().getDisplayMetrics().density * 150
        );

        screen.addView(
                monthlyScroll,
                new LinearLayout.LayoutParams(-1, monthlyHeight));

        TextView historyTitle = new TextView(activity);
        historyTitle.setText("TRANSACTIONS");
        historyTitle.setTextColor(Color.rgb(25, 75, 110));
        historyTitle.setTextSize(15);
        historyTitle.setTypeface(null, Typeface.BOLD);
        historyTitle.setPadding(4, 14, 4, 6);
        screen.addView(historyTitle);

        LinearLayout filters = new LinearLayout(activity);
        filters.setOrientation(LinearLayout.HORIZONTAL);
        filters.setPadding(0, 2, 0, 8);
        screen.addView(filters);

        LinearLayout transactionList = new LinearLayout(activity);
        transactionList.setOrientation(LinearLayout.VERTICAL);
        transactionList.setPadding(2, 2, 2, 8);

        ScrollView transactionScroll = new ScrollView(activity);
        transactionScroll.setFillViewport(false);
        transactionScroll.setVerticalScrollBarEnabled(true);
        transactionScroll.addView(transactionList);

        screen.addView(
                transactionScroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        final String[] selectedFilter = new String[]{"ALL"};

        final TextView allFilter = createHistoryFilter("ALL");
        final TextView incomeFilter = createHistoryFilter("INCOME");
        final TextView expenseFilter = createHistoryFilter("EXPENSES");

        filters.addView(
                allFilter,
                new LinearLayout.LayoutParams(0, -2, 1));

        filters.addView(
                incomeFilter,
                new LinearLayout.LayoutParams(0, -2, 1));

        filters.addView(
                expenseFilter,
                new LinearLayout.LayoutParams(0, -2, 1));

        Runnable refresh = () -> {

            transactionList.removeAllViews();

            setHistoryFilterStyle(
                    allFilter, selectedFilter[0].equals("ALL"));

            setHistoryFilterStyle(
                    incomeFilter, selectedFilter[0].equals("INCOME"));

            setHistoryFilterStyle(
                    expenseFilter, selectedFilter[0].equals("EXPENSES"));

            int shown = 0;

            for (int i = entries.size() - 1; i >= 0; i--) {

                String entry = entries.get(i);
                String[] parts = entry.split("\\|");

                String direction = parts.length > 1
                        ? parts[1].trim().toUpperCase(Locale.ROOT)
                        : "";

                if (selectedFilter[0].equals("INCOME")
                        && !direction.contains("INCOME")) {
                    continue;
                }

                if (selectedFilter[0].equals("EXPENSES")
                        && !direction.contains("EXPENSE")) {
                    continue;
                }

                addTransactionHistoryRow(transactionList, entry);
                shown++;
            }

            if (shown == 0) {

                TextView empty = new TextView(activity);
                empty.setText("No transactions in this filter.");
                empty.setTextColor(Color.rgb(90, 90, 90));
                empty.setTextSize(14);
                empty.setPadding(12, 18, 12, 18);

                transactionList.addView(empty);
            }
        };

        allFilter.setOnClickListener(v -> {
            selectedFilter[0] = "ALL";
            refresh.run();
        });

        incomeFilter.setOnClickListener(v -> {
            selectedFilter[0] = "INCOME";
            refresh.run();
        });

        expenseFilter.setOnClickListener(v -> {
            selectedFilter[0] = "EXPENSES";
            refresh.run();
        });

        refresh.run();

        new AlertDialog.Builder(activity)
                .setTitle("📒 Transaction History")
                .setView(screen)
                .setPositiveButton("CLOSE", null)
                .show();
    }

    private long extractTransactionAmount(String value) {

        if (value == null) {
            return 0;
        }

        String digits = value.replaceAll("[^0-9]", "");

        if (digits.isEmpty()) {
            return 0;
        }

        try {
            return Long.parseLong(digits);

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private TextView createHistoryFilter(String label) {

        TextView filter = new TextView(activity);

        filter.setText(label);
        filter.setTextSize(12);
        filter.setGravity(android.view.Gravity.CENTER);
        filter.setPadding(5, 10, 5, 10);
        filter.setTypeface(null, Typeface.BOLD);

        return filter;
    }

    private void setHistoryFilterStyle(
            TextView filter, boolean selected) {

        filter.setTextColor(
                selected ? Color.WHITE : Color.rgb(45, 55, 65));

        filter.setBackground(background(
                selected
                        ? Color.rgb(42, 91, 135)
                        : Color.rgb(232, 237, 241)));
    }

    private void addTransactionHistoryRow(
            LinearLayout parent, String entry) {

        String[] parts = entry.split("\\|");

        String direction = parts.length > 1
                ? parts[1].trim().toUpperCase(Locale.ROOT)
                : "";

        boolean income = direction.contains("INCOME");

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(12, 10, 12, 10);
        card.setBackground(background(Color.rgb(247, 248, 249)));

        TextView description = new TextView(activity);
        description.setText(entry.trim());
        description.setTextColor(Color.rgb(50, 58, 65));
        description.setTextSize(13);
        description.setLineSpacing(3, 1.0f);

        card.addView(description);

        if (parts.length > 3) {

            TextView amount = new TextView(activity);

            amount.setText(parts[3].trim());

            amount.setTextColor(
                    income
                            ? Color.rgb(25, 135, 75)
                            : Color.rgb(190, 55, 55));

            amount.setTextSize(14);
            amount.setTypeface(null, Typeface.BOLD);
            amount.setPadding(0, 5, 0, 0);

            card.addView(amount);
        }

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = 6;

        parent.addView(card, params);
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

            // HOUSEHOLD: record each bill separately, allocating the actual
            // cash paid in the same order used by HouseholdManager.
            long householdCashBefore = cash;
            LinkedHashMap<String, Long> householdItems =
                    householdManager.getMonthlyExpenseBreakdown(
                            chargeAnnualPropertyTax);
            householdManager.applyMonthlyExpenses(
                    chargeAnnualPropertyTax);
            syncCash();

            long householdPaid = Math.max(
                    0, householdCashBefore - cash);
            long remainingHouseholdPaid = householdPaid;
            long runningHouseholdBalance = householdCashBefore;

            for (Map.Entry<String, Long> item : householdItems.entrySet()) {
                long due = Math.max(0, item.getValue());
                long paid = Math.min(remainingHouseholdPaid, due);
                if (paid > 0) {
                    remainingHouseholdPaid -= paid;
                    runningHouseholdBalance -= paid;
                    if (transactionListener != null) {
                        transactionListener.onTransaction(
                                item.getKey(), -paid, runningHouseholdBalance);
                    }
                }
            }

            // LOAN: split the actual EMI into principal and interest.
            long loanCashBefore = cash;
            loanManager.applyMonthlyLoanPayment();
            syncCash();

            long principalPaid = loanManager.getLastMonthPrincipalPaid();
            long interestPaid = loanManager.getLastMonthInterestPaid();
            long actualLoanDebit = Math.max(0, loanCashBefore - cash);
            long loanBalance = loanCashBefore;

            long recordedInterest = Math.min(interestPaid, actualLoanDebit);
            if (recordedInterest > 0) {
                loanBalance -= recordedInterest;
                if (transactionListener != null) {
                    transactionListener.onTransaction(
                            "Loan interest", -recordedInterest, loanBalance);
                }
            }

            long recordedPrincipal = Math.min(
                    principalPaid, Math.max(0, actualLoanDebit - recordedInterest));
            if (recordedPrincipal > 0) {
                loanBalance -= recordedPrincipal;
                if (transactionListener != null) {
                    transactionListener.onTransaction(
                            "Loan principal repayment", -recordedPrincipal, loanBalance);
                }
            }

            long before = cash;

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
