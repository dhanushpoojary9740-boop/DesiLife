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

public class LoanManager {

    public interface MoneyListener {
        void onMoneyChanged(long newMoney);
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener listener;
    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private long cash;
    private String loanType = "";
    private long principal, outstanding;
    private double interestRate;
    private int tenureMonths, monthsPaid;
    private int missedPayments, consecutiveMissed;
    private int creditScore = 700;
    private long lateFees, totalInterestPaid;
    private boolean repossessed;
    private LinearLayout content;

    private static final String PREF = "desilife_loan_data";

    public LoanManager(Activity activity, long currentCash,
                       MoneyListener listener) {
        this.activity = activity;
        this.cash = currentCash;
        this.listener = listener;
        prefs = activity.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
        loadData();
    }

    private void loadData() {
        loanType = prefs.getString("loan_type", "");
        principal = prefs.getLong("principal", 0);
        outstanding = prefs.getLong("outstanding", 0);
        interestRate = Double.longBitsToDouble(
                prefs.getLong("rate", Double.doubleToLongBits(0)));
        tenureMonths = prefs.getInt("tenure", 0);
        monthsPaid = prefs.getInt("months_paid", 0);
        missedPayments = prefs.getInt("missed", 0);
        consecutiveMissed = prefs.getInt("consecutive_missed", 0);
        creditScore = prefs.getInt("credit_score", 700);
        lateFees = prefs.getLong("late_fees", 0);
        totalInterestPaid = prefs.getLong("interest_paid", 0);
        repossessed = prefs.getBoolean("repossessed", false);
    }

    private void saveData() {
        prefs.edit()
                .putString("loan_type", loanType)
                .putLong("principal", principal)
                .putLong("outstanding", outstanding)
                .putLong("rate", Double.doubleToLongBits(interestRate))
                .putInt("tenure", tenureMonths)
                .putInt("months_paid", monthsPaid)
                .putInt("missed", missedPayments)
                .putInt("consecutive_missed", consecutiveMissed)
                .putInt("credit_score", creditScore)
                .putLong("late_fees", lateFees)
                .putLong("interest_paid", totalInterestPaid)
                .putBoolean("repossessed", repossessed)
                .apply();
    }

    private String money(long amount) {
        return currency.format(amount);
    }

    private void updateCash(long amount) {
        cash = amount;
        if (listener != null) listener.onMoneyChanged(cash);
    }

    public void setCash(long amount) {
        cash = amount;
    }

    public long getCash() {
        return cash;
    }

    public long getOutstanding() {
        return outstanding;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public boolean hasActiveLoan() {
        return outstanding > 0;
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private TextView text(String value, int size, boolean bold) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private GradientDrawable background(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(20);
        return d;
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
        card.setBackground(background(Color.rgb(39, 48, 65)));
        card.addView(text(title, 14, false));
        card.addView(text(value, 22, true));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = 12;
        content.addView(card, p);
    }

    private void addButton(String label, View.OnClickListener action) {
        Button button = new Button(activity);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackground(background(Color.rgb(54, 91, 143)));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = 8;
        content.addView(button, p);
        button.setOnClickListener(action);
    }

    // MAIN SCREEN

    public void show() {
        ScrollView scroll = new ScrollView(activity);
        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 20, 20, 30);
        scroll.addView(content);

        content.addView(text("LOANS & CREDIT", 25, true));
        content.addView(text(
                "Borrow responsibly and build your credit history",
                14, false));

        addSpace(18);
        addCard("Available Cash", money(cash));
        addCard("Credit Score", creditScore + " / 900");
        addCard("Outstanding Loan", money(outstanding));

        addSpace(10);

        if (hasActiveLoan()) {
            addActiveLoanDetails();
            addButton("Make Extra Repayment", v -> extraRepayment());
            addButton("Close Loan Early", v -> closeLoan());
        } else {
            addCard("Loan Status", "No active loan");
            addButton("Explore Loan Offers", v -> showLoanTypes());
        }

        addButton("View Credit Report", v -> showCreditReport());
        addButton("Loan Information", v -> showLoanInformation());

        new AlertDialog.Builder(activity)
                .setTitle("Loans & Credit")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    private void addActiveLoanDetails() {
        addCard("Loan Type", loanType);
        addCard("Original Principal", money(principal));
        addCard("Interest Rate",
                String.format(Locale.US, "%.1f%%", interestRate));
        addCard("Loan Tenure", tenureMonths + " months");
        addCard("Months Paid", monthsPaid + "");
        addCard("Missed Payments", missedPayments + "");
        addCard("Late Fees", money(lateFees));
        addCard("Total Interest Paid", money(totalInterestPaid));
        addCard("Estimated Monthly EMI", money(calculateEMI()));

        if (repossessed) addCard("Asset Status", "Repossessed");
    }

    // LOAN TYPES

    private void showLoanTypes() {
        String[] types = {
                "Personal Loan - 18%",
                "Home Loan - 8.5%",
                "Mortgage Loan - 10%",
                "Vehicle Loan - 11%",
                "Education Loan - 9%",
                "Gold Loan - 12%",
                "Business Loan - 15%"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose a Loan")
                .setItems(types, (dialog, which) -> {
                    String[] names = {
                            "Personal Loan", "Home Loan",
                            "Mortgage Loan", "Vehicle Loan",
                            "Education Loan", "Gold Loan",
                            "Business Loan"
                    };

                    double[] rates = {18, 8.5, 10, 11, 9, 12, 15};
                    int[] maxTenure = {60, 300, 180, 84, 120, 36, 120};

                    loanType = names[which];
                    interestRate = rates[which];
                    chooseLoanAmount(maxTenure[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseLoanAmount(int maxTenure) {
        String[] amounts = {
                "₹50,000", "₹1,00,000", "₹2,50,000",
                "₹5,00,000", "₹10,00,000"
        };

        long[] values = {50000, 100000, 250000, 500000, 1000000};

        new AlertDialog.Builder(activity)
                .setTitle(loanType + " Amount")
                .setItems(amounts, (dialog, which) ->
                        chooseTenure(values[which], maxTenure))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseTenure(long amount, int maxTenure) {
        int[] choices = {12, 24, 36, 60, 84, 120, 180, 240, 300};

        java.util.ArrayList<Integer> valid =
                new java.util.ArrayList<>();

        for (int value : choices) {
            if (value <= maxTenure) valid.add(value);
        }

        String[] labels = new String[valid.size()];
        for (int i = 0; i < valid.size(); i++) {
            labels[i] = valid.get(i) + " months";
        }

        new AlertDialog.Builder(activity)
                .setTitle("Choose Repayment Period")
                .setItems(labels, (dialog, which) ->
                        confirmLoan(amount, valid.get(which)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // LOAN APPROVAL

    private void confirmLoan(long amount, int months) {
        double effectiveRate = interestRate;

        if (creditScore >= 800) {
            effectiveRate -= 1.0;
        } else if (creditScore >= 750) {
            effectiveRate -= 0.5;
        } else if (creditScore < 600) {
            effectiveRate += 2.0;
        }

        final double approvedRate = effectiveRate;

        long processingFee = Math.round(amount * 0.02);
        long gst = Math.round(processingFee * 0.18);
        long disbursed = amount - processingFee - gst;

        double monthlyRate = approvedRate / 1200.0;
        double emi;

        if (monthlyRate == 0) {
            emi = amount / (double) months;
        } else {
            emi = amount * monthlyRate *
                    Math.pow(1 + monthlyRate, months) /
                    (Math.pow(1 + monthlyRate, months) - 1);
        }

        String message =
                "Loan: " + loanType
                + "\nPrincipal: " + money(amount)
                + "\nInterest: " + approvedRate + "%"
                + "\nTenure: " + months + " months"
                + "\nEstimated EMI: " + money(Math.round(emi))
                + "\nProcessing fee: " + money(processingFee)
                + "\nGST on fee: " + money(gst)
                + "\nAmount received: " + money(disbursed)
                + "\n\nApprove this loan?";

        new AlertDialog.Builder(activity)
                .setTitle("Loan Offer")
                .setMessage(message)
                .setPositiveButton("Accept", (dialog, which) -> {
                    principal = amount;
                    outstanding = amount;
                    interestRate = approvedRate;
                    tenureMonths = months;
                    monthsPaid = 0;
                    missedPayments = 0;
                    consecutiveMissed = 0;
                    lateFees = 0;
                    totalInterestPaid = 0;
                    repossessed = false;

                    updateCash(cash + disbursed);
                    addHistory("Loan approved: " + loanType
                            + ", principal " + money(amount));

                    saveData();
                    toast("Loan approved");
                })
                .setNegativeButton("Reject", null)
                .show();
    }

    // EMI CALCULATION

    public long calculateEMI() {
        if (outstanding <= 0 || tenureMonths <= 0) return 0;

        double rate = interestRate / 1200.0;
        int remaining = Math.max(1, tenureMonths - monthsPaid);

        double emi;

        if (rate == 0) {
            emi = outstanding / (double) remaining;
        } else {
            emi = outstanding * rate *
                    Math.pow(1 + rate, remaining) /
                    (Math.pow(1 + rate, remaining) - 1);
        }

        return Math.round(emi);
    }

    // ANNUAL LOAN SIMULATION

    public void applyAnnualLoanPayments() {
        if (!hasActiveLoan()) return;

        for (int month = 0; month < 12; month++) {
            if (outstanding <= 0) break;

            long interest = Math.round(
                    outstanding * interestRate / 1200.0);

            outstanding += interest;
            totalInterestPaid += interest;

            long emi = calculateEMI();

            if (cash >= emi) {
                updateCash(cash - emi);

                long principalPaid = Math.max(0, emi - interest);
                outstanding = Math.max(0, outstanding - principalPaid);

                consecutiveMissed = 0;
                creditScore = Math.min(900, creditScore + 2);
                monthsPaid++;

                addHistory("EMI paid: " + money(emi));

            } else {
                missedPayments++;
                consecutiveMissed++;

                long penalty = Math.max(100,
                        Math.round(emi * 0.02));

                outstanding += penalty;
                lateFees += penalty;
                creditScore = Math.max(300, creditScore - 25);

                addHistory("EMI missed. Penalty: " + money(penalty));

                if (isSecuredLoan()
                        && consecutiveMissed >= 6
                        && !repossessed) {
                    repossessed = true;
                    addHistory(
                            "Secured asset repossessed after missed payments");
                }
            }

            if (monthsPaid >= tenureMonths && outstanding > 0) {
                tenureMonths = monthsPaid + 1;
            }
        }

        if (outstanding <= 0) finishLoan();
        saveData();
    }

    private boolean isSecuredLoan() {
        return loanType.equals("Home Loan")
                || loanType.equals("Mortgage Loan")
                || loanType.equals("Vehicle Loan")
                || loanType.equals("Gold Loan");
    }

    // EXTRA REPAYMENT

    private void extraRepayment() {
        if (!hasActiveLoan()) {
            toast("No active loan");
            return;
        }

        EditText input = new EditText(activity);
        input.setHint("Enter extra repayment amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Extra Repayment")
                .setMessage("Outstanding: " + money(outstanding)
                        + "\nAvailable cash: " + money(cash))
                .setView(input)
                .setPositiveButton("Pay", (dialog, which) -> {
                    long amount = readAmount(input);

                    if (amount <= 0 || amount > cash) {
                        toast("Invalid repayment amount");
                        return;
                    }

                    if (amount > outstanding) amount = outstanding;

                    updateCash(cash - amount);
                    outstanding -= amount;

                    addHistory("Extra repayment: " + money(amount));

                    if (outstanding <= 0) finishLoan();

                    saveData();
                    toast("Extra repayment successful");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // FORECLOSURE

    private void closeLoan() {
        if (!hasActiveLoan()) return;

        final long foreclosureAmount = outstanding;

        new AlertDialog.Builder(activity)
                .setTitle("Close Loan Early")
                .setMessage("Pay " + money(foreclosureAmount)
                        + " to close your remaining loan?"
                        + "\nAvailable cash: " + money(cash))
                .setPositiveButton("Pay & Close", (dialog, which) -> {
                    if (cash < foreclosureAmount) {
                        toast("Not enough cash to close loan");
                        return;
                    }

                    updateCash(cash - foreclosureAmount);
                    outstanding = 0;

                    finishLoan();
                    saveData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void finishLoan() {
        addHistory("Loan fully repaid: " + loanType);

        loanType = "";
        principal = 0;
        outstanding = 0;
        interestRate = 0;
        tenureMonths = 0;
        monthsPaid = 0;
        missedPayments = 0;
        consecutiveMissed = 0;
        repossessed = false;

        creditScore = Math.min(900, creditScore + 20);

        saveData();
        toast("Loan closed successfully");
    }

    private long readAmount(EditText input) {
        try {
            return Long.parseLong(input.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // CREDIT REPORT

    private void showCreditReport() {
        String status;

        if (creditScore >= 800) {
            status = "Excellent";
        } else if (creditScore >= 750) {
            status = "Very Good";
        } else if (creditScore >= 700) {
            status = "Good";
        } else if (creditScore >= 650) {
            status = "Fair";
        } else {
            status = "Poor";
        }

        String report =
                "Credit Score: " + creditScore + " / 900"
                + "\nRating: " + status
                + "\nTotal missed EMIs: " + missedPayments
                + "\nConsecutive missed EMIs: " + consecutiveMissed
                + "\nTotal late fees: " + money(lateFees)
                + "\nTotal interest paid: " + money(totalInterestPaid)
                + "\n\nPaying EMIs on time improves your score."
                + "\nMissed payments reduce your score.";

        new AlertDialog.Builder(activity)
                .setTitle("Credit Report")
                .setMessage(report)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showLoanInformation() {
        new AlertDialog.Builder(activity)
                .setTitle("Loan Rules")
                .setMessage(
                        "• Loan processing fee: 2%"
                        + "\n• GST on processing fee: 18%"
                        + "\n• Credit score range: 300–900"
                        + "\n• Higher scores unlock lower interest"
                        + "\n• Missed EMI reduces credit score"
                        + "\n• Late payment penalty: 2% of EMI"
                        + "\n• Secured assets may be repossessed"
                        + "\n• Extra repayments reduce outstanding debt"
                        + "\n\nThis is a simplified game simulation.")
                .setPositiveButton("Close", null)
                .show();
    }

    // TRANSACTION HISTORY

    private void addHistory(String message) {
        String old = prefs.getString("loan_history", "");
        String updated = message + "\n" + old;

        String[] lines = updated.split("\n");
        StringBuilder limited = new StringBuilder();

        for (int i = 0; i < Math.min(50, lines.length); i++) {
            limited.append(lines[i]).append("\n");
        }

        prefs.edit()
                .putString("loan_history", limited.toString())
                .apply();
    }
}
