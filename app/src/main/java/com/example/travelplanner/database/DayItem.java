package com.example.travelplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DayItem {
    private String date;
    private List<ItineraryItem> items;

    public DayItem(Context context, String date, TripPlan plan){
        this.date = date;
        this.items = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM itineraries WHERE trip_id="+plan.getId()+" and date='"+date+"'", null);
        while (cursor.moveToNext()){
            int tripId = cursor.getInt(cursor.getColumnIndexOrThrow("trip_id"));
            int place_id = cursor.getInt(cursor.getColumnIndexOrThrow("place_id"));
            String start_time = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
            String end_time = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));

            this.items.add(new ItineraryItem(context, new TripPlan(context, tripId), new Place(context, place_id), start_time, end_time, date,  false));
        }
    }

    public void addItineraryItem(ItineraryItem item){ items.add(item);}
    public void removeItineraryItem(ItineraryItem item) {items.remove(item);}
    public void clearItineraryItems(){items.clear();}

    public String getDate(){return this.date;}

    public List<ItineraryItem> getItineraryItems(){ return this.items;}
}
