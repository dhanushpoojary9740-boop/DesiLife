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

public class InsuranceManager {

    public interface MoneyListener {
        void onMoneyChanged(long newMoney);
    }

    private static class Policy {
        String type;
        long coverage;
        long premium;
        boolean active;

        Policy(String type, long coverage, long premium,
               boolean active) {
            this.type = type;
            this.coverage = coverage;
            this.premium = premium;
            this.active = active;
        }
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener listener;

    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private final ArrayList<Policy> policies = new ArrayList<>();
    private final ArrayList<String> history = new ArrayList<>();

    private long cash;
    private long totalPremiumPaid;
    private long totalClaimsReceived;

    private static final String PREF =
            "desilife_insurance_data";

    private LinearLayout content;

    public InsuranceManager(Activity activity,
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

        totalPremiumPaid =
                prefs.getLong("total_premium", 0);

        totalClaimsReceived =
                prefs.getLong("total_claims", 0);

        int count = prefs.getInt("policy_count", 0);

        for (int i = 0; i < count; i++) {

            String type = prefs.getString(
                    "policy_type_" + i, "");

            long coverage = prefs.getLong(
                    "policy_coverage_" + i, 0);

            long premium = prefs.getLong(
                    "policy_premium_" + i, 0);

            boolean active = prefs.getBoolean(
                    "policy_active_" + i, false);

            if (!type.isEmpty()) {
                policies.add(new Policy(
                        type, coverage, premium, active));
            }
        }

        int historyCount =
                prefs.getInt("history_count", 0);

        for (int i = 0; i < historyCount; i++) {
            String item = prefs.getString(
                    "history_" + i, null);

            if (item != null) history.add(item);
        }
    }

    private void saveData() {

        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("total_premium", totalPremiumPaid);
        editor.putLong("total_claims", totalClaimsReceived);

        editor.putInt("policy_count", policies.size());

        for (int i = 0; i < policies.size(); i++) {

            Policy p = policies.get(i);

            editor.putString("policy_type_" + i, p.type);
            editor.putLong("policy_coverage_" + i, p.coverage);
            editor.putLong("policy_premium_" + i, p.premium);
            editor.putBoolean("policy_active_" + i, p.active);
        }

        editor.putInt("history_count", history.size());

        for (int i = 0; i < history.size(); i++) {
            editor.putString("history_" + i, history.get(i));
        }

        editor.apply();
    }

    // ---------------- BASIC METHODS ----------------

    private String money(long amount) {
        return currency.format(amount);
    }

    private void updateCash(long amount) {
        cash = amount;

        if (listener != null) {
            listener.onMoneyChanged(cash);
        }
    }

    public void setCash(long amount) {
        cash = amount;
    }

    public long getCash() {
        return cash;
    }

    public long getTotalPremiumPaid() {
        return totalPremiumPaid;
    }

    public long getTotalClaimsReceived() {
        return totalClaimsReceived;
    }

    public void addHistory(String message) {

        history.add(0, message);

        while (history.size() > 50) {
            history.remove(history.size() - 1);
        }

        saveData();
    }

    private void toast(String message) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show();
    }

    // ---------------- UI HELPERS ----------------

    private TextView text(String value, int size,
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

        GradientDrawable drawable = new GradientDrawable();
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

        content.addView(text("INSURANCE", 25, true));

        content.addView(text(
                "Protect your health, family, vehicle and property",
                14, false));

        addSpace(18);

        addCard("Available Cash", money(cash));
        addCard("Active Policies", getActivePolicyCount() + "");
        addCard("Total Premium Paid", money(totalPremiumPaid));
        addCard("Total Claims Received",
                money(totalClaimsReceived));

        addSpace(12);

        addButton("Explore Insurance Policies",
                v -> showInsuranceTypes());

        addButton("My Insurance Policies",
                v -> showPolicies());

        addButton("Make an Insurance Claim",
                v -> showClaimPolicies());

        addButton("Insurance History",
                v -> showHistory());

        new AlertDialog.Builder(activity)
                .setTitle("Insurance")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    private int getActivePolicyCount() {

        int count = 0;

        for (Policy p : policies) {
            if (p.active) count++;
        }

        return count;
    }

    // ---------------- POLICY TYPES ----------------

    private void showInsuranceTypes() {

        String[] types = {
                "Health Insurance",
                "Life Insurance",
                "Vehicle Insurance",
                "Home Insurance"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Insurance")
                .setItems(types, (dialog, which) -> {

                    showCoverageOptions(types[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- COVERAGE OPTIONS ----------------

    private void showCoverageOptions(String type) {

        String[] options = {
                "₹2,00,000 Coverage",
                "₹5,00,000 Coverage",
                "₹10,00,000 Coverage",
                "₹25,00,000 Coverage",
                "₹50,00,000 Coverage"
        };

        long[] coverage = {
                200000, 500000, 1000000,
                2500000, 5000000
        };

        new AlertDialog.Builder(activity)
                .setTitle(type + " Coverage")
                .setItems(options, (dialog, which) -> {

                    long amount = coverage[which];

                    long premium = calculatePremium(
                            type, amount);

                    confirmPolicy(type, amount, premium);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- PREMIUM CALCULATION ----------------

    private long calculatePremium(String type,
                                  long coverage) {

        double rate;

        switch (type) {
            case "Health Insurance":
                rate = 0.025;
                break;

            case "Life Insurance":
                rate = 0.012;
                break;

            case "Vehicle Insurance":
                rate = 0.035;
                break;

            case "Home Insurance":
                rate = 0.008;
                break;

            default:
                rate = 0.02;
        }

        return Math.max(500,
                Math.round(coverage * rate));
    }

    // ---------------- BUY POLICY ----------------

    private void confirmPolicy(String type,
                               long coverage,
                               long premium) {

        for (Policy p : policies) {

            if (p.type.equals(type) && p.active) {
                toast("You already have active " + type);
                return;
            }
        }

        String message =
                "Insurance: " + type
                + "\nCoverage: " + money(coverage)
                + "\nAnnual Premium: " + money(premium)
                + "\n\nBuy this policy?";

        new AlertDialog.Builder(activity)
                .setTitle("Insurance Offer")
                .setMessage(message)
                .setPositiveButton("Buy Policy",
                        (dialog, which) -> {

                    if (cash < premium) {
                        toast("Not enough cash to pay premium");
                        return;
                    }

                    updateCash(cash - premium);

                    policies.add(new Policy(
                            type, coverage, premium, true));

                    totalPremiumPaid += premium;

                    addHistory("Purchased " + type
                            + ", coverage " + money(coverage)
                            + ", premium " + money(premium));

                    saveData();

                    toast(type + " activated");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- VIEW POLICIES ----------------

    private void showPolicies() {

        if (policies.isEmpty()) {
            toast("You have no insurance policies");
            return;
        }

        String[] names = new String[policies.size()];

        for (int i = 0; i < policies.size(); i++) {

            Policy p = policies.get(i);

            names[i] = p.type
                    + (p.active ? " - Active" : " - Expired");
        }

        new AlertDialog.Builder(activity)
                .setTitle("My Insurance Policies")
                .setItems(names, (dialog, which) -> {

                    showPolicyDetails(which);
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showPolicyDetails(int index) {

        if (index < 0 || index >= policies.size()) return;

        Policy p = policies.get(index);

        String message =
                "Policy: " + p.type
                + "\nCoverage: " + money(p.coverage)
                + "\nAnnual Premium: " + money(p.premium)
                + "\nStatus: "
                + (p.active ? "Active" : "Inactive");

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);

        builder.setTitle("Policy Details");
        builder.setMessage(message);
        builder.setPositiveButton("Close", null);

        if (p.active) {

            builder.setNeutralButton("Cancel Policy",
                    (dialog, which) -> {

                p.active = false;
                addHistory("Cancelled " + p.type);
                saveData();

                toast("Policy cancelled");
            });
        }

        builder.show();
    }

    // ---------------- INSURANCE CLAIMS ----------------

    private void showClaimPolicies() {

        ArrayList<Policy> active = new ArrayList<>();

        for (Policy p : policies) {
            if (p.active) active.add(p);
        }

        if (active.isEmpty()) {
            toast("You have no active policies");
            return;
        }

        String[] names = new String[active.size()];

        for (int i = 0; i < active.size(); i++) {
            names[i] = active.get(i).type
                    + " - " + money(active.get(i).coverage);
        }

        new AlertDialog.Builder(activity)
                .setTitle("Select Policy for Claim")
                .setItems(names, (dialog, which) -> {

                    showClaimAmountDialog(active.get(which));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClaimAmountDialog(Policy policy) {

        EditText input = new EditText(activity);
        input.setHint("Enter claim amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle(policy.type + " Claim")
                .setMessage("Maximum coverage: "
                        + money(policy.coverage))
                .setView(input)
                .setPositiveButton("Submit Claim",
                        (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount <= 0
                            || amount > policy.coverage) {

                        toast("Enter a valid claim amount");
                        return;
                    }

                    processClaim(policy, amount);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- CLAIM PROCESSING ----------------

    private void processClaim(Policy policy,
                              long amount) {

        // Simplified game claim approval system.
        // A claim has an 80% chance of approval.

        boolean approved =
                Math.random() < 0.80;

        if (approved) {

            updateCash(cash + amount);

            totalClaimsReceived += amount;

            addHistory("Claim approved: "
                    + policy.type + ", received "
                    + money(amount));

            saveData();

            new AlertDialog.Builder(activity)
                    .setTitle("Claim Approved")
                    .setMessage("Claim amount received: "
                            + money(amount))
                    .setPositiveButton("OK", null)
                    .show();

        } else {

            addHistory("Claim rejected: "
                    + policy.type + ", amount "
                    + money(amount));

            saveData();

            new AlertDialog.Builder(activity)
                    .setTitle("Claim Rejected")
                    .setMessage(
                            "Your insurance claim was rejected."
                            + "\nNo money was credited."
                            + "\nReview the policy conditions.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    // ---------------- ANNUAL PREMIUM PROCESSING ----------------

    public void applyAnnualPremiums() {

        for (Policy p : policies) {

            if (!p.active) continue;

            if (cash >= p.premium) {

                updateCash(cash - p.premium);

                totalPremiumPaid += p.premium;

                addHistory("Annual premium paid: "
                        + p.type + " - " + money(p.premium));

            } else {

                p.active = false;

                addHistory("Policy expired due to unpaid premium: "
                        + p.type);
            }
        }

        saveData();
    }

    // ---------------- HISTORY ----------------

    private void showHistory() {

        if (history.isEmpty()) {
            toast("No insurance history available");
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
                .setTitle("Insurance History")
                .setView(scroll)
                .setPositiveButton("Close", null)
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
}
