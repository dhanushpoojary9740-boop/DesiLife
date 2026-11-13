
package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class BusinessManager {

    public interface BusinessCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    public interface FinancialEventListener {
        void onEvent(String category, long amount, String details, boolean cashMovement);
    }

    private FinancialEventListener financialEventListener;

    public void setFinancialEventListener(FinancialEventListener listener) {
        this.financialEventListener = listener;
    }

    private void recordEvent(String category, long amount, String details, boolean cashMovement) {
        if (amount != 0 && financialEventListener != null) {
            financialEventListener.onEvent(category, amount, details, cashMovement);
        }
    }

    private Activity activity;
    private SharedPreferences save;
    private int age, money, health, happiness;
    private BusinessCallback callback;
    private Random random = new Random();

    private int businessCount;
    private int loan;
    private ArrayList<String> businesses = new ArrayList<>();

    private final int gold = Color.rgb(226,184,105);
    private final int dark = Color.rgb(12,25,23);
    private final int white = Color.WHITE;

    private final String[] types = {
            "Restaurant", "Retail Shop", "Hotel",
            "Factory", "Supermarket", "Cafe",
            "Clothing Store", "Technology Company"
    };

    private final int[] startingCosts = {
            300000, 200000, 1500000, 2500000,
            800000, 250000, 350000, 2000000
    };

    public BusinessManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            BusinessCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        businessCount = save.getInt("business_count", 0);
        loan = save.getInt("business_loan", 0);

        loadBusinesses();
    }

    private void loadBusinesses() {
        businesses.clear();

        for (int i = 0; i < businessCount; i++) {
            businesses.add(save.getString(
                    "business_" + i,
                    "Small Business"
            ));
        }
    }

    private void saveBusinesses() {
        SharedPreferences.Editor editor = save.edit();

        editor.putInt("business_count", businessCount);
        editor.putInt("business_loan", loan);

        for (int i = 0; i < businesses.size(); i++) {
            editor.putString("business_" + i, businesses.get(i));
        }

        editor.apply();
    }

    private void update(
            int newMoney,
            int newHealth,
            int newHappiness,
            String description
    ) {
        money = Math.max(0, newMoney);
        health = Math.max(0, Math.min(100, newHealth));
        happiness = Math.max(0, Math.min(100, newHappiness));

        saveBusinesses();

        if (callback != null) {
            callback.onUpdate(
                    money,
                    health,
                    happiness,
                    description
            );
        }
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private TextView makeText(String text, int size, int color) {
        TextView view = new TextView(activity);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(14, 13, 14, 13);
        return view;
    }

    private void addButton(
            LinearLayout layout,
            String text,
            Runnable action
    ) {
        TextView button = makeText(text, 16, dark);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setBackground(shape(gold, 18));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1, -2
                );

        params.setMargins(0, 7, 0, 7);

        layout.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void addInfo(
            LinearLayout layout,
            String text
    ) {
        TextView view = makeText(text, 15, white);
        layout.addView(view);
    }

    private void showScreen(
            String title,
            LinearLayout content
    ) {
        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(dark);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 25);

        TextView heading = makeText(title, 24, gold);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);

        root.addView(heading);
        root.addView(content);

        addButton(root, "⬅ BACK TO BUSINESS", this::open);

        scroll.addView(root);
        activity.setContentView(scroll);
    }

    public void open() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        addInfo(content, "💰 Available Money: ₹" + money);
        addInfo(content, "🏢 Businesses Owned: " + businessCount);
        addInfo(content, "🏦 Business Loan: ₹" + loan);

        addButton(content, "➕ START A BUSINESS",
                this::chooseBusiness);

        addButton(content, "🏢 MY BUSINESS PORTFOLIO",
                this::portfolio);

        addButton(content, "👨‍💼 HIRE EMPLOYEES",
                this::hireEmployees);

        addButton(content, "📊 MANAGE BUSINESS",
                this::manageBusiness);

        addButton(content, "🌍 EXPAND BUSINESS",
                this::expandBusiness);

        addButton(content, "💳 BUSINESS LOAN",
                this::loanMenu);

        addButton(content, "📅 RUN BUSINESS FOR A YEAR",
                this::runBusiness);

        addButton(content, "💼 SELL A BUSINESS",
                this::sellBusiness);

        showScreen("🏢 BUSINESS & ENTREPRENEURSHIP", content);
    }

    private void chooseBusiness() {
        if (age < 18) {
            message("You must be at least 18 years old to start a business.");
            return;
        }

        String[] options = new String[types.length];

        for (int i = 0; i < types.length; i++) {
            options[i] = types[i] +
                    " - ₹" + startingCosts[i];
        }

        new AlertDialog.Builder(activity)
                .setTitle("Choose Your Business")
                .setItems(options, (dialog, which) -> {
                    enterBusinessDetails(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void enterBusinessDetails(int type) {
        LinearLayout form = new LinearLayout(activity);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(20, 10, 20, 10);

        EditText nameInput = new EditText(activity);
        nameInput.setHint("Business name");
        nameInput.setSingleLine(true);

        EditText cityInput = new EditText(activity);
        cityInput.setHint("City / Location");
        cityInput.setSingleLine(true);

        form.addView(nameInput);
        form.addView(cityInput);

        new AlertDialog.Builder(activity)
                .setTitle(types[type])
                .setMessage(
                        "Starting investment: ₹" +
                        startingCosts[type]
                )
                .setView(form)
                .setPositiveButton("Start Business", (dialog, which) -> {

                    String name = nameInput.getText()
                            .toString().trim();

                    String city = cityInput.getText()
                            .toString().trim();

                    if (name.isEmpty()) name = types[type];
                    if (city.isEmpty()) city = "Local City";

                    int cost = startingCosts[type];

                    if (money < cost) {
                        message("Not enough money to start this business.");
                        return;
                    }

                    String record = name + "|" +
                            types[type] + "|" +
                            city + "|1|50|0|" +
                            cost + "|100|100";

                    businesses.add(record);
                    businessCount++;
                    recordEvent("Business startup - " + name, -cost,
                            "Initial business investment", true);
                    recordEvent("Business asset acquired - " + name, cost,
                            "Business opened in " + city, false);

                    update(
                            money - cost,
                            health,
                            happiness + 3,
                            "You started " + name +
                            " in " + city + "."
                    );

                    message(
                            "Congratulations!\n\n" +
                            name + " is now open for business."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String[] getBusinessData(int index) {
        return businesses.get(index).split("\\|", -1);
    }

    private void setBusinessData(
            int index,
            String[] data
    ) {
        businesses.set(index, String.join("|", data));
        saveBusinesses();
    }

    private void portfolio() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        if (businessCount == 0) {
            addInfo(content, "You don't own any businesses yet.");
        }

        for (int i = 0; i < businesses.size(); i++) {
            String[] b = getBusinessData(i);

            if (b.length < 9) continue;

            addInfo(content,
                    "🏢 " + b[0] +
                    "\nType: " + b[1] +
                    "\nLocation: " + b[2] +
                    "\nBranches: " + b[3] +
                    "\nEmployees: " + b[4] +
                    "\nReputation: " + b[5] + "/100" +
                    "\nBusiness Value: ₹" + b[6] +
                    "\nProduct Price: ₹" + b[7] +
                    "\nEmployee Salary: ₹" + b[8]
            );
        }

        showScreen("MY BUSINESS PORTFOLIO", content);
    }

    private int selectedBusiness() {
        if (businessCount == 0) {
            message("You don't own a business.");
            return -1;
        }

        return -2;
    }

    private void chooseOwnedBusiness(
            String title,
            BusinessAction action
    ) {
        if (selectedBusiness() == -1) return;

        String[] options = new String[businesses.size()];

        for (int i = 0; i < businesses.size(); i++) {
            String[] b = getBusinessData(i);
            options[i] = b[0] + " (" + b[1] + ")";
        }

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(options, (dialog, which) -> {
                    action.run(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private interface BusinessAction {
        void run(int index);
    }

    private void hireEmployees() {
        chooseOwnedBusiness(
                "Select Business",
                index -> {

                    String[] b = getBusinessData(index);

                    EditText input = new EditText(activity);
                    input.setHint("Number of employees");
                    input.setInputType(2);

                    new AlertDialog.Builder(activity)
                            .setTitle("Hire Employees")
                            .setMessage(
                                    "Current employees: " + b[4]
                            )
                            .setView(input)
                            .setPositiveButton("Hire", (d, w) -> {

                                int count;

                                try {
                                    count = Integer.parseInt(
                                            input.getText().toString()
                                    );
                                } catch (Exception e) {
                                    count = 0;
                                }

                                if (count <= 0 || count > 500) {
                                    message("Enter a number between 1 and 500.");
                                    return;
                                }

                                int employees = Integer.parseInt(b[4]);
                                b[4] = String.valueOf(employees + count);

                                int hiringCost = count * 5000;

                                if (money < hiringCost) {
                                    message("Not enough money for hiring expenses.");
                                    return;
                                }

                                setBusinessData(index, b);

                                update(
                                        money - hiringCost,
                                        health,
                                        happiness,
                                        "You hired " + count +
                                        " employees for " + b[0] + "."
                                );
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
        );
    }

    private void manageBusiness() {
        chooseOwnedBusiness(
                "Choose Business to Manage",
                index -> {

                    String[] b = getBusinessData(index);

                    String[] options = {
                            "Set Product Price",
                            "Set Employee Salary",
                            "Improve Marketing",
                            "Improve Reputation"
                    };

                    new AlertDialog.Builder(activity)
                            .setTitle(b[0])
                            .setItems(options, (dialog, which) -> {

                                if (which == 0) {
                                    changeNumber(index, 7,
                                            "Set Product Price");
                                } else if (which == 1) {
                                    changeNumber(index, 8,
                                            "Set Employee Salary");
                                } else if (which == 2) {
                                    marketing(index);
                                } else {
                                    reputation(index);
                                }
                            })
                            .show();
                }
        );
    }

    private void changeNumber(
            int index,
            int field,
            String title
    ) {
        String[] b = getBusinessData(index);

        EditText input = new EditText(activity);
        input.setInputType(2);
        input.setHint("Enter amount");

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {

                    try {
                        int amount = Integer.parseInt(
                                input.getText().toString()
                        );

                        if (amount <= 0 || amount > 1000000) {
                            message("Enter a valid amount.");
                            return;
                        }

                        b[field] = String.valueOf(amount);
                        setBusinessData(index, b);

                        update(
                                money,
                                health,
                                happiness,
                                title + " updated for " + b[0] + "."
                        );

                    } catch (Exception e) {
                        message("Invalid amount.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void marketing(int index) {
        int cost = 25000;

        if (money < cost) {
            message("Not enough money for marketing.");
            return;
        }

        String[] b = getBusinessData(index);

        int reputation = Integer.parseInt(b[5]);
        b[5] = String.valueOf(
                Math.min(100, reputation + 10)
        );

        setBusinessData(index, b);

        update(
                money - cost,
                health,
                happiness,
                "You invested in marketing for " + b[0] + "."
        );
    }

    private void reputation(int index) {
        String[] b = getBusinessData(index);

        int reputation = Integer.parseInt(b[5]);

        if (reputation >= 100) {
            message("Your business already has maximum reputation.");
            return;
        }

        int cost = 15000;

        if (money < cost) {
            message("Not enough money.");
            return;
        }

        b[5] = String.valueOf(
                Math.min(100, reputation + 15)
        );

        setBusinessData(index, b);

        update(
                money - cost,
                health,
                happiness,
                "Your business reputation improved."
        );
    }

    private void expandBusiness() {
        chooseOwnedBusiness(
                "Choose Business to Expand",
                index -> {

                    String[] b = getBusinessData(index);

                    int cost = 200000;

                    if (money < cost) {
                        message("You need ₹" + cost +
                                " to open a new branch.");
                        return;
                    }

                    int branches = Integer.parseInt(b[3]);
                    b[3] = String.valueOf(branches + 1);

                    b[6] = String.valueOf(
                            Integer.parseInt(b[6]) + cost
                    );

                    setBusinessData(index, b);

                    update(
                            money - cost,
                            health,
                            happiness + 2,
                            "You opened a new branch of " + b[0] + "."
                    );
                }
        );
    }

    private void loanMenu() {
        String[] options = {
                "Borrow ₹1,00,000",
                "Borrow ₹5,00,000",
                "Borrow ₹10,00,000",
                "Repay Loan"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Business Banking")
                .setItems(options, (dialog, which) -> {

                    int amount = 0;

                    if (which == 0) amount = 100000;
                    if (which == 1) amount = 500000;
                    if (which == 2) amount = 1000000;

                    if (which == 3) {
                        repayLoan();
                        return;
                    }

                    if (loan + amount > 5000000) {
                        message("Your business loan limit is ₹50,00,000.");
                        return;
                    }

                    loan += amount;

                    update(
                            money + amount,
                            health,
                            happiness,
                            "You received a business loan of ₹" +
                                    amount + "."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void repayLoan() {
        if (loan <= 0) {
            message("You have no outstanding business loan.");
            return;
        }

        EditText input = new EditText(activity);
        input.setInputType(2);
        input.setHint("Repayment amount");

        new AlertDialog.Builder(activity)
                .setTitle("Repay Business Loan")
                .setMessage("Outstanding loan: ₹" + loan)
                .setView(input)
                .setPositiveButton("Repay", (dialog, which) -> {

                    try {
                        int amount = Integer.parseInt(
                                input.getText().toString()
                        );

                        if (amount <= 0 ||
                                amount > loan ||
                                amount > money) {
                            message("Invalid repayment amount.");
                            return;
                        }

                        loan -= amount;

                        update(
                                money - amount,
                                health,
                                happiness,
                                "You repaid ₹" + amount +
                                        " of your business loan."
                        );

                    } catch (Exception e) {
                        message("Enter a valid amount.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void runBusiness() {
        if (businessCount == 0) {
            message("Start a business first.");
            return;
        }

        int totalProfit = 0;
        int totalExpenses = 0;
        int successful = 0;

        for (int i = 0; i < businesses.size(); i++) {

            String[] b = getBusinessData(i);

            if (b.length < 9) continue;

            int branches = Integer.parseInt(b[3]);
            int employees = Integer.parseInt(b[4]);
            int reputation = Integer.parseInt(b[5]);
            int value = Integer.parseInt(b[6]);
            int price = Integer.parseInt(b[7]);
            int salary = Integer.parseInt(b[8]);

            int customers = random.nextInt(501) + 200;
            int revenue = customers * price *
                    branches;

            int expenses = (employees * salary * 12) +
                    (branches * 50000) +
                    (revenue / 4);

            int profit = revenue - expenses;

            int reputationChange = random.nextInt(11) - 5;

            if (profit > 0) {
                successful++;
                reputation += 3;
            } else {
                reputation -= 5;
            }

            reputation += reputationChange;
            reputation = Math.max(0, Math.min(100, reputation));

            b[5] = String.valueOf(reputation);

            int newValue = Math.max(
                    10000,
                    value + (profit / 2)
            );

            if (newValue != value) {
                recordEvent("Business valuation - " + b[0],
                        (long)newValue - value,
                        "Annual revenue ₹" + revenue
                                + "; operating expenses ₹" + expenses
                                + "; operating profit/loss ₹" + profit,
                        false);
            }

            b[6] = String.valueOf(newValue);

            setBusinessData(i, b);

            totalProfit += profit;
            totalExpenses += expenses;
        }

        int net = totalProfit;

        if (loan > 0) {
            int interest = loan / 10;
            net -= interest;
        }

        int updatedLoan = loan;

        if (loan > 0) {
            updatedLoan = Math.min(
                    5000000,
                    loan + loan / 10
            );
        }

        loan = updatedLoan;

        String result = "📊 BUSINESS YEARLY REPORT\n\n" +
                "Successful businesses: " + successful +
                "\nTotal operating expenses: ₹" + totalExpenses +
                "\nNet business result: ₹" + net +
                "\nOutstanding loan: ₹" + loan;

        int actualCashChange = Math.max(0, money + net) - money;
        recordEvent("Business annual net result", actualCashChange,
                "Combined yearly business revenue and operating expenses; "
                        + "operating expenses ₹" + totalExpenses
                        + "; loan interest is reflected in net result",
                true);

        update(
                money + net,
                health,
                happiness + (net >= 0 ? 3 : -3),
                result
        );

        new AlertDialog.Builder(activity)
                .setTitle("Annual Business Report")
                .setMessage(result)
                .setPositiveButton("Continue", (d, w) -> open())
                .show();
    }

    private void sellBusiness() {
        chooseOwnedBusiness(
                "Select Business to Sell",
                index -> {

                    String[] b = getBusinessData(index);

                    int value = Integer.parseInt(b[6]);

                    int salePrice = value +
                            (value * random.nextInt(21) / 100);

                    new AlertDialog.Builder(activity)
                            .setTitle("Sell " + b[0] + "?")
                            .setMessage(
                                    "Estimated sale price: ₹" +
                                    salePrice +
                                    "\n\nThis business will be permanently removed."
                            )
                            .setPositiveButton("Sell", (dialog, which) -> {

                                businesses.remove(index);
                                businessCount--;

                                update(
                                        money + salePrice,
                                        health,
                                        happiness,
                                        "You sold " + b[0] +
                                                " for ₹" + salePrice + "."
                                );

                                message("Business sold successfully.");
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
        );
    }

    private void message(String text) {
        new AlertDialog.Builder(activity)
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show();
    }
}
