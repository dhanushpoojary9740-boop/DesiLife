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

import java.util.Locale;

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

    int get(String key,int fallback) {
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

    int safeInt(long value) {
        return (int)Math.max(0,Math.min(Integer.MAX_VALUE,value));
    }

    int safeAdd(int a,int b) {
        return safeInt((long)a+b);
    }

    void updateMoney() {
        save.edit().putInt("money",money).apply();

        if(listener!=null) {
            listener.onMoneyChanged(money);
        }

        if(activity instanceof MainActivity) {
            ((MainActivity)activity).showDashboard();
        }
    }

    GradientDrawable shape(int color,int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    TextView text(String value,int size,int color) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(10,10,10,10);
        return t;
    }

    void button(LinearLayout layout,String title,Runnable action) {
        TextView b = text(title,15,bg);
        b.setTypeface(null,Typeface.BOLD);
        b.setBackground(shape(gold,20));
        b.setPadding(12,14,12,14);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,6,0,6);

        layout.addView(b,p);
        b.setOnClickListener(v -> action.run());
    }

    void heading(LinearLayout layout,String title) {
        layout.addView(text(title,19,gold));
    }

    void message(String msg) {
        new AlertDialog.Builder(activity)
            .setTitle("DesiLife Bank")
            .setMessage(msg)
            .setPositiveButton("OK",null)
            .show();
    }

    void error(String msg) {
        message(msg);
    }

    void refresh() {
        if(dialog!=null && dialog.isShowing()) {
            dialog.dismiss();
        }
        show();
    }

    public void open() {
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

        int savings = get("savingsBalance",0);
        int debt = get("loanDebt",0);

        root.addView(text("Bank Balance: ₹"+money,23,gold));
        root.addView(text("Savings: ₹"+savings,18,white));
        root.addView(text(
            "Total assets: ₹"+((long)money+savings),
            16,white
        ));
        root.addView(text(
            "Outstanding debt: ₹"+debt,
            16,white
        ));

        // BANK
        heading(root,"🏦 BANK & SAVINGS");

        button(root,"DEPOSIT INTO SAVINGS",() ->
            amountDialog("Deposit into savings",amount -> {
                if(amount>money) {
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

        button(root,"WITHDRAW SAVINGS",() ->
            amountDialog("Withdraw savings",amount -> {
                int current = get("savingsBalance",0);

                if(amount>current) {
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

        if(house.isEmpty()) {
            root.addView(text(
                "You don't own a property yet.",14,white
            ));
            button(root,"BUY PROPERTY",() -> buyHouse());
        } else {
            root.addView(text(
                "Owned: "+house+
                "\nValue: ₹"+get("houseValue",0)+
                "\nLuxury Level: "+get("houseLevel",0)+"/5",
                16,white
            ));

            button(root,"UPGRADE HOUSE",() -> upgradeHouse());
            button(root,"SELL HOUSE",() -> sellHouse());
        }

        // VEHICLES
        heading(root,"🚗 VEHICLES");

        String vehicle = getString("ownedVehicle");

        if(vehicle.isEmpty()) {
            root.addView(text(
                "You don't own a vehicle.",14,white
            ));
            button(root,"BUY VEHICLE",() -> buyVehicle());
        } else {
            root.addView(text(
                "Owned: "+vehicle+
                "\nValue: ₹"+get("vehicleValue",0),
                16,white
            ));
            button(root,"SELL VEHICLE",() -> sellVehicle());
        }

        // MONTHLY FINANCES
        heading(root,"📊 MONTHLY FINANCES");

        int income = get("monthlyIncome",0);
        int legacyExpenses = get("monthlyExpenses",0);

        root.addView(text(
            "Monthly Income: ₹"+income+
            "\nPreviously Saved Monthly Expenses: ₹"+legacyExpenses+
            "\n\nLegacy monthly expenses are no longer deducted here."+
            "\nManage your actual household bills in Finance > Household Expenses.",
            14,white
        ));

        button(root,"SET MONTHLY INCOME",() ->
            amountDialog("Monthly income",amount -> {
                put("monthlyIncome",amount);
                refresh();
            })
        );

        root.addView(text(
            "Monthly income is credited over 12 months when you age up.",
            13,white
        ));

        // LOANS
        heading(root,"🏦 LOANS & CREDIT");

        showLoanDetails(root);

        if(get("loanDebt",0)<=0) {
            button(root,"APPLY FOR A LOAN",() -> chooseLoan());
        } else {
            button(root,"MAKE EXTRA LOAN REPAYMENT",() ->
                extraRepayment()
            );
        }

        button(root,"CREDIT SCORE & BANK RECORD",() ->
            showCreditReport()
        );

        button(root,"CLOSE",() -> {
            if(dialog!=null) dialog.dismiss();
        });

        dialog = new AlertDialog.Builder(activity)
            .setView(scroll)
            .create();

        dialog.show();

        if(dialog.getWindow()!=null) {
            dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
            );
        }
    }

    // LOAN DETAILS

    void showLoanDetails(LinearLayout root) {
        int debt = get("loanDebt",0);
        int score = get("creditScore",700);
        int missed = get("missedPayments",0);
        String type = getString("loanType");

        if(debt<=0) {
            root.addView(text(
                "No active loan.\nCredit Score: "+score+"/900",
                16,white
            ));
            return;
        }

        double rate = get("loanRate",18);
        int tenure = get("loanTenure",5);
        int emi = get("loanEMI",0);
        int months = get("loanMonthsLeft",tenure*12);

        root.addView(text(
            "Loan Type: "+type+
            "\nOutstanding Principal: ₹"+debt+
            "\nAnnual Interest: "+rate+"%"+
            "\nMonthly EMI: ₹"+emi+
            "\nRemaining Tenure: "+months+" months"+
            "\nCredit Score: "+score+"/900"+
            "\nMissed Payments: "+missed,
            15,white
        ));
    }

    void chooseLoan() {
        String[] types = {
            "Personal Loan - 18%",
            "Home Loan - 8.5%",
            "Mortgage Loan - 10%",
            "Vehicle Loan - 11%",
            "Education Loan - 9%",
            "Business Loan - 15%"
        };

        String[] names = {
            "Personal Loan",
            "Home Loan",
            "Mortgage Loan",
            "Vehicle Loan",
            "Education Loan",
            "Business Loan"
        };

        double[] rates = {18,8.5,10,11,9,15};
        int[] maxYears = {5,25,15,7,10,10};

        new AlertDialog.Builder(activity)
            .setTitle("Choose Loan Type")
            .setItems(types,(d,index) -> {
                askLoanAmount(
                    names[index],
                    rates[index],
                    maxYears[index]
                );
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    void askLoanAmount(
        String type,
        double rate,
        int maxYears
    ) {
        String[] amounts = {
            "₹50000",
            "₹100000",
            "₹250000",
            "₹500000",
            "₹1000000"
        };

        int[] values = {
            50000,100000,250000,500000,1000000
        };

        new AlertDialog.Builder(activity)
            .setTitle(type+" - Choose Amount")
            .setItems(amounts,(d,index) -> {
                chooseTenure(type,rate,maxYears,values[index]);
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    void chooseTenure(
        String type,
        double rate,
        int maxYears,
        int principal
    ) {
        String[] years = new String[maxYears];

        for(int i=0;i<maxYears;i++) {
            years[i]=(i+1)+" year(s)";
        }

        new AlertDialog.Builder(activity)
            .setTitle("Choose Loan Tenure")
            .setItems(years,(d,index) -> {
                int tenure=index+1;
                confirmLoan(type,rate,principal,tenure);
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    int calculateEMI(int principal,double annualRate,int years) {
        int months=years*12;
        double monthlyRate=annualRate/1200.0;

        if(monthlyRate==0) {
            return (int)Math.ceil((double)principal/months);
        }

        double power=Math.pow(1+monthlyRate,months);
        double emi=principal*monthlyRate*power/(power-1);

        return safeInt((long)Math.ceil(emi));
    }

    void confirmLoan(
        String type,
        double rate,
        int principal,
        int years
    ) {
        int emi=calculateEMI(principal,rate,years);

        String warning =
            "Loan: "+type+
            "\nPrincipal: ₹"+principal+
            "\nAnnual interest: "+rate+"%"+
            "\nTenure: "+years+" years"+
            "\nMonthly EMI: ₹"+emi+
            "\n\nTotal scheduled repayment: ₹"+
            ((long)emi*years*12)+
            "\n\nYour EMI will be deducted every month when you age up. " +
            "Missed payments can damage your credit score.";

        new AlertDialog.Builder(activity)
            .setTitle("Loan Agreement")
            .setMessage(warning)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("ACCEPT",(d,w) -> {
                if(money>Integer.MAX_VALUE-principal) {
                    error("Balance limit reached.");
                    return;
                }

                money+=principal;

                put("loanDebt",principal);
                putString("loanType",type);
                put("loanRate",(int)Math.round(rate*10));
                put("loanTenure",years);
                put("loanEMI",emi);
                put("loanMonthsLeft",years*12);
                put("missedPayments",0);
                put("consecutiveMissed",0);
                put("loanOriginalAmount",principal);
                put("loanActive",1);

                save.edit()
                    .putFloat("loanRateExact",(float)rate)
                    .apply();

                updateMoney();
                refresh();

                message(
                    "Loan approved!\n₹"+principal+
                    " has been credited to your bank account."
                );
            })
            .show();
    }

    // EXTRA REPAYMENT

    void extraRepayment() {
        final int originalDebt = get("loanDebt",0);

        amountDialog("Extra repayment",amount -> {

            int debt = originalDebt;

            if(amount>money) {
                error("Insufficient bank balance.");
                return;
            }

            if(amount>debt) {
                error("Repayment cannot exceed outstanding debt.");
                return;
            }

            money-=amount;
            debt-=amount;

            put("loanDebt",debt);
            put("creditScore",
                Math.min(900,get("creditScore",700)+2));

            if(debt==0) {
                clearLoan();
                updateMoney();
                refresh();
                message("Congratulations! Your loan is fully repaid.");
                return;
            }

            updateMoney();
            refresh();
            message("Extra repayment successful. Your principal has reduced.");
        });
    }

    void clearLoan() {
        save.edit()
            .putInt("loanDebt",0)
            .putString("loanType","")
            .putInt("loanRate",0)
            .putFloat("loanRateExact",0)
            .putInt("loanTenure",0)
            .putInt("loanEMI",0)
            .putInt("loanMonthsLeft",0)
            .putInt("missedPayments",0)
            .putInt("consecutiveMissed",0)
            .putInt("loanActive",0)
            .apply();
    }

    void showCreditReport() {
        int score=get("creditScore",700);
        int missed=get("missedPayments",0);
        int good=get("onTimePayments",0);

        String rating;

        if(score>=800) rating="Excellent";
        else if(score>=750) rating="Very Good";
        else if(score>=700) rating="Good";
        else if(score>=650) rating="Fair";
        else if(score>=550) rating="Poor";
        else rating="Very Poor";

        message(
            "CREDIT REPORT"+
            "\n\nCredit Score: "+score+"/900"+
            "\nRating: "+rating+
            "\nOn-time payments: "+good+
            "\nMissed payments: "+missed+
            "\n\nPaying EMIs on time improves your score. " +
            "Missed payments damage your banking reputation."
        );
    }

    // AMOUNT DIALOG

    interface AmountAction {
        void run(int amount);
    }

    void amountDialog(String title,AmountAction action) {
        EditText input=new EditText(activity);
        input.setHint("Enter amount in ₹");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(input)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("CONFIRM",(d,w) -> {
                try {
                    int amount=Integer.parseInt(
                        input.getText().toString().trim()
                    );

                    if(amount<=0) {
                        error("Enter an amount greater than zero.");
                        return;
                    }

                    action.run(amount);

                } catch(Exception e) {
                    error("Enter a valid amount.");
                }
            })
            .show();
    }

    // PROPERTY

    void buyHouse() {
        String[] names={
            "Small House - ₹150000",
            "Family Apartment - ₹400000",
            "Luxury Villa - ₹1200000"
        };

        int[] prices={150000,400000,1200000};

        new AlertDialog.Builder(activity)
            .setTitle("Choose Property")
            .setItems(names,(d,index) -> {
                if(money<prices[index]) {
                    error("Not enough money!");
                    return;
                }

                money-=prices[index];

                putString("ownedHouse",
                    names[index].split(" - ")[0]);
                put("houseValue",prices[index]);
                put("houseLevel",0);

                updateMoney();
                refresh();
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    void upgradeHouse() {
        int level=get("houseLevel",0);

        if(level>=5) {
            error("Maximum luxury level reached!");
            return;
        }

        int cost=25000*(level+1);

        new AlertDialog.Builder(activity)
            .setTitle("Luxury Upgrade")
            .setMessage("Upgrade level: "+(level+1)+
                "\nCost: ₹"+cost)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("UPGRADE",(d,w) -> {
                if(money<cost) {
                    error("Not enough money!");
                    return;
                }

                money-=cost;
                put("houseLevel",level+1);
                put("houseValue",
                    safeAdd(get("houseValue",0),cost+cost/2));

                updateMoney();
                refresh();
            })
            .show();
    }

    void sellHouse() {
        int value=get("houseValue",0);
        int sellingPrice=(int)(value*0.90);

        new AlertDialog.Builder(activity)
            .setTitle("Sell Property")
            .setMessage("You will receive ₹"+sellingPrice)
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

    // VEHICLES

    void buyVehicle() {
        String[] names={
            "Bicycle - ₹5000",
            "Scooter - ₹75000",
            "Motorcycle - ₹150000",
            "Family Car - ₹600000",
            "Luxury Car - ₹1800000"
        };

        int[] prices={5000,75000,150000,600000,1800000};

        new AlertDialog.Builder(activity)
            .setTitle("Choose Vehicle")
            .setItems(names,(d,index) -> {
                if(money<prices[index]) {
                    error("Not enough money!");
                    return;
                }

                money-=prices[index];

                putString("ownedVehicle",
                    names[index].split(" - ")[0]);
                put("vehicleValue",prices[index]);

                updateMoney();
                refresh();
            })
            .setNegativeButton("CANCEL",null)
            .show();
    }

    void sellVehicle() {
        int value=get("vehicleValue",0);
        int sellingPrice=(int)(value*0.75);

        new AlertDialog.Builder(activity)
            .setTitle("Sell Vehicle")
            .setMessage("You will receive ₹"+sellingPrice)
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

    // ANNUAL FINANCIAL SIMULATION

    public static int applyAnnualFinances(
        SharedPreferences save,
        int currentMoney
    ) {
        long cash=currentMoney;

        int monthlyIncome=save.getInt("monthlyIncome",0);

        int debt=save.getInt("loanDebt",0);

        // Compatibility with old saves.
        if(debt>0 && save.getString("loanType","").isEmpty()) {
            save.edit()
                .putString("loanType","Personal Loan")
                .putFloat("loanRateExact",18f)
                .putInt("loanRate",180)
                .putInt("loanTenure",5)
                .putInt("loanEMI",
                    (int)Math.ceil(debt*0.025))
                .putInt("loanMonthsLeft",60)
                .putInt("loanOriginalAmount",debt)
                .putInt("loanActive",1)
                .apply();
        }

        int emi=save.getInt("loanEMI",0);
        int monthsLeft=save.getInt("loanMonthsLeft",0);
        int missed=save.getInt("missedPayments",0);
        int consecutive=save.getInt("consecutiveMissed",0);
        int credit=save.getInt("creditScore",700);
        int onTime=save.getInt("onTimePayments",0);

        float rate=save.getFloat(
            "loanRateExact",
            save.getInt("loanRate",180)/10f
        );

        String loanType=save.getString("loanType","");
        long totalPaid=save.getLong("loanTotalPaid",0);

        for(int month=0;month<12;month++) {

            // Credit monthly income.
            cash=safeStaticAdd(cash,monthlyIncome);

            // Household expenses are handled separately
            // by HouseholdManager. Do not deduct the old
            // generic monthlyExpenses value here.

            if(debt<=0 || emi<=0 || monthsLeft<=0) {
                continue;
            }

            double monthlyRate=rate/1200.0;
            long interest=(long)Math.ceil(debt*monthlyRate);
            long due=interest+emi;

            if(due<debt) {
                due=Math.min((long)emi+interest,debt+interest);
            }

            if(cash>=due) {

                cash-=due;

                long principalPaid=Math.max(0,due-interest);
                debt=(int)Math.max(0,debt-principalPaid);

                totalPaid+=due;
                onTime++;
                consecutive=0;

                credit=Math.min(900,credit+2);

            } else {

                debt=safeStaticInt(safeStaticAdd(debt,interest));

                long lateFee=Math.max(
                    100,(long)Math.ceil(emi*0.02)
                );

                debt=safeStaticInt(safeStaticAdd(debt,lateFee));

                missed++;
                consecutive++;

                credit=Math.max(300,credit-25);

                if(consecutive>=6 &&
                    (loanType.equals("Home Loan") ||
                     loanType.equals("Mortgage Loan") ||
                     loanType.equals("Vehicle Loan"))) {

                    if(loanType.equals("Vehicle Loan")) {
                        save.edit()
                            .putString("ownedVehicle","")
                            .putInt("vehicleValue",0)
                            .apply();
                    } else {
                        save.edit()
                            .putString("ownedHouse","")
                            .putInt("houseValue",0)
                            .putInt("houseLevel",0)
                            .apply();
                    }

                    credit=Math.max(300,credit-50);
                    consecutive=0;

                    save.edit()
                        .putInt("loanDebt",debt)
                        .putInt("creditScore",credit)
                        .putInt("missedPayments",missed)
                        .putInt("consecutiveMissed",consecutive)
                        .putLong("loanTotalPaid",totalPaid)
                        .apply();

                    continue;
                }
            }

            monthsLeft=Math.max(0,monthsLeft-1);

            if(debt<=0) {
                debt=0;
                monthsLeft=0;
            }
        }

        save.edit()
            .putInt("loanDebt",debt)
            .putInt("loanMonthsLeft",monthsLeft)
            .putInt("missedPayments",missed)
            .putInt("consecutiveMissed",consecutive)
            .putInt("creditScore",credit)
            .putInt("onTimePayments",onTime)
            .putLong("loanTotalPaid",totalPaid)
            .apply();

        if(debt<=0 && save.getInt("loanActive",0)==1) {
            save.edit()
                .putInt("loanDebt",0)
                .putInt("loanActive",0)
                .putString("loanType","")
                .putInt("loanEMI",0)
                .putInt("loanMonthsLeft",0)
                .apply();
        }

        return safeStaticInt(cash);
    }

    static long safeStaticAdd(long a,long b) {
        return Math.min(Integer.MAX_VALUE,
            Math.max(0L,a+b));
    }

    static int safeStaticInt(long value) {
        return (int)Math.min(Integer.MAX_VALUE,
            Math.max(0L,value));
    }
}
