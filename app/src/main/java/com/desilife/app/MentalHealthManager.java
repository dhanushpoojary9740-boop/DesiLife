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

public class MentalHealthManager {

    public interface MentalHealthCallback {
        void onUpdate(
            int money,
            int health,
            int happiness,
            int stress,
            String condition,
            String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final MentalHealthCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;
    private int stress;

    private String condition;

    private final int gold = Color.rgb(226,184,105);
    private final int dark = Color.rgb(12,25,23);
    private final int white = Color.WHITE;

    public MentalHealthManager(
        Activity activity,
        SharedPreferences save,
        int age,
        int money,
        int health,
        int happiness,
        MentalHealthCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        this.stress = save.getInt("mental_stress",20);
        this.condition = save.getString(
            "mental_condition",
            "Emotionally Stable"
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

        button.setBackground(shape(Color.rgb(239,202,126),18));
        button.setElevation(3);
        button.setPadding(14,15,14,15);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,7,0,7);

        root.addView(button,p);

        button.setOnClickListener(v -> action.run());
    }

    private void addDivider(LinearLayout root) {
        addText(
            root,
            "━━━━━━━━━━━━━━━━━━━━",
            12,
            gold,
            false
        );
    }

    private String stressStatus() {
        if (stress >= 80) return "Very High";
        if (stress >= 60) return "High";
        if (stress >= 35) return "Moderate";
        return "Low";
    }

    private String happinessStatus() {
        if (happiness >= 80) return "Very Happy";
        if (happiness >= 60) return "Happy";
        if (happiness >= 35) return "Neutral";
        return "Unhappy";
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

        addText(
            root,
            "🧠 MENTAL HEALTH",
            26,
            gold,
            true
        );

        addText(
            root,
            "Your mind matters. Balance your life and take care of yourself.",
            14,
            Color.LTGRAY,
            false
        );

        addDivider(root);

        addText(root,"YOUR MENTAL WELLNESS",19,gold,true);

        addText(
            root,
            "Age: "+age+" years",
            15,
            Color.LTGRAY,
            false
        );

        addText(
            root,
            "😊 Happiness: "+happiness+"%",
            23,
            happiness >= 70 ?
                Color.rgb(70,210,110) :
                happiness >= 35 ?
                Color.rgb(255,190,65) :
                Color.rgb(255,85,85),
            true
        );

        addText(
            root,
            "😓 Stress: "+stress+"% ("+stressStatus()+")",
            21,
            stress >= 70 ?
                Color.rgb(255,85,85) :
                stress >= 40 ?
                Color.rgb(255,190,65) :
                Color.rgb(70,210,110),
            true
        );

        addText(
            root,
            "Mental condition: "+condition,
            16,
            white,
            true
        );

        addDivider(root);

        addText(root,"SELF-CARE ACTIVITIES",19,gold,true);

        addButton(
            root,
            "🧘 Meditation",
            () -> meditation()
        );

        addButton(
            root,
            "🏖️ Take a Vacation – ₹3,000",
            () -> vacation()
        );

        addButton(
            root,
            "🎮 Enjoy a Hobby",
            () -> hobby()
        );

        addButton(
            root,
            "👨‍👩‍👧 Spend Time with Family",
            () -> familyTime()
        );

        addButton(
            root,
            "🌳 Nature Walk",
            () -> natureWalk()
        );

        addDivider(root);

        addText(root,"PROFESSIONAL SUPPORT",19,gold,true);

        addButton(
            root,
            "🧑‍⚕️ Counselling Session – ₹1,500",
            () -> counselling()
        );

        addButton(
            root,
            "🏥 Mental Health Specialist – ₹3,000",
            () -> specialist()
        );

        addDivider(root);

        addText(root,"WELLNESS GUIDE",18,gold,true);

        addText(
            root,
            "• Meditation helps manage stress.\n"+
            "• Hobbies can improve happiness.\n"+
            "• Spending time with loved ones builds support.\n"+
            "• Professional support is available when needed.\n"+
            "• High stress may affect your overall wellbeing.",
            14,
            Color.LTGRAY,
            false
        );

        addDivider(root);

        addButton(
            root,
            "BACK TO LIFE",
            () -> activity.recreate()
        );
    }

    public void open() {
        showScreen();
    }

    private void meditation() {
        int reduction = 10 + random.nextInt(11);

        stress = Math.max(0,stress-reduction);
        happiness = Math.min(100,happiness+4);

        updateCondition();

        finish(
            "You practised meditation. Stress reduced by "+
            reduction+" points."
        );

        message(
            "🧘 You feel more relaxed!\n\n"+
            "Stress: "+stress+"%\n"+
            "Happiness: "+happiness+"%"
        );
    }

    private void vacation() {
        if (money < 3000) {
            message("You need ₹3,000 for a vacation.");
            return;
        }

        money -= 3000;

        stress = Math.max(0,stress-25);
        happiness = Math.min(100,happiness+18);

        updateCondition();

        finish(
            "You went on a vacation and returned refreshed."
        );

        message(
            "🏖️ Wonderful vacation!\n\n"+
            "Stress: "+stress+"%\n"+
            "Happiness: "+happiness+"%\n"+
            "Money remaining: ₹"+money
        );
    }

    private void hobby() {
        stress = Math.max(0,stress-12);
        happiness = Math.min(100,happiness+12);

        updateCondition();

        finish(
            "You spent time enjoying your favourite hobby."
        );

        message(
            "🎮 You enjoyed your hobby!\n\n"+
            "Happiness: "+happiness+"%\n"+
            "Stress: "+stress+"%"
        );
    }

    private void familyTime() {
        stress = Math.max(0,stress-15);
        happiness = Math.min(100,happiness+15);

        updateCondition();

        finish(
            "You spent quality time with your family."
        );

        message(
            "👨‍👩‍👧 Family time made you feel better!\n\n"+
            "Happiness: "+happiness+"%\n"+
            "Stress: "+stress+"%"
        );
    }

    private void natureWalk() {
        stress = Math.max(0,stress-10);
        happiness = Math.min(100,happiness+7);
        health = Math.min(100,health+2);

        updateCondition();

        finish(
            "You enjoyed a peaceful walk in nature."
        );

        message(
            "🌳 A refreshing walk!\n\n"+
            "Happiness: "+happiness+"%\n"+
            "Stress: "+stress+"%\n"+
            "Health: "+health+"%"
        );
    }

    private void counselling() {
        if (money < 1500) {
            message(
                "You need ₹1,500 for a counselling session."
            );
            return;
        }

        money -= 1500;

        stress = Math.max(0,stress-22);
        happiness = Math.min(100,happiness+8);

        updateCondition();

        finish(
            "You attended a counselling session and received support."
        );

        message(
            "🧑‍⚕️ Counselling completed!\n\n"+
            "Stress: "+stress+"%\n"+
            "Happiness: "+happiness+"%\n"+
            "Money remaining: ₹"+money
        );
    }

    private void specialist() {
        if (money < 3000) {
            message(
                "You need ₹3,000 for specialist treatment."
            );
            return;
        }

        money -= 3000;

        stress = Math.max(0,stress-35);
        happiness = Math.min(100,happiness+12);

        updateCondition();

        finish(
            "You visited a mental health specialist and received professional support."
        );

        message(
            "🏥 Specialist consultation completed!\n\n"+
            "Stress: "+stress+"%\n"+
            "Happiness: "+happiness+"%\n"+
            "Money remaining: ₹"+money
        );
    }

    private void updateCondition() {
        if (stress >= 80) {
            condition = "Severely Stressed";
        } else if (stress >= 60) {
            condition = "Highly Stressed";
        } else if (stress >= 35) {
            condition = "Moderately Stressed";
        } else {
            condition = "Emotionally Stable";
        }
    }

    // Called once every year from MainActivity.
    public void applyAnnualMentalUpdate() {

        int stressIncrease = random.nextInt(13);

        if (age >= 60) {
            stressIncrease += random.nextInt(5);
        }

        stress = Math.min(100,stress+stressIncrease);

        int happinessChange = random.nextInt(11)-5;

        happiness = Math.max(
            0,
            Math.min(100,happiness+happinessChange)
        );

        if (stress >= 85) {
            condition = "Severely Stressed";

            happiness = Math.max(0,happiness-5);
            health = Math.max(1,health-2);

            finish(
                "You experienced very high stress this year. "+
                "Your happiness and health were affected."
            );

            new AlertDialog.Builder(activity)
                .setTitle("⚠️ Mental Wellness Alert")
                .setMessage(
                    "Your stress level is very high.\n\n"+
                    "Stress: "+stress+"%\n"+
                    "Happiness: "+happiness+"%\n\n"+
                    "Consider taking time for self-care or seeking professional support."
                )
                .setPositiveButton(
                    "Open Mental Health",
                    (dialog,which) -> showScreen()
                )
                .setNegativeButton(
                    "Continue",
                    null
                )
                .show();

        } else if (stress >= 65) {

            condition = "Highly Stressed";

            finish(
                "You experienced increased stress this year. "+
                "Consider making time for relaxation."
            );

        } else if (stress >= 40) {

            condition = "Moderately Stressed";

            finish(
                "You experienced some stress this year."
            );

        } else {

            condition = "Emotionally Stable";

            finish(
                "You maintained a balanced mental wellness level this year."
            );
        }
    }

    private void finish(String description) {

        stress = Math.max(0,Math.min(100,stress));
        happiness = Math.max(0,Math.min(100,happiness));
        health = Math.max(0,Math.min(100,health));

        save.edit()
            .putInt("mental_stress",stress)
            .putInt("happiness",happiness)
            .putString("mental_condition",condition)
            .apply();

        if (callback != null) {
            callback.onUpdate(
                money,
                health,
                happiness,
                stress,
                condition,
                description
            );
        }
    }

    private void message(String value) {
        new AlertDialog.Builder(activity)
            .setTitle("Mental Health & Wellness")
            .setMessage(value)
            .setPositiveButton("OK",null)
            .show();
    }
}
