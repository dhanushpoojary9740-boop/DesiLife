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

public class VehicleManager {

    public interface VehicleCallback {
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
    private final VehicleCallback callback;
    private final Random random = new Random();

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;

    private final ArrayList<String> garage = new ArrayList<>();

    private boolean hasLicence;
    private boolean licenceTestAttempted;
    private int fines;
    private int fuel;
    private int vehicleCondition;
    private String vehicleType;
    private String vehicleName;

    public VehicleManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            VehicleCallback callback
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
        hasLicence = save.getBoolean("vehicle_licence", false);
        licenceTestAttempted = save.getBoolean("vehicle_test_attempted", false);
        fines = save.getInt("vehicle_fines", 0);
        fuel = save.getInt("vehicle_fuel", 100);
        vehicleCondition = save.getInt("vehicle_condition", 100);
        vehicleType = save.getString("vehicle_type", "None");
        vehicleName = save.getString("vehicle_name", "No Vehicle");

        int count = save.getInt("vehicle_count", 0);

        garage.clear();

        for (int i = 0; i < count; i++) {
            String vehicle = save.getString("vehicle_" + i, null);

            if (vehicle != null) {
                garage.add(vehicle);
            }
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = save.edit();

        editor.putBoolean("vehicle_licence", hasLicence);
        editor.putBoolean("vehicle_test_attempted", licenceTestAttempted);
        editor.putInt("vehicle_fines", fines);
        editor.putInt("vehicle_fuel", fuel);
        editor.putInt("vehicle_condition", vehicleCondition);
        editor.putString("vehicle_type", vehicleType);
        editor.putString("vehicle_name", vehicleName);
        editor.putInt("vehicle_count", garage.size());

        for (int i = 0; i < garage.size(); i++) {
            editor.putString("vehicle_" + i, garage.get(i));
        }

        editor.apply();
    }

    public void open() {
        showGarage();
    }

    private void showGarage() {
        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(dark);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 25, 20, 25);

        scroll.addView(root);

        TextView title = text("🚗 DRIVING & VEHICLES", 24, gold);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        addSpace(root, 15);

        addInfo(root, "Money: ₹" + money);
        addInfo(root, "Driving Licence: " + (hasLicence ? "Valid" : "Not Available"));
        addInfo(root, "Vehicles Owned: " + garage.size());
        addInfo(root, "Traffic Fines: ₹" + fines);

        addSpace(root, 15);

        addButton(root, "🪪 APPLY FOR DRIVING LICENCE", this::applyForLicence);
        addButton(root, "🚘 BUY A VEHICLE", this::buyVehicle);
        addButton(root, "🛠️ VEHICLE MAINTENANCE", this::maintenance);
        addButton(root, "⛽ REFUEL VEHICLE", this::refuel);
        addButton(root, "🚦 DRIVE VEHICLE", this::driveVehicle);
        addButton(root, "👮 TRAFFIC POLICE CHECK", this::policeCheck);
        addButton(root, "💰 SELL VEHICLE", this::sellVehicle);
        addButton(root, "🏠 VIEW GARAGE", this::viewGarage);

        addSpace(root, 15);

        addButton(root, "⬅ BACK", () -> activity.recreate());

        activity.setContentView(scroll);
    }

    private void applyForLicence() {
        if (hasLicence) {
            message("Driving Licence", "You already have a valid driving licence.");
            return;
        }

        if (age < 18) {
            message("Not Eligible", "You must be at least 18 years old to apply.");
            return;
        }

        if (licenceTestAttempted) {
            message("Driving Test", "You have already attempted the test. You can try again after one year.");
            return;
        }

        if (money < 1500) {
            message("Insufficient Money", "You need ₹1,500 for the driving test.");
            return;
        }

        money -= 1500;
        licenceTestAttempted = true;

        boolean passed = random.nextInt(100) < 70;

        if (passed) {
            hasLicence = true;
            saveData();
            update("You passed your driving test and received your driving licence!");
        } else {
            saveData();
            update("You failed the driving test. You can try again next year.");
        }
    }

    private void buyVehicle() {
        String[] types = {
                "Scooter - ₹45,000",
                "Motorcycle - ₹85,000",
                "Used Car - ₹2,00,000",
                "New Car - ₹6,00,000",
                "Luxury Car - ₹25,00,000"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Your Vehicle")
                .setItems(types, (dialog, which) -> {
                    String name;
                    int price;
                    String type;

                    switch (which) {
                        case 0:
                            name = "Scooter";
                            price = 45000;
                            type = "Two Wheeler";
                            break;
                        case 1:
                            name = "Motorcycle";
                            price = 85000;
                            type = "Two Wheeler";
                            break;
                        case 2:
                            name = "Used Car";
                            price = 200000;
                            type = "Car";
                            break;
                        case 3:
                            name = "New Car";
                            price = 600000;
                            type = "Car";
                            break;
                        default:
                            name = "Luxury Car";
                            price = 2500000;
                            type = "Luxury Car";
                            break;
                    }

                    if (money < price) {
                        message("Not Enough Money", "You need ₹" + price + " to buy this vehicle.");
                        return;
                    }

                    if (!hasLicence) {
                        message("Driving Licence Required", "Get your driving licence before buying a vehicle.");
                        return;
                    }

                    money -= price;
                    garage.add(name);
                    vehicleName = name;
                    vehicleType = type;
                    vehicleCondition = 100;
                    fuel = 100;

                    saveData();
                    update("You purchased a " + name + " for ₹" + price + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void maintenance() {
        if (garage.isEmpty()) {
            message("Garage Empty", "Buy a vehicle first.");
            return;
        }

        if (vehicleCondition >= 100) {
            message("Vehicle Condition", "Your vehicle is already in excellent condition.");
            return;
        }

        if (money < 3000) {
            message("Insufficient Money", "You need ₹3,000 for maintenance.");
            return;
        }

        money -= 3000;
        vehicleCondition = 100;

        saveData();
        update("Your vehicle has been fully serviced. Maintenance cost: ₹3,000.");
    }

    private void refuel() {
        if (garage.isEmpty()) {
            message("Garage Empty", "You don't own any vehicles.");
            return;
        }

        if (fuel >= 100) {
            message("Fuel", "Your vehicle already has a full tank.");
            return;
        }

        if (money < 1000) {
            message("Insufficient Money", "You need ₹1,000 to refuel.");
            return;
        }

        money -= 1000;
        fuel = 100;

        saveData();
        update("Your vehicle has been refuelled. Cost: ₹1,000.");
    }

    private void driveVehicle() {
        if (garage.isEmpty()) {
            message("No Vehicle", "You don't own a vehicle.");
            return;
        }

        if (!hasLicence) {
            message("No Licence", "You cannot legally drive without a licence.");
            return;
        }

        if (fuel < 10) {
            message("Low Fuel", "Refuel your vehicle before driving.");
            return;
        }

        if (vehicleCondition < 20) {
            message("Vehicle Damage", "Your vehicle needs maintenance before driving.");
            return;
        }

        fuel = Math.max(0, fuel - 10);

        int event = random.nextInt(100);

        if (event < 8) {
            int damage = random.nextInt(20) + 10;
            vehicleCondition = Math.max(0, vehicleCondition - damage);
            health = Math.max(1, health - 10);
            happiness = Math.max(0, happiness - 5);

            saveData();
            update("You were involved in a minor road accident. Vehicle damage: " + damage + "%.");
        } else if (event < 18) {
            vehicleCondition = Math.max(0, vehicleCondition - 5);
            saveData();
            update("You had a minor vehicle problem during your journey.");
        } else {
            happiness = Math.min(100, happiness + 3);
            saveData();
            update("You enjoyed a peaceful drive. Fuel used: 10%.");
        }
    }

    private void policeCheck() {
        if (garage.isEmpty()) {
            message("Police Check", "You don't own a vehicle.");
            return;
        }

        if (!hasLicence || random.nextInt(100) < 12) {
            int fine = random.nextInt(2000) + 500;
            fines += fine;
            money = Math.max(0, money - fine);

            saveData();
            update("Traffic police issued you a fine of ₹" + fine + ".");
        } else {
            saveData();
            update("Police checked your documents. Everything is in order.");
        }
    }

    private void sellVehicle() {
        if (garage.isEmpty()) {
            message("Garage Empty", "You have no vehicle to sell.");
            return;
        }

        String[] vehicles = garage.toArray(new String[0]);

        new AlertDialog.Builder(activity)
                .setTitle("Sell Vehicle")
                .setItems(vehicles, (dialog, which) -> {
                    String selected = garage.get(which);

                    int value;

                    if (selected.equals("Scooter")) {
                        value = 25000;
                    } else if (selected.equals("Motorcycle")) {
                        value = 45000;
                    } else if (selected.equals("Used Car")) {
                        value = 100000;
                    } else if (selected.equals("New Car")) {
                        value = 400000;
                    } else {
                        value = 1500000;
                    }

                    garage.remove(which);
                    money += value;

                    if (garage.isEmpty()) {
                        vehicleName = "No Vehicle";
                        vehicleType = "None";
                        vehicleCondition = 100;
                        fuel = 100;
                    } else {
                        vehicleName = garage.get(0);
                    }

                    saveData();
                    update("You sold your " + selected + " for ₹" + value + ".");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void viewGarage() {
        StringBuilder details = new StringBuilder();

        details.append("Vehicles Owned: ").append(garage.size()).append("\n\n");

        if (garage.isEmpty()) {
            details.append("Your garage is empty.");
        } else {
            for (int i = 0; i < garage.size(); i++) {
                details.append(i + 1)
                        .append(". ")
                        .append(garage.get(i))
                        .append("\n");
            }

            details.append("\nActive Vehicle: ").append(vehicleName);
            details.append("\nVehicle Condition: ").append(vehicleCondition).append("%");
            details.append("\nFuel: ").append(fuel).append("%");
        }

        new AlertDialog.Builder(activity)
                .setTitle("🏠 Your Garage")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void update(String description) {
        saveData();

        if (callback != null) {
            callback.onUpdate(money, health, happiness, description);
        } else {
            showGarage();
        }
    }

    private void message(String title, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(10, 10, 10, 10);
        return view;
    }

    private void addInfo(LinearLayout root, String value) {
        TextView view = text(value, 16, white);
        root.addView(view);
    }

    private void addButton(LinearLayout root, String value, Runnable action) {
        TextView button = text(value, 15, dark);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(null, Typeface.BOLD);
        button.setBackground(shape(gold, 16));
        button.setPadding(12, 15, 12, 15);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 7, 0, 7);
        root.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private void addSpace(LinearLayout root, int height) {
        TextView space = new TextView(activity);
        space.setHeight(height);
        root.addView(space);
    }
}
