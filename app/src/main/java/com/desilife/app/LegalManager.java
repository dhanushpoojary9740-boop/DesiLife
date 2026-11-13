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

import java.util.ArrayList;
import java.util.Random;

public class LegalManager {

    public interface LegalCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final LegalCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;

    private int casesFiled;
    private int casesWon;
    private int casesLost;
    private int legalFees;
    private int reputation;
    private int pendingCases;
    private int criminalCases;

    private final ArrayList<String> caseHistory = new ArrayList<>();

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 190, 185);

    public LegalManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            LegalCallback callback
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

    private void loadData() {
        casesFiled = save.getInt("legal_cases_filed", 0);
        casesWon = save.getInt("legal_cases_won", 0);
        casesLost = save.getInt("legal_cases_lost", 0);
        legalFees = save.getInt("legal_fees", 0);
        reputation = save.getInt("legal_reputation", 50);
        pendingCases = save.getInt("legal_pending_cases", 0);
        criminalCases = save.getInt("legal_criminal_cases", 0);

        int count = save.getInt("legal_history_count", 0);

        for (int i = 0; i < count; i++) {
            String item = save.getString("legal_history_" + i, null);
            if (item != null) {
                caseHistory.add(item);
            }
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = save.edit();

        editor.putInt("legal_cases_filed", casesFiled);
        editor.putInt("legal_cases_won", casesWon);
        editor.putInt("legal_cases_lost", casesLost);
        editor.putInt("legal_fees", legalFees);
        editor.putInt("legal_reputation", reputation);
        editor.putInt("legal_pending_cases", pendingCases);
        editor.putInt("legal_criminal_cases", criminalCases);

        editor.putInt("legal_history_count", caseHistory.size());

        for (int i = 0; i < caseHistory.size(); i++) {
            editor.putString("legal_history_" + i, caseHistory.get(i));
        }

        editor.apply();
    }

    public void open() {
        showDashboard();
    }

    private void showDashboard() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 26);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);

        TextView title = new TextView(activity);
        title.setText("⚖️ COURT & LEGAL SYSTEM");
        title.setTextColor(gold);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);
        root.addView(title);

        addInfo(root, "Cases Filed", String.valueOf(casesFiled));
        addInfo(root, "Cases Won", String.valueOf(casesWon));
        addInfo(root, "Cases Lost", String.valueOf(casesLost));
        addInfo(root, "Pending Cases", String.valueOf(pendingCases));
        addInfo(root, "Criminal Cases", String.valueOf(criminalCases));
        addInfo(root, "Legal Expenses", "₹" + legalFees);
        addInfo(root, "Legal Reputation", reputation + "/100");

        addDivider(root);

        addButton(root, "📄 File a Civil Lawsuit", this::fileCivilCase);
        addButton(root, "🏠 Property Dispute", this::propertyDispute);
        addButton(root, "📝 Contract Dispute", this::contractDispute);
        addButton(root, "👨‍⚖️ Hire a Lawyer", this::hireLawyer);
        addButton(root, "🏛️ Attend Court Hearing", this::attendHearing);
        addButton(root, "⚖️ View Case History", this::showHistory);
        addButton(root, "📊 Legal Career Statistics", this::showStatistics);
        addButton(root, "⬅ Back to Game", activity::recreate);

        activity.setContentView(scroll);
    }

    private void addInfo(LinearLayout root, String label, String value) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 12, 16, 12);
        card.setBackground(background(Color.rgb(28, 58, 50), 14));

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
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 4, 0, 4);
        root.addView(card, params);
    }

    private void addButton(
            LinearLayout root,
            String text,
            Runnable action
    ) {
        TextView button = new TextView(activity);
        button.setText(text);
        button.setTextColor(dark);
        button.setTextSize(16);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(14, 17, 14, 17);
        button.setBackground(background(Color.rgb(239, 202, 126), 18));
        button.setElevation(3);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 7, 0, 7);

        root.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    private void addDivider(LinearLayout root) {
        TextView divider = new TextView(activity);
        divider.setBackgroundColor(Color.rgb(76, 105, 89));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        params.setMargins(0, 18, 0, 12);
        root.addView(divider, params);
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private void fileCivilCase() {
        String[] types = {
                "Defamation",
                "Personal Injury",
                "Consumer Complaint",
                "Financial Dispute",
                "Employment Dispute"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Civil Case")
                .setItems(types, (dialog, which) -> {
                    startCase(types[which], 15000 + random.nextInt(35001), false);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void propertyDispute() {
        startCase(
                "Property Dispute",
                25000 + random.nextInt(50001),
                false
        );
    }

    private void contractDispute() {
        startCase(
                "Contract Dispute",
                20000 + random.nextInt(40001),
                false
        );
    }

    private void startCase(String type, int filingCost, boolean criminal) {
        if (age < 18) {
            showMessage("Not Eligible",
                    "You must be at least 18 years old to file a legal case.");
            return;
        }

        if (money < filingCost) {
            showMessage("Insufficient Money",
                    "You need ₹" + filingCost + " to file this case.");
            return;
        }

        money -= filingCost;
        legalFees += filingCost;
        casesFiled++;
        pendingCases++;

        if (criminal) {
            criminalCases++;
        }

        caseHistory.add("Filed: " + type + " | Filing fee ₹" + filingCost);

        update("You filed a " + type +
                ". Your case has been registered. Filing expenses: ₹" +
                filingCost + ".");
    }

    private void hireLawyer() {
        if (pendingCases <= 0) {
            showMessage("No Pending Cases",
                    "You do not have any pending cases requiring a lawyer.");
            return;
        }

        String[] lawyers = {
                "Junior Lawyer - ₹10,000",
                "Experienced Lawyer - ₹30,000",
                "Senior Advocate - ₹75,000",
                "Top Advocate - ₹1,50,000"
        };

        int[] costs = {10000, 30000, 75000, 150000};
        int[] bonuses = {5, 12, 20, 30};

        new AlertDialog.Builder(activity)
                .setTitle("Hire a Lawyer")
                .setItems(lawyers, (dialog, which) -> {
                    int cost = costs[which];

                    if (money < cost) {
                        showMessage("Insufficient Money",
                                "You need ₹" + cost + " to hire this lawyer.");
                        return;
                    }

                    money -= cost;
                    legalFees += cost;

                    save.edit()
                            .putInt("legal_lawyer_bonus", bonuses[which])
                            .apply();

                    caseHistory.add("Hired " + lawyers[which]);

                    update("You hired a lawyer. Legal representation may improve your chances in court. Fee: ₹" + cost + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void attendHearing() {
        if (pendingCases <= 0) {
            showMessage("No Pending Cases",
                    "You currently have no pending court cases.");
            return;
        }

        String[] actions = {
                "Present Evidence",
                "Submit Documents",
                "Question Witnesses",
                "Request Settlement",
                "Wait for Judgment"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Court Hearing")
                .setItems(actions, (dialog, which) -> {
                    if (which == 3) {
                        requestSettlement();
                    } else if (which == 4) {
                        deliverJudgment();
                    } else {
                        int improvement = 3 + random.nextInt(6);
                        reputation = Math.min(100, reputation + improvement);

                        caseHistory.add("Court action: " + actions[which]);

                        update("You completed the court action: " +
                                actions[which] +
                                ". Your legal reputation improved.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void requestSettlement() {
        new AlertDialog.Builder(activity)
                .setTitle("Settlement")
                .setMessage("Would you like to settle the case outside court? This will close one pending case.")
                .setPositiveButton("Accept Settlement", (dialog, which) -> {
                    int amount = 5000 + random.nextInt(20001);

                    money = Math.max(0, money - amount);
                    legalFees += amount;

                    pendingCases--;
                    casesLost++;

                    caseHistory.add("Case settled outside court. Settlement cost ₹" + amount);

                    update("You settled a legal dispute outside court. Settlement cost: ₹" + amount + ".");
                })
                .setNegativeButton("Continue Case", null)
                .show();
    }

    private void deliverJudgment() {
        int lawyerBonus = save.getInt("legal_lawyer_bonus", 0);

        int chance = 45 + reputation / 5 + lawyerBonus;
        chance += random.nextInt(31) - 15;
        chance = Math.max(5, Math.min(95, chance));

        boolean won = random.nextInt(100) < chance;

        pendingCases--;

        if (won) {
            casesWon++;
            reputation = Math.min(100, reputation + 5);

            int compensation = 10000 + random.nextInt(50001);
            money += compensation;

            caseHistory.add("Won a court case. Compensation ₹" + compensation);

            update("The court ruled in your favour! You won the case and received ₹" +
                    compensation + " compensation.");
        } else {
            casesLost++;
            reputation = Math.max(0, reputation - 5);

            int fine = 5000 + random.nextInt(25001);
            money = Math.max(0, money - fine);

            caseHistory.add("Lost a court case. Fine ₹" + fine);

            update("The court ruled against you. You lost the case and paid ₹" +
                    fine + " in damages and legal costs.");
        }

        if (pendingCases == 0) {
            save.edit().putInt("legal_lawyer_bonus", 0).apply();
        }
    }

    private void showHistory() {
        if (caseHistory.isEmpty()) {
            showMessage("Case History", "No legal cases have been recorded yet.");
            return;
        }

        StringBuilder history = new StringBuilder();

        for (int i = caseHistory.size() - 1; i >= 0; i--) {
            history.append("• ")
                    .append(caseHistory.get(i))
                    .append("\n\n");
        }

        new AlertDialog.Builder(activity)
                .setTitle("Legal Case History")
                .setMessage(history.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private void showStatistics() {
        String details =
                "Cases Filed: " + casesFiled +
                "\nCases Won: " + casesWon +
                "\nCases Lost: " + casesLost +
                "\nPending Cases: " + pendingCases +
                "\nCriminal Cases: " + criminalCases +
                "\nTotal Legal Expenses: ₹" + legalFees +
                "\nLegal Reputation: " + reputation + "/100";

        showMessage("Legal Statistics", details);
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void update(String description) {
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
}
