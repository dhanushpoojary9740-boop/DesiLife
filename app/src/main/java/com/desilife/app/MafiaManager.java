package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Random;

/**
 * Fictional, choice-driven mafia empire progression for DesiLife.
 * All missions use abstract strategy/story outcomes rather than real-world crime instructions.
 */
public class MafiaManager {
    public interface Callback {
        void onUpdate(int money, int health, int happiness, String description);
    }

    private final Activity activity;
    private final SharedPreferences save;
    private final Callback callback;
    private final Random random = new Random();

    private final int dark = Color.rgb(12,25,23);
    private final int panel = Color.rgb(25,43,39);
    private final int gold = Color.rgb(226,184,105);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(190,198,192);
    private final int red = Color.rgb(205,83,73);

    private final String[] gangs = {
        "Mumbai Syndicate", "Delhi Kings", "Bangalore Brotherhood",
        "Chennai Shadows", "Kolkata Cartel"
    };
    private final String[] specialties = {
        "Business influence and wealth", "Political influence and connections",
        "Technology, networks and reputation", "Territory influence and loyalty",
        "Trade networks and underground contacts"
    };
    private final String[] ranks = {
        "Street Recruit", "Soldier", "Enforcer", "Lieutenant",
        "Underboss", "Gang Boss", "Crime Lord", "Mafia Emperor"
    };
    private final String[] districts = {
        "Old Market", "Harbour District", "Central Business Zone",
        "University Quarter", "Industrial Belt", "Riverside",
        "Tech Park", "Historic Centre", "Northern District", "Capital Circle"
    };

    private String gang;
    private int rank;
    private int reputation;
    private int influence;
    private int completedMissions;
    private int controlledTerritories;
    private int rivalWins;
    private int heat;
    private int money;
    private int health;
    private int happiness;

    public MafiaManager(Activity activity, SharedPreferences save,
                        int money, int health, int happiness, Callback callback) {
        this.activity = activity;
        this.save = save;
        this.money = money;
        this.health = health;
        this.happiness = happiness;
        this.callback = callback;
        load();
    }

    private void load() {
        gang = save.getString("mafia_gang", "");
        rank = save.getInt("mafia_rank", 0);
        reputation = save.getInt("mafia_reputation", 0);
        influence = save.getInt("mafia_influence", 0);
        completedMissions = save.getInt("mafia_missions", 0);
        controlledTerritories = save.getInt("mafia_territories", 0);
        rivalWins = save.getInt("mafia_rival_wins", 0);
        heat = save.getInt("mafia_heat", 0);
    }

    private void persist() {
        save.edit()
            .putString("mafia_gang", gang)
            .putInt("mafia_rank", rank)
            .putInt("mafia_reputation", reputation)
            .putInt("mafia_influence", influence)
            .putInt("mafia_missions", completedMissions)
            .putInt("mafia_territories", controlledTerritories)
            .putInt("mafia_rival_wins", rivalWins)
            .putInt("mafia_heat", heat)
            .apply();
    }

    public void open() {
        if (save.getInt("age", 0) < 18) {
            new AlertDialog.Builder(activity)
                .setTitle("🔒 Underworld Career Locked")
                .setMessage("The fictional underworld storyline unlocks at age 18.")
                .setPositiveButton("OK", null).show();
            return;
        }
        showDashboard();
    }

    private void showDashboard() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(18, 18, 18, 24);
        page.setBackgroundColor(dark);

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.addView(page);

        title(page, "♛ MAFIA EMPIRE");
        text(page, "A fictional underworld rise-through-the-ranks story", 13, muted);
        card(page, "YOUR ORGANISATION",
            gang.isEmpty() ? "You are not a member of a gang yet." :
            gang + "\nRank: " + ranks[Math.min(rank, ranks.length - 1)]);
        card(page, "EMPIRE STATUS",
            "💰 Cash: ₹" + money +
            "\n⭐ Reputation: " + reputation + "/100" +
            "\n influence: " + influence +
            "\n🏙 Territories: " + controlledTerritories + "/10" +
            "\n🎯 Missions completed: " + completedMissions +
            "\n⚔ Rivalry victories: " + rivalWins +
            "\n🚨 Police heat: " + heat + "/100");

        if (gang.isEmpty()) {
            button(page, "🤝 CHOOSE A GANG & JOIN", this::showGangSelection);
            text(page, "Choose carefully: each organisation has a different speciality and starting advantage.", 13, muted);
        } else {
            button(page, "📜 STORY MISSIONS", this::showMissions);
            button(page, "🏙 EXPAND INTO A NEW TERRITORY", this::expandTerritory);
            button(page, "⚔ RIVAL GANG CHALLENGE", this::rivalChallenge);
            button(page, "👥 GANG MANAGEMENT", this::gangManagement);
            button(page, "🏆 RANK & EMPIRE PROGRESS", this::showProgress);
            button(page, "🤝 LEAVE CURRENT GANG", this::leaveGang);
        }
        button(page, "⬅ BACK TO LIFE", activity::recreate);
        activity.setContentView(scroll);
    }

    private void showGangSelection() {
        String[] choices = new String[gangs.length];
        for (int i = 0; i < gangs.length; i++) {
            choices[i] = gangs[i] + " — " + specialties[i];
        }
        new AlertDialog.Builder(activity)
            .setTitle("Choose Your Organisation")
            .setItems(choices, (d, which) -> {
                gang = gangs[which];
                rank = 0;
                reputation = 12 + random.nextInt(8);
                influence = 5;
                completedMissions = 0;
                controlledTerritories = 0;
                rivalWins = 0;
                heat = 0;
                persist();
                notifyChange("You joined " + gang + " as a Street Recruit.");
                showDashboard();
            })
            .setNegativeButton("CANCEL", null).show();
    }

    private void showMissions() {
        String[] missions = {
            "Earn trust at a community event",
            "Negotiate a fictional business partnership",
            "Resolve a dispute between two members",
            "Organise a charity event to improve public standing",
            "Protect the organisation's reputation during a crisis"
        };
        new AlertDialog.Builder(activity)
            .setTitle("Story Missions")
            .setItems(missions, (d, which) -> runMission(which))
            .setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void runMission(int type) {
        String[] outcomes = {
            "You built trust through a public community event.",
            "A carefully negotiated partnership strengthened the organisation.",
            "You settled an internal disagreement and earned loyalty.",
            "A charity initiative improved the gang's public reputation.",
            "You handled a public-relations crisis without escalating conflict."
        };
        int reward = 500 + random.nextInt(1800);
        int rep = 5 + random.nextInt(8);
        money += reward;
        reputation = clamp(reputation + rep, 0, 100);
        influence += 4 + random.nextInt(5);
        completedMissions++;
        happiness = clamp(happiness + 2, 0, 100);
        heat = Math.max(0, heat - 2);
        checkPromotion();
        persist();
        notifyChange(outcomes[type] + " Reward: ₹" + reward + ". Reputation +" + rep + ".");
        showDashboard();
    }

    private void expandTerritory() {
        if (controlledTerritories >= districts.length) {
            new AlertDialog.Builder(activity).setTitle("Empire Complete")
                .setMessage("You already control every available district in this city map.")
                .setPositiveButton("OK", null).show();
            return;
        }
        final String district = districts[controlledTerritories];
        final int cost = 1800 + controlledTerritories * 1200;
        new AlertDialog.Builder(activity)
            .setTitle("Expand Your Influence")
            .setMessage("Target district: " + district +
                "\nDevelopment and community investment: ₹" + cost +
                "\nUse influence and negotiation to establish control?")
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("INVEST", (d,w) -> {
                if (money < cost) {
                    message("You need ₹" + cost + " to fund this expansion.");
                    return;
                }
                money -= cost;
                controlledTerritories++;
                influence += 12;
                reputation = clamp(reputation + 4, 0, 100);
                checkPromotion();
                persist();
                notifyChange("Your organisation established influence in " + district + ".");
                showDashboard();
            }).show();
    }

    private void rivalChallenge() {
        if (controlledTerritories == 0 && reputation < 20) {
            message("Build more reputation or establish your first territory before challenging a rival.");
            return;
        }
        String[] strategies = {
            "Diplomatic settlement", "Business competition", "Public influence campaign"
        };
        new AlertDialog.Builder(activity).setTitle("Rival Gang Challenge")
            .setItems(strategies, (d, which) -> {
                int chance = 45 + reputation / 4 + Math.min(20, influence / 5);
                if (which == 0) chance += 12;
                if (which == 1) chance += 5;
                if (which == 2) chance += 8;
                boolean won = random.nextInt(100) < Math.min(90, chance);
                if (won) {
                    rivalWins++;
                    reputation = clamp(reputation + 7, 0, 100);
                    influence += 10;
                    money += 1200;
                    heat = Math.max(0, heat - 3);
                    notifyChange("Your strategy outperformed the rival organisation. You gained ₹1,200, influence and reputation.");
                } else {
                    reputation = Math.max(0, reputation - 3);
                    happiness = Math.max(0, happiness - 4);
                    heat = clamp(heat + 8, 0, 100);
                    notifyChange("The rival challenge did not go your way. Your reputation slipped and attention increased.");
                }
                checkPromotion();
                persist();
                showDashboard();
            }).setNegativeButton("CANCEL", null).show();
    }

    private void gangManagement() {
        String[] actions = {
            "Recruit a new member (₹1,000)",
            "Train your members (₹1,500)",
            "Promote a trusted lieutenant",
            "Hold a loyalty meeting"
        };
        new AlertDialog.Builder(activity).setTitle("Gang Management")
            .setItems(actions, (d, which) -> {
                if (which == 0) {
                    if (money < 1000) { message("You need ₹1,000."); return; }
                    money -= 1000; influence += 5; reputation = clamp(reputation + 2,0,100);
                    notifyChange("A new fictional member joined your organisation.");
                } else if (which == 1) {
                    if (money < 1500) { message("You need ₹1,500."); return; }
                    money -= 1500; influence += 9;
                    notifyChange("Your members completed leadership and teamwork training.");
                } else if (which == 2) {
                    if (rank < 3 || influence < 35) { message("Reach Lieutenant rank and 35 influence first."); return; }
                    influence += 4; reputation = clamp(reputation + 3,0,100);
                    notifyChange("A trusted member was promoted to lieutenant.");
                } else {
                    reputation = clamp(reputation + 5,0,100);
                    happiness = clamp(happiness + 2,0,100);
                    notifyChange("Your loyalty meeting strengthened team morale.");
                }
                checkPromotion(); persist(); showDashboard();
            }).setNegativeButton("BACK", null).show();
    }

    private void showProgress() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < ranks.length; i++) {
            s.append(i <= rank ? "✓ " : "🔒 ").append(ranks[i]);
            if (i == rank) s.append("  ← CURRENT");
            s.append("\n");
        }
        s.append("\nNext rank requirements:\n");
        if (rank < ranks.length - 1) {
            s.append("Reputation: ").append((rank + 1) * 12).append("\n");
            s.append("Influence: ").append((rank + 1) * 18).append("\n");
            s.append("Missions: ").append((rank + 1) * 2).append("\n");
            s.append("Territories: ").append(Math.max(0, rank - 1));
        } else {
            s.append("You have reached the final rank.");
        }
        new AlertDialog.Builder(activity).setTitle("🏆 Mafia Career")
            .setMessage(s.toString()).setPositiveButton("OK", null).show();
    }

    private void checkPromotion() {
        while (rank < ranks.length - 1) {
            int next = rank + 1;
            if (reputation >= next * 12 && influence >= next * 18 &&
                completedMissions >= next * 2 &&
                controlledTerritories >= Math.max(0, next - 1)) {
                rank = next;
                reputation = clamp(reputation + 5,0,100);
                influence += 8;
                notifyChange("PROMOTION! You are now " + ranks[rank] + ".");
            } else break;
        }
    }

    private void leaveGang() {
        new AlertDialog.Builder(activity).setTitle("Leave Organisation?")
            .setMessage("Your gang career progress will be retained, but you will leave your current organisation.")
            .setNegativeButton("STAY", null)
            .setPositiveButton("LEAVE", (d,w) -> {
                gang = "";
                rank = 0;
                persist();
                notifyChange("You left your organisation. You can join another gang.");
                showDashboard();
            }).show();
    }

    private void notifyChange(String description) {
        persist();
        if (callback != null) callback.onUpdate(money, health, happiness, description);
    }

    private void message(String s) {
        new AlertDialog.Builder(activity).setMessage(s)
            .setPositiveButton("OK", null).show();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void title(LinearLayout root, String value) {
        TextView t = makeText(value, 25, gold, true);
        root.addView(t);
    }

    private void text(LinearLayout root, String value, int size, int color) {
        root.addView(makeText(value, size, color, false));
    }

    private void card(LinearLayout root, String heading, String body) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(14, 12, 14, 12);
        box.setBackground(shape(panel, 16));
        TextView h = makeText(heading, 15, gold, true);
        h.setGravity(Gravity.LEFT);
        TextView b = makeText(body, 15, white, false);
        b.setGravity(Gravity.LEFT);
        box.addView(h); box.addView(b);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 8, 0, 8);
        root.addView(box, p);
    }

    private void button(LinearLayout root, String label, Runnable action) {
        TextView b = makeText(label, 15, dark, true);
        b.setBackground(shape(gold, 18));
        b.setPadding(12, 15, 12, 15);
        b.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);
        root.addView(b, p);
    }

    private TextView makeText(String value, int size, int color, boolean bold) {
        TextView t = new TextView(activity);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setPadding(5, 7, 5, 7);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius);
        return d;
    }
}
