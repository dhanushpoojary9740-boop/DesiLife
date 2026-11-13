package com.desilife.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Random;

public class LifeEventsManager {

    public interface EventCallback {
        void onUpdate(int money, int health, int happiness,
                      int intelligence, String description);
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final EventCallback callback;
    private final Random random = new Random();

    private int age;
    private int money;
    private int health;
    private int happiness;
    private int intelligence;
    private Dialog activeDialog;

    private final int ink = Color.rgb(65, 19, 31);
    private final int cream = Color.rgb(250, 248, 243);
    private final int blue = Color.rgb(0, 119, 202);
    private final int orange = Color.rgb(222, 54, 12);

    public LifeEventsManager(Activity activity, SharedPreferences save,
            int age, int money, int health, int happiness,
            int intelligence, EventCallback callback) {
        this.activity = activity;
        this.save = save;
        this.age = age;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.intelligence = intelligence;
        this.callback = callback;
    }

    public void triggerRandomEvent() {
        if (age < 5 || activity.isFinishing()) return;

        // Events are offered once per birthday. The age key also protects
        // against accidental duplicate launches during the same year.
        String eventKey = "desilife_event_shown_age";
        if (save.getInt(eventKey, -1) == age) {
            finish("You completed another year of life.");
            return;
        }
        save.edit().putInt(eventKey, age).apply();

        int event = random.nextInt(15);
        switch (event) {
            case 0: educationEvent(); break;
            case 1: moneyEvent(); break;
            case 2: healthEvent(); break;
            case 3: friendshipEvent(); break;
            case 4: opportunityEvent(); break;
            case 5: luckyEvent(); break;
            case 6: challengeEvent(); break;
            case 7: achievementEvent(); break;
            case 8: shoppingEvent(); break;
            case 9: socialMediaEvent(); break;
            case 10: roadsideEncounterEvent(); break;
            case 11: vehicleEvent(); break;
            case 12: familyEvent(); break;
            case 13: leisureEvent(); break;
            default: careerOfferEvent(); break;
        }
    }

    private void educationEvent() {
        showChoice("Education", "📚 Learning Opportunity",
            "You have been offered a place in a weekend learning programme. It could improve your skills and open new doors.",
            "Join programme · ₹1,000", "Skip for now",
            () -> {
                if (money >= 1000) {
                    money -= 1000;
                    intelligence = Math.min(100, intelligence + 8);
                    happiness = Math.min(100, happiness + 3);
                    finish("You joined the programme, improved your knowledge and spent ₹1,000.");
                } else finish("You could not afford the programme and decided to skip it.");
            },
            () -> finish("You decided to skip the learning programme."));
    }

    private void moneyEvent() {
        showChoice("Family", "💰 Unexpected Gift",
            "A distant relative has sent you a gift of ₹5,000. Your family asks what you want to do.",
            "Accept gift", "Politely decline",
            () -> {
                money = safeAdd(money, 5000);
                happiness = Math.min(100, happiness + 5);
                finish("You accepted the family gift of ₹5,000.");
            },
            () -> finish("You politely declined the family gift."));
    }

    private void healthEvent() {
        showChoice("Health", "🤒 Feeling Unwell",
            "You have been feeling weak and unwell for a few days. You should decide how to take care of yourself.",
            "Visit doctor · ₹1,000", "Rest at home",
            () -> {
                if (money >= 1000) {
                    money -= 1000;
                    health = Math.min(100, health + 8);
                    finish("You visited a doctor, received treatment and spent ₹1,000.");
                } else {
                    health = Math.max(0, health - 2);
                    finish("You did not have enough money for a consultation. Your condition worsened slightly.");
                }
            },
            () -> {
                health = Math.max(0, health - 3);
                finish("You rested at home. You feel a little better, but recovery will take time.");
            });
    }

    private void friendshipEvent() {
        showChoice("Social", "🤝 A New Friendship",
            "You meet someone interesting at a local event. They seem friendly and invite you to spend some time together.",
            "Make a new friend", "Continue your day",
            () -> {
                happiness = Math.min(100, happiness + 8);
                finish("You spent time together and started a promising friendship.");
            },
            () -> finish("You smiled and continued with your day."));
    }

    private void opportunityEvent() {
        showChoice("Career", "💼 Professional Workshop",
            "You have been invited to a career development workshop. It may help you build useful professional skills.",
            "Attend · ₹2,000", "Decline invitation",
            () -> {
                if (age >= 18 && money >= 2000) {
                    money -= 2000;
                    intelligence = Math.min(100, intelligence + 5);
                    happiness = Math.min(100, happiness + 3);
                    finish("You attended the workshop and gained valuable professional knowledge. Fee: ₹2,000.");
                } else finish("You are not eligible yet or cannot afford the workshop.");
            },
            () -> finish("You declined the workshop invitation."));
    }

    private void luckyEvent() {
        int reward = 1000 + random.nextInt(4001);
        showChoice("Opportunity", "🍀 A Lucky Day",
            "A neighbour needs help with a short project and is willing to pay you ₹" + reward + ".",
            "Take the opportunity", "Ignore it",
            () -> {
                money = safeAdd(money, reward);
                happiness = Math.min(100, happiness + 5);
                finish("You completed the project and earned ₹" + reward + ".");
            },
            () -> finish("You decided not to take the short-term project."));
    }

    private void challengeEvent() {
        showChoice("Situation", "⚡ An Unexpected Challenge",
            "A difficult situation has disrupted your plans. You can face it calmly or walk away.",
            "Stay positive", "Give up",
            () -> {
                happiness = Math.min(100, happiness + 3);
                intelligence = Math.min(100, intelligence + 3);
                finish("You handled the challenge patiently and learned from the experience.");
            },
            () -> {
                happiness = Math.max(0, happiness - 5);
                finish("You walked away. The unresolved situation affected your happiness.");
            });
    }

    private void achievementEvent() {
        showChoice("Achievement", "🏆 Special Recognition",
            "Your recent efforts have been noticed. You have received recognition and a small reward.",
            "Celebrate · Receive ₹2,000", "Stay focused",
            () -> {
                happiness = Math.min(100, happiness + 10);
                money = safeAdd(money, 2000);
                finish("You celebrated your achievement and received ₹2,000.");
            },
            () -> {
                intelligence = Math.min(100, intelligence + 2);
                finish("You thanked everyone and continued focusing on your goals.");
            });
    }

    private void shoppingEvent() {
        showChoice("Lifestyle", "🛍️ A Special Offer",
            "A shop nearby is offering a product you have wanted for a long time at a discounted price.",
            "Buy item · ₹1,500", "Save the money",
            () -> {
                if (money >= 1500) {
                    money -= 1500;
                    happiness = Math.min(100, happiness + 5);
                    finish("You bought the item at a discounted price for ₹1,500.");
                } else finish("You checked your balance and decided not to spend money you do not have.");
            },
            () -> finish("You skipped the offer and kept your savings safe."));
    }

    private void socialMediaEvent() {
        showChoice("Social", "📱 Everyone Is Joining Social Media",
            "Your friends are joining a popular social networking platform. A few of them are encouraging you to create an account.",
            "Create an account", "Stay away from it",
            () -> {
                happiness = Math.min(100, happiness + 4);
                finish("You joined the platform and reconnected with several friends.");
            },
            () -> {
                intelligence = Math.min(100, intelligence + 1);
                finish("You decided to spend less time on social media and focus on other interests.");
            });
    }

    private void roadsideEncounterEvent() {
        int gift = 100 + random.nextInt(401);
        showChoice("Encounter", "🧍 Someone Needs Help",
            "While you are out, a person approaches and asks if you can spare some money. What will you do?",
            "Help with ₹" + gift, "Politely refuse",
            () -> {
                if (money >= gift) {
                    money -= gift;
                    happiness = Math.min(100, happiness + 3);
                    finish("You helped the person with ₹" + gift + ". It felt good to help someone.");
                } else finish("You wanted to help, but you did not have enough spare money.");
            },
            () -> finish("You politely explained that you could not help today."));
    }

    private void vehicleEvent() {
        int repair = 500 + random.nextInt(2501);
        showChoice("Vehicle", "🚗 Vehicle Trouble",
            "Your vehicle has developed a mechanical problem. A nearby mechanic estimates the repair cost at ₹" + repair + ".",
            "Repair it · ₹" + repair, "Postpone repair",
            () -> {
                if (money >= repair) {
                    money -= repair;
                    finish("Your vehicle was repaired. You paid ₹" + repair + ".");
                } else {
                    health = Math.max(0, health - 1);
                    finish("You could not afford the repair. The vehicle remains unusable.");
                }
            },
            () -> {
                happiness = Math.max(0, happiness - 2);
                finish("You postponed the repair. The vehicle may cause more trouble later.");
            });
    }

    private void familyEvent() {
        int cost = 1000 + random.nextInt(2001);
        showChoice("Family", "👨‍👩‍👧 A Family Celebration",
            "Your family is planning a small celebration. Everyone hopes you can contribute towards the arrangements.",
            "Contribute · ₹" + cost, "Keep your money",
            () -> {
                if (money >= cost) {
                    money -= cost;
                    happiness = Math.min(100, happiness + 8);
                    finish("You contributed ₹" + cost + " and enjoyed a lovely family celebration.");
                } else finish("You could not contribute this time, but your family understood.");
            },
            () -> finish("You explained that you need to manage your expenses and skipped the contribution."));
    }

    private void leisureEvent() {
        showChoice("Leisure", "🎡 Friends Are Going Out",
            "Your friends are planning a day out. It could be a refreshing break from your routine.",
            "Join them · ₹800", "Stay home",
            () -> {
                if (money >= 800) {
                    money -= 800;
                    happiness = Math.min(100, happiness + 9);
                    finish("You joined your friends and enjoyed a refreshing day out. You spent ₹800.");
                } else finish("You could not afford the outing, so you made other plans.");
            },
            () -> {
                happiness = Math.max(0, happiness - 1);
                finish("You stayed home and spent a quiet day.");
            });
    }

    private void careerOfferEvent() {
        if (age < 18) {
            friendshipEvent();
            return;
        }
        int salary = 12000 + random.nextInt(18001);
        showChoice("Career", "🌟 A New Job Opportunity",
            "A local employer has contacted you about a possible job opportunity. The offered monthly salary is ₹" + salary + ".",
            "Explore the offer", "Decline for now",
            () -> {
                intelligence = Math.min(100, intelligence + 2);
                happiness = Math.min(100, happiness + 3);
                finish("You arranged an interview for the opportunity offering ₹" + salary + " per month. Your current job has not changed.");
            },
            () -> finish("You decided to remain focused on your current career."));
    }

    private void normalEvent() {
        showChoice("Everyday Life", "🌟 An Ordinary Day",
            "Nothing extraordinary happened today, but you have a chance to make the day meaningful.",
            "Spend time with family", "Learn something new",
            () -> {
                happiness = Math.min(100, happiness + 4);
                finish("You spent quality time with your family.");
            },
            () -> {
                intelligence = Math.min(100, intelligence + 2);
                finish("You spent time learning something new.");
            });
    }

    private void showChoice(String category, String title, String description,
            String firstLabel, String secondLabel,
            Runnable firstAction, Runnable secondAction) {

        if (activity.isFinishing()) return;

        final Dialog dialog = new Dialog(activity);
        activeDialog = dialog;

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(cream);
        cardBg.setCornerRadius(dp(22));
        cardBg.setStroke(dp(3), category.equals("Health") ? Color.rgb(35, 125, 72) : orange);
        card.setBackground(cardBg);

        TextView ribbon = new TextView(activity);
        ribbon.setText(category.toUpperCase());
        ribbon.setTextColor(Color.WHITE);
        ribbon.setTextSize(13);
        ribbon.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
        ribbon.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        ribbon.setPadding(dp(14), dp(11), dp(14), dp(11));
        GradientDrawable ribbonBg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.rgb(250, 235, 226), orange}
        );
        ribbonBg.setCornerRadius(dp(8));
        ribbon.setBackground(ribbonBg);
        card.addView(ribbon, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView heading = new TextView(activity);
        heading.setText(title);
        heading.setTextColor(ink);
        heading.setTextSize(23);
        heading.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(dp(4), dp(20), dp(4), dp(12));
        card.addView(heading);

        TextView body = new TextView(activity);
        body.setText(description);
        body.setTextColor(ink);
        body.setTextSize(17);
        body.setGravity(Gravity.CENTER);
        body.setLineSpacing(dp(3), 1.05f);
        body.setPadding(dp(3), dp(4), dp(3), dp(16));
        card.addView(body);

        addDecisionButton(card, firstLabel, firstAction);
        addDecisionButton(card, secondLabel, secondAction);

        TextView surprise = new TextView(activity);
        surprise.setText("🎲  Surprise me!");
        surprise.setTextColor(Color.rgb(20, 99, 155));
        surprise.setTextSize(16);
        surprise.setGravity(Gravity.CENTER);
        surprise.setPadding(dp(8), dp(18), dp(8), dp(8));
        surprise.setPaintFlags(surprise.getPaintFlags() | 8);
        surprise.setOnClickListener(v -> {
            if (dialog.isShowing()) dialog.dismiss();
            if (random.nextBoolean()) firstAction.run();
            else secondAction.run();
        });
        card.addView(surprise);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        scroll.addView(card);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(scroll);
        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = Math.min(activity.getResources().getDisplayMetrics().widthPixels - dp(34), dp(520));
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.dimAmount = 0.78f;
            window.setAttributes(params);
        }
    }

    private void addDecisionButton(LinearLayout parent, String label, Runnable action) {
        Button button = new Button(activity);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setAllCaps(false);
        button.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        GradientDrawable background = new GradientDrawable();
        background.setColor(blue);
        background.setCornerRadius(dp(11));
        button.setBackground(background);
        button.setElevation(dp(2));
        button.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(7), 0, dp(5));
        parent.addView(button, params);
        button.setOnClickListener(v -> {
            if (activeDialog != null && activeDialog.isShowing()) activeDialog.dismiss();
            action.run();
        });
    }

    private int dp(int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    private int safeAdd(int current, int amount) {
        return (int) Math.min(Integer.MAX_VALUE,
            Math.max(0L, (long) current + amount));
    }

    private void finish(String description) {
        if (callback != null) {
            callback.onUpdate(money, health, happiness, intelligence, description);
        }
    }
}
