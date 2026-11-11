package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;

public class SocialManager {

    Activity activity;
    SharedPreferences save;
    int money, age, health, happiness;
    LinearLayout root;

    int gold = Color.rgb(226,184,105);
    int bg = Color.rgb(12,25,23);
    int white = Color.WHITE;

    public interface GameListener {
        void onUpdate(int money, int health, int happiness);
    }

    GameListener listener;

    public SocialManager(
        Activity activity,
        SharedPreferences save,
        int money,
        int age,
        int health,
        int happiness,
        GameListener listener
    ) {
        this.activity = activity;
        this.save = save;
        this.money = money;
        this.age = age;
        this.health = health;
        this.happiness = happiness;
        this.listener = listener;
    }

    int get(String key, int fallback) {
        return save.getInt(key, fallback);
    }

    String getString(String key) {
        return save.getString(key, "");
    }

    void put(String key, int value) {
        save.edit().putInt(key, value).apply();
    }

    void putString(String key, String value) {
        save.edit().putString(key, value).apply();
    }

    int limit(int value) {
        return Math.max(0, Math.min(100, value));
    }

    void update() {
        health = limit(health);
        happiness = limit(happiness);

        save.edit()
            .putInt("money", money)
            .putInt("health", health)
            .putInt("happiness", happiness)
            .apply();

        if (listener != null) {
            listener.onUpdate(money, health, happiness);
        }
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(10, 10, 10, 10);
        return t;
    }

    void heading(String value) {
        TextView t = text(value, 21, gold);
        t.setTypeface(null, Typeface.BOLD);
        root.addView(t);
    }

    void info(String value) {
        root.addView(text(value, 14, white));
    }

    void button(String value, Runnable action) {
        TextView b = text(value, 15, bg);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackground(shape(gold, 22));
        b.setPadding(12, 15, 12, 15);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);

        root.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    void divider() {
        android.view.View line = new android.view.View(activity);
        line.setBackgroundColor(gold);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1, 2);
        p.setMargins(0, 14, 0, 14);

        root.addView(line, p);
    }

    void setup(String title) {
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);

        root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 28);
        root.setBackgroundColor(bg);

        scroll.addView(root);
        activity.setContentView(scroll);

        TextView titleView = text(title, 26, gold);
        titleView.setTypeface(null, Typeface.BOLD);
        root.addView(titleView);

        info("Age: " + age + " years");
        divider();
    }

    void backButton() {
        button("BACK TO LIFE", () -> {
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).showDashboard();
            }
        });
    }

    // MAIN SOCIAL HUB

    public void open() {
        showRelations();
    }

    void showRelations() {
        setup("❤️ RELATIONS");

        heading("👨‍👩‍👧 FAMILY");

        info("Your family is an important part of your life.");

        button("❤️ SPEND TIME WITH FAMILY", () -> {
            happiness = limit(happiness + 5);
            update();
            showRelations();
            message("You spent quality time with your family!");
        });

        button("📞 CALL YOUR PARENTS", () -> {
            happiness = limit(happiness + 3);
            update();
            showRelations();
            message("You had a lovely conversation with your parents.");
        });

        divider();

        heading("👥 FRIENDS");

        int count = get("friendCount", 0);

        if (count == 0) {
            info("You haven't made any friends yet.");
        }

        for (int i = 0; i < count; i++) {
            final int index = i;
            String name = getString("friend_" + i);
            int level = get("friend_level_" + i, 50);

            info("🧑 " + name + "\nFriendship: " + level + "%");

            button("TALK TO " + name.toUpperCase(), () ->
                interactFriend(index, "talk"));

            button("SPEND TIME WITH " + name.toUpperCase(), () ->
                interactFriend(index, "time"));

            button("GIVE GIFT TO " + name.toUpperCase(), () ->
                interactFriend(index, "gift"));
        }

        button("➕ MAKE A NEW FRIEND", () -> addFriend());

        divider();

        heading("💕 ROMANTIC RELATIONSHIP");

        String partner = getString("partner");

        if (age < 18) {
            info("Romantic relationships unlock when you become 18.");
        } else if (partner.isEmpty()) {
            info("You are currently single.");

            button("💌 MEET SOMEONE", () -> meetPartner());

        } else {
            int level = get("partnerLevel", 50);

            info("Partner: " + partner +
                "\nRelationship: " + level + "%");

            button("❤️ SPEND TIME TOGETHER", () ->
                partnerAction("time"));

            button("🎁 GIVE A GIFT", () ->
                partnerAction("gift"));

            button("💬 HAVE A CONVERSATION", () ->
                partnerAction("talk"));

            button("💔 END RELATIONSHIP", () ->
                endRelationship());
        }

        if (age >= 21 && !partner.isEmpty()) {
            divider();
            heading("💍 MARRIAGE");

            if (get("married", 0) == 0) {
                button("💍 PROPOSE MARRIAGE", () ->
                    proposeMarriage());
            } else {
                info("You are married to " + partner + ".");
            }
        }

        divider();
        button("🎯 GO TO ACTIVITIES", () -> showActivities());
        backButton();
    }

    // FRIEND SYSTEM

    void addFriend() {
        EditText input = new EditText(activity);
        input.setHint("Enter friend's name");

        new AlertDialog.Builder(activity)
            .setTitle("Make a Friend")
            .setView(input)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("ADD", (d, w) -> {
                String name = input.getText().toString().trim();

                if (name.isEmpty()) {
                    message("Please enter a name.");
                    return;
                }

                int count = get("friendCount", 0);

                if (count >= 20) {
                    message("You have reached your friend limit.");
                    return;
                }

                putString("friend_" + count, name);
                put("friend_level_" + count, 50);
                put("friendCount", count + 1);

                happiness = limit(happiness + 3);
                update();

                showRelations();
                message("You made a new friend: " + name);
            })
            .show();
    }

    void interactFriend(int index, String action) {
        String name = getString("friend_" + index);
        int level = get("friend_level_" + index, 50);

        if (action.equals("gift")) {
            if (money < 500) {
                message("You need ₹500 to buy a gift.");
                return;
            }

            money -= 500;
            level += 12;
            happiness += 3;

        } else if (action.equals("time")) {
            level += 10;
            happiness += 5;

        } else {
            level += 5;
            happiness += 2;
        }

        put("friend_level_" + index, limit(level));
        update();

        showRelations();
        message("Your friendship with " + name + " improved!");
    }

    // ROMANTIC RELATIONSHIP

    void meetPartner() {
        String[] names = {
            "Aarav", "Ananya", "Rahul",
            "Priya", "Rohan", "Sneha",
            "Arjun", "Meera"
        };

        String selected =
            names[(int)(Math.random() * names.length)];

        putString("partner", selected);
        put("partnerLevel", 40);
        put("married", 0);

        happiness = limit(happiness + 5);
        update();

        showRelations();
        message("You met " + selected +
            ". A new relationship may begin!");
    }

    void partnerAction(String action) {
        int level = get("partnerLevel", 50);

        if (action.equals("gift")) {
            if (money < 1000) {
                message("You need ₹1,000 for this gift.");
                return;
            }

            money -= 1000;
            level += 15;

        } else if (action.equals("time")) {
            level += 10;
            happiness += 5;

        } else {
            level += 5;
            happiness += 2;
        }

        put("partnerLevel", limit(level));
        update();

        showRelations();
        message("Your relationship became stronger!");
    }

    void endRelationship() {
        new AlertDialog.Builder(activity)
            .setTitle("End Relationship?")
            .setMessage("Are you sure you want to end this relationship?")
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("END", (d, w) -> {
                putString("partner", "");
                put("partnerLevel", 0);
                put("married", 0);

                happiness = limit(happiness - 10);
                update();

                showRelations();
            })
            .show();
    }

    void proposeMarriage() {
        int level = get("partnerLevel", 0);

        if (level < 75) {
            message("Improve your relationship to at least 75% first.");
            return;
        }

        new AlertDialog.Builder(activity)
            .setTitle("Marriage Proposal")
            .setMessage("Would you like to get married?")
            .setNegativeButton("NOT NOW", null)
            .setPositiveButton("YES", (d, w) -> {
                put("married", 1);
                happiness = limit(happiness + 15);
                update();

                showRelations();
                message("Congratulations! You are married!");
            })
            .show();
    }

    // ACTIVITIES SYSTEM

    void showActivities() {
        setup("🎯 ACTIVITIES");

        heading("🏏 SPORTS");

        info("Enjoy sports to improve your health and happiness.");

        activityButton(
            "🏏 PLAY CRICKET",
            100,
            3,
            8,
            0,
            "You enjoyed playing cricket!"
        );

        activityButton(
            "🏸 PLAY BADMINTON",
            80,
            4,
            7,
            0,
            "You had a great badminton session!"
        );

        activityButton(
            "⚽ PLAY FOOTBALL",
            100,
            4,
            8,
            0,
            "You enjoyed playing football!"
        );

        divider();

        heading("🏋️ FITNESS");

        activityButton(
            "💪 GYM WORKOUT",
            200,
            8,
            3,
            0,
            "You completed a gym workout!"
        );

        activityButton(
            "🏃 GO RUNNING",
            0,
            6,
            3,
            0,
            "You went for a refreshing run!"
        );

        activityButton(
            "🧘 YOGA",
            0,
            5,
            4,
            0,
            "You completed a relaxing yoga session!"
        );

        divider();

        heading("🎨 HOBBIES");

        activityButton(
            "🎮 PLAY VIDEO GAMES",
            100,
            0,
            8,
            0,
            "You enjoyed playing video games!"
        );

        activityButton(
            "🎵 LISTEN TO MUSIC",
            0,
            0,
            5,
            0,
            "Music helped you relax!"
        );

        activityButton(
            "📚 READ A BOOK",
            50,
            0,
            3,
            5,
            "You learned something new!"
        );

        activityButton(
            "🎬 WATCH A MOVIE",
            200,
            0,
            8,
            0,
            "You enjoyed watching a movie!"
        );

        divider();

        heading("🌴 TRAVEL & OUTINGS");

        activityButton(
            "🏖️ BEACH OUTING",
            1000,
            0,
            12,
            0,
            "You enjoyed a relaxing beach outing!"
        );

        activityButton(
            "⛰️ HILL STATION TRIP",
            5000,
            0,
            18,
            0,
            "You enjoyed a beautiful hill station trip!"
        );

        activityButton(
            "✈️ INTERNATIONAL TRIP",
            50000,
            0,
            25,
            0,
            "You enjoyed an international vacation!"
        );

        divider();

        heading("🎉 SOCIAL EVENTS");

        activityButton(
            "🎂 ATTEND A PARTY",
            500,
            0,
            8,
            0,
            "You had fun at a party!"
        );

        activityButton(
            "🍽️ DINNER WITH FRIENDS",
            800,
            0,
            10,
            0,
            "You enjoyed dinner with friends!"
        );

        activityButton(
            "🎭 ATTEND A CULTURAL EVENT",
            300,
            0,
            10,
            0,
            "You enjoyed a cultural event!"
        );

        divider();

        info("Current Money: ₹" + money);
        info("Health: " + health + "%");
        info("Happiness: " + happiness + "%");

        button("❤️ GO TO RELATIONS", () -> showRelations());
        backButton();
    }

    void activityButton(
        String name,
        int cost,
        int healthGain,
        int happinessGain,
        int intelligenceGain,
        String result
    ) {
        String price = cost == 0 ? "FREE" : "₹" + cost;

        button(name + "  •  " + price, () -> {

            if (age < 5) {
                message("You need to be at least 5 years old for this activity.");
                return;
            }

            if (money < cost) {
                message("You don't have enough money.");
                return;
            }

            money -= cost;
            health = limit(health + healthGain);
            happiness = limit(happiness + happinessGain);

            int intelligence =
                get("intelligence", 50);

            intelligence = limit(
                intelligence + intelligenceGain
            );

            put("intelligence", intelligence);

            update();
            showActivities();
            message(result);
        });
    }

    void message(String value) {
        new AlertDialog.Builder(activity)
            .setTitle("DesiLife")
            .setMessage(value)
            .setPositiveButton("OK", null)
            .show();
    }
}
