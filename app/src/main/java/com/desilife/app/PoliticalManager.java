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

import java.util.Random;

public class PoliticalManager {

    public interface PoliticalCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final PoliticalCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;

    private String party;
    private String office;
    private int popularity;
    private int scandals;
    private int politicalYears;
    private int policies;
    private int electionWins;

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 190, 185);

    public PoliticalManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            PoliticalCallback callback
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
        party = save.getString("political_party", "");
        office = save.getString("political_office", "Ordinary Citizen");
        popularity = save.getInt("political_popularity", 0);
        scandals = save.getInt("political_scandals", 0);
        politicalYears = save.getInt("political_years", 0);
        policies = save.getInt("political_policies", 0);
        electionWins = save.getInt("political_election_wins", 0);
    }

    private void saveData() {
        save.edit()
                .putString("political_party", party)
                .putString("political_office", office)
                .putInt("political_popularity", popularity)
                .putInt("political_scandals", scandals)
                .putInt("political_years", politicalYears)
                .putInt("political_policies", policies)
                .putInt("political_election_wins", electionWins)
                .apply();
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
        title.setText("🏛️ GOVERNMENT & POLITICS");
        title.setTextColor(gold);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);
        root.addView(title);

        addInfo(root, "Political Party", party.isEmpty() ? "Independent / No Party" : party);
        addInfo(root, "Current Position", office);
        addInfo(root, "Public Approval", popularity + "%");
        addInfo(root, "Election Victories", String.valueOf(electionWins));
        addInfo(root, "Political Experience", politicalYears + " years");
        addInfo(root, "Scandals", String.valueOf(scandals));
        addInfo(root, "Policies Introduced", String.valueOf(policies));

        addDivider(root);

        addButton(root, "🗳️ Join a Political Party", this::joinParty);
        addButton(root, "📢 Run a Political Campaign", this::campaign);
        addButton(root, "🏆 Contest an Election", this::contestElection);
        addButton(root, "🎖️ Political Leadership Advancement", this::seekLeadershipPosition);
        addButton(root, "📜 Introduce a Public Policy", this::introducePolicy);
        addButton(root, "🤝 Public Service", this::publicService);
        addButton(root, "📰 Handle a Political Scandal", this::handleScandal);
        addButton(root, "📊 Political Career Details", this::showCareerDetails);
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
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
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

    private void joinParty() {
        if (!party.isEmpty()) {
            showMessage(
                    "Already a Party Member",
                    "You are currently a member of " + party +
                            ". You must leave your current party before joining another one."
            );
            return;
        }

        String[] parties = {
                "Indian National Congress",
                "Bharatiya Janata Party",
                "Aam Aadmi Party",
                "Janata Dal (Secular)",
                "Regional People's Party",
                "Independent"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Your Political Party")
                .setItems(parties, (dialog, which) -> {
                    party = parties[which];
                    popularity = Math.min(100, popularity + 5);
                    saveData();
                    update("You joined " + party +
                            " and started your political journey.");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void campaign() {
        if (party.isEmpty()) {
            showMessage("No Political Party",
                    "Join a political party before starting a campaign.");
            return;
        }

        String[] campaigns = {
                "Local Community Campaign - ₹10,000",
                "District-Level Campaign - ₹50,000",
                "State-Level Campaign - ₹2,00,000",
                "Major Public Campaign - ₹5,00,000"
        };

        int[] costs = {10000, 50000, 200000, 500000};
        int[] gains = {5, 10, 18, 25};

        new AlertDialog.Builder(activity)
                .setTitle("Choose Campaign")
                .setItems(campaigns, (dialog, which) -> {
                    int cost = costs[which];

                    if (money < cost) {
                        showMessage("Insufficient Money",
                                "You need ₹" + cost + " to run this campaign.");
                        return;
                    }

                    money -= cost;

                    int increase = gains[which] + random.nextInt(6);
                    popularity = Math.min(100, popularity + increase);

                    update("Your campaign attracted public attention. " +
                            "Public approval increased by " + increase +
                            "%. Campaign expense: ₹" + cost + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void contestElection() {
        if (age < 25) {
            showMessage("Not Eligible",
                    "You must be at least 25 years old to contest the available elections in this game.");
            return;
        }

        if (party.isEmpty()) {
            showMessage("Join a Party First",
                    "You need to join a political party before contesting an election.");
            return;
        }

        String[] elections = {
                "Local Municipal Election",
                "Member of Legislative Assembly (MLA)",
                "Member of Parliament (MP)"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose an Election")
                .setItems(elections, (dialog, which) -> {
                    if (which == 2 && age < 25) {
                        showMessage("Not Eligible",
                                "You must be at least 25 years old.");
                        return;
                    }

                    runElection(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void runElection(int type) {
        String position;
        int requiredApproval;
        int campaignCost;

        if (type == 0) {
            position = "Municipal Councillor";
            requiredApproval = 15;
            campaignCost = 25000;
        } else if (type == 1) {
            position = "Member of Legislative Assembly (MLA)";
            requiredApproval = 30;
            campaignCost = 100000;
        } else {
            position = "Member of Parliament (MP)";
            requiredApproval = 45;
            campaignCost = 250000;
        }

        if (money < campaignCost) {
            showMessage("Insufficient Funds",
                    "You need ₹" + campaignCost +
                            " for the election campaign.");
            return;
        }

        if (popularity < requiredApproval) {
            showMessage("Low Public Approval",
                    "You need at least " + requiredApproval +
                            "% public approval to have a reasonable chance of winning. " +
                            "Run more campaigns or participate in public service.");
            return;
        }

        money -= campaignCost;

        int chance = popularity + random.nextInt(31) - 10;
        chance -= scandals * 5;
        chance = Math.max(5, Math.min(95, chance));

        boolean won = random.nextInt(100) < chance;

        if (won) {
            office = position;
            electionWins++;
            politicalYears = 0;
            popularity = Math.min(100, popularity + 10);

            update("Congratulations! You won the election and became " +
                    position + ". Your political career has reached a new milestone.");
        } else {
            popularity = Math.max(0, popularity - 5);

            update("You lost the election. Your campaign expense was ₹" +
                    campaignCost + ". Work harder and improve your public approval before the next election.");
        }
    }

    private void seekLeadershipPosition() {
        if (party.isEmpty()) {
            showMessage("Join a Political Party",
                    "You must join a political party before progressing in politics.");
            return;
        }

        String nextPosition;
        int requiredPopularity;
        int requiredWins;
        int requiredYears;

        if (office.equals("Ordinary Citizen")) {
            nextPosition = "Local Party Worker";
            requiredPopularity = 10;
            requiredWins = 0;
            requiredYears = 1;
        } else if (office.equals("Local Party Worker")) {
            nextPosition = "Ward / Block Party President";
            requiredPopularity = 25;
            requiredWins = 0;
            requiredYears = 2;
        } else if (office.equals("Ward / Block Party President")) {
            nextPosition = "District Party President";
            requiredPopularity = 40;
            requiredWins = 0;
            requiredYears = 3;
        } else if (office.equals("Municipal Councillor")) {
            nextPosition = "Municipal Chairperson";
            requiredPopularity = 45;
            requiredWins = 1;
            requiredYears = 2;
        } else if (office.equals("Member of Legislative Assembly (MLA)")) {
            nextPosition = "State Minister";
            requiredPopularity = 55;
            requiredWins = 1;
            requiredYears = 2;
        } else if (office.equals("State Minister")) {
            nextPosition = "Chief Minister";
            requiredPopularity = 80;
            requiredWins = 2;
            requiredYears = 4;
        } else if (office.equals("Member of Parliament (MP)")) {
            nextPosition = "Union Minister";
            requiredPopularity = 60;
            requiredWins = 1;
            requiredYears = 2;
        } else if (office.equals("Union Minister")) {
            nextPosition = "Prime Minister";
            requiredPopularity = 90;
            requiredWins = 3;
            requiredYears = 5;
        } else {
            showMessage("Leadership Progression",
                    "Your current political position has no further automatic appointment route. Continue serving, winning elections and building party support.");
            return;
        }

        if (popularity < requiredPopularity
                || electionWins < requiredWins
                || politicalYears < requiredYears) {
            showMessage("Promotion Requirements",
                    "Next position: " + nextPosition
                            + "\nRequired public approval: " + requiredPopularity + "%"
                            + "\nYour public approval: " + popularity + "%"
                            + "\nRequired election wins: " + requiredWins
                            + "\nYour election wins: " + electionWins
                            + "\nRequired political experience: " + requiredYears + " years"
                            + "\nYour experience: " + politicalYears + " years");
            return;
        }

        int chance = Math.min(90, 25 + popularity / 2
                + electionWins * 8 - scandals * 5);
        if (random.nextInt(100) < chance) {
            office = nextPosition;
            update("Political advancement achieved! You are now "
                    + nextPosition + ".");
        } else {
            popularity = Math.max(0, popularity - 2);
            update("Your party leadership advancement request was not approved. Continue building public trust.");
        }
    }

    private void introducePolicy() {
        if (office.equals("Ordinary Citizen")) {
            showMessage("No Government Position",
                    "You need to win an election before introducing government policies.");
            return;
        }

        String[] policyNames = {
                "Education Development",
                "Healthcare Improvement",
                "Roads and Infrastructure",
                "Employment Generation",
                "Rural Development",
                "Environmental Protection"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Select a Public Policy")
                .setItems(policyNames, (dialog, which) -> {
                    String policy = policyNames[which];

                    int cost = 20000 + random.nextInt(80001);

                    if (money < cost) {
                        showMessage("Insufficient Funds",
                                "This policy initiative requires ₹" + cost +
                                        " in personal political funds.");
                        return;
                    }

                    money -= cost;
                    policies++;

                    int increase = 3 + random.nextInt(8);
                    popularity = Math.min(100, popularity + increase);

                    update("You introduced the " + policy +
                            " initiative. Public approval increased by " +
                            increase + "%. Initiative expense: ₹" + cost + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void publicService() {
        String[] services = {
                "Visit Local Communities",
                "Organise a Health Camp",
                "Support Students",
                "Help During a Natural Disaster",
                "Meet Citizens and Hear Complaints"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Public Service Activities")
                .setItems(services, (dialog, which) -> {
                    String service = services[which];

                    int cost = 5000 + random.nextInt(15001);

                    if (money < cost) {
                        showMessage("Insufficient Money",
                                "You need ₹" + cost + " for this activity.");
                        return;
                    }

                    money -= cost;
                    popularity = Math.min(100, popularity + 4 + random.nextInt(5));
                    happiness = Math.min(100, happiness + 3);

                    update("You completed: " + service +
                            ". Citizens appreciated your efforts. Expense: ₹" +
                            cost + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleScandal() {
        if (scandals <= 0) {
            showMessage("No Active Scandals",
                    "Your political career currently has no recorded scandals.");
            return;
        }

        String[] choices = {
                "Issue a Public Apology",
                "Hold a Press Conference",
                "Hire a Legal Advisor",
                "Ignore the Allegations"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Handle Political Scandal")
                .setItems(choices, (dialog, which) -> {
                    if (which == 0) {
                        popularity = Math.min(100, popularity + 5);
                        scandals--;
                        update("You issued a public apology and accepted responsibility.");
                    } else if (which == 1) {
                        popularity = Math.min(100, popularity + 2);
                        scandals--;
                        update("You addressed the allegations in a press conference.");
                    } else if (which == 2) {
                        if (money < 25000) {
                            showMessage("Insufficient Money",
                                    "You need ₹25,000 to hire a legal advisor.");
                            return;
                        }

                        money -= 25000;
                        scandals--;
                        update("You hired a legal advisor to handle the allegations. Fee: ₹25,000.");
                    } else {
                        popularity = Math.max(0, popularity - 8);
                        update("You ignored the allegations. Public approval declined.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCareerDetails() {
        String details =
                "Political Party: " + (party.isEmpty() ? "None" : party) +
                "\nCurrent Position: " + office +
                "\nPublic Approval: " + popularity + "%" +
                "\nElection Victories: " + electionWins +
                "\nPolitical Experience: " + politicalYears + " years" +
                "\nActive Scandals: " + scandals +
                "\nPolicies Introduced: " + policies;

        showMessage("Political Career", details);
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
