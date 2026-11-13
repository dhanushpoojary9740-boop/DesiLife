package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;

import java.util.Locale;

public class CareerManager {

    public interface MoneyCallback {
        void onMoneyChanged(long newMoney);
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final SharedPreferences career;
    private final MoneyCallback callback;

    private final int age;
    private final int intelligence;
    private final String education;
    private final boolean degreeCompleted;

    private long money;

    private String jobTitle;
    private String company;
    private String sector;
    private String businessName;

    private long monthlySalary;
    private long businessProfit;

    private int experience;
    private int promotions;
    private int lastPaidAge;

    private boolean employed;
    private boolean businessActive;
    private boolean retired;

    private final int dark = Color.rgb(12,25,23);
    private final int gold = Color.rgb(226,184,105);
    private final int white = Color.WHITE;

    public CareerManager(
            Activity activity,
            SharedPreferences save,
            int age,
            int intelligence,
            String education,
            boolean degreeCompleted,
            int money,
            MoneyCallback callback) {

        this.activity = activity;
        this.save = save;
        this.age = age;
        this.intelligence = intelligence;
        this.education = education == null ? "" : education;
        this.degreeCompleted = degreeCompleted;
        this.money = Math.max(0,money);
        this.callback = callback;

        career = activity.getSharedPreferences(
                "desilife_career_data",
                Activity.MODE_PRIVATE
        );

        loadCareer();
    }

    private void loadCareer() {
        jobTitle = career.getString("jobTitle","");
        company = career.getString("company","");
        sector = career.getString("sector","");
        businessName = career.getString("businessName","");

        monthlySalary = career.getLong("monthlySalary",0);
        businessProfit = career.getLong("businessProfit",0);

        experience = career.getInt("experience",0);
        promotions = career.getInt("promotions",0);
        lastPaidAge = career.getInt("lastPaidAge",-1);

        employed = career.getBoolean("employed",false);
        businessActive = career.getBoolean("businessActive",false);
        retired = career.getBoolean("retired",false);
    }

    private void saveCareer() {
        career.edit()
                .putString("jobTitle",jobTitle)
                .putString("company",company)
                .putString("sector",sector)
                .putString("businessName",businessName)
                .putLong("monthlySalary",monthlySalary)
                .putLong("businessProfit",businessProfit)
                .putInt("experience",experience)
                .putInt("promotions",promotions)
                .putInt("lastPaidAge",lastPaidAge)
                .putBoolean("employed",employed)
                .putBoolean("businessActive",businessActive)
                .putBoolean("retired",retired)
                .apply();
    }

    private GradientDrawable shape(int color,int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String value,int size,int color) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(12,10,12,10);
        return t;
    }

    private void button(
            LinearLayout root,
            String label,
            Runnable action) {

        TextView b = text(label,16,dark);
        b.setGravity(Gravity.CENTER);
        b.setTypeface(null,Typeface.BOLD);
        b.setBackground(shape(Color.rgb(239,202,126),18));
        b.setElevation(3);
        b.setPadding(14,15,14,15);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,7,0,7);
        root.addView(b,p);

        b.setOnClickListener(v -> action.run());
    }

    private void showMessage(String title,String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("CONTINUE",null)
                .show();
    }

    private String rupees(long amount) {
        return "₹"+String.format(
                Locale.US,"%,d",amount
        );
    }

    public void open() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18,15,18,20);
        root.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.addView(root);

        TextView heading = text(
                "💼 CAREER & PROFESSIONAL LIFE",
                23,gold
        );
        heading.setTypeface(null,Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        root.addView(heading);

        addDivider(root);

        if (age < 18) {
            addInfo(root,"Age Requirement",
                    "Career applications open at age 18.");
            showScreen(scroll);
            return;
        }

        addInfo(root,"Current Status",
                retired ? "Retired" :
                employed ? "Employed" :
                businessActive ? "Self Employed" :
                "Unemployed");

        addInfo(root,"Current Job",
                employed ? jobTitle : "No active job");

        addInfo(root,"Company",
                employed ? company : "Not applicable");

        addInfo(root,"Monthly Salary",rupees(monthlySalary));

        addInfo(root,"Work Experience",experience+" years");

        addInfo(root,"Promotions",String.valueOf(promotions));

        addInfo(root,"Available Cash",rupees(money));

        addDivider(root);

        if (retired) {
            button(root,"RETIREMENT INFORMATION",
                    this::showRetirement);

        } else if (employed) {
            button(root,"🏆 CAREER DEVELOPMENT",
                    this::showCareerDevelopment);

            button(root,"💼 CHANGE JOB",this::showJobs);

            button(root,"RESIGN FROM JOB",this::resign);

        } else if (businessActive) {
            button(root,"🏢 MANAGE BUSINESS",this::manageBusiness);

            button(root,"APPLY FOR A JOB",this::showJobs);

        } else {
            button(root,"🔎 FIND A JOB",this::showJobs);

            button(root,"🏢 START YOUR BUSINESS",this::startBusiness);

            button(root,"💻 FREELANCING",this::freelancing);
        }

        button(root,"🏛️ GOVERNMENT JOBS",this::showGovernmentJobs);

        button(root,"📊 CAREER STATISTICS",this::showStatistics);

        button(root,"🏖️ RETIREMENT",this::retire);

        showScreen(scroll);
    }

    private void showScreen(ScrollView scroll) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(scroll)
                .setNegativeButton("CLOSE",null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    shape(Color.WHITE,18)
            );

            dialog.getWindow().setLayout(
                    (int)(activity.getResources()
                            .getDisplayMetrics().widthPixels*0.94),
                    -2
            );
        }
    }

    private void addDivider(LinearLayout root) {
        android.view.View line = new android.view.View(activity);
        line.setBackgroundColor(gold);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1,2);

        p.setMargins(0,12,0,12);
        root.addView(line,p);
    }

    private void addInfo(
            LinearLayout root,String title,String value) {

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(12,8,12,8);
        card.setBackground(shape(Color.rgb(28,58,50),15));

        card.addView(text(title,13,Color.LTGRAY));
        card.addView(text(value,20,white));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,5,0,5);
        root.addView(card,p);
    }

    private void showJobs() {

        if (retired || age < 18) {
            showMessage("Career","You are not eligible to apply.");
            return;
        }

        String[] jobs = {
                "Delivery Partner",
                "Retail Associate",
                "Office Assistant",
                "Customer Support Executive",
                "Sales Executive",
                "Bank Clerk",
                "Accountant",
                "School Teacher",
                "Software Developer",
                "Government Officer",
                "Police Constable",
                "Business Analyst"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Available Job Opportunities")
                .setItems(jobs,(d,which) -> applyForJob(jobs[which]))
                .setNegativeButton("CANCEL",null)
                .show();
    }

    private void applyForJob(String job) {

        if (employed) {
            showMessage("Already Employed",
                    "Resign from your current job before accepting another job.");
            return;
        }

        if (retired) return;

        long salary;
        String category;
        String employer;

        switch(job) {
            case "Delivery Partner":
                salary=18000;
                category="Private";
                employer="Delivery Services";
                break;
            case "Retail Associate":
                salary=16000;
                category="Private";
                employer="Retail Company";
                break;
            case "Office Assistant":
                salary=20000;
                category="Private";
                employer="Corporate Office";
                break;
            case "Customer Support Executive":
                salary=24000;
                category="Private";
                employer="Customer Services";
                break;
            case "Sales Executive":
                salary=25000;
                category="Private";
                employer="Sales Corporation";
                break;
            case "Bank Clerk":
                salary=32000;
                category="Banking";
                employer="Indian Banking Sector";
                break;
            case "Accountant":
                salary=35000;
                category="Finance";
                employer="Finance Company";
                break;
            case "School Teacher":
                salary=30000;
                category="Education";
                employer="Private School";
                break;
            case "Software Developer":
                salary=55000;
                category="IT";
                employer="Technology Company";
                break;
            case "Government Officer":
                salary=48000;
                category="Government";
                employer="Government Department";
                break;
            case "Police Constable":
                salary=30000;
                category="Government";
                employer="State Police Department";
                break;
            default:
                salary=45000;
                category="Corporate";
                employer="Business Corporation";
                break;
        }

        if (!checkEligibility(job)) {
            showMessage("Qualification Required",
                    "Your current education or qualification does not meet the requirements for this job.");
            return;
        }

        final long offeredSalary=salary;
        final String offeredCategory=category;
        final String offeredEmployer=employer;

        final int chance=Math.min(
                95,45+intelligence/3
        );

        final int result=(int)(Math.random()*100);

        new AlertDialog.Builder(activity)
                .setTitle("Job Interview")
                .setMessage(
                        "Position: "+job+
                        "\nCompany: "+employer+
                        "\nExpected monthly salary: "+rupees(salary)+
                        "\n\nAttend the interview?"
                )
                .setNegativeButton("CANCEL",null)
                .setPositiveButton("ATTEND",(d,w) -> {

                    if (result < chance) {

                        employed=true;
                        jobTitle=job;
                        company=offeredEmployer;
                        sector=offeredCategory;
                        monthlySalary=offeredSalary;

                        experience=0;
                        promotions=0;
                        lastPaidAge=age;

                        saveCareer();

                        showMessage("🎉 Congratulations!",
                                "You got the job!\n\n"+
                                "Position: "+jobTitle+
                                "\nCompany: "+company+
                                "\nMonthly salary: "+rupees(monthlySalary)+
                                "\n\nYour salary will be credited when you age up to the next year.");

                    } else {
                        showMessage("Interview Result",
                                "Unfortunately, you were not selected. Improve your intelligence and qualifications and try again.");
                    }
                })
                .show();
    }

    private boolean checkEligibility(String job) {

        String e=education.toLowerCase(Locale.US);

        boolean hasSSLC=e.contains("sslc") ||
                e.contains("puc") ||
                e.contains("graduated");

        boolean hasPUC=e.contains("puc") ||
                e.contains("graduated");

        if (job.equals("Delivery Partner")) return true;

        if (job.equals("Police Constable")) {
            return age<=27 && hasSSLC;
        }

        if (job.equals("Retail Associate")) return hasSSLC;

        if (job.equals("Office Assistant") ||
                job.equals("Customer Support Executive") ||
                job.equals("Sales Executive")) {
            return hasPUC;
        }

        if (job.equals("Bank Clerk") ||
                job.equals("Accountant") ||
                job.equals("School Teacher") ||
                job.equals("Software Developer") ||
                job.equals("Government Officer") ||
                job.equals("Business Analyst")) {
            return degreeCompleted;
        }

        return false;
    }

    private void showGovernmentJobs() {

        String[] options={
                "Government Clerk",
                "Police Constable",
                "Government Officer"
        };

        new AlertDialog.Builder(activity)
                .setTitle("🏛️ Government Careers")
                .setItems(options,(d,which) -> {

                    if (which==0) {
                        if (!degreeCompleted) {
                            showMessage("Qualification",
                                    "A completed degree is required in this simulation.");
                            return;
                        }
                        applyForJob("Government Officer");

                    } else if (which==1) {
                        applyForJob("Police Constable");

                    } else {
                        applyForJob("Government Officer");
                    }
                })
                .setNegativeButton("CLOSE",null)
                .show();
    }

    private void showCareerDevelopment() {

        String info=
                "Current position: "+jobTitle+
                "\nCompany: "+company+
                "\nMonthly salary: "+rupees(monthlySalary)+
                "\nExperience: "+experience+" years"+
                "\nPromotions: "+promotions;

        new AlertDialog.Builder(activity)
                .setTitle("🏆 Career Development")
                .setMessage(info)
                .setPositiveButton("TRAINING",(d,w) -> training())
                .setNegativeButton("CLOSE",null)
                .show();
    }

    private void training() {

        long fee=5000;

        if (money<fee) {
            showMessage("Insufficient Funds",
                    "You need "+rupees(fee)+" for professional training.");
            return;
        }

        money-=fee;

        int improvement=3+(int)(Math.random()*5);
        long bonus=monthlySalary*improvement/100;

        monthlySalary+=bonus;

        callback.onMoneyChanged(money);
        saveCareer();

        showMessage("Training Completed",
                "Your professional skills improved.\n\nMonthly salary increased by "+rupees(bonus)+".");
    }

    private void resign() {

        new AlertDialog.Builder(activity)
                .setTitle("Resign from Job?")
                .setMessage("You will stop receiving your current salary. Your experience will be retained.")
                .setNegativeButton("CANCEL",null)
                .setPositiveButton("RESIGN",(d,w) -> {

                    employed=false;
                    monthlySalary=0;
                    jobTitle="";
                    company="";
                    sector="";
                    lastPaidAge=-1;

                    saveCareer();

                    showMessage("Resignation",
                            "You have resigned successfully. You can now apply for another job.");
                })
                .show();
    }

    private void startBusiness() {

        if (age<18 || retired) return;

        String[] businesses={
                "Small Retail Shop",
                "Restaurant",
                "Clothing Store",
                "Online Business",
                "Technology Startup",
                "Real Estate Agency"
        };

        new AlertDialog.Builder(activity)
                .setTitle("🏢 Start a Business")
                .setItems(businesses,(d,which) ->
                        launchBusiness(businesses[which]))
                .setNegativeButton("CANCEL",null)
                .show();
    }

    private void launchBusiness(String name) {

        long investment;

        switch(name) {
            case "Small Retail Shop":
                investment=100000;
                break;
            case "Restaurant":
                investment=300000;
                break;
            case "Clothing Store":
                investment=200000;
                break;
            case "Online Business":
                investment=50000;
                break;
            case "Technology Startup":
                investment=500000;
                break;
            default:
                investment=1000000;
                break;
        }

        if (money<investment) {
            showMessage("Insufficient Capital",
                    "Required investment: "+rupees(investment)+
                    "\nYour available money: "+rupees(money));
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("Business Investment")
                .setMessage(
                        "Business: "+name+
                        "\nInitial Investment: "+rupees(investment)+
                        "\n\nDo you want to launch this business?"
                )
                .setNegativeButton("CANCEL",null)
                .setPositiveButton("LAUNCH",(d,w) -> {

                    money-=investment;

                    businessName=name;
                    businessActive=true;
                    businessProfit=investment/20;

                    callback.onMoneyChanged(money);
                    saveCareer();

                    showMessage("Business Launched!",
                            "Congratulations!\n\nYour "+name+
                            " is now operational.\nEstimated yearly operating profit: "+
                            rupees(businessProfit));
                })
                .show();
    }

    private void manageBusiness() {

        new AlertDialog.Builder(activity)
                .setTitle("🏢 Business Management")
                .setMessage(
                        "Business: "+businessName+
                        "\nEstimated annual profit: "+rupees(businessProfit)
                )
                .setPositiveButton("EXPAND BUSINESS",
                        (d,w) -> expandBusiness())
                .setNegativeButton("CLOSE",null)
                .show();
    }

    private void expandBusiness() {

        long investment=Math.max(10000,businessProfit/2);

        if (money<investment) {
            showMessage("Insufficient Funds",
                    "You need "+rupees(investment)+" to expand.");
            return;
        }

        money-=investment;

        businessProfit=Math.min(
                1000000000L,
                businessProfit+businessProfit/2
        );

        callback.onMoneyChanged(money);
        saveCareer();

        showMessage("Expansion Successful",
                "Your business has expanded.\nNew estimated annual profit: "+
                rupees(businessProfit));
    }

    private void freelancing() {

        String[] work={
                "Content Writing",
                "Graphic Design",
                "Programming",
                "Digital Marketing"
        };

        new AlertDialog.Builder(activity)
                .setTitle("💻 Freelancing")
                .setItems(work,(d,which) -> {

                    long earning=5000+
                            (int)(Math.random()*20000);

                    money+=earning;

                    callback.onMoneyChanged(money);

                    showMessage("Freelance Project Completed",
                            "Work: "+work[which]+
                            "\nProject income: "+rupees(earning));
                })
                .setNegativeButton("CANCEL",null)
                .show();
    }

    public void applyAnnualSalary() {

        if (age<18 || retired) return;

        if (lastPaidAge>=age) return;

        long income=0;

        if (employed && monthlySalary>0) {

            income=monthlySalary*12;

            experience++;

            if (experience%3==0) {

                promotions++;

                long increment=monthlySalary*15/100;
                monthlySalary+=increment;
            }

        } else if (businessActive) {

            long randomFactor=
                    75+(int)(Math.random()*51);

            income=businessProfit*randomFactor/100;

            businessProfit=Math.max(
                    0,businessProfit+businessProfit/10
            );
        }

        lastPaidAge=age;

        if (income>0) {

            money=Math.min(
                    Integer.MAX_VALUE,
                    money+income
            );

            callback.onMoneyChanged(money);
        }

        saveCareer();
    }

    private void showStatistics() {

        showMessage("Career Statistics",
                "Current job: "+(employed?jobTitle:"None")+
                "\nCompany: "+company+
                "\nMonthly salary: "+rupees(monthlySalary)+
                "\nWork experience: "+experience+" years"+
                "\nPromotions: "+promotions+
                "\nBusiness: "+(businessActive?businessName:"None")+
                "\nBusiness annual profit: "+rupees(businessProfit)+
                "\nRetirement: "+(retired?"Retired":"Active"));
    }

    private void retire() {

        if (age<60) {
            showMessage("Retirement",
                    "You can retire at age 60 in this simulation.");
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle("🏖️ Retirement")
                .setMessage("Are you sure you want to retire? You will stop receiving your regular employment salary.")
                .setNegativeButton("CANCEL",null)
                .setPositiveButton("RETIRE",(d,w) -> {

                    retired=true;
                    employed=false;
                    monthlySalary=0;

                    saveCareer();

                    showMessage("Congratulations!",
                            "You have officially retired after your career journey.");
                })
                .show();
    }

    private void showRetirement() {
        showMessage("Retirement",
                "You are retired.\n\nYour career journey has ended. Your accumulated money and assets remain yours.");
    }
}
