package com.desilife.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CareerDashboardManager {

    public interface CareerActions {
        void generalCareer();
        void governmentExams();
        void governmentService();
        void privateCareer();
        void employment();
        void politics();
        void back();
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final int age;
    private final int money;
    private final CareerActions actions;

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int card = Color.rgb(27, 43, 39);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 195, 187);

    public CareerDashboardManager(Activity activity,
                                  SharedPreferences save,
                                  int age, int money,
                                  CareerActions actions) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.actions = actions;
    }

    public void open() {
        showDashboard();
    }

    private void showDashboard() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 28);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);

        TextView title = new TextView(activity);
        title.setText("💼 CAREER & PROFESSION");
        title.setTextColor(gold);
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 8);
        root.addView(title);

        TextView subtitle = new TextView(activity);
        subtitle.setText("Your occupation, qualifications and professional journey");
        subtitle.setTextColor(muted);
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, 18);
        root.addView(subtitle);

        addInfo(root, "CHARACTER", "Age " + age + "  •  Cash ₹" + money);

        String activeJob = "Unemployed";
        String organization = "No active organization";
        String grade = "Not assigned";
        String salary = "₹0 / month";
        String status = "No active occupation";
        int careerExperience = 0;
        int careerPerformance = 0;
        int careerPromotions = 0;

        // Read each career system from its actual, namespaced save keys.
        String publicJob = save.getString("public_service_job", "");
        if (!publicJob.isEmpty()
                && "Active".equals(save.getString("public_service_status", "Active"))) {
            activeJob = publicJob;
            organization = save.getString("public_service_department", "Government Department");
            grade = "Grade " + (save.getInt("public_service_level", 0) + 1);
            salary = "₹" + save.getInt("public_service_salary", 0) + " / month";
            status = "Active Government Service";
            careerExperience = save.getInt("public_service_service_years", 0);
            careerPerformance = save.getInt("public_service_performance", 50);
            careerPromotions = save.getInt("public_service_level", 0);
        }

        String privateJob = save.getString("private_career_job", "");
        if (!privateJob.isEmpty()
                && "Active".equals(save.getString("private_career_status", "Active"))) {
            activeJob = privateJob;
            organization = save.getString("private_career_company", "Private Company");
            grade = "Grade " + (save.getInt("private_career_level", 0) + 1);
            salary = "₹" + save.getInt("private_career_salary", 0) + " / month";
            status = "Active Private-Sector Job";
            careerExperience = save.getInt("private_career_experience", 0);
            careerPerformance = save.getInt("private_career_performance", 50);
            careerPromotions = save.getInt("private_career_level", 0);
        }

        if (save.getBoolean("employment_employed", false)) {
            activeJob = save.getString("employment_job_name", "Employee");
            organization = save.getString("employment_job_sector", "General Employment");
            grade = save.getString("employment_job_level", "Entry Level");
            salary = "₹" + save.getInt("employment_salary", 0) + " / month";
            status = "Active General Employment";
            careerExperience = save.getInt("employment_experience", 0);
            careerPerformance = save.getInt("employment_performance", 50);
            careerPromotions = save.getInt("employment_promotions", 0);
        }

        SharedPreferences generalCareer = activity.getSharedPreferences(
                "desilife_career_data", Activity.MODE_PRIVATE);
        String generalTitle = generalCareer.getString("jobTitle", "");
        if (!generalTitle.isEmpty()
                && generalCareer.getBoolean("employed", false)
                && "Unemployed".equals(activeJob)) {
            activeJob = generalTitle;
            organization = generalCareer.getString("company", "General Career");
            grade = "Grade " + (generalCareer.getInt("promotions", 0) + 1);
            salary = "₹" + generalCareer.getLong("monthlySalary", 0) + " / month";
            status = "Active General Career";
            careerExperience = generalCareer.getInt("experience", 0);
            careerPerformance = generalCareer.getInt("performance", 50);
            careerPromotions = generalCareer.getInt("promotions", 0);
        }

        final String currentJob = activeJob;
        final String currentOrganization = organization;
        final String currentGrade = grade;
        final String currentSalary = salary;
        final String currentStatus = status;
        final int currentExperience = careerExperience;
        final int currentPerformance = careerPerformance;
        final int currentPromotions = careerPromotions;

        addSection(root, "CAREER DEVELOPMENT");
        addButton(root, "📋 Current Job & Grade Dashboard", () ->
                showCareerRecord(currentJob, currentOrganization,
                        currentGrade, currentSalary, currentStatus,
                        currentExperience, currentPerformance, currentPromotions));
        addButton(root, "🎓 Education & Qualifications", () ->
                showEducationSummary());

        addSection(root, "CHOOSE A CAREER PATH");
        addButton(root, "🧭 General Careers", actions::generalCareer);
        addButton(root, "🏛️ Government Competitive Exams", actions::governmentExams);
        addButton(root, "🇮🇳 Government Departments & Service", actions::governmentService);
        addButton(root, "🏢 Private Company Careers", actions::privateCareer);
        addButton(root, "👔 General Employment", actions::employment);

        addSection(root, "POLITICAL CAREER");
        String politicalOffice = save.getString("political_office", "Ordinary Citizen");
        String politicalParty = save.getString("political_party", "");
        addInfo(root, "Political Position", politicalOffice);
        addInfo(root, "Political Party",
                politicalParty.isEmpty() ? "Not affiliated" : politicalParty);
        addInfo(root, "Public Approval",
                save.getInt("political_popularity", 0) + "%");
        addInfo(root, "Election Wins",
                String.valueOf(save.getInt("political_election_wins", 0)));
        addButton(root, "🏛️ Political Career & Elections", actions::politics);

        addButton(root, "⬅ BACK TO LIFE", actions::back);
        activity.setContentView(scroll);
    }

    private void showCareerRecord(String job, String organization,
                                  String grade, String salary, String status) {
        String details = "Current Position: " + job
                + "\nOrganization: " + organization
                + "\nGrade / Level: " + grade
                + "\nMonthly Salary: " + salary
                + "\nStatus: " + status
                + "\n\nCareer experience: " + save.getInt("employment_experience", 0)
                + " years"
                + "\nPerformance: " + save.getInt("employment_performance", 50) + "%"
                + "\nReputation: " + save.getInt("employment_reputation", 50) + "%"
                + "\nPromotions: " + save.getInt("employment_promotions", 0)
                + "\n\nUse the relevant career path below to manage work, request promotions, train skills or apply for another position.";
        new android.app.AlertDialog.Builder(activity)
                .setTitle("Current Occupation Record")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showEducationSummary() {
        String level = save.getString("edu_level",
                save.getString("education_level", "Not recorded"));
        String course = save.getString("edu_course", "Not specified");
        boolean graduated = save.getBoolean("edu_graduated",
                save.getBoolean("degreeCompleted", false));
        String details = "Highest Education: " + level
                + "\nCourse / Degree: " + course
                + "\nGraduation Status: " + (graduated ? "Completed" : "Not completed")
                + "\n\nJob applications will check your completed qualification and the specific degree required for each profession.";
        new android.app.AlertDialog.Builder(activity)
                .setTitle("Education & Qualifications")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void addSection(LinearLayout root, String label) {
        TextView t = new TextView(activity);
        t.setText(label);
        t.setTextColor(gold);
        t.setTextSize(14);
        t.setTypeface(null, Typeface.BOLD);
        t.setPadding(4, 18, 4, 8);
        root.addView(t);
    }

    private void addInfo(LinearLayout root, String label, String value) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(15, 11, 15, 11);
        box.setBackground(background(card, 14));

        TextView heading = new TextView(activity);
        heading.setText(label);
        heading.setTextColor(muted);
        heading.setTextSize(11);

        TextView data = new TextView(activity);
        data.setText(value);
        data.setTextColor(white);
        data.setTextSize(15);
        data.setTypeface(null, Typeface.BOLD);
        data.setPadding(0, 3, 0, 0);

        box.addView(heading);
        box.addView(data);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 4, 0, 4);
        root.addView(box, p);
    }

    private void addButton(LinearLayout root, String label, Runnable action) {
        TextView button = new TextView(activity);
        button.setText(label);
        button.setTextColor(dark);
        button.setTextSize(15);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(17, 16, 17, 16);
        button.setBackground(background(gold, 15));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 5, 0, 5);
        root.addView(button, p);
        button.setOnClickListener(v -> action.run());
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }
}
