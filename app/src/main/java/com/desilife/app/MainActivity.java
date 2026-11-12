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

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
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
        root.setPadding(22,25,22,30);
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
        root.setPadding(12,12,12,16);
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
        TextView b = text(label,16,bg);
        b.setTypeface(null,Typeface.BOLD);
        b.setBackground(shape(gold,28));
        b.setPadding(12,16,12,16);

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
        e.setBackground(shape(Color.rgb(35,48,44),14));

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
        nav.setBackground(shape(Color.rgb(5,83,145),12));

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

                } else {
                    showHome();
                }
            })
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

        addText("🇮🇳",40,gold);
        addText("CREATE YOUR LIFE",27,gold);
        addText("Every great journey starts with a birth.",15,white);

        addDivider();

        addText("Character name",16,white);
        EditText name = addInput("Enter your name");

        addText("Gender",16,white);
        Spinner genderSpinner =
            addSpinner(new String[]{"Male","Female"});

        addText("Birthplace",16,white);

        Spinner citySpinner = addSpinner(new String[]{
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

        addText("Family background",16,white);

        Spinner familySpinner =
            addSpinner(new String[]{"Poor","Middle Class","Rich"});

        addButton("CREATE MY LIFE",() -> {

            String n = name.getText().toString().trim();

            if (n.isEmpty()) {
                name.setError("Please enter your name");
                return;
            }

            playerName = n;
            gender = genderSpinner.getSelectedItem().toString();
            city = citySpinner.getSelectedItem().toString();
            family = familySpinner.getSelectedItem().toString();

            age = 0;
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

        addButton("BACK",() -> showHome());
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

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(14,12,14,12);
        header.setBackground(shape(Color.rgb(218,48,25),18));

        TextView menu = dashboardText("☰",25,white,true);
        menu.setOnClickListener(v -> showDashboardMenu());

        header.addView(menu,
            new LinearLayout.LayoutParams(42,-2));

        TextView brand = dashboardText(
            "🇮🇳 DesiLife",25,Color.WHITE,true
        );

        brand.setGravity(Gravity.CENTER_VERTICAL);

        header.addView(brand,
            new LinearLayout.LayoutParams(0,-2,1));

        TextView fame = dashboardText(
            "★ 0",23,Color.rgb(255,220,70),true
        );

        header.addView(fame,
            new LinearLayout.LayoutParams(-2,-2));

        root.addView(header,
            new LinearLayout.LayoutParams(-1,-2));

        LinearLayout identity = new LinearLayout(this);
        identity.setOrientation(LinearLayout.HORIZONTAL);
        identity.setGravity(Gravity.CENTER_VERTICAL);
        identity.setPadding(12,12,12,12);
        identity.setBackground(shape(Color.rgb(229,237,243),14));

        TextView avatar = dashboardText(
            gender.equalsIgnoreCase("Female") ? "👩" : "👦",
            42,Color.rgb(25,75,110),false
        );

        identity.addView(avatar,
            new LinearLayout.LayoutParams(58,-2));

        LinearLayout identityInfo = new LinearLayout(this);
        identityInfo.setOrientation(LinearLayout.VERTICAL);

        TextView nameView = dashboardText(
            playerName,21,Color.rgb(12,78,126),true
        );

        nameView.setGravity(Gravity.LEFT);

        TextView detailView = dashboardText(
            age+" years • "+gender+" • "+city,
            12,Color.rgb(55,91,119),false
        );

        detailView.setGravity(Gravity.LEFT);

        identityInfo.addView(nameView);
        identityInfo.addView(detailView);

        identity.addView(identityInfo,
            new LinearLayout.LayoutParams(0,-2,1));

        TextView balance = dashboardText(
            "₹"+money+"\nBank Balance",
            16,Color.rgb(12,78,126),true
        );

        identity.addView(balance,
            new LinearLayout.LayoutParams(-2,-2));

        LinearLayout.LayoutParams identityParams =
            new LinearLayout.LayoutParams(-1,-2);

        identityParams.setMargins(0,8,0,8);
        root.addView(identity,identityParams);

        LinearLayout journal = new LinearLayout(this);
        journal.setOrientation(LinearLayout.VERTICAL);
        journal.setPadding(14,12,14,12);
        journal.setBackground(shape(Color.WHITE,14));

        TextView ageHeading = dashboardText(
            "Age: "+age+" years",19,
            Color.rgb(17,82,130),true
        );

        ageHeading.setGravity(Gravity.LEFT);
        journal.addView(ageHeading);

        TextView birthInfo = dashboardText(
            "I was born in "+city+
            ". My family background is "+family.toLowerCase()+".",
            14,Color.rgb(95,95,95),false
        );

        birthInfo.setGravity(Gravity.LEFT);
        journal.addView(birthInfo);

        TextView storyHeading = dashboardText(
            "YOUR LIFE JOURNEY",16,
            Color.rgb(17,82,130),true
        );

        storyHeading.setGravity(Gravity.LEFT);
        journal.addView(storyHeading);

        TextView story = dashboardText(
            getCurrentLifeStory(),14,
            Color.rgb(95,95,95),false
        );

        story.setGravity(Gravity.LEFT);
        journal.addView(story);

        LinearLayout.LayoutParams journalParams =
            new LinearLayout.LayoutParams(-1,-2);

        journalParams.setMargins(0,0,0,8);
        root.addView(journal,journalParams);

        addLifeEventsPanel();

        TextView ageUpButton = dashboardText(
            "＋\nAGE UP",18,Color.WHITE,true
        );

        ageUpButton.setBackground(
            shape(Color.rgb(39,153,67),100)
        );

        ageUpButton.setElevation(8);

        LinearLayout.LayoutParams ageUpParams =
            new LinearLayout.LayoutParams(92,92);

        ageUpParams.gravity = Gravity.CENTER_HORIZONTAL;
        ageUpParams.setMargins(0,2,0,8);

        root.addView(ageUpButton,ageUpParams);
        ageUpButton.setOnClickListener(v -> ageUp());

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.VERTICAL);
        stats.setPadding(12,12,12,12);
        stats.setBackground(shape(Color.WHITE,14));

        TextView statsTitle = dashboardText(
            "YOUR LIFE STATS",18,
            Color.rgb(17,82,130),true
        );

        statsTitle.setGravity(Gravity.LEFT);
        stats.addView(statsTitle);

        addDashboardStat(stats,"Happiness","😊",happiness,
            Color.rgb(42,157,75));

        addDashboardStat(stats,"Health","❤️",health,
            Color.rgb(42,157,75));

        addDashboardStat(stats,"Smarts","🧠",intelligence,
            Color.rgb(42,157,75));

        addDashboardStat(stats,"Looks","✨",65,
            Color.rgb(235,145,35));

        root.addView(stats,
            new LinearLayout.LayoutParams(-1,-2));

        TextView education = dashboardText(
            "📚 EDUCATION",16,bg,true
        );

        education.setBackground(
            shape(Color.rgb(229,184,105),18)
        );

        education.setPadding(12,14,12,14);

        LinearLayout.LayoutParams educationParams =
            new LinearLayout.LayoutParams(-1,-2);

        educationParams.setMargins(0,12,0,4);

        root.addView(education,educationParams);
        education.setOnClickListener(v -> showEducation());

        TextView familyButton = dashboardText(
            "👨‍👩‍👧 FAMILY & LEGACY",16,bg,true
        );

        familyButton.setBackground(
            shape(Color.rgb(229,184,105),18)
        );

        familyButton.setPadding(12,14,12,14);

        LinearLayout.LayoutParams familyParams =
            new LinearLayout.LayoutParams(-1,-2);

        familyParams.setMargins(0,8,0,4);

        root.addView(familyButton,familyParams);
        familyButton.setOnClickListener(v -> openFamily());

        TextView saveButton = dashboardText(
            "💾 SAVE GAME",15,white,true
        );

        saveButton.setBackground(
            shape(Color.rgb(49,63,59),18)
        );

        saveButton.setPadding(12,13,12,13);

        root.addView(saveButton,
            new LinearLayout.LayoutParams(-1,-2));

        saveButton.setOnClickListener(v -> {
            saveGame();
            message("Your life has been saved!");
        });

        dashboardShell.addView(createBottomNavigation());
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
            icon,23,Color.WHITE,true
        );

        TextView labelView = dashboardText(
            label,11,Color.WHITE,false
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
        addText("Age: "+age+" years",16,muted);

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

    // CAREER

    void openCareer() {
        CareerManager career = new CareerManager(
            this,
            save,
            age,
            intelligence,
            educationStatus+" "+degreeCourse,
            degreeCompleted,
            money,
            newMoney -> {
                money = (int)Math.max(
                    0,Math.min(Integer.MAX_VALUE,newMoney)
                );

                addLifeEvent("You made a career decision or received career income.");

                saveGame();
            }
        );

        career.open();
    }

    void processCareerSalary() {
        if (age < 18) return;

        CareerManager career = new CareerManager(
            this,
            save,
            age,
            intelligence,
            educationStatus+" "+degreeCourse,
            degreeCompleted,
            money,
            newMoney -> {
                money = (int)Math.max(
                    0,Math.min(Integer.MAX_VALUE,newMoney)
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
        return new FinanceManager(
            this,
            money,
            newMoney -> {
                money = (int)Math.max(
                    0,Math.min(Integer.MAX_VALUE,newMoney)
                );

                addLifeEvent("You completed a banking or finance transaction.");

                saveGame();
            }
        );
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

    // AGE UP

    void ageUp() {

        age++;

        money = AssetsManager.applyAnnualFinances(save,money);

        if (age >= 18) {

            if (financeManager == null) {
                financeManager = createFinanceManager();
            }

            financeManager.setCash(money);
            financeManager.applyAnnualFinance();

            money = (int)Math.max(
                0,Math.min(Integer.MAX_VALUE,financeManager.getCash())
            );

            processCareerSalary();
        }

        // Process family responsibilities and children's birthdays.
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
            happiness = Math.max(0,happiness-1);
            event = "Another year has passed. Your life continues.";
        }

        addLifeEvent(event);

        if (age >= 6 && age <= 14 && !schoolType.isEmpty()) {
            educationStatus = "Class "+(age-5);
        }

        if (age == 16 && sslcPassed && !pucPassed) {
            educationStatus = "PUC admission pending";
        }

        if (age == 17 && !pucStream.isEmpty() && !pucPassed) {
            educationStatus = "PUC 2nd Year";
        }

        if (age >= 18 && !degreeCourse.isEmpty()
                && !degreeCompleted && pucPassed) {
            educationStatus = "Degree "+(age-17)+" Year";
        }

        saveGame();
        showDashboard();

        new android.app.AlertDialog.Builder(this)
            .setTitle("🎂 Happy Birthday!")
            .setMessage(
                "You are now "+age+" years old.\n\n"+event
            )
            .setPositiveButton("Continue",(d,w) -> {
                runRandomLifeEvent();
            })
            .show();
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
