package com.example.travelplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ItineraryItem {
    private TripPlan plan;
    private String startTime;
    private String endTime;
    private Place place;
    Context context;

    public ItineraryItem(Context context,TripPlan plan, Place place, String startTime, String endTime,String date, boolean create){
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.plan = plan;
        DatabaseHelper helper = new DatabaseHelper(context);

        if(create){
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("trip_id", plan.getId());
            values.put("place_id", place.getId());
            values.put("start_time", startTime);
            values.put("end_time", endTime);
            values.put("date", date);
            database.insert("itineraries", null, values);
            database.close();
        }

        helper.close();

    }

    public Place getPlace() {return this.place;}

    public String getTime(){
        return this.startTime + " To " + this.endTime;
    }


}
