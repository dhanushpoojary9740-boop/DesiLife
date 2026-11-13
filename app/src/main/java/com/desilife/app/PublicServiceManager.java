
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

public class PublicServiceManager {

    public interface PublicServiceCallback {
        void onUpdate(int money, int health, int happiness, String description);
    }

    private Activity activity;
    private SharedPreferences save;
    private int age, money, health, happiness;
    private PublicServiceCallback callback;
    private Random random = new Random();

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int card = Color.rgb(27, 43, 39);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 195, 187);

    private static class Department {
        String name;
        String category;
        String location;
        String[] positions;
        int salary;
        int minimumAge;
        int minimumEducation;

        Department(String name, String category, String location,
                   String[] positions, int salary,
                   int minimumAge, int minimumEducation) {
            this.name = name;
            this.category = category;
            this.location = location;
            this.positions = positions;
            this.salary = salary;
            this.minimumAge = minimumAge;
            this.minimumEducation = minimumEducation;
        }
    }

    private final ArrayList<Department> departments = new ArrayList<>();

    public PublicServiceManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            PublicServiceCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        loadDepartments();
    }

    private void loadDepartments() {
        departments.clear();

        // Karnataka Government Departments

        add("Revenue Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Village Accountant", "Revenue Inspector",
                        "Tahsildar", "Assistant Commissioner"},
                30000, 18, 2);

        add("Survey and Land Records", "Karnataka Government",
                "Karnataka",
                new String[]{"Survey Assistant", "Surveyor",
                        "Senior Surveyor", "Survey Supervisor"},
                32000, 18, 2);

        add("Karnataka Police Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Police Constable", "Head Constable",
                        "Police Sub Inspector", "Police Inspector"},
                28000, 18, 1);

        add("Forest Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Forest Guard", "Forester",
                        "Range Forest Officer", "Forest Officer"},
                29000, 18, 2);

        add("Health Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Health Assistant", "Health Inspector",
                        "Health Officer", "District Health Officer"},
                30000, 18, 2);

        add("Education Department", "Karnataka Government",
                "Karnataka",
                new String[]{"School Assistant", "Teacher",
                        "Head Teacher", "Education Officer"},
                28000, 18, 2);

        add("Public Works Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Junior Assistant", "Junior Engineer",
                        "Assistant Engineer", "Executive Engineer"},
                32000, 18, 2);

        add("Karnataka Transport Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Transport Assistant", "Motor Vehicle Inspector",
                        "Assistant Transport Officer", "Regional Transport Officer"},
                30000, 18, 2);

        add("Rural Development Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Village Development Assistant",
                        "Panchayat Development Officer",
                        "Assistant Director", "Development Officer"},
                29000, 18, 2);

        add("Agriculture Department", "Karnataka Government",
                "Karnataka",
                new String[]{"Agriculture Assistant", "Agriculture Officer",
                        "Senior Agriculture Officer", "Joint Director"},
                30000, 18, 2);

        // Central Government Departments

        add("Indian Railways", "Central Government",
                "All India",
                new String[]{"Railway Assistant", "Senior Railway Assistant",
                        "Station Supervisor", "Railway Officer"},
                30000, 18, 1);

        add("Income Tax Department", "Central Government",
                "All India",
                new String[]{"Tax Assistant", "Income Tax Inspector",
                        "Income Tax Officer", "Assistant Commissioner"},
                35000, 18, 3);

        add("Postal Department", "Central Government",
                "All India",
                new String[]{"Postal Assistant", "Sorting Assistant",
                        "Postal Inspector", "Postal Superintendent"},
                26000, 18, 1);

        add("Central Secretariat", "Central Government",
                "New Delhi",
                new String[]{"Assistant Section Officer",
                        "Section Officer", "Under Secretary",
                        "Deputy Secretary"},
                38000, 18, 3);

        add("Central Public Works Department", "Central Government",
                "All India",
                new String[]{"Junior Engineer", "Assistant Engineer",
                        "Executive Engineer", "Superintending Engineer"},
                35000, 18, 2);

        add("Customs Department", "Central Government",
                "All India",
                new String[]{"Customs Assistant", "Preventive Officer",
                        "Superintendent", "Assistant Commissioner"},
                35000, 18, 3);
    }

    private void add(String name, String category, String location,
                     String[] positions, int salary,
                     int minimumAge, int minimumEducation) {
        departments.add(new Department(
                name, category, location, positions,
                salary, minimumAge, minimumEducation
        ));
    }

    private String k(String field) {
        return "public_service_" + field;
    }

    private int getInt(String field, int defaultValue) {
        return save.getInt(k(field), defaultValue);
    }

    private void putInt(String field, int value) {
        save.edit().putInt(k(field), value).apply();
    }

    private String getString(String field, String defaultValue) {
        return save.getString(k(field), defaultValue);
    }

    private void putString(String field, String value) {
        save.edit().putString(k(field), value).apply();
    }

    private boolean getBoolean(String field, boolean defaultValue) {
        return save.getBoolean(k(field), defaultValue);
    }

    private void putBoolean(String field, boolean value) {
        save.edit().putBoolean(k(field), value).apply();
    }

    private void saveAll() {
        save.edit()
                .putInt("public_service_money", money)
                .putInt("public_service_health", health)
                .putInt("public_service_happiness", happiness)
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

        addTitle(root, "PUBLIC SERVICE CAREER");

        addText(root,
                "Build your career in Karnataka and Central Government departments.");

        String job = getString("job", "");
        String department = getString("department", "");

        String status = job.isEmpty()
                ? "Not employed"
                : "Working as " + job;

        addInfoCard(root, "YOUR PROFILE",
                "Age: " + age
                        + "\nCash: ₹" + money
                        + "\nHealth: " + health
                        + "\nHappiness: " + happiness
                        + "\nEmployment: " + status
                        + (department.isEmpty() ? "" : "\nDepartment: " + department));

        addButton(root, "Government Departments",
                this::showCategories);

        addButton(root, "My Service Record",
                this::showServiceRecord);

        addButton(root, "Work Assignments",
                this::showAssignments);

        addButton(root, "Transfers and Postings",
                this::showTransfers);

        addButton(root, "Government Benefits",
                this::showBenefits);

        addButton(root, "Retirement and Pension",
                this::showRetirement);

        addButton(root, "Back to Game",
                () -> activity.recreate());

        activity.setContentView(scroll);
    }

    private void showCategories() {
        String[] categories = {
                "Karnataka Government",
                "Central Government"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Government")
                .setItems(categories, (dialog, which) -> {
                    showDepartmentList(categories[which]);
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void showDepartmentList(String category) {
        ArrayList<Department> filtered = new ArrayList<>();

        for (Department d : departments) {
            if (d.category.equals(category)) {
                filtered.add(d);
            }
        }

        final Department[] choices =
                filtered.toArray(new Department[0]);

        String[] labels = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {
            labels[i] = choices[i].name
                    + " - ₹" + choices[i].salary + "/month";
        }

        new AlertDialog.Builder(activity)
                .setTitle(category)
                .setItems(labels, (dialog, which) -> {
                    showDepartmentDetails(choices[which]);
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showCategories())
                .show();
    }

    private void showDepartmentDetails(Department d) {
        StringBuilder positions = new StringBuilder();

        for (int i = 0; i < d.positions.length; i++) {
            positions.append(i + 1)
                    .append(". ")
                    .append(d.positions[i])
                    .append("\n");
        }

        String details =
                "Department: " + d.name
                        + "\nGovernment: " + d.category
                        + "\nPosting area: " + d.location
                        + "\nStarting salary: ₹" + d.salary
                        + "\nMinimum age: " + d.minimumAge
                        + "\nRequired qualification: "
                        + qualificationName(d.minimumEducation)
                        + "\n\nCareer positions:\n" + positions;

        new AlertDialog.Builder(activity)
                .setTitle(d.name)
                .setMessage(details)
                .setPositiveButton("Apply for Department",
                        (dialog, which) -> applyForDepartment(d))
                .setNegativeButton("Back",
                        (dialog, which) -> showDepartmentList(d.category))
                .show();
    }

    private void applyForDepartment(Department d) {
        if (!getString("job", "").isEmpty()) {
            message("You are already employed in a public service department. Resign before applying for another department.");
            return;
        }

        if (age < d.minimumAge || age > 60) {
            message("You do not meet the game's age requirement.");
            return;
        }

        if (getQualificationLevel() < d.minimumEducation) {
            message("Required qualification: "
                    + qualificationName(d.minimumEducation));
            return;
        }

        int preparation = getInt("preparation", 0);

        final int finalChance = Math.min(
                95,
                25 + preparation / 2 + random.nextInt(26)
        );

        new AlertDialog.Builder(activity)
                .setTitle("Department Recruitment")
                .setMessage("Apply for " + d.name + "?\n\n"
                        + "Starting salary: ₹" + d.salary
                        + "\nSelection probability: " + finalChance + "%\n\n"
                        + "Your application will be evaluated immediately.")
                .setPositiveButton("Apply",
                        (dialog, which) -> {

                            if (random.nextInt(100) < finalChance) {
                                putString("job", d.positions[0]);
                                putString("department", d.name);
                                putString("category", d.category);
                                putString("location", d.location);

                                putInt("salary", d.salary);
                                putInt("level", 0);
                                putInt("service_years", 0);
                                putInt("performance", 50);
                                putInt("assignments_completed", 0);
                                putInt("assignments_failed", 0);

                                putBoolean("accommodation", false);
                                putBoolean("vehicle_benefit", false);
                                putBoolean("medical_benefit", false);

                                putString("status", "Active");

                                notifyUpdate("Congratulations! You joined "
                                        + d.name + " as "
                                        + d.positions[0] + ".");

                                showServiceRecord();

                            } else {
                                happiness = Math.max(0, happiness - 5);

                                notifyUpdate("Your application to "
                                        + d.name + " was unsuccessful.");

                                showDepartmentDetails(d);
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showServiceRecord() {
        String job = getString("job", "");

        if (job.isEmpty()) {
            message("You are not currently employed in a government department.");
            return;
        }

        String details =
                "Department: " + getString("department", "")
                        + "\nPosition: " + job
                        + "\nGovernment: " + getString("category", "")
                        + "\nPosting: " + getString("location", "")
                        + "\nMonthly salary: ₹" + getInt("salary", 0)
                        + "\nService years: " + getInt("service_years", 0)
                        + "\nPromotion level: " + getInt("level", 0)
                        + "\nPerformance: " + getInt("performance", 50) + "%"
                        + "\nCompleted assignments: "
                        + getInt("assignments_completed", 0)
                        + "\nFailed assignments: "
                        + getInt("assignments_failed", 0)
                        + "\nEmployment status: " + getString("status", "Active");

        new AlertDialog.Builder(activity)
                .setTitle("Government Service Record")
                .setMessage(details)
                .setPositiveButton("Collect Salary",
                        (dialog, which) -> collectSalary())
                .setNeutralButton("Promotion",
                        (dialog, which) -> requestPromotion())
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void collectSalary() {
        String job = getString("job", "");
        int salary = getInt("salary", 0);

        if (job.isEmpty() || salary <= 0) {
            message("No active government employment.");
            return;
        }

        if (!getString("status", "Active").equals("Active")) {
            message("Your government service is not active.");
            return;
        }

        money = (int) Math.min(
                Integer.MAX_VALUE,
                (long) money + salary
        );

        notifyUpdate("Monthly government salary received: ₹" + salary);

        showServiceRecord();
    }

    private void requestPromotion() {
        String job = getString("job", "");

        if (job.isEmpty()) {
            message("You are not employed in government service.");
            return;
        }

        if (!getString("status", "Active").equals("Active")) {
            message("Your government service is not active.");
            return;
        }

        int performance = getInt("performance", 50);
        int level = getInt("level", 0);

        if (level >= 3) {
            message("You have reached the highest position in this career path.");
            return;
        }

        int serviceYears = getInt("service_years", 0);
        int requiredYears = 1 + (level * 2);
        int requiredPerformance = 60 + (level * 5);

        if (serviceYears < requiredYears) {
            message("Promotion requirement not met.\n\n"
                    + "Required service: " + requiredYears + " years"
                    + "\nYour service: " + serviceYears + " years.");
            return;
        }

        if (performance < requiredPerformance) {
            message("Promotion requirement not met.\n\n"
                    + "Required performance: " + requiredPerformance + "%"
                    + "\nYour performance: " + performance + "%.");
            return;
        }

        int chance = Math.min(90, 30 + performance / 2);

        if (random.nextInt(100) < chance) {
            String department = getString("department", "");
            Department d = findDepartment(department);

            if (d != null && level + 1 < d.positions.length) {
                level++;

                putInt("level", level);
                putString("job", d.positions[level]);

                int salary = getInt("salary", d.salary);
                salary = (int) Math.min(
                        Integer.MAX_VALUE,
                        (long) salary + Math.max(3000, salary / 5)
                );

                putInt("salary", salary);

                notifyUpdate("Promotion approved! You are now "
                        + d.positions[level] + ".");
            } else {
                message("No further position is available in this career path.");
            }

        } else {
            happiness = Math.max(0, happiness - 3);

            notifyUpdate("Your promotion request was not approved.");
        }

        showServiceRecord();
    }

    private Department findDepartment(String name) {
        for (Department d : departments) {
            if (d.name.equals(name)) return d;
        }
        return null;
    }

    private void showAssignments() {
        if (getString("job", "").isEmpty()) {
            message("You need to join a government department first.");
            return;
        }

        if (!getString("status", "Active").equals("Active")) {
            message("Your government service is not active.");
            return;
        }

        String[] tasks = {
                "Complete an official file review",
                "Attend a departmental meeting",
                "Inspect a government project",
                "Complete public service records",
                "Resolve a citizen complaint",
                "Submit an annual departmental report"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Official Work Assignments")
                .setItems(tasks, (dialog, which) -> {
                    completeAssignment(tasks[which]);
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void completeAssignment(String task) {
        int performance = getInt("performance", 50);
        int chance = 50 + performance / 2;

        if (random.nextInt(100) < chance) {
            performance = Math.min(100, performance + 8);

            putInt("performance", performance);
            putInt("assignments_completed",
                    getInt("assignments_completed", 0) + 1);

            happiness = Math.min(100, happiness + 2);

            notifyUpdate("Assignment completed: " + task
                    + ". Work performance: " + performance + "%.");

        } else {
            performance = Math.max(0, performance - 5);

            putInt("performance", performance);
            putInt("assignments_failed",
                    getInt("assignments_failed", 0) + 1);

            happiness = Math.max(0, happiness - 3);

            notifyUpdate("Assignment incomplete: " + task
                    + ". Your performance decreased.");
        }

        showAssignments();
    }

    private void showTransfers() {
        if (getString("job", "").isEmpty()) {
            message("You need to be employed first.");
            return;
        }

        if (!getString("status", "Active").equals("Active")) {
            message("Your government service is not active.");
            return;
        }

        String[] locations = {
                "Bengaluru",
                "Mysuru",
                "Mangaluru",
                "Udupi",
                "Belagavi",
                "Shivamogga",
                "Kalaburagi",
                "Hubballi",
                "New Delhi",
                "Other State"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Request Transfer")
                .setItems(locations, (dialog, which) -> {
                    requestTransfer(locations[which]);
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void requestTransfer(String location) {
        int performance = getInt("performance", 50);
        int chance = 30 + performance / 2;

        if (random.nextInt(100) < chance) {
            String oldLocation = getString("location", "");

            putString("location", location);

            notifyUpdate("Transfer approved from "
                    + oldLocation + " to " + location + ".");
        } else {
            happiness = Math.max(0, happiness - 3);

            notifyUpdate("Your transfer request to "
                    + location + " was rejected.");
        }

        showDashboard();
    }

    private void showBenefits() {
        if (getString("job", "").isEmpty()) {
            message("Government benefits are available after joining public service.");
            return;
        }

        String[] options = {
                "Government Accommodation",
                "Medical Benefits",
                "Official Vehicle Allowance",
                "View My Benefits"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Government Employee Benefits")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        activateBenefit("accommodation",
                                "Government accommodation");
                    } else if (which == 1) {
                        activateBenefit("medical_benefit",
                                "Medical benefits");
                    } else if (which == 2) {
                        activateBenefit("vehicle_benefit",
                                "Vehicle allowance");
                    } else {
                        showBenefitSummary();
                    }
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void activateBenefit(String field, String name) {
        if (getBoolean(field, false)) {
            message("This benefit is already active.");
            return;
        }

        int performance = getInt("performance", 50);

        if (performance < 50) {
            message("Improve your work performance before requesting this benefit.");
            return;
        }

        putBoolean(field, true);

        notifyUpdate(name + " activated successfully.");

        showBenefits();
    }

    private void showBenefitSummary() {
        String details =
                "Government accommodation: "
                        + yesNo(getBoolean("accommodation", false))
                        + "\nMedical benefits: "
                        + yesNo(getBoolean("medical_benefit", false))
                        + "\nVehicle allowance: "
                        + yesNo(getBoolean("vehicle_benefit", false));

        new AlertDialog.Builder(activity)
                .setTitle("My Government Benefits")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showRetirement() {
        if (getString("job", "").isEmpty()) {
            message("You do not have government service history.");
            return;
        }

        int serviceYears = getInt("service_years", 0);
        int salary = getInt("salary", 0);
        int retirementAge = 60;

        int pension = (int) Math.min(
                Integer.MAX_VALUE,
                (long) salary * Math.min(75, serviceYears * 2) / 100
        );

        String details =
                "Age: " + age
                        + "\nRetirement age: " + retirementAge
                        + "\nYears of service: " + serviceYears
                        + "\nCurrent monthly salary: ₹" + salary
                        + "\nEstimated monthly pension: ₹" + pension
                        + "\n\nPension is a simplified fictional game calculation.";

        if (age >= retirementAge) {
            new AlertDialog.Builder(activity)
                    .setTitle("Retirement")
                    .setMessage(details + "\n\nYou are eligible to retire in the game.")
                    .setPositiveButton("Retire",
                            (dialog, which) -> retire(pension))
                    .setNegativeButton("Back",
                            (dialog, which) -> showDashboard())
                    .show();
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("Retirement Planning")
                    .setMessage(details)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void retire(int pension) {
        putString("status", "Retired");
        putInt("pension", pension);
        putString("retired_job", getString("job", ""));
        putString("retired_department", getString("department", ""));

        notifyUpdate("You retired from government service. "
                + "Estimated monthly pension: ₹" + pension + ".");

        showDashboard();
    }

    public void applyAnnualUpdate() {
        String job = getString("job", "");

        if (job.isEmpty()) return;

        String status = getString("status", "Active");

        if (status.equals("Active")) {
            putInt("service_years", getInt("service_years", 0) + 1);

            int salary = getInt("salary", 0);

            if (salary > 0) {
                int increment = Math.max(500, salary / 20);

                long updatedSalary = (long) salary + increment;

                putInt("salary", (int) Math.min(
                        Integer.MAX_VALUE, updatedSalary));
            }

            int performance = getInt("performance", 50);

            if (performance < 100) {
                putInt("performance",
                        Math.min(100, performance + 2));
            }

            if (age >= 60) {
                int years = getInt("service_years", 0);

                int pension = (int) Math.min(
                        Integer.MAX_VALUE,
                        (long) getInt("salary", 0)
                                * Math.min(75, years * 2) / 100
                );

                putString("status", "Retired");
                putInt("pension", pension);
            }

            saveAll();

        } else if (status.equals("Retired")) {
            int pension = getInt("pension", 0);

            if (pension > 0) {
                money = (int) Math.min(
                        Integer.MAX_VALUE,
                        (long) money + pension
                );

                notifyUpdate("Monthly pension received: ₹" + pension);
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

    private String qualificationName(int level) {
        if (level >= 4) return "Postgraduate";
        if (level == 3) return "Graduate";
        if (level == 2) return "12th / PUC";
        if (level == 1) return "10th / SSLC";
        return "Below 10th";
    }

    private String yesNo(boolean value) {
        return value ? "Active" : "Not Available";
    }

    private void addTitle(LinearLayout root, String text) {
        TextView title = new TextView(activity);
        title.setText(text);
        title.setTextColor(gold);
        title.setTextSize(23);
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

    private void addInfoCard(LinearLayout root, String title,
                             String details) {
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

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 10, 0, 14);
        root.addView(box, params);
    }

    private void addButton(LinearLayout root, String text,
                           Runnable action) {
        TextView button = new TextView(activity);
        button.setText(text);
        button.setTextColor(dark);
        button.setTextSize(15);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(12, 17, 12, 17);
        button.setBackground(background(gold, 14));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

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
