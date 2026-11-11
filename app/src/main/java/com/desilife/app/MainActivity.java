package com.desilife.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {

    int gold = Color.rgb(226, 184, 105);
    int background = Color.rgb(12, 25, 23);
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        showHome();
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(radius);
        return bg;
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    void addText(String value, int size, int color) {
        TextView t = text(value, size, color);
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(
                -1, -2);
        p.setMargins(0, 12, 0, 12);
        root.addView(t, p);
    }

    void addButton(String title, Runnable action) {
        Button b = new Button(this);
        b.setText(title);
        b.setTextColor(background);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackground(shape(gold, 28));

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(
                -1, 58);
        p.setMargins(0, 12, 0, 12);

        root.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    void setup() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(28, 35, 28, 35);
        root.setBackgroundColor(background);

        setContentView(root);
    }

    void showHome() {
        setup();

        addText("🇮🇳", 48, gold);

        TextView title = text("DESILIFE", 38, gold);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        addText("APNI ZINDAGI, APNE FAISLE", 13,
            Color.LTGRAY);

        addText("━━━━━━━━━━━━━━━━━━━━", 18, gold);

        addText("Your Life. Your Story.", 24, Color.WHITE);

        addText(
            "Born in India. Raised by choices. " +
            "Build your own destiny.",
            16, Color.LTGRAY
        );

        addText("━━━━━━━━━━━━━━━━━━━━", 18, gold);

        addButton("✦  START A NEW LIFE", () -> showCreate());

        addButton("CONTINUE GAME", () -> showMessage(
            "No saved life yet. Start your first life!"
        ));

        addText("AN INDIAN LIFE SIMULATION", 12, gold);

        addText("Version 1.0", 11, Color.GRAY);
    }

    void showMessage(String message) {
        new android.app.AlertDialog.Builder(this)
            .setTitle("DesiLife")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }

    void showCreate() {
        setup();

        addText("CREATE YOUR LIFE", 27, gold);

        addText(
            "Your journey begins here.\n" +
            "Every choice shapes your future.",
            16, Color.WHITE
        );

        addButton("BEGIN", () -> showMessage(
            "Character creation is coming next!"
        ));

        addButton("BACK", () -> showHome());
    }
}
