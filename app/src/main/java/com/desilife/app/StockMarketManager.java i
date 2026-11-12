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

public class StockMarketManager {

    public interface StockCallback {
        void onUpdate(int money, int health, int happiness,
                      String description);
    }

    private Activity activity;
    private SharedPreferences save;
    private int age, money, health, happiness;
    private StockCallback callback;

    private Random random = new Random();

    private final int gold = Color.rgb(226,184,105);
    private final int dark = Color.rgb(12,25,23);
    private final int white = Color.WHITE;

    private String[] stockNames = {
            "Tata Motors",
            "Reliance Industries",
            "Infosys",
            "HDFC Bank",
            "ICICI Bank",
            "ITC",
            "TCS",
            "Wipro",
            "Adani Enterprises",
            "Hindustan Unilever"
    };

    private String[] symbols = {
            "TATAMOTORS",
            "RELIANCE",
            "INFY",
            "HDFCBANK",
            "ICICIBANK",
            "ITC",
            "TCS",
            "WIPRO",
            "ADANIENT",
            "HINDUNILVR"
    };

    private double[] prices = {
            950, 1450, 1800, 1650, 1100,
            450, 3900, 520, 2800, 2400
    };

    private int[] holdings = new int[10];

    private String marketStatus = "Normal";

    private int sipAmount;
    private int sipValue;
    private int sipMonths;

    private int ipoInvestment;
    private int ipoValue;

    public StockMarketManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            StockCallback callback
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

        for (int i = 0; i < 10; i++) {
            prices[i] = Double.longBitsToDouble(
                    save.getLong(
                            "stock_price_" + i,
                            Double.doubleToLongBits(prices[i])
                    )
            );

            holdings[i] = save.getInt(
                    "stock_holding_" + i, 0
            );
        }

        marketStatus = save.getString(
                "stock_market_status", "Normal"
        );

        sipAmount = save.getInt("stock_sip_amount", 0);
        sipValue = save.getInt("stock_sip_value", 0);
        sipMonths = save.getInt("stock_sip_months", 0);

        ipoInvestment = save.getInt("stock_ipo_investment", 0);
        ipoValue = save.getInt("stock_ipo_value", 0);
    }

    private void saveData() {

        SharedPreferences.Editor editor = save.edit();

        for (int i = 0; i < 10; i++) {
            editor.putLong(
                    "stock_price_" + i,
                    Double.doubleToLongBits(prices[i])
            );

            editor.putInt(
                    "stock_holding_" + i,
                    holdings[i]
            );
        }

        editor.putString("stock_market_status", marketStatus);

        editor.putInt("stock_sip_amount", sipAmount);
        editor.putInt("stock_sip_value", sipValue);
        editor.putInt("stock_sip_months", sipMonths);

        editor.putInt("stock_ipo_investment", ipoInvestment);
        editor.putInt("stock_ipo_value", ipoValue);

        editor.apply();
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
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private TextView makeText(
            String value,
            int size,
            int color
    ) {
        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setPadding(14, 12, 14, 12);
        return text;
    }

    private void addInfo(
            LinearLayout layout,
            String value
    ) {
        TextView text = makeText(value, 15, white);
        layout.addView(text);
    }

    private void addButton(
            LinearLayout layout,
            String label,
            Runnable action
    ) {
        TextView button = makeText(label, 16, dark);

        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setBackground(shape(gold, 18));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);

        layout.addView(button, params);

        button.setOnClickListener(v -> action.run());
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

        TextView heading = makeText(title, 23, gold);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);

        root.addView(heading);
        root.addView(content);

        addButton(root, "⬅ BACK TO STOCK MARKET", this::open);

        scroll.addView(root);
        activity.setContentView(scroll);
    }

    public void open() {

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        addInfo(content, "💰 Cash Balance: ₹" + money);
        addInfo(content, "📊 Market: " + marketStatus);

        addButton(content, "📈 STOCK EXCHANGE", this::showStocks);

        addButton(content, "🛒 BUY SHARES", this::buyShares);

        addButton(content, "💸 SELL SHARES", this::sellShares);

        addButton(content, "💼 MY PORTFOLIO", this::portfolio);

        addButton(content, "🏦 MUTUAL FUNDS & SIP", this::mutualFunds);

        addButton(content, "🚀 IPO INVESTMENTS", this::ipoMenu);

        addButton(content, "💵 COLLECT DIVIDENDS", this::collectDividends);

        addButton(content, "📅 ADVANCE MARKET ONE DAY",
                this::advanceMarket);

        addButton(content, "📊 MARKET NEWS", this::marketNews);

        showScreen("📈 STOCK MARKET & TRADING", content);
    }

    private void showStocks() {

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < stockNames.length; i++) {

            int change = (int) prices[i];

            addInfo(content,
                    stockNames[i] +
                    "\nNSE/BSE: " + symbols[i] +
                    "\nShare Price: ₹" + change
            );
        }

        showScreen("INDIAN STOCK EXCHANGE", content);
    }

    private void chooseStock(
            String title,
            StockAction action
    ) {

        String[] options = new String[stockNames.length];

        for (int i = 0; i < stockNames.length; i++) {
            options[i] = stockNames[i] +
                    " - ₹" + (int) prices[i];
        }

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(options, (dialog, which) -> {
                    action.run(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private interface StockAction {
        void run(int index);
    }

    private void buyShares() {

        if (age < 18) {
            message("You must be at least 18 to invest in stocks.");
            return;
        }

        chooseStock("Choose Stock", index -> {

            EditText input = new EditText(activity);
            input.setHint("Number of shares");
            input.setInputType(2);

            new AlertDialog.Builder(activity)
                    .setTitle("Buy " + stockNames[index])
                    .setMessage(
                            "Current price: ₹" + (int) prices[index]
                    )
                    .setView(input)
                    .setPositiveButton("BUY", (dialog, which) -> {

                        try {

                            int quantity = Integer.parseInt(
                                    input.getText().toString()
                            );

                            if (quantity <= 0 || quantity > 100000) {
                                message("Enter a valid quantity.");
                                return;
                            }

                            double cost = prices[index] * quantity;

                            if (cost > money) {
                                message("Insufficient cash balance.");
                                return;
                            }

                            holdings[index] += quantity;
                            money -= (int) cost;

                            update(
                                    "You bought " + quantity +
                                    " shares of " + stockNames[index] +
                                    " for ₹" + (int) cost + "."
                            );

                            message("Shares purchased successfully!");

                        } catch (Exception e) {
                            message("Enter a valid quantity.");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void sellShares() {

        chooseStock("Choose Stock to Sell", index -> {

            if (holdings[index] <= 0) {
                message("You don't own this stock.");
                return;
            }

            EditText input = new EditText(activity);
            input.setInputType(2);
            input.setHint("Number of shares");

            new AlertDialog.Builder(activity)
                    .setTitle("Sell " + stockNames[index])
                    .setMessage(
                            "Owned shares: " + holdings[index] +
                            "\nCurrent price: ₹" + (int) prices[index]
                    )
                    .setView(input)
                    .setPositiveButton("SELL", (dialog, which) -> {

                        try {

                            int quantity = Integer.parseInt(
                                    input.getText().toString()
                            );

                            if (quantity <= 0 ||
                                    quantity > holdings[index]) {
                                message("Invalid quantity.");
                                return;
                            }

                            int revenue = (int) (
                                    prices[index] * quantity
                            );

                            holdings[index] -= quantity;
                            money += revenue;

                            update(
                                    "You sold " + quantity +
                                    " shares of " + stockNames[index] +
                                    " for ₹" + revenue + "."
                            );

                            message("Shares sold successfully!");

                        } catch (Exception e) {
                            message("Enter a valid quantity.");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void portfolio() {

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        long totalValue = 0;
        int ownedStocks = 0;

        for (int i = 0; i < stockNames.length; i++) {

            int value = (int) (prices[i] * holdings[i]);

            totalValue += value;

            if (holdings[i] > 0) {

                ownedStocks++;

                addInfo(content,
                        stockNames[i] +
                        "\nShares: " + holdings[i] +
                        "\nCurrent Value: ₹" + value
                );
            }
        }

        addInfo(content, "Total Stock Value: ₹" + totalValue);
        addInfo(content, "Different Stocks Owned: " + ownedStocks);

        addInfo(content, "SIP Value: ₹" + sipValue);
        addInfo(content, "IPO Value: ₹" + ipoValue);

        addInfo(content,
                "Total Investment Assets: ₹" +
                (totalValue + sipValue + ipoValue)
        );

        showScreen("MY INVESTMENT PORTFOLIO", content);
    }

    private void mutualFunds() {

        String[] options = {
                "Start Monthly SIP",
                "Stop SIP",
                "View SIP Details"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Mutual Funds & SIP")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        startSIP();
                    } else if (which == 1) {
                        sipAmount = 0;
                        saveData();
                        message("Your SIP has been stopped.");
                    } else {
                        message(
                                "Monthly SIP: ₹" + sipAmount +
                                "\nSIP Value: ₹" + sipValue +
                                "\nMonths Invested: " + sipMonths
                        );
                    }
                })
                .show();
    }

    private void startSIP() {

        EditText input = new EditText(activity);
        input.setHint("Monthly SIP amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Start Monthly SIP")
                .setMessage("Enter your monthly investment.")
                .setView(input)
                .setPositiveButton("START", (dialog, which) -> {

                    try {

                        int amount = Integer.parseInt(
                                input.getText().toString()
                        );

                        if (amount < 500 || amount > 1000000) {
                            message("Minimum SIP is ₹500.");
                            return;
                        }

                        if (amount > money) {
                            message("Insufficient cash balance.");
                            return;
                        }

                        sipAmount = amount;

                        update("You started a monthly mutual fund SIP.");

                        message(
                                "SIP started successfully.\n" +
                                "Monthly investment: ₹" + amount
                        );

                    } catch (Exception e) {
                        message("Enter a valid amount.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void ipoMenu() {

        String[] options = {
                "Apply for IPO",
                "View IPO Investment"
        };

        new AlertDialog.Builder(activity)
                .setTitle("IPO Investment")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        applyIPO();
                    } else {
                        message(
                                "IPO Investment: ₹" + ipoInvestment +
                                "\nCurrent IPO Value: ₹" + ipoValue
                        );
                    }
                })
                .show();
    }

    private void applyIPO() {

        int[] amounts = {
                15000, 25000, 50000, 100000
        };

        String[] options = {
                "₹15,000",
                "₹25,000",
                "₹50,000",
                "₹1,00,000"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose IPO Investment")
                .setItems(options, (dialog, which) -> {

                    int amount = amounts[which];

                    if (money < amount) {
                        message("Insufficient cash balance.");
                        return;
                    }

                    money -= amount;
                    ipoInvestment += amount;

                    int returnPercent = random.nextInt(81) + 70;

                    ipoValue += amount * returnPercent / 100;

                    update(
                            "You invested ₹" + amount +
                            " in a new IPO."
                    );

                    message(
                            "IPO application successful!\n" +
                            "Initial estimated value: ₹" +
                            (amount * returnPercent / 100)
                    );
                })
                .show();
    }

    private void collectDividends() {

        int dividend = 0;

        for (int i = 0; i < holdings.length; i++) {

            int rate = random.nextInt(5) + 1;

            dividend += holdings[i] * rate;
        }

        if (dividend <= 0) {
            message("You have no shares eligible for dividends.");
            return;
        }

        money += dividend;

        update(
                "You received stock dividends of ₹" +
                dividend + "."
        );

        message("Dividend received: ₹" + dividend);
    }

    private void advanceMarket() {

        String[] marketTypes = {
                "Bull Market",
                "Bear Market",
                "Normal",
                "Market Crash",
                "Market Rally"
        };

        int market = random.nextInt(marketTypes.length);

        marketStatus = marketTypes[market];

        double multiplier;

        if (market == 0) {
            multiplier = 1.04;
        } else if (market == 1) {
            multiplier = 0.96;
        } else if (market == 3) {
            multiplier = 0.80;
        } else if (market == 4) {
            multiplier = 1.12;
        } else {
            multiplier = 1.0;
        }

        for (int i = 0; i < prices.length; i++) {

            double dailyChange =
                    0.90 + (random.nextDouble() * 0.20);

            prices[i] = prices[i] *
                    multiplier *
                    dailyChange;

            prices[i] = Math.max(1, prices[i]);
        }

        if (sipAmount > 0) {

            if (money >= sipAmount) {

                money -= sipAmount;

                double sipGrowth =
                        0.97 + (random.nextDouble() * 0.08);

                sipValue = (int) (
                        (sipValue + sipAmount) * sipGrowth
                );

                sipMonths++;

            } else {
                sipAmount = 0;
            }
        }

        if (ipoValue > 0) {

            double ipoGrowth =
                    0.90 + (random.nextDouble() * 0.20);

            ipoValue = (int) (ipoValue * ipoGrowth);
        }

        update(
                "Market moved to " + marketStatus +
                ". Share prices have changed."
        );

        new AlertDialog.Builder(activity)
                .setTitle("📊 Market Update")
                .setMessage(
                        "Today's market condition: " +
                        marketStatus +
                        "\n\nStock prices have been updated." +
                        "\nYour portfolio value may have changed."
                )
                .setPositiveButton("Continue", (d,w) -> open())
                .show();
    }

    private void marketNews() {

        String[] news = {
                "Indian markets are showing positive momentum.",
                "Banking stocks are seeing increased activity.",
                "Technology stocks are facing market pressure.",
                "Investors are watching corporate earnings.",
                "Market volatility is increasing.",
                "Foreign investors are changing their positions.",
                "The market is experiencing a strong rally."
        };

        String headline = news[random.nextInt(news.length)];

        message(
                "📰 MARKET NEWS\n\n" +
                headline +
                "\n\nMarket condition: " + marketStatus
        );
    }

    private void message(String value) {

        new AlertDialog.Builder(activity)
                .setTitle("Stock Market")
                .setMessage(value)
                .setPositiveButton("OK", null)
                .show();
    }
}
