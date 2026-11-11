package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;

public class AssetsManager {

    Activity activity;
    SharedPreferences save;
    int money;

    int gold = Color.rgb(226,184,105);
    int bg = Color.rgb(12,25,23);
    int white = Color.WHITE;

    AlertDialog dialog;

    public interface MoneyListener {
        void onMoneyChanged(int amount);
    }

    MoneyListener listener;

    public AssetsManager(
        Activity activity,
        SharedPreferences save,
        int money,
        MoneyListener listener
    ) {
        this.activity = activity;
        this.save = save;
        this.money = money;
        this.listener = listener;
    }

    int get(String key, int fallback) {
        return save.getInt(key,fallback);
    }

    String getString(String key) {
        return save.getString(key,"");
    }

    void put(String key,int value) {
        save.edit().putInt(key,value).apply();
    }

    void putString(String key,String value) {
        save.edit().putString(key,value).apply();
    }

    int safeAdd(int a,int b) {
        long result = (long)a+b;
        return (int)Math.min(Integer.MAX_VALUE,
                Math.max(0,result));
    }

    void updateMoney() {
        save.edit().putInt("money",money).apply();

        if (listener != null) {
            listener.onMoneyChanged(money);
        }

        // Refresh the dashboard behind the Assets window.
        if (activity instanceof MainActivity) {
            ((MainActivity)activity).showDashboard();
        }
    }

    GradientDrawable shape(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(20);
        return d;
    }

    TextView text(String value,int size,int color) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(12,12,12,12);
        return t;
    }

    void button(
        LinearLayout layout,
        String title,
        Runnable action
    ) {
        TextView b = text(title,15,bg);
        b.setTypeface(null,Typeface.BOLD);
        b.setBackground(shape(gold));

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,7,0,7);
        layout.addView(b,p);

        b.setOnClickListener(v -> action.run());
    }

    void heading(LinearLayout layout,String title) {
        TextView t = text(title,19,gold);
        layout.addView(t);
    }

    public void open() {
        show();
    }

    void refresh() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        show();
    }

    void show() {

        ScrollView scroll = new ScrollView(activity);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18,18,18,25);
        root.setBackgroundColor(bg);

        scroll.addView(root);

        heading(root,"💰 ASSETS & FINANCES");

        root.addView(text(
            "Bank Balance: ₹"+money,
            23,gold
        ));

        int savings = get("savingsBalance",0);

        root.addView(text(
            "Savings: ₹"+savings,
            18,white
        ));

        root.addView(text(
            "Total wealth: ₹"+((long)money+savings),
            15,white
        ));

        // BANK AND SAVINGS
        heading(root,"🏦 BANK & SAVINGS");

        button(root,"DEPOSIT INTO SAVINGS",
            () -> amountDialog("Deposit",amount -> {

                if (amount<=0 || amount>money) {
                    error("Insufficient bank balance.");
                    return;
                }

                money-=amount;

                put("savingsBalance",
                    safeAdd(get("savingsBalance",0),amount));

                updateMoney();
                refresh();
            })
        );

        button(root,"WITHDRAW SAVINGS",
            () -> amountDialog("Withdraw",amount -> {

                int current = get("savingsBalance",0);

                if (amount<=0 || amount>current) {
                    error("Insufficient savings.");
                    return;
                }

                put("savingsBalance",current-amount);
                money=safeAdd(money,amount);

                updateMoney();
                refresh();
            })
        );

        // PROPERTY
        heading(root,"🏠 PROPERTY");

        String house = getString("ownedHouse");

        if (house.isEmpty()) {

            root.addView(text(
                "You don't own a property yet.",
                14,white
            ));

            button(root,"BUY PROPERTY",
                () -> buyHouse());

        } else {

            root.addView(text(
                "Owned: "+house+
                "\nValue: ₹"+get("houseValue",0)+
                "\nLuxury Level: "+get("houseLevel",0)+"/5",
                16,white
            ));

            button(root,"UPGRADE HOUSE",
                () -> upgradeHouse());

            button(root,"SELL HOUSE",
                () -> sellHouse());
        }

        // VEHICLES
        heading(root,"🚗 VEHICLES");

        String vehicle = getString("ownedVehicle");

        if (vehicle.isEmpty()) {

            root.addView(text(
                "You don't own a vehicle.",
                14,white
            ));

            button(root,"BUY VEHICLE",
                () -> buyVehicle());

        } else {

            root.addView(text(
                "Owned: "+vehicle+
                "\nValue: ₹"+get("vehicleValue",0),
                16,white
            ));

            button(root,"SELL VEHICLE",
                () -> sellVehicle());
        }

        // MONTHLY FINANCES
        heading(root,"📊 MONTHLY FINANCES");

        int income = get("monthlyIncome",0);
        int expenses = get("monthlyExpenses",0);

        root.addView(text(
            "Monthly Income: ₹"+income+
            "\nMonthly Expenses: ₹"+expenses+
            "\nMonthly Balance: ₹"+((long)income-expenses),
            16,white
        ));

        button(root,"SET MONTHLY INCOME",
            () -> amountDialog("Monthly income",amount -> {
                put("monthlyIncome",amount);
                refresh();
            })
        );

        button(root,"SET MONTHLY EXPENSES",
            () -> amountDialog("Monthly expenses",amount -> {
                put("monthlyExpenses",amount);
                refresh();
            })
        );

        root.addView(text(
            "Income and expenses are calculated over 12 months when you age up.",
            13,white
        ));

        // LOANS
        heading(root,"💳 LOANS & DEBT");

        int debt = get("loanDebt",0);

        root.addView(text(
            "Outstanding Debt: ₹"+debt+
            "\nAnnual Interest: 5%",
            16,white
        ));

        button(root,"TAKE LOAN",
            () -> takeLoan());

        if (debt>0) {
            button(root,"REPAY LOAN",
                () -> amountDialog("Repayment amount",amount -> {

                    int current = get("loanDebt",0);

                    if (amount<=0 ||
                        amount>current ||
                        amount>money) {

                        error("Invalid repayment amount.");
                        return;
                    }

                    money-=amount;
                    put("loanDebt",current-amount);

                    updateMoney();
                    refresh();
                })
            );
        }

        button(root,"CLOSE",() -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(activity)
            .setView(scroll)
            .create();

        dialog.show();

        if (dialog.getWindow()!=null) {
            dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
            );
        }
    }

    interface AmountAction {
        void run(int amount);
    }

    void amountDialog(String title,AmountAction action) {

        EditText input = new EditText(activity);

        input.setHint("Enter amount in ₹");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(input)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("CONFIRM",(d,w) -> {

                try {
                    String value = input.getText().toString().trim();

                    if (value.isEmpty()) {
                        error("Enter a valid amount.");
                        return;
                    }

                    int amount = Integer.parseInt(value);

                    if (amount<=0) {
                        error("Enter an amount greater than zero.");
                        return;
                    }

                    action.run(amount);

                } catch(Exception e) {
                    error("Enter a valid number.");
                }
            })
            .show();
    }

    // BUY PROPERTY
    void buyHouse() {

        String[] names = {
            "Small House - ₹150000",
            "Family Apartment - ₹400000",
            "Luxury Villa - ₹1200000"
        };

        int[] prices = {
            150000,400000,1200000
        };

        new AlertDialog.Builder(activity)
            .setTitle("Choose Property")
            .setItems(names,(d,index) -> {

                if (money<prices[index]) {
                    error("Not enough money!");
                    return;
                }

                money-=prices[index];

                String name = names[index].split(" - ")[0];

                putString("ownedHouse",name);
                put("houseValue",prices[index]);
                put("houseLevel",0);

                updateMoney();
                refresh();
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    // LUXURY HOUSE UPGRADES
    void upgradeHouse() {

        int level = get("houseLevel",0);

        if (level>=5) {
            error("Maximum luxury level reached!");
            return;
        }

        int cost = 25000*(level+1);

        new AlertDialog.Builder(activity)
            .setTitle("Luxury Upgrade")
            .setMessage(
                "Upgrade level: "+(level+1)+
                "\nCost: ₹"+cost
            )
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("UPGRADE",(d,w) -> {

                if (money<cost) {
                    error("Not enough money!");
                    return;
                }

                money-=cost;

                put("houseLevel",level+1);

                put("houseValue",
                    safeAdd(get("houseValue",0),
                    cost+(cost/2)));

                updateMoney();
                refresh();
            })
            .show();
    }

    // SELL PROPERTY
    void sellHouse() {

        int value = get("houseValue",0);
        int sellingPrice = (int)(value*0.90);

        new AlertDialog.Builder(activity)
            .setTitle("Sell Property")
            .setMessage(
                "You will receive ₹"+sellingPrice
            )
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("SELL",(d,w) -> {

                money=safeAdd(money,sellingPrice);

                putString("ownedHouse","");
                put("houseValue",0);
                put("houseLevel",0);

                updateMoney();
                refresh();
            })
            .show();
    }

    // BUY VEHICLE
    void buyVehicle() {

        String[] names = {
            "Bicycle - ₹5000",
            "Scooter - ₹75000",
            "Motorcycle - ₹150000",
            "Family Car - ₹600000",
            "Luxury Car - ₹1800000"
        };

        int[] prices = {
            5000,75000,150000,600000,1800000
        };

        new AlertDialog.Builder(activity)
            .setTitle("Choose Vehicle")
            .setItems(names,(d,index) -> {

                if (money<prices[index]) {
                    error("Not enough money!");
                    return;
                }

                money-=prices[index];

                putString(
                    "ownedVehicle",
                    names[index].split(" - ")[0]
                );

                put("vehicleValue",prices[index]);

                updateMoney();
                refresh();
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    // SELL VEHICLE
    void sellVehicle() {

        int value = get("vehicleValue",0);
        int sellingPrice = (int)(value*0.75);

        new AlertDialog.Builder(activity)
            .setTitle("Sell Vehicle")
            .setMessage(
                "You will receive ₹"+sellingPrice
            )
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("SELL",(d,w) -> {

                money=safeAdd(money,sellingPrice);

                putString("ownedVehicle","");
                put("vehicleValue",0);

                updateMoney();
                refresh();
            })
            .show();
    }

    // TAKE LOAN
    void takeLoan() {

        String[] choices = {
            "₹50000",
            "₹100000",
            "₹250000"
        };

        int[] amounts = {
            50000,100000,250000
        };

        new AlertDialog.Builder(activity)
            .setTitle("Choose Loan")
            .setItems(choices,(d,index) -> {

                money=safeAdd(money,amounts[index]);

                put("loanDebt",
                    safeAdd(get("loanDebt",0),amounts[index]));

                updateMoney();
                refresh();
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    void error(String message) {
        new AlertDialog.Builder(activity)
            .setTitle("DesiLife")
            .setMessage(message)
            .setPositiveButton("OK",null)
            .show();
    }

    // APPLY ANNUAL FINANCES WHEN PLAYER AGES UP
    public static int applyAnnualFinances(
        SharedPreferences save,
        int currentMoney
    ) {

        long income =
            (long)save.getInt("monthlyIncome",0)*12;

        long expenses =
            (long)save.getInt("monthlyExpenses",0)*12;

        long balance =
            (long)currentMoney+income-expenses;

        int updated = (int)Math.max(
            0,
            Math.min(Integer.MAX_VALUE,balance)
        );

        int debt = save.getInt("loanDebt",0);

        if (debt>0) {

            long newDebt = debt+Math.max(1,debt/20);

            debt = (int)Math.min(
                Integer.MAX_VALUE,newDebt
            );

            save.edit().putInt("loanDebt",debt).apply();
        }

        return updated;
    }
}
