package com.example.travelplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import retrofit2.Retrofit;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "travel_planner.db";

    // Table names
    private static final String TABLE_DESTINATIONS = "destinations";
    private static final String TABLE_PLACES = "places";
    private static final String TABLE_TRIPS = "trips";
    private static final String TABLE_ITINERARIES = "itineraries";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    // Destinations table column names
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_IMAGE = "image";

    // Places table column names
    private static final String KEY_DESTINATION_ID = "destination_id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    // Trips table column names
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    // Itineraries table column names
    private static final String KEY_TRIP_ID = "trip_id";
    private static final String KEY_PLACE_ID = "place_id";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";

    // Create Destinations table
    private static final String CREATE_TABLE_DESTINATIONS = "CREATE TABLE " + TABLE_DESTINATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_COUNTRY + " TEXT"
            + ")";

    // Create Places table
    private static final String CREATE_TABLE_PLACES = "CREATE TABLE " + TABLE_PLACES + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_DESTINATION_ID + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_LATITUDE + " REAL,"
            + KEY_LONGITUDE + " REAL,"
            + "FOREIGN KEY(" + KEY_DESTINATION_ID + ") REFERENCES " + TABLE_DESTINATIONS + "(" + KEY_ID + ")"
            + ")";

    private static final String KEY_DESTINATIONS = "destinations";
    // Create Trips table
    private static final String CREATE_TABLE_TRIPS = "CREATE TABLE " + TABLE_TRIPS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_START_DATE + " TEXT,"
            + KEY_END_DATE + " TEXT,"
            + KEY_DESTINATIONS + " TEXT"
            + ")";

    private static final String KEY_DATE = "date";
    // Create Itineraries table
    private static final String CREATE_TABLE_ITINERARIES = "CREATE TABLE " + TABLE_ITINERARIES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TRIP_ID + " INTEGER,"
            + KEY_PLACE_ID + " INTEGER,"
            + KEY_START_TIME + " TEXT,"
            + KEY_END_TIME + " TEXT,"
            + KEY_DATE + " TEXT,"
            + "FOREIGN KEY(" + KEY_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + KEY_ID + "),"
            + "FOREIGN KEY(" + KEY_PLACE_ID + ") REFERENCES " + TABLE_PLACES + "(" + KEY_ID + ")"
            + ")";

    int[] id = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    String[] name = {"Paris", "Tokyo", "New York", "Rome", "Sydney", "Cape Town", "Rio de Janeiro", "Barcelona", "Bali", "Dubai"};
    String[] country = {"France", "Japan", "USA", "Italy", "Australia", "South Africa", "Brazil", "Spain", "Indonesia", "UAE"};
    String[] description = {"The City of Love", "The Land of the Rising Sun", "The City That Never Sleeps", "The Eternal City", "The Harbour City", "The Mother City", "The Marvelous City", "The City of Gaudi", "The Island of the Gods", "The City of Gold"};



    int[] placeIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    String[] placeNames = {"Eiffel Tower", "Louvre Museum", "Palace of Versailles", "Notre-Dame Cathedral", "Arc de Triomphe", "Musée d'Orsay", "Sainte-Chapelle", "Les Invalides", "Place Vendôme", "Panthéon"};
    String[] placeDescriptions = {"Iconic tower from 1889 with a wrought-iron top", "World's largest art museum and historic monument", "Royal château and UNESCO World Heritage Site", "14th-century cathedral with flying buttresses", "Triumphal arch honoring Napoleon's victories", "Impressionist & post-Impressionist masterpieces", "Gothic chapel known for stained glass windows", "17th-century military complex with museums", "Historic public square lined with luxury shops", "Neoclassical mausoleum with tombs of famous French figures"};
    double[] placeLatitudes = {48.8584, 48.8606, 48.8049, 48.8529, 48.8738, 48.8599, 48.8557, 48.8566, 48.8674, 48.8462};
    double[] placeLongitudes = {2.2945, 2.3376, 2.1204, 2.3507, 2.2950, 2.3264, 2.3454, 2.3122, 2.3294, 2.3465};

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIPS);
        db.execSQL(CREATE_TABLE_ITINERARIES);
        db.execSQL(CREATE_TABLE_DESTINATIONS);
        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put("id", id[i]);
            values.put("name", name[i]);
            values.put("country", country[i]);
            values.put("description", description[i]);
            db.insert("destinations", null, values);
        }

        db.execSQL(CREATE_TABLE_PLACES);
        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put("id", placeIds[i]);
            values.put("name", placeNames[i]);
            values.put("description", placeDescriptions[i]);
            values.put("destination_id", 1);
            values.put("latitude", placeLatitudes[i]);
            values.put("longitude", placeLongitudes[i]);

            db.insert("places", null, values);
        }
    }

    // Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }

}
