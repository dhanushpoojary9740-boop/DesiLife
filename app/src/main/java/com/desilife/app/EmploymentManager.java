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

public class EmploymentManager {

    public interface EmploymentCallback {
        void onUpdate(
                int money,
                int health,
                int happiness,
                String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final EmploymentCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;

    private boolean employed;
    private String jobName;
    private String jobType;
    private String jobSector;
    private String jobLevel;

    private int monthlySalary;
    private int performance;
    private int reputation;
    private int experience;
    private int promotions;
    private int jobApplications;
    private int interviews;
    private int bonuses;
    private int yearsInJob;
    private int salaryReceived;
    private int unemploymentYears;

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 190, 185);

    public EmploymentManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            EmploymentCallback callback
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

    // ---------------- LOAD DATA ----------------

    private void loadData() {

        employed = save.getBoolean("employment_employed", false);

        jobName = save.getString(
                "employment_job_name", "Unemployed");

        jobType = save.getString(
                "employment_job_type", "None");

        jobSector = save.getString(
                "employment_job_sector", "None");

        jobLevel = save.getString(
                "employment_job_level", "None");

        monthlySalary = save.getInt(
                "employment_salary", 0);

        performance = save.getInt(
                "employment_performance", 50);

        reputation = save.getInt(
                "employment_reputation", 50);

        experience = save.getInt(
                "employment_experience", 0);

        promotions = save.getInt(
                "employment_promotions", 0);

        jobApplications = save.getInt(
                "employment_applications", 0);

        interviews = save.getInt(
                "employment_interviews", 0);

        bonuses = save.getInt(
                "employment_bonuses", 0);

        yearsInJob = save.getInt(
                "employment_years_in_job", 0);

        salaryReceived = save.getInt(
                "employment_salary_received", 0);

        unemploymentYears = save.getInt(
                "employment_unemployment_years", 0);

        normalize();
    }

    // ---------------- SAVE DATA ----------------

    private void saveData() {

        SharedPreferences.Editor editor = save.edit();

        editor.putBoolean("employment_employed", employed);

        editor.putString("employment_job_name", jobName);
        editor.putString("employment_job_type", jobType);
        editor.putString("employment_job_sector", jobSector);
        editor.putString("employment_job_level", jobLevel);

        editor.putInt("employment_salary", monthlySalary);
        editor.putInt("employment_performance", performance);
        editor.putInt("employment_reputation", reputation);
        editor.putInt("employment_experience", experience);
        editor.putInt("employment_promotions", promotions);
        editor.putInt("employment_applications", jobApplications);
        editor.putInt("employment_interviews", interviews);
        editor.putInt("employment_bonuses", bonuses);
        editor.putInt("employment_years_in_job", yearsInJob);
        editor.putInt("employment_salary_received", salaryReceived);
        editor.putInt("employment_unemployment_years", unemploymentYears);

        editor.apply();
    }

    private void normalize() {

        performance = clamp(performance, 0, 100);
        reputation = clamp(reputation, 0, 100);

        monthlySalary = Math.max(0, monthlySalary);
        experience = Math.max(0, experience);
        promotions = Math.max(0, promotions);
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
        Toast.makeText(
                activity,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    // ---------------- PUBLIC METHODS ----------------

    public void open() {
        showDashboard();
    }

    public boolean isEmployed() {
        return employed;
    }

    public int getMonthlySalary() {
        return monthlySalary;
    }

    public String getJobName() {
        return jobName;
    }

    public int getPerformance() {
        return performance;
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
        title.setText("💼 EMPLOYMENT & CAREER");
        title.setTextColor(gold);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 20);

        root.addView(title);

        addInfo(root, "Employment Status",
                employed ? "Employed" : "Unemployed");

        addInfo(root, "Current Job", jobName);

        addInfo(root, "Employment Sector", jobSector);

        addInfo(root, "Job Level", jobLevel);

        addInfo(root, "Monthly Salary",
                "₹" + monthlySalary);

        addInfo(root, "Work Performance",
                performance + "/100");

        addInfo(root, "Professional Reputation",
                reputation + "/100");

        addInfo(root, "Work Experience",
                experience + " years");

        addInfo(root, "Promotions",
                String.valueOf(promotions));

        addInfo(root, "Salary Earned",
                "₹" + salaryReceived);

        addDivider(root);

        if (employed) {

            addButton(root, "📋 Work & Performance",
                    this::workMenu);

            addButton(root, "🏆 Request Promotion",
                    this::requestPromotion);

            addButton(root, "🎁 Request Performance Bonus",
                    this::requestBonus);

            addButton(root, "🔄 Apply for Another Job",
                    this::openJobCategories);

            addButton(root, "🚪 Resign from Current Job",
                    this::resign);

            addButton(root, "📊 Employment Report",
                    this::showReport);

        } else {

            addInfo(root, "Unemployment Duration",
                    unemploymentYears + " years");

            addButton(root, "🔍 Find a Job",
                    this::openJobCategories);

            addButton(root, "📚 Improve Skills",
                    this::improveSkills);

            addButton(root, "📊 Employment Report",
                    this::showReport);
        }

        addButton(root, "⬅ Back to Game",
                activity::recreate);

        activity.setContentView(scroll);
    }

    // ---------------- UI HELPERS ----------------

    private void addInfo(
            LinearLayout root,
            String label,
            String value
    ) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 12, 16, 12);
        card.setBackground(background(
                Color.rgb(23, 39, 35), 14));

        TextView heading = new TextView(activity);
        heading.setText(label);
        heading.setTextColor(muted);
        heading.setTextSize(12);

        TextView data = new TextView(activity);
        data.setText(value);
        data.setTextColor(white);
        data.setTextSize(17);
        data.setTypeface(null, Typeface.BOLD);
        data.setPadding(0, 4, 0, 0);

        card.addView(heading);
        card.addView(data);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 4, 0, 4);

        root.addView(card, params);
    }

    private void addButton(
            LinearLayout root,
            String label,
            Runnable action
    ) {

        TextView button = new TextView(activity);

        button.setText(label);
        button.setTextColor(dark);
        button.setTextSize(16);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(14, 17, 14, 17);
        button.setBackground(background(gold, 14));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 7, 0, 7);

        root.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void addDivider(LinearLayout root) {

        TextView divider = new TextView(activity);
        divider.setBackgroundColor(
                Color.rgb(65, 75, 68));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, 1);

        params.setMargins(0, 18, 0, 12);

        root.addView(divider, params);
    }

    private GradientDrawable background(
            int color,
            int radius
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

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

    // ---------------- JOB CATEGORIES ----------------

    private void openJobCategories() {

        if (age < 18) {
            toast("You must be at least 18 years old to apply for these jobs.");
            return;
        }

        String[] categories = {
                "Government Jobs",
                "Private Sector Jobs",
                "Banking Jobs",
                "Technology Jobs",
                "Healthcare Jobs",
                "Business & Management Jobs",
                "Education Jobs"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Employment Sector")
                .setItems(categories, (dialog, which) -> {

                    switch (which) {
                        case 0:
                            showJobs("Government");
                            break;
                        case 1:
                            showJobs("Private");
                            break;
                        case 2:
                            showJobs("Banking");
                            break;
                        case 3:
                            showJobs("Technology");
                            break;
                        case 4:
                            showJobs("Healthcare");
                            break;
                        case 5:
                            showJobs("Management");
                            break;
                        case 6:
                            showJobs("Education");
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- JOB LIST ----------------

    private void showJobs(String sector) {

        String[] jobs;
        int[] salaries;

        switch (sector) {

            case "Government":
                jobs = new String[]{
                        "Government Clerk",
                        "Police Constable",
                        "Revenue Inspector",
                        "Government Teacher",
                        "Government Officer",
                        "IAS Officer"
                };

                salaries = new int[]{
                        22000, 28000, 38000,
                        42000, 65000, 100000
                };
                break;

            case "Private":
                jobs = new String[]{
                        "Office Assistant",
                        "Sales Executive",
                        "Accountant",
                        "HR Executive",
                        "Operations Manager",
                        "Company Director"
                };

                salaries = new int[]{
                        15000, 22000, 30000,
                        35000, 65000, 120000
                };
                break;

            case "Banking":
                jobs = new String[]{
                        "Bank Clerk",
                        "Bank Assistant",
                        "Bank Officer",
                        "Bank Manager",
                        "Regional Manager"
                };

                salaries = new int[]{
                        25000, 30000, 45000,
                        75000, 120000
                };
                break;

            case "Technology":
                jobs = new String[]{
                        "Junior Developer",
                        "Software Engineer",
                        "Senior Developer",
                        "Technical Lead",
                        "IT Manager",
                        "Technology Director"
                };

                salaries = new int[]{
                        25000, 45000, 75000,
                        100000, 140000, 200000
                };
                break;

            case "Healthcare":
                jobs = new String[]{
                        "Hospital Assistant",
                        "Nurse",
                        "Pharmacist",
                        "Doctor",
                        "Senior Doctor",
                        "Hospital Director"
                };

                salaries = new int[]{
                        15000, 30000, 35000,
                        70000, 110000, 180000
                };
                break;

            case "Management":
                jobs = new String[]{
                        "Management Trainee",
                        "Team Leader",
                        "Project Manager",
                        "General Manager",
                        "Business Director"
                };

                salaries = new int[]{
                        25000, 40000, 70000,
                        110000, 180000
                };
                break;

            default:
                jobs = new String[]{
                        "Teaching Assistant",
                        "School Teacher",
                        "Senior Teacher",
                        "Lecturer",
                        "Professor",
                        "Principal"
                };

                salaries = new int[]{
                        15000, 28000, 40000,
                        50000, 90000, 100000
                };
                break;
        }

        String[] labels = new String[jobs.length];

        for (int i = 0; i < jobs.length; i++) {
            labels[i] = jobs[i] + "  |  ₹"
                    + salaries[i] + "/month";
        }

        new AlertDialog.Builder(activity)
                .setTitle(sector + " Opportunities")
                .setItems(labels, (dialog, which) -> {

                    applyForJob(
                            jobs[which],
                            salaries[which],
                            sector
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- APPLY FOR JOB ----------------

    private void applyForJob(
            String selectedJob,
            int salary,
            String sector
    ) {

        jobApplications++;

        int chance = 40;

        chance += performance / 5;
        chance += reputation / 10;
        chance += Math.min(20, experience * 2);

        if (sector.equals("Government")) {
            chance -= 15;
        }

        if (selectedJob.contains("IAS")
                || selectedJob.contains("Director")
                || selectedJob.contains("Professor")) {
            chance -= 15;
        }

        if (selectedJob.contains("Doctor")
                && experience < 2) {
            chance -= 10;
        }

        chance = clamp(chance, 10, 90);

        int finalChance = chance;

        new AlertDialog.Builder(activity)
                .setTitle("Job Application")
                .setMessage(
                        "Position: " + selectedJob
                                + "\nSector: " + sector
                                + "\nMonthly Salary: ₹" + salary
                                + "\nEstimated Selection Chance: "
                                + finalChance + "%"
                                + "\n\nSubmit your application?"
                )
                .setPositiveButton("Apply", (dialog, which) -> {

                    interviews++;

                    boolean selected =
                            random.nextInt(100) < finalChance;

                    if (selected) {

                        showInterviewResult(
                                selectedJob,
                                salary,
                                sector
                        );

                    } else {

                        reputation = Math.max(0, reputation - 1);

                        saveData();

                        notifyUpdate(
                                "Your application for "
                                        + selectedJob
                                        + " was unsuccessful. "
                                        + "You can improve your skills and apply again."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- INTERVIEW ----------------

    private void showInterviewResult(
            String selectedJob,
            int salary,
            String sector
    ) {

        String[] options = {
                "Confident and professional",
                "Nervous but honest",
                "Unprepared",
                "Highly experienced and confident"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Job Interview")
                .setMessage("You have been shortlisted for "
                        + selectedJob + ". Choose your interview approach.")
                .setItems(options, (dialog, which) -> {

                    int bonus = 0;

                    if (which == 0) bonus = 15;
                    if (which == 1) bonus = 5;
                    if (which == 2) bonus = -20;
                    if (which == 3) bonus = 25;

                    int interviewChance = clamp(
                            50 + bonus + performance / 5,
                            10,
                            95
                    );

                    if (random.nextInt(100) < interviewChance) {

                        acceptJob(
                                selectedJob,
                                salary,
                                sector
                        );

                    } else {

                        saveData();

                        notifyUpdate(
                                "You did not pass the interview for "
                                        + selectedJob
                                        + ". Keep improving your skills."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- ACCEPT JOB ----------------

    private void acceptJob(
            String selectedJob,
            int salary,
            String sector
    ) {

        employed = true;

        jobName = selectedJob;
        jobSector = sector;
        monthlySalary = salary;

        jobType = sector.equals("Government")
                ? "Permanent Government"
                : "Private Employment";

        jobLevel = "Entry Level";

        performance = Math.max(performance, 50);

        yearsInJob = 0;
        unemploymentYears = 0;

        saveData();

        notifyUpdate(
                "Congratulations! You got the job as "
                        + selectedJob
                        + ". Your monthly salary is ₹"
                        + salary + "."
        );
    }

    // ---------------- WORK MENU ----------------

    private void workMenu() {

        String[] options = {
                "Work Hard",
                "Work Normally",
                "Take It Easy",
                "Improve Professional Skills",
                "Build Relationships with Colleagues"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Workplace Activities")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        performance = clamp(
                                performance + 10, 0, 100);

                        reputation = clamp(
                                reputation + 5, 0, 100);

                        health = Math.max(0, health - 2);

                        happiness = Math.max(0, happiness - 1);

                        notifyUpdate(
                                "You worked very hard. Your performance improved."
                        );

                    } else if (which == 1) {

                        performance = clamp(
                                performance + 4, 0, 100);

                        reputation = clamp(
                                reputation + 2, 0, 100);

                        notifyUpdate(
                                "You completed your normal work successfully."
                        );

                    } else if (which == 2) {

                        performance = clamp(
                                performance - 5, 0, 100);

                        reputation = clamp(
                                reputation - 3, 0, 100);

                        happiness = Math.min(
                                100, happiness + 3);

                        notifyUpdate(
                                "You took it easy at work. Your performance decreased slightly."
                        );

                    } else if (which == 3) {

                        improveSkills();

                    } else {

                        reputation = clamp(
                                reputation + 8, 0, 100);

                        happiness = Math.min(
                                100, happiness + 2);

                        notifyUpdate(
                                "You built good relationships with your colleagues."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- IMPROVE SKILLS ----------------

    private void improveSkills() {

        if (money < 5000) {
            toast("You need ₹5,000 for professional training.");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Professional Training")
                .setMessage(
                        "Join a professional development course for ₹5,000?"
                )
                .setPositiveButton("Enroll", (dialog, which) -> {

                    if (money < 5000) {
                        toast("Insufficient money");
                        return;
                    }

                    money -= 5000;

                    performance = clamp(
                            performance + 8, 0, 100);

                    reputation = clamp(
                            reputation + 3, 0, 100);

                    notifyUpdate(
                            "You completed professional training. Your skills improved."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- AUTOMATIC MONTHLY SALARY ----------------

    /**
     * Pays salary once for the supplied absolute in-game month.
     * The month number is calculated by MainActivity as:
     * (age in years * 12) + completed age months + 1.
     * A saved guard prevents duplicate salary if this method is
     * called again during the same in-game month or after reload.
     */
    public int processMonthlySalary(int gameMonth) {

        if (!employed || monthlySalary <= 0 || gameMonth <= 0) {
            return 0;
        }

        int lastPaidMonth = save.getInt(
                "employment_last_salary_month", -1);

        if (gameMonth <= lastPaidMonth) {
            return 0;
        }

        money = safeAdd(money, monthlySalary);
        salaryReceived = safeAdd(salaryReceived, monthlySalary);

        save.edit()
                .putInt("employment_last_salary_month", gameMonth)
                .apply();

        saveData();

        return monthlySalary;
    }

    // ---------------- PROMOTION ----------------

    private void requestPromotion() {

        if (!employed) return;

        if (yearsInJob < 1) {
            toast("You need at least one year in your current job.");
            return;
        }

        int chance = 20;

        chance += performance / 3;
        chance += reputation / 5;
        chance += Math.min(15, experience);

        chance = clamp(chance, 5, 90);

        new AlertDialog.Builder(activity)
                .setTitle("Promotion Request")
                .setMessage(
                        "Your promotion chance is approximately "
                                + chance + "%."
                                + "\n\nRequest a promotion?"
                )
                .setPositiveButton("Request", (dialog, which) -> {

                    if (random.nextInt(100) < chance) {

                        promotions++;

                        jobLevel = nextLevel(jobLevel);

                        monthlySalary = safeAdd(
                                monthlySalary,
                                Math.max(2000, monthlySalary / 5)
                        );

                        performance = clamp(
                                performance + 5, 0, 100);

                        reputation = clamp(
                                reputation + 5, 0, 100);

                        notifyUpdate(
                                "Congratulations! You were promoted to "
                                        + jobLevel
                                        + ". Your new monthly salary is ₹"
                                        + monthlySalary + "."
                        );

                    } else {

                        notifyUpdate(
                                "Your promotion request was rejected. "
                                        + "Improve your performance and try again."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String nextLevel(String current) {

        switch (current) {
            case "Entry Level":
                return "Junior Level";

            case "Junior Level":
                return "Mid Level";

            case "Mid Level":
                return "Senior Level";

            case "Senior Level":
                return "Manager";

            case "Manager":
                return "Senior Manager";

            case "Senior Manager":
                return "Director";

            default:
                return "Executive";
        }
    }

    // ---------------- BONUS ----------------

    private void requestBonus() {

        if (!employed) return;

        int chance = clamp(
                20 + performance / 2 + reputation / 4,
                5,
                95
        );

        new AlertDialog.Builder(activity)
                .setTitle("Performance Bonus")
                .setMessage(
                        "Your bonus approval chance is "
                                + chance + "%."
                                + "\n\nRequest a bonus?"
                )
                .setPositiveButton("Request", (dialog, which) -> {

                    if (random.nextInt(100) < chance) {

                        int bonus = Math.max(
                                1000, monthlySalary / 2);

                        money = safeAdd(money, bonus);

                        bonuses++;

                        notifyUpdate(
                                "Congratulations! You received a bonus of ₹"
                                        + bonus + "."
                        );

                    } else {

                        notifyUpdate(
                                "Your bonus request was not approved this time."
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- RESIGN ----------------

    private void resign() {

        new AlertDialog.Builder(activity)
                .setTitle("Resign from Job")
                .setMessage(
                        "Are you sure you want to resign from "
                                + jobName + "?"
                )
                .setPositiveButton("Resign", (dialog, which) -> {

                    String oldJob = jobName;

                    employed = false;

                    jobName = "Unemployed";
                    jobType = "None";
                    jobSector = "None";
                    jobLevel = "None";

                    monthlySalary = 0;

                    performance = Math.max(
                            30, performance - 5);

                    unemploymentYears = 0;

                    saveData();

                    notifyUpdate(
                            "You resigned from " + oldJob
                                    + ". You are now unemployed."
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- TERMINATION ----------------

    public void terminateEmployment() {

        if (!employed) return;

        String oldJob = jobName;

        employed = false;

        jobName = "Unemployed";
        jobType = "None";
        jobSector = "None";
        jobLevel = "None";

        monthlySalary = 0;

        performance = Math.max(0, performance - 15);
        reputation = Math.max(0, reputation - 10);

        unemploymentYears = 0;

        saveData();

        notifyUpdate(
                "You were terminated from " + oldJob + "."
        );
    }

    // ---------------- ANNUAL UPDATE ----------------

    public void applyAnnualEmploymentUpdate() {

        if (employed) {

            yearsInJob++;
            experience++;

            if (performance >= 80) {

                monthlySalary = safeAdd(
                        monthlySalary,
                        Math.max(1000, monthlySalary / 10)
                );

                reputation = clamp(
                        reputation + 3, 0, 100
                );

            } else if (performance >= 60) {

                monthlySalary = safeAdd(
                        monthlySalary,
                        Math.max(500, monthlySalary / 20)
                );

            } else if (performance < 30) {

                reputation = Math.max(
                        0, reputation - 5
                );

                if (random.nextInt(100) < 15) {
                    terminateEmployment();
                    return;
                }
            }

            if (performance >= 95
                    && yearsInJob >= 2
                    && random.nextInt(100) < 20) {

                promotions++;

                jobLevel = nextLevel(jobLevel);

                monthlySalary = safeAdd(
                        monthlySalary,
                        Math.max(2000, monthlySalary / 5)
                );
            }

            saveData();

        } else {

            unemploymentYears++;

            if (unemploymentYears >= 2) {

                performance = Math.max(
                        20, performance - 3
                );
            }

            saveData();
        }
    }

    // ---------------- EMPLOYMENT REPORT ----------------

    private void showReport() {

        String report =
                "DESILIFE EMPLOYMENT REPORT"
                        + "\n\nEmployment Status: "
                        + (employed ? "Employed" : "Unemployed")
                        + "\nJob: " + jobName
                        + "\nSector: " + jobSector
                        + "\nJob Level: " + jobLevel
                        + "\nMonthly Salary: ₹" + monthlySalary
                        + "\n\nPERFORMANCE"
                        + "\nPerformance: " + performance + "/100"
                        + "\nReputation: " + reputation + "/100"
                        + "\nExperience: " + experience + " years"
                        + "\nYears in Current Job: " + yearsInJob
                        + "\nPromotions: " + promotions
                        + "\nBonuses Received: " + bonuses
                        + "\n\nAPPLICATIONS"
                        + "\nApplications: " + jobApplications
                        + "\nInterviews: " + interviews
                        + "\n\nTOTAL SALARY EARNED"
                        + "\n₹" + salaryReceived
                        + "\n\nUnemployment Duration: "
                        + unemploymentYears + " years";

        showMessage("Employment Report", report);
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
