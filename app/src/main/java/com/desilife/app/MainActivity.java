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
    SharedPreferences save;

    String playerName;
    String gender;
    String city;
    String family;

    int age = 0;
    int health = 100;
    int happiness = 100;
    int intelligence = 50;
    int money = 5000;

    TextView ageText;
    TextView moneyText;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);

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

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22,25,22,30);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(bg);

        scroll.addView(root);
        setContentView(scroll);
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

    void showHome() {
        setup();

        addText("🇮🇳",55,gold);

        TextView title = text("DESILIFE",38,gold);
        title.setTypeface(null,Typeface.BOLD);
        root.addView(title);

        addText("APNI ZINDAGI, APNE FAISLE",13,muted);

        addDivider();

        addText("Your Life. Your Story.",25,white);

        addText(
            "Born in India. Raised by choices. Build your own destiny.",
            15,muted
        );

        addDivider();

        addButton("✦  START A NEW LIFE", () -> showCreate());

        addButton("CONTINUE GAME", () -> {
            if (save.getBoolean("created",false)) {
                loadGame();
                showDashboard();
            } else {
                message("No saved life yet. Start your first life!");
            }
        });

        addDivider();

        addText("AN INDIAN LIFE SIMULATION",12,gold);
        addText("Version 1.1",12,muted);
    }

    void showCreate() {
        setup();

        addText("🇮🇳",40,gold);
        addText("CREATE YOUR LIFE",27,gold);

        addText(
            "Every great journey starts with a birth.",
            15,white
        );

        addDivider();

        addText("What is your character's name?",16,white);

        EditText name = addInput("Enter your name");

        addText("Select gender",16,white);

        Spinner genderSpinner = addSpinner(
            new String[]{"Male","Female"}
        );

        addText("Choose birthplace",16,white);

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

        addText("Choose family background",16,white);

        Spinner familySpinner = addSpinner(new String[]{
            "Poor",
            "Middle Class",
            "Rich"
        });

        addDivider();

        addButton("CREATE MY LIFE", () -> {

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

            if (family.equals("Poor")) {
                money = 500;
            } else if (family.equals("Rich")) {
                money = 500000;
            } else {
                money = 5000;
            }

            saveGame();
            showDashboard();
        });

        addButton("BACK", () -> showHome());
    }

    void saveGame() {
        save.edit()
            .putBoolean("created",true)
            .putString("name",playerName)
            .putString("gender",gender)
            .putString("city",city)
            .putString("family",family)
            .putInt("age",age)
            .putInt("health",health)
            .putInt("happiness",happiness)
            .putInt("intelligence",intelligence)
            .putInt("money",money)
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
    }

    void showDashboard() {
        setup();

        addText("🇮🇳 DESILIFE",25,gold);

        addDivider();

        addText("Hello, "+playerName+"!",27,white);

        addText("Age: "+age+" years",21,gold);

        addText(city+" | India",15,muted);
        addText("Family: "+family,15,muted);
        addText("Gender: "+gender,15,muted);

        addDivider();

        addText("YOUR LIFE STATS",20,gold);

        addText("❤️ Health: "+health+"/100",17,white);
        addText("😊 Happiness: "+happiness+"/100",17,white);
        addText("🧠 Intelligence: "+intelligence+"/100",17,white);

        moneyText = text(
            "💰 Money: ₹"+money,19,gold
        );
        root.addView(moneyText);

        addDivider();

        addText("YOUR LIFE JOURNEY",20,gold);

        addButton("🎂  AGE UP - NEXT YEAR", () -> ageUp());

        addButton("👨‍👩‍👧  FAMILY", () ->
            message("Family activities will be added in the next update.")
        );

        addButton("📚  EDUCATION", () ->
            message("Indian schools and colleges are coming soon.")
        );

        addButton("💼  CAREER", () ->
            message("Career opportunities are coming soon.")
        );

        addButton("❤️  RELATIONSHIPS", () ->
            message("Relationships are coming soon.")
        );

        addButton("🏠  PROPERTY", () ->
            message("Houses and vehicles are coming soon.")
        );

        addButton("💾  SAVE GAME", () -> {
            saveGame();
            message("Your life has been saved!");
        });
    }

    void ageUp() {
        age++;

        String event = "";

        if (age == 1) {
            event = "You celebrated your first birthday with your family.";
        } else if (age == 5) {
            intelligence = Math.min(100,intelligence+5);
            event = "You started exploring the world around you.";
        } else if (age == 6) {
            event = "Your school journey begins!";
        } else if (age == 18) {
            event = "You are now an adult. A new chapter begins!";
        } else if (age == 21) {
            event = "Your future is in your hands. Make wise decisions.";
        } else {
            happiness = Math.max(0,happiness-1);
            event = "Another year has passed. What will you do next?";
        }

        saveGame();
        showDashboard();

        new android.app.AlertDialog.Builder(this)
            .setTitle("🎂 Happy Birthday!")
            .setMessage(
                "You are now "+age+" years old.\n\n"+event
            )
            .setPositiveButton("Continue",null)
            .show();
    }

    void message(String msg) {
        new android.app.AlertDialog.Builder(this)
            .setTitle("DesiLife")
            .setMessage(msg)
            .setPositiveButton("OK",null)
            .show();
    }
}
