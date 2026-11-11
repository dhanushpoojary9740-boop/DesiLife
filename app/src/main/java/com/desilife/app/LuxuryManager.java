package com.desilife.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class LuxuryManager {

    private final Activity activity;
    private final SharedPreferences save;
    private int cash;
    private AlertDialog dialog;

    private final int gold = Color.rgb(226,184,105);
    private final int bg = Color.rgb(12,25,23);
    private final int card = Color.rgb(35,45,62);
    private final int white = Color.WHITE;

    public interface MoneyListener {
        void onMoneyChanged(int amount);
    }

    private final MoneyListener listener;

    private static final String PROPERTY_KEY = "luxury_properties";
    private static final String VEHICLE_KEY = "luxury_vehicles";

    private final String[] propertyNames = {
        "Premium Apartment",
        "Luxury Apartment",
        "Sea View Apartment",
        "Luxury Villa",
        "Penthouse",
        "Royal Mansion",
        "Palace Estate"
    };

    private final int[] propertyPrices = {
        4500000,8500000,15000000,25000000,
        50000000,120000000,500000000
    };

    private final int[] propertyRent = {
        18000,35000,65000,100000,
        200000,500000,1500000
    };

    private final int[] propertyMaintenance = {
        3000,5000,9000,15000,25000,60000,180000
    };

    private final String[] vehicleNames = {
        "Maruti Swift",
        "Hyundai Creta",
        "Mahindra Scorpio N",
        "Toyota Fortuner",
        "BMW 3 Series",
        "Mercedes-Benz E-Class",
        "Porsche 911",
        "Range Rover",
        "Lamborghini Huracan",
        "Rolls-Royce Ghost"
    };

    private final int[] vehiclePrices = {
        800000,1600000,2200000,4500000,7500000,
        9500000,20000000,25000000,40000000,80000000
    };

    private final int[] vehicleMaintenance = {
        1500,3000,4000,8000,12000,
        18000,30000,40000,60000,100000
    };

    public LuxuryManager(
        Activity activity,
        SharedPreferences save,
        int cash,
        MoneyListener listener
    ) {
        this.activity = activity;
        this.save = save;
        this.cash = Math.max(0,cash);
        this.listener = listener;
    }

    public void setCash(int amount) {
        cash = Math.max(0,amount);
    }

    public int getCash() {
        return cash;
    }

    private int safeInt(long value) {
        return (int)Math.max(
            0,Math.min(Integer.MAX_VALUE,value)
        );
    }

    private String money(long amount) {
        return "₹"+String.format(
            java.util.Locale.US,"%,d",amount
        );
    }

    private GradientDrawable shape(int color,int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(
        String value,int size,int color,boolean bold
    ) {
        TextView t = new TextView(activity);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(10,11,10,11);

        if(bold) t.setTypeface(null,Typeface.BOLD);

        return t;
    }

    private void heading(LinearLayout root,String title) {
        TextView t = text(title,19,gold,true);
        t.setGravity(Gravity.LEFT);
        root.addView(t);
    }

    private void button(
        LinearLayout root,String title,Runnable action
    ) {
        TextView b = text(title,14,bg,true);
        b.setBackground(shape(gold,18));

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,5,0,5);
        root.addView(b,p);

        b.setOnClickListener(v -> action.run());
    }

    private void safePut(
        JSONObject object,String key,Object value
    ) {
        try {
            object.put(key,value);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Safely replace an existing JSONArray element.
    private void safeArrayPut(
        JSONArray array,int index,Object value
    ) {
        try {
            array.put(index,value);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray getArray(String key) {
        try {
            return new JSONArray(save.getString(key,"[]"));
        } catch(Exception e) {
            return new JSONArray();
        }
    }

    private void storeArray(String key,JSONArray array) {
        save.edit().putString(key,array.toString()).apply();
    }

    private JSONObject createAsset(
        String name,int price,int income,
        int maintenance,boolean property
    ) {
        JSONObject item = new JSONObject();

        safePut(item,"name",name);
        safePut(item,"purchasePrice",price);
        safePut(item,"value",price);
        safePut(item,"income",income);
        safePut(item,"maintenance",maintenance);
        safePut(item,"level",1);
        safePut(item,"property",property);

        return item;
    }

    private void updateMoney() {
        cash = Math.max(0,cash);

        save.edit().putInt("money",cash).apply();

        if(listener!=null) {
            listener.onMoneyChanged(cash);
        }
    }

    public void open() {
        show();
    }

    private void show() {
        ScrollView scroll = new ScrollView(activity);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16,16,16,24);
        root.setBackgroundColor(bg);

        scroll.addView(root);

        heading(root,"👑 DESILIFE LUXURY EMPIRE");

        root.addView(text(
            "Build your dream lifestyle",14,white,false
        ));

        LinearLayout balance = new LinearLayout(activity);
        balance.setOrientation(LinearLayout.VERTICAL);
        balance.setPadding(14,12,14,12);
        balance.setBackground(shape(card,18));

        balance.addView(text(
            "AVAILABLE CASH",13,Color.LTGRAY,false
        ));

        balance.addView(text(
            money(cash),27,gold,true
        ));

        root.addView(balance);

        heading(root,"🏠 REAL ESTATE");

        JSONArray properties = getArray(PROPERTY_KEY);

        root.addView(text(
            "Owned properties: "+properties.length(),
            14,white,false
        ));

        for(int i=0;i<properties.length();i++) {
            JSONObject item = properties.optJSONObject(i);
            if(item!=null) showAssetCard(root,item,true,i);
        }

        button(root,"🏡 EXPLORE PROPERTY MARKET",
            () -> propertyMarket());

        heading(root,"🚘 LUXURY GARAGE");

        JSONArray vehicles = getArray(VEHICLE_KEY);

        root.addView(text(
            "Owned vehicles: "+vehicles.length(),
            14,white,false
        ));

        for(int i=0;i<vehicles.length();i++) {
            JSONObject item = vehicles.optJSONObject(i);
            if(item!=null) showAssetCard(root,item,false,i);
        }

        button(root,"🚗 EXPLORE VEHICLE SHOWROOM",
            () -> vehicleMarket());

        heading(root,"💰 LUXURY FINANCIAL REPORT");

        long propertyValue = totalValue(properties);
        long vehicleValue = totalValue(vehicles);
        long rent = totalIncome(properties);
        long propertyCosts = totalMaintenance(properties);
        long vehicleCosts = totalMaintenance(vehicles);

        root.addView(text(
            "Property value: "+money(propertyValue)+
            "\nVehicle value: "+money(vehicleValue)+
            "\nMonthly rental income: "+money(rent)+
            "\nMonthly property maintenance: "+money(propertyCosts)+
            "\nMonthly vehicle maintenance: "+money(vehicleCosts)+
            "\nTotal luxury net worth: "+
            money(propertyValue+vehicleValue),
            15,white,false
        ));

        root.addView(text(
            "Monthly luxury cash flow: "+
            money(rent-propertyCosts-vehicleCosts),
            17,gold,true
        ));

        button(root,"CLOSE",() -> {
            if(dialog!=null) dialog.dismiss();
        });

        dialog = new AlertDialog.Builder(activity)
            .setView(scroll)
            .create();

        dialog.show();

        if(dialog.getWindow()!=null) {
            dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
            );
        }
    }

    private long totalValue(JSONArray array) {
        long total=0;

        for(int i=0;i<array.length();i++) {
            JSONObject item=array.optJSONObject(i);
            if(item!=null) total+=item.optLong("value",0);
        }

        return total;
    }

    private long totalIncome(JSONArray array) {
        long total=0;

        for(int i=0;i<array.length();i++) {
            JSONObject item=array.optJSONObject(i);
            if(item!=null) total+=item.optLong("income",0);
        }

        return total;
    }

    private long totalMaintenance(JSONArray array) {
        long total=0;

        for(int i=0;i<array.length();i++) {
            JSONObject item=array.optJSONObject(i);
            if(item!=null) total+=item.optLong("maintenance",0);
        }

        return total;
    }

    private void showAssetCard(
        LinearLayout root,JSONObject item,
        boolean property,int index
    ) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(12,10,12,10);
        box.setBackground(shape(card,16));

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);

        p.setMargins(0,5,0,8);
        root.addView(box,p);

        String name=item.optString("name","Asset");
        long value=item.optLong("value",0);
        long income=item.optLong("income",0);
        long maintenance=item.optLong("maintenance",0);
        int level=item.optInt("level",1);

        box.addView(text(
            (property?"🏡 ":"🚘 ")+name,17,gold,true
        ));

        box.addView(text(
            "Current value: "+money(value)+
            "\nLevel: "+level+"/5"+
            (property?"\nMonthly rent: "+money(income):"")+
            "\nMonthly maintenance: "+money(maintenance),
            14,white,false
        ));

        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        TextView upgrade=text("UPGRADE",12,bg,true);
        upgrade.setBackground(shape(gold,12));

        TextView sell=text("SELL",12,bg,true);
        sell.setBackground(shape(gold,12));

        actions.addView(upgrade,
            new LinearLayout.LayoutParams(0,-2,1));

        actions.addView(sell,
            new LinearLayout.LayoutParams(0,-2,1));

        box.addView(actions);

        upgrade.setOnClickListener(v ->
            upgradeAsset(property,index));

        sell.setOnClickListener(v ->
            sellAsset(property,index));
    }

    private void propertyMarket() {
        String[] descriptions=new String[propertyNames.length];

        for(int i=0;i<propertyNames.length;i++) {
            descriptions[i]=propertyNames[i]+
                "\nPrice: "+money(propertyPrices[i])+
                "\nMonthly rent: "+money(propertyRent[i])+
                "\nMaintenance: "+money(propertyMaintenance[i]);
        }

        new AlertDialog.Builder(activity)
            .setTitle("🏠 Luxury Property Market")
            .setItems(descriptions,(d,index) ->
                confirmPurchase(
                    true,propertyNames[index],
                    propertyPrices[index],propertyRent[index],
                    propertyMaintenance[index]
                ))
            .setNegativeButton("CLOSE",null)
            .show();
    }

    private void vehicleMarket() {
        String[] descriptions=new String[vehicleNames.length];

        for(int i=0;i<vehicleNames.length;i++) {
            descriptions[i]=vehicleNames[i]+
                "\nPrice: "+money(vehiclePrices[i])+
                "\nMonthly maintenance: "+
                money(vehicleMaintenance[i]);
        }

        new AlertDialog.Builder(activity)
            .setTitle("🚘 Luxury Vehicle Showroom")
            .setItems(descriptions,(d,index) ->
                confirmPurchase(
                    false,vehicleNames[index],
                    vehiclePrices[index],0,
                    vehicleMaintenance[index]
                ))
            .setNegativeButton("CLOSE",null)
            .show();
    }

    private void confirmPurchase(
        boolean property,String name,
        int price,int income,int maintenance
    ) {
        new AlertDialog.Builder(activity)
            .setTitle("Confirm Purchase")
            .setMessage(
                "Asset: "+name+
                "\nPurchase price: "+money(price)+
                "\nMonthly income: "+money(income)+
                "\nMonthly maintenance: "+money(maintenance)+
                "\n\nAvailable cash: "+money(cash)
            )
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("BUY",(d,w) -> {

                if(cash<price) {
                    message("Insufficient funds!\n\nYou need "+
                        money((long)price-cash)+" more.");
                    return;
                }

                cash-=price;

                String key=property?PROPERTY_KEY:VEHICLE_KEY;
                JSONArray array=getArray(key);

                array.put(createAsset(
                    name,price,income,maintenance,property
                ));

                storeArray(key,array);
                updateMoney();

                addHistory(
                    "Purchased "+name+" for "+money(price)
                );

                message("Congratulations!\n\nYou now own "+name+"!");
                show();
            })
            .show();
    }

    private void upgradeAsset(boolean property,int index) {
        String key=property?PROPERTY_KEY:VEHICLE_KEY;
        JSONArray array=getArray(key);

        JSONObject item=array.optJSONObject(index);
        if(item==null) return;

        int level=item.optInt("level",1);

        if(level>=5) {
            message("This asset has reached maximum level.");
            return;
        }

        long upgradeCost=Math.max(
            100000L,
            item.optLong("purchasePrice",0)*(level+1)*15L/100L
        );

        if(upgradeCost>Integer.MAX_VALUE || cash<upgradeCost) {
            message("Insufficient funds for this upgrade.\n"+
                "Required: "+money(upgradeCost));
            return;
        }

        new AlertDialog.Builder(activity)
            .setTitle("Premium Upgrade")
            .setMessage(
                "Asset: "+item.optString("name")+
                "\nCurrent level: "+level+
                "\nNew level: "+(level+1)+
                "\nUpgrade cost: "+money(upgradeCost)+
                (property?
                    "\nRental income increases by 15%.":
                    "\nVehicle value increases by 10%.")
            )
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("UPGRADE",(d,w) -> {

                cash-=(int)upgradeCost;

                safePut(item,"level",level+1);

                if(property) {
                    safePut(item,"income",
                        Math.round(item.optLong("income")*1.15));

                    safePut(item,"value",
                        Math.round(item.optLong("value")*1.10));
                } else {
                    safePut(item,"value",
                        Math.round(item.optLong("value")*1.10));
                }

                safeArrayPut(array,index,item);

                storeArray(key,array);
                updateMoney();

                addHistory(
                    "Upgraded "+item.optString("name")+
                    " to level "+(level+1)
                );

                message("Upgrade completed successfully!");
                show();
            })
            .show();
    }

    private void sellAsset(boolean property,int index) {
        String key=property?PROPERTY_KEY:VEHICLE_KEY;
        JSONArray array=getArray(key);

        JSONObject item=array.optJSONObject(index);
        if(item==null) return;

        long value=item.optLong("value",0);
        long resale=Math.round(value*(property?0.90:0.80));

        new AlertDialog.Builder(activity)
            .setTitle("Sell Asset")
            .setMessage(
                "Asset: "+item.optString("name")+
                "\nCurrent value: "+money(value)+
                "\nResale offer: "+money(resale)+
                "\n\nDo you want to sell?"
            )
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("SELL",(d,w) -> {

                cash=safeInt((long)cash+resale);

                JSONArray updated=new JSONArray();

                for(int i=0;i<array.length();i++) {
                    if(i!=index) {
                        updated.put(array.optJSONObject(i));
                    }
                }

                storeArray(key,updated);
                updateMoney();

                addHistory(
                    "Sold "+item.optString("name")+
                    " for "+money(resale)
                );

                message("Asset sold successfully.");
                show();
            })
            .show();
    }

    // Call once when the player ages up by one year.
    public int applyAnnualCosts() {
        JSONArray properties=getArray(PROPERTY_KEY);
        JSONArray vehicles=getArray(VEHICLE_KEY);

        long annualRent=totalIncome(properties)*12L;
        long propertyCosts=totalMaintenance(properties)*12L;
        long vehicleCosts=totalMaintenance(vehicles)*12L;

        long net=annualRent-propertyCosts-vehicleCosts;

        cash=safeInt((long)cash+net);

        updateAssetValues(properties,true);
        updateAssetValues(vehicles,false);

        storeArray(PROPERTY_KEY,properties);
        storeArray(VEHICLE_KEY,vehicles);

        updateMoney();

        if(net!=0) {
            addHistory(
                "Annual luxury finances: income "+
                money(annualRent)+", expenses "+
                money(propertyCosts+vehicleCosts)
            );
        }

        return cash;
    }

    private void updateAssetValues(
        JSONArray array,boolean property
    ) {
        for(int i=0;i<array.length();i++) {
            JSONObject item=array.optJSONObject(i);
            if(item==null) continue;

            long value=item.optLong("value",0);
            value=Math.round(value*(property?1.025:0.92));

            safePut(item,"value",value);
            safeArrayPut(array,i,item);
        }
    }

    private void addHistory(String event) {
        String old=save.getString("life_events","");

        String entry="Age "+
            save.getInt("age",0)+" • "+event;

        String updated=old.isEmpty()?
            entry:old+"\n"+entry;

        String[] lines=updated.split("\n");

        if(lines.length>150) {
            StringBuilder b=new StringBuilder();

            for(int i=lines.length-150;i<lines.length;i++) {
                if(b.length()>0) b.append("\n");
                b.append(lines[i]);
            }

            updated=b.toString();
        }

        save.edit().putString("life_events",updated).apply();
    }

    private void message(String msg) {
        new AlertDialog.Builder(activity)
            .setTitle("DesiLife Luxury")
            .setMessage(msg)
            .setPositiveButton("OK",null)
            .show();
    }
}
