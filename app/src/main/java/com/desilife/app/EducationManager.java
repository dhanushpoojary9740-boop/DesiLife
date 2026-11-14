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
import android.widget.Toast;

import java.util.Random;

public class EducationManager {

    public interface EducationCallback {
        void onUpdate(int money, int health, int happiness, String description);
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final EducationCallback callback;
    private final Random random = new Random();

    private int age, money, health, happiness;
    private int marks, attendance, academicPerformance, educationYears;
    private int fees, scholarships, entranceScore, examsPassed;
    private int examsFailed, educationLoans, hostelExpenses;
    private int currentAnnualFee, requiredYears;

    private String educationLevel, institution, institutionCity;
    private String institutionState, stream, course, entranceExam, grade;
    private String pendingHigherStudy = "None";
    private String pendingAdmissionLevel = "None";

    private boolean enrolled, graduated, entranceCleared, hostel;
    private boolean waitingForEntranceAdmission;

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 190, 185);

    public EducationManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            EducationCallback callback
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

    // ---------------- SAVE / LOAD ----------------

    private void loadData() {
        educationLevel = save.getString("edu_level", "Not Started");
        institution = save.getString("edu_institution", "None");
        institutionCity = save.getString("edu_city", "None");
        institutionState = save.getString("edu_state", "None");
        stream = save.getString("edu_stream", "None");
        course = save.getString("edu_course", "None");
        entranceExam = save.getString("edu_entrance_exam", "None");
        grade = save.getString("edu_grade", "Not Available");

        marks = save.getInt("edu_marks", 0);
        attendance = save.getInt("edu_attendance", 0);
        academicPerformance = save.getInt("edu_performance", 50);
        educationYears = save.getInt("edu_years", 0);
        fees = save.getInt("edu_fees", 0);
        scholarships = save.getInt("edu_scholarships", 0);
        entranceScore = save.getInt("edu_entrance_score", 0);
        examsPassed = save.getInt("edu_exams_passed", 0);
        examsFailed = save.getInt("edu_exams_failed", 0);
        educationLoans = save.getInt("edu_loans", 0);
        hostelExpenses = save.getInt("edu_hostel_expenses", 0);
        currentAnnualFee = save.getInt("edu_annual_fee", 0);
        requiredYears = save.getInt("edu_required_years", 0);

        pendingHigherStudy = save.getString(
                "edu_pending_higher", "None");

        pendingAdmissionLevel = save.getString(
                "edu_pending_admission", "None");

        enrolled = save.getBoolean("edu_enrolled", false);
        graduated = save.getBoolean("edu_graduated", false);
        entranceCleared = save.getBoolean("edu_entrance_cleared", false);
        hostel = save.getBoolean("edu_hostel", false);
        waitingForEntranceAdmission = save.getBoolean(
                "edu_waiting_entrance", false);

        normalize();
    }

    private void saveData() {
        SharedPreferences.Editor e = save.edit();

        e.putString("edu_level", educationLevel);
        e.putString("edu_institution", institution);
        e.putString("edu_city", institutionCity);
        e.putString("edu_state", institutionState);
        e.putString("edu_stream", stream);
        e.putString("edu_course", course);
        e.putString("edu_entrance_exam", entranceExam);
        e.putString("edu_grade", grade);
        e.putString("edu_pending_higher", pendingHigherStudy);
        e.putString("edu_pending_admission", pendingAdmissionLevel);

        e.putInt("edu_marks", marks);
        e.putInt("edu_attendance", attendance);
        e.putInt("edu_performance", academicPerformance);
        e.putInt("edu_years", educationYears);
        e.putInt("edu_fees", fees);
        e.putInt("edu_scholarships", scholarships);
        e.putInt("edu_entrance_score", entranceScore);
        e.putInt("edu_exams_passed", examsPassed);
        e.putInt("edu_exams_failed", examsFailed);
        e.putInt("edu_loans", educationLoans);
        e.putInt("edu_hostel_expenses", hostelExpenses);
        e.putInt("edu_annual_fee", currentAnnualFee);
        e.putInt("edu_required_years", requiredYears);

        e.putBoolean("edu_enrolled", enrolled);
        e.putBoolean("edu_graduated", graduated);
        e.putBoolean("edu_entrance_cleared", entranceCleared);
        e.putBoolean("edu_hostel", hostel);
        e.putBoolean("edu_waiting_entrance", waitingForEntranceAdmission);

        e.apply();
    }

    private void normalize() {
        marks = clamp(marks, 0, 100);
        attendance = clamp(attendance, 0, 100);
        academicPerformance = clamp(academicPerformance, 0, 100);
        fees = Math.max(0, fees);
        scholarships = Math.max(0, scholarships);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private int safeAdd(int a, int b) {
        return (int) Math.min(
                Integer.MAX_VALUE,
                Math.max(0L, (long) a + b)
        );
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    // ---------------- PUBLIC METHODS ----------------

    public void open() {
        showDashboard();
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public boolean hasGraduated() {
        return graduated;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public String getInstitution() {
        return institution;
    }

    public int getMarks() {
        return marks;
    }

    public void setMoney(int newMoney) {
        money = Math.max(0, newMoney);
    }

    public int getMoney() {
        return money;
    }

    // ---------------- DASHBOARD ----------------

    private void showDashboard() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 25, 24, 30);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);

        TextView title = new TextView(activity);
        title.setText("🎓 EDUCATION & UNIVERSITY");
        title.setTextColor(gold);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);
        root.addView(title);

        addInfo(root, "Education Level", educationLevel);
        addInfo(root, "Institution", institution);
        addInfo(root, "City", institutionCity);
        addInfo(root, "State", institutionState);
        addInfo(root, "Stream", stream);
        addInfo(root, "Course", course);
        addInfo(root, "Academic Performance",
                academicPerformance + "/100");
        addInfo(root, "Last Exam Marks", marks + "/100");
        addInfo(root, "Attendance", attendance + "%");
        addInfo(root, "Grade", grade);
        addInfo(root, "Years Studied", String.valueOf(educationYears));
        addInfo(root, "Scholarships Received", "₹" + scholarships);
        addInfo(root, "Total Education Fees", "₹" + fees);

        addDivider(root);

        if (enrolled && !graduated) {
            addButton(root, "📚 Attend Classes", this::attendClasses);
            addButton(root, "📝 Write Examination", this::writeExam);
            addButton(root, "💰 Pay Education Fees", this::payFees);
            addButton(root, "🏠 Hostel & Accommodation", this::hostelMenu);
            addButton(root, "🎖 Apply for Scholarship", this::applyScholarship);
            addButton(root, "📊 Academic Report", this::academicReport);
            addButton(root, "🚪 Drop Out", this::dropOut);
        } else {
            addButton(root, "🏫 Find a School / College",
                    this::chooseEducationLevel);

            addButton(root, "📝 Entrance Examinations",
                    this::chooseEntranceExam);

            if (graduated) {
                if (educationLevel.contains("Undergraduate Degree")
                        || educationLevel.contains("Bachelor")) {
                    addButton(root, "🎓 Study Another Degree",
                            this::startAnotherDegree);
                }
                addButton(root, "📚 Higher Studies", this::higherStudies);
            }

            addButton(root, "📊 Academic Report", this::academicReport);
        }

        addButton(root, "⬅ Back to Game", activity::recreate);
        activity.setContentView(scroll);
    }

    // ---------------- UI HELPERS ----------------

    private void addInfo(LinearLayout root, String label, String value) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 12, 16, 12);
        card.setBackground(background(Color.rgb(28, 58, 50), 14));

        TextView heading = new TextView(activity);
        heading.setText(label);
        heading.setTextColor(muted);
        heading.setTextSize(12);

        TextView data = new TextView(activity);
        data.setText(value);
        data.setTextColor(white);
        data.setTextSize(16);
        data.setTypeface(null, Typeface.BOLD);
        data.setPadding(0, 4, 0, 0);

        card.addView(heading);
        card.addView(data);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 4, 0, 4);
        root.addView(card, params);
    }

    private void addButton(LinearLayout root, String label, Runnable action) {
        TextView button = new TextView(activity);
        button.setText(label);
        button.setTextColor(dark);
        button.setTextSize(16);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(14, 17, 14, 17);
        button.setBackground(background(Color.rgb(239, 202, 126), 17));
        button.setElevation(3);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 7, 0, 7);

        root.addView(button, params);
        button.setOnClickListener(v -> action.run());
    }

    private void addDivider(LinearLayout root) {
        TextView divider = new TextView(activity);
        divider.setBackgroundColor(Color.rgb(76, 105, 89));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 1);
        params.setMargins(0, 18, 0, 12);
        root.addView(divider, params);
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // ---------------- EDUCATION PROGRESSION ----------------

    private boolean completedLevel(String level) {
        return educationLevel.equals(level)
                || educationLevel.equals(level + " - Completed");
    }

    private boolean hasCompletedPrimary() {
        return completedLevel("Primary School")
                || completedLevel("High School")
                || completedLevel("PUC / Class 11–12")
                || educationLevel.contains("Degree")
                || educationLevel.contains("Master")
                || educationLevel.contains("MBA")
                || educationLevel.contains("M.Tech")
                || educationLevel.contains("PhD");
    }

    private boolean hasCompletedHighSchool() {
        return completedLevel("High School")
                || completedLevel("PUC / Class 11–12")
                || educationLevel.contains("Degree")
                || educationLevel.contains("Master")
                || educationLevel.contains("MBA")
                || educationLevel.contains("M.Tech")
                || educationLevel.contains("PhD");
    }

    private boolean hasCompletedPUC() {
        return completedLevel("PUC / Class 11–12")
                || educationLevel.contains("Degree")
                || educationLevel.contains("Master")
                || educationLevel.contains("MBA")
                || educationLevel.contains("M.Tech")
                || educationLevel.contains("PhD");
    }

    private void chooseEducationLevel() {
        String[] options = {
                "Primary School",
                "High School",
                "PUC / Class 11–12",
                "Undergraduate Degree"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Education Level")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        chooseInstitution("Primary School");
                    } else if (which == 1) {
                        chooseInstitution("High School");
                    } else if (which == 2) {
                        chooseStream();
                    } else {
                        chooseCourse();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseCourse() {
        if (!hasCompletedPUC()) {
            toast("Complete PUC / Class 11–12 before applying for a degree.");
            return;
        }
        chooseInstitution("Undergraduate Degree");
    }

    private void chooseStream() {
        if (!hasCompletedHighSchool()) {
            toast("Complete High School before joining PUC.");
            return;
        }

        String[] streams = {
                "Science",
                "Commerce",
                "Arts / Humanities"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose PUC Stream")
                .setItems(streams, (dialog, which) -> {
                    stream = streams[which];
                    chooseInstitution("PUC / Class 11–12");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- STATE SELECTION ----------------

    private void chooseInstitution(String level) {
        final String[] states = {
                "Karnataka",
                "Tamil Nadu",
                "Maharashtra",
                "Delhi",
                "Uttar Pradesh",
                "West Bengal",
                "Rajasthan",
                "Kerala",
                "Telangana",
                "Gujarat",
                "Punjab"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose State")
                .setItems(states, (dialog, which) -> {
                    institutionState = states[which];
                    showInstitutions(level);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- INSTITUTION DATABASE ----------------

    private void showInstitutions(String level) {
        String[] names;
        String[] cities;
        int[] costs;

        if (level.equals("Primary School")
                || level.equals("High School")) {

            if (institutionState.equals("Karnataka")) {
                names = new String[]{
                        "Bishop Cotton Boys' School",
                        "Bishop Cotton Girls' School",
                        "National Public School",
                        "St. Joseph's Boys' High School",
                        "The Valley School",
                        "Delhi Public School Bengaluru",
                        "Mysore Public School",
                        "Demonstration School Mysuru",
                        "Kendriya Vidyalaya",
                        "Jawahar Navodaya Vidyalaya"
                };

                cities = new String[]{
                        "Bengaluru", "Bengaluru", "Bengaluru",
                        "Bengaluru", "Bengaluru", "Bengaluru",
                        "Mysuru", "Mysuru", "Various Cities",
                        "Various Districts"
                };

                costs = new int[]{
                        80000, 80000, 65000, 50000, 90000,
                        55000, 30000, 15000, 12000, 5000
                };
            } else {
                names = new String[]{
                        "The Doon School",
                        "Mayo College",
                        "Delhi Public School",
                        "Modern School",
                        "La Martiniere",
                        "Don Bosco School",
                        "St. Xavier's School",
                        "Kendriya Vidyalaya",
                        "Jawahar Navodaya Vidyalaya"
                };

                cities = new String[]{
                        "Dehradun", "Ajmer", "Multiple Cities",
                        "New Delhi", "Kolkata", "Multiple Cities",
                        "Multiple Cities", "Various Cities",
                        "Various Districts"
                };

                costs = new int[]{
                        500000, 400000, 70000, 100000,
                        80000, 60000, 65000, 12000, 5000
                };
            }

        } else if (level.equals("PUC / Class 11–12")) {

            if (institutionState.equals("Karnataka")) {
                names = new String[]{
                        "St. Joseph's PU College",
                        "Christ Junior College",
                        "MES PU College",
                        "Mount Carmel PU College",
                        "Deeksha PU College",
                        "Expert PU College",
                        "Alva's PU College",
                        "Mahatma Gandhi Memorial PU College",
                        "Government PU College",
                        "Kendriya Vidyalaya"
                };

                cities = new String[]{
                        "Bengaluru", "Bengaluru", "Bengaluru",
                        "Bengaluru", "Bengaluru", "Mangaluru",
                        "Moodbidri", "Udupi", "Various Cities",
                        "Various Cities"
                };

                costs = new int[]{
                        30000, 35000, 25000, 40000, 50000,
                        55000, 25000, 18000, 5000, 12000
                };
            } else {
                names = new String[]{
                        "Delhi Public School",
                        "St. Xavier's College",
                        "Loyola College",
                        "Mayo College",
                        "Modern School",
                        "Kendriya Vidyalaya"
                };

                cities = new String[]{
                        "Multiple Cities", "Mumbai", "Chennai",
                        "Ajmer", "New Delhi", "Various Cities"
                };

                costs = new int[]{
                        70000, 50000, 45000, 400000, 100000, 12000
                };
            }

        } else {
            getUniversityList(level);
            return;
        }

        showInstitutionChoices(level, names, cities, costs);
    }

    // ---------------- UNIVERSITY DATABASE ----------------

    private void getUniversityList(String level) {
        String[] names;
        String[] cities;
        int[] costs;

        if (institutionState.equals("Karnataka")) {
            names = new String[]{
                    "Indian Institute of Science (IISc)",
                    "IIM Bangalore",
                    "National Law School of India University",
                    "Christ University",
                    "RV College of Engineering",
                    "PES University",
                    "BMS College of Engineering",
                    "University of Mysore",
                    "JSS Academy of Higher Education and Research",
                    "Manipal Academy of Higher Education",
                    "NITK Surathkal",
                    "KLE Technological University",
                    "Kuvempu University",
                    "Mangalore University",
                    "Jain University",
                    "Bangalore University",
                    "Mysore Medical College",
                    "St. Joseph's University"
            };

            cities = new String[]{
                    "Bengaluru", "Bengaluru", "Bengaluru",
                    "Bengaluru", "Bengaluru", "Bengaluru",
                    "Bengaluru", "Mysuru", "Mysuru",
                    "Manipal", "Surathkal", "Hubballi",
                    "Shivamogga", "Mangaluru", "Bengaluru",
                    "Bengaluru", "Mysuru", "Bengaluru"
            };

            costs = new int[]{
                    100000, 1500000, 250000, 150000, 180000,
                    200000, 150000, 30000, 150000, 250000,
                    100000, 120000, 50000, 40000, 180000,
                    50000, 50000, 80000
            };

        } else if (institutionState.equals("Tamil Nadu")) {
            names = new String[]{
                    "IIT Madras",
                    "Anna University",
                    "Loyola College",
                    "VIT Vellore",
                    "SRM Institute of Science and Technology",
                    "Madras Medical College"
            };

            cities = new String[]{
                    "Chennai", "Chennai", "Chennai",
                    "Vellore", "Chennai", "Chennai"
            };

            costs = new int[]{
                    50000, 60000, 40000, 200000, 250000, 50000
            };

        } else if (institutionState.equals("Maharashtra")) {
            names = new String[]{
                    "IIT Bombay",
                    "University of Mumbai",
                    "COEP Technological University",
                    "Fergusson College",
                    "Institute of Chemical Technology",
                    "Tata Institute of Social Sciences"
            };

            cities = new String[]{
                    "Mumbai", "Mumbai", "Pune",
                    "Pune", "Mumbai", "Mumbai"
            };

            costs = new int[]{
                    50000, 40000, 100000, 35000, 100000, 80000
            };

        } else if (institutionState.equals("Delhi")) {
            names = new String[]{
                    "University of Delhi",
                    "Jawaharlal Nehru University",
                    "AIIMS Delhi",
                    "St. Stephen's College",
                    "Hindu College",
                    "Jamia Millia Islamia"
            };

            cities = new String[]{
                    "New Delhi", "New Delhi", "New Delhi",
                    "New Delhi", "New Delhi", "New Delhi"
            };

            costs = new int[]{
                    30000, 20000, 10000, 50000, 30000, 25000
            };

        } else if (institutionState.equals("Uttar Pradesh")) {
            names = new String[]{
                    "Banaras Hindu University",
                    "IIT Kanpur",
                    "Aligarh Muslim University",
                    "IIM Lucknow",
                    "University of Lucknow"
            };

            cities = new String[]{
                    "Varanasi", "Kanpur", "Aligarh",
                    "Lucknow", "Lucknow"
            };

            costs = new int[]{
                    30000, 50000, 25000, 800000, 25000
            };

        } else if (institutionState.equals("West Bengal")) {
            names = new String[]{
                    "IIT Kharagpur",
                    "Jadavpur University",
                    "St. Xavier's College Kolkata",
                    "University of Calcutta",
                    "Presidency University"
            };

            cities = new String[]{
                    "Kharagpur", "Kolkata", "Kolkata",
                    "Kolkata", "Kolkata"
            };

            costs = new int[]{
                    50000, 30000, 40000, 25000, 30000
            };

        } else if (institutionState.equals("Rajasthan")) {
            names = new String[]{
                    "BITS Pilani",
                    "IIM Udaipur",
                    "Malaviya National Institute of Technology",
                    "University of Rajasthan"
            };

            cities = new String[]{
                    "Pilani", "Udaipur", "Jaipur", "Jaipur"
            };

            costs = new int[]{
                    500000, 1200000, 100000, 30000
            };

        } else if (institutionState.equals("Kerala")) {
            names = new String[]{
                    "Cochin University of Science and Technology",
                    "University of Kerala",
                    "IISER Thiruvananthapuram",
                    "Government Medical College Kozhikode",
                    "NIT Calicut"
            };

            cities = new String[]{
                    "Kochi", "Thiruvananthapuram",
                    "Thiruvananthapuram", "Kozhikode", "Kozhikode"
            };

            costs = new int[]{
                    40000, 25000, 50000, 40000, 50000
            };

        } else if (institutionState.equals("Telangana")) {
            names = new String[]{
                    "IIT Hyderabad",
                    "University of Hyderabad",
                    "Osmania University",
                    "NALSAR University of Law",
                    "IIIT Hyderabad"
            };

            cities = new String[]{
                    "Hyderabad", "Hyderabad", "Hyderabad",
                    "Hyderabad", "Hyderabad"
            };

            costs = new int[]{
                    50000, 30000, 35000, 200000, 300000
            };

        } else if (institutionState.equals("Gujarat")) {
            names = new String[]{
                    "IIM Ahmedabad",
                    "IIT Gandhinagar",
                    "Gujarat University",
                    "Nirma University"
            };

            cities = new String[]{
                    "Ahmedabad", "Gandhinagar",
                    "Ahmedabad", "Ahmedabad"
            };

            costs = new int[]{
                    1500000, 50000, 30000, 150000
            };

        } else {
            names = new String[]{
                    "Panjab University",
                    "Thapar Institute of Engineering and Technology",
                    "IIT Ropar",
                    "AIIMS Bathinda"
            };

            cities = new String[]{
                    "Chandigarh", "Patiala", "Rupnagar", "Bathinda"
            };

            costs = new int[]{
                    30000, 200000, 50000, 20000
            };
        }

        showInstitutionChoices(level, names, cities, costs);
    }

    // ---------------- INSTITUTION SELECTION ----------------

    private void showInstitutionChoices(
            String level,
            String[] names,
            String[] cities,
            int[] costs
    ) {
        String[] labels = new String[names.length];

        for (int i = 0; i < names.length; i++) {
            labels[i] = names[i]
                    + "\n" + cities[i]
                    + " | Estimated annual fee ₹" + costs[i];
        }

        new AlertDialog.Builder(activity)
                .setTitle("Select Institution")
                .setItems(labels, (dialog, which) -> {
                    institution = names[which];
                    institutionCity = cities[which];
                    chooseAdmission(level, costs[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- ADMISSION VALIDATION ----------------

    private void chooseAdmission(String level, int annualFee) {
        if (enrolled && !graduated) {
            toast("Complete or leave your current course first.");
            return;
        }

        if (level.equals("Primary School")) {
            if (age < 5) {
                toast("You are too young to start school.");
                return;
            }
            if (hasCompletedPrimary()) {
                toast("You have already completed Primary School.");
                return;
            }
            enroll(level, "General Studies", annualFee, 5);
            return;
        }

        if (level.equals("High School")) {
            if (age < 10) {
                toast("You need to be at least 10 years old.");
                return;
            }
            if (!completedLevel("Primary School")) {
                toast("Complete Primary School first.");
                return;
            }
            enroll(level, "General Studies", annualFee, 5);
            return;
        }

        if (level.equals("PUC / Class 11–12")) {
            if (age < 15) {
                toast("You need to be at least 15 years old.");
                return;
            }
            if (!completedLevel("High School")) {
                toast("Complete High School first.");
                return;
            }
            enroll(level, "General Studies", annualFee, 2);
            return;
        }

        if (level.equals("Undergraduate Degree")) {
            if (age < 17) {
                toast("You need to be at least 17 years old.");
                return;
            }
            if (!hasCompletedPUC()) {
                toast("Complete PUC first.");
                return;
            }
            chooseDegreeCourse(annualFee);
            return;
        }

        if (level.equals("Higher Studies")) {
            if (!graduated) {
                toast("Complete your current course first.");
                return;
            }
            chooseHigherAdmission(annualFee);
        }
    }

    // ---------------- UNDERGRADUATE COURSES ----------------

    private void chooseDegreeCourse(int annualFee) {
        String[] courses = {
                "B.E. / B.Tech",
                "MBBS",
                "BDS",
                "B.Sc",
                "B.Com",
                "BBA",
                "BCA",
                "BA",
                "LLB",
                "B.Arch",
                "B.Pharm",
                "B.V.Sc",
                "B.Ed",
                "B.Des"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Degree Course")
                .setItems(courses, (dialog, which) -> {
                    course = courses[which];

                    if (course.equals("B.E. / B.Tech")) {
                        entranceExam = "KCET / COMEDK / JEE";
                    } else if (course.equals("MBBS")
                            || course.equals("BDS")
                            || course.equals("B.V.Sc")) {
                        entranceExam = "NEET";
                    } else if (course.equals("LLB")) {
                        entranceExam = "CLAT / University Entrance";
                    } else {
                        entranceExam = "University Admission";
                    }

                    chooseDegreeAdmission(annualFee);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getDegreeDuration(String selectedCourse) {
        if (selectedCourse.equals("MBBS")) return 5;
        if (selectedCourse.equals("B.E. / B.Tech")
                || selectedCourse.equals("B.Arch")) return 4;
        if (selectedCourse.equals("BDS")) return 5;
        if (selectedCourse.equals("B.Pharm")) return 4;
        if (selectedCourse.equals("B.V.Sc")) return 5;
        if (selectedCourse.equals("B.Ed")) return 2;
        return 3;
    }

    private void chooseDegreeAdmission(int annualFee) {
        String[] options = {
                "Merit Admission",
                "Entrance Exam Admission"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Admission Method")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        entranceCleared = false;
                        entranceScore = 0;

                        enroll(
                                "Undergraduate Degree",
                                course,
                                annualFee,
                                getDegreeDuration(course)
                        );
                    } else {
                        pendingAdmissionLevel = "Undergraduate Degree";
                        currentAnnualFee = annualFee;
                        waitingForEntranceAdmission = true;
                        chooseEntranceExam();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- SECOND UNDERGRADUATE DEGREE ----------------

    private void startAnotherDegree() {
        if (!graduated) {
            toast("Complete your current course before starting another degree.");
            return;
        }

        if (!(educationLevel.contains("Undergraduate Degree")
                || educationLevel.contains("Bachelor"))) {
            toast("Complete an undergraduate degree before applying for another degree.");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Study Another Degree")
                .setMessage(
                        "You have completed " + course + "."
                                + "\n\nWould you like to apply for another undergraduate degree?"
                                + "\n\nYou can choose a different course, institution and admission method."
                )
                .setPositiveButton("Choose Degree", (dialog, which) -> {
                    pendingHigherStudy = "None";
                    chooseInstitution("Undergraduate Degree");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- HIGHER STUDIES ----------------

    private void higherStudies() {
        if (!graduated) {
            toast("Complete your current education before applying for higher studies.");
            return;
        }

        String[] options = {
                "Master's Degree",
                "MBA",
                "M.Tech",
                "LLM",
                "PhD"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Higher Education")
                .setItems(options, (dialog, which) -> {
                    pendingHigherStudy = options[which];

                    if (pendingHigherStudy.equals("MBA")) {
                        entranceExam = "CAT";
                    } else if (pendingHigherStudy.equals("M.Tech")) {
                        entranceExam = "GATE";
                    } else if (pendingHigherStudy.equals("LLM")) {
                        entranceExam = "CLAT PG / University Entrance";
                    } else {
                        entranceExam = "University Entrance";
                    }

                    saveData();
                    chooseInstitution("Higher Studies");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void chooseHigherAdmission(int annualFee) {
        if (pendingHigherStudy.equals("None")) {
            pendingHigherStudy = "Master's Degree";
        }

        String[] options = {
                "Merit Admission",
                "Entrance Exam Admission"
        };

        new AlertDialog.Builder(activity)
                .setTitle(pendingHigherStudy + " Admission")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        entranceScore = 0;
                        entranceCleared = false;
                        enroll(
                                pendingHigherStudy,
                                pendingHigherStudy,
                                annualFee,
                                getHigherStudyDuration(pendingHigherStudy)
                        );
                    } else {
                        pendingAdmissionLevel = pendingHigherStudy;
                        currentAnnualFee = annualFee;
                        waitingForEntranceAdmission = true;
                        chooseEntranceExam();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getHigherStudyDuration(String selected) {
        if (selected.equals("PhD")) return 4;
        if (selected.equals("M.Tech")) return 2;
        if (selected.equals("MBA")) return 2;
        if (selected.equals("LLM")) return 2;
        return 2;
    }

    // ---------------- ENTRANCE EXAMS ----------------

    private void chooseEntranceExam() {
        String[] exams = {
                "KCET",
                "COMEDK",
                "JEE Main",
                "JEE Advanced",
                "NEET",
                "CLAT",
                "CUET",
                "CAT",
                "GATE",
                "University Entrance"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Entrance Examination")
                .setItems(exams, (dialog, which) -> {
                    entranceExam = exams[which];
                    writeEntranceExam();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void writeEntranceExam() {
        if (age < 16) {
            toast("You are too young for this entrance exam.");
            waitingForEntranceAdmission = false;
            saveData();
            return;
        }

        int score = clamp(
                random.nextInt(61) + academicPerformance / 3,
                0,
                100
        );

        entranceScore = score;

        int cutoff = entranceExam.equals("JEE Advanced")
                || entranceExam.equals("NEET")
                || entranceExam.equals("CAT")
                || entranceExam.equals("GATE") ? 70 : 55;

        entranceCleared = score >= cutoff;

        if (entranceCleared) {
            academicPerformance = clamp(
                    academicPerformance + 5, 0, 100);

            showMessage(
                    "Entrance Exam Result",
                    "Exam: " + entranceExam
                            + "\nScore: " + score + "/100"
                            + "\nResult: QUALIFIED"
            );

            if (waitingForEntranceAdmission
                    && !pendingAdmissionLevel.equals("None")) {
                String level = pendingAdmissionLevel;
                int fee = currentAnnualFee;
                waitingForEntranceAdmission = false;
                pendingAdmissionLevel = "None";

                saveData();

                int duration = level.equals("Undergraduate Degree")
                        ? getDegreeDuration(course)
                        : getHigherStudyDuration(level);

                String selectedCourse = level.equals("Undergraduate Degree")
                        ? course : level;

                enroll(level, selectedCourse, fee, duration);
                return;
            }
        } else {
            waitingForEntranceAdmission = false;
            pendingAdmissionLevel = "None";

            showMessage(
                    "Entrance Exam Result",
                    "Exam: " + entranceExam
                            + "\nScore: " + score + "/100"
                            + "\nResult: NOT QUALIFIED"
                            + "\n\nImprove your preparation and try again."
            );
        }

        saveData();
        showDashboard();
    }

    // ---------------- ENROLLMENT ----------------

    private void enroll(
            String level,
            String selectedCourse,
            int annualFee,
            int duration
    ) {
        if (enrolled && !graduated) {
            toast("You are already studying a course.");
            return;
        }

        int admissionFee = Math.max(1000, annualFee / 10);
        int firstPayment = safeAdd(annualFee, admissionFee);

        new AlertDialog.Builder(activity)
                .setTitle("Admission Offer")
                .setMessage(
                        "Institution: " + institution
                                + "\nCity: " + institutionCity
                                + "\nState: " + institutionState
                                + "\nLevel: " + level
                                + "\nCourse: " + selectedCourse
                                + "\nDuration: " + duration + " years"
                                + "\nAnnual Fee: ₹" + annualFee
                                + "\nAdmission Fee: ₹" + admissionFee
                                + "\n\nInitial payment: ₹" + firstPayment
                )
                .setPositiveButton("Pay & Enroll", (dialog, which) -> {
                    if (money < firstPayment) {
                        toast("Insufficient money. Apply for a scholarship or arrange funds.");
                        return;
                    }

                    money -= firstPayment;
                    fees = safeAdd(fees, firstPayment);

                    enrolled = true;
                    graduated = false;

                    educationLevel = level;
                    course = selectedCourse;
                    currentAnnualFee = annualFee;
                    requiredYears = duration;

                    educationYears = 0;
                    attendance = 0;
                    marks = 0;
                    examsPassed = 0;
                    examsFailed = 0;
                    grade = "Not Available";

                    academicPerformance = Math.max(
                            academicPerformance, 50);

                    entranceScore = 0;
                    entranceCleared = false;
                    waitingForEntranceAdmission = false;
                    pendingAdmissionLevel = "None";

                    saveData();

                    notifyUpdate(
                            "Congratulations! You enrolled in "
                                    + institution + " for " + course + "."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- ATTENDANCE ----------------

    private void attendClasses() {
        if (!enrolled || graduated) return;

        attendance = clamp(
                attendance + random.nextInt(8) + 5,
                0,
                100
        );

        academicPerformance = clamp(
                academicPerformance + random.nextInt(4) + 1,
                0,
                100
        );

        happiness = Math.min(100, happiness + 1);

        notifyUpdate(
                "You attended classes at " + institution
                        + ". Attendance is now " + attendance + "%."
        );
    }

    // ---------------- EXAM ----------------

    private void writeExam() {
        if (!enrolled || graduated) return;

        int score = clamp(
                random.nextInt(41)
                        + academicPerformance / 2
                        + attendance / 10,
                0,
                100
        );

        marks = score;

        if (score >= 90) {
            grade = "A+";
        } else if (score >= 80) {
            grade = "A";
        } else if (score >= 70) {
            grade = "B";
        } else if (score >= 60) {
            grade = "C";
        } else if (score >= 40) {
            grade = "D";
        } else {
            grade = "F";
        }

        if (score >= 40) {
            examsPassed++;
            academicPerformance = clamp(
                    academicPerformance + 5, 0, 100);

            notifyUpdate(
                    "You passed your examination with "
                            + score + "/100. Grade: " + grade
            );
        } else {
            examsFailed++;
            academicPerformance = clamp(
                    academicPerformance - 5, 0, 100);
            happiness = Math.max(0, happiness - 3);

            notifyUpdate(
                    "You failed the examination with "
                            + score + "/100. You can try again."
            );
        }
    }

    // ---------------- ANNUAL UPDATE ----------------

    public void applyAnnualEducationUpdate() {
        if (!enrolled || graduated) return;

        educationYears++;

        attendance = Math.max(0, attendance - 20);

        if (attendance < 50) {
            academicPerformance = Math.max(
                    0, academicPerformance - 8);
        } else if (attendance >= 75) {
            academicPerformance = clamp(
                    academicPerformance + 3, 0, 100);
        }

        // Annual fee is charged automatically when affordable.
        if (currentAnnualFee > 0) {
            if (money >= currentAnnualFee) {
                money -= currentAnnualFee;
                fees = safeAdd(fees, currentAnnualFee);
            } else {
                academicPerformance = Math.max(
                        0, academicPerformance - 5);
            }
        }

        saveData();
        checkGraduation();
    }

    private void checkGraduation() {
        int duration = requiredYears > 0
                ? requiredYears
                : getDefaultDuration();

        if (educationYears < duration) return;

        // Require at least one passed exam per academic year.
        if (examsPassed < duration || marks < 40) {
            showMessage(
                    "Graduation Delayed",
                    "You must pass your examinations before graduating."
                            + "\n\nExams passed: " + examsPassed
                            + "\nRequired: " + duration
                            + "\nLast marks: " + marks + "/100"
            );

            educationYears = duration - 1;
            saveData();
            return;
        }

        graduated = true;
        enrolled = false;

        educationLevel = educationLevel + " - Completed";

        if (academicPerformance >= 85) {
            grade = "Distinction";
        } else if (academicPerformance >= 60) {
            grade = "First Class";
        } else {
            grade = "Pass";
        }

        saveData();

        notifyUpdate(
                "Congratulations! You completed your education at "
                        + institution + ". Grade: " + grade
        );
    }

    private int getDefaultDuration() {
        if (educationLevel.startsWith("Primary School")) return 5;
        if (educationLevel.startsWith("High School")) return 5;
        if (educationLevel.startsWith("PUC / Class 11–12")) return 2;
        if (course.equals("MBBS")) return 5;
        if (course.equals("B.E. / B.Tech")
                || course.equals("B.Arch")) return 4;
        return 3;
    }

    // ---------------- FEES ----------------

    private void payFees() {
        if (!enrolled || graduated) return;

        EditText input = new EditText(activity);
        input.setHint("Enter fee amount");
        input.setInputType(2);

        new AlertDialog.Builder(activity)
                .setTitle("Education Fee Payment")
                .setMessage(
                        "Enter the amount you want to pay."
                                + "\nAvailable money: ₹" + money
                )
                .setView(input)
                .setPositiveButton("Pay", (dialog, which) -> {
                    int payment = readAmount(input);

                    if (payment <= 0 || payment > money) {
                        toast("Invalid payment amount.");
                        return;
                    }

                    money -= payment;
                    fees = safeAdd(fees, payment);

                    notifyUpdate(
                            "Education fee payment of ₹"
                                    + payment + " completed."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- SCHOLARSHIP ----------------

    private void applyScholarship() {
        if (!enrolled || graduated) return;

        int chance = clamp(
                academicPerformance + attendance / 5,
                10,
                95
        );

        final int finalChance = chance;

        new AlertDialog.Builder(activity)
                .setTitle("Scholarship Application")
                .setMessage(
                        "Your scholarship selection chance is "
                                + finalChance + "%."
                                + "\n\nApply now?"
                )
                .setPositiveButton("Apply", (dialog, which) -> {
                    if (random.nextInt(100) < finalChance) {
                        int award = 10000 + random.nextInt(40001);

                        scholarships = safeAdd(scholarships, award);
                        money = safeAdd(money, award);

                        notifyUpdate(
                                "Congratulations! You received a scholarship of ₹"
                                        + award + "."
                        );
                    } else {
                        notifyUpdate(
                                "Your scholarship application was unsuccessful."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- HOSTEL ----------------

    private void hostelMenu() {
        String[] options = {
                "Join Hostel",
                "Leave Hostel",
                "View Hostel Details"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Accommodation")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (hostel) {
                            toast("You are already staying in a hostel.");
                            return;
                        }

                        int cost = 20000;

                        if (money < cost) {
                            toast("You need ₹20,000 to join the hostel.");
                            return;
                        }

                        money -= cost;
                        hostel = true;
                        hostelExpenses = safeAdd(hostelExpenses, cost);

                        notifyUpdate(
                                "You joined the college hostel. Initial fee: ₹20,000."
                        );

                    } else if (which == 1) {
                        if (!hostel) {
                            toast("You are not staying in a hostel.");
                            return;
                        }

                        hostel = false;

                        notifyUpdate(
                                "You left your college hostel."
                        );

                    } else {
                        showMessage(
                                "Hostel Details",
                                "Hostel Status: "
                                        + (hostel ? "Living in Hostel" : "Not Staying in Hostel")
                                        + "\nTotal Hostel Expenses: ₹"
                                        + hostelExpenses
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- DROPOUT ----------------

    private void dropOut() {
        new AlertDialog.Builder(activity)
                .setTitle("Drop Out")
                .setMessage(
                        "Are you sure you want to leave your current institution?"
                )
                .setPositiveButton("Drop Out", (dialog, which) -> {
                    enrolled = false;
                    graduated = false;

                    institution = "None";
                    institutionCity = "None";
                    institutionState = "None";

                    educationLevel = "Dropped Out";
                    course = "None";
                    stream = "None";
                    hostel = false;

                    currentAnnualFee = 0;
                    requiredYears = 0;
                    educationYears = 0;

                    saveData();

                    notifyUpdate(
                            "You left your educational institution."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- ACADEMIC REPORT ----------------

    private void academicReport() {
        String report =
                "DESILIFE ACADEMIC REPORT"
                        + "\n\nEducation: " + educationLevel
                        + "\nInstitution: " + institution
                        + "\nCity: " + institutionCity
                        + "\nState: " + institutionState
                        + "\nStream: " + stream
                        + "\nCourse: " + course
                        + "\n\nAcademic Performance: "
                        + academicPerformance + "/100"
                        + "\nLast Exam Marks: " + marks
                        + "\nAttendance: " + attendance + "%"
                        + "\nGrade: " + grade
                        + "\n\nExams Passed: " + examsPassed
                        + "\nExams Failed: " + examsFailed
                        + "\nYears Studied: " + educationYears
                        + "\n\nScholarships: ₹" + scholarships
                        + "\nEducation Fees: ₹" + fees
                        + "\nHostel Expenses: ₹" + hostelExpenses
                        + "\nEducation Loans: ₹" + educationLoans;

        showMessage("Academic Report", report);
    }

    // ---------------- HELPERS ----------------

    private int readAmount(EditText input) {
        try {
            return Integer.parseInt(
                    input.getText().toString().trim()
            );
        } catch (Exception e) {
            return 0;
        }
    }

    // ---------------- CALLBACK ----------------

    private void notifyUpdate(String description) {
        saveData();

        if (callback != null) {
            callback.onUpdate(
                    money,
                    health,
                    happiness,
                    description
            );
        }

        showDashboard();
    }
}
