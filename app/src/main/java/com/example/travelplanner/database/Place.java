package com.example.travelplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.example.travelplanner.database.DatabaseHelper;
import com.example.travelplanner.database.Destination;
import com.example.travelplanner.unsplash.UnsplashApi;
import com.example.travelplanner.unsplash.UnsplashResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Place {
    private int id;
    private Destination destination;
    private String name;
    private String description;
    private double lat;
    private double lng;

    public String getName(){return this.name;}

    public int getId(){return this.id;}

    public String getDescription(){return this.description;}

    public Destination getDestination(){return this.destination;}
    public int getDestinationId(){return this.destination.getId();}

    public Place(Context context, String name, Destination destination){
        this.destination = destination;
        this.name = name;

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM places WHERE name = \"" + name+"\" and destination_id="+destination.getId(), null);
        if (cursor.moveToFirst()) {
            this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            this.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            this.lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            this.lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

        }
        cursor.close();
        database.close();

    }

    public Place(Context context, int id){

        this.id = id;

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM places WHERE id = " + id, null);
        if (cursor.moveToFirst()) {
            this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            this.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            this.lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            this.lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
            int dest_id = cursor.getInt(cursor.getColumnIndexOrThrow("destination_id"));
            this.destination = new Destination(context, dest_id);
        }
        cursor.close();
        database.close();

    }


    public List<String> getPlacesNames(Context context) {
        List<String> placesNames = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            DatabaseHelper dataBaseHelper = new DatabaseHelper(context);
            db = dataBaseHelper.getReadableDatabase();

            String[] columns = {"name"};
            cursor = db.query("places", columns, "destination_id", new String[]{Integer.toString(this.destination.getId())}, null, null, null);

            while (cursor.moveToNext()) {
                placesNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return placesNames;
    }

    public List<String> getImageUrls() {
        List<String> imageUrls = new ArrayList<>();

        // Build the Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the UnsplashApi interface
        UnsplashApi unsplashApi = retrofit.create(UnsplashApi.class);

        // Send a request to the Unsplash API to get a list of photos related to the destination
        Call<UnsplashResponse> call = unsplashApi.getPhotos(this.name.toLowerCase());
        try {
            Response<UnsplashResponse> response = call.execute();
            UnsplashResponse unsplashResponse = response.body();
            if (unsplashResponse != null) {
                for (UnsplashResponse.UnsplashPhoto uresponse : unsplashResponse.getPhotos()) {
                    String imageUrl = uresponse.getUrls().getRegularUrl();
                    imageUrls.add(imageUrl);
                }
            }
            else {
                Log.i("Call Result", "Result is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUrls;
    }

}
