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
import android.widget.Toast;

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

        cash = Math.max(0, newCash);

        syncCash();

        prefs.edit()
                .putLong("last_cash", cash)
                .apply();

        if (externalListener != null) {
            externalListener.onMoneyChanged(cash);
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

        // 1. Household monthly expenses.
        householdManager.applyMonthlyExpenses(
                chargeAnnualPropertyTax);

        syncCash();

        // 2. Loan EMI and interest.
        loanManager.applyMonthlyLoanPayment();

        syncCash();

        // 3. Banking interest, charges and deposits.
        bankManager.applyMonthlyBanking();

        syncCash();

        // 4. Monthly investment and SIP update.
        investmentManager.applyMonthlyInvestmentUpdate();

        syncCash();

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

        insuranceManager.applyAnnualPremiums();

        syncCash();

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

        content.addView(space,
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
