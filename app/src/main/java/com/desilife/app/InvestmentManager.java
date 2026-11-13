package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class InvestmentManager {

    public interface MoneyListener {
        void onMoneyChanged(long newMoney);
    }

    private static class Investment {
        String name;
        long invested;
        long value;

        Investment(String name, long invested, long value) {
            this.name = name;
            this.invested = invested;
            this.value = value;
        }
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener listener;

    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private final ArrayList<Investment> investments =
            new ArrayList<>();

    private final ArrayList<String> history =
            new ArrayList<>();

    private final Random random = new Random();

    private long cash;
    private long totalDividends;
    private long totalRealizedProfit;
    private long totalRealizedLoss;

    private long sipAmount;
    private String sipType = "";
    private boolean sipActive;

    private LinearLayout content;

    private static final String PREF =
            "desilife_investment_data";

    public InvestmentManager(Activity activity,
                             long currentCash,
                             MoneyListener listener) {

        this.activity = activity;
        this.cash = currentCash;
        this.listener = listener;

        prefs = activity.getSharedPreferences(
                PREF, Activity.MODE_PRIVATE);

        loadData();
    }

    // ---------------- LOAD DATA ----------------

    private void loadData() {

        totalDividends = prefs.getLong("dividends", 0);
        totalRealizedProfit = prefs.getLong("realized_profit", 0);
        totalRealizedLoss = prefs.getLong("realized_loss", 0);

        sipAmount = prefs.getLong("sip_amount", 0);
        sipType = prefs.getString("sip_type", "");
        sipActive = prefs.getBoolean("sip_active", false);

        int count = prefs.getInt("investment_count", 0);

        for (int i = 0; i < count; i++) {

            String name = prefs.getString(
                    "investment_name_" + i, "");

            long invested = prefs.getLong(
                    "investment_invested_" + i, 0);

            long value = prefs.getLong(
                    "investment_value_" + i, 0);

            if (!name.isEmpty() && invested > 0) {
                investments.add(
                        new Investment(name, invested, value));
            }
        }

        int historyCount = prefs.getInt("history_count", 0);

        for (int i = 0; i < historyCount; i++) {

            String item = prefs.getString(
                    "history_" + i, null);

            if (item != null) {
                history.add(item);
            }
        }
    }

    // ---------------- SAVE DATA ----------------

    private void saveData() {

        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("dividends", totalDividends);
        editor.putLong("realized_profit", totalRealizedProfit);
        editor.putLong("realized_loss", totalRealizedLoss);

        editor.putLong("sip_amount", sipAmount);
        editor.putString("sip_type", sipType);
        editor.putBoolean("sip_active", sipActive);

        editor.putInt("investment_count", investments.size());

        for (int i = 0; i < investments.size(); i++) {

            Investment item = investments.get(i);

            editor.putString("investment_name_" + i, item.name);
            editor.putLong("investment_invested_" + i,
                    item.invested);
            editor.putLong("investment_value_" + i,
                    item.value);
        }

        editor.putInt("history_count", history.size());

        for (int i = 0; i < history.size(); i++) {
            editor.putString("history_" + i, history.get(i));
        }

        editor.apply();
    }

    // ---------------- BASIC METHODS ----------------

    private String money(long amount) {
        return currency.format(amount);
    }

    private long safeAdd(long first, long second) {

        if (second > 0 && first > Long.MAX_VALUE - second) {
            return Long.MAX_VALUE;
        }

        if (second < 0 && first < Long.MIN_VALUE - second) {
            return Long.MIN_VALUE;
        }

        return first + second;
    }

    private void updateCash(long amount) {

        cash = Math.max(0, amount);

        if (listener != null) {
            listener.onMoneyChanged(cash);
        }
    }

    public void setCash(long amount) {
        cash = Math.max(0, amount);
    }

    public long getCash() {
        return cash;
    }

    public long getPortfolioValue() {

        long total = 0;

        for (Investment item : investments) {
            total = safeAdd(total, item.value);
        }

        return total;
    }

    public long getTotalInvested() {

        long total = 0;

        for (Investment item : investments) {
            total = safeAdd(total, item.invested);
        }

        return total;
    }

    public long getProfitLoss() {
        return getPortfolioValue() - getTotalInvested();
    }

    public long getTotalDividends() {
        return totalDividends;
    }

    private void addHistory(String message) {

        history.add(0, message);

        while (history.size() > 50) {
            history.remove(history.size() - 1);
        }

        saveData();
    }

    private void toast(String message) {

        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show();
    }

    // ---------------- UI HELPERS ----------------

    private TextView text(String value,
                          int size,
                          boolean bold) {

        TextView t = new TextView(activity);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(bold ? Color.rgb(239, 202, 126) : Color.rgb(222, 231, 222));

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    private GradientDrawable background(int color) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(20);

        return drawable;
    }

    private void addSpace(int height) {

        View space = new View(activity);

        content.addView(space,
                new LinearLayout.LayoutParams(1, height));
    }

    private void addCard(String title, String value) {

        LinearLayout card = new LinearLayout(activity);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackground(background(
                Color.rgb(28, 58, 50)));

        card.addView(text(title, 14, false));
        card.addView(text(value, 22, true));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = 10;

        content.addView(card, params);
    }

    private void addButton(String label,
                           View.OnClickListener action) {

        Button button = new Button(activity);

        button.setText(label);
        button.setTextColor(Color.rgb(25, 48, 39));
        button.setAllCaps(false);
        button.setBackground(background(
                Color.rgb(239, 202, 126)));
        button.setElevation(3);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = 8;

        content.addView(button, params);

        button.setOnClickListener(action);
    }

    // ---------------- MAIN SCREEN ----------------

    public void show() {

        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(Color.rgb(12, 31, 27));

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(18, 18, 18, 26);
        content.setBackgroundColor(Color.rgb(12, 31, 27));

        scroll.addView(content);

        content.addView(text("📈  INVESTMENTS", 25, true));

        content.addView(text(
                "Build wealth through stocks, funds and gold",
                14, false));

        addSpace(18);

        addCard("Available Cash", money(cash));
        addCard("Portfolio Value", money(getPortfolioValue()));
        addCard("Total Invested", money(getTotalInvested()));

        long profitLoss = getProfitLoss();

        addCard("Profit / Loss",
                (profitLoss >= 0 ? "+" : "")
                        + money(profitLoss));

        addCard("Dividends Earned", money(totalDividends));

        addSpace(12);

        addButton("Explore Investments",
                v -> showInvestmentTypes());

        addButton("My Portfolio",
                v -> showPortfolio());

        addButton("Sell Investments",
                v -> showSellOptions());

        addButton("Start / Manage SIP",
                v -> showSIPMenu());

        addButton("Market Update",
                v -> showMarketUpdate());

        addButton("Investment History",
                v -> showHistory());

        new AlertDialog.Builder(activity)
                .setTitle("Investments")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- INVESTMENT TYPES ----------------

    private void showInvestmentTypes() {

        String[] types = {
                "Indian Stocks",
                "Mutual Funds",
                "Gold"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Investment")
                .setItems(types, (dialog, which) -> {

                    String type = types[which];

                    showInvestmentOptions(type);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- INVESTMENT OPTIONS ----------------

    private void showInvestmentOptions(String type) {

        String[] options;

        if (type.equals("Indian Stocks")) {

            options = new String[]{
                    "Large Cap Stocks",
                    "Mid Cap Stocks",
                    "Small Cap Stocks",
                    "High Growth Stocks"
            };

        } else if (type.equals("Mutual Funds")) {

            options = new String[]{
                    "Index Fund",
                    "Large Cap Fund",
                    "Flexi Cap Fund",
                    "Mid Cap Fund",
                    "Small Cap Fund",
                    "Debt Fund"
            };

        } else {

            options = new String[]{
                    "Digital Gold",
                    "Gold ETF",
                    "Gold Savings Fund"
            };
        }

        new AlertDialog.Builder(activity)
                .setTitle(type)
                .setItems(options, (dialog, which) -> {

                    String selected = options[which];

                    showBuyDialog(type, selected);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- BUY INVESTMENT ----------------

    private void showBuyDialog(String type, String name) {

        EditText input = new EditText(activity);

        input.setHint("Investment amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Buy " + name)
                .setMessage("Available cash: " + money(cash))
                .setView(input)
                .setPositiveButton("Invest", (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount < 100 || amount > cash) {
                        toast("Enter a valid amount");
                        return;
                    }

                    buyInvestment(name, amount);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void buyInvestment(String name, long amount) {

        Investment existing = findInvestment(name);

        updateCash(cash - amount);

        if (existing == null) {

            investments.add(new Investment(
                    name, amount, amount));

        } else {

            existing.invested = safeAdd(existing.invested, amount);
            existing.value = safeAdd(existing.value, amount);
        }

        addHistory("Invested " + money(amount)
                + " in " + name);

        saveData();

        toast("Investment successful");
    }

    private Investment findInvestment(String name) {

        for (Investment item : investments) {

            if (item.name.equals(name)) {
                return item;
            }
        }

        return null;
    }

    // ---------------- PORTFOLIO ----------------

    private void showPortfolio() {

        if (investments.isEmpty()) {
            toast("Your portfolio is empty");
            return;
        }

        String[] names = new String[investments.size()];

        for (int i = 0; i < investments.size(); i++) {

            Investment item = investments.get(i);

            long profit = item.value - item.invested;

            names[i] = item.name
                    + "\nValue: " + money(item.value)
                    + "\nP/L: " + money(profit);
        }

        new AlertDialog.Builder(activity)
                .setTitle("My Portfolio")
                .setItems(names, (dialog, which) -> {

                    showInvestmentDetails(which);
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showInvestmentDetails(int index) {

        if (index < 0 || index >= investments.size()) {
            return;
        }

        Investment item = investments.get(index);

        long profit = item.value - item.invested;

        String message =
                "Investment: " + item.name
                + "\nTotal Invested: " + money(item.invested)
                + "\nCurrent Value: " + money(item.value)
                + "\nProfit / Loss: " + money(profit);

        new AlertDialog.Builder(activity)
                .setTitle("Investment Details")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- SELL INVESTMENTS ----------------

    private void showSellOptions() {

        if (investments.isEmpty()) {
            toast("Nothing to sell");
            return;
        }

        String[] names = new String[investments.size()];

        for (int i = 0; i < investments.size(); i++) {

            Investment item = investments.get(i);

            names[i] = item.name
                    + " - " + money(item.value);
        }

        new AlertDialog.Builder(activity)
                .setTitle("Select Investment to Sell")
                .setItems(names, (dialog, which) -> {

                    sellInvestment(which);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sellInvestment(int index) {

        if (index < 0 || index >= investments.size()) {
            return;
        }

        Investment item = investments.get(index);

        long saleValue = item.value;
        long profit = saleValue - item.invested;

        new AlertDialog.Builder(activity)
                .setTitle("Sell " + item.name)
                .setMessage(
                        "Invested: " + money(item.invested)
                        + "\nCurrent value: " + money(saleValue)
                        + "\nProfit / Loss: " + money(profit)
                        + "\n\nSell this investment?")
                .setPositiveButton("Sell", (dialog, which) -> {

                    updateCash(safeAdd(cash, saleValue));

                    if (profit >= 0) {
                        totalRealizedProfit =
                                safeAdd(totalRealizedProfit, profit);
                    } else {
                        totalRealizedLoss =
                                safeAdd(totalRealizedLoss, Math.abs(profit));
                    }

                    addHistory("Sold " + item.name
                            + " for " + money(saleValue));

                    investments.remove(index);

                    saveData();

                    toast("Investment sold");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- SIP ----------------

    private void showSIPMenu() {

        String[] options = {
                "Start SIP",
                "View SIP",
                "Stop SIP"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Systematic Investment Plan")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        startSIP();
                    } else if (which == 1) {
                        viewSIP();
                    } else {
                        stopSIP();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startSIP() {

        if (sipActive) {
            toast("You already have an active SIP");
            return;
        }

        String[] types = {
                "Index Fund",
                "Large Cap Fund",
                "Flexi Cap Fund",
                "Mid Cap Fund",
                "Small Cap Fund",
                "Debt Fund"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose SIP Fund")
                .setItems(types, (dialog, which) -> {

                    sipType = types[which];

                    enterSIPAmount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void enterSIPAmount() {

        EditText input = new EditText(activity);

        input.setHint("Monthly SIP amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Monthly SIP")
                .setMessage("SIP starts from the next Age Up.")
                .setView(input)
                .setPositiveButton("Start SIP",
                        (dialog, which) -> {

                    long amount = readAmount(input);

                    if (amount < 500 || amount > 100000) {
                        toast("Enter ₹500 to ₹1,00,000");
                        return;
                    }

                    sipAmount = amount;
                    sipActive = true;

                    saveData();

                    addHistory("Started SIP of "
                            + money(amount)
                            + " in " + sipType);

                    toast("SIP started");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void viewSIP() {

        String message;

        if (!sipActive) {

            message = "No active SIP.";

        } else {

            message =
                    "Fund: " + sipType
                    + "\nMonthly amount: " + money(sipAmount)
                    + "\nStatus: Active";
        }

        new AlertDialog.Builder(activity)
                .setTitle("Your SIP")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void stopSIP() {

        if (!sipActive) {
            toast("No active SIP");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Stop SIP")
                .setMessage("Stop your monthly SIP?")
                .setPositiveButton("Stop", (dialog, which) -> {

                    sipActive = false;

                    addHistory("Stopped SIP in " + sipType);

                    sipAmount = 0;
                    sipType = "";

                    saveData();

                    toast("SIP stopped");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- MONTHLY INVESTMENT UPDATE ----------------

    /*
     * Processes exactly ONE in-game month:
     *
     * 1. One SIP installment, if active and affordable.
     * 2. One month of market movement for every investment.
     * 3. One month's equivalent of expected annual dividends.
     *
     * Annual return assumptions remain unchanged.
     */

    public void applyMonthlyInvestmentUpdate() {

        // One SIP installment per month.

        if (sipActive && sipAmount > 0) {

            if (cash >= sipAmount) {

                updateCash(cash - sipAmount);

                Investment item = findInvestment(sipType);

                if (item == null) {

                    investments.add(new Investment(
                            sipType, sipAmount, sipAmount));

                } else {

                    item.invested = safeAdd(
                            item.invested, sipAmount);

                    item.value = safeAdd(
                            item.value, sipAmount);
                }

                addHistory("Monthly SIP installment invested: "
                        + money(sipAmount)
                        + " in " + sipType);

            } else {

                addHistory("Monthly SIP installment missed: "
                        + money(sipAmount));
            }
        }

        // Apply one month's market movement.

        for (Investment item : investments) {

            double annualRate = getAnnualReturn(item.name);

            double expectedMonthlyRate =
                    Math.pow(1.0 + annualRate, 1.0 / 12.0) - 1.0;

            // Annual random volatility is scaled to monthly volatility.
            double monthlyVolatility = 0.15 / Math.sqrt(12.0);

            double movement = expectedMonthlyRate
                    + (random.nextDouble() * 2.0 - 1.0)
                    * monthlyVolatility;

            long oldValue = item.value;

            item.value = Math.max(0,
                    Math.round(item.value * (1.0 + movement)));

            long change = item.value - oldValue;

            addHistory(item.name
                    + " monthly market change: "
                    + (change >= 0 ? "+" : "")
                    + money(change));

            // Monthly equivalent of the existing annual stock dividend range.
            if (item.name.contains("Stocks")
                    && item.value > 0) {

                double annualDividendRate =
                        0.01 + random.nextDouble() * 0.02;

                long dividend = Math.round(
                        item.value * annualDividendRate / 12.0);

                if (dividend > 0) {

                    updateCash(safeAdd(cash, dividend));

                    totalDividends = safeAdd(
                            totalDividends, dividend);

                    addHistory("Monthly stock dividend received: "
                            + money(dividend));
                }
            }
        }

        saveData();
    }

    // ---------------- ANNUAL MARKET SIMULATION ----------------

    /*
     * Compatibility method.
     * Processes 12 separate monthly investment updates.
     */

    public void applyAnnualMarketChanges() {

        for (int month = 0; month < 12; month++) {
            applyMonthlyInvestmentUpdate();
        }

        addHistory("Annual investment simulation completed.");

        saveData();
    }

    // ---------------- EXPECTED ANNUAL RETURNS ----------------

    private double getAnnualReturn(String name) {

        if (name.contains("Large Cap Stocks")) {
            return 0.10;
        }

        if (name.contains("Mid Cap Stocks")) {
            return 0.12;
        }

        if (name.contains("Small Cap Stocks")) {
            return 0.14;
        }

        if (name.contains("High Growth Stocks")) {
            return 0.16;
        }

        if (name.contains("Index Fund")) {
            return 0.10;
        }

        if (name.contains("Large Cap Fund")) {
            return 0.10;
        }

        if (name.contains("Flexi Cap Fund")) {
            return 0.11;
        }

        if (name.contains("Mid Cap Fund")) {
            return 0.12;
        }

        if (name.contains("Small Cap Fund")) {
            return 0.14;
        }

        if (name.contains("Debt Fund")) {
            return 0.06;
        }

        if (name.contains("Gold")) {
            return 0.08;
        }

        return 0.07;
    }

    // ---------------- MARKET UPDATE ----------------

    private void showMarketUpdate() {

        String message =
                "Market simulation uses fictional game returns."
                + "\n\nLarge Cap Stocks: Expected 10%"
                + "\nMid Cap Stocks: Expected 12%"
                + "\nSmall Cap Stocks: Expected 14%"
                + "\nHigh Growth Stocks: Expected 16%"
                + "\nIndex Fund: Expected 10%"
                + "\nFlexi Cap Fund: Expected 11%"
                + "\nDebt Fund: Expected 6%"
                + "\nGold: Expected 8%"
                + "\n\nActual returns can be positive or negative.";

        new AlertDialog.Builder(activity)
                .setTitle("Market Update")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- HISTORY ----------------

    private void showHistory() {

        if (history.isEmpty()) {
            toast("No investment history available");
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String item : history) {

            builder.append("• ")
                    .append(item)
                    .append("\n\n");
        }

        TextView view = text(builder.toString(), 14, false);
        view.setPadding(20, 20, 20, 20);

        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(Color.rgb(12, 31, 27));
        scroll.addView(view);

        new AlertDialog.Builder(activity)
                .setTitle("Investment History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .show();
    }

    // ---------------- INPUT ----------------

    private long readAmount(EditText input) {

        try {
            return Long.parseLong(
                    input.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
