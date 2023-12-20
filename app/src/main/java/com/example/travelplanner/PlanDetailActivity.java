package com.example.travelplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.travelplanner.adapters.ItineraryListAdapter;
import com.example.travelplanner.database.DatabaseHelper;
import com.example.travelplanner.database.DayItem;
import com.example.travelplanner.database.TripPlan;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanDetailActivity extends AppCompatActivity {
    TripPlan plan;
    ExpandableListView listView;
    TextView title, dates, destinations;
    List<DayItem> itinerary;

    Button deleteTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        
        plan = getIntent().getParcelableExtra("trip_plan");
        plan.setDestinations(getIntent().getParcelableArrayListExtra("selected"));
        listView = findViewById(R.id.itinerary_list_view);
        title = findViewById(R.id.title_text_view);
        dates = findViewById(R.id.dates_text_view);
        destinations = findViewById(R.id.destinations_text_view);
        deleteTrip = findViewById(R.id.delete_trip_button);
        itinerary = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String startDate = plan.getStartDate();
        String endDate = plan.getEndDate();

// Convert the start and end dates to Date objects
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date startDateObj = null;
        Date endDateObj = null;
        try {
            startDateObj = dateFormat.parse(startDate);
            endDateObj = dateFormat.parse(endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

// Create a list of dates for each day in the trip
        List<Date> dateList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDateObj);
        while (cal.getTime().before(endDateObj) || cal.getTime().equals(endDateObj)) {
            dateList.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }

// Create a list of ListView items based on the date list
        List<Map<String, String>> itemList = new ArrayList<>();
        for (Date date : dateList) {
            // Convert the date to a formatted string
            String dateString = dateFormat.format(date);

            String[] dateParts = dateString.split("/");
            int dayOfMonth = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM");
            String newStr = sdf.format(calendar.getTime());

            // Create a new item for the ListView
            Map<String, String> item = new HashMap<>();
            item.put("title", newStr);
            itemList.add(item);
            itinerary.add(new DayItem(this, newStr, plan));
        }


        title.setText(plan.getName());
        dates.setText(plan.getDate());
        destinations.setText(getIntent().getStringExtra("destinations"));

        ItineraryListAdapter listAdapter = new ItineraryListAdapter(this, plan, itinerary, getIntent().getParcelableArrayListExtra("selected"), getSupportFragmentManager());

        deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });

// Set the adapter for the ListView
        listView.setAdapter(listAdapter);
        listView.expandGroup(0);
    }

    private void confirmDelete() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this trip?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes button
                deletePlan(Integer.toString(plan.getId()));
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void deletePlan(String id) {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete("trips", "id=?", new String[]{id});
        database.close();
        finish();
    }


}