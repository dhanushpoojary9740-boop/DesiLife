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

    // Permanent examination records
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
        getWindow().getDecorView().setSystemUiVisibility(0);

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
        addText("DESILIFE",38,gold);
        addText("APNI ZINDAGI, APNE FAISLE",13,muted);

        addDivider();

        addText("Your Life. Your Story.",25,white);
        addText(
            "Born in India. Raised by choices. Build your own destiny.",
            15,muted
        );

        addDivider();

        addButton("✦ START A NEW LIFE", () -> showCreate());

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
        addText("Version 1.3",12,muted);
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
        Spinner genderSpinner = addSpinner(
            new String[]{"Male","Female"}
        );

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
        Spinner familySpinner = addSpinner(new String[]{
            "Poor","Middle Class","Rich"
        });

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

        // Recover existing progress from earlier game versions.
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
            educationStatus = "Class "+(age-5);
        }
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
        addText("💰 Money: ₹"+money,19,gold);

        addDivider();

        addText("YOUR LIFE JOURNEY",20,gold);

        addButton("🎂 AGE UP - NEXT YEAR", () -> ageUp());

        addButton("👨‍👩‍👧 FAMILY", () ->
            message("Family activities are coming soon.")
        );

        addButton("📚 EDUCATION", () -> showEducation());

        addButton("💼 CAREER", () ->
            message("Career opportunities are coming soon.")
        );

        addButton("❤️ RELATIONSHIPS", () ->
            message("Relationships are coming soon.")
        );

        addButton("🏠 PROPERTY", () ->
            message("Houses and vehicles are coming soon.")
        );

        addButton("💾 SAVE GAME", () -> {
            saveGame();
            message("Your life has been saved!");
        });
    }

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
                "Government School",
                "Private School"
            });

            addButton("ADMIT TO SCHOOL", () -> {
                schoolType = school.getSelectedItem().toString();

                educationStatus = "School admission completed";

                if (schoolType.equals("Private School")) {
                    money = Math.max(0,money-5000);
                }

                happiness = Math.min(100,happiness+5);
                saveGame();
                showEducation();
            });

        } else if (age >= 6 && age <= 14) {

            int currentClass = age - 5;

            addText("🏫 "+schoolType,18,gold);
            addText("Currently studying in Class "+currentClass,20,white);

            addText(
                "Study hard to improve your intelligence "+
                "and prepare for your future exams.",
                16,muted
            );

            addButton("📖 STUDY HARD", () -> {
                intelligence = Math.min(100,intelligence+5);
                happiness = Math.max(0,happiness-2);
                educationStatus = "Class "+currentClass;
                saveGame();
                showEducation();
                message("You studied hard! Intelligence increased.");
            });

            addButton("🎨 SCHOOL ACTIVITIES", () -> {
                happiness = Math.min(100,happiness+5);
                saveGame();
                showEducation();
                message("You enjoyed your school activities!");
            });

        } else if (age == 15) {

            addText("🏫 CLASS 10 – SSLC",21,gold);

            if (sslcPassed) {
                addText("SSLC: PASSED ✓",20,white);
                addText("Your SSLC examination is completed.",16,muted);

            } else if (sslcAttempted) {
                addText("SSLC: FAILED",20,white);
                addText(
                    "Your examination attempt is completed.",
                    16,muted
                );

            } else {
                addText("Your board examination is approaching.",16,white);

                addButton("📝 TAKE SSLC EXAM", () ->
                    takeExam("SSLC")
                );
            }

        } else if (age >= 16 && age <= 17) {

            addText("🎓 PRE-UNIVERSITY COURSE",21,gold);

            if (!sslcPassed) {

                addText(
                    "You must pass SSLC before joining PUC.",
                    16,white
                );

            } else if (pucPassed) {

                addText("PUC: PASSED ✓",20,gold);
                addText("Your PUC examination is completed.",16,white);

            } else if (pucAttempted) {

                addText("PUC: FAILED",20,white);
                addText(
                    "Your PUC examination attempt is completed.",
                    16,muted
                );

            } else if (pucStream.isEmpty()) {

                addText("Choose your PUC stream",17,white);

                Spinner stream = addSpinner(new String[]{
                    "Science","Commerce","Arts"
                });

                addButton("JOIN PUC", () -> {
                    pucStream = stream.getSelectedItem().toString();
                    educationStatus = "PUC 1st Year";

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

                addButton("📖 STUDY HARD", () -> {
                    intelligence = Math.min(100,intelligence+5);
                    saveGame();
                    showEducation();
                    message("Your intelligence increased!");
                });

                if (age == 17) {
                    addButton("📝 TAKE PUC EXAM", () ->
                        takeExam("PUC")
                    );
                }
            }

        } else if (age >= 18) {

            addText("🎓 HIGHER EDUCATION",22,gold);

            // IMPORTANT: Check permanent PUC result,
            // not the changing educationStatus string.

            if (!pucPassed) {

                if (!sslcPassed) {
                    addText(
                        "You must pass SSLC before higher education.",
                        16,white
                    );

                } else if (pucAttempted) {

                    addText("PUC: FAILED",20,white);
                    addText(
                        "Your PUC examination attempt is completed.",
                        16,muted
                    );

                } else if (pucStream.isEmpty()) {

                    addText("Choose your PUC stream",17,white);

                    Spinner stream = addSpinner(new String[]{
                        "Science","Commerce","Arts"
                    });

                    addButton("JOIN PUC", () -> {
                        pucStream = stream.getSelectedItem().toString();
                        educationStatus = "PUC 1st Year";
                        saveGame();
                        showEducation();
                    });

                } else {

                    addText("PUC Stream: "+pucStream,18,white);

                    addButton("📝 TAKE PUC EXAM", () ->
                        takeExam("PUC")
                    );
                }

            } else if (degreeCompleted) {

                addText("🎓 DEGREE COMPLETED ✓",22,gold);
                addText("Course: "+degreeCourse,18,white);
                addText("Congratulations! You are a graduate.",16,muted);

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

                addButton("JOIN COLLEGE", () -> {
                    degreeCourse = course.getSelectedItem().toString();
                    educationStatus = "Degree 1st Year";

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
                    addButton("🎓 COMPLETE DEGREE", () -> {

                        if (!degreeCompleted) {
                            degreeCompleted = true;
                            educationStatus = "Graduated";

                            intelligence =
                                Math.min(100,intelligence+10);

                            happiness =
                                Math.min(100,happiness+10);

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

        addButton("BACK TO LIFE", () -> showDashboard());
    }

    void takeExam(String exam) {

        // Prevent repeated examinations.
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

        examMarks = 40 + (int)(Math.random()*61);

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

        // Save the result immediately.
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

    void ageUp() {

        age++;

        String event = "";

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

        if (age >= 6 && age <= 14 && !schoolType.isEmpty()) {
            educationStatus = "Class "+(age-5);
        }

        if (age == 16 && sslcPassed && !pucPassed) {
            educationStatus = "PUC admission pending";
        }

        if (age == 17 && !pucStream.isEmpty() && !pucPassed) {
            educationStatus = "PUC 2nd Year";
        }

        // Never overwrite the passed PUC result or graduation.
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
            .setPositiveButton("Continue", (d,w) -> {
                if (age == 5 || age == 15 || age == 16 ||
                    age == 17 || age == 18) {
                    showEducation();
                }
            })
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
