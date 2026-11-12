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
import java.util.ArrayList;
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
    private long principal;
    private long outstanding;
    private long scheduledEmi;

    private double interestRate;

    private int tenureMonths;
    private int monthsPaid;
    private int missedPayments;
    private int consecutiveMissed;
    private int creditScore = 700;

    private long lateFees;
    private long totalInterestPaid;

    private boolean repossessed;

    private LinearLayout content;

    private static final String PREF = "desilife_loan_data";

    public LoanManager(Activity activity, long currentCash,
                       MoneyListener listener) {

        this.activity = activity;
        this.cash = currentCash;
        this.listener = listener;

        prefs = activity.getSharedPreferences(
                PREF, Activity.MODE_PRIVATE);

        loadData();
        migrateOldLoan();
    }

    private void loadData() {

        loanType = prefs.getString("loan_type", "");
        principal = prefs.getLong("principal", 0);
        outstanding = prefs.getLong("outstanding", 0);
        scheduledEmi = prefs.getLong("scheduled_emi", 0);

        interestRate = Double.longBitsToDouble(
                prefs.getLong("rate",
                        Double.doubleToLongBits(0)));

        tenureMonths = prefs.getInt("tenure", 0);
        monthsPaid = prefs.getInt("months_paid", 0);
        missedPayments = prefs.getInt("missed", 0);

        consecutiveMissed =
                prefs.getInt("consecutive_missed", 0);

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
                .putLong("scheduled_emi", scheduledEmi)
                .putLong("rate",
                        Double.doubleToLongBits(interestRate))
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

    private void migrateOldLoan() {

        if (outstanding <= 0 || loanType.isEmpty()) return;

        if (scheduledEmi <= 0) {

            scheduledEmi = calculateInstallment(
                    outstanding, interestRate, 60);

            tenureMonths = Math.max(
                    tenureMonths, monthsPaid + 60);

            addHistory("Existing loan repayment schedule updated.");

            saveData();
        }
    }

    // BASIC HELPERS

    private String money(long amount) {
        return currency.format(amount);
    }

    private long safeAdd(long first, long second) {

        if (second > 0 && first > Long.MAX_VALUE - second) {
            return Long.MAX_VALUE;
        }

        if (second < 0 && first < Long.MIN_VALUE - second) {
            return Long.MIN_VALUE;
        }

        return first + second;
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
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show();
    }

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

    private void addButton(String label,
                           View.OnClickListener action) {

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

        if (hasActiveLoan()) {

            addActiveLoanDetails();

            addButton("Make Extra Repayment",
                    v -> extraRepayment());

            addButton("Close Loan Early",
                    v -> closeLoan());

        } else {

            addCard("Loan Status", "No active loan");

            addButton("Explore Loan Offers",
                    v -> showLoanTypes());
        }

        addButton("View Credit Report",
                v -> showCreditReport());

        addButton("Loan Information",
                v -> showLoanInformation());

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
        addCard("Months Paid", String.valueOf(monthsPaid));
        addCard("Missed Payments", String.valueOf(missedPayments));
        addCard("Late Fees", money(lateFees));

        addCard("Total Interest Paid",
                money(totalInterestPaid));

        addCard("Fixed Monthly EMI", money(scheduledEmi));
        addCard("Remaining Principal", money(outstanding));

        if (repossessed) {
            addCard("Asset Status", "Repossessed");
        }
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
                            "Personal Loan",
                            "Home Loan",
                            "Mortgage Loan",
                            "Vehicle Loan",
                            "Education Loan",
                            "Gold Loan",
                            "Business Loan"
                    };

                    double[] rates = {
                            18, 8.5, 10, 11, 9, 12, 15
                    };

                    int[] maxTenure = {
                            60, 300, 180, 84, 120, 36, 120
                    };

                    loanType = names[which];
                    interestRate = rates[which];

                    chooseLoanAmount(maxTenure[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseLoanAmount(int maxTenure) {

        String[] amounts = {
                "₹50,000",
                "₹1,00,000",
                "₹2,50,000",
                "₹5,00,000",
                "₹10,00,000"
        };

        long[] values = {
                50000, 100000, 250000, 500000, 1000000
        };

        new AlertDialog.Builder(activity)
                .setTitle(loanType + " Amount")
                .setItems(amounts, (dialog, which) ->
                        chooseTenure(values[which], maxTenure))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseTenure(long amount, int maxTenure) {

        int[] choices = {
                12, 24, 36, 60, 84, 120, 180, 240, 300
        };

        ArrayList<Integer> valid = new ArrayList<>();

        for (int value : choices) {
            if (value <= maxTenure) {
                valid.add(value);
            }
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

    // EMI CALCULATION

    private long calculateInstallment(
            long amount, double annualRate, int months) {

        if (amount <= 0 || months <= 0) return 0;

        double rate = annualRate / 1200.0;

        if (rate <= 0) {
            return (long) Math.ceil(amount / (double) months);
        }

        double factor = Math.pow(1 + rate, months);

        double emi = amount * rate * factor / (factor - 1);

        return (long) Math.ceil(emi);
    }

    // LOAN APPROVAL

    private void confirmLoan(long amount, int months) {

        double rate = interestRate;

        if (creditScore >= 800) {
            rate -= 1.0;
        } else if (creditScore >= 750) {
            rate -= 0.5;
        } else if (creditScore < 600) {
            rate += 2.0;
        }

        final double finalRate = rate;

        long processingFee = Math.round(amount * 0.02);
        long gst = Math.round(processingFee * 0.18);

        long disbursed = amount - processingFee - gst;

        final long emi = calculateInstallment(
                amount, finalRate, months);

        String message =
                "Loan: " + loanType
                + "\nPrincipal: " + money(amount)
                + "\nInterest: " + finalRate + "%"
                + "\nTenure: " + months + " months"
                + "\nFixed EMI: " + money(emi)
                + "\nProcessing fee: " + money(processingFee)
                + "\nGST: " + money(gst)
                + "\nAmount received: " + money(disbursed)
                + "\n\nApprove this loan?";

        new AlertDialog.Builder(activity)
                .setTitle("Loan Offer")
                .setMessage(message)
                .setPositiveButton("Accept", (dialog, which) -> {

                    principal = amount;
                    outstanding = amount;

                    interestRate = finalRate;

                    tenureMonths = months;
                    scheduledEmi = emi;

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

    public long calculateEMI() {
        return scheduledEmi;
    }

    // ONE MONTH OF AUTOMATIC EMI

    public void applyMonthlyLoanPayment() {

        if (!hasActiveLoan()) return;

        if (scheduledEmi <= 0) {
            migrateOldLoan();
        }

        if (scheduledEmi <= 0 || outstanding <= 0) return;

        long interest = Math.round(
                outstanding * interestRate / 1200.0);

        long amountDue = safeAdd(outstanding, interest);

        long payment = Math.min(scheduledEmi, amountDue);

        if (cash >= payment && payment > 0) {

            updateCash(cash - payment);

            long interestCovered = Math.min(payment, interest);

            totalInterestPaid = safeAdd(
                    totalInterestPaid, interestCovered);

            outstanding = Math.max(0, amountDue - payment);

            consecutiveMissed = 0;
            monthsPaid++;

            creditScore = Math.min(900, creditScore + 2);

            addHistory("Monthly EMI debited: "
                    + money(payment)
                    + " | Interest: " + money(interestCovered));

            if (outstanding <= 0) {
                finishLoan();
                saveData();
                return;
            }

            if (monthsPaid >= tenureMonths) {

                tenureMonths += 12;

                scheduledEmi = calculateInstallment(
                        outstanding, interestRate, 12);

                addHistory(
                        "Repayment schedule extended by 12 months.");
            }

        } else {

            outstanding = amountDue;

            missedPayments++;
            consecutiveMissed++;

            long penalty = Math.max(
                    100,
                    Math.min(5000,
                            Math.round(scheduledEmi * 0.02)));

            outstanding = safeAdd(outstanding, penalty);
            lateFees = safeAdd(lateFees, penalty);

            creditScore = Math.max(300, creditScore - 25);

            addHistory("EMI missed: "
                    + money(scheduledEmi)
                    + " | Interest: " + money(interest)
                    + " | Late fee: " + money(penalty));

            if (isSecuredLoan()
                    && consecutiveMissed >= 6
                    && !repossessed) {

                repossessed = true;

                addHistory(
                        "Secured asset repossessed after six missed payments.");
            }
        }

        saveData();
    }

    // ANNUAL EMI COMPATIBILITY

    public void applyAnnualLoanPayments() {

        if (!hasActiveLoan()) return;

        int successfulPayments = 0;
        int failedPayments = 0;

        for (int month = 0; month < 12; month++) {

            if (!hasActiveLoan()) break;

            int oldPaid = monthsPaid;
            int oldMissed = missedPayments;

            applyMonthlyLoanPayment();

            if (monthsPaid > oldPaid) {
                successfulPayments++;
            }

            if (missedPayments > oldMissed) {
                failedPayments++;
            }

            if (!hasActiveLoan()) break;
        }

        if (successfulPayments > 0) {
            addHistory(successfulPayments
                    + " EMI payments automatically debited.");
        }

        if (failedPayments > 0) {
            addHistory(failedPayments
                    + " EMI payments missed due to insufficient cash.");
        }

        saveData();

        if (successfulPayments > 0) {
            toast(successfulPayments
                    + " EMI payments automatically debited.");
        }
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

        input.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER);

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

                    amount = Math.min(amount, outstanding);

                    updateCash(cash - amount);

                    outstanding -= amount;

                    addHistory("Extra repayment: " + money(amount));

                    if (outstanding <= 0) {
                        finishLoan();
                    }

                    saveData();

                    toast("Extra repayment successful");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // EARLY CLOSURE

    private void closeLoan() {

        if (!hasActiveLoan()) return;

        long amount = outstanding;

        new AlertDialog.Builder(activity)
                .setTitle("Close Loan Early")
                .setMessage("Pay " + money(amount)
                        + " to close your remaining loan?"
                        + "\nAvailable cash: " + money(cash))
                .setPositiveButton("Pay & Close", (dialog, which) -> {

                    if (cash < amount) {
                        toast("Not enough cash");
                        return;
                    }

                    updateCash(cash - amount);

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
        scheduledEmi = 0;

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
            return Long.parseLong(
                    input.getText().toString().trim());
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

    // LOAN INFORMATION

    private void showLoanInformation() {

        new AlertDialog.Builder(activity)
                .setTitle("Loan Rules")
                .setMessage(
                        "Personal Loan: 18%"
                        + "\nHome Loan: 8.5%"
                        + "\nMortgage Loan: 10%"
                        + "\nVehicle Loan: 11%"
                        + "\nEducation Loan: 9%"
                        + "\nGold Loan: 12%"
                        + "\nBusiness Loan: 15%"
                        + "\n\nProcessing fee: 2%"
                        + "\nGST on processing fee: 18%"
                        + "\nLate fee: 2% of EMI, capped at ₹5,000"
                        + "\n\nEMI remains fixed during repayment."
                        + "\nMissed payments extend repayment."
                        + "\nSecured assets can be repossessed."
                        + "\nExtra repayments reduce debt."
                        + "\n\nSimplified game simulation.")
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
