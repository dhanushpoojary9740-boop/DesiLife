package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;

import java.util.Random;

public class LifeEventsManager {

    public interface EventCallback {
        void onUpdate(
            int money,
            int health,
            int happiness,
            int intelligence,
            String description
        );
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final EventCallback callback;

    private int age;
    private int money;
    private int health;
    private int happiness;
    private int intelligence;

    private final Random random = new Random();

    public LifeEventsManager(
        Activity activity,
        SharedPreferences save,
        int age,
        int money,
        int health,
        int happiness,
        int intelligence,
        EventCallback callback
    ) {
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

        if (age < 5) return;

        int event = random.nextInt(10);

        switch (event) {

            case 0:
                educationEvent();
                break;

            case 1:
                moneyEvent();
                break;

            case 2:
                healthEvent();
                break;

            case 3:
                friendshipEvent();
                break;

            case 4:
                opportunityEvent();
                break;

            case 5:
                luckyEvent();
                break;

            case 6:
                challengeEvent();
                break;

            case 7:
                achievementEvent();
                break;

            case 8:
                shoppingEvent();
                break;

            default:
                normalEvent();
                break;
        }
    }

    private void educationEvent() {

        showChoice(
            "📚 Education Opportunity",
            "You received an opportunity to attend a special learning programme.",
            "Join programme",
            "Skip opportunity",
            () -> {
                money = Math.max(0,money-1000);
                intelligence = Math.min(100,intelligence+8);
                happiness = Math.min(100,happiness+3);
                finish("You joined a learning programme.");
            },
            () -> {
                happiness = Math.max(0,happiness-1);
                finish("You decided to skip the learning programme.");
            }
        );
    }

    private void moneyEvent() {

        showChoice(
            "💰 Unexpected Money",
            "A distant relative sent you a gift of ₹5,000.",
            "Accept gift",
            "Decline gift",
            () -> {
                money = safeAdd(money,5000);
                happiness = Math.min(100,happiness+5);
                finish("You received ₹5,000 as a family gift.");
            },
            () -> finish("You declined a family gift.")
        );
    }

    private void healthEvent() {

        showChoice(
            "🏥 Health Challenge",
            "You are feeling unwell and need to take care of your health.",
            "Visit doctor – ₹1,000",
            "Rest at home",
            () -> {
                if (money >= 1000) {
                    money -= 1000;
                    health = Math.min(100,health+8);
                    finish("You visited a doctor and recovered.");
                } else {
                    health = Math.max(0,health-2);
                    finish("You could not afford a doctor visit.");
                }
            },
            () -> {
                health = Math.max(0,health-3);
                finish("You rested at home while recovering.");
            }
        );
    }

    private void friendshipEvent() {

        showChoice(
            "🤝 Friendship",
            "You met someone who could become a close friend.",
            "Spend time together",
            "Continue your day",
            () -> {
                happiness = Math.min(100,happiness+8);
                finish("You made a new friend.");
            },
            () -> finish("You decided to continue your day.")
        );
    }

    private void opportunityEvent() {

        showChoice(
            "💼 Career Opportunity",
            "You received an invitation to attend a professional workshop.",
            "Attend workshop – ₹2,000",
            "Decline",
            () -> {
                if (age >= 18 && money >= 2000) {
                    money -= 2000;
                    intelligence = Math.min(100,intelligence+5);
                    happiness = Math.min(100,happiness+3);
                    finish("You attended a professional workshop.");
                } else {
                    finish("You were not eligible or could not afford the workshop.");
                }
            },
            () -> finish("You declined the workshop invitation.")
        );
    }

    private void luckyEvent() {

        int reward = 1000 + random.nextInt(4001);

        showChoice(
            "🍀 Lucky Day",
            "You found an unexpected opportunity to earn ₹"+reward+".",
            "Take opportunity",
            "Ignore",
            () -> {
                money = safeAdd(money,reward);
                happiness = Math.min(100,happiness+5);
                finish("You earned ₹"+reward+" from a lucky opportunity.");
            },
            () -> finish("You ignored a lucky opportunity.")
        );
    }

    private void challengeEvent() {

        showChoice(
            "⚡ Life Challenge",
            "You faced an unexpected difficult situation.",
            "Stay positive",
            "Give up",
            () -> {
                happiness = Math.min(100,happiness+3);
                intelligence = Math.min(100,intelligence+3);
                finish("You overcame a difficult situation.");
            },
            () -> {
                happiness = Math.max(0,happiness-5);
                finish("A difficult situation affected your happiness.");
            }
        );
    }

    private void achievementEvent() {

        showChoice(
            "🏆 Special Achievement",
            "Your efforts have been noticed. You received recognition.",
            "Celebrate",
            "Stay focused",
            () -> {
                happiness = Math.min(100,happiness+10);
                money = safeAdd(money,2000);
                finish("You received recognition and a ₹2,000 reward.");
            },
            () -> {
                intelligence = Math.min(100,intelligence+2);
                finish("You stayed focused on your goals.");
            }
        );
    }

    private void shoppingEvent() {

        showChoice(
            "🛍️ Shopping Opportunity",
            "There is a special offer on something you wanted.",
            "Buy item – ₹1,500",
            "Save money",
            () -> {
                if (money >= 1500) {
                    money -= 1500;
                    happiness = Math.min(100,happiness+5);
                    finish("You purchased something special.");
                } else {
                    finish("You decided not to spend money you didn't have.");
                }
            },
            () -> finish("You saved your money instead of shopping.")
        );
    }

    private void normalEvent() {

        showChoice(
            "🌟 Everyday Moment",
            "You had an ordinary but meaningful day.",
            "Spend time with family",
            "Focus on yourself",
            () -> {
                happiness = Math.min(100,happiness+4);
                finish("You spent quality time with family.");
            },
            () -> {
                intelligence = Math.min(100,intelligence+2);
                finish("You spent time learning something new.");
            }
        );
    }

    private void showChoice(
        String title,
        String description,
        String positive,
        String negative,
        Runnable positiveAction,
        Runnable negativeAction
    ) {

        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(positive,(dialog,which) -> {
                positiveAction.run();
            })
            .setNegativeButton(negative,(dialog,which) -> {
                negativeAction.run();
            })
            .setCancelable(false)
            .show();
    }

    private int safeAdd(int current,int amount) {
        return (int)Math.min(
            Integer.MAX_VALUE,
            Math.max(0L,(long)current+amount)
        );
    }

    private void finish(String description) {

        if (callback != null) {
            callback.onUpdate(
                money,
                health,
                happiness,
                intelligence,
                description
            );
        }
    }
}
