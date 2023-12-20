package com.example.travelplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.travelplanner.unsplash.UnsplashApi;
import com.example.travelplanner.unsplash.UnsplashResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Destination implements Parcelable{
    private static final String BASE_URL = "https://unsplash.com/api";
    private static final String API_KEY = "K0eIbhXfonc3UuKiLVp6s4QTx1Q1glzQC0DA9s-lAh4";
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private String country;

    private List<Place> places;

    public Destination(Context context, String name) {
        this.name = name;
        // Retrieve data from database and populate variables
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinations WHERE name = '" + name+ "'", null);
        if (cursor.moveToFirst()) {
            this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            this.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            this.country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        }
        cursor.close();
        db.close();
    }

    public Destination(Context context, int id) {
        this.id = id;
        // Retrieve data from database and populate variables
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM destinations WHERE id = " + id, null);
        if (cursor.moveToFirst()) {
            this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            this.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            this.country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        }
        cursor.close();
        db.close();
    }

    protected Destination(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        country = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(country);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Destination> CREATOR = new Creator<Destination>() {
        @Override
        public Destination createFromParcel(Parcel in) {
            return new Destination(in);
        }

        @Override
        public Destination[] newArray(int size) {
            return new Destination[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    // Other methods for retrieving places of interest and creating itinerary
    public static List<String> getDestinationNames(Context context) {
        List<String> destinationNames = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            DatabaseHelper dataBaseHelper = new DatabaseHelper(context);
            db = dataBaseHelper.getReadableDatabase();

            String[] columns = {"name"};
            cursor = db.query("destinations", columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                destinationNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
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
        return destinationNames;
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

    public List<Place> getPlaces(Context context) {
        this.places = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM places WHERE destination_id = ?", new String[] {String.valueOf(id)});
        while (cursor.moveToNext()) {
            String placeName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            Place place = new Place(context, placeName, this);
            places.add(place);
        }
        cursor.close();
        db.close();
        return places;
    }

    public Place getPlace(String name){
        for(Place place : places){
            if(place.getName().equals(name)){
                return place;
            }
        }
        return null;
    }

    public static List<Destination> getAllDestinations(Context context){
        List<Destination> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            DatabaseHelper dataBaseHelper = new DatabaseHelper(context);
            db = dataBaseHelper.getReadableDatabase();

            String[] columns = {"name"};
            cursor = db.query("destinations", columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                list.add(new Destination(context, name));
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


        return list;
    }

}
