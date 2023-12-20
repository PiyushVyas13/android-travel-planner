package com.example.travelplanner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.travelplanner.database.DayItem;
import com.example.travelplanner.database.Destination;
import com.example.travelplanner.database.ItineraryItem;
import com.example.travelplanner.database.Place;
import com.example.travelplanner.R;
import com.example.travelplanner.database.TripPlan;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ItineraryListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<DayItem> dayItemList;
    private TripPlan plan;
    private FragmentManager manager;

    private List<Destination> selected;

    public ItineraryListAdapter(Context context, TripPlan plan, List<DayItem> items, List<Destination> selected, FragmentManager manager){
        this.context = context;
        this.dayItemList = items;
        this.plan = plan;
        this.manager = manager;
        this.selected = selected;
    }



    @Override
    public int getGroupCount() {
        return dayItemList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return dayItemList.get(i).getItineraryItems().size();
    }

    @Override
    public Object getGroup(int i) {
        return dayItemList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return dayItemList.get(i).getItineraryItems().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public class GroupViewHolder {
        Button viewTrip;
        TextView dateTextView;
        ListView sublist;
    }

    public class ChildViewHolder {
        ImageView image;
        TextView place;
        TextView time;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        DayItem item = (DayItem) getGroup(i);
        GroupViewHolder viewHolder;
        final View result;

        if(convertView == null){
            viewHolder = new GroupViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itinerary_list_item, viewGroup, false);

            viewHolder.viewTrip = convertView.findViewById(R.id.addButton);
            viewHolder.dateTextView = convertView.findViewById(R.id.dateTextView);
            result = convertView;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (GroupViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.dateTextView.setText(item.getDate());


        viewHolder.viewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPlaceDialog(item);
            }
        });
        ExpandableListView listView = (ExpandableListView) viewGroup;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b){
                    listView.collapseGroup(i);
                }
                else {
                    listView.expandGroup(i, true);
                }
            }
        });

        return result;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        ItineraryItem item = (ItineraryItem) getChild(i, i1);
        ChildViewHolder viewHolder;
        final View result;

        if(convertView == null){
            viewHolder = new ChildViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itinerary_list_sub_item, viewGroup, false);

            viewHolder.image = convertView.findViewById(R.id.subitem_icon);
            viewHolder.place = convertView.findViewById(R.id.subitem_title);
            viewHolder.time = convertView.findViewById(R.id.subitem_time);

            result = convertView;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ChildViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.place.setText(item.getPlace().getName());
        viewHolder.time.setText(item.getTime());
        FetchImage fetchImage = new FetchImage(viewHolder.image);
        fetchImage.execute(item.getPlace());

        return result;

    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private void showAddPlaceDialog(DayItem item) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_place_dialog, null);

        TextInputEditText fromTime = dialogView.findViewById(R.id.from_time_et);
        TextInputEditText toTime = dialogView.findViewById(R.id.to_time_et);

        List<String> desti = new ArrayList<>();
        for(Destination destination : selected){
            for(Place place : destination.getPlaces(context)){
                desti.add(place.getName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, desti);
        AutoCompleteTextView atv = dialogView.findViewById(R.id.place_name_et);
        atv.setAdapter(adapter);

        fromTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MaterialTimePicker.Builder timePickerBuilder = new MaterialTimePicker.Builder();
                    timePickerBuilder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK);
                    timePickerBuilder.setTitleText("From Time");
                    MaterialTimePicker timePicker = timePickerBuilder.build();
                    timePicker.show(manager, "FROM_TIME");
                    timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            String ampm = dateFormat.format(calendar.getTime());
                            fromTime.setText(ampm);
                        }
                    });
                }
            }
        });

        toTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    MaterialTimePicker.Builder timePickerBuilder = new MaterialTimePicker.Builder();
                    timePickerBuilder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK);
                    timePickerBuilder.setTitleText("To Time");
                    MaterialTimePicker timePicker = timePickerBuilder.build();
                    timePicker.show(manager, "TO_TIME");

                    timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            String ampm = dateFormat.format(calendar.getTime());
                            toTime.setText(ampm);
                        }
                    });
                }
            }
        });

        builder.setView(dialogView);
        builder.setTitle("Add Place");
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                Place selectedPlace = null;
                for(Destination destination : plan.getDestinations()){
                    if(destination.getPlace(atv.getText().toString()) != null){
                        selectedPlace = destination.getPlace(atv.getText().toString());
                    }
                }
                item.addItineraryItem(new ItineraryItem(context, plan, selectedPlace, fromTime.getText().toString(), toTime.getText().toString(), item.getDate(), true));
                notifyDataSetChanged();
                Toast.makeText(context, "Place Added", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public class FetchImage extends AsyncTask<Place, Void, List<String>> {
        private ImageView imageView;

        public FetchImage(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected List<String> doInBackground(Place... places) {
            return places[0].getImageUrls();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            Picasso.get().load(strings.get(0)).into(imageView);
        }
    }
}
