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

import java.util.ArrayList;
import java.util.Random;

public class PropertyManager {

    public interface PropertyCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final int age;
    private int money;
    private int health;
    private int happiness;
    private final PropertyCallback callback;
    private final Random random = new Random();

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int grey = Color.rgb(185, 190, 185);

    private final ArrayList<Property> properties = new ArrayList<>();

    private static class Property {
        String name;
        String type;
        int value;
        int rent;
        int condition;
        int level;
        int purchasePrice;

        Property(
                String name,
                String type,
                int value,
                int rent,
                int condition,
                int level,
                int purchasePrice
        ) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.rent = rent;
            this.condition = condition;
            this.level = level;
            this.purchasePrice = purchasePrice;
        }
    }

    public PropertyManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            PropertyCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        loadProperties();
    }

    private void loadProperties() {
        properties.clear();

        int count = save.getInt("property_count", 0);

        for (int i = 0; i < count; i++) {
            String prefix = "property_" + i + "_";

            String name = save.getString(prefix + "name", "Property");
            String type = save.getString(prefix + "type", "House");

            int value = save.getInt(prefix + "value", 0);
            int rent = save.getInt(prefix + "rent", 0);
            int condition = save.getInt(prefix + "condition", 100);
            int level = save.getInt(prefix + "level", 1);
            int purchasePrice = save.getInt(prefix + "purchase", value);

            properties.add(new Property(
                    name,
                    type,
                    value,
                    rent,
                    condition,
                    level,
                    purchasePrice
            ));
        }
    }

    private void saveProperties() {
        SharedPreferences.Editor editor = save.edit();

        editor.putInt("property_count", properties.size());

        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            String prefix = "property_" + i + "_";

            editor.putString(prefix + "name", p.name);
            editor.putString(prefix + "type", p.type);
            editor.putInt(prefix + "value", p.value);
            editor.putInt(prefix + "rent", p.rent);
            editor.putInt(prefix + "condition", p.condition);
            editor.putInt(prefix + "level", p.level);
            editor.putInt(prefix + "purchase", p.purchasePrice);
        }

        editor.apply();
    }

    public void open() {
        showMainMenu();
    }

    private void showMainMenu() {
        LinearLayout root = createLayout();

        addTitle(root, "REAL ESTATE & PROPERTY");

        addText(root, "Age: " + age + " years", 14, grey);
        addText(root, "Available Money: " + formatMoney(money), 19, gold);
        addText(root, "Properties Owned: " + properties.size(), 15, white);

        addDivider(root);

        addButton(root, "🏠 BUY PROPERTY", this::showBuyMenu);
        addButton(root, "🏡 MY PROPERTIES", this::showPortfolio);
        addButton(root, "💰 COLLECT RENT", this::collectRent);
        addButton(root, "🔨 RENOVATE PROPERTY", this::showRenovationMenu);
        addButton(root, "🧾 PROPERTY TAX & MAINTENANCE", this::payExpenses);
        addButton(root, "📈 PROPERTY PORTFOLIO", this::showPortfolioSummary);

        addButton(root, "⬅ BACK TO GAME", () -> activity.recreate());

        activity.setContentView(wrap(root));
    }

    private void showBuyMenu() {
        String[] names = {
                "Small Apartment",
                "City Apartment",
                "Family House",
                "Luxury Villa",
                "Commercial Shop",
                "Agricultural Land",
                "Premium Plot"
        };

        String[] types = {
                "Apartment",
                "Apartment",
                "House",
                "Villa",
                "Commercial",
                "Land",
                "Land"
        };

        int[] prices = {
                500000,
                1200000,
                2500000,
                7500000,
                1800000,
                900000,
                3000000
        };

        int[] rents = {
                5000,
                12000,
                20000,
                60000,
                25000,
                3000,
                8000
        };

        String[] descriptions = new String[names.length];

        for (int i = 0; i < names.length; i++) {
            descriptions[i] = names[i]
                    + "\nPrice: " + formatMoney(prices[i])
                    + "\nEstimated monthly rent: " + formatMoney(rents[i]);
        }

        new AlertDialog.Builder(activity)
                .setTitle("Buy Property")
                .setItems(descriptions, (dialog, which) -> {
                    buyProperty(
                            names[which],
                            types[which],
                            prices[which],
                            rents[which]
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void buyProperty(
            String name,
            String type,
            int price,
            int rent
    ) {
        if (money < price) {
            message("Not Enough Money",
                    "You need " + formatMoney(price)
                            + " to purchase this property.");
            return;
        }

        money -= price;

        properties.add(new Property(
                name,
                type,
                price,
                rent,
                100,
                1,
                price
        ));

        saveProperties();

        update("Purchased " + name + " successfully!");
    }

    private void showPortfolio() {
        LinearLayout root = createLayout();

        addTitle(root, "MY PROPERTIES");

        if (properties.isEmpty()) {
            addText(root, "You don't own any properties yet.", 16, grey);
        } else {
            for (int i = 0; i < properties.size(); i++) {
                final int index = i;
                Property p = properties.get(i);

                addPropertyCard(root, p, index);
            }
        }

        addButton(root, "⬅ BACK", this::showMainMenu);

        activity.setContentView(wrap(root));
    }

    private void addPropertyCard(
            LinearLayout root,
            Property p,
            int index
    ) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackground(background(Color.rgb(29, 43, 39), 15));

        TextView title = makeText(p.name, 18, gold);
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        addCardText(card, "Type: " + p.type);
        addCardText(card, "Current Value: " + formatMoney(p.value));
        addCardText(card, "Monthly Rent: " + formatMoney(p.rent));
        addCardText(card, "Condition: " + p.condition + "%");
        addCardText(card, "Upgrade Level: " + p.level);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 8, 0, 8);

        root.addView(card, params);

        addButton(root, "MANAGE " + p.name, () -> manageProperty(index));
    }

    private void manageProperty(int index) {
        if (index < 0 || index >= properties.size()) {
            return;
        }

        Property p = properties.get(index);

        String details =
                "Property: " + p.name
                        + "\nType: " + p.type
                        + "\nValue: " + formatMoney(p.value)
                        + "\nMonthly Rent: " + formatMoney(p.rent)
                        + "\nCondition: " + p.condition + "%"
                        + "\nUpgrade Level: " + p.level;

        String[] options = {
                "Renovate",
                "Sell Property",
                "View Details"
        };

        new AlertDialog.Builder(activity)
                .setTitle(p.name)
                .setMessage(details)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        renovate(index);
                    } else if (which == 1) {
                        sellProperty(index);
                    } else {
                        manageProperty(index);
                    }
                })
                .setNegativeButton("Back", (d, w) -> showPortfolio())
                .show();
    }

    private void showRenovationMenu() {
        if (properties.isEmpty()) {
            message("No Properties", "Buy a property before renovating.");
            return;
        }

        String[] names = new String[properties.size()];

        for (int i = 0; i < properties.size(); i++) {
            names[i] = properties.get(i).name
                    + " - Level " + properties.get(i).level;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Choose Property")
                .setItems(names, (dialog, which) -> renovate(which))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void renovate(int index) {
        if (index < 0 || index >= properties.size()) {
            return;
        }

        Property p = properties.get(index);

        int cost = 50000 * p.level;

        new AlertDialog.Builder(activity)
                .setTitle("Renovate " + p.name)
                .setMessage(
                        "Renovation Cost: " + formatMoney(cost)
                                + "\n\nThis improves property condition, value and rent."
                )
                .setPositiveButton("Renovate", (dialog, which) -> {
                    if (money < cost) {
                        message("Not Enough Money", "You need " + formatMoney(cost));
                        return;
                    }

                    money -= cost;

                    p.level++;
                    p.condition = Math.min(100, p.condition + 20);
                    p.value += cost + (cost / 2);
                    p.rent += Math.max(1000, p.rent / 5);

                    saveProperties();

                    update("Renovation completed for " + p.name + "!");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void collectRent() {
        if (properties.isEmpty()) {
            message("No Properties", "You have no properties to collect rent from.");
            return;
        }

        int totalRent = 0;

        for (Property p : properties) {
            if (p.condition > 0 && !p.type.equals("Land")) {
                int actualRent = p.rent * p.condition / 100;
                totalRent += actualRent;
            }
        }

        if (totalRent <= 0) {
            message("No Rental Income", "Your properties are not generating rent.");
            return;
        }

        money += totalRent;

        update("You collected " + formatMoney(totalRent)
                + " in rental income.");
    }

    private void payExpenses() {
        if (properties.isEmpty()) {
            message("No Properties", "You don't have any property expenses.");
            return;
        }

        int taxes = 0;
        int maintenance = 0;

        for (Property p : properties) {
            taxes += Math.max(500, p.value / 100);
            maintenance += Math.max(500, (100 - p.condition) * 200);
        }

        int total = taxes + maintenance;

        new AlertDialog.Builder(activity)
                .setTitle("Property Expenses")
                .setMessage(
                        "Property Tax: " + formatMoney(taxes)
                                + "\nMaintenance: " + formatMoney(maintenance)
                                + "\nTotal: " + formatMoney(total)
                )
                .setPositiveButton("Pay Expenses", (dialog, which) -> {
                    if (money < total) {
                        message("Not Enough Money",
                                "You need " + formatMoney(total)
                                        + " to pay these expenses.");
                        return;
                    }

                    money -= total;

                    for (Property p : properties) {
                        p.condition = Math.min(100, p.condition + 15);
                    }

                    saveProperties();

                    update("Property taxes and maintenance paid successfully.");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sellProperty(int index) {
        if (index < 0 || index >= properties.size()) {
            return;
        }

        Property p = properties.get(index);

        int marketValue = p.value * p.condition / 100;

        int finalValue = marketValue
                + (marketValue * random.nextInt(21) / 100);

        int profit = finalValue - p.purchasePrice;

        new AlertDialog.Builder(activity)
                .setTitle("Sell " + p.name)
                .setMessage(
                        "Estimated Selling Price: " + formatMoney(finalValue)
                                + "\nPurchase Price: " + formatMoney(p.purchasePrice)
                                + "\nProfit/Loss: " + formatMoney(profit)
                )
                .setPositiveButton("Sell", (dialog, which) -> {
                    money += finalValue;
                    properties.remove(index);

                    saveProperties();

                    update("Sold " + p.name + " for "
                            + formatMoney(finalValue) + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPortfolioSummary() {
        int totalValue = 0;
        int monthlyRent = 0;
        int totalPurchase = 0;

        for (Property p : properties) {
            totalValue += p.value;
            totalRentAdd(p);
            monthlyRent += p.rent;
            totalPurchase += p.purchasePrice;
        }

        int profit = totalValue - totalPurchase;

        String report =
                "Properties Owned: " + properties.size()
                        + "\nTotal Market Value: " + formatMoney(totalValue)
                        + "\nTotal Purchase Cost: " + formatMoney(totalPurchase)
                        + "\nEstimated Portfolio Gain/Loss: " + formatMoney(profit)
                        + "\nPotential Monthly Rent: " + formatMoney(monthlyRent)
                        + "\nAnnual Rental Income: " + formatMoney(monthlyRent * 12);

        new AlertDialog.Builder(activity)
                .setTitle("PROPERTY PORTFOLIO")
                .setMessage(report)
                .setPositiveButton("OK", null)
                .show();
    }

    private void totalRentAdd(Property p) {
        // Reserved for future portfolio calculations.
    }

    private void update(String description) {
        saveProperties();

        if (callback != null) {
            callback.onUpdate(
                    money,
                    health,
                    happiness,
                    description
            );
        } else {
            showMainMenu();
        }
    }

    private void message(String title, String description) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("OK", null)
                .show();
    }

    private String formatMoney(int amount) {
        return "₹" + String.format("%,d", amount);
    }

    private LinearLayout createLayout() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 22, 18, 22);
        root.setBackgroundColor(dark);
        return root;
    }

    private ScrollView wrap(LinearLayout root) {
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);
        return scroll;
    }

    private void addTitle(LinearLayout root, String title) {
        TextView text = makeText(title, 23, gold);
        text.setTypeface(null, Typeface.BOLD);
        text.setGravity(Gravity.CENTER);
        text.setPadding(0, 12, 0, 20);
        root.addView(text);
    }

    private void addText(
            LinearLayout root,
            String value,
            int size,
            int color
    ) {
        TextView text = makeText(value, size, color);
        text.setPadding(4, 7, 4, 7);
        root.addView(text);
    }

    private void addCardText(LinearLayout root, String value) {
        TextView text = makeText(value, 14, white);
        text.setPadding(0, 5, 0, 5);
        root.addView(text);
    }

    private void addDivider(LinearLayout root) {
        TextView divider = new TextView(activity);
        divider.setBackgroundColor(Color.rgb(65, 80, 72));
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 1);
        params.setMargins(0, 14, 0, 14);
        root.addView(divider, params);
    }

    private void addButton(
            LinearLayout root,
            String label,
            Runnable action
    ) {
        TextView button = makeText(label, 15, dark);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(12, 16, 12, 16);
        button.setBackground(background(gold, 16));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 7, 0, 7);

        root.addView(button, params);
        button.setOnClickListener(v -> action.run());
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
        return text;
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
