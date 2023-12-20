package com.example.travelplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TripPlan implements Parcelable {
    private int id;
    private String planName;
    private String startDate;
    private String endDate;
    private List<Destination> destinations;

    public TripPlan(Context context, String planName, String startDate, String endDate, List<Destination> destinations){
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinations = destinations;

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", planName);
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        values.put("destinations", getDestinationString());
        database.insert("trips", null, values);

        database.close();
        helper.close();

    }

    public TripPlan(Context context, int id){
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        this.destinations = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM trips WHERE id="+id, null);
        if(cursor.moveToFirst()){
            this.planName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            this.startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
            this.endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));

            String destinations = cursor.getString(cursor.getColumnIndexOrThrow("destinations"));
            String[] arr = destinations.split(",");
            for(String dest : arr){
                dest= dest.trim();
                this.destinations.add(new Destination(context, dest));
            }

        }
    }

    public List<Destination> getDestinations(){return this.destinations;}
    public TripPlan(Context context, String name){
        this.destinations = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String destStr;
        Cursor cursor = db.rawQuery("SELECT * FROM trips WHERE name = '" + name+ "'", null);
        if (cursor.moveToFirst()) {
            this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            this.planName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            this.startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
            this.endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));
            destStr = cursor.getString(cursor.getColumnIndexOrThrow("destinations"));
            String[] arr = destStr.split(",");
            for(String dest : arr){
                dest= dest.trim();
                this.destinations.add(new Destination(context, dest));
            }

        }
        cursor.close();
        db.close();
    }

    public void setDestinations(List<Destination> destinations){
        this.destinations = destinations;
    }

    protected TripPlan(Parcel in) {
        id = in.readInt();
        planName = in.readString();
        startDate = in.readString();
        endDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(planName);
        dest.writeString(startDate);
        dest.writeString(endDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripPlan> CREATOR = new Creator<TripPlan>() {
        @Override
        public TripPlan createFromParcel(Parcel in) {
            return new TripPlan(in);
        }

        @Override
        public TripPlan[] newArray(int size) {
            return new TripPlan[size];
        }
    };

    public String getName(){
        return this.planName;
    }

    public int getId(){
        return this.id;
    }

    public String getDate(){
        return this.startDate + " to " + this.endDate;
    }

    public String getStartDate(){return this.startDate;}
    public String getEndDate() {return this.endDate;}

    public int getDuration() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        try {
            Date startDate = sdf.parse(this.startDate);
            Date endDate = sdf.parse(this.endDate);
            long diffInMs = Math.abs(endDate.getTime() - startDate.getTime());
            return (int) TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addDestination(Destination d){
        destinations.add(d);
    }

    public String getDestinationString(){
        StringBuilder stringBuilder = new StringBuilder();

        for(Destination dest : destinations){
            stringBuilder.append(dest.getName() + ", ");
        }

        String finalStr = stringBuilder.toString();
        if(finalStr.endsWith(" ")){
            finalStr = finalStr.substring(0, finalStr.length()-2);
        }

        return finalStr;
    }

    public static List<TripPlan> getAllPlans(Context context){
        List<TripPlan> plans = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from trips", null);
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            plans.add(new TripPlan(context, name));
        }
        cursor.close();
        database.close();
        helper.close();
        return plans;
    }
}
