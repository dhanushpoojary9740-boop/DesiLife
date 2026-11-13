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

import java.util.Random;

public class CrimeManager {

    private final Activity activity;
    private final SharedPreferences save;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;

    private int crimeCount;
    private int jailYears;
    private int wantedLevel;
    private int reputation;

    private boolean criminalRecord;

    private final int dark = Color.rgb(12, 25, 23);
    private final int card = Color.rgb(25, 43, 39);
    private final int gold = Color.rgb(226, 184, 105);
    private final int white = Color.WHITE;
    private final int red = Color.rgb(220, 85, 75);

    private CrimeCallback callback;

    public interface CrimeCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    public CrimeManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            CrimeCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        loadCrimeData();
    }

    private void loadCrimeData() {
        crimeCount = save.getInt("crime_count", 0);
        jailYears = save.getInt("jail_years", 0);
        wantedLevel = save.getInt("wanted_level", 0);
        reputation = save.getInt("crime_reputation", 100);
        criminalRecord = save.getBoolean("criminal_record", false);
    }

    private void saveCrimeData() {
        save.edit()
                .putInt("crime_count", crimeCount)
                .putInt("jail_years", jailYears)
                .putInt("wanted_level", wantedLevel)
                .putInt("crime_reputation", reputation)
                .putBoolean("criminal_record", criminalRecord)
                .apply();
    }

    public void open() {
        showMainScreen();
    }

    private void showMainScreen() {

        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(dark);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 26);

        scroll.addView(root);

        addTitle(root, "⚖️ CRIME & JUSTICE");

        addText(root, "Age: " + age + " years", 14, gold);
        addText(root, "Money: ₹" + money, 15, white);
        addText(root, "Crimes committed: " + crimeCount, 15, white);
        addText(root, "Wanted level: " + wantedLevel + "/5", 15, red);
        addText(root, "Criminal record: " +
                (criminalRecord ? "Yes" : "No"), 15, white);
        addText(root, "Reputation: " + reputation + "/100", 15, gold);
        addText(root, "Prison sentence remaining: " +
                jailYears + " years", 15, white);

        addDivider(root);

        addButton(root, "🚨 Crime Opportunities", this::showCrimeOptions);

        addButton(root, "👮 Police Investigation", this::policeInvestigation);

        addButton(root, "⚖️ Court & Lawyer", this::openCourt);

        addButton(root, "🔒 Prison Life", this::openPrison);

        addButton(root, "🌱 Rebuild Your Life", this::rebuildLife);

        addButton(root, "📋 Criminal Record", this::showRecord);

        addButton(root, "⬅ Back to Life", activity::recreate);

        activity.setContentView(scroll);
    }

    private void showCrimeOptions() {

        if (jailYears > 0) {
            message("You cannot commit crimes while serving a prison sentence.");
            return;
        }

        if (age < 18) {
            message("You must be at least 18 years old to access these crime opportunities.");
            return;
        }

        String[] crimes = {
                "Shoplifting - Low risk",
                "Pickpocketing - Medium risk",
                "Vehicle Theft - High risk",
                "Burglary - Very high risk",
                "Fraud - High risk"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose an opportunity")
                .setItems(crimes, (dialog, which) -> {

                    switch (which) {
                        case 0:
                            commitCrime(
                                    "Shoplifting",
                                    1000,
                                    15,
                                    12,
                                    15
                            );
                            break;

                        case 1:
                            commitCrime(
                                    "Pickpocketing",
                                    3000,
                                    30,
                                    22,
                                    25
                            );
                            break;

                        case 2:
                            commitCrime(
                                    "Vehicle Theft",
                                    10000,
                                    50,
                                    35,
                                    40
                            );
                            break;

                        case 3:
                            commitCrime(
                                    "Burglary",
                                    20000,
                                    65,
                                    45,
                                    55
                            );
                            break;

                        case 4:
                            commitCrime(
                                    "Fraud",
                                    15000,
                                    45,
                                    30,
                                    45
                            );
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void commitCrime(
            String crimeName,
            int reward,
            int risk,
            int healthDamage,
            int reputationDamage
    ) {

        new AlertDialog.Builder(activity)
                .setTitle("⚠️ " + crimeName)
                .setMessage(
                        "Potential reward: ₹" + reward +
                        "\nRisk level: " + risk + "%" +
                        "\n\nDo you want to proceed?"
                )
                .setPositiveButton("Proceed", (dialog, which) -> {

                    int actualRisk = risk + (wantedLevel * 5);
                    actualRisk = Math.min(actualRisk, 90);

                    boolean caught = random.nextInt(100) < actualRisk;

                    crimeCount++;
                    criminalRecord = true;

                    reputation = Math.max(
                            0,
                            reputation - reputationDamage
                    );

                    if (caught) {

                        wantedLevel = Math.min(5, wantedLevel + 1);

                        health = Math.max(
                                1,
                                health - healthDamage
                        );

                        happiness = Math.max(
                                0,
                                happiness - 15
                        );

                        message(
                                "🚨 You were caught!\n\n" +
                                "Crime: " + crimeName +
                                "\nPolice have arrested you." +
                                "\nYou can now face a court case."
                        );

                    } else {

                        money += reward;

                        wantedLevel = Math.min(
                                5,
                                wantedLevel + 1
                        );

                        happiness = Math.min(
                                100,
                                happiness + 5
                        );

                        message(
                                "You escaped successfully!\n\n" +
                                "Crime: " + crimeName +
                                "\nMoney gained: ₹" + reward +
                                "\nPolice may investigate later."
                        );
                    }

                    saveCrimeData();
                    finish("Crime activity: " + crimeName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void policeInvestigation() {

        if (wantedLevel == 0) {
            message("The police currently have no active leads against you.");
            return;
        }

        int investigationChance = Math.min(
                90,
                20 + wantedLevel * 15
        );

        boolean caught = random.nextInt(100) < investigationChance;

        if (caught) {

            wantedLevel = Math.max(0, wantedLevel - 1);

            message(
                    "👮 Police investigation successful!\n\n" +
                    "You have been arrested and must face court."
            );

            saveCrimeData();
            finish("Arrested during a police investigation.");

        } else {

            message(
                    "You avoided arrest for now.\n\n" +
                    "The investigation is still open."
            );
        }
    }

    private void openCourt() {

        if (!criminalRecord) {
            message("You have no criminal cases.");
            return;
        }

        String[] options = {
                "Hire a lawyer - ₹10,000",
                "Represent yourself",
                "Accept the charges"
        };

        new AlertDialog.Builder(activity)
                .setTitle("⚖️ Court Hearing")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        if (money < 10000) {
                            message("You need ₹10,000 to hire a lawyer.");
                            return;
                        }

                        money -= 10000;

                        int success = random.nextInt(100);

                        if (success < 45) {

                            wantedLevel = Math.max(0, wantedLevel - 1);

                            message(
                                    "Your lawyer successfully defended you!\n" +
                                    "The charges were reduced."
                            );

                        } else {

                            message(
                                    "Your lawyer could not win the case.\n" +
                                    "The judge will decide your sentence."
                            );

                            sentence();
                        }

                    } else if (which == 1) {

                        if (random.nextInt(100) < 20) {

                            wantedLevel = Math.max(0, wantedLevel - 1);

                            message("You successfully defended yourself!");

                        } else {

                            message("You lost the court case.");
                            sentence();
                        }

                    } else {

                        message("You accepted the charges.");
                        sentence();
                    }

                    saveCrimeData();
                    finish("Court hearing completed.");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sentence() {

        int sentence = 1 + random.nextInt(5);

        jailYears += sentence;

        money = Math.max(
                0,
                money - (sentence * 2000)
        );

        health = Math.max(
                1,
                health - 5
        );

        happiness = Math.max(
                0,
                happiness - 20
        );

        wantedLevel = 0;
        criminalRecord = true;

        message(
                "🔒 You were sentenced to prison!\n\n" +
                "Sentence: " + sentence + " years" +
                "\nFine: ₹" + (sentence * 2000)
        );
    }

    private void openPrison() {

        if (jailYears <= 0) {
            message("You are not currently in prison.");
            return;
        }

        String[] options = {
                "Serve one year",
                "Maintain good behaviour",
                "Exercise in prison",
                "Try to escape"
        };

        new AlertDialog.Builder(activity)
                .setTitle("🔒 Prison Life")
                .setMessage("Remaining sentence: " + jailYears + " years")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        serveYear();

                    } else if (which == 1) {

                        if (random.nextInt(100) < 60) {

                            jailYears = Math.max(0, jailYears - 1);

                            message(
                                    "Good behaviour rewarded!\n" +
                                    "One year has been reduced."
                            );

                        } else {

                            happiness = Math.max(0, happiness - 5);

                            message(
                                    "Your behaviour review did not reduce your sentence."
                            );
                        }

                    } else if (which == 2) {

                        health = Math.min(100, health + 5);
                        happiness = Math.min(100, happiness + 2);

                        message("You exercised and improved your health.");

                    } else {

                        tryEscape();
                    }

                    saveCrimeData();
                    finish("Prison activity completed.");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void serveYear() {

        jailYears--;

        health = Math.max(1, health - 5);
        happiness = Math.max(0, happiness - 10);

        age++;

        if (jailYears <= 0) {

            jailYears = 0;

            message(
                    "🎉 You have completed your prison sentence!\n\n" +
                    "You are now free to rebuild your life."
            );

        } else {

            message(
                    "One year served.\n\n" +
                    "Remaining sentence: " + jailYears + " years"
            );
        }
    }

    private void tryEscape() {

        if (random.nextInt(100) < 20) {

            jailYears = 0;
            wantedLevel = 5;

            health = Math.max(1, health - 15);
            happiness = Math.max(0, happiness - 10);

            message(
                    "You escaped from prison!\n\n" +
                    "However, you are now wanted by the police."
            );

        } else {

            jailYears += 2;

            health = Math.max(1, health - 10);
            happiness = Math.max(0, happiness - 15);

            message(
                    "Escape attempt failed!\n\n" +
                    "Your sentence has increased by 2 years."
            );
        }
    }

    private void rebuildLife() {

        if (jailYears > 0) {
            message("Complete your prison sentence before rebuilding your life.");
            return;
        }

        String[] options = {
                "Community service - ₹2,000",
                "Pay outstanding fines - ₹5,000",
                "Start a fresh chapter"
        };

        new AlertDialog.Builder(activity)
                .setTitle("🌱 Rebuild Your Life")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        if (money < 2000) {
                            message("You need ₹2,000.");
                            return;
                        }

                        money -= 2000;
                        reputation = Math.min(100, reputation + 15);

                        message(
                                "You completed community service.\n" +
                                "Your reputation improved."
                        );

                    } else if (which == 1) {

                        if (money < 5000) {
                            message("You need ₹5,000.");
                            return;
                        }

                        money -= 5000;
                        reputation = Math.min(100, reputation + 10);

                        message(
                                "You paid your outstanding fines."
                        );

                    } else {

                        reputation = Math.min(100, reputation + 5);
                        wantedLevel = 0;

                        message(
                                "You decided to start fresh.\n" +
                                "Rebuilding trust will take time."
                        );
                    }

                    saveCrimeData();
                    finish("You took a step towards rebuilding your life.");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRecord() {

        String details =
                "Crimes committed: " + crimeCount +
                "\nCriminal record: " + (criminalRecord ? "Yes" : "No") +
                "\nWanted level: " + wantedLevel + "/5" +
                "\nReputation: " + reputation + "/100" +
                "\nRemaining prison sentence: " + jailYears + " years";

        new AlertDialog.Builder(activity)
                .setTitle("📋 Criminal Record")
                .setMessage(details)
                .setPositiveButton("Close", null)
                .show();
    }

    private void finish(String description) {

        saveCrimeData();

        if (callback != null) {
            callback.onUpdate(
                    money,
                    health,
                    happiness,
                    description
            );
        }
    }

    private void addTitle(LinearLayout root, String value) {

        TextView title = new TextView(activity);
        title.setText(value);
        title.setTextColor(gold);
        title.setTextSize(25);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(5, 15, 5, 20);

        root.addView(title);
    }

    private void addText(
            LinearLayout root,
            String value,
            int size,
            int color
    ) {

        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setPadding(5, 7, 5, 7);

        root.addView(text);
    }

    private void addDivider(LinearLayout root) {

        TextView divider = new TextView(activity);
        divider.setBackgroundColor(gold);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 2);

        params.setMargins(0, 15, 0, 15);

        root.addView(divider, params);
    }

    private void addButton(
            LinearLayout root,
            String value,
            Runnable action
    ) {

        TextView button = new TextView(activity);

        button.setText(value);
        button.setTextColor(Color.rgb(25, 48, 39));
        button.setTextSize(16);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(12, 17, 12, 17);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(239, 202, 126));
        background.setCornerRadius(18);
        background.setStroke(1, gold);

        button.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);

        root.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void message(String value) {

        new AlertDialog.Builder(activity)
                .setMessage(value)
                .setPositiveButton("OK", null)
                .show();
    }
}
