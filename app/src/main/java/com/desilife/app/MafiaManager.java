package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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
    private int businessLevel;
    private int businessIncome;
    private int prisonYears;
    private int dominationScore;
    private int loyalty;
    private int lieutenants;
    private int safehouseLevel;
    private int bossVictories;
    private int legacyScore;
    private int internationalCities;
    private int globalAlliances;
    private int assetPortfolio;
    private int globalInfluence;
    // Bundle 5: living-world, elections, succession and rival AI.
    private int rivalEmpirePower;
    private int lastRivalUpdateAge;
    private int lastWorldEventAge;
    private int worldEventsExperienced;
    private int electionWins;
    private int electionAttempts;
    private String heirName;
    private boolean successionCompleted;
    private int dynastyGenerations;
    // Bundle 6: city map, family bonds, economic ledger and story choices.
    private int familyBond = 65;
    private int economyReports;
    private int storyChoicesMade;
    private String lastStoryChoice = "No major story choice yet";
    // Bundle 7: character identity, family tree, rival personalities and lifetime records.
    private int respect = 50;
    private int fear = 10;
    private int popularity = 50;
    private int lifetimeWealthPeak;
    private int lifetimeInfluencePeak;
    private int lifetimeTerritoriesPeak;
    private int permanentDecisions;
    private String rivalPersonality = "Strategic";
    private String familyParent1 = "Not recorded";
    private String familyParent2 = "Not recorded";
    private String familyChild = "Not recorded";
    private String familySibling = "Not recorded";

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
        businessLevel = save.getInt("mafia_business_level", 0);
        businessIncome = save.getInt("mafia_business_income", 0);
        prisonYears = save.getInt("mafia_prison_years", 0);
        dominationScore = save.getInt("mafia_domination", 0);
        loyalty = save.getInt("mafia_loyalty", 50);
        lieutenants = save.getInt("mafia_lieutenants", 0);
        safehouseLevel = save.getInt("mafia_safehouse_level", 0);
        bossVictories = save.getInt("mafia_boss_victories", 0);
        legacyScore = save.getInt("mafia_legacy_score", 0);
        internationalCities = save.getInt("mafia_global_cities", 0);
        globalAlliances = save.getInt("mafia_global_alliances", 0);
        assetPortfolio = save.getInt("mafia_global_assets", 0);
        globalInfluence = save.getInt("mafia_global_influence", 0);
        rivalEmpirePower = save.getInt("mafia_rival_empire_power", 20);
        lastRivalUpdateAge = save.getInt("mafia_rival_update_age", -1);
        lastWorldEventAge = save.getInt("mafia_world_event_age", -1);
        worldEventsExperienced = save.getInt("mafia_world_events", 0);
        electionWins = save.getInt("mafia_election_wins", 0);
        electionAttempts = save.getInt("mafia_election_attempts", 0);
        heirName = save.getString("mafia_heir_name", "");
        successionCompleted = save.getBoolean("mafia_succession_completed", false);
        dynastyGenerations = save.getInt("mafia_dynasty_generations", 1);
        familyBond = save.getInt("mafia_family_bond", 65);
        economyReports = save.getInt("mafia_economy_reports", 0);
        storyChoicesMade = save.getInt("mafia_story_choices", 0);
        lastStoryChoice = save.getString("mafia_last_story_choice", "No major story choice yet");
        respect = save.getInt("mafia_respect", 50);
        fear = save.getInt("mafia_fear", 10);
        popularity = save.getInt("mafia_popularity", 50);
        lifetimeWealthPeak = save.getInt("mafia_lifetime_wealth_peak", 0);
        lifetimeInfluencePeak = save.getInt("mafia_lifetime_influence_peak", 0);
        lifetimeTerritoriesPeak = save.getInt("mafia_lifetime_territories_peak", 0);
        permanentDecisions = save.getInt("mafia_permanent_decisions", 0);
        rivalPersonality = save.getString("mafia_rival_personality", "Strategic");
        familyParent1 = save.getString("mafia_family_parent1", "Not recorded");
        familyParent2 = save.getString("mafia_family_parent2", "Not recorded");
        familyChild = save.getString("mafia_family_child", "Not recorded");
        familySibling = save.getString("mafia_family_sibling", "Not recorded");
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
            .putInt("mafia_business_level", businessLevel)
            .putInt("mafia_business_income", businessIncome)
            .putInt("mafia_prison_years", prisonYears)
            .putInt("mafia_domination", dominationScore)
            .putInt("mafia_loyalty", loyalty)
            .putInt("mafia_lieutenants", lieutenants)
            .putInt("mafia_safehouse_level", safehouseLevel)
            .putInt("mafia_boss_victories", bossVictories)
            .putInt("mafia_legacy_score", legacyScore)
            .putInt("mafia_global_cities", internationalCities)
            .putInt("mafia_global_alliances", globalAlliances)
            .putInt("mafia_global_assets", assetPortfolio)
            .putInt("mafia_global_influence", globalInfluence)
            .putInt("mafia_rival_empire_power", rivalEmpirePower)
            .putInt("mafia_rival_update_age", lastRivalUpdateAge)
            .putInt("mafia_world_event_age", lastWorldEventAge)
            .putInt("mafia_world_events", worldEventsExperienced)
            .putInt("mafia_election_wins", electionWins)
            .putInt("mafia_election_attempts", electionAttempts)
            .putString("mafia_heir_name", heirName)
            .putBoolean("mafia_succession_completed", successionCompleted)
            .putInt("mafia_dynasty_generations", dynastyGenerations)
            .putInt("mafia_family_bond", familyBond)
            .putInt("mafia_economy_reports", economyReports)
            .putInt("mafia_story_choices", storyChoicesMade)
            .putString("mafia_last_story_choice", lastStoryChoice)
            .putInt("mafia_respect", respect)
            .putInt("mafia_fear", fear)
            .putInt("mafia_popularity", popularity)
            .putInt("mafia_lifetime_wealth_peak", Math.max(lifetimeWealthPeak, money))
            .putInt("mafia_lifetime_influence_peak", Math.max(lifetimeInfluencePeak, globalInfluence))
            .putInt("mafia_lifetime_territories_peak", Math.max(lifetimeTerritoriesPeak, controlledTerritories))
            .putInt("mafia_permanent_decisions", permanentDecisions)
            .putString("mafia_rival_personality", rivalPersonality)
            .putString("mafia_family_parent1", familyParent1)
            .putString("mafia_family_parent2", familyParent2)
            .putString("mafia_family_child", familyChild)
            .putString("mafia_family_sibling", familySibling)
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
        processLivingWorld();
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
            "\n🚨 Police heat: " + heat + "/100" +
            "\n🌐 Rival empire power: " + rivalEmpirePower + "/100" +
            "\n🗳 Election victories: " + electionWins +
            "\n👑 Dynasty generations: " + dynastyGenerations);

        if (gang.isEmpty()) {
            button(page, "🤝 CHOOSE A GANG & JOIN", this::showGangSelection);
            text(page, "Choose carefully: each organisation has a different speciality and starting advantage.", 13, muted);
        } else {
            button(page, "📜 STORY MISSIONS", this::showMissions);
            button(page, "🏙 EXPAND INTO A NEW TERRITORY", this::expandTerritory);
            button(page, "⚔ RIVAL GANG CHALLENGE", this::rivalChallenge);
            button(page, "👥 GANG MANAGEMENT", this::gangManagement);
            button(page, "⚔ RIVAL BOSS SHOWDOWN", this::bossShowdown);
            button(page, "🤝 LOYALTY & FAMILY HIERARCHY", this::familyHierarchy);
            button(page, "🏠 SAFEHOUSE NETWORK", this::safehouseNetwork);
            button(page, "📜 LEGACY & GENERATION RECORD", this::legacyRecord);
            button(page, "🏆 RANK & EMPIRE PROGRESS", this::showProgress);
            button(page, "🏢 UNDERGROUND BUSINESS NETWORK", this::businessNetwork);
            button(page, "🚨 POLICE ATTENTION & PRISON", this::policeAndPrison);
            button(page, "👑 MAFIA WORLD DOMINATION", this::worldDomination);
            button(page, "🌍 INTERNATIONAL NETWORK", this::internationalNetwork);
            button(page, "🤝 GLOBAL ALLIANCES", this::globalAlliancesMenu);
            button(page, "💎 WEALTH & ASSET PORTFOLIO", this::globalAssetPortfolio);
            button(page, "🏆 EMPEROR ACHIEVEMENTS", this::emperorAchievements);
            button(page, "🌦 DYNAMIC WORLD EVENTS", this::worldEventsReport);
            button(page, "🗳 GLOBAL UNDERWORLD ELECTION", this::mafiaElection);
            button(page, "👑 HEIR & GENERATIONAL SUCCESSION", this::successionMenu);
            button(page, "🌐 RIVAL EMPIRE AI", this::rivalEmpireReport);
            button(page, "🗺 LIVING CITY WORLD MAP", this::livingCityMap);
            button(page, "👨‍👩‍👧 FAMILY RELATIONSHIPS", this::familyRelationships);
            button(page, "📊 EMPIRE ECONOMY REPORT", this::empireEconomyReport);
            button(page, "🥇 GLOBAL WORLD RANKING", this::worldRanking);
            button(page, "📖 DYNAMIC STORY CHOICES", this::dynamicStoryChoices);
            button(page, "🎭 CHARACTER REPUTATION", this::characterReputation);
            button(page, "🌳 MAFIA FAMILY TREE", this::mafiaFamilyTree);
            button(page, "🧠 RIVAL BOSS PERSONALITY", this::rivalPersonalityReport);
            button(page, "⚖ MAJOR LIFE DECISIONS", this::majorLifeDecisions);
            button(page, "📈 LIFETIME EMPIRE STATISTICS", this::lifetimeStatistics);
            button(page, "♾ CONTINUE DYNASTY", this::continueDynasty);
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
                loyalty = 55;
                lieutenants = 0;
                persist();
                notifyChange("You joined " + gang + " as a Street Recruit.");
                showDashboard();
            })
            .setNegativeButton("CANCEL", null).show();
    }

    private void showMissions() {
        if (prisonYears > 0) { message("Your character is serving a prison sentence. Visit Police Attention & Prison first."); return; }
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
        dominationScore = clamp(dominationScore + 2, 0, 100);
        happiness = clamp(happiness + 2, 0, 100);
        heat = Math.max(0, heat - 2);
        checkPromotion();
        persist();
        notifyChange(outcomes[type] + " Reward: ₹" + reward + ". Reputation +" + rep + ".");
        showDashboard();
    }

    private void expandTerritory() {
        if (prisonYears > 0) { message("Your character is serving a prison sentence. Visit Police Attention & Prison first."); return; }
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
                dominationScore = clamp(dominationScore + 8, 0, 100);
                influence += 12;
                reputation = clamp(reputation + 4, 0, 100);
                checkPromotion();
                persist();
                notifyChange("Your organisation established influence in " + district + ".");
                showDashboard();
            }).show();
    }

    private void rivalChallenge() {
        if (prisonYears > 0) { message("Your character is serving a prison sentence. Visit Police Attention & Prison first."); return; }
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
                    dominationScore = clamp(dominationScore + 5, 0, 100);
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
        if (prisonYears > 0) { message("Your character is serving a prison sentence. Visit Police Attention & Prison first."); return; }
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


    /**
     * Fictional, abstract business-management loop. No real-world criminal
     * methods are described; businesses are represented as strategy-game assets.
     */
    private void businessNetwork() {
        if (prisonYears > 0) {
            message("Your character is serving a sentence. Resolve the prison status before managing the network.");
            return;
        }
        String[] options = {
            "Invest in a fictional business hub (₹3,000)",
            "Upgrade business network (₹5,000)",
            "Collect business income (₹" + (businessIncome * Math.max(1, businessLevel)) + ")",
            "Review business portfolio"
        };
        new AlertDialog.Builder(activity).setTitle("Underground Business Network")
            .setItems(options, (dialog, which) -> {
                if (which == 0) {
                    if (money < 3000) { message("You need ₹3,000 to establish a business hub."); return; }
                    money -= 3000;
                    businessLevel = Math.min(10, businessLevel + 1);
                    businessIncome = Math.max(700, businessIncome + 700);
                    reputation = clamp(reputation + 2, 0, 100);
                    dominationScore = clamp(dominationScore + 3, 0, 100);
                    notifyChange("You established a fictional business hub. Your network now has " + businessLevel + " level(s).");
                } else if (which == 1) {
                    if (businessLevel == 0) { message("Establish a business hub first."); return; }
                    if (money < 5000) { message("You need ₹5,000 for the network upgrade."); return; }
                    money -= 5000;
                    businessLevel = Math.min(10, businessLevel + 1);
                    businessIncome += 900;
                    influence += 6;
                    dominationScore = clamp(dominationScore + 5, 0, 100);
                    notifyChange("Your business network was upgraded and its fictional income potential increased.");
                } else if (which == 2) {
                    if (businessLevel == 0) { message("You do not own a business hub yet."); return; }
                    int income = businessIncome * businessLevel;
                    money += income;
                    notifyChange("Your business network generated ₹" + income + " in game income.");
                } else {
                    message("Business network level: " + businessLevel +
                        "\nIncome per collection: ₹" + (businessIncome * businessLevel) +
                        "\nInfluence: " + influence + "\nReputation: " + reputation);
                }
                persist();
                showDashboard();
            }).setNegativeButton("BACK", (d, w) -> showDashboard()).show();
    }

    private void policeAndPrison() {
        String[] options = prisonYears > 0
            ? new String[]{"Serve one year of your sentence", "Apply for a legal review (₹2,500)", "View legal status"}
            : new String[]{"Keep a low profile", "Hire legal representation (₹2,000)", "View legal status"};
        new AlertDialog.Builder(activity).setTitle("Police Attention & Legal Status")
            .setItems(options, (dialog, which) -> {
                if (prisonYears > 0) {
                    if (which == 0) {
                        prisonYears = Math.max(0, prisonYears - 1);
                        heat = Math.max(0, heat - 12);
                        happiness = Math.max(0, happiness - 3);
                        notifyChange(prisonYears == 0
                            ? "Your character completed the sentence and returned to the city."
                            : "One year served. " + prisonYears + " year(s) remain.");
                    } else if (which == 1) {
                        if (money < 2500) { message("You need ₹2,500 for legal representation."); return; }
                        money -= 2500;
                        if (random.nextInt(100) < 45) {
                            prisonYears = Math.max(0, prisonYears - 1);
                            notifyChange("The legal review reduced your sentence by one year.");
                        } else {
                            notifyChange("The legal review did not change the sentence.");
                        }
                    } else {
                        message("Remaining sentence: " + prisonYears + " year(s)\nPolice heat: " + heat + "/100");
                    }
                } else {
                    if (which == 0) {
                        heat = Math.max(0, heat - 15);
                        reputation = clamp(reputation + 1, 0, 100);
                        notifyChange("You stepped away from the spotlight. Police attention decreased.");
                    } else if (which == 1) {
                        if (money < 2000) { message("You need ₹2,000 for legal representation."); return; }
                        money -= 2000;
                        heat = Math.max(0, heat - 25);
                        notifyChange("Your character received fictional legal advice and police attention decreased.");
                    } else {
                        message("Police heat: " + heat + "/100\n" +
                            (heat >= 75 ? "Status: Critical attention" : heat >= 40 ? "Status: Elevated attention" : "Status: Low attention") +
                            "\nPrison sentence: None");
                        return;
                    }
                    // High heat creates a non-graphic, chance-based story consequence.
                    if (heat >= 85 && random.nextInt(100) < 30) {
                        prisonYears = 1 + random.nextInt(3);
                        heat = 45;
                        notifyChange("A fictional investigation resulted in a prison sentence of " + prisonYears + " years. You can serve time or request a legal review.");
                    }
                }
                persist();
                showDashboard();
            }).setNegativeButton("BACK", (d, w) -> showDashboard()).show();
    }

    private void worldDomination() {
        int score = clamp(dominationScore + rank * 5 + controlledTerritories * 3 +
            rivalWins * 2 + businessLevel * 2, 0, 100);
        String status = score >= 90 && rank >= 7
            ? "MAFIA EMPEROR — You have achieved complete fictional world domination!"
            : score >= 65 ? "International influence is within reach."
            : score >= 35 ? "Your organisation is becoming a major power."
            : "Your empire is still growing. Build influence, reputation and territories.";
        new AlertDialog.Builder(activity).setTitle("👑 Mafia World Domination")
            .setMessage(status + "\n\nDomination rating: " + score + "/100" +
                "\nCurrent rank: " + (gang.isEmpty() ? "No gang" : ranks[Math.min(rank, ranks.length - 1)]) +
                "\nTerritories: " + controlledTerritories + "/10" +
                "\nBusiness levels: " + businessLevel +
                "\nRival victories: " + rivalWins +
                "\n\nReach rank Mafia Emperor and a domination rating of 90 to complete the storyline.")
            .setPositiveButton("OK", null).show();
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


    /**
     * Fictional strategy encounters. Outcomes are abstracted into reputation,
     * morale and influence; no real-world violence or criminal tactics.
     */
    private void bossShowdown() {
        if (prisonYears > 0) { message("Complete your sentence before taking part in a boss showdown."); return; }
        String[] rivals = {"The Iron Crown", "The Eastern Council", "The Sapphire Circle", "The Crimson House"};
        String rival = rivals[Math.min(bossVictories, rivals.length - 1)];
        String[] approaches = {"Negotiation and alliance", "Business leadership contest", "Public reputation challenge"};
        new AlertDialog.Builder(activity).setTitle("Rival Boss: " + rival)
            .setMessage("Choose an abstract strategy challenge. Your reputation, influence and team loyalty affect the result.")
            .setItems(approaches, (d, which) -> {
                int chance = 38 + reputation / 5 + influence / 8 + loyalty / 10 + rank * 3;
                if (which == 0) chance += 12;
                else if (which == 1) chance += 7;
                chance = Math.min(92, chance);
                if (random.nextInt(100) < chance) {
                    bossVictories++;
                    rivalWins++;
                    reputation = clamp(reputation + 8, 0, 100);
                    influence += 12;
                    loyalty = clamp(loyalty + 5, 0, 100);
                    dominationScore = clamp(dominationScore + 8, 0, 100);
                    legacyScore += 10;
                    money += 2500 + bossVictories * 500;
                    checkPromotion();
                    notifyChange("Victory! Your leadership outperformed " + rival + ". Your organisation gained influence, loyalty and a legacy milestone.");
                } else {
                    reputation = Math.max(0, reputation - 4);
                    loyalty = Math.max(0, loyalty - 8);
                    happiness = Math.max(0, happiness - 3);
                    heat = clamp(heat + 4, 0, 100);
                    notifyChange(rival + " won the leadership contest. Your organisation must rebuild morale.");
                }
                persist();
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void familyHierarchy() {
        String[] actions = {
            "Appoint a lieutenant (₹2,000)",
            "Hold a family council",
            "Review loyalty and hierarchy",
            "Recognise a trusted member"
        };
        new AlertDialog.Builder(activity).setTitle("Mafia Family & Loyalty")
            .setItems(actions, (d, which) -> {
                if (which == 0) {
                    if (rank < 2 || money < 2000) {
                        message("Reach Enforcer rank and have ₹2,000 to appoint a lieutenant.");
                        return;
                    }
                    money -= 2000;
                    lieutenants = Math.min(5, lieutenants + 1);
                    loyalty = clamp(loyalty + 7, 0, 100);
                    influence += 5;
                    legacyScore += 2;
                    notifyChange("A trusted lieutenant joined your fictional leadership council.");
                } else if (which == 1) {
                    loyalty = clamp(loyalty + 10, 0, 100);
                    happiness = clamp(happiness + 3, 0, 100);
                    notifyChange("The family council improved communication and team morale.");
                } else if (which == 2) {
                    message("Gang loyalty: " + loyalty + "/100\nLieutenants: " + lieutenants +
                        "\nCurrent rank: " + ranks[Math.min(rank, ranks.length - 1)] +
                        "\n\nLow loyalty can trigger a fictional departure event after a failed boss showdown.");
                    return;
                } else {
                    loyalty = clamp(loyalty + 5, 0, 100);
                    reputation = clamp(reputation + 2, 0, 100);
                    notifyChange("You recognised a trusted member and strengthened the family.");
                }
                persist();
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void safehouseNetwork() {
        String[] actions = {
            "Acquire a safehouse (₹4,000)",
            "Upgrade safehouse network (₹3,500)",
            "Review safehouse assets"
        };
        new AlertDialog.Builder(activity).setTitle("Safehouse Network")
            .setMessage("Safehouses are fictional strategy-game properties that provide stability and legacy points.")
            .setItems(actions, (d, which) -> {
                if (which == 0) {
                    if (safehouseLevel >= 5) { message("Your safehouse network is fully developed."); return; }
                    if (money < 4000) { message("You need ₹4,000."); return; }
                    money -= 4000;
                    safehouseLevel++;
                    loyalty = clamp(loyalty + 3, 0, 100);
                    legacyScore += 4;
                    dominationScore = clamp(dominationScore + 3, 0, 100);
                    notifyChange("You acquired a new fictional safehouse. Your network is now level " + safehouseLevel + ".");
                } else if (which == 1) {
                    if (safehouseLevel == 0) { message("Acquire a safehouse first."); return; }
                    if (money < 3500) { message("You need ₹3,500."); return; }
                    money -= 3500;
                    safehouseLevel = Math.min(5, safehouseLevel + 1);
                    reputation = clamp(reputation + 2, 0, 100);
                    legacyScore += 3;
                    notifyChange("Your safehouse network received a comfort and facilities upgrade.");
                } else {
                    message("Safehouse network level: " + safehouseLevel + "/5\nLeadership council: " + lieutenants +
                        "\nLegacy points: " + legacyScore);
                    return;
                }
                persist();
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void legacyRecord() {
        int total = legacyScore + bossVictories * 5 + lieutenants * 3 + safehouseLevel * 4 +
            completedMissions + controlledTerritories * 2;
        String tier = total >= 150 ? "Legendary Dynasty" : total >= 90 ? "Powerful Family" :
            total >= 40 ? "Rising Legacy" : "Legacy in the Making";
        new AlertDialog.Builder(activity).setTitle("Family Legacy")
            .setMessage("Legacy tier: " + tier + "\nLegacy score: " + total +
                "\nBoss victories: " + bossVictories +
                "\nLieutenants appointed: " + lieutenants +
                "\nSafehouse network: " + safehouseLevel + "/5" +
                "\nMissions completed: " + completedMissions +
                "\nTerritories influenced: " + controlledTerritories +
                "\n\nYour character's achievements are saved as a permanent career record.")
            .setPositiveButton("OK", null).show();
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


    // International expansion is a fictional, non-violent strategy-game system.
    private void internationalNetwork() {
        String[] cities = {"Dubai", "Singapore", "London", "Tokyo", "New York", "Zurich"};
        String[] actions = new String[cities.length + 1];
        for (int i = 0; i < cities.length; i++) {
            actions[i] = (i < internationalCities ? "✓ " : "🌐 ") + cities[i] +
                (i < internationalCities ? " — established" : " — establish network (₹" + (8000 + i * 4000) + ")");
        }
        actions[cities.length] = "Review international network";
        new AlertDialog.Builder(activity).setTitle("International Network")
            .setMessage("Build a fictional global organisation through lawful business partnerships, cultural exchanges and reputation campaigns.")
            .setItems(actions, (d, which) -> {
                if (which == cities.length) {
                    message("International cities: " + internationalCities + "/" + cities.length +
                        "\nGlobal influence: " + globalInfluence +
                        "\nAlliances: " + globalAlliances);
                    return;
                }
                if (which < internationalCities) { message("Your network is already established in " + cities[which] + "."); return; }
                if (which != internationalCities) { message("Expand one city at a time to strengthen the network."); return; }
                final int cost = 8000 + which * 4000;
                new AlertDialog.Builder(activity).setTitle("Establish in " + cities[which])
                    .setMessage("Investment required: ₹" + cost + "\nThis creates a fictional international business and reputation presence.")
                    .setNegativeButton("CANCEL", null).setPositiveButton("INVEST", (x, y) -> {
                        if (money < cost) { message("You need ₹" + cost + "."); return; }
                        money -= cost; internationalCities++; globalInfluence += 10;
                        influence += 5; reputation = clamp(reputation + 3, 0, 100);
                        dominationScore = clamp(dominationScore + 6, 0, 100);
                        legacyScore += 5; persist();
                        notifyChange("Your fictional international network expanded to " + cities[which] + ".");
                        showDashboard();
                    }).show();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void globalAlliancesMenu() {
        String[] partners = {"European Business Council", "Asia-Pacific Trade Forum", "African Development Network", "Americas Leadership Circle", "Global Cultural Foundation"};
        String[] options = new String[partners.length + 1];
        for (int i = 0; i < partners.length; i++)
            options[i] = (i < globalAlliances ? "✓ " : "🤝 ") + partners[i] + (i < globalAlliances ? " — allied" : " — partnership (₹" + (5000 + i * 2500) + ")");
        options[partners.length] = "Review alliance benefits";
        new AlertDialog.Builder(activity).setTitle("Global Alliances")
            .setMessage("Create fictional diplomatic and business partnerships. Each alliance improves global standing.")
            .setItems(options, (d, which) -> {
                if (which == partners.length) {
                    message("Global alliances: " + globalAlliances + "/" + partners.length +
                        "\nGlobal influence: " + globalInfluence +
                        "\nBenefits: reputation, influence and legacy bonuses."); return;
                }
                if (which < globalAlliances) { message("You already have this partnership."); return; }
                if (which != globalAlliances) { message("Build partnerships in sequence to grow your diplomatic standing."); return; }
                int cost = 5000 + which * 2500;
                if (money < cost) { message("You need ₹" + cost + "."); return; }
                money -= cost; globalAlliances++; globalInfluence += 8;
                reputation = clamp(reputation + 4, 0, 100); loyalty = clamp(loyalty + 2, 0, 100);
                dominationScore = clamp(dominationScore + 4, 0, 100); legacyScore += 4;
                persist(); notifyChange("A new global partnership was established with " + partners[which] + ".");
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void globalAssetPortfolio() {
        String[] assets = {"Premium commercial property (₹10,000)", "International hospitality group (₹15,000)",
            "Technology investment fund (₹20,000)", "Cultural and sports ventures (₹12,000)", "Review portfolio"};
        new AlertDialog.Builder(activity).setTitle("Wealth & Asset Portfolio")
            .setMessage("Build a diversified, fictional legal asset portfolio. Assets increase your long-term wealth score.")
            .setItems(assets, (d, which) -> {
                if (which == 4) {
                    message("Portfolio assets: " + assetPortfolio +
                        "\nPortfolio value invested: ₹" + (assetPortfolio * 10000) +
                        "\nGlobal influence: " + globalInfluence +
                        "\nYour portfolio is a game statistic; it does not change real-world finances."); return;
                }
                int[] costs = {10000, 15000, 20000, 12000};
                if (money < costs[which]) { message("You need ₹" + costs[which] + "."); return; }
                money -= costs[which]; assetPortfolio++;
                globalInfluence += 6; reputation = clamp(reputation + 2, 0, 100);
                dominationScore = clamp(dominationScore + 3, 0, 100); legacyScore += 3;
                persist(); notifyChange("You added a diversified asset to your fictional portfolio.");
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void emperorAchievements() {
        int score = internationalCities * 10 + globalAlliances * 8 + assetPortfolio * 5 +
            bossVictories * 5 + lieutenants * 3 + safehouseLevel * 3 + controlledTerritories * 2 + completedMissions;
        String[] names = {"Global Explorer", "Diplomatic Leader", "Wealth Architect", "Territory Strategist", "Mafia Emperor"};
        boolean[] unlocked = {internationalCities >= 3, globalAlliances >= 3, assetPortfolio >= 4,
            controlledTerritories >= 8 && rank >= 5, rank >= 7 &&
            (dominationScore + globalInfluence + internationalCities * 3 + globalAlliances * 2) >= 90};
        StringBuilder report = new StringBuilder("EMPEROR ACHIEVEMENTS\n\n");
        for (int i = 0; i < names.length; i++) report.append(unlocked[i] ? "🏆 " : "🔒 ").append(names[i]).append("\n");
        report.append("\nGlobal score: ").append(score)
            .append("\nInternational cities: ").append(internationalCities).append("/6")
            .append("\nGlobal alliances: ").append(globalAlliances).append("/5")
            .append("\nPortfolio assets: ").append(assetPortfolio)
            .append("\nGlobal influence: ").append(globalInfluence);
        new AlertDialog.Builder(activity).setTitle("Mafia Emperor Achievements")
            .setMessage(report.toString()).setPositiveButton("OK", null).show();
    }

    // Bundle 5: age-based world simulation. Events are fictional and non-violent.
    private void processLivingWorld() {
        int age = save.getInt("age", 0);
        if (gang.isEmpty() || age < 18) return;
        if (lastRivalUpdateAge < 0) lastRivalUpdateAge = age;
        if (age > lastRivalUpdateAge) {
            int years = Math.min(10, age - lastRivalUpdateAge);
            rivalEmpirePower = clamp(rivalEmpirePower + years * (1 + random.nextInt(3)), 5, 100);
            lastRivalUpdateAge = age;
            persist();
        }
        if (lastWorldEventAge != age) {
            lastWorldEventAge = age;
            worldEventsExperienced++;
            int event = random.nextInt(5);
            String headline;
            if (event == 0) {
                int gain = 800 + random.nextInt(1700); money += gain;
                reputation = clamp(reputation + 2, 0, 100);
                headline = "A respected community initiative attracts positive attention. Game funds +₹" + gain + ".";
            } else if (event == 1) {
                rivalEmpirePower = clamp(rivalEmpirePower - 5, 5, 100);
                influence += 4;
                headline = "A rival organisation faces an internal leadership dispute. Your influence grows.";
            } else if (event == 2) {
                int cost = Math.min(money, 1000 + random.nextInt(1500)); money -= cost;
                loyalty = clamp(loyalty + 4, 0, 100);
                headline = "A city-wide economic slowdown affects your fictional business portfolio. Costs: ₹" + cost + ". Team loyalty improves after your support.";
            } else if (event == 3) {
                globalInfluence += 5; reputation = clamp(reputation + 3, 0, 100);
                headline = "An international cultural summit improves your organisation's public reputation and global influence.";
            } else {
                rivalEmpirePower = clamp(rivalEmpirePower + 4, 5, 100);
                heat = Math.max(0, heat - 3);
                headline = "A new rival leadership group emerges. Competition increases, but public attention around you cools.";
            }
            persist();
            new AlertDialog.Builder(activity).setTitle("🌦 WORLD EVENT — AGE " + age)
                .setMessage(headline + "\n\nThe world continues to change while your empire grows.")
                .setPositiveButton("CONTINUE", null).show();
        }
    }

    private void worldEventsReport() {
        new AlertDialog.Builder(activity).setTitle("Dynamic World Events")
            .setMessage("World events experienced: " + worldEventsExperienced +
                "\nCurrent age: " + save.getInt("age", 0) +
                "\nRival empire power: " + rivalEmpirePower + "/100" +
                "\n\nEvents can influence finances, reputation, loyalty, global influence and rival strength. New events are generated as your character ages.")
            .setPositiveButton("OK", null).show();
    }

    private void mafiaElection() {
        if (prisonYears > 0) { message("You cannot campaign while serving a sentence. Resolve your legal status first."); return; }
        if (rank < 4 || reputation < 45 || globalInfluence < 20) {
            message("Election eligibility requires Underboss rank, 45 reputation and 20 global influence.\n\nCurrent rank: " + ranks[Math.min(rank, ranks.length - 1)] +
                "\nReputation: " + reputation + "/100\nGlobal influence: " + globalInfluence + "/20");
            return;
        }
        String[] choices = {"Build a broad alliance campaign (₹5,000)", "Present a strong public leadership record (₹3,000)", "Withdraw and prepare for the next election"};
        new AlertDialog.Builder(activity).setTitle("Global Underworld Leadership Election")
            .setMessage("A fictional, non-violent leadership contest decided by reputation, alliances and influence.\n\nRival candidate strength: " + rivalEmpirePower + "/100")
            .setItems(choices, (d, which) -> {
                if (which == 2) { message("You withdrew from this election and can try again later."); return; }
                int cost = which == 0 ? 5000 : 3000;
                if (money < cost) { message("You need ₹" + cost + " to run this campaign."); return; }
                money -= cost; electionAttempts++;
                int chance = 35 + reputation / 3 + globalInfluence / 4 + globalAlliances * 3 + electionWins * 2 - rivalEmpirePower / 4;
                if (which == 0) chance += 12;
                boolean won = random.nextInt(100) < clamp(chance, 10, 90);
                if (won) {
                    electionWins++; rank = Math.max(rank, 7); globalInfluence += 12;
                    dominationScore = clamp(dominationScore + 15, 0, 100);
                    legacyScore += 12; reputation = clamp(reputation + 5, 0, 100);
                    notifyChange("You won the global leadership election and were recognised as the leading Mafia Emperor. Election victories: " + electionWins + ".");
                } else {
                    reputation = Math.max(0, reputation - 3); rivalEmpirePower = clamp(rivalEmpirePower + 5, 5, 100);
                    notifyChange("You did not win this election. The rival candidate gained momentum; strengthen your reputation and alliances before trying again.");
                }
                persist(); showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void successionMenu() {
        String status = heirName.isEmpty() ? "No heir appointed" : "Named heir: " + heirName;
        new AlertDialog.Builder(activity).setTitle("Generational Succession")
            .setMessage(status + "\nDynasty generations: " + dynastyGenerations +
                "\nSuccession: " + (successionCompleted ? "Completed" : "Not yet completed") +
                "\n\nAppoint a trusted heir now. When your character reaches age 60, you can pass on the family legacy and continue with the next generation.")
            .setPositiveButton(heirName.isEmpty() ? "APPOINT HEIR" : "CHANGE HEIR", (d,w) -> promptHeirName())
            .setNeutralButton("COMPLETE SUCCESSION", (d,w) -> completeSuccession())
            .setNegativeButton("CLOSE", null).show();
    }

    private void promptHeirName() {
        final EditText input = new EditText(activity);
        input.setSingleLine(true); input.setHint("Enter heir's name");
        if (!heirName.isEmpty()) input.setText(heirName);
        new AlertDialog.Builder(activity).setTitle("Appoint Your Heir")
            .setView(input).setNegativeButton("CANCEL", null)
            .setPositiveButton("SAVE HEIR", (d,w) -> {
                String value = input.getText().toString().trim();
                if (value.isEmpty()) { message("Please enter a name for your heir."); return; }
                heirName = value; successionCompleted = false; persist();
                notifyChange(heirName + " has been appointed as the next generation's heir.");
                showDashboard();
            }).show();
    }

    private void completeSuccession() {
        int age = save.getInt("age", 0);
        if (heirName.isEmpty()) { message("Appoint an heir before completing succession."); return; }
        if (age < 60) { message("Your character must reach age 60 before passing on the empire. Current age: " + age + "."); return; }
        if (successionCompleted) { message("This generation has already completed its succession."); return; }
        new AlertDialog.Builder(activity).setTitle("Pass the Legacy to " + heirName + "?")
            .setMessage("Your achievements, family legacy, assets and global standing will be recorded. The next generation will inherit the dynasty.")
            .setNegativeButton("NOT YET", null).setPositiveButton("PASS THE LEGACY", (d,w) -> {
                successionCompleted = true; dynastyGenerations++;
                legacyScore += 20; globalInfluence += 5;
                // The dynasty continues; the new generation starts with a modest leadership reset.
                rank = Math.max(2, rank - 2); reputation = Math.max(25, reputation - 10);
                heirName = heirName + " (Generation " + dynastyGenerations + ")";
                persist(); notifyChange("A new generation has inherited the family legacy. " + heirName + " now leads the continuing dynasty.");
                showDashboard();
            }).show();
    }

    private void rivalEmpireReport() {
        String level = rivalEmpirePower >= 80 ? "Dominant global competitor" : rivalEmpirePower >= 55 ? "Powerful rival network" : rivalEmpirePower >= 30 ? "Growing rival organisation" : "Emerging competitor";
        String[] actions = {"Study rival strengths", "Strengthen your public alliances (₹2,500)", "Compete through a reputation campaign (₹3,500)"};
        new AlertDialog.Builder(activity).setTitle("Rival Empire AI")
            .setMessage(level + "\nRival power: " + rivalEmpirePower + "/100" +
                "\nYour global influence: " + globalInfluence +
                "\n\nRival organisations develop over time as your character ages. Choose peaceful, strategy-based responses.")
            .setItems(actions, (d,which) -> {
                if (which == 0) {
                    message("Rival strength: " + rivalEmpirePower + "/100\nYour advantage: " + (globalInfluence - rivalEmpirePower) + " global influence points.");
                    return;
                }
                int cost = which == 1 ? 2500 : 3500;
                if (money < cost) { message("You need ₹" + cost + "."); return; }
                money -= cost;
                int reduction = which == 1 ? 5 : 8;
                rivalEmpirePower = clamp(rivalEmpirePower - reduction, 5, 100);
                globalInfluence += which == 1 ? 5 : 3;
                reputation = clamp(reputation + 2, 0, 100);
                persist(); notifyChange(which == 1 ? "Your alliances strengthened and rival influence declined." : "Your public reputation campaign reduced the rival network's standing.");
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }


    // Bundle 6: an abstract, choice-driven city map. Territory ownership is
    // represented as game progression and does not describe real-world activity.
    private void livingCityMap() {
        StringBuilder map = new StringBuilder();
        map.append("CITY CONTROL MAP\n\n");
        for (int i = 0; i < districts.length; i++) {
            map.append(i < controlledTerritories ? "🟨 OWNED  " : "⬜ OPEN   ")
                .append(districts[i]).append("\n");
        }
        map.append("\nOwned districts: ").append(controlledTerritories)
            .append("/").append(districts.length)
            .append("\nGlobal cities: ").append(internationalCities).append("/6")
            .append("\nRival power: ").append(rivalEmpirePower).append("/100");
        String[] options = {"Review city map", "Develop the next open district (₹4,000)", "View international footprint"};
        new AlertDialog.Builder(activity).setTitle("🗺 Living World Map")
            .setMessage(map.toString())
            .setItems(options, (d, which) -> {
                if (which == 0) { livingCityMap(); return; }
                if (which == 2) {
                    message("International cities established: " + internationalCities + "/6"
                        + "\nGlobal alliances: " + globalAlliances + "/5"
                        + "\nGlobal influence: " + globalInfluence);
                    return;
                }
                if (controlledTerritories >= districts.length) {
                    message("You have completed the available city map."); return;
                }
                if (money < 4000) { message("You need ₹4,000 to develop the next district."); return; }
                money -= 4000;
                controlledTerritories++;
                influence += 5;
                dominationScore = clamp(dominationScore + 4, 0, 100);
                persist();
                notifyChange("You developed " + districts[controlledTerritories - 1]
                    + " through a public-facing business and community investment.");
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void familyRelationships() {
        String familyStatus = heirName.isEmpty() ? "No named heir" : "Heir: " + heirName;
        String[] choices = {"Spend time with family (₹500)", "Support a family member (₹1,500)", "Hold a family council", "Review relationships"};
        new AlertDialog.Builder(activity).setTitle("Family Relationships")
            .setMessage(familyStatus + "\nFamily bond: " + familyBond + "/100"
                + "\nDynasty generations: " + dynastyGenerations
                + "\n\nYour decisions shape trust and succession stability.")
            .setItems(choices, (d, which) -> {
                if (which == 3) {
                    message("Family bond: " + familyBond + "/100\nHeir: "
                        + (heirName.isEmpty() ? "Not appointed" : heirName)
                        + "\nSuccession completed: " + (successionCompleted ? "Yes" : "No"));
                    return;
                }
                if (which == 0 || which == 1) {
                    int cost = which == 0 ? 500 : 1500;
                    if (money < cost) { message("You need ₹" + cost + "."); return; }
                    money -= cost;
                    familyBond = clamp(familyBond + (which == 0 ? 6 : 12), 0, 100);
                    happiness = clamp(happiness + 3, 0, 100);
                    lastStoryChoice = which == 0 ? "Made time for family" : "Supported a family member";
                    storyChoicesMade++;
                    persist();
                    notifyChange(which == 0 ? "You made time for family. Trust improved." : "Your support strengthened family relationships.");
                    showDashboard();
                    return;
                }
                int change = familyBond >= 60 ? 5 : 10;
                familyBond = clamp(familyBond + change, 0, 100);
                loyalty = clamp(loyalty + 2, 0, 100);
                lastStoryChoice = "Held a family council";
                storyChoicesMade++;
                persist();
                notifyChange("A calm family council helped resolve disagreements and strengthen trust.");
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void empireEconomyReport() {
        int gross = businessIncome * businessLevel;
        int portfolioValue = assetPortfolio * 5000;
        int estimatedExpenses = businessLevel * 250 + internationalCities * 350
            + safehouseLevel * 150 + lieutenants * 100;
        int net = gross - estimatedExpenses;
        economyReports++;
        persist();
        new AlertDialog.Builder(activity).setTitle("Empire Economy — Financial Report")
            .setMessage("BUSINESS & ASSET SUMMARY\n"
                + "Business hubs: " + businessLevel + "/10\n"
                + "Estimated business revenue: ₹" + gross + "\n"
                + "Estimated operating expenses: ₹" + estimatedExpenses + "\n"
                + "Estimated operating balance: ₹" + net + "\n\n"
                + "International cities: " + internationalCities + "\n"
                + "Asset portfolio units: " + assetPortfolio + "\n"
                + "Estimated portfolio value: ₹" + portfolioValue + "\n"
                + "Cash on hand: ₹" + money + "\n\n"
                + "This is an in-game estimate, not real financial advice.")
            .setPositiveButton("CLOSE", null).show();
    }

    private void worldRanking() {
        int score = reputation * 2 + globalInfluence * 3 + controlledTerritories * 8
            + internationalCities * 12 + globalAlliances * 7 + businessLevel * 4
            + electionWins * 15 + dynastyGenerations * 5 + dominationScore;
        int rivalScore = rivalEmpirePower * 2 + 40;
        String rankLabel = score >= rivalScore + 100 ? "Global #1 — Legendary" :
            score >= rivalScore + 30 ? "Global #2 — Elite" :
            score >= rivalScore ? "Global #3 — Rising" : "Global #4 — Challenger";
        new AlertDialog.Builder(activity).setTitle("🥇 Global Empire Ranking")
            .setMessage(rankLabel + "\n\nYour empire score: " + score
                + "\nRival network score: " + rivalScore
                + "\nReputation: " + reputation + "/100"
                + "\nGlobal influence: " + globalInfluence
                + "\nTerritories: " + controlledTerritories + "/10"
                + "\nInternational cities: " + internationalCities + "/6"
                + "\nElection wins: " + electionWins
                + "\nDynasty generations: " + dynastyGenerations
                + "\n\nRankings update as your empire develops.")
            .setPositiveButton("OK", null).show();
    }

    private void dynamicStoryChoices() {
        String[] events = {
            "A trusted lieutenant wants to retire and return to family life.",
            "A rival organisation proposes a peaceful business competition.",
            "Your heir asks for a greater role in the family legacy.",
            "A city community group requests support for a public project."
        };
        String event = events[random.nextInt(events.length)];
        String[] options = {"Choose a people-first response", "Choose a business-focused response", "Delay the decision"};
        new AlertDialog.Builder(activity).setTitle("📖 A New Chapter")
            .setMessage(event + "\n\nYour choice will affect family trust, reputation and the organisation's future.")
            .setItems(options, (d, which) -> {
                if (which == 2) {
                    lastStoryChoice = "Delayed: " + event;
                    message("You decided to revisit this situation later.");
                } else if (which == 0) {
                    familyBond = clamp(familyBond + 8, 0, 100);
                    loyalty = clamp(loyalty + 5, 0, 100);
                    happiness = clamp(happiness + 2, 0, 100);
                    reputation = clamp(reputation + 2, 0, 100);
                    lastStoryChoice = "People-first: " + event;
                    notifyChange("Your people-first decision strengthened relationships and public trust.");
                } else {
                    if (money < 1000) { message("You need ₹1,000 for the business-focused response."); return; }
                    money -= 1000;
                    influence += 4;
                    globalInfluence += 2;
                    lastStoryChoice = "Business-focused: " + event;
                    notifyChange("Your business-focused decision improved influence and global standing.");
                }
                storyChoicesMade++;
                persist();
                showDashboard();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    // Bundle 7: reputation is multidimensional rather than a single score.
    private void characterReputation() {
        String status = "Respect: " + respect + "/100\\nFear: " + fear + "/100\\nPopularity: "
            + popularity + "/100\\nLoyalty: " + loyalty + "/100\\nPublic reputation: " + reputation + "/100";
        new AlertDialog.Builder(activity).setTitle("🎭 Character Reputation")
            .setMessage(status + "\\n\\nChoose the leadership style you want to develop.")
            .setItems(new String[]{"Earn respect through fair leadership", "Build a feared but unstable image", "Improve popularity through community support"}, (d, which) -> {
                if (which == 0) {
                    respect = clamp(respect + 8, 0, 100);
                    loyalty = clamp(loyalty + 4, 0, 100);
                    reputation = clamp(reputation + 2, 0, 100);
                } else if (which == 1) {
                    fear = clamp(fear + 8, 0, 100);
                    popularity = clamp(popularity - 3, 0, 100);
                    loyalty = clamp(loyalty - 2, 0, 100);
                } else {
                    if (money < 750) { message("You need ₹750 for the community initiative."); return; }
                    money -= 750;
                    popularity = clamp(popularity + 8, 0, 100);
                    respect = clamp(respect + 3, 0, 100);
                    reputation = clamp(reputation + 3, 0, 100);
                    happiness = clamp(happiness + 2, 0, 100);
                }
                permanentDecisions++;
                persist();
                notifyChange("Your leadership style changed how people see your character.");
                showDashboard();
            }).setNegativeButton("CLOSE", (d,w) -> showDashboard()).show();
    }

    private void mafiaFamilyTree() {
        String tree = "MAFIA FAMILY TREE\\n"
            + "Parent 1: " + familyParent1 + "\\nParent 2: " + familyParent2
            + "\\nSibling: " + familySibling + "\\nChild / Heir: " + familyChild
            + "\\nNamed successor: " + (heirName == null || heirName.isEmpty() ? "Not selected" : heirName)
            + "\\nDynasty generation: " + dynastyGenerations;
        String[] options = {"Add / update parent 1", "Add / update parent 2", "Add / update sibling", "Add / update child"};
        new AlertDialog.Builder(activity).setTitle("🌳 Family Tree").setMessage(tree)
            .setItems(options, (d, which) -> {
                EditText input = new EditText(activity);
                input.setSingleLine(true);
                input.setHint("Enter family member name");
                new AlertDialog.Builder(activity).setTitle("Family member name").setView(input)
                    .setPositiveButton("SAVE", (x,y) -> {
                        String name = input.getText().toString().trim();
                        if (name.isEmpty()) { message("Please enter a name."); return; }
                        if (which == 0) familyParent1 = name;
                        else if (which == 1) familyParent2 = name;
                        else if (which == 2) familySibling = name;
                        else { familyChild = name; if (heirName == null || heirName.isEmpty()) heirName = name; }
                        familyBond = clamp(familyBond + 2, 0, 100);
                        persist();
                        showDashboard();
                    }).setNegativeButton("CANCEL", (x,y) -> mafiaFamilyTree()).show();
            }).setNegativeButton("BACK", (d,w) -> showDashboard()).show();
    }

    private void rivalPersonalityReport() {
        String[] personalities = {"Aggressive", "Strategic", "Diplomatic", "Unpredictable"};
        String[] descriptions = {
            "Aggressive rivals compete quickly and create sudden pressure.",
            "Strategic rivals grow steadily and prioritise influence.",
            "Diplomatic rivals seek alliances and avoid unnecessary conflict.",
            "Unpredictable rivals change their priorities often."
        };
        if (random.nextInt(100) < 35) {
            rivalPersonality = personalities[random.nextInt(personalities.length)];
            persist();
        }
        int index = 0;
        for (int i = 0; i < personalities.length; i++) if (personalities[i].equals(rivalPersonality)) index = i;
        new AlertDialog.Builder(activity).setTitle("🧠 Rival Boss Profile")
            .setMessage("Current rival style: " + rivalPersonality + "\\n\\n" + descriptions[index]
                + "\\n\\nRival power: " + rivalEmpirePower + "/100"
                + "\\nYour influence: " + globalInfluence)
            .setPositiveButton("OK", null).show();
    }

    private void majorLifeDecisions() {
        String[] choices = {
            "Prioritise family stability",
            "Invest in legitimate businesses",
            "Seek reconciliation with a rival",
            "Focus on personal wellbeing"
        };
        new AlertDialog.Builder(activity).setTitle("⚖ A Defining Life Decision")
            .setMessage("This decision becomes part of your permanent life record. Choose carefully.")
            .setItems(choices, (d, which) -> {
                if (which == 0) {
                    familyBond = clamp(familyBond + 12, 0, 100);
                    loyalty = clamp(loyalty + 5, 0, 100);
                    happiness = clamp(happiness + 5, 0, 100);
                } else if (which == 1) {
                    if (money < 1500) { message("You need ₹1,500 to make this investment."); return; }
                    money -= 1500;
                    businessLevel = clamp(businessLevel + 1, 0, 10);
                    businessIncome += 250;
                    reputation = clamp(reputation + 3, 0, 100);
                } else if (which == 2) {
                    rivalEmpirePower = clamp(rivalEmpirePower - 5, 0, 100);
                    globalAlliances = clamp(globalAlliances + 1, 0, 10);
                    respect = clamp(respect + 4, 0, 100);
                } else {
                    health = clamp(health + 8, 0, 100);
                    happiness = clamp(happiness + 8, 0, 100);
                    fear = clamp(fear - 2, 0, 100);
                }
                permanentDecisions++;
                lastStoryChoice = "Major decision: " + choices[which];
                persist();
                notifyChange("Your decision has been added to your permanent life history.");
                showDashboard();
            }).setNegativeButton("LATER", (d,w) -> showDashboard()).show();
    }

    private void lifetimeStatistics() {
        int score = reputation * 2 + globalInfluence * 3 + controlledTerritories * 8
            + internationalCities * 12 + electionWins * 15 + legacyScore + dominationScore;
        new AlertDialog.Builder(activity).setTitle("📈 Lifetime Empire Statistics")
            .setMessage("Peak cash recorded: ₹" + Math.max(lifetimeWealthPeak, money)
                + "\\nPeak global influence: " + Math.max(lifetimeInfluencePeak, globalInfluence)
                + "\\nMost territories held: " + Math.max(lifetimeTerritoriesPeak, controlledTerritories)
                + "\\nMissions completed: " + completedMissions
                + "\\nBoss victories: " + bossVictories
                + "\\nElection victories: " + electionWins
                + "\\nPermanent decisions: " + permanentDecisions
                + "\\nDynasty generations: " + dynastyGenerations
                + "\\nLifetime empire score: " + score)
            .setPositiveButton("CLOSE", null).show();
    }

    private void continueDynasty() {
        if (heirName == null || heirName.trim().isEmpty()) {
            message("First select an heir in the Heir & Generational Succession section.");
            return;
        }
        new AlertDialog.Builder(activity).setTitle("♾ Continue the Dynasty?")
            .setMessage("Record " + heirName + " as the next generation's leader? Your current empire records will remain saved.")
            .setPositiveButton("CONTINUE", (d,w) -> {
                dynastyGenerations++;
                successionCompleted = true;
                legacyScore += 25;
                respect = clamp(respect + 3, 0, 100);
                persist();
                notifyChange(heirName + " has been recorded as the next generation's leader. Dynasty generation " + dynastyGenerations + " begins.");
                showDashboard();
            }).setNegativeButton("CANCEL", null).show();
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
