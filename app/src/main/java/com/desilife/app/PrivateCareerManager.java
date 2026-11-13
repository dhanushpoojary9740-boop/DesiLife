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

public class PrivateCareerManager {

    public interface PrivateCareerCallback {
        void onUpdate(int money, int health, int happiness,
                      String description);
    }

    private Activity activity;
    private SharedPreferences save;
    private int age, money, health, happiness;
    private PrivateCareerCallback callback;
    private Random random = new Random();

    private final int gold = Color.rgb(226, 184, 105);
    private final int dark = Color.rgb(12, 25, 23);
    private final int card = Color.rgb(27, 43, 39);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(185, 195, 187);

    private static class Company {
        String name;
        String sector;
        String location;
        String[] positions;
        int salary;
        int minimumAge;
        int education;
        int difficulty;

        Company(String name, String sector, String location,
                String[] positions, int salary,
                int minimumAge, int education, int difficulty) {
            this.name = name;
            this.sector = sector;
            this.location = location;
            this.positions = positions;
            this.salary = salary;
            this.minimumAge = minimumAge;
            this.education = education;
            this.difficulty = difficulty;
        }
    }

    private final ArrayList<Company> companies = new ArrayList<>();

    public PrivateCareerManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int money,
            int health,
            int happiness,
            PrivateCareerCallback callback
    ) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;

        loadCompanies();
    }

    private void loadCompanies() {
        companies.clear();

        // Information Technology

        add("Infosys", "Information Technology", "Bengaluru",
                new String[]{"Junior Software Engineer",
                        "Software Engineer", "Senior Engineer",
                        "Technical Manager"},
                35000, 18, 3, 65);

        add("TCS", "Information Technology", "Bengaluru",
                new String[]{"IT Associate", "Software Developer",
                        "Senior Developer", "Project Manager"},
                32000, 18, 3, 60);

        add("Wipro", "Information Technology", "Bengaluru",
                new String[]{"Associate Engineer", "Software Engineer",
                        "Senior Engineer", "Technology Manager"},
                33000, 18, 3, 62);

        add("Accenture", "Information Technology", "Bengaluru",
                new String[]{"Technology Associate", "Developer",
                        "Senior Developer", "Delivery Manager"},
                38000, 18, 3, 68);

        add("Google India", "Information Technology", "Bengaluru",
                new String[]{"Junior Software Engineer",
                        "Software Engineer", "Senior Engineer",
                        "Engineering Manager"},
                85000, 18, 3, 92);

        add("Microsoft India", "Information Technology", "Bengaluru",
                new String[]{"Software Engineer", "Developer",
                        "Senior Software Engineer", "Engineering Manager"},
                90000, 18, 3, 93);

        // Banking and Finance

        add("HDFC Bank", "Banking and Finance", "Bengaluru",
                new String[]{"Banking Associate", "Relationship Manager",
                        "Branch Manager", "Regional Manager"},
                28000, 18, 2, 60);

        add("ICICI Bank", "Banking and Finance", "Mangaluru",
                new String[]{"Customer Service Officer", "Bank Officer",
                        "Branch Manager", "Area Manager"},
                30000, 18, 2, 62);

        add("Axis Bank", "Banking and Finance", "Mysuru",
                new String[]{"Bank Associate", "Assistant Manager",
                        "Branch Manager", "Regional Head"},
                29000, 18, 2, 60);

        add("Kotak Mahindra Bank", "Banking and Finance", "Bengaluru",
                new String[]{"Bank Associate", "Relationship Officer",
                        "Branch Manager", "Regional Manager"},
                31000, 18, 2, 63);

        // Healthcare

        add("Manipal Hospitals", "Healthcare", "Manipal",
                new String[]{"Hospital Assistant", "Hospital Administrator",
                        "Senior Administrator", "Operations Manager"},
                28000, 18, 2, 60);

        add("Apollo Hospitals", "Healthcare", "Bengaluru",
                new String[]{"Healthcare Assistant", "Hospital Executive",
                        "Senior Executive", "Hospital Manager"},
                30000, 18, 2, 65);

        // Education

        add("Private School Network", "Education", "Mysuru",
                new String[]{"Teaching Assistant", "School Teacher",
                        "Senior Teacher", "School Principal"},
                22000, 18, 2, 55);

        add("Private College Network", "Education", "Bengaluru",
                new String[]{"Lecturer", "Assistant Professor",
                        "Associate Professor", "Principal"},
                30000, 18, 3, 70);

        // Manufacturing and Automobile

        add("Tata Motors", "Manufacturing and Automobile", "Bengaluru",
                new String[]{"Production Assistant", "Production Engineer",
                        "Senior Engineer", "Plant Manager"},
                30000, 18, 2, 65);

        add("Toyota Kirloskar", "Manufacturing and Automobile",
                "Bidadi",
                new String[]{"Factory Associate", "Technician",
                        "Production Supervisor", "Factory Manager"},
                28000, 18, 2, 62);

        add("Bosch India", "Manufacturing and Automobile", "Bengaluru",
                new String[]{"Technical Assistant", "Engineer",
                        "Senior Engineer", "Operations Manager"},
                35000, 18, 3, 70);

        // Hospitality and Tourism

        add("Taj Hotels", "Hospitality and Tourism", "Bengaluru",
                new String[]{"Front Office Associate", "Guest Relations Executive",
                        "Hotel Supervisor", "Hotel Manager"},
                24000, 18, 1, 58);

        add("ITC Hotels", "Hospitality and Tourism", "Mysuru",
                new String[]{"Hotel Associate", "Department Supervisor",
                        "Assistant Hotel Manager", "General Manager"},
                25000, 18, 1, 60);

        add("Oberoi Hotels", "Hospitality and Tourism", "Bengaluru",
                new String[]{"Guest Service Associate", "Hotel Executive",
                        "Department Manager", "General Manager"},
                30000, 18, 2, 68);

        // Retail and Supermarkets

        add("Reliance Retail", "Retail and Supermarkets", "Bengaluru",
                new String[]{"Sales Associate", "Store Supervisor",
                        "Store Manager", "Area Manager"},
                20000, 18, 1, 52);

        add("DMart", "Retail and Supermarkets", "Mysuru",
                new String[]{"Store Associate", "Department Supervisor",
                        "Store Manager", "Regional Manager"},
                19000, 18, 1, 50);

        add("Amazon India", "Retail and E-Commerce", "Bengaluru",
                new String[]{"Operations Associate", "Team Leader",
                        "Operations Manager", "Regional Manager"},
                30000, 18, 2, 68);

        // Aviation and Logistics

        add("IndiGo", "Aviation", "Bengaluru",
                new String[]{"Airport Associate", "Ground Staff Supervisor",
                        "Airport Operations Manager", "Station Manager"},
                26000, 18, 2, 65);

        add("Blue Dart", "Logistics", "Mangaluru",
                new String[]{"Delivery Coordinator", "Operations Executive",
                        "Logistics Manager", "Regional Manager"},
                23000, 18, 1, 55);
    }

    private void add(String name, String sector, String location,
                     String[] positions, int salary,
                     int minimumAge, int education, int difficulty) {
        companies.add(new Company(name, sector, location,
                positions, salary, minimumAge, education, difficulty));
    }

    private String key(String field) {
        return "private_career_" + field;
    }

    private int getInt(String field, int defaultValue) {
        return save.getInt(key(field), defaultValue);
    }

    private void putInt(String field, int value) {
        save.edit().putInt(key(field), value).apply();
    }

    private String getString(String field, String defaultValue) {
        return save.getString(key(field), defaultValue);
    }

    private void putString(String field, String value) {
        save.edit().putString(key(field), value).apply();
    }

    private boolean getBoolean(String field, boolean defaultValue) {
        return save.getBoolean(key(field), defaultValue);
    }

    private void putBoolean(String field, boolean value) {
        save.edit().putBoolean(key(field), value).apply();
    }

    private void notifyUpdate(String description) {
        save.edit()
                .putInt("private_career_money", money)
                .putInt("private_career_health", health)
                .putInt("private_career_happiness", happiness)
                .apply();

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

        addTitle(root, "PRIVATE SECTOR CAREERS");

        addText(root,
                "Build your career in India's leading private companies.");

        String job = getString("job", "");
        String company = getString("company", "");

        addInfoCard(root, "CAREER PROFILE",
                "Age: " + age
                        + "\nCash: ₹" + money
                        + "\nHealth: " + health
                        + "\nHappiness: " + happiness
                        + "\nEmployment: "
                        + (job.isEmpty() ? "Unemployed" : job)
                        + (company.isEmpty() ? "" : "\nCompany: " + company)
                        + "\nExperience: " + getInt("experience", 0) + " years");

        addButton(root, "Browse Private Companies",
                this::showSectors);

        addButton(root, "My Job and Employment Record",
                this::showJobRecord);

        addButton(root, "Promotion History",
                this::showPromotionHistory);
        addButton(root, "Performance Appraisals",
                this::showAppraisalHistory);

        addButton(root, "Career Awards & Recognition",
                this::showAwardHistory);
        addButton(root, "Career Achievements & Milestones",
                this::showAchievements);

        addButton(root, "Workplace and Performance",
                this::showWorkplace);

        addButton(root, "Salary and Bonuses",
                this::showSalary);

        addButton(root, "Company Benefits",
                this::showBenefits);

        addButton(root, "Resign from Job",
                this::resign);

        addButton(root, "Back to Game",
                () -> activity.recreate());

        activity.setContentView(scroll);
    }

    private void showSectors() {
        String[] sectors = {
                "Information Technology",
                "Banking and Finance",
                "Healthcare",
                "Education",
                "Manufacturing and Automobile",
                "Hospitality and Tourism",
                "Retail and Supermarkets",
                "Retail and E-Commerce",
                "Aviation",
                "Logistics"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Choose Career Sector")
                .setItems(sectors, (dialog, which) -> {
                    showCompanies(sectors[which]);
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void showCompanies(String sector) {
        ArrayList<Company> filtered = new ArrayList<>();

        for (Company c : companies) {
            if (c.sector.equals(sector)) {
                filtered.add(c);
            }
        }

        final Company[] choices = filtered.toArray(new Company[0]);
        String[] labels = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {
            labels[i] = choices[i].name
                    + " - ₹" + choices[i].salary + "/month";
        }

        new AlertDialog.Builder(activity)
                .setTitle(sector)
                .setItems(labels, (dialog, which) -> {
                    showCompanyDetails(choices[which]);
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showSectors())
                .show();
    }

    private void showCompanyDetails(Company c) {
        StringBuilder positions = new StringBuilder();

        for (int i = 0; i < c.positions.length; i++) {
            positions.append(i + 1)
                    .append(". ")
                    .append(c.positions[i])
                    .append("\n");
        }

        String details =
                "Company: " + c.name
                        + "\nSector: " + c.sector
                        + "\nLocation: " + c.location
                        + "\nStarting salary: ₹" + c.salary
                        + "\nMinimum age: " + c.minimumAge
                        + "\nQualification: " + qualificationName(c.education)
                        + "\nInterview difficulty: " + c.difficulty + "/100"
                        + "\n\nCareer positions:\n" + positions;

        new AlertDialog.Builder(activity)
                .setTitle(c.name)
                .setMessage(details)
                .setPositiveButton("Apply for Job",
                        (dialog, which) -> applyForJob(c))
                .setNegativeButton("Back",
                        (dialog, which) -> showCompanies(c.sector))
                .show();
    }

    private void applyForJob(Company c) {
        if (!getString("job", "").isEmpty()) {
            message("You are already employed. Resign before applying for another company.");
            return;
        }

        if (age < c.minimumAge || age > 65) {
            message("You do not meet the game's age requirement.");
            return;
        }

        if (getQualificationLevel() < c.education) {
            message("Required qualification: "
                    + qualificationName(c.education));
            return;
        }

        final int interviewChance = Math.min(
                95,
                Math.max(5,
                        65 + getInt("skills", 0) / 3
                                + getInt("experience", 0) * 2
                                - c.difficulty / 3
                                + random.nextInt(21))
        );

        String currentEducation = save.getString("education_level", "");
        if (currentEducation.isEmpty()) {
            currentEducation = save.getString("edu_level", "Not recorded");
        }
        String course = save.getString("edu_course", "");
        if (!course.isEmpty()) {
            currentEducation += " - " + course;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Private Company Job Application")
                .setMessage("Company: " + c.name
                        + "\nPosition: " + c.positions[0]
                        + "\nStarting Salary: ₹" + c.salary
                        + "\n\nEDUCATION REQUIREMENTS"
                        + "\nRequired Qualification: " + qualificationName(c.education)
                        + "\nYour Qualification: " + currentEducation
                        + "\n\nAGE REQUIREMENTS"
                        + "\nRequired Age: " + c.minimumAge + "–65 years"
                        + "\nYour Age: " + age
                        + "\n\nEligibility: QUALIFIED"
                        + "\nInterview Success Chance: " + interviewChance + "%"
                        + "\n\nAttend the interview?")
                .setPositiveButton("Attend Interview",
                        (dialog, which) -> {

                            if (random.nextInt(100) < interviewChance) {
                                setJob(c);

                                notifyUpdate("Congratulations! You joined "
                                        + c.name + " as "
                                        + c.positions[0] + ".");

                                showJobRecord();

                            } else {
                                happiness = Math.max(0, happiness - 5);

                                notifyUpdate("Your interview at "
                                        + c.name + " was unsuccessful.");

                                showCompanyDetails(c);
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setJob(Company c) {
        boolean firstCareerJob = !getBoolean("first_job_earned", false);
        putString("company", c.name);
        putString("sector", c.sector);
        putString("location", c.location);
        putString("job", c.positions[0]);
        putInt("salary", c.salary);
        putInt("level", 0);
        putInt("experience", 0);
        putInt("performance", 50);
        putInt("skills", getInt("skills", 0));
        putInt("appraisals", 0);
        putInt("bonuses", 0);
        putString("status", "Active");
        if (firstCareerJob) { putBoolean("first_job_earned", true); unlockAchievement("first_job", "First Job"); }

        putBoolean("health_benefit", false);
        putBoolean("insurance_benefit", false);
        putBoolean("transport_benefit", false);
        putBoolean("food_benefit", false);
    }

    private Company findCompany(String name) {
        for (Company c : companies) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        return null;
    }

    private void showJobRecord() {
        String job = getString("job", "");

        if (job.isEmpty()) {
            message("You are currently unemployed.");
            return;
        }

        String details =
                "Company: " + getString("company", "")
                        + "\nSector: " + getString("sector", "")
                        + "\nPosition: " + job
                        + "\nLocation: " + getString("location", "")
                        + "\nMonthly salary: ₹" + getInt("salary", 0)
                        + "\nExperience: " + getInt("experience", 0) + " years"
                        + "\nPromotion level: " + getInt("level", 0)
                        + "\nPerformance: " + getInt("performance", 50) + "%"
                        + "\nAppraisals: " + getInt("appraisals", 0)
                        + "\nBonuses received: ₹" + getInt("bonuses", 0)
                        + "\nStatus: " + getString("status", "Active");

        new AlertDialog.Builder(activity)
                .setTitle("Employment Record")
                .setMessage(details)
                .setPositiveButton("Promotion",
                        (dialog, which) -> requestPromotion())
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void showWorkplace() {
        if (getString("job", "").isEmpty()) {
            message("Get a job first to access workplace activities.");
            return;
        }

        if (!getString("status", "Active").equals("Active")) {
            message("Your employment is not active.");
            return;
        }

        String[] options = {
                "Complete Work Assignment",
                "Attend Skill Training",
                "Request Performance Appraisal",
                "View Performance"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Workplace")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        completeAssignment();
                    } else if (which == 1) {
                        skillTraining();
                    } else if (which == 2) {
                        performanceAppraisal();
                    } else {
                        message("Work performance: "
                                + getInt("performance", 50) + "%"
                                + "\nSkills: " + getInt("skills", 0)
                                + "\nExperience: "
                                + getInt("experience", 0) + " years");
                    }
                })
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void completeAssignment() {
        int performance = getInt("performance", 50);
        int chance = 40 + performance / 2;

        if (random.nextInt(100) < chance) {
            performance = Math.min(100, performance + 7);

            putInt("performance", performance);

            happiness = Math.min(100, happiness + 2);

            notifyUpdate("Work assignment completed successfully. Performance: "
                    + performance + "%.");

        } else {
            performance = Math.max(0, performance - 5);

            putInt("performance", performance);

            happiness = Math.max(0, happiness - 3);

            health = Math.max(0, health - 1);

            notifyUpdate("Work assignment was unsuccessful. Improve your performance.");
        }

        showWorkplace();
    }

    private void skillTraining() {
        int cost = 2000;

        if (money < cost) {
            message("You need ₹" + cost + " for professional skill training.");
            return;
        }

        money -= cost;

        int skills = Math.min(100, getInt("skills", 0) + 15);
        putInt("skills", skills);

        notifyUpdate("Professional training completed. Your skill level is now "
                + skills + ".");

        showWorkplace();
    }

    private void performanceAppraisal() {
        int performance = getInt("performance", 50);
        int salary = getInt("salary", 0);

        if (performance < 60) {
            message("You need at least 60% performance to qualify for an appraisal.");
            return;
        }

        int bonus = Math.max(1000, salary / 4);

        money = (int) Math.min(
                Integer.MAX_VALUE,
                (long) money + bonus
        );

        int newSalary = (int) Math.min(
                Integer.MAX_VALUE,
                (long) salary + Math.max(1000, salary / 10)
        );

        putInt("salary", newSalary);
        putInt("appraisals", getInt("appraisals", 0) + 1);
        putInt("bonuses", (int) Math.min(
                Integer.MAX_VALUE,
                (long) getInt("bonuses", 0) + bonus
        ));

        notifyUpdate("Performance appraisal successful! Bonus: ₹"
                + bonus + ". New monthly salary: ₹" + newSalary + ".");

        showJobRecord();
    }

    private void requestPromotion() {
        String job = getString("job", "");

        if (job.isEmpty()
                || !getString("status", "Active").equals("Active")) {
            message("You need active employment to request promotion.");
            return;
        }

        int level = getInt("level", 0);
        int performance = getInt("performance", 50);
        Company company = findCompany(getString("company", ""));

        if (company == null || level + 1 >= company.positions.length) {
            message("You have reached the highest position in this company career path.");
            return;
        }

        int experience = getInt("experience", 0);
        int yearsInGrade = getInt("years_in_grade", experience);
        int requiredYears = 1 + (level * 2);
        int requiredPerformance = 65 + (level * 5);

        if (yearsInGrade < requiredYears) {
            message("Promotion requirement not met.\n\n"
                    + "Current position: " + job
                    + "\nNext position: " + company.positions[level + 1]
                    + "\nRequired experience: " + requiredYears + " years"
                    + "\nYour experience: " + experience + " years.");
            return;
        }

        if (performance < requiredPerformance) {
            message("Promotion requirement not met.\n\n"
                    + "Current position: " + job
                    + "\nNext position: " + company.positions[level + 1]
                    + "\nRequired performance: " + requiredPerformance + "%"
                    + "\nYour performance: " + performance + "%.");
            return;
        }

        final int chance = Math.min(90, 30 + performance / 2 + experience);
        final int nextLevel = level + 1;

        new AlertDialog.Builder(activity)
                .setTitle("Private Career Promotion")
                .setMessage("Company: " + getString("company", "")
                        + "\nCurrent Grade: " + job
                        + "\nNext Grade: " + company.positions[nextLevel]
                        + "\n\nEligibility: QUALIFIED"
                        + "\nRequired Experience: " + requiredYears + " years"
                        + "\nYour Experience: " + experience + " years"
                        + "\nRequired Performance: " + requiredPerformance + "%"
                        + "\nYour Performance: " + performance + "%"
                        + "\n\nEstimated Promotion Chance: " + chance + "%"
                        + "\n\nSubmit promotion request?")
                .setPositiveButton("Request", (dialog, which) -> {
                    if (random.nextInt(100) < chance) {
                        String previousGrade = getString("job", "");
                        putInt("level", nextLevel);
                        putString("job", company.positions[nextLevel]);
                        unlockAchievement("first_promotion", "First Promotion");
                        putInt("years_in_grade", 0);

                        int salary = getInt("salary", 0);
                        int updatedSalary = (int) Math.min(
                                Integer.MAX_VALUE,
                                (long) salary + Math.max(3000, salary / 5)
                        );
                        putInt("salary", updatedSalary);
                        String history = getString("promotion_history", "");
                        String entry = "Age " + age + ": " + previousGrade
                                + " → " + company.positions[nextLevel]
                                + " | Salary ₹" + updatedSalary;
                        putString("promotion_history",
                                history.isEmpty() ? entry : history + "\\n" + entry);

                        notifyUpdate("Congratulations! You were promoted to "
                                + company.positions[nextLevel]
                                + ". Salary: ₹" + updatedSalary);
                    } else {
                        happiness = Math.max(0, happiness - 3);
                        notifyUpdate("Your promotion request was not approved.");
                    }
                    showJobRecord();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPromotionHistory() {
        String history = getString("promotion_history", "");
        if (history.isEmpty()) {
            history = "No promotions recorded yet. Successful promotions will appear here.";
        }
        new AlertDialog.Builder(activity)
                .setTitle("Private Career Promotion History")
                .setMessage(history)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSalary() {
        if (getString("job", "").isEmpty()) {
            message("You do not have a private-sector job.");
            return;
        }

        int salary = getInt("salary", 0);
        int lastPaidAge = getInt("salary_paid_age", -1);
        String paymentStatus = lastPaidAge == age
                ? "Already credited for game age " + age
                : "Credited automatically when you advance the game year";

        new AlertDialog.Builder(activity)
                .setTitle("Salary and Bonuses")
                .setMessage("Company: " + getString("company", "")
                        + "\nMonthly salary: ₹" + salary
                        + "\nEstimated annual salary: ₹" + ((long) salary * 12)
                        + "\nSalary payment: " + paymentStatus
                        + "\nTotal bonuses: ₹" + getInt("bonuses", 0)
                        + "\nAppraisals: " + getInt("appraisals", 0)
                        + "\n\nYour annual salary is credited automatically once per game year.")
                .setPositiveButton("OK", null)
                .setNegativeButton("Back",
                        (dialog, which) -> showDashboard())
                .show();
    }

    private void showBenefits() {
        if (getString("job", "").isEmpty()) {
            message("You need a job to receive employee benefits.");
            return;
        }

        String[] options = {
                "Health Insurance",
                "Medical Coverage",
                "Transport Allowance",
                "Food Allowance",
                "View Benefits"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Employee Benefits")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        activateBenefit("insurance_benefit",
                                "Health insurance");
                    } else if (which == 1) {
                        activateBenefit("health_benefit",
                                "Medical coverage");
                    } else if (which == 2) {
                        activateBenefit("transport_benefit",
                                "Transport allowance");
                    } else if (which == 3) {
                        activateBenefit("food_benefit",
                                "Food allowance");
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

        putBoolean(field, true);

        notifyUpdate(name + " activated successfully.");

        showBenefits();
    }

    private void showBenefitSummary() {
        String details =
                "Health insurance: "
                        + yesNo(getBoolean("insurance_benefit", false))
                        + "\nMedical coverage: "
                        + yesNo(getBoolean("health_benefit", false))
                        + "\nTransport allowance: "
                        + yesNo(getBoolean("transport_benefit", false))
                        + "\nFood allowance: "
                        + yesNo(getBoolean("food_benefit", false));

        new AlertDialog.Builder(activity)
                .setTitle("My Employee Benefits")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void resign() {
        String job = getString("job", "");

        if (job.isEmpty()) {
            message("You do not have a job to resign from.");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Resignation")
                .setMessage("Are you sure you want to resign from "
                        + getString("company", "") + " as " + job + "?")
                .setPositiveButton("Resign",
                        (dialog, which) -> {
                            putString("last_company",
                                    getString("company", ""));
                            putString("last_job", job);
                            putString("job", "");
                            putString("company", "");
                            putString("sector", "");
                            putString("status", "Resigned");

                            notifyUpdate("You resigned from your job as "
                                    + job + ".");

                            showDashboard();
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void recordAnnualAppraisal(int score, int salary, String role) {
        String rating;
        int rewardRate;
        if (score >= 90) {
            rating = "Excellent";
            rewardRate = 25;
        } else if (score >= 75) {
            rating = "Good";
            rewardRate = 10;
        } else if (score >= 55) {
            rating = "Satisfactory";
            rewardRate = 0;
        } else if (score >= 35) {
            rating = "Needs Improvement";
            rewardRate = 0;
        } else {
            rating = "Unsatisfactory";
            rewardRate = 0;
        }

        int reward = (int) Math.min(Integer.MAX_VALUE,
                (long) Math.max(0, salary) * rewardRate / 100);
        if (reward > 0) {
            money = (int) Math.min(Integer.MAX_VALUE,
                    (long) money + reward);
        }

        int streak = score >= 75 ? getInt("high_rating_streak", 0) + 1 : 0;
        putInt("high_rating_streak", streak);
        String award = "";
        if (streak >= 3) {
            int awardBonus = (int) Math.min(Integer.MAX_VALUE,
                    (long) Math.max(0, salary) * 2L);
            money = (int) Math.min(Integer.MAX_VALUE,
                    (long) money + awardBonus);
            int awards = getInt("awards_count", 0) + 1;
            String awardEntry = "Age " + age
                    + " — Consistent Excellence Award (3+ strong annual reviews)"
                    + ", bonus ₹" + awardBonus;
            String awardHistory = getString("award_history", "");
            awardHistory = awardEntry + (awardHistory.isEmpty()
                    ? "" : "\\n\\n" + awardHistory);
            if (awardHistory.length() > 5000) awardHistory = awardHistory.substring(0, 5000);
            putInt("awards_count", awards);
            putString("award_history", awardHistory);
            putInt("high_rating_streak", 0);
            award = ", Consistent Excellence Award earned (₹" + awardBonus + ")";
        }

        String entry = "Age " + age + " — " + rating + " (" + score
                + "/100), " + role
                + (reward > 0 ? ", reward ₹" + reward : "")
                + (score < 55 ? ", performance warning issued" : "") + award;
        String history = getString("appraisal_history", "");
        history = entry + (history.isEmpty() ? "" : "\\n\\n" + history);
        if (history.length() > 5000) history = history.substring(0, 5000);
        putString("appraisal_history", history);
        putString("last_appraisal", rating);
        putInt("last_appraisal_score", score);
        saveAll();
    }

    private void unlockAchievement(String id, String title) {
        String flag = "achievement_" + id;
        if (getBoolean(flag, false)) return;
        String history = getString("achievement_history", "");
        String entry = "Age " + age + " — " + title;
        putBoolean(flag, true);
        putInt("achievement_count", getInt("achievement_count", 0) + 1);
        putString("achievement_history", history.isEmpty()
                ? entry : history + "\\n" + entry);
        message("Achievement Unlocked: " + title);
    }

    private void showAchievements() {
        String history = getString("achievement_history", "");
        if (history.isEmpty()) history = "No achievements unlocked yet. Start your career, earn promotions, and build long service.";
        message("CAREER ACHIEVEMENTS & MILESTONES\\n\\nUnlocked: "
                + getInt("achievement_count", 0) + "\\n\\n" + history);
    }

    private void showAwardHistory() {
        String history = getString("award_history", "");
        if (history.isEmpty()) history = "No career awards earned yet. Receive Good or Excellent ratings for three consecutive annual reviews to earn the Consistent Excellence Award.";
        message("CAREER AWARDS & RECOGNITION\\n\\nAwards earned: "
                + getInt("awards_count", 0) + "\\n\\n" + history);
    }

    private void showAppraisalHistory() {
        String history = getString("appraisal_history", "");
        if (history.isEmpty()) {
            history = "No performance appraisals recorded yet. Your first review will appear after an annual career update.";
        }
        message("PERFORMANCE APPRAISALS\\n\\n" + history);
    }

    public void applyAnnualUpdate() {
        String job = getString("job", "");

        if (job.isEmpty()
                || !getString("status", "Active").equals("Active")) {
            return;
        }

        // Prevent duplicate salary payments and duplicate annual progression
        // if MainActivity calls this method more than once in the same game year.
        int lastPaidAge = getInt("salary_paid_age", -1);
        if (lastPaidAge == age) {
            return;
        }

        int monthlySalary = Math.max(0, getInt("salary", 0));
        long annualSalary = (long) monthlySalary * 12L;

        long updatedMoney = (long) money + annualSalary;
        money = (int) Math.min(Integer.MAX_VALUE,
                Math.max(0L, updatedMoney));

        // Record this game age before notifying the main game.
        putInt("salary_paid_age", age);

        putInt("experience", getInt("experience", 0) + 1)
            int careerYears = getInt("experience", 0);
            if (careerYears >= 5) unlockAchievement("service_5", "5-Year Service Milestone");
            if (careerYears >= 10) unlockAchievement("service_10", "10-Year Service Milestone");;
        putInt("years_in_grade", getInt("years_in_grade",
                getInt("experience", 0)) + 1);

        int increment = Math.max(500, monthlySalary / 20);
        long updatedSalary = (long) monthlySalary + increment;
        int newMonthlySalary = (int) Math.min(
                Integer.MAX_VALUE, updatedSalary
        );
        putInt("salary", newMonthlySalary);

        int performance = getInt("performance", 50);
        recordAnnualAppraisal(performance, monthlySalary, job);
        putInt("performance", Math.min(100, performance + 2));

        if (health > 0 && random.nextInt(100) < 10) {
            health = Math.max(0, health - 1);
        }

        notifyUpdate("Annual salary credited: ₹" + annualSalary
                + " (12 months). Your experience increased by one year."
                + " New monthly salary: ₹" + newMonthlySalary + ".");
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

    private void saveAll() {
        save.edit()
                .putInt("private_career_money", money)
                .putInt("private_career_health", health)
                .putInt("private_career_happiness", happiness)
                .apply();
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
