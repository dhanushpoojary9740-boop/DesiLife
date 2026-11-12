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

public class GovernmentExamManager {

    public interface GovernmentExamCallback {
        void onUpdate(int money, int health, int happiness, String description);
    }

    private Activity activity;
    private SharedPreferences save;
    private int age, money, health, happiness;
    private GovernmentExamCallback callback;
    private Random random = new Random();

    private int gold = Color.rgb(226, 184, 105);
    private int dark = Color.rgb(12, 25, 23);
    private int card = Color.rgb(27, 43, 39);
    private int white = Color.WHITE;
    private int muted = Color.rgb(185, 195, 187);

    private String selectedExam = "";
    private String selectedJob = "";

    private static class Exam {
        String name;
        String department;
        String job;
        int minAge;
        int maxAge;
        int qualification;
        int fee;
        int preparationCost;
        int difficulty;
        int salary;

        Exam(String name, String department, String job,
             int minAge, int maxAge, int qualification,
             int fee, int preparationCost, int difficulty,
             int salary) {
            this.name = name;
            this.department = department;
            this.job = job;
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.qualification = qualification;
            this.fee = fee;
            this.preparationCost = preparationCost;
            this.difficulty = difficulty;
            this.salary = salary;
        }
    }

    private final ArrayList<Exam> exams = new ArrayList<>();

    public GovernmentExamManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            GovernmentExamCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        loadExams();
    }

    private void loadExams() {
        exams.clear();

        // UPSC
        addExam("UPSC Civil Services", "Union Government",
                "IAS Officer", 21, 32, 3, 1200, 5000, 95, 85000);

        addExam("UPSC Civil Services", "Union Government",
                "IPS Officer", 21, 32, 3, 1200, 5000, 90, 80000);

        addExam("UPSC Civil Services", "Foreign Services",
                "IFS Officer", 21, 32, 3, 1200, 5000, 93, 85000);

        // Karnataka Public Service Commission
        addExam("KPSC KAS", "Karnataka Government",
                "KAS Officer", 21, 35, 3, 600, 3500, 82, 65000);

        // SSC
        addExam("SSC CGL", "Central Government",
                "Income Tax Inspector", 18, 30, 3, 500, 2500, 72, 48000);

        addExam("SSC CGL", "Central Government",
                "Assistant Section Officer", 18, 30, 3, 500, 2500, 68, 45000);

        addExam("SSC CHSL", "Central Government",
                "Data Entry Operator", 18, 27, 2, 300, 1800, 60, 28000);

        addExam("SSC MTS", "Central Government",
                "Multi Tasking Staff", 18, 27, 1, 250, 1200, 50, 22000);

        // Banking
        addExam("IBPS PO", "Banking",
                "Probationary Officer", 20, 30, 3, 850, 3000, 75, 42000);

        addExam("SBI PO", "Banking",
                "Probationary Officer", 21, 30, 3, 850, 3500, 80, 48000);

        addExam("IBPS Clerk", "Banking",
                "Bank Clerk", 20, 28, 3, 600, 2200, 62, 28000);

        addExam("SBI Clerk", "Banking",
                "Junior Associate", 20, 28, 3, 600, 2500, 65, 30000);

        // Railways
        addExam("RRB NTPC", "Indian Railways",
                "Station Master", 18, 30, 3, 500, 2500, 70, 38000);

        addExam("RRB NTPC", "Indian Railways",
                "Goods Train Manager", 18, 30, 3, 500, 2500, 65, 35000);

        addExam("RRB Group D", "Indian Railways",
                "Railway Group D Employee", 18, 33, 1, 300, 1500, 55, 22000);

        // Police
        addExam("Karnataka Police", "Karnataka Police Department",
                "Police Constable", 18, 25, 2, 400, 1800, 60, 28000);

        addExam("Karnataka PSI", "Karnataka Police Department",
                "Police Sub Inspector", 21, 28, 3, 600, 2500, 72, 40000);

        addExam("State Police SI", "State Police Department",
                "Sub Inspector", 21, 28, 3, 600, 2500, 72, 40000);

        // Defence
        addExam("Indian Army Recruitment", "Indian Army",
                "Army Soldier", 17, 23, 1, 250, 1500, 55, 25000);

        addExam("AFCAT", "Indian Air Force",
                "Air Force Officer", 20, 24, 3, 550, 3000, 78, 56000);

        addExam("NDA", "National Defence Academy",
                "Defence Officer Cadet", 16, 19, 2, 500, 2500, 80, 45000);

        addExam("Indian Navy Recruitment", "Indian Navy",
                "Navy Sailor", 17, 23, 1, 300, 1500, 60, 26000);
    }

    private void addExam(String name, String department, String job,
                         int minAge, int maxAge, int qualification,
                         int fee, int preparationCost, int difficulty,
                         int salary) {
        exams.add(new Exam(name, department, job, minAge, maxAge,
                qualification, fee, preparationCost, difficulty, salary));
    }

    private String key(String exam, String field) {
        return "gov_exam_" + exam.replaceAll("[^A-Za-z0-9]", "_")
                + "_" + field;
    }

    private int getValue(String exam, String field, int defaultValue) {
        return save.getInt(key(exam, field), defaultValue);
    }

    private void setValue(String exam, String field, int value) {
        save.edit().putInt(key(exam, field), value).apply();
    }

    private String getText(String exam, String field, String defaultValue) {
        return save.getString(key(exam, field), defaultValue);
    }

    private void setText(String exam, String field, String value) {
        save.edit().putString(key(exam, field), value).apply();
    }

    private void saveAll() {
        save.edit()
                .putInt("gov_exam_player_money", money)
                .putInt("gov_exam_player_health", health)
                .putInt("gov_exam_player_happiness", happiness)
                .apply();
    }

    private void notifyUpdate(String description) {
        saveAll();

        if (callback != null) {
            callback.onUpdate(money, health, happiness, description);
        }
    }

    public void open() {
        showDashboard();
    }

    private void showDashboard() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 18);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(root);

        addTitle(root, "GOVERNMENT CAREERS");
        addText(root, "Prepare for competitive examinations and build a government career.");

        addInfoCard(root,
                "YOUR STATUS",
                "Age: " + age + " years\n"
                        + "Cash: ₹" + money + "\n"
                        + "Health: " + health + "\n"
                        + "Happiness: " + happiness + "\n"
                        + "Qualification: " + getQualificationName()
        );

        addButton(root, "Browse All Government Exams", new Runnable() {
            @Override
            public void run() {
                showExamCategories();
            }
        });

        addButton(root, "My Exam Applications", new Runnable() {
            @Override
            public void run() {
                showMyApplications();
            }
        });

        addButton(root, "My Government Job", new Runnable() {
            @Override
            public void run() {
                showGovernmentJob();
            }
        });

        addButton(root, "Exam Preparation Centre", new Runnable() {
            @Override
            public void run() {
                showPreparationCentre();
            }
        });

        addButton(root, "Back to Game", new Runnable() {
            @Override
            public void run() {
                activity.recreate();
            }
        });

        activity.setContentView(scroll);
    }

    private void showExamCategories() {
        final String[] categories = {
                "UPSC",
                "KPSC",
                "SSC",
                "Banking",
                "Railways",
                "Police",
                "Defence"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Exam Category")
                .setItems(categories, (dialog, which) -> {
                    showCategoryExams(categories[which]);
                })
                .setNegativeButton("Back", (dialog, which) -> showDashboard())
                .show();
    }

    private void showCategoryExams(String category) {
        ArrayList<Exam> filtered = new ArrayList<>();

        for (Exam exam : exams) {
            boolean match = false;

            if (category.equals("UPSC")) {
                match = exam.name.startsWith("UPSC");
            } else if (category.equals("KPSC")) {
                match = exam.name.startsWith("KPSC");
            } else if (category.equals("SSC")) {
                match = exam.name.startsWith("SSC");
            } else if (category.equals("Banking")) {
                match = exam.department.equals("Banking");
            } else if (category.equals("Railways")) {
                match = exam.department.equals("Indian Railways");
            } else if (category.equals("Police")) {
                match = exam.department.contains("Police");
            } else if (category.equals("Defence")) {
                match = exam.department.contains("Army")
                        || exam.department.contains("Air Force")
                        || exam.department.contains("Navy")
                        || exam.department.contains("Defence");
            }

            if (match) filtered.add(exam);
        }

        final Exam[] choices = filtered.toArray(new Exam[0]);
        String[] labels = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {
            labels[i] = choices[i].name + " - " + choices[i].job;
        }

        new AlertDialog.Builder(activity)
                .setTitle(category + " Examinations")
                .setItems(labels, (dialog, which) -> showExamDetails(choices[which]))
                .setNegativeButton("Back", (dialog, which) -> showExamCategories())
                .show();
    }

    private void showExamDetails(Exam exam) {
        String status = getText(exam.name + exam.job, "status", "Not Applied");
        int preparation = getValue(exam.name + exam.job, "preparation", 0);
        int attempts = getValue(exam.name + exam.job, "attempts", 0);

        String details =
                "Examination: " + exam.name
                        + "\nDepartment: " + exam.department
                        + "\nCareer: " + exam.job
                        + "\nAge requirement: " + exam.minAge + " - " + exam.maxAge
                        + "\nMinimum qualification: " + qualificationLabel(exam.qualification)
                        + "\nApplication fee: ₹" + exam.fee
                        + "\nPreparation cost: ₹" + exam.preparationCost
                        + "\nDifficulty: " + exam.difficulty + "/100"
                        + "\nStarting salary: ₹" + exam.salary
                        + "\nPreparation: " + preparation + "/100"
                        + "\nAttempts: " + attempts
                        + "\nApplication status: " + status;

        new AlertDialog.Builder(activity)
                .setTitle(exam.name)
                .setMessage(details)
                .setPositiveButton("Apply", (dialog, which) -> applyForExam(exam))
                .setNeutralButton("Prepare", (dialog, which) -> prepareForExam(exam))
                .setNegativeButton("Back", (dialog, which) -> showCategoryExams(categoryFor(exam)))
                .show();
    }

    private String categoryFor(Exam exam) {
        if (exam.name.startsWith("UPSC")) return "UPSC";
        if (exam.name.startsWith("KPSC")) return "KPSC";
        if (exam.name.startsWith("SSC")) return "SSC";
        if (exam.department.equals("Banking")) return "Banking";
        if (exam.department.equals("Indian Railways")) return "Railways";
        if (exam.department.contains("Police")) return "Police";
        return "Defence";
    }

    private void applyForExam(Exam exam) {
        String id = exam.name + exam.job;
        String status = getText(id, "status", "Not Applied");

        if (status.equals("Selected") || status.equals("Employed")) {
            message("You have already been selected for this career.");
            return;
        }

        if (status.equals("Applied") || status.equals("Written Passed")
                || status.equals("Interview")) {
            message("You already have an active application.");
            return;
        }

        if (age < exam.minAge || age > exam.maxAge) {
            message("You do not meet the game's age requirement for this examination.");
            return;
        }

        if (getQualificationLevel() < exam.qualification) {
            message("You need " + qualificationLabel(exam.qualification)
                    + " qualification before applying.");
            return;
        }

        if (money < exam.fee) {
            message("Not enough money to pay the application fee. Required: ₹" + exam.fee);
            return;
        }

        money -= exam.fee;
        setText(id, "status", "Applied");
        setValue(id, "attempts", getValue(id, "attempts", 0) + 1);
        setValue(id, "written_score", 0);
        setValue(id, "interview_score", 0);
        saveAll();

        notifyUpdate("Applied for " + exam.name + " - " + exam.job
                + ". Application fee ₹" + exam.fee + " paid.");

        showExamDashboard(exam);
    }

    private void prepareForExam(Exam exam) {
        String id = exam.name + exam.job;
        String status = getText(id, "status", "Not Applied");

        if (status.equals("Selected") || status.equals("Employed")) {
            message("You are already selected for this job.");
            return;
        }

        int preparation = getValue(id, "preparation", 0);

        if (preparation >= 100) {
            message("Your preparation is already complete.");
            return;
        }

        if (money < exam.preparationCost) {
            message("Not enough money. Preparation costs ₹"
                    + exam.preparationCost + ".");
            return;
        }

        money -= exam.preparationCost;

        int increase = 10 + random.nextInt(16);
        preparation = Math.min(100, preparation + increase);

        setValue(id, "preparation", preparation);

        if (health > 10) health = Math.max(10, health - random.nextInt(4));
        happiness = Math.min(100, happiness + 2);

        notifyUpdate("Studied for " + exam.name
                + ". Preparation level is now " + preparation + "%.");

        showExamDetails(exam);
    }

    private void showExamDashboard(Exam exam) {
        String id = exam.name + exam.job;
        String status = getText(id, "status", "Not Applied");

        String[] options;

        if (status.equals("Applied")) {
            options = new String[]{
                    "Write Written Examination",
                    "Prepare More",
                    "View Application"
            };
        } else if (status.equals("Written Passed")) {
            options = new String[]{
                    "Attend Interview",
                    "Prepare More",
                    "View Application"
            };
        } else if (status.equals("Interview")) {
            options = new String[]{
                    "Attend Interview Again",
                    "View Application"
            };
        } else {
            options = new String[]{"View Application"};
        }

        new AlertDialog.Builder(activity)
                .setTitle(exam.name + " - " + exam.job)
                .setMessage("Current status: " + status
                        + "\nPreparation: " + getValue(id, "preparation", 0) + "%"
                        + "\nAttempts: " + getValue(id, "attempts", 0))
                .setItems(options, (dialog, which) -> {
                    String choice = options[which];

                    if (choice.equals("Write Written Examination")) {
                        writeWrittenExam(exam);
                    } else if (choice.equals("Attend Interview")
                            || choice.equals("Attend Interview Again")) {
                        attendInterview(exam);
                    } else if (choice.equals("Prepare More")) {
                        prepareForExam(exam);
                    } else {
                        showExamDetails(exam);
                    }
                })
                .setNegativeButton("Back", (dialog, which) -> showDashboard())
                .show();
    }

    private void writeWrittenExam(Exam exam) {
        String id = exam.name + exam.job;

        if (!getText(id, "status", "").equals("Applied")) {
            message("You do not have an active written examination.");
            return;
        }

        int preparation = getValue(id, "preparation", 0);

        int score = 25 + random.nextInt(36)
                + preparation / 3
                + (health / 20)
                + (intelligenceBonus());

        score = Math.min(100, score);

        setValue(id, "written_score", score);

        if (score >= exam.difficulty) {
            setText(id, "status", "Written Passed");

            notifyUpdate("Congratulations! You passed the written examination for "
                    + exam.job + ". Score: " + score + "/100.");

            showExamDashboard(exam);
        } else {
            setText(id, "status", "Failed");
            happiness = Math.max(0, happiness - 8);

            notifyUpdate("You did not pass the written examination for "
                    + exam.job + ". Score: " + score + "/100.");

            showExamDetails(exam);
        }
    }

    private int intelligenceBonus() {
        return save.getInt("intelligence", 50) / 5;
    }

    private void attendInterview(Exam exam) {
        String id = exam.name + exam.job;

        if (!getText(id, "status", "").equals("Written Passed")
                && !getText(id, "status", "").equals("Interview")) {
            message("You must pass the written examination first.");
            return;
        }

        int preparation = getValue(id, "preparation", 0);
        int interviewScore = 30 + random.nextInt(36)
                + preparation / 4
                + happiness / 20;

        interviewScore = Math.min(100, interviewScore);
        setValue(id, "interview_score", interviewScore);

        if (interviewScore >= 60) {
            setText(id, "status", "Selected");

            notifyUpdate("Congratulations! You have been selected as "
                    + exam.job + "! Interview score: " + interviewScore + "/100.");

            offerAppointment(exam);
        } else {
            setText(id, "status", "Interview");
            happiness = Math.max(0, happiness - 5);

            notifyUpdate("Interview unsuccessful for " + exam.job
                    + ". Score: " + interviewScore + "/100.");

            showExamDashboard(exam);
        }
    }

    private void offerAppointment(Exam exam) {
        String id = exam.name + exam.job;

        new AlertDialog.Builder(activity)
                .setTitle("Government Job Selection!")
                .setMessage("You have been selected!\n\n"
                        + "Department: " + exam.department
                        + "\nPosition: " + exam.job
                        + "\nStarting monthly salary: ₹" + exam.salary
                        + "\n\nAccept this appointment?")
                .setCancelable(false)
                .setPositiveButton("Accept Job", (dialog, which) -> {
                    setText(id, "status", "Employed");
                    setText("career", "job", exam.job);
                    setText("career", "department", exam.department);
                    setValue("career", "salary", exam.salary);
                    setValue("career", "promotion_level", 0);

                    selectedJob = exam.job;

                    notifyUpdate("You joined " + exam.department
                            + " as " + exam.job + ".");

                    showGovernmentJob();
                })
                .setNegativeButton("Decline", (dialog, which) -> {
                    setText(id, "status", "Selected");
                    notifyUpdate("You declined the government job offer.");
                    showDashboard();
                })
                .show();
    }

    private void showMyApplications() {
        StringBuilder details = new StringBuilder();
        boolean found = false;

        for (Exam exam : exams) {
            String id = exam.name + exam.job;
            String status = getText(id, "status", "Not Applied");

            if (!status.equals("Not Applied")) {
                found = true;
                details.append("\n")
                        .append(exam.name)
                        .append("\nPosition: ").append(exam.job)
                        .append("\nStatus: ").append(status)
                        .append("\nPreparation: ")
                        .append(getValue(id, "preparation", 0))
                        .append("%\nWritten score: ")
                        .append(getValue(id, "written_score", 0))
                        .append("\nInterview score: ")
                        .append(getValue(id, "interview_score", 0))
                        .append("\nAttempts: ")
                        .append(getValue(id, "attempts", 0))
                        .append("\n-------------------------\n");
            }
        }

        if (!found) {
            details.append("You have not applied for any government examination yet.");
        }

        new AlertDialog.Builder(activity)
                .setTitle("My Applications")
                .setMessage(details.toString())
                .setPositiveButton("OK", (dialog, which) -> showDashboard())
                .show();
    }

    private void showPreparationCentre() {
        ArrayList<Exam> available = new ArrayList<>();

        for (Exam exam : exams) {
            String status = getText(exam.name + exam.job, "status", "Not Applied");

            if (!status.equals("Selected") && !status.equals("Employed")) {
                available.add(exam);
            }
        }

        final Exam[] choices = available.toArray(new Exam[0]);
        String[] labels = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {
            String id = choices[i].name + choices[i].job;
            labels[i] = choices[i].name + " - " + choices[i].job
                    + " (" + getValue(id, "preparation", 0) + "%)";
        }

        new AlertDialog.Builder(activity)
                .setTitle("Choose Your Preparation")
                .setItems(labels, (dialog, which) -> prepareForExam(choices[which]))
                .setNegativeButton("Back", (dialog, which) -> showDashboard())
                .show();
    }

    private void showGovernmentJob() {
        String job = getText("career", "job", "");
        String department = getText("career", "department", "");
        int salary = getValue("career", "salary", 0);
        int level = getValue("career", "promotion_level", 0);

        if (job.isEmpty()) {
            message("You do not currently hold a government job.");
            return;
        }

        String details = "Position: " + job
                + "\nDepartment: " + department
                + "\nMonthly salary: ₹" + salary
                + "\nAnnual salary: ₹" + ((long) salary * 12)
                + "\nPromotion level: " + level
                + "\nCareer status: Active";

        new AlertDialog.Builder(activity)
                .setTitle("Government Career")
                .setMessage(details)
                .setPositiveButton("Request Promotion", (dialog, which) -> requestPromotion())
                .setNeutralButton("Collect Salary", (dialog, which) -> collectSalary())
                .setNegativeButton("Back", (dialog, which) -> showDashboard())
                .show();
    }

    private void collectSalary() {
        String job = getText("career", "job", "");
        int salary = getValue("career", "salary", 0);

        if (job.isEmpty() || salary <= 0) {
            message("No active government job found.");
            return;
        }

        if (money > Integer.MAX_VALUE - salary) {
            money = Integer.MAX_VALUE;
        } else {
            money += salary;
        }

        notifyUpdate("You received your monthly government salary of ₹" + salary + ".");
        showGovernmentJob();
    }

    private void requestPromotion() {
        String job = getText("career", "job", "");
        int salary = getValue("career", "salary", 0);
        int level = getValue("career", "promotion_level", 0);

        if (job.isEmpty()) {
            message("You do not have a government job.");
            return;
        }

        if (level >= 10) {
            message("You have reached the maximum promotion level.");
            return;
        }

        int chance = 35 + random.nextInt(46);

        if (random.nextInt(100) < chance) {
            int increase = Math.max(3000, salary / 5);
            long newSalary = (long) salary + increase;

            setValue("career", "salary",
                    (int) Math.min(Integer.MAX_VALUE, newSalary));

            setValue("career", "promotion_level", level + 1);

            notifyUpdate("Promotion successful! Your salary increased to ₹"
                    + getValue("career", "salary", salary) + ".");
        } else {
            happiness = Math.max(0, happiness - 3);
            notifyUpdate("Your promotion request was not approved this time.");
        }

        showGovernmentJob();
    }

    public void applyAnnualUpdate() {
        String job = getText("career", "job", "");

        if (!job.isEmpty()) {
            int salary = getValue("career", "salary", 0);

            if (salary > 0) {
                int increment = Math.max(500, salary / 20);
                long updated = (long) salary + increment;

                setValue("career", "salary",
                        (int) Math.min(Integer.MAX_VALUE, updated));

                saveAll();
            }
        }
    }

    private int getQualificationLevel() {
        String education = save.getString("education_level", "");

        if (education.isEmpty()) {
            education = save.getString("edu_level", "");
        }

        if (education.contains("PhD")
                || education.contains("MBA")
                || education.contains("Master")
                || education.contains("M.Tech")
                || education.contains("LLM")) {
            return 4;
        }

        if (education.contains("Degree")
                || education.contains("Undergraduate")
                || education.contains("Bachelor")) {
            return 3;
        }

        if (education.contains("PUC")
                || education.contains("12")
                || education.contains("Higher Secondary")) {
            return 2;
        }

        if (education.contains("High School")
                || education.contains("SSLC")
                || education.contains("10th")) {
            return 1;
        }

        return 0;
    }

    private String getQualificationName() {
        return qualificationLabel(getQualificationLevel());
    }

    private String qualificationLabel(int level) {
        if (level >= 4) return "Postgraduate";
        if (level == 3) return "Graduate";
        if (level == 2) return "12th / PUC";
        if (level == 1) return "10th / SSLC";
        return "Below 10th";
    }

    private void addTitle(LinearLayout root, String text) {
        TextView title = new TextView(activity);
        title.setText(text);
        title.setTextColor(gold);
        title.setTextSize(24);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(8, 20, 8, 20);
        root.addView(title);
    }

    private void addText(LinearLayout root, String text) {
        TextView view = new TextView(activity);
        view.setText(text);
        view.setTextColor(muted);
        view.setTextSize(14);
        view.setPadding(8, 8, 8, 16);
        root.addView(view);
    }

    private void addInfoCard(LinearLayout root, String title, String details) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(16, 14, 16, 14);
        box.setBackground(background(card, 16));

        TextView heading = new TextView(activity);
        heading.setText(title);
        heading.setTextColor(gold);
        heading.setTextSize(17);
        heading.setTypeface(null, Typeface.BOLD);
        box.addView(heading);

        TextView content = new TextView(activity);
        content.setText(details);
        content.setTextColor(white);
        content.setTextSize(15);
        content.setPadding(0, 10, 0, 0);
        box.addView(content);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                -1, -2);
        params.setMargins(0, 10, 0, 14);
        root.addView(box, params);
    }

    private void addButton(LinearLayout root, String text, Runnable action) {
        TextView button = new TextView(activity);
        button.setText(text);
        button.setTextColor(dark);
        button.setTextSize(15);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(12, 17, 12, 17);
        button.setBackground(background(gold, 14));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                -1, -2);
        params.setMargins(0, 7, 0, 7);
        root.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private void message(String text) {
        new AlertDialog.Builder(activity)
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show();
    }
}
