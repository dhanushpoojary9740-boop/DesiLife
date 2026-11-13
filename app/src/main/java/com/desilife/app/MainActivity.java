package com.desilife.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    int gold = Color.rgb(226,184,105);
    int bg = Color.rgb(12,25,23);
    int white = Color.WHITE;
    int muted = Color.LTGRAY;

    LinearLayout root;
    LinearLayout dashboardShell;
    SharedPreferences save;
    FinanceManager financeManager;

    String playerName, gender, city, family;
    String schoolType = "";
    String pucStream = "";
    String degreeCourse = "";
    String educationStatus = "Not enrolled";

    int age = 0;
    int ageMonths = 0;
    int health = 100;
    int happiness = 100;
    int intelligence = 50;
    int money = 5000;
    int examMarks = 0;

    boolean sslcAttempted = false;
    boolean sslcPassed = false;
    boolean pucAttempted = false;
    boolean pucPassed = false;
    boolean degreeCompleted = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(Color.rgb(12, 31, 27));
        getWindow().setNavigationBarColor(Color.rgb(12, 31, 27));
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_VISIBLE
        );

        if (android.os.Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        save = getSharedPreferences("DesiLifeSave", MODE_PRIVATE);

        if (save.getBoolean("created", false)) {
            loadGame();
            showDashboard();
        } else {
            showHome();
        }
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(8,10,8,10);
        return t;
    }

    void setup() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18,22,18,28);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(bg);

        scroll.addView(root);
        setContentView(scroll);
    }

    void setupDashboard() {
        dashboardShell = new LinearLayout(this);
        dashboardShell.setOrientation(LinearLayout.VERTICAL);
        dashboardShell.setBackgroundColor(bg);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            dashboardShell.setOnApplyWindowInsetsListener((v, insets) -> {
                int top = insets.getSystemWindowInsetTop();
                int bottom = insets.getSystemWindowInsetBottom();
                v.setPadding(0, top, 0, bottom);
                return insets;
            });
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setBackgroundColor(bg);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16,16,16,22);
        root.setGravity(Gravity.TOP);
        root.setBackgroundColor(bg);

        scroll.addView(root);

        dashboardShell.addView(
            scroll,
            new LinearLayout.LayoutParams(-1,0,1)
        );

        setContentView(dashboardShell);
        dashboardShell.requestApplyInsets();
    }

    void addText(String value, int size, int color) {
        TextView t = text(value,size,color);
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,5,0,5);
        root.addView(t,p);
    }

    void addButton(String label, Runnable action) {
        TextView b = text(label,15,Color.rgb(25,48,39));
        b.setTypeface(null,Typeface.BOLD);
        b.setBackground(shape(Color.rgb(239,202,126),18));
        b.setPadding(14,15,14,15);
        b.setElevation(2);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,9,0,9);
        root.addView(b,p);

        b.setOnClickListener(v -> action.run());
    }

    void addDivider() {
        View line = new View(this);
        line.setBackgroundColor(gold);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,2);
        p.setMargins(0,18,0,18);
        root.addView(line,p);
    }

    Spinner addSpinner(String[] items) {
        Spinner s = new Spinner(this);

        ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
            );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        s.setAdapter(adapter);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,55);
        p.setMargins(0,5,0,12);
        root.addView(s,p);

        return s;
    }

    EditText addInput(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextColor(white);
        e.setHintTextColor(muted);
        e.setSingleLine(true);
        e.setPadding(15,10,15,10);
        e.setBackground(shape(Color.rgb(28,58,50),16));
        e.setTextSize(15);
        e.setCompoundDrawablePadding(8);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,55);
        p.setMargins(0,8,0,15);
        root.addView(e,p);

        return e;
    }

    LinearLayout createBottomNavigation() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(4,7,4,7);
        nav.setBackground(shape(Color.rgb(20,48,42),18));
        nav.setElevation(8);

        addDashboardNav(nav,"👤","Life",() -> showDashboard());
        addDashboardNav(nav,"💰","Assets",() -> openAssets());
        addDashboardNav(nav,"💼","Career",() -> openCareer());
        addDashboardNav(nav,"❤️","Relations",() -> openRelations());
        addDashboardNav(nav,"•••","Activities",() -> openActivities());
        addDashboardNav(nav,"🏦","Finance",() -> openFinance());

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(8,0,8,8);

        return nav;
    }

    void addLifeEvent(String description) {
        if (description == null || description.trim().isEmpty()) return;

        String old = save.getString("life_events", "");
        String entry = "Age " + age + " • " + description;

        String updated = old.isEmpty()
            ? entry : old + "\n" + entry;

        String[] entries = updated.split("\n");

        if (entries.length > 150) {
            StringBuilder trimmed = new StringBuilder();

            for (int i = entries.length - 150;
                 i < entries.length; i++) {

                if (trimmed.length() > 0) trimmed.append("\n");
                trimmed.append(entries[i]);
            }

            updated = trimmed.toString();
        }

        save.edit().putString("life_events",updated).apply();
    }

    void recordFinancialTransaction(String category, long amount, long balance) {
        if (amount == 0) return;

        String direction = amount > 0 ? "INCOME" : "EXPENSE";
        String signedAmount = (amount > 0 ? "+₹" : "-₹")
                + Math.abs(amount);
        String entry = "Age " + getFormattedAge()
                + " | " + direction
                + " | " + category
                + " | " + signedAmount
                + " | Balance: ₹" + balance;

        String old = save.getString("transaction_history", "");
        String updated = old.isEmpty() ? entry : old + "\\n" + entry;
        String[] entries = updated.split("\\n");

        if (entries.length > 500) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = entries.length - 500; i < entries.length; i++) {
                if (trimmed.length() > 0) trimmed.append("\\n");
                trimmed.append(entries[i]);
            }
            updated = trimmed.toString();
        }

        save.edit().putString("transaction_history", updated).apply();

        addLifeEvent((amount > 0 ? "Income" : "Expense")
                + ": " + category + " "
                + signedAmount + ". Remaining cash: ₹" + balance + ".");
        saveGame();
    }

    void recordFinancialAssetEvent(String category, long change, String details) {
        if (change == 0) return;

        String sign = change > 0 ? "+₹" : "-₹";
        String entry = "Age " + getFormattedAge()
                + " | " + category
                + " | " + sign + Math.abs(change)
                + " | " + details;

        String old = save.getString("asset_history", "");
        String updated = old.isEmpty() ? entry : old + "\\n" + entry;
        String[] entries = updated.split("\\n");

        if (entries.length > 300) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = entries.length - 300; i < entries.length; i++) {
                if (trimmed.length() > 0) trimmed.append("\\n");
                trimmed.append(entries[i]);
            }
            updated = trimmed.toString();
        }

        save.edit().putString("asset_history", updated).apply();
        addLifeEvent("Financial asset update: " + category + " "
                + sign + Math.abs(change) + " (" + details + ").");
        saveGame();
    }

    String getFormattedAge() {
        if (ageMonths == 0) return age + (age == 1 ? " year" : " years");
        return age + (age == 1 ? " year, " : " years, ")
                + ageMonths + (ageMonths == 1 ? " month" : " months");
    }

    void showFullLifeHistory() {
        String history = save.getString("life_events", "");

        if (history.isEmpty()) {
            history = "No life events recorded yet.";
        }

        String[] entries = history.split("\n");
        StringBuilder display = new StringBuilder();

        for (int i = entries.length-1; i >= 0; i--) {
            display.append("• ").append(entries[i]);

            if (i > 0) display.append("\n\n");
        }

        ScrollView scroll = new ScrollView(this);
        TextView content = new TextView(this);

        content.setText(display.toString());
        content.setTextSize(15);
        content.setTextColor(Color.rgb(65,65,65));
        content.setPadding(18,14,18,14);

        scroll.addView(content);

        new android.app.AlertDialog.Builder(this)
            .setTitle("📖 Your Complete Life History")
            .setView(scroll)
            .setPositiveButton("CLOSE",null)
            .show();
    }

    void showDashboardMenu() {
        String[] options = {
            "Continue Game",
            "Start New Life",
            "Save Game",
            "Life Statistics",
            "Net Worth Dashboard",
            "Main Menu"
        };

        new android.app.AlertDialog.Builder(this)
            .setTitle("DesiLife Menu")
            .setItems(options,(dialog,which) -> {

                if (which == 0) {
                    loadGame();
                    showDashboard();

                } else if (which == 1) {

                    new android.app.AlertDialog.Builder(this)
                        .setTitle("Start a New Life?")
                        .setMessage(
                            "Your current life will remain saved until you create the new character. Continue?"
                        )
                        .setNegativeButton("CANCEL",null)
                        .setPositiveButton("CONTINUE",
                            (d,w) -> showCreate())
                        .show();

                } else if (which == 2) {
                    saveGame();
                    message("Your life has been saved!");

                } else if (which == 3) {
                    showLifeStatistics();

                } else if (which == 4) {
                    showNetWorthDashboard();

                } else {
                    showHome();
                }
            })
            .show();
    }

    void showNetWorthDashboard() {
        long cash = Math.max(0, money);

        long bankAssets = 0;
        android.content.SharedPreferences bankPrefs =
                getSharedPreferences("desilife_bank_data", MODE_PRIVATE);
        bankAssets += Math.max(0, bankPrefs.getLong("savings", 0));
        bankAssets += Math.max(0, bankPrefs.getLong("current", 0));
        bankAssets += Math.max(0, bankPrefs.getLong("fd_principal", 0));
        bankAssets += Math.max(0, bankPrefs.getLong("rd_balance", 0));

        long investmentAssets = 0;
        android.content.SharedPreferences investmentPrefs =
                getSharedPreferences("desilife_investment_data", MODE_PRIVATE);
        int investmentCount = investmentPrefs.getInt("investment_count", 0);
        for (int i = 0; i < investmentCount; i++) {
            investmentAssets += Math.max(0,
                    investmentPrefs.getLong("investment_value_" + i, 0));
        }

        long propertyAssets = 0;
        int propertyCount = save.getInt("property_count", 0);
        for (int i = 0; i < propertyCount; i++) {
            propertyAssets += Math.max(0,
                    save.getInt("property_" + i + "_value", 0));
        }

        long businessAssets = 0;
        int businessCount = save.getInt("business_count", 0);
        for (int i = 0; i < businessCount; i++) {
            String business = save.getString("business_" + i, "");
            if (business == null || business.isEmpty()) continue;
            String[] fields = business.split("\\|", -1);
            if (fields.length > 6) {
                try {
                    businessAssets += Math.max(0, Long.parseLong(fields[6]));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        long personalAssets = 0;
        personalAssets += Math.max(0, save.getInt("houseValue", 0));
        personalAssets += Math.max(0, save.getInt("vehicleValue", 0));

        long legacyDebt = Math.max(0, save.getInt("loanDebt", 0));
        long businessDebt = Math.max(0, save.getInt("business_loan", 0));
        android.content.SharedPreferences loanPrefs =
                getSharedPreferences("desilife_loan_data", MODE_PRIVATE);
        long loanDebt = Math.max(0, loanPrefs.getLong("outstanding", 0));
        long financeDebt = Math.max(0, legacyDebt + businessDebt + loanDebt);

        long totalAssets = cash + bankAssets + investmentAssets
                + propertyAssets + businessAssets + personalAssets;
        long netWorth = totalAssets - financeDebt;

        java.text.NumberFormat inr =
                java.text.NumberFormat.getCurrencyInstance(
                        new java.util.Locale("en", "IN"));

        String report =
                "TOTAL ASSETS\n" +
                "Cash: " + inr.format(cash) + "\n" +
                "Bank balances: " + inr.format(bankAssets) + "\n" +
                "Investments: " + inr.format(investmentAssets) + "\n" +
                "Properties: " + inr.format(propertyAssets) + "\n" +
                "Businesses: " + inr.format(businessAssets) + "\n" +
                "Other owned assets: " + inr.format(personalAssets) + "\n" +
                "------------------------------\n" +
                "Total assets: " + inr.format(totalAssets) + "\n\n" +
                "LIABILITIES\n" +
                "Outstanding loans: " + inr.format(financeDebt) + "\n" +
                "------------------------------\n" +
                "NET WORTH: " + inr.format(netWorth) + "\n\n" +
                "Figures are calculated from saved game balances and asset values. " +
                "Some items may not be included if their value is not stored.";

        ScrollView scroll = new ScrollView(this);
        TextView content = new TextView(this);
        content.setText(report);
        content.setTextSize(16);
        content.setTextColor(Color.rgb(45, 45, 45));
        content.setPadding(22, 20, 22, 20);
        scroll.addView(content);

        new android.app.AlertDialog.Builder(this)
                .setTitle("📊 Net Worth Dashboard")
                .setView(scroll)
                .setPositiveButton("CLOSE", null)
                .show();
    }

    void showLifeStatistics() {
        String history = save.getString("life_events","");
        int eventCount = history.isEmpty()
            ? 0 : history.split("\n").length;

        String stats =
            "Name: " + playerName +
            "\nAge: " + age +
            "\nHealth: " + health + "%" +
            "\nHappiness: " + happiness + "%" +
            "\nIntelligence: " + intelligence + "%" +
            "\nBank balance: ₹" + money +
            "\nEducation: " + educationStatus +
            "\nRecorded life events: " + eventCount;

        message(stats);
    }

    void addLifeEventsPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(13,12,13,12);
        panel.setBackground(shape(Color.WHITE,14));

        TextView heading = dashboardText(
            "📖 LIFE EVENTS",18,Color.rgb(17,82,130),true
        );

        heading.setGravity(Gravity.LEFT);
        panel.addView(heading);

        String history = save.getString("life_events","");
        String[] events = history.isEmpty()
            ? new String[0] : history.split("\n");

        if (events.length == 0) {
            TextView empty = dashboardText(
                "Your story is waiting to unfold. Age up and make choices to create your first memories.",
                14,Color.rgb(100,100,100),false
            );

            empty.setGravity(Gravity.LEFT);
            panel.addView(empty);

        } else {
            int shown = 0;

            for (int i = events.length-1;
                 i >= 0 && shown < 5;
                 i--,shown++) {

                TextView event = dashboardText(
                    "• " + events[i],
                    14,Color.rgb(80,80,80),false
                );

                event.setGravity(Gravity.LEFT);
                event.setPadding(4,7,4,7);
                panel.addView(event);

                if (shown < 4 && i > 0) {
                    View line = new View(this);
                    line.setBackgroundColor(Color.rgb(230,230,230));
                    panel.addView(line,
                        new LinearLayout.LayoutParams(-1,1));
                }
            }
        }

        TextView viewAll = dashboardText(
            "VIEW FULL LIFE HISTORY →",
            13,Color.rgb(17,82,130),true
        );

        viewAll.setGravity(Gravity.RIGHT);
        viewAll.setPadding(5,12,5,3);
        panel.addView(viewAll);

        viewAll.setOnClickListener(v -> showFullLifeHistory());

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,0,0,9);
        root.addView(panel,p);
    }

    void showHome() {
        setup();

        addText("🇮🇳",55,gold);
        addText("DESILIFE",38,gold);
        addText("APNI ZINDAGI, APNE FAISLE",13,muted);

        addDivider();

        addText("Your Life. Your Story.",25,white);

        addText(
            "Born in India. Raised by choices. Build your own destiny.",
            15,muted
        );

        addDivider();

        addButton("✦ START A NEW LIFE",() -> showCreate());

        addButton("CONTINUE GAME",() -> {
            if (save.getBoolean("created",false)) {
                loadGame();
                showDashboard();
            } else {
                message("No saved life yet. Start your first life!");
            }
        });

        addDivider();
        addText("AN INDIAN LIFE SIMULATION",12,gold);
        addText("Version 1.4",12,muted);
    }

    void showCreate() {
        setup();

        root.setPadding(18, 24, 18, 32);

        addText("🇮🇳", 42, gold);
        addText("CREATE YOUR LIFE", 29, gold);
        addText("Every great journey starts with a birth.", 15, muted);

        addDivider();

        addCreateSectionTitle("PERSONAL DETAILS", "Tell us who you are");

        addCreateLabel("CHARACTER NAME");
        EditText name = addInput("Enter your name");
        name.setTextSize(17);
        name.setTextColor(Color.WHITE);
        name.setHintTextColor(Color.rgb(185, 194, 190));
        name.setBackground(shape(Color.rgb(35, 52, 47), 16));
        name.setPadding(18, 12, 18, 12);

        addCreateLabel("GENDER");
        Spinner genderSpinner = addCreateSpinner(
            new String[]{"Male", "Female"}
        );

        addDivider();
        addCreateSectionTitle("YOUR ORIGIN", "Choose where your life begins");

        addCreateLabel("BIRTHPLACE");
        Spinner citySpinner = addCreateSpinner(new String[]{
            "Bengaluru, Karnataka",
            "Mysuru, Karnataka",
            "Mangaluru, Karnataka",
            "Udupi, Karnataka",
            "Mumbai, Maharashtra",
            "Pune, Maharashtra",
            "Delhi",
            "Chennai, Tamil Nadu",
            "Hyderabad, Telangana",
            "Kochi, Kerala",
            "Kolkata, West Bengal",
            "Jaipur, Rajasthan",
            "Lucknow, Uttar Pradesh",
            "Ahmedabad, Gujarat",
            "Bhopal, Madhya Pradesh"
        });

        addCreateLabel("FAMILY BACKGROUND");
        Spinner familySpinner = addCreateSpinner(
            new String[]{"Poor", "Middle Class", "Rich"}
        );

        addDivider();
        addText("Your choices shape your journey. You can build your future from here.",
            14, muted);

        addButton("CREATE MY LIFE  →", () -> {
            String n = name.getText().toString().trim();

            if (n.isEmpty()) {
                name.setError("Please enter your name");
                name.requestFocus();
                return;
            }

            playerName = n;
            gender = genderSpinner.getSelectedItem().toString();
            city = citySpinner.getSelectedItem().toString();
            family = familySpinner.getSelectedItem().toString();

            age = 0;
            ageMonths = 0;
            health = 100;
            happiness = 100;
            intelligence = 50;
            examMarks = 0;

            schoolType = "";
            pucStream = "";
            degreeCourse = "";
            educationStatus = "Not enrolled";

            sslcAttempted = false;
            sslcPassed = false;
            pucAttempted = false;
            pucPassed = false;
            degreeCompleted = false;

            if (family.equals("Poor")) {
                money = 500;
            } else if (family.equals("Rich")) {
                money = 500000;
            } else {
                money = 5000;
            }

            resetNewLifeData();
            financeManager = null;

            addLifeEvent(
                "A new life began in " + city +
                ". Family background: " + family + "."
            );

            saveGame();
            showDashboard();
        });

        addButton("← BACK", () -> showHome());
    }

    void addCreateSectionTitle(String title, String subtitle) {
        TextView heading = text(title, 15, gold);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        heading.setPadding(4, 8, 4, 2);
        root.addView(heading, new LinearLayout.LayoutParams(-1, -2));

        TextView description = text(subtitle, 13, muted);
        description.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        description.setPadding(4, 0, 4, 12);
        root.addView(description, new LinearLayout.LayoutParams(-1, -2));
    }

    void addCreateLabel(String label) {
        TextView heading = text(label, 13, Color.rgb(235, 221, 190));
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        heading.setPadding(4, 10, 4, 5);
        root.addView(heading, new LinearLayout.LayoutParams(-1, -2));
    }

    Spinner addCreateSpinner(String[] items) {
        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            items
        ) {
            @Override
            public View getView(int position, View convertView,
                                android.view.ViewGroup parent) {
                TextView value = new TextView(MainActivity.this);
                value.setText(getItem(position));
                value.setTextColor(Color.WHITE);
                value.setTextSize(16);
                value.setGravity(Gravity.CENTER_VERTICAL);
                value.setPadding(16, 0, 16, 0);
                return value;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        android.view.ViewGroup parent) {
                TextView value = new TextView(MainActivity.this);
                value.setText(getItem(position));
                value.setTextColor(Color.WHITE);
                value.setTextSize(16);
                value.setGravity(Gravity.CENTER_VERTICAL);
                value.setPadding(20, 15, 20, 15);
                value.setBackgroundColor(Color.rgb(35, 52, 47));
                return value;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setBackground(shape(Color.rgb(35, 52, 47), 16));
        spinner.setPadding(10, 4, 12, 4);
        spinner.setPopupBackgroundDrawable(shape(Color.rgb(25, 39, 35), 12));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, 58);
        params.setMargins(0, 2, 0, 12);
        root.addView(spinner, params);

        return spinner;
    }

    void resetNewLifeData() {
        save.edit().clear().commit();

        getSharedPreferences("desilife_finance_data", MODE_PRIVATE)
            .edit().clear().commit();

        getSharedPreferences("desilife_loan_data", MODE_PRIVATE)
            .edit().clear().commit();

        getSharedPreferences("desilife_bank_data", MODE_PRIVATE)
            .edit().clear().commit();

        getSharedPreferences("desilife_household_data", MODE_PRIVATE)
            .edit().clear().commit();

        getSharedPreferences("desilife_insurance_data", MODE_PRIVATE)
            .edit().clear().commit();

        getSharedPreferences("desilife_investment_data", MODE_PRIVATE)
            .edit().clear().commit();

        getSharedPreferences("desilife_career_data", MODE_PRIVATE)
            .edit().clear().commit();

        financeManager = null;
    }

    void saveGame() {
        save.edit()
            .putBoolean("created",true)
            .putString("name",playerName)
            .putString("gender",gender)
            .putString("city",city)
            .putString("family",family)
            .putString("schoolType",schoolType)
            .putString("pucStream",pucStream)
            .putString("degreeCourse",degreeCourse)
            .putString("educationStatus",educationStatus)
            .putInt("age",age)
            .putInt("age_months",ageMonths)
            .putInt("health",health)
            .putInt("happiness",happiness)
            .putInt("intelligence",intelligence)
            .putInt("money",money)
            .putInt("examMarks",examMarks)
            .putBoolean("sslcAttempted",sslcAttempted)
            .putBoolean("sslcPassed",sslcPassed)
            .putBoolean("pucAttempted",pucAttempted)
            .putBoolean("pucPassed",pucPassed)
            .putBoolean("degreeCompleted",degreeCompleted)
            .apply();
    }

    void loadGame() {
        playerName = save.getString("name","Player");
        gender = save.getString("gender","Male");
        city = save.getString("city","Bengaluru");
        family = save.getString("family","Middle Class");

        age = save.getInt("age",0);
        ageMonths = Math.max(0, Math.min(11, save.getInt("age_months",0)));
        health = save.getInt("health",100);
        happiness = save.getInt("happiness",100);
        intelligence = save.getInt("intelligence",50);
        money = save.getInt("money",5000);

        schoolType = save.getString("schoolType","");
        pucStream = save.getString("pucStream","");
        degreeCourse = save.getString("degreeCourse","");

        educationStatus = save.getString(
            "educationStatus","Not enrolled"
        );

        examMarks = save.getInt("examMarks",0);

        sslcPassed = save.getBoolean(
            "sslcPassed",
            educationStatus.equals("SSLC Passed") ||
            educationStatus.equals("PUC Passed") ||
            !degreeCourse.isEmpty() ||
            educationStatus.equals("Graduated")
        );

        pucPassed = save.getBoolean(
            "pucPassed",
            educationStatus.equals("PUC Passed") ||
            !degreeCourse.isEmpty() ||
            educationStatus.equals("Graduated")
        );

        degreeCompleted = save.getBoolean(
            "degreeCompleted",
            educationStatus.equals("Graduated")
        );

        sslcAttempted = save.getBoolean(
            "sslcAttempted",sslcPassed
        );

        pucAttempted = save.getBoolean(
            "pucAttempted",pucPassed
        );

        if (age >= 6 && age <= 14 && schoolType.isEmpty()) {
            educationStatus = "Class " + (age-5);
        }
    }

    void showDashboard() {
        setupDashboard();

        // Premium brand header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(18, 16, 18, 16);
        header.setBackground(shape(Color.rgb(20, 48, 42), 22));

        LinearLayout brandBlock = new LinearLayout(this);
        brandBlock.setOrientation(LinearLayout.VERTICAL);

        TextView brand = dashboardText("DESILIFE", 22, Color.rgb(239, 202, 126), true);
        brand.setGravity(Gravity.LEFT);
        TextView tagline = dashboardText("YOUR STORY. YOUR CHOICES.", 10, Color.rgb(190, 204, 195), true);
        tagline.setGravity(Gravity.LEFT);
        brandBlock.addView(brand);
        brandBlock.addView(tagline);
        header.addView(brandBlock, new LinearLayout.LayoutParams(0, -2, 1));

        TextView menu = dashboardText("☰", 25, Color.WHITE, true);
        menu.setPadding(14, 8, 8, 8);
        menu.setOnClickListener(v -> showDashboardMenu());
        header.addView(menu);
        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        // Character profile card
        LinearLayout profile = new LinearLayout(this);
        profile.setOrientation(LinearLayout.HORIZONTAL);
        profile.setGravity(Gravity.CENTER_VERTICAL);
        profile.setPadding(16, 16, 16, 16);
        profile.setBackground(shape(Color.rgb(28, 58, 50), 20));
        LinearLayout.LayoutParams profileParams = new LinearLayout.LayoutParams(-1, -2);
        profileParams.setMargins(0, 12, 0, 12);
        root.addView(profile, profileParams);

        TextView avatar = dashboardText(
                gender.equalsIgnoreCase("Female") ? "👩" : "👨",
                36, Color.WHITE, true);
        avatar.setBackground(shape(Color.rgb(54, 86, 72), 18));
        avatar.setPadding(12, 10, 12, 10);
        profile.addView(avatar);

        LinearLayout profileInfo = new LinearLayout(this);
        profileInfo.setOrientation(LinearLayout.VERTICAL);
        profileInfo.setPadding(12, 0, 4, 0);
        TextView player = dashboardText(playerName == null ? "Player" : playerName,
                19, Color.WHITE, true);
        player.setGravity(Gravity.LEFT);
        TextView details = dashboardText(getFormattedAge() + "  •  " + gender + "  •  " + city,
                11, Color.rgb(192, 207, 198), false);
        details.setGravity(Gravity.LEFT);
        profileInfo.addView(player);
        profileInfo.addView(details);
        profile.addView(profileInfo, new LinearLayout.LayoutParams(0, -2, 1));

        LinearLayout balanceBox = new LinearLayout(this);
        balanceBox.setOrientation(LinearLayout.VERTICAL);
        balanceBox.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        TextView balanceValue = dashboardText("₹" + money, 17,
                Color.rgb(239, 202, 126), true);
        balanceValue.setGravity(Gravity.RIGHT);
        TextView balanceLabel = dashboardText("CASH", 10,
                Color.rgb(192, 207, 198), true);
        balanceLabel.setGravity(Gravity.RIGHT);
        balanceBox.addView(balanceValue);
        balanceBox.addView(balanceLabel);
        profile.addView(balanceBox);

        // Main life progress card
        LinearLayout journey = new LinearLayout(this);
        journey.setOrientation(LinearLayout.VERTICAL);
        journey.setPadding(17, 16, 17, 16);
        journey.setBackground(shape(Color.rgb(245, 241, 231), 20));
        TextView journeyTitle = dashboardText("✦  YOUR LIFE JOURNEY", 14,
                Color.rgb(31, 73, 59), true);
        journeyTitle.setGravity(Gravity.LEFT);
        TextView journeyStory = dashboardText(
                "Born in " + city + " • " + family + " family\n" + getCurrentLifeStory(),
                13, Color.rgb(76, 83, 76), false);
        journeyStory.setGravity(Gravity.LEFT);
        journeyStory.setPadding(0, 7, 0, 0);
        journey.addView(journeyTitle);
        journey.addView(journeyStory);
        LinearLayout.LayoutParams journeyParams = new LinearLayout.LayoutParams(-1, -2);
        journeyParams.setMargins(0, 0, 0, 12);
        root.addView(journey, journeyParams);

        // Quick stats
        LinearLayout statsCard = new LinearLayout(this);
        statsCard.setOrientation(LinearLayout.VERTICAL);
        statsCard.setPadding(16, 14, 16, 14);
        statsCard.setBackground(shape(Color.rgb(28, 58, 50), 20));
        TextView statsTitle = dashboardText("LIFE STATUS", 14,
                Color.rgb(239, 202, 126), true);
        statsTitle.setGravity(Gravity.LEFT);
        statsCard.addView(statsTitle);
        addDashboardStat(statsCard, "Happiness", "😊", happiness, Color.rgb(72, 181, 121));
        addDashboardStat(statsCard, "Health", "❤️", health, Color.rgb(220, 100, 100));
        addDashboardStat(statsCard, "Intelligence", "🧠", intelligence, Color.rgb(90, 153, 220));
        addDashboardStat(statsCard, "Stress", "🌿", save.getInt("mental_stress", 20),
                Color.rgb(222, 167, 83));
        LinearLayout.LayoutParams statsParams = new LinearLayout.LayoutParams(-1, -2);
        statsParams.setMargins(0, 0, 0, 12);
        root.addView(statsCard, statsParams);

        // Primary action
        TextView ageUp = dashboardText("＋   AGE UP", 16,
                Color.rgb(25, 48, 39), true);
        ageUp.setBackground(shape(Color.rgb(239, 202, 126), 18));
        ageUp.setPadding(16, 16, 16, 16);
        ageUp.setElevation(4);
        LinearLayout.LayoutParams ageParams = new LinearLayout.LayoutParams(-1, -2);
        ageParams.setMargins(0, 0, 0, 14);
        root.addView(ageUp, ageParams);
        ageUp.setOnClickListener(v -> showAgeUpOptions());

        addLifeEventsPanel();

        // Premium section directory: every area has an icon AND a written name.
        addPremiumSectionHeading("YOUR LIFE", "Personal growth and everyday decisions");
        addPremiumSectionRow("📚", "Education", "Studies and examinations", () -> showEducation(),
                "💼", "Career & Jobs", "Work and career progression", () -> openCareer());
        addPremiumSectionRow("👨‍👩‍👧", "Family", "Family, children and legacy", () -> openFamily(),
                "❤️", "Relationships", "Friends and relationships", () -> openRelations());
        addPremiumSectionRow("🏥", "Health", "Physical health and wellness", () -> openHealth(),
                "🧠", "Mental Wellness", "Stress and mental wellbeing", () -> openMentalHealth());

        addPremiumSectionHeading("MONEY & ASSETS", "Build, manage and grow your wealth");
        addPremiumSectionRow("💰", "Assets", "Your owned assets", () -> openAssets(),
                "🏦", "Finance & Banking", "Accounts, loans and insurance", () -> openFinance());
        addPremiumSectionRow("📈", "Stock Market", "Stocks and investments", () -> openStockMarket(),
                "🏠", "Property", "Real estate and rental income", () -> openProperty());
        addPremiumSectionRow("🏢", "Business", "Build your own business", () -> openBusiness(),
                "📊", "Net Worth", "Assets, debts and net worth", () -> showNetWorthDashboard());

        addPremiumSectionHeading("LIFESTYLE & ACTIVITIES", "Explore choices beyond work and money");
        addPremiumSectionRow("🚗", "Vehicles", "Driving and vehicle ownership", () -> openVehicle(),
                "⚖️", "Crime & Justice", "Legal choices and consequences", () -> openCrime());
        addPremiumSectionRow("🎯", "Activities", "Hobbies and life activities", () -> openActivities(),
                "🌟", "Life Statistics", "Your overall life progress", () -> showLifeStatistics());

        TextView saveButton = dashboardText("💾   SAVE GAME", 14,
                Color.rgb(239, 202, 126), true);
        saveButton.setBackground(shape(Color.rgb(28, 58, 50), 16));
        saveButton.setPadding(14, 15, 14, 15);
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(-1, -2);
        saveParams.setMargins(0, 12, 0, 12);
        root.addView(saveButton, saveParams);
        saveButton.setOnClickListener(v -> {
            saveGame();
            message("Your life has been saved!");
        });

        dashboardShell.addView(createBottomNavigation());
    }

    void addPremiumSectionHeading(String title, String subtitle) {
        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);
        block.setPadding(2, 8, 2, 5);
        TextView heading = dashboardText(title, 15, Color.rgb(239, 202, 126), true);
        heading.setGravity(Gravity.LEFT);
        TextView description = dashboardText(subtitle, 11,
                Color.rgb(181, 198, 188), false);
        description.setGravity(Gravity.LEFT);
        block.addView(heading);
        block.addView(description);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 3);
        root.addView(block, p);
    }

    void addPremiumSectionRow(
            String icon1, String title1, String subtitle1, Runnable action1,
            String icon2, String title2, String subtitle2, Runnable action2) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.TOP);

        addPremiumSectionCard(row, icon1, title1, subtitle1, action1);
        addPremiumSectionCard(row, icon2, title2, subtitle2, action2);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 3, 0, 3);
        root.addView(row, p);
    }

    void addPremiumSectionCard(
            LinearLayout row, String icon, String title,
            String subtitle, Runnable action) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(12, 13, 10, 13);
        card.setBackground(shape(Color.rgb(28, 58, 50), 17));
        card.setElevation(2);

        TextView iconView = dashboardText(icon, 23, Color.WHITE, true);
        iconView.setGravity(Gravity.LEFT);
        TextView titleView = dashboardText(title, 13,
                Color.rgb(239, 202, 126), true);
        titleView.setGravity(Gravity.LEFT);
        TextView subtitleView = dashboardText(subtitle, 10,
                Color.rgb(190, 204, 195), false);
        subtitleView.setGravity(Gravity.LEFT);
        card.addView(iconView);
        card.addView(titleView);
        card.addView(subtitleView);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(3, 3, 3, 3);
        row.addView(card, p);
        card.setOnClickListener(v -> action.run());
    }

    TextView dashboardText(
        String value,int size,int color,boolean bold
    ) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(5,5,5,5);

        if (bold) t.setTypeface(null,Typeface.BOLD);

        return t;
    }

    void addDashboardNav(
        LinearLayout nav,String icon,String label,Runnable action
    ) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(2,4,2,4);

        TextView iconView = dashboardText(
            icon,22,Color.rgb(239,202,126),true
        );

        TextView labelView = dashboardText(
            label,10,Color.rgb(231,236,229),true
        );

        item.addView(iconView);
        item.addView(labelView);

        nav.addView(item,
            new LinearLayout.LayoutParams(0,-2,1));

        item.setOnClickListener(v -> action.run());
    }

    void addDashboardStat(
        LinearLayout parent,String label,String emoji,
        int value,int barColor
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0,5,0,5);

        TextView title = dashboardText(
            label+" "+emoji,13,
            Color.rgb(17,82,130),true
        );

        title.setGravity(Gravity.LEFT);

        row.addView(title,
            new LinearLayout.LayoutParams(105,-2));

        LinearLayout track = new LinearLayout(this);
        track.setBackground(
            shape(Color.rgb(225,233,240),5)
        );

        track.setClipToOutline(true);

        View fill = new View(this);
        fill.setBackground(shape(barColor,5));

        int safeValue = Math.max(0,Math.min(100,value));

        track.addView(fill,
            new LinearLayout.LayoutParams(0,13,safeValue));

        if (safeValue < 100) {
            track.addView(new View(this),
                new LinearLayout.LayoutParams(
                    0,13,100-safeValue
                ));
        }

        row.addView(track,
            new LinearLayout.LayoutParams(0,15,1));

        TextView percent = dashboardText(
            value+"%",13,Color.rgb(17,82,130),true
        );

        row.addView(percent,
            new LinearLayout.LayoutParams(48,-2));

        parent.addView(row);
    }

    String getCurrentLifeStory() {
        if (age == 0) {
            return "My life story is just beginning. Every year will bring new choices and experiences.";
        }

        if (degreeCompleted) {
            return "I completed my "+degreeCourse+
                " degree. A new chapter of my life begins.";
        }

        if (pucPassed && !degreeCourse.isEmpty()) {
            return "I passed PUC and joined "+degreeCourse+
                ". I am continuing my higher education.";
        }

        if (pucPassed) {
            return "I successfully passed my PUC examination. I can now choose my degree course.";
        }

        if (sslcPassed && age >= 16) {
            return "I passed my SSLC examination. My next goal is to complete PUC.";
        }

        if (age >= 6 && age <= 14 && !schoolType.isEmpty()) {
            return "I am studying in Class "+(age-5)+
                " at a "+schoolType+".";
        }

        if (age >= 5) {
            return "I am growing up and preparing for the next stage of my education.";
        }

        return "I am enjoying my childhood with my family.";
    }

    // EDUCATION

    void showEducation() {
        setup();

        addText("📚 EDUCATION",28,gold);
        addDivider();

        addText("Your Education Journey",21,white);
        addText("Age: "+getFormattedAge(),16,muted);

        if (age < 5) {

            addText(
                "You are enjoying your early childhood.\n"+
                "School admission begins at age 5.",
                17,white
            );

        } else if (age == 5 && schoolType.isEmpty()) {

            addText("It's time to start school!",19,gold);
            addText("Choose your school",16,white);

            Spinner school = addSpinner(new String[]{
                "Government School","Private School"
            });

            addButton("ADMIT TO SCHOOL",() -> {
                schoolType = school.getSelectedItem().toString();
                educationStatus = "School admission completed";

                if (schoolType.equals("Private School")) {
                    money = Math.max(0,money-5000);
                }

                happiness = Math.min(100,happiness+5);
                addLifeEvent("You joined "+schoolType+".");

                saveGame();
                showEducation();
            });

        } else if (age >= 6 && age <= 14) {

            int currentClass = age-5;

            addText("🏫 "+schoolType,18,gold);
            addText("Currently studying in Class "+currentClass,20,white);

            addText(
                "Study hard to improve your intelligence and prepare for your future exams.",
                16,muted
            );

            addButton("📖 STUDY HARD",() -> {
                intelligence = Math.min(100,intelligence+5);
                happiness = Math.max(0,happiness-2);

                educationStatus = "Class "+currentClass;

                addLifeEvent("You studied hard in Class "+currentClass+".");

                saveGame();
                showEducation();

                message("You studied hard! Intelligence increased.");
            });

            addButton("🎨 SCHOOL ACTIVITIES",() -> {
                happiness = Math.min(100,happiness+5);

                addLifeEvent("You took part in school activities.");

                saveGame();
                showEducation();

                message("You enjoyed your school activities!");
            });

        } else if (age == 15) {

            addText("🏫 CLASS 10 – SSLC",21,gold);

            if (sslcPassed) {
                addText("SSLC: PASSED ✓",20,white);
            } else if (sslcAttempted) {
                addText("SSLC: FAILED",20,white);
            } else {
                addText("Your board examination is approaching.",16,white);
                addButton("📝 TAKE SSLC EXAM",() -> takeExam("SSLC"));
            }

        } else if (age >= 16 && age <= 17) {

            addText("🎓 PRE-UNIVERSITY COURSE",21,gold);

            if (!sslcPassed) {
                addText("You must pass SSLC before joining PUC.",16,white);

            } else if (pucPassed) {
                addText("PUC: PASSED ✓",20,gold);

            } else if (pucAttempted) {
                addText("PUC: FAILED",20,white);

            } else if (pucStream.isEmpty()) {

                addText("Choose your PUC stream",17,white);

                Spinner stream = addSpinner(new String[]{
                    "Science","Commerce","Arts"
                });

                addButton("JOIN PUC",() -> {
                    pucStream = stream.getSelectedItem().toString();
                    educationStatus = "PUC 1st Year";

                    addLifeEvent("You joined PUC in the "+pucStream+" stream.");

                    if (family.equals("Poor")) {
                        money = Math.max(0,money-500);
                    } else {
                        money = Math.max(0,money-3000);
                    }

                    saveGame();
                    showEducation();
                });

            } else {

                addText("Stream: "+pucStream,18,gold);

                addText(
                    age == 16 ? "PUC First Year" : "PUC Second Year",
                    18,white
                );

                addButton("📖 STUDY HARD",() -> {
                    intelligence = Math.min(100,intelligence+5);

                    addLifeEvent("You studied for your PUC examinations.");

                    saveGame();
                    showEducation();
                });

                if (age == 17) {
                    addButton("📝 TAKE PUC EXAM",() -> takeExam("PUC"));
                }
            }

        } else if (age >= 18) {

            addText("🎓 HIGHER EDUCATION",22,gold);

            if (!pucPassed) {

                if (!sslcPassed) {
                    addText("You must pass SSLC first.",16,white);

                } else if (pucAttempted) {
                    addText("PUC: FAILED",20,white);

                } else if (pucStream.isEmpty()) {

                    addText("Choose your PUC stream",17,white);

                    Spinner stream = addSpinner(new String[]{
                        "Science","Commerce","Arts"
                    });

                    addButton("JOIN PUC",() -> {
                        pucStream = stream.getSelectedItem().toString();
                        educationStatus = "PUC 1st Year";

                        addLifeEvent("You joined PUC in "+pucStream+".");

                        saveGame();
                        showEducation();
                    });

                } else {
                    addText("PUC Stream: "+pucStream,18,white);
                    addButton("📝 TAKE PUC EXAM",() -> takeExam("PUC"));
                }

            } else if (degreeCompleted) {

                addText("🎓 DEGREE COMPLETED ✓",22,gold);
                addText("Course: "+degreeCourse,18,white);

            } else if (degreeCourse.isEmpty()) {

                addText("PUC Passed ✓",18,white);
                addText("Choose your degree course",17,white);

                Spinner course = addSpinner(new String[]{
                    "B.A. – Arts",
                    "B.Com – Commerce",
                    "B.Sc – Science",
                    "BCA – Computer Applications",
                    "BBA – Business Administration",
                    "B.E. – Engineering",
                    "MBBS – Medicine",
                    "LLB – Law"
                });

                addButton("JOIN COLLEGE",() -> {
                    degreeCourse = course.getSelectedItem().toString();
                    educationStatus = "Degree 1st Year";

                    addLifeEvent("You joined "+degreeCourse+" college.");

                    money = Math.max(0,money-10000);

                    saveGame();
                    showEducation();
                });

            } else {

                addText("Course: "+degreeCourse,18,white);
                addText("PUC Passed ✓",16,gold);
                addText(educationStatus,18,white);

                addText(
                    "Continue your studies and complete your degree.",
                    16,muted
                );

                if (age >= 21) {
                    addButton("🎓 COMPLETE DEGREE",() -> {

                        if (!degreeCompleted) {
                            degreeCompleted = true;
                            educationStatus = "Graduated";

                            addLifeEvent("You graduated in "+degreeCourse+".");

                            intelligence = Math.min(100,intelligence+10);
                            happiness = Math.min(100,happiness+10);

                            saveGame();
                        }

                        showEducation();

                        message(
                            "🎉 Congratulations!\n\n"+
                            "You have successfully graduated in "+
                            degreeCourse+"."
                        );
                    });
                }
            }
        }

        addDivider();
        addText("Education status: "+educationStatus,15,muted);
        addButton("BACK TO LIFE",() -> showDashboard());
    }

    void takeExam(String exam) {

        if (exam.equals("SSLC")) {

            if (sslcAttempted) {
                message("You have already taken your SSLC examination.");
                showEducation();
                return;
            }

            sslcAttempted = true;

        } else if (exam.equals("PUC")) {

            if (pucAttempted || pucPassed) {
                message("You have already completed your PUC examination.");
                showEducation();
                return;
            }

            if (!sslcPassed) {
                message("You must pass SSLC first.");
                return;
            }

            pucAttempted = true;
        }

        examMarks = 40+(int)(Math.random()*61);
        boolean passed = examMarks >= 35;

        if (exam.equals("SSLC")) {
            sslcPassed = passed;
        } else {
            pucPassed = passed;
        }

        if (passed) {

            if (exam.equals("SSLC")) {
                educationStatus = "SSLC Passed";
                intelligence = Math.min(100,intelligence+5);
            } else {
                educationStatus = "PUC Passed";
                intelligence = Math.min(100,intelligence+10);
            }

            happiness = Math.min(100,happiness+10);

        } else {
            educationStatus = exam+" Failed";
            happiness = Math.max(0,happiness-10);
        }

        addLifeEvent(
            exam+" examination: "+
            (passed ? "PASSED" : "FAILED")+
            " with "+examMarks+"/100 marks."
        );

        saveGame();
        showEducation();

        message(
            (passed ? "🎉 Congratulations!\n\n" :
                "Unfortunately, you did not pass.\n\n")+
            exam+" Result: "+(passed ? "PASS" : "FAIL")+
            "\nMarks: "+examMarks+"/100\n\n"+
            "Examination attempts cannot be repeated."
        );
    }

    // CAREER HUB - CONNECTS ALL EXISTING CAREER SYSTEMS

    void openCareer() {

        String[] careerOptions = {
            "💼 General Careers",
            "🏛️ Government Competitive Exams",
            "🇮🇳 Karnataka & Central Government Service",
            "🏢 Private Company Careers",
            "👔 General Employment"
        };

        new android.app.AlertDialog.Builder(this)
            .setTitle("💼 CAREER CENTRE")
            .setItems(careerOptions, (dialog, which) -> {
                switch (which) {
                    case 0:
                        openGeneralCareer();
                        break;
                    case 1:
                        openGovernmentExams();
                        break;
                    case 2:
                        openPublicServiceCareer();
                        break;
                    case 3:
                        openPrivateCareer();
                        break;
                    case 4:
                        openGeneralEmployment();
                        break;
                }
            })
            .setNegativeButton("BACK", null)
            .show();
    }

    // 1. GENERAL CAREERS
    void openGeneralCareer() {
        CareerManager career = new CareerManager(
            this,
            save,
            age,
            intelligence,
            educationStatus + " " + degreeCourse,
            degreeCompleted,
            money,
            newMoney -> {
                money = (int) Math.max(
                    0, Math.min(Integer.MAX_VALUE, newMoney)
                );
                addLifeEvent("You made a general career decision or received career income.");
                saveGame();
            }
        );
        career.open();
    }

    // 2. GOVERNMENT COMPETITIVE EXAMS
    void openGovernmentExams() {
        GovernmentExamManager manager = new GovernmentExamManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney, newHealth, newHappiness, description) -> {
                money = Math.max(0, newMoney);
                health = Math.max(0, Math.min(100, newHealth));
                happiness = Math.max(0, Math.min(100, newHappiness));
                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );
        manager.open();
    }

    // 3. KARNATAKA & CENTRAL GOVERNMENT SERVICE
    void openPublicServiceCareer() {
        PublicServiceManager manager = new PublicServiceManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney, newHealth, newHappiness, description) -> {
                money = Math.max(0, newMoney);
                health = Math.max(0, Math.min(100, newHealth));
                happiness = Math.max(0, Math.min(100, newHappiness));
                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );
        manager.open();
    }

    // 4. PRIVATE COMPANY CAREERS
    void openPrivateCareer() {
        PrivateCareerManager manager = new PrivateCareerManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney, newHealth, newHappiness, description) -> {
                money = Math.max(0, newMoney);
                health = Math.max(0, Math.min(100, newHealth));
                happiness = Math.max(0, Math.min(100, newHappiness));
                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );
        manager.open();
    }

    // 5. GENERAL EMPLOYMENT
    void openGeneralEmployment() {
        EmploymentManager manager = new EmploymentManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney, newHealth, newHappiness, description) -> {
                money = Math.max(0, newMoney);
                health = Math.max(0, Math.min(100, newHealth));
                happiness = Math.max(0, Math.min(100, newHappiness));
                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );
        manager.open();
    }

    // ANNUAL PROGRESSION FOR ALL CAREER SYSTEMS
    // Runs once when the player advances a year. Salary is not credited
    // by systems that only update salary progression. Private Career keeps
    // its own once-per-age annual salary guard.
    void processAnnualCareerProgression() {
        if (age < 18) return;

        // Government competitive-exam career records use their own
        // namespaced keys, so they do not collide with other job systems.
        GovernmentExamManager governmentExams =
                new GovernmentExamManager(
                        this, save, age, money, health, happiness,
                        (newMoney, newHealth, newHappiness, description) -> {
                            money = Math.max(0, newMoney);
                            health = Math.max(0, Math.min(100, newHealth));
                            happiness = Math.max(0, Math.min(100, newHappiness));
                            addLifeEvent(description);
                            saveGame();
                        }
                );
        governmentExams.applyAnnualUpdate();

        // Public Service and Private Career share legacy keys such as
        // job/salary/status. Their department/company markers identify
        // which record is valid. If both markers exist, skip both safely.
        String department = save.getString("department", "");
        String company = save.getString("company", "");

        if (!department.isEmpty() && company.isEmpty()) {
            PublicServiceManager publicService =
                    new PublicServiceManager(
                            this, save, age, money, health, happiness,
                            (newMoney, newHealth, newHappiness, description) -> {
                                money = Math.max(0, newMoney);
                                health = Math.max(0, Math.min(100, newHealth));
                                happiness = Math.max(0, Math.min(100, newHappiness));
                                addLifeEvent(description);
                                saveGame();
                            }
                    );
            publicService.applyAnnualUpdate();
        } else if (!company.isEmpty() && department.isEmpty()) {
            PrivateCareerManager privateCareer =
                    new PrivateCareerManager(
                            this, save, age, money, health, happiness,
                            (newMoney, newHealth, newHappiness, description) -> {
                                money = Math.max(0, newMoney);
                                health = Math.max(0, Math.min(100, newHealth));
                                happiness = Math.max(0, Math.min(100, newHappiness));
                                addLifeEvent(description);
                                saveGame();
                            }
                    );
            privateCareer.applyAnnualUpdate();
        }

        // General Employment has independent employment_* keys.
        // Its annual method updates experience, salary and promotions;
        // it does not credit salary to cash.
        EmploymentManager employment = new EmploymentManager(
                this, save, age, money, health, happiness,
                (newMoney, newHealth, newHappiness, description) -> {
                    money = Math.max(0, newMoney);
                    health = Math.max(0, Math.min(100, newHealth));
                    happiness = Math.max(0, Math.min(100, newHappiness));
                    addLifeEvent(description);
                    saveGame();
                }
        );
        employment.applyAnnualEmploymentUpdate();

        // Preserve the existing General Careers annual processing.
        processCareerSalary();
    }

    // EXISTING GENERAL CAREER ANNUAL PROCESSING
    // Other career systems are not automatically paid here to prevent
    // duplicate salary credits until their payment schedules are unified.
    void processCareerSalary() {
        if (age < 18) return;

        CareerManager career = new CareerManager(
            this,
            save,
            age,
            intelligence,
            educationStatus + " " + degreeCourse,
            degreeCompleted,
            money,
            newMoney -> {
                money = (int) Math.max(
                    0, Math.min(Integer.MAX_VALUE, newMoney)
                );
                saveGame();
            }
        );
        career.applyAnnualSalary();
    }

    // RELATIONS AND ACTIVITIES

    void openRelations() {
        SocialManager social = new SocialManager(
            this,
            save,
            money,
            age,
            health,
            happiness,
            (newMoney,newHealth,newHappiness) -> {
                money = newMoney;
                health = newHealth;
                happiness = newHappiness;

                addLifeEvent("You made a relationship decision.");

                saveGame();
            }
        );

        social.open();
    }

    void openActivities() {
        SocialManager social = new SocialManager(
            this,
            save,
            money,
            age,
            health,
            happiness,
            (newMoney,newHealth,newHappiness) -> {
                money = newMoney;
                health = newHealth;
                happiness = newHappiness;

                addLifeEvent("You completed an activity.");

                saveGame();
            }
        );

        social.showActivities();
    }

    // ASSETS

    void openAssets() {
        AssetsManager assets = new AssetsManager(
            this,
            save,
            money,
            amount -> {
                money = amount;

                addLifeEvent("You made an asset purchase or sale.");

                saveGame();
            }
        );

        assets.open();
    }

    // FINANCE

    FinanceManager createFinanceManager() {
        FinanceManager manager = new FinanceManager(
            this,
            money,
            newMoney -> {
                money = (int)Math.max(
                    0, Math.min(Integer.MAX_VALUE, newMoney)
                );
                saveGame();
            }
        );

        manager.setTransactionListener((category, amount, balance) -> {
            money = (int)Math.max(
                    0, Math.min(Integer.MAX_VALUE, balance));
            recordFinancialTransaction(category, amount, balance);
        });

        manager.setAssetEventListener((category, change, details) -> {
            recordFinancialAssetEvent(category, change, details);
        });

        return manager;
    }

    void openFinance() {
        if (financeManager == null) {
            financeManager = createFinanceManager();
        } else {
            financeManager.setCash(money);
        }

        financeManager.show();
    }

    // FAMILY & GENERATIONAL LEGACY

    void openFamily() {
        FamilyManager familyManager = new FamilyManager(
            this,
            save,
            money,
            age,
            happiness,
            (newMoney,newHappiness) -> {
                money = Math.max(0,newMoney);
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent("You made a family decision.");

                saveGame();
                showDashboard();
            }
        );

        familyManager.open();
    }

    // AUTOMATIC FAMILY UPDATE EVERY BIRTHDAY

    void processAnnualFamilyUpdate() {

        boolean married = save.getBoolean("family_married",false);
        String children = save.getString("family_children","");

        int childCount = 0;

        if (!children.trim().isEmpty()) {

            String[] childList = children.split("\\|");
            StringBuilder updatedChildren = new StringBuilder();

            for (String child : childList) {

                String updatedChild = child.trim();

                if (updatedChild.isEmpty()) continue;

                int ageStart = updatedChild.lastIndexOf("(Age ");
                int ageEnd = updatedChild.lastIndexOf(")");

                if (ageStart >= 0 && ageEnd > ageStart) {

                    try {
                        int childAge = Integer.parseInt(
                            updatedChild.substring(
                                ageStart+5,ageEnd
                            ).trim()
                        );

                        childAge++;

                        updatedChild =
                            updatedChild.substring(0,ageStart).trim()+
                            "(Age "+childAge+")";

                    } catch (Exception ignored) {
                    }
                }

                if (updatedChildren.length() > 0) {
                    updatedChildren.append("|");
                }

                updatedChildren.append(updatedChild);
                childCount++;
            }

            save.edit()
                .putString("family_children",updatedChildren.toString())
                .apply();
        }

        if (married) {

            int yearlyFamilyExpense = 5000 + (childCount * 2000);

            if (money >= yearlyFamilyExpense) {

                money -= yearlyFamilyExpense;

                addLifeEvent(
                    "Annual family expenses of ₹"+
                    yearlyFamilyExpense+" were paid."
                );

            } else {

                happiness = Math.max(0,happiness-3);

                addLifeEvent(
                    "You struggled to manage annual family expenses of ₹"+
                    yearlyFamilyExpense+"."
                );
            }
        }

        if (childCount > 0) {

            addLifeEvent(
                "Your children celebrated another birthday. "+
                childCount+" child/children are growing up."
            );
        }

        if (married && age % 3 == 0) {

            happiness = Math.min(100,happiness+2);

            addLifeEvent(
                "You spent another year building your married life."
            );
        }
    }

    // AGE UP - MONTHLY AND YEARLY PROGRESSION

    void showAgeUpOptions() {
        String[] options = {"Age by 1 Month", "Age by 1 Year"};

        new android.app.AlertDialog.Builder(this)
            .setTitle("How much time should pass?")
            .setItems(options, (dialog, which) -> {
                if (which == 0) ageUp();
                else ageUpOneYear();
            })
            .setNegativeButton("CANCEL", null)
            .show();
    }

    void ageUp() {
        advanceGameMonths(1);
    }

    void ageUpOneYear() {
        advanceGameMonths(12);
    }

    void advanceGameMonths(int monthsToAdvance) {
        if (monthsToAdvance <= 0) return;

        boolean birthdayReached = false;

        for (int month = 0; month < monthsToAdvance; month++) {

            // Pay general-employment salary once for this in-game month.
            // The persistent month guard prevents repeat payments.
            if (age >= 18) {
                processMonthlyEmploymentSalary(
                        age * 12 + ageMonths + 1);
            }

            // Run one monthly finance cycle for adult characters.
            if (age >= 18) {
                if (financeManager == null) {
                    financeManager = createFinanceManager();
                }

                financeManager.setCash(money);
                financeManager.applyMonthlyFinance(false);

                money = (int)Math.max(
                    0, Math.min(Integer.MAX_VALUE, financeManager.getCash())
                );
            }

            ageMonths++;

            if (ageMonths >= 12) {
                ageMonths = 0;
                age++;
                birthdayReached = true;
                processBirthdayUpdates();
            }

            saveGame();
        }

        if (birthdayReached) {
            showBirthdayDialog();
        } else {
            saveGame();
            showDashboard();
            message("One month has passed.\\n\\nYour age is now "
                    + getFormattedAge() + ".");
        }
    }

    // General Employment salary is credited once per in-game month.
    void processMonthlyEmploymentSalary(int gameMonth) {
        EmploymentManager employment = new EmploymentManager(
                this, save, age, money, health, happiness,
                (newMoney, newHealth, newHappiness, description) -> {
                    money = Math.max(0, newMoney);
                    health = Math.max(0, Math.min(100, newHealth));
                    happiness = Math.max(0, Math.min(100, newHappiness));
                    addLifeEvent(description);
                    saveGame();
                }
        );
        employment.setMoney(money);
        int salaryPaid = employment.processMonthlySalary(gameMonth);

        if (salaryPaid > 0) {
            money = Math.max(0, employment.getMoney());
            recordFinancialTransaction(
                    "Monthly salary - " + employment.getJobName(),
                    salaryPaid,
                    money
            );
        }
    }

    // Annual systems run once when 12 months have been completed.
    void processBirthdayUpdates() {

        int cashBeforeAnnualAssets = money;
        money = AssetsManager.applyAnnualFinances(save, money);
        long annualAssetDelta = (long) money - cashBeforeAnnualAssets;
        if (annualAssetDelta != 0) {
            recordFinancialTransaction(
                    "Annual asset income and expenses",
                    annualAssetDelta,
                    money
            );
        }

        if (age >= 18) {
            if (financeManager == null) {
                financeManager = createFinanceManager();
            }

            financeManager.setCash(money);

            // Annual insurance only; the other finance systems have
            // already completed their 12 individual monthly updates.
            financeManager.getInsuranceManager().applyAnnualPremiums();

            money = (int)Math.max(
                0, Math.min(Integer.MAX_VALUE,
                    financeManager.getInsuranceManager().getCash())
            );

            financeManager.setCash(money);
            processAnnualCareerProgression();
        }

        processAnnualFamilyUpdate();

        String event;

        if (age == 1) {
            event = "You celebrated your first birthday with your family.";
        } else if (age == 5) {
            event = "You are ready to begin your school journey!";
        } else if (age == 6) {
            event = "Your first day at school begins!";
        } else if (age == 15) {
            event = "You are preparing for your SSLC examination.";
        } else if (age == 16) {
            event = "A new chapter begins. Choose your PUC stream.";
        } else if (age == 18) {
            event = "You are now an adult. Your future awaits.";
        } else {
            happiness = Math.max(0, happiness - 1);
            event = "Another year has passed. Your life continues.";
        }

        addLifeEvent(event);

        if (age >= 6 && age <= 14 && !schoolType.isEmpty()) {
            educationStatus = "Class " + (age - 5);
        }

        if (age == 16 && sslcPassed && !pucPassed) {
            educationStatus = "PUC admission pending";
        }

        if (age == 17 && !pucStream.isEmpty() && !pucPassed) {
            educationStatus = "PUC 2nd Year";
        }

        if (age >= 18 && !degreeCourse.isEmpty()
                && !degreeCompleted && pucPassed) {
            educationStatus = "Degree " + (age - 17) + " Year";
        }

        saveGame();
    }

    void showBirthdayDialog() {
        showDashboard();

        new android.app.AlertDialog.Builder(this)
            .setTitle("🎂 Happy Birthday!")
            .setMessage("You are now " + getFormattedAge()
                    + " old.\\n\\nAnother year of your life has been completed.")
            .setPositiveButton("Continue", (d, w) -> runAnnualHealthCheck())
            .show();
    }

    // HEALTH & WELLNESS SYSTEM

    void openHealth() {
        HealthManager healthManager = new HealthManager(
            this,
            save,
            age,
            money,
            health,
            (newMoney,newHealth,condition,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        healthManager.open();
    }

    void runAnnualHealthCheck() {
        HealthManager healthManager = new HealthManager(
            this,
            save,
            age,
            money,
            health,
            (newMoney,newHealth,condition,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));

                addLifeEvent(description);
                saveGame();

                // Mental wellness update runs after the annual health check.
                runAnnualMentalCheck();
            }
        );

        healthManager.applyAnnualHealthUpdate();
    }

    // MENTAL HEALTH & WELLNESS SYSTEM

    void openMentalHealth() {
        MentalHealthManager mentalManager = new MentalHealthManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,newStress,condition,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        mentalManager.open();
    }

    void runAnnualMentalCheck() {
        MentalHealthManager mentalManager = new MentalHealthManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,newStress,condition,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();

                // Random life event begins after both annual wellness checks.
                runRandomLifeEvent();
            }
        );

        mentalManager.applyAnnualMentalUpdate();
    }

    // CRIME & JUSTICE SYSTEM

    void openCrime() {
        CrimeManager crimeManager = new CrimeManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        crimeManager.open();
    }

    // DRIVING & VEHICLES SYSTEM

    void openVehicle() {
        VehicleManager vehicleManager = new VehicleManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        vehicleManager.open();
    }

    // REAL ESTATE & PROPERTY SYSTEM

    void openProperty() {
        PropertyManager propertyManager = new PropertyManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        propertyManager.setFinancialEventListener((category, amount, details, cashMovement) -> {
            if (cashMovement) {
                money = (int)Math.max(0, Math.min(Integer.MAX_VALUE, money + amount));
                recordFinancialTransaction(category, amount, money);
            } else {
                recordFinancialAssetEvent(category, amount, details);
            }
        });

        propertyManager.open();
    }

    // BUSINESS & ENTREPRENEURSHIP SYSTEM

    void openBusiness() {
        BusinessManager businessManager = new BusinessManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        businessManager.setFinancialEventListener((category, amount, details, cashMovement) -> {
            if (cashMovement) {
                money = (int)Math.max(0, Math.min(Integer.MAX_VALUE, money + amount));
                recordFinancialTransaction(category, amount, money);
            } else {
                recordFinancialAssetEvent(category, amount, details);
            }
        });

        businessManager.open();
    }

    // STOCK MARKET & INVESTMENTS SYSTEM

    void openStockMarket() {
        StockMarketManager stockManager = new StockMarketManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            (newMoney,newHealth,newHappiness,description) -> {
                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));

                addLifeEvent(description);
                saveGame();
                showDashboard();
            }
        );

        stockManager.open();
    }

    // RANDOM LIFE EVENTS 2.0

    void runRandomLifeEvent() {

        // Life events begin at age 5. Younger characters continue normally.
        if (age < 5) {
            showDashboard();
            return;
        }

        LifeEventsManager eventsManager = new LifeEventsManager(
            this,
            save,
            age,
            money,
            health,
            happiness,
            intelligence,
            (newMoney,newHealth,newHappiness,newIntelligence,description) -> {

                money = Math.max(0,newMoney);
                health = Math.max(0,Math.min(100,newHealth));
                happiness = Math.max(0,Math.min(100,newHappiness));
                intelligence = Math.max(0,Math.min(100,newIntelligence));

                addLifeEvent(description);
                saveGame();
                showDashboard();

                // Continue to education only after the random event is completed.
                if (age == 5 || age == 15 || age == 16 ||
                    age == 17 || age == 18) {
                    showEducation();
                }
            }
        );

        eventsManager.triggerRandomEvent();
    }

    void message(String msg) {
        new android.app.AlertDialog.Builder(this)
            .setTitle("DesiLife")
            .setMessage(msg)
            .setPositiveButton("OK",null)
            .show();
    }
}
