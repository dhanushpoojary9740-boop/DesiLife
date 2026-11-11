package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class CareerManager {

    public interface MoneyListener {
        void onMoneyChanged(long amount);
    }

    private final Activity activity;
    private final SharedPreferences prefs;
    private final MoneyListener listener;
    private final NumberFormat currency =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final Random random = new Random();

    private long cash;
    private long monthlySalary;

    private String jobTitle = "";
    private String sector = "";
    private String qualification = "";

    private int age;
    private int intelligence;
    private int experience;
    private int performance = 75;
    private int lastSalaryAge;

    private boolean employed;
    private boolean retired;

    private final int bg = Color.rgb(12, 25, 23);
    private final int gold = Color.rgb(226, 184, 105);
    private final int white = Color.WHITE;
    private final int blue = Color.rgb(55, 91, 145);

    private static class Job {
        String title;
        String sector;
        String qualification;
        long salary;
        int minimumIntelligence;

        Job(String title, String sector, String qualification,
            long salary, int minimumIntelligence) {
            this.title = title;
            this.sector = sector;
            this.qualification = qualification;
            this.salary = salary;
            this.minimumIntelligence = minimumIntelligence;
        }
    }

    private final Job[] jobs = {
        new Job("Delivery Partner", "Part-time", "None", 12000, 20),
        new Job("Retail Sales Executive", "Private", "SSLC", 16000, 25),
        new Job("Office Assistant", "Private", "PUC", 18000, 30),
        new Job("Customer Support Executive", "Private", "PUC", 22000, 35),
        new Job("Data Entry Operator", "Private", "PUC", 20000, 40),
        new Job("School Teacher", "Private", "Degree", 30000, 50),
        new Job("Accountant", "Private", "Degree", 35000, 55),
        new Job("Software Developer", "Private", "Degree", 55000, 65),
        new Job("Bank Officer", "Private", "Degree", 45000, 60),
        new Job("Government Clerk", "Government", "PUC", 28000, 45),
        new Job("Railway Employee", "Government", "PUC", 32000, 45),
        new Job("Police Sub-Inspector", "Government", "Degree", 42000, 60),
        new Job("KAS Officer", "Government", "Degree", 65000, 75),
        new Job("IAS Officer", "Government", "Degree", 80000, 85),
        new Job("Doctor", "Professional", "Medical Degree", 75000, 75),
        new Job("Lawyer", "Professional", "Law Degree", 50000, 65)
    };

    public CareerManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int intelligence,
            String educationStatus,
            boolean degreeCompleted,
            long currentCash,
            MoneyListener listener) {

        this.activity = activity;
        this.age = age;
        this.intelligence = intelligence;
        this.qualification = educationStatus;
        this.cash = currentCash;
        this.listener = listener;

        prefs = activity.getSharedPreferences(
                "desilife_career_data",
                Activity.MODE_PRIVATE);

        loadData();
    }

    private void loadData() {
        jobTitle = prefs.getString("job_title", "");
        sector = prefs.getString("sector", "");

        monthlySalary = prefs.getLong("salary", 0);
        experience = prefs.getInt("experience", 0);
        performance = prefs.getInt("performance", 75);
        lastSalaryAge = prefs.getInt("last_salary_age", age);

        employed = prefs.getBoolean("employed", false);
        retired = prefs.getBoolean("retired", false);
    }

    private void saveData() {
        prefs.edit()
                .putString("job_title", jobTitle)
                .putString("sector", sector)
                .putLong("salary", monthlySalary)
                .putInt("experience", experience)
                .putInt("performance", performance)
                .putInt("last_salary_age", lastSalaryAge)
                .putBoolean("employed", employed)
                .putBoolean("retired", retired)
                .apply();
    }

    private void updateCash(long amount) {
        cash = Math.max(0, amount);

        if (listener != null) {
            listener.onMoneyChanged(cash);
        }
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(10, 10, 10, 10);
        return t;
    }

    private void button(LinearLayout layout, String label,
                        Runnable action) {
        TextView b = text(label, 15, white);
        b.setBackground(shape(blue, 18));
        b.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 7);

        layout.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    private String money(long amount) {
        return currency.format(amount);
    }

    public void open() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(18, 15, 18, 15);
        layout.setBackgroundColor(bg);

        TextView heading = text("💼 JOBS & CAREER", 23, gold);
        layout.addView(heading);

        layout.addView(text(
                "Build your career and financial future.",
                14, white));

        layout.addView(text(
                "Available Cash: " + money(cash),
                17, gold));

        layout.addView(text(
                "Employment Status: " +
                (retired ? "Retired" :
                employed ? "Employed" : "Unemployed"),
                16, white));

        layout.addView(text(
                "Current Job: " +
                (employed ? jobTitle : "No job"),
                16, white));

        layout.addView(text(
                "Monthly Salary: " + money(monthlySalary),
                16, white));

        layout.addView(text(
                "Work Experience: " + experience + " years",
                15, white));

        layout.addView(text(
                "Work Performance: " + performance + "/100",
                15, white));

        if (employed) {
            button(layout, "📈 VIEW CAREER DETAILS",
                    this::careerDetails);

            button(layout, "🔄 CHANGE JOB",
                    this::showJobs);

            button(layout, "🚪 RESIGN FROM JOB",
                    this::resign);
        } else if (!retired) {
            button(layout, "🔎 FIND A JOB",
                    this::showJobs);
        }

        if (age >= 18 && !retired) {
            button(layout, "💻 FREELANCE PROJECT",
                    this::freelance);
        }

        if (age >= 58 && !retired) {
            button(layout, "🏖 RETIRE",
                    this::retire);
        }

        button(layout, "CLOSE", () -> open());

        ScrollView scroll = new ScrollView(activity);
        scroll.addView(layout);

        new AlertDialog.Builder(activity)
                .setView(scroll)
                .setPositiveButton("DONE", null)
                .show();
    }

    private boolean eligible(Job job) {
        if (age < 18) return false;

        String q = qualification.toLowerCase();

        if (job.qualification.equals("None")) return true;

        if (job.qualification.equals("SSLC")) {
            return q.contains("sslc") ||
                    q.contains("puc") ||
                    q.contains("graduated") ||
                    q.contains("degree");
        }

        if (job.qualification.equals("PUC")) {
            return q.contains("puc") ||
                    q.contains("graduated") ||
                    q.contains("degree");
        }

        if (job.qualification.equals("Degree")) {
            return q.contains("graduated");
        }

        if (job.qualification.equals("Medical Degree")) {
            return q.contains("mbbs") ||
                    q.contains("medical");
        }

        if (job.qualification.equals("Law Degree")) {
            return q.contains("llb") ||
                    q.contains("law");
        }

        return false;
    }

    private void showJobs() {
        if (age < 18) {
            message("You must be at least 18 years old to apply for these jobs.");
            return;
        }

        String[] names = new String[jobs.length];

        for (int i = 0; i < jobs.length; i++) {
            Job j = jobs[i];
            names[i] = j.title + " | " +
                    money(j.salary) + "/month | " +
                    j.qualification;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Available Job Vacancies")
                .setItems(names, (dialog, which) -> {
                    applyForJob(jobs[which]);
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void applyForJob(Job job) {
        if (!eligible(job)) {
            message("You don't have the required qualification for this job. Complete your education first.");
            return;
        }

        if (intelligence < job.minimumIntelligence) {
            message("Your intelligence is not high enough for this position. Improve your skills and try again.");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Job Application")
                .setMessage(
                        "Position: " + job.title +
                        "\nSector: " + job.sector +
                        "\nMonthly Salary: " + money(job.salary) +
                        "\n\nYou must pass an interview to get this job.")
                .setPositiveButton("ATTEND INTERVIEW",
                        (d, w) -> interview(job))
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void interview(Job job) {
        int score = random.nextInt(61) +
                (intelligence / 3) +
                (performance / 10);

        if (score >= 45) {
            employed = true;
            retired = false;

            jobTitle = job.title;
            sector = job.sector;
            monthlySalary = job.salary;

            lastSalaryAge = age;
            performance = Math.min(100, performance + 5);

            saveData();

            message(
                    "🎉 Congratulations!\n\n" +
                    "You got the job as " + jobTitle +
                    ".\n\nMonthly Salary: " +
                    money(monthlySalary) +
                    "\n\nYour salary will be credited when you complete a game year."
            );
        } else {
            performance = Math.max(0, performance - 2);
            saveData();

            message(
                    "Unfortunately, you were not selected.\n\n" +
                    "Improve your skills and try another job."
            );
        }
    }

    public void applyAnnualSalary() {
        if (!employed || retired || monthlySalary <= 0) return;
        if (age <= lastSalaryAge) return;

        long annualGross = monthlySalary * 12L;

        long tax = annualGross <= 300000 ? 0 :
                annualGross <= 700000 ?
                (annualGross - 300000) * 5 / 100 :
                (400000 * 5 / 100) +
                (annualGross - 700000) * 10 / 100;

        long netSalary = Math.max(0, annualGross - tax);

        updateCash(cash + netSalary);

        experience++;
        performance = Math.min(100,
                performance + random.nextInt(5));

        String promotion = "";

        if (experience % 3 == 0) {
            monthlySalary = monthlySalary * 110 / 100;
            promotion = "\n\n🎉 Promotion! Your salary increased by 10%.";
        }

        lastSalaryAge = age;
        saveData();

        message(
                "💰 Annual Salary Credited!\n\n" +
                "Job: " + jobTitle +
                "\nGross Salary: " + money(annualGross) +
                "\nIncome Tax: " + money(tax) +
                "\nNet Salary: " + money(netSalary) +
                "\n\nExperience: " + experience + " years" +
                promotion
        );
    }

    private void freelance() {
        if (age < 18 || retired) return;

        long earnings = 2000 + random.nextInt(13001);

        new AlertDialog.Builder(activity)
                .setTitle("Freelance Project")
                .setMessage(
                        "Complete a freelance project?\n\n" +
                        "Possible earnings: ₹2,000 – ₹15,000"
                )
                .setPositiveButton("COMPLETE PROJECT", (d, w) -> {
                    int chance = 40 + intelligence / 2;
                    boolean success = random.nextInt(100) < chance;

                    if (success) {
                        updateCash(cash + earnings);
                        message(
                                "Project completed successfully!\n\n" +
                                "You earned " + money(earnings) + "."
                        );
                    } else {
                        message(
                                "The client rejected your work.\n\n" +
                                "You earned nothing. Improve your skills."
                        );
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void careerDetails() {
        message(
                "CAREER DETAILS\n\n" +
                "Job: " + jobTitle +
                "\nSector: " + sector +
                "\nMonthly Salary: " + money(monthlySalary) +
                "\nAnnual Salary: " + money(monthlySalary * 12) +
                "\nExperience: " + experience + " years" +
                "\nPerformance: " + performance + "/100"
        );
    }

    private void resign() {
        new AlertDialog.Builder(activity)
                .setTitle("Resign from Job?")
                .setMessage(
                        "You will stop receiving your salary. " +
                        "Your previous experience will be preserved."
                )
                .setPositiveButton("RESIGN", (d, w) -> {
                    employed = false;
                    jobTitle = "";
                    sector = "";
                    monthlySalary = 0;

                    saveData();
                    message("You have resigned from your job.");
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void retire() {
        new AlertDialog.Builder(activity)
                .setTitle("Retirement")
                .setMessage(
                        "Are you ready to retire from your career?"
                )
                .setPositiveButton("RETIRE", (d, w) -> {
                    employed = false;
                    retired = true;
                    monthlySalary = 0;
                    jobTitle = "";

                    saveData();
                    message("You have retired. Enjoy your retirement!");
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void message(String value) {
        new AlertDialog.Builder(activity)
                .setTitle("DesiLife Career")
                .setMessage(value)
                .setPositiveButton("OK", null)
                .show();
    }
}
