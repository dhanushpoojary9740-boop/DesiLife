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
import java.util.List;

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
    private int relationship = 80;

    private final int bg = Color.rgb(12,25,23);
    private final int gold = Color.rgb(226,184,105);
    private final int white = Color.WHITE;
    private final int muted = Color.LTGRAY;

    private LinearLayout root;

    private String spouseName = "";
    private String spouseStatus = "Single";
    private String children = "";
    private String siblings = "";
    private String parentsStatus = "Both living";

    private String fatherName = "Father";
    private String motherName = "Mother";

    private boolean married = false;

    private int legacyFund = 0;

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
            "family_parents_status","Both living"
        );

        fatherName = save.getString(
            "family_father_name","Father"
        );

        motherName = save.getString(
            "family_mother_name","Mother"
        );

        married = save.getBoolean("family_married",false);

        relationship = save.getInt(
            "family_relationship",80
        );

        legacyFund = save.getInt(
            "family_legacy_fund",0
        );
    }

    private void saveData() {

        save.edit()
            .putString("family_spouse_name",spouseName)
            .putString("family_spouse_status",spouseStatus)
            .putString("family_children",children)
            .putString("family_siblings",siblings)
            .putString("family_parents_status",parentsStatus)
            .putString("family_father_name",fatherName)
            .putString("family_mother_name",motherName)
            .putBoolean("family_married",married)
            .putInt("family_relationship",relationship)
            .putInt("family_legacy_fund",legacyFund)
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

    private void message(String text) {

        new AlertDialog.Builder(activity)
            .setMessage(text)
            .setPositiveButton("OK",null)
            .show();
    }

    public void open() {
        showFamily();
    }

    // MAIN FAMILY SCREEN

    private void showFamily() {

        setup();

        addText("👨‍👩‍👧‍👦 FAMILY & LEGACY",25,gold);

        addText(
            "Your family. Your responsibilities. Your legacy.",
            14,muted
        );

        addDivider();

        addText("YOUR FAMILY PROFILE",20,gold);

        addText("Age: "+age+" years",17,white);

        addText(
            "Family background: "+
            save.getString("family","Middle Class"),
            16,white
        );

        addText(
            "Relationship: "+spouseStatus,
            17,white
        );

        addText(
            "Family happiness: "+happiness+"/100",
            16,white
        );

        addDivider();

        addText("👨‍👩‍👧 PARENTS",20,gold);

        addText(
            fatherName+"\n"+motherName,
            16,white
        );

        addText(
            "Parents' status: "+parentsStatus,
            15,muted
        );

        addButton("👨 UPDATE FATHER'S DETAILS",() -> {
            showInput(
                "Father's name",
                "Enter father's name",
                name -> {
                    fatherName = name;
                    saveData();
                    showFamily();
                }
            );
        });

        addButton("👩 UPDATE MOTHER'S DETAILS",() -> {
            showInput(
                "Mother's name",
                "Enter mother's name",
                name -> {
                    motherName = name;
                    saveData();
                    showFamily();
                }
            );
        });

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

        // SIBLINGS

        addText("👫 SIBLINGS",20,gold);

        List<String> siblingList = getList(siblings);

        if (siblingList.isEmpty()) {

            addText("No siblings added.",15,muted);

        } else {

            for (int i=0;i<siblingList.size();i++) {

                final int index = i;

                addText(
                    "👤 "+siblingList.get(i),
                    16,white
                );

                addButton(
                    "REMOVE SIBLING",
                    () -> {

                        siblingList.remove(index);
                        siblings = joinList(siblingList);

                        saveData();
                        showFamily();
                    }
                );
            }
        }

        addButton("➕ ADD SIBLING",() -> {

            showInput(
                "Add sibling",
                "Enter sibling name",
                name -> {

                    siblingList.add(name);
                    siblings = joinList(siblingList);

                    saveData();
                    showFamily();
                }
            );
        });

        addDivider();

        // MARRIAGE

        addText("❤️ MARRIAGE & RELATIONSHIP",20,gold);

        if (married) {

            addText(
                "💍 Married to "+spouseName,
                19,white
            );

            addText(
                "Relationship happiness: "+
                relationship+"/100",
                16,gold
            );

            addButton("❤️ SPEND QUALITY TIME – ₹500",() -> {

                if (money < 500) {
                    message("You need ₹500.");
                    return;
                }

                money -= 500;
                relationship = Math.min(100,relationship+10);
                happiness = Math.min(100,happiness+5);

                saveData();
                showFamily();
            });

            addButton("🎁 BUY A GIFT – ₹2,000",() -> {

                if (money < 2000) {
                    message("You need ₹2,000.");
                    return;
                }

                money -= 2000;
                relationship = Math.min(100,relationship+15);
                happiness = Math.min(100,happiness+8);

                saveData();
                showFamily();

                message("Your spouse loved the gift!");
            });

            addButton("UPDATE SPOUSE NAME",() -> {

                showInput(
                    "Spouse name",
                    "Enter spouse name",
                    name -> {
                        spouseName = name;
                        saveData();
                        showFamily();
                    }
                );
            });

            addButton("SEPARATE / DIVORCE",() -> {

                new AlertDialog.Builder(activity)
                    .setTitle("End Marriage?")
                    .setMessage(
                        "Are you sure you want to end your marriage?"
                    )
                    .setNegativeButton("CANCEL",null)
                    .setPositiveButton("CONFIRM",(d,w) -> {

                        married = false;
                        spouseStatus = "Divorced";
                        relationship = 20;

                        happiness = Math.max(0,happiness-15);

                        saveData();
                        showFamily();
                    })
                    .show();
            });

        } else {

            addText(
                "You are currently "+spouseStatus+".",
                16,muted
            );

            if (age >= 21) {

                addText(
                    "Wedding cost: ₹15,000",
                    15,white
                );

                addButton("💍 GET MARRIED",() -> {

                    if (money < 15000) {
                        message(
                            "You need ₹15,000 for the wedding."
                        );
                        return;
                    }

                    showInput(
                        "Marriage",
                        "Enter spouse name",
                        name -> {

                            money -= 15000;

                            spouseName = name;
                            married = true;
                            spouseStatus = "Married";

                            relationship = 85;

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

        // CHILDREN

        addText("👶 CHILDREN & NEXT GENERATION",20,gold);

        List<String> childList = getList(children);

        if (childList.isEmpty()) {

            addText(
                "You don't have children yet.",
                15,muted
            );

        } else {

            for (int i=0;i<childList.size();i++) {

                final int index = i;

                addText(
                    "👶 "+childList.get(i),
                    16,white
                );

                addButton(
                    "REMOVE CHILD",
                    () -> {

                        new AlertDialog.Builder(activity)
                            .setTitle("Remove child?")
                            .setMessage(
                                "This will remove this child from your family record."
                            )
                            .setNegativeButton("CANCEL",null)
                            .setPositiveButton("REMOVE",(d,w) -> {

                                childList.remove(index);
                                children = joinList(childList);

                                saveData();
                                showFamily();
                            })
                            .show();
                    }
                );
            }
        }

        if (married && age >= 21) {

            addButton("➕ WELCOME A CHILD",() -> {

                showInput(
                    "New child",
                    "Enter child's name",
                    name -> {

                        childList.add(name+" (Age 0)");
                        children = joinList(childList);

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

        // FAMILY RESPONSIBILITIES

        addText("🏠 FAMILY RESPONSIBILITIES",20,gold);

        addText(
            "Available money: ₹"+money,
            17,white
        );

        addText(
            "Family happiness: "+happiness+"/100",
            16,white
        );

        addButton("❤️ SUPPORT YOUR FAMILY – ₹2,000",() -> {

            if (money < 2000) {
                message("You need ₹2,000.");
                return;
            }

            money -= 2000;
            happiness = Math.min(100,happiness+10);

            saveData();
            showFamily();

            message("Your family appreciates your support!");
        });

        addButton("🎂 FAMILY CELEBRATION – ₹1,000",() -> {

            if (money < 1000) {
                message("You need at least ₹1,000.");
                return;
            }

            money -= 1000;
            happiness = Math.min(100,happiness+15);

            saveData();
            showFamily();

            message(
                "Your family enjoyed a wonderful celebration!"
            );
        });

        addDivider();

        // INHERITANCE

        addText("💰 INHERITANCE & WEALTH",20,gold);

        addText(
            "Family legacy fund: ₹"+legacyFund,
            17,white
        );

        addButton("🏦 ADD MONEY TO LEGACY FUND",() -> {

            showAmountInput(
                "Add to legacy fund",
                amount -> {

                    if (amount <= 0 || amount > money) {
                        message("Invalid amount or insufficient money.");
                        return;
                    }

                    if (legacyFund > Integer.MAX_VALUE-amount) {
                        message("Legacy fund limit reached.");
                        return;
                    }

                    money -= amount;
                    legacyFund += amount;

                    saveData();
                    showFamily();
                }
            );
        });

        addButton("🎁 WITHDRAW FROM LEGACY FUND",() -> {

            showAmountInput(
                "Withdraw from legacy fund",
                amount -> {

                    if (amount <= 0 || amount > legacyFund) {
                        message("Invalid amount or insufficient fund.");
                        return;
                    }

                    if (money > Integer.MAX_VALUE-amount) {
                        message("Bank balance limit reached.");
                        return;
                    }

                    legacyFund -= amount;
                    money += amount;

                    saveData();
                    showFamily();
                }
            );
        });

        addDivider();

        // GENERATIONAL LEGACY

        addText("👑 GENERATIONAL LEGACY",20,gold);

        addText(
            "Build your family's wealth, raise the next generation "+
            "and create a lasting legacy.",
            15,muted
        );

        addText(
            "Children: "+childList.size()+
            "\nFamily fund: ₹"+legacyFund+
            "\nMarriage: "+(married ? "Yes" : "No"),
            16,white
        );

        addButton("BACK TO LIFE",() -> {

            saveData();

            if (activity instanceof MainActivity) {
                ((MainActivity)activity).showDashboard();
            }
        });
    }

    // AMOUNT INPUT

    private interface AmountCallback {
        void onAmount(int amount);
    }

    private void showAmountInput(
            String title,
            AmountCallback result) {

        EditText input = new EditText(activity);

        input.setHint("Enter amount in rupees");
        input.setInputType(2);
        input.setPadding(25,15,25,15);

        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(input)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("CONFIRM",(dialog,which) -> {

                try {

                    int amount = Integer.parseInt(
                        input.getText().toString().trim()
                    );

                    result.onAmount(amount);

                } catch (Exception e) {

                    message("Please enter a valid amount.");
                }
            })
            .show();
    }

    // LIST STORAGE

    private List<String> getList(String data) {

        List<String> list = new ArrayList<>();

        if (data == null || data.trim().isEmpty()) {
            return list;
        }

        String[] parts = data.split("\\|");

        for (String part : parts) {

            if (!part.trim().isEmpty()) {
                list.add(part.trim());
            }
        }

        return list;
    }

    private String joinList(List<String> list) {

        StringBuilder result = new StringBuilder();

        for (String item : list) {

            if (result.length() > 0) {
                result.append("|");
            }

            result.append(item);
        }

        return result.toString();
    }
}
