package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;

import java.util.ArrayList;

public class FamilyManager {

    public interface FamilyCallback {
        void onUpdate(int money, int happiness);
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final FamilyCallback callback;

    private int money;
    private int age;
    private int happiness;

    private final int bg = Color.rgb(12,25,23);
    private final int gold = Color.rgb(226,184,105);
    private final int white = Color.WHITE;
    private final int muted = Color.LTGRAY;

    private LinearLayout root;

    private String spouseName;
    private String spouseStatus;
    private String children;
    private String siblings;
    private String parentsStatus;

    private boolean married;

    public FamilyManager(
            Activity activity,
            SharedPreferences save,
            int money,
            int age,
            int happiness,
            FamilyCallback callback) {

        this.activity = activity;
        this.save = save;
        this.money = money;
        this.age = age;
        this.happiness = happiness;
        this.callback = callback;

        loadData();
    }

    private void loadData() {

        spouseName = save.getString("family_spouse_name","");
        spouseStatus = save.getString("family_spouse_status","Single");
        children = save.getString("family_children","");
        siblings = save.getString("family_siblings","");
        parentsStatus = save.getString(
                "family_parents_status","Living"
        );

        married = save.getBoolean("family_married",false);
    }

    private void saveData() {

        save.edit()
            .putString("family_spouse_name",spouseName)
            .putString("family_spouse_status",spouseStatus)
            .putString("family_children",children)
            .putString("family_siblings",siblings)
            .putString("family_parents_status",parentsStatus)
            .putBoolean("family_married",married)
            .apply();

        if (callback != null) {
            callback.onUpdate(money,happiness);
        }
    }

    private GradientDrawable background(int color) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(18);

        return drawable;
    }

    private void setup() {

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);

        root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22,25,22,30);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(bg);

        scroll.addView(root);
        activity.setContentView(scroll);
    }

    private void addText(String value,int size,int color) {

        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setGravity(Gravity.CENTER);
        text.setPadding(8,10,8,10);

        root.addView(text);
    }

    private void addDivider() {

        TextView line = new TextView(activity);
        line.setBackgroundColor(Color.rgb(75,75,65));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1,1);

        params.setMargins(0,12,0,12);
        root.addView(line,params);
    }

    private void addButton(String title,Runnable action) {

        Button button = new Button(activity);
        button.setText(title);
        button.setTextColor(Color.rgb(25,25,25));
        button.setTextSize(14);
        button.setTypeface(null,Typeface.BOLD);
        button.setAllCaps(false);
        button.setBackground(background(gold));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1,-2);

        params.setMargins(0,7,0,7);

        root.addView(button,params);

        button.setOnClickListener(v -> action.run());
    }

    private void showInput(
            String title,
            String hint,
            java.util.function.Consumer<String> result) {

        EditText input = new EditText(activity);
        input.setSingleLine(true);
        input.setHint(hint);
        input.setPadding(25,15,25,15);

        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(input)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("SAVE",(dialog,which) -> {

                String value = input.getText()
                        .toString().trim();

                if (value.isEmpty()) {
                    Toast.makeText(
                        activity,
                        "Please enter a name.",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                result.accept(value);
            })
            .show();
    }

    public void open() {
        showFamily();
    }

    private void showFamily() {

        setup();

        addText("👨‍👩‍👧‍👦 FAMILY & LEGACY",25,gold);
        addText("Your family. Your responsibilities. Your legacy.",
                14,muted);

        addDivider();

        addText("YOUR FAMILY PROFILE",20,gold);

        addText("Age: "+age+" years",17,white);
        addText("Family background: "+
                save.getString("family","Middle Class"),
                16,white);

        addText("Relationship: "+spouseStatus,17,white);

        addDivider();

        addText("👨‍👩‍👧 PARENTS",20,gold);

        addText("Parents' status: "+parentsStatus,16,white);

        addButton("UPDATE PARENTS' STATUS",() -> {

            String[] options = {
                "Both living",
                "Father passed away",
                "Mother passed away",
                "Both passed away"
            };

            new AlertDialog.Builder(activity)
                .setTitle("Parents")
                .setItems(options,(dialog,which) -> {

                    parentsStatus = options[which];
                    saveData();
                    showFamily();
                })
                .show();
        });

        addDivider();

        addText("👫 SIBLINGS",20,gold);

        addText(
            siblings.isEmpty()
                ? "No siblings added."
                : siblings.replace("|","\n"),
            16,white
        );

        addButton("ADD SIBLING",() -> {

            showInput("Sibling name","Enter name",name -> {

                if (!siblings.isEmpty()) {
                    siblings += "|";
                }

                siblings += name;

                saveData();
                showFamily();
            });
        });

        addDivider();

        addText("❤️ MARRIAGE",20,gold);

        if (married) {

            addText("Spouse: "+spouseName,18,white);
            addText("Status: Married",16,gold);

        } else {

            addText("You are currently single.",16,muted);

            if (age >= 21) {

                addButton("💍 GET MARRIED",() -> {

                    showInput(
                        "Marriage",
                        "Enter spouse name",
                        name -> {

                            spouseName = name;
                            married = true;
                            spouseStatus = "Married";

                            happiness = Math.min(
                                100,happiness+15
                            );

                            saveData();

                            showFamily();

                            message(
                                "Congratulations! 🎉\n\n"+
                                "You married "+spouseName+"."
                            );
                        }
                    );
                });

            } else {

                addText(
                    "Marriage opportunities unlock at age 21.",
                    14,muted
                );
            }
        }

        addDivider();

        addText("👶 CHILDREN",20,gold);

        String[] childList = children.isEmpty()
                ? new String[0]
                : children.split("\\|");

        if (childList.length == 0) {

            addText("You don't have children yet.",15,muted);

        } else {

            for (String child : childList) {

                addText("👶 "+child,16,white);
            }
        }

        if (married && age >= 21) {

            addButton("➕ WELCOME A CHILD",() -> {

                showInput(
                    "New child",
                    "Enter child's name",
                    name -> {

                        if (!children.isEmpty()) {
                            children += "|";
                        }

                        children += name+" (Age 0)";

                        happiness = Math.min(
                            100,happiness+10
                        );

                        saveData();

                        showFamily();

                        message(
                            "Congratulations!\n\n"+
                            name+" has joined your family."
                        );
                    }
                );
            });
        }

        addDivider();

        addText("💰 FAMILY FINANCES",20,gold);

        addText("Available money: ₹"+money,18,white);
        addText("Happiness: "+happiness+"/100",16,white);

        addButton("🎁 FAMILY CELEBRATION",() -> {

            if (money < 1000) {

                message("You need at least ₹1,000.");
                return;
            }

            money -= 1000;
            happiness = Math.min(100,happiness+15);

            saveData();
            showFamily();

            message(
                "Your family enjoyed a wonderful celebration!\n\n"+
                "Money spent: ₹1,000\n"+
                "Happiness increased."
            );
        });

        addDivider();

        addText("🌳 FAMILY LEGACY",20,gold);

        addText(
            "Build your family, raise the next generation "+
            "and create a lasting legacy.",
            15,muted
        );

        addButton("BACK TO LIFE",() -> {

            if (activity instanceof MainActivity) {
                ((MainActivity)activity).showDashboard();
            }
        });
    }

    private void message(String text) {

        new AlertDialog.Builder(activity)
            .setMessage(text)
            .setPositiveButton("OK",null)
            .show();
    }

}
