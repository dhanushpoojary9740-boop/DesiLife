
package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class CreditScoreManager {

    public interface CreditCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final CreditCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;

    private int creditScore;
    private int missedPayments;
    private int successfulPayments;
    private int defaults;
    private int enquiries;
    private int settlements;
    private int creditLimit;
    private int creditUsed;
    private int creditAge;
    private int loansClosed;
    private int onTimeStreak;

    private final ArrayList<String> history = new ArrayList<>();

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 190, 185);

    public CreditScoreManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            CreditCallback callback
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

        creditScore = save.getInt("credit_score", 650);
        missedPayments = save.getInt("credit_missed", 0);
        successfulPayments = save.getInt("credit_successful", 0);
        defaults = save.getInt("credit_defaults", 0);
        enquiries = save.getInt("credit_enquiries", 0);
        settlements = save.getInt("credit_settlements", 0);

        creditLimit = save.getInt("credit_limit", 0);
        creditUsed = save.getInt("credit_used", 0);

        creditAge = save.getInt("credit_age", 0);
        loansClosed = save.getInt("credit_loans_closed", 0);
        onTimeStreak = save.getInt("credit_streak", 0);

        int count = save.getInt("credit_history_count", 0);

        for (int i = 0; i < count; i++) {
            String item = save.getString(
                    "credit_history_" + i, null);

            if (item != null) {
                history.add(item);
            }
        }

        normalizeScore();
    }

    // ---------------- SAVE DATA ----------------

    private void saveData() {

        SharedPreferences.Editor editor = save.edit();

        editor.putInt("credit_score", creditScore);
        editor.putInt("credit_missed", missedPayments);
        editor.putInt("credit_successful", successfulPayments);
        editor.putInt("credit_defaults", defaults);
        editor.putInt("credit_enquiries", enquiries);
        editor.putInt("credit_settlements", settlements);

        editor.putInt("credit_limit", creditLimit);
        editor.putInt("credit_used", creditUsed);

        editor.putInt("credit_age", creditAge);
        editor.putInt("credit_loans_closed", loansClosed);
        editor.putInt("credit_streak", onTimeStreak);

        editor.putInt("credit_history_count", history.size());

        for (int i = 0; i < history.size(); i++) {
            editor.putString(
                    "credit_history_" + i,
                    history.get(i)
            );
        }

        editor.apply();
    }

    // ---------------- HELPERS ----------------

    private void normalizeScore() {
        creditScore = Math.max(300, Math.min(900, creditScore));
    }

    private void changeScore(int amount) {
        creditScore += amount;
        normalizeScore();
    }

    private String rating() {

        if (creditScore >= 800) return "Excellent";
        if (creditScore >= 750) return "Very Good";
        if (creditScore >= 700) return "Good";
        if (creditScore >= 650) return "Fair";
        if (creditScore >= 550) return "Poor";

        return "Very Poor";
    }

    private String utilizationRating() {

        if (creditLimit <= 0) return "No Credit Card";

        int percentage = (int) (
                creditUsed * 100L / creditLimit
        );

        if (percentage <= 10) return "Excellent";
        if (percentage <= 30) return "Good";
        if (percentage <= 50) return "Moderate";

        return "High";
    }

    private void addHistory(String description) {

        history.add(0, description);

        while (history.size() > 50) {
            history.remove(history.size() - 1);
        }

        saveData();
    }

    private void toast(String message) {
        Toast.makeText(
                activity,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    public int getCreditScore() {
        return creditScore;
    }

    public String getCreditRating() {
        return rating();
    }

    // ---------------- DASHBOARD ----------------

    public void open() {
        showDashboard();
    }

    private void showDashboard() {

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 25, 24, 30);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);

        TextView title = new TextView(activity);
        title.setText("📊 CREDIT SCORE SYSTEM");
        title.setTextColor(gold);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);

        root.addView(title);

        addInfo(root, "Your Credit Score",
                creditScore + " / 900");

        addInfo(root, "Credit Rating", rating());

        addInfo(root, "Payment History",
                successfulPayments + " successful / "
                        + missedPayments + " missed");

        addInfo(root, "On-Time Payment Streak",
                onTimeStreak + " payments");

        addInfo(root, "Credit Card Limit",
                "₹" + creditLimit);

        addInfo(root, "Credit Card Used",
                "₹" + creditUsed);

        addInfo(root, "Credit Utilisation",
                utilizationRating());

        addInfo(root, "Credit Enquiries",
                String.valueOf(enquiries));

        addInfo(root, "Loan Defaults",
                String.valueOf(defaults));

        addDivider(root);

        addButton(root, "💳 Manage Credit Card",
                this::manageCreditCard);

        addButton(root, "✅ Record EMI Payment",
                this::recordPayment);

        addButton(root, "❌ Record Missed Payment",
                this::recordMissedPayment);

        addButton(root, "🏦 Apply for Credit Enquiry",
                this::applyCreditEnquiry);

        addButton(root, "📉 Record Loan Default",
                this::recordDefault);

        addButton(root, "🤝 Settle Outstanding Loan",
                this::settleLoan);

        addButton(root, "🏆 Close a Loan Successfully",
                this::closeLoan);

        addButton(root, "📈 Improve Credit Score",
                this::improveScore);

        addButton(root, "🔍 Check Loan Eligibility",
                this::checkEligibility);

        addButton(root, "📄 View Credit Report",
                this::showCreditReport);

        addButton(root, "📜 Credit History",
                this::showHistory);

        addButton(root, "⬅ Back to Game",
                activity::recreate);

        activity.setContentView(scroll);
    }

    // ---------------- UI ----------------

    private void addInfo(
            LinearLayout root,
            String label,
            String value
    ) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 12, 16, 12);
        card.setBackground(background(
                Color.rgb(23, 39, 35), 14));

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
        divider.setBackgroundColor(
                Color.rgb(65, 75, 68));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 1);

        params.setMargins(0, 18, 0, 12);

        root.addView(divider, params);
    }

    private GradientDrawable background(
            int color,
            int radius
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        return drawable;
    }

    // ---------------- CREDIT CARD ----------------

    private void manageCreditCard() {

        String[] options = {
                "Apply for Credit Card",
                "Use Credit Card",
                "Pay Credit Card Bill",
                "View Credit Card Details"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Credit Card Management")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        applyCreditCard();
                    } else if (which == 1) {
                        useCreditCard();
                    } else if (which == 2) {
                        payCreditCardBill();
                    } else {
                        showCreditCardDetails();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyCreditCard() {

        if (creditLimit > 0) {
            toast("You already have a credit card");
            return;
        }

        if (age < 18) {
            toast("You must be at least 18 years old");
            return;
        }

        int limit = 10000;

        if (creditScore >= 800) {
            limit = 200000;
        } else if (creditScore >= 750) {
            limit = 150000;
        } else if (creditScore >= 700) {
            limit = 100000;
        } else if (creditScore >= 650) {
            limit = 50000;
        } else if (creditScore >= 600) {
            limit = 25000;
        }

        int finalLimit = limit;

        new AlertDialog.Builder(activity)
                .setTitle("Credit Card Approved")
                .setMessage(
                        "Your simulated credit card limit is ₹"
                                + finalLimit
                                + "\n\nApply for this card?"
                )
                .setPositiveButton("Apply", (dialog, which) -> {

                    creditLimit = finalLimit;
                    enquiries++;

                    changeScore(-3);

                    addHistory(
                            "Credit card approved with limit ₹"
                                    + finalLimit
                    );

                    notifyUpdate(
                            "You received a credit card with a limit of ₹"
                                    + finalLimit + "."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void useCreditCard() {

        if (creditLimit <= 0) {
            toast("Apply for a credit card first");
            return;
        }

        EditText input = new EditText(activity);
        input.setHint("Enter purchase amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Credit Card Purchase")
                .setMessage(
                        "Available limit: ₹"
                                + (creditLimit - creditUsed)
                )
                .setView(input)
                .setPositiveButton("Purchase", (dialog, which) -> {

                    int amount = readAmount(input);

                    if (amount <= 0
                            || amount > creditLimit - creditUsed) {
                        toast("Invalid amount or insufficient limit");
                        return;
                    }

                    creditUsed += amount;

                    int utilization = creditUsed * 100 / creditLimit;

                    if (utilization > 50) {
                        changeScore(-5);
                    } else if (utilization <= 30) {
                        changeScore(2);
                    }

                    addHistory(
                            "Credit card purchase: ₹" + amount
                    );

                    notifyUpdate(
                            "Credit card purchase completed: ₹"
                                    + amount + "."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void payCreditCardBill() {

        if (creditUsed <= 0) {
            toast("No outstanding credit card bill");
            return;
        }

        int outstanding = creditUsed;

        new AlertDialog.Builder(activity)
                .setTitle("Pay Credit Card Bill")
                .setMessage(
                        "Outstanding bill: ₹" + outstanding
                                + "\nAvailable cash: ₹" + money
                )
                .setPositiveButton("Pay Full Bill", (dialog, which) -> {

                    if (money < outstanding) {
                        toast("Insufficient cash to pay bill");
                        return;
                    }

                    money -= outstanding;
                    creditUsed = 0;

                    successfulPayments++;
                    onTimeStreak++;

                    changeScore(15);

                    addHistory(
                            "Credit card bill paid: ₹" + outstanding
                    );

                    notifyUpdate(
                            "Credit card bill paid successfully. "
                                    + "Your credit score improved."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCreditCardDetails() {

        showMessage(
                "Credit Card Details",
                "Credit Limit: ₹" + creditLimit
                        + "\nUsed Amount: ₹" + creditUsed
                        + "\nAvailable Limit: ₹"
                        + Math.max(0, creditLimit - creditUsed)
                        + "\nUtilisation: "
                        + (creditLimit > 0
                        ? (creditUsed * 100 / creditLimit) + "%"
                        : "N/A")
        );
    }

    // ---------------- EMI PAYMENT ----------------

    private void recordPayment() {

        if (age < 18) {
            toast("You must be at least 18 years old");
            return;
        }

        String[] options = {
                "Regular EMI Paid",
                "Early Loan Repayment"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Record Loan Payment")
                .setItems(options, (dialog, which) -> {

                    successfulPayments++;
                    onTimeStreak++;

                    if (which == 0) {
                        changeScore(8);
                        addHistory("Regular EMI paid on time");
                    } else {
                        changeScore(12);
                        addHistory("Early loan repayment completed");
                    }

                    notifyUpdate(
                            "Payment recorded successfully. Your credit history improved."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- MISSED PAYMENT ----------------

    private void recordMissedPayment() {

        new AlertDialog.Builder(activity)
                .setTitle("Record Missed EMI")
                .setMessage(
                        "This will negatively affect your credit history and score."
                                + "\nContinue?"
                )
                .setPositiveButton("Confirm", (dialog, which) -> {

                    missedPayments++;
                    onTimeStreak = 0;

                    changeScore(-35);

                    addHistory("Loan EMI missed");

                    notifyUpdate(
                            "You missed an EMI payment. Your credit score decreased."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- CREDIT ENQUIRY ----------------

    private void applyCreditEnquiry() {

        String[] options = {
                "Personal Loan",
                "Home Loan",
                "Vehicle Loan",
                "Business Loan",
                "Education Loan"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Credit Enquiry")
                .setItems(options, (dialog, which) -> {

                    enquiries++;

                    changeScore(-2);

                    int probability = creditScore >= 750 ? 85 :
                            creditScore >= 700 ? 70 :
                            creditScore >= 650 ? 50 :
                            creditScore >= 600 ? 30 : 15;

                    boolean approved =
                            random.nextInt(100) < probability;

                    if (approved) {

                        addHistory(
                                options[which] + " enquiry: Eligible"
                        );

                        notifyUpdate(
                                "Your " + options[which]
                                        + " enquiry was successful. "
                                        + "You are eligible for a simulated loan offer."
                        );

                    } else {

                        addHistory(
                                options[which] + " enquiry: Rejected"
                        );

                        notifyUpdate(
                                "Your " + options[which]
                                        + " enquiry was unsuccessful. "
                                        + "Improve your credit score and try again."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- LOAN DEFAULT ----------------

    private void recordDefault() {

        new AlertDialog.Builder(activity)
                .setTitle("Loan Default")
                .setMessage(
                        "Recording a loan default will significantly reduce your credit score."
                                + "\nThis action cannot be reversed."
                                + "\n\nContinue?"
                )
                .setPositiveButton("Confirm Default", (dialog, which) -> {

                    defaults++;

                    onTimeStreak = 0;

                    changeScore(-100);

                    addHistory("Loan default recorded");

                    notifyUpdate(
                            "A loan default was recorded. Your credit score dropped significantly."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- LOAN SETTLEMENT ----------------

    private void settleLoan() {

        if (defaults <= settlements) {
            toast("No unsettled loan defaults");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Loan Settlement")
                .setMessage(
                        "Settle an outstanding defaulted loan."
                                + "\nThis can help rebuild your credit profile, "
                                + "but the settlement remains on your history."
                )
                .setPositiveButton("Settle Loan", (dialog, which) -> {

                    settlements++;

                    changeScore(10);

                    addHistory(
                            "Defaulted loan settled. Settlement recorded."
                    );

                    notifyUpdate(
                            "Loan settlement completed. Your credit profile has started recovering."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- CLOSE LOAN ----------------

    private void closeLoan() {

        new AlertDialog.Builder(activity)
                .setTitle("Close Loan Successfully")
                .setMessage(
                        "Record a loan that has been fully repaid."
                )
                .setPositiveButton("Confirm", (dialog, which) -> {

                    loansClosed++;

                    successfulPayments += 3;
                    onTimeStreak += 3;

                    changeScore(20);

                    addHistory("Loan successfully closed");

                    notifyUpdate(
                            "Your loan was closed successfully. "
                                    + "Your credit profile improved."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- IMPROVE SCORE ----------------

    private void improveScore() {

        String[] options = {
                "Pay Outstanding Credit Card Bill",
                "Maintain Low Credit Utilisation",
                "Avoid New Credit Enquiries",
                "Maintain a Long Credit History"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Credit Improvement Tips")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        payCreditCardBill();

                    } else if (which == 1) {

                        if (creditLimit <= 0) {
                            toast("You need a credit card first");
                            return;
                        }

                        if (creditUsed * 100L / creditLimit <= 30) {
                            changeScore(5);
                            addHistory("Maintained low credit utilisation");

                            notifyUpdate(
                                    "Your low credit utilisation was rewarded."
                            );
                        } else {
                            showMessage(
                                    "High Utilisation",
                                    "Reduce your credit card balance to below 30% of your limit."
                            );
                        }

                    } else if (which == 2) {

                        addHistory("Avoided unnecessary credit enquiries");

                        notifyUpdate(
                                "You decided to avoid unnecessary credit applications."
                        );

                    } else {

                        creditAge++;
                        changeScore(5);

                        addHistory("Credit history age increased");

                        notifyUpdate(
                                "Your credit history became older. "
                                        + "A longer responsible history can help your score."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- LOAN ELIGIBILITY ----------------

    private void checkEligibility() {

        String status;
        int maxLoan;

        if (creditScore >= 800) {
            status = "Excellent Eligibility";
            maxLoan = 5000000;
        } else if (creditScore >= 750) {
            status = "Very Good Eligibility";
            maxLoan = 3000000;
        } else if (creditScore >= 700) {
            status = "Good Eligibility";
            maxLoan = 1500000;
        } else if (creditScore >= 650) {
            status = "Moderate Eligibility";
            maxLoan = 500000;
        } else if (creditScore >= 600) {
            status = "Limited Eligibility";
            maxLoan = 200000;
        } else {
            status = "Low Eligibility";
            maxLoan = 0;
        }

        showMessage(
                "Loan Eligibility",
                "Credit Score: " + creditScore
                        + "\nRating: " + rating()
                        + "\nStatus: " + status
                        + "\nEstimated Maximum Loan: ₹" + maxLoan
                        + "\n\nActual lending decisions may also depend on income, employment and existing debts."
        );
    }

    // ---------------- CREDIT REPORT ----------------

    private void showCreditReport() {

        int utilization = creditLimit > 0
                ? creditUsed * 100 / creditLimit : 0;

        String report =
                "DESILIFE CREDIT REPORT"
                        + "\n\nCredit Score: " + creditScore + "/900"
                        + "\nRating: " + rating()
                        + "\n\nPAYMENT HISTORY"
                        + "\nSuccessful Payments: " + successfulPayments
                        + "\nMissed Payments: " + missedPayments
                        + "\nOn-Time Streak: " + onTimeStreak
                        + "\n\nCREDIT UTILISATION"
                        + "\nCredit Limit: ₹" + creditLimit
                        + "\nUsed: ₹" + creditUsed
                        + "\nUtilisation: " + utilization + "%"
                        + "\n\nCREDIT ENQUIRIES"
                        + "\nTotal Enquiries: " + enquiries
                        + "\n\nLOAN RECORD"
                        + "\nLoans Closed: " + loansClosed
                        + "\nDefaults: " + defaults
                        + "\nSettlements: " + settlements
                        + "\n\nCredit History Age: "
                        + creditAge + " years";

        showMessage("Credit Report", report);
    }

    // ---------------- CREDIT HISTORY ----------------

    private void showHistory() {

        if (history.isEmpty()) {
            showMessage(
                    "Credit History",
                    "No credit activity recorded yet."
            );
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
                .setTitle("Credit History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- ANNUAL UPDATE ----------------

    public void applyAnnualCreditUpdate() {

        creditAge++;

        if (onTimeStreak >= 6) {
            changeScore(10);
        }

        if (missedPayments > 0 && onTimeStreak >= 3) {
            changeScore(5);
        }

        if (creditLimit > 0) {

            int utilization = creditUsed * 100 / creditLimit;

            if (utilization > 70) {
                changeScore(-10);
            } else if (utilization <= 30) {
                changeScore(5);
            }
        }

        if (defaults > settlements) {
            changeScore(-5);
        }

        addHistory(
                "Annual credit review completed. Score: " + creditScore
        );

        saveData();
    }

    // ---------------- NOTIFICATIONS ----------------

    private void notifyUpdate(String description) {

        saveData();

        if (callback != null) {
            callback.onUpdate(
                    money,
                    health,
                    happiness,
                    description
            );
        }

        showDashboard();
    }

    private void showMessage(String title, String message) {

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private int readAmount(EditText input) {

        try {
            return Integer.parseInt(
                    input.getText().toString().trim()
            );
        } catch (Exception e) {
            return 0;
        }
    }
}
