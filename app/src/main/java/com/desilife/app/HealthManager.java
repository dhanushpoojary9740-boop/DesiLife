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

public class HealthManager {

    public interface HealthCallback {
        void onUpdate(
            int money,
            int health,
            String condition,
            String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final HealthCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private String condition;

    private final int gold = Color.rgb(226,184,105);
    private final int dark = Color.rgb(12,25,23);
    private final int white = Color.WHITE;

    public HealthManager(
        Activity activity,
        SharedPreferences save,
        int age,
        int money,
        int health,
        HealthCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.callback = callback;

        this.condition = save.getString(
            "health_condition",
            "Healthy"
        );
    }

    private GradientDrawable shape(int color,int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView makeText(
        String value,
        int size,
        int color,
        boolean bold
    ) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(8,10,8,10);

        if (bold) {
            t.setTypeface(null,Typeface.BOLD);
        }

        return t;
    }

    private void addText(
        LinearLayout root,
        String value,
        int size,
        int color,
        boolean bold
    ) {
        root.addView(
            makeText(value,size,color,bold),
            new LinearLayout.LayoutParams(-1,-2)
        );
    }

    private void addButton(
        LinearLayout root,
        String value,
        Runnable action
    ) {
        TextView button = makeText(
            value,15,dark,true
        );

        button.setBackground(shape(gold,20));

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,7,0,7);

        root.addView(button,p);

        button.setOnClickListener(v -> action.run());
    }

    private void showScreen() {
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(dark);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20,22,20,25);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(dark);

        scroll.addView(root);
        activity.setContentView(scroll);

        addText(root,"❤️ HEALTH & WELLNESS",26,gold,true);

        addText(
            root,
            "Take care of your body. Your choices affect your health.",
            14,
            Color.LTGRAY,
            false
        );

        addDivider(root);

        addText(root,"YOUR HEALTH STATUS",18,white,true);

        addText(
            root,
            "Age: "+age+" years",
            16,
            Color.LTGRAY,
            false
        );

        addText(
            root,
            "Health: "+health+"%",
            24,
            health >= 70 ? Color.rgb(70,210,110) :
                health >= 35 ? Color.rgb(255,190,65) :
                Color.rgb(255,85,85),
            true
        );

        addText(
            root,
            "Current condition: "+condition,
            17,
            white,
            true
        );

        addDivider(root);

        addText(root,"HEALTH SERVICES",19,gold,true);

        addButton(root,"🏥 Visit Hospital – ₹2,000",
            () -> visitHospital()
        );

        addButton(root,"💊 Buy Basic Medicine – ₹500",
            () -> buyMedicine()
        );

        addButton(root,"🛌 Rest at Home",
            () -> restAtHome()
        );

        addButton(root,"🏃 Exercise",
            () -> exercise()
        );

        addButton(root,"🥗 Healthy Lifestyle",
            () -> healthyLifestyle()
        );

        addDivider(root);

        addText(root,"HEALTH INFORMATION",17,gold,true);

        addText(
            root,
            "• Hospital treatment can help recover from illness.\n"+
            "• Rest can gradually improve your health.\n"+
            "• Exercise supports your overall health.\n"+
            "• Older characters may face higher health risks.\n"+
            "• Serious conditions should be treated promptly.",
            14,
            Color.LTGRAY,
            false
        );

        addDivider(root);

        addButton(root,"BACK TO LIFE",
            () -> activity.recreate()
        );
    }

    private void addDivider(LinearLayout root) {
        TextView line = makeText(
            "━━━━━━━━━━━━━━━━━━━━",
            12,
            gold,
            false
        );

        root.addView(line);
    }

    public void open() {
        showScreen();
    }

    private void visitHospital() {

        if (money < 2000) {
            message(
                "Not enough money!\n\n"+
                "You need ₹2,000 for hospital treatment."
            );
            return;
        }

        money -= 2000;

        int recovery = 25 + random.nextInt(16);

        health = Math.min(100,health+recovery);
        condition = "Healthy";

        finish(
            "You visited the hospital and received treatment. "+
            "Health improved by "+recovery+" points."
        );

        message(
            "🏥 Treatment completed!\n\n"+
            "Health: "+health+"%\n"+
            "Money remaining: ₹"+money
        );
    }

    private void buyMedicine() {

        if (money < 500) {
            message("You need ₹500 to buy medicine.");
            return;
        }

        money -= 500;

        int recovery = 8 + random.nextInt(8);

        health = Math.min(100,health+recovery);

        if (health >= 85) {
            condition = "Healthy";
        } else if (condition.equals("Healthy")) {
            condition = "Recovering";
        }

        finish(
            "You purchased medicine. Health improved by "+
            recovery+" points."
        );

        message(
            "💊 Medicine purchased!\n\n"+
            "Health: "+health+"%"
        );
    }

    private void restAtHome() {

        int recovery = 3 + random.nextInt(5);

        health = Math.min(100,health+recovery);

        if (health >= 90) {
            condition = "Healthy";
        } else if (condition.equals("Healthy")) {
            condition = "Recovering";
        }

        finish(
            "You rested at home and recovered "+
            recovery+" health points."
        );

        message(
            "🛌 You had a good rest.\n\n"+
            "Health: "+health+"%"
        );
    }

    private void exercise() {

        if (health < 15) {
            message(
                "Your health is too low for exercise. "+
                "Please seek medical treatment."
            );
            return;
        }

        int recovery = 2 + random.nextInt(4);

        health = Math.min(100,health+recovery);

        finish(
            "You exercised and improved your health by "+
            recovery+" points."
        );

        message(
            "🏃 Great work!\n\n"+
            "Health: "+health+"%"
        );
    }

    private void healthyLifestyle() {

        int recovery = 4 + random.nextInt(5);

        health = Math.min(100,health+recovery);

        if (health >= 90) {
            condition = "Healthy";
        }

        finish(
            "You followed a healthy lifestyle with good food, "+
            "sleep and hydration."
        );

        message(
            "🥗 Healthy habits make a difference!\n\n"+
            "Health: "+health+"%"
        );
    }

    // Called once each year from MainActivity.
    public void applyAnnualHealthUpdate() {

        if (health <= 0) {
            condition = "Critical";
            finish(
                "Your health is critical. You need medical attention."
            );
            return;
        }

        int risk = random.nextInt(100);

        int illnessChance = age >= 65 ? 20 :
                            age >= 45 ? 12 :
                            age >= 25 ? 7 : 4;

        if (risk < illnessChance) {

            String[] illnesses = {
                "Seasonal Fever",
                "Cold and Flu",
                "Stomach Infection",
                "Weakness"
            };

            String illness = illnesses[
                random.nextInt(illnesses.length)
            ];

            condition = illness;

            int damage = 3 + random.nextInt(9);

            health = Math.max(1,health-damage);

            new AlertDialog.Builder(activity)
                .setTitle("🤒 Health Alert")
                .setMessage(
                    "You developed "+illness+".\n\n"+
                    "Health decreased by "+damage+" points.\n"+
                    "Current health: "+health+"%\n\n"+
                    "What would you like to do?"
                )
                .setPositiveButton(
                    "Visit Hospital – ₹2,000",
                    (dialog,which) -> visitHospital()
                )
                .setNegativeButton(
                    "Rest at Home",
                    (dialog,which) -> restAtHome()
                )
                .setCancelable(false)
                .show();

        } else {

            if (health < 100 && random.nextInt(100) < 45) {

                health = Math.min(100,health+2);

                if (health >= 90) {
                    condition = "Healthy";
                } else if (condition.equals("Healthy")) {
                    condition = "Recovering";
                }

                finish(
                    "You stayed healthy this year and recovered naturally."
                );

            } else {
                finish(
                    "You completed another year without a major health issue."
                );
            }
        }
    }

    private void finish(String description) {

        save.edit()
            .putString("health_condition",condition)
            .apply();

        if (callback != null) {
            callback.onUpdate(
                money,
                health,
                condition,
                description
            );
        }
    }

    private void message(String value) {
        new AlertDialog.Builder(activity)
            .setTitle("Health & Wellness")
            .setMessage(value)
            .setPositiveButton("OK",null)
            .show();
    }
}
