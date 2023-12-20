package com.example.travelplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.travelplanner.PlanDetailActivity;
import com.example.travelplanner.R;
import com.example.travelplanner.database.TripPlan;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends ArrayAdapter<TripPlan> implements View.OnClickListener {

    Context context;

    public TripAdapter(List<TripPlan> trips, Context context) {
        super(context, R.layout.trip_list_item, trips);
        this.context = context;
    }

    private static class ViewHolder {
        TextView tripName;
        TextView tripDate, tripDuration, tripDestinations;
        Button viewTrip;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        TripPlan item = (TripPlan) getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TripPlan plan = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.trip_list_item, parent, false);

            viewHolder.tripName = convertView.findViewById(R.id.trip_name);
            viewHolder.tripDate = convertView.findViewById(R.id.trip_dates);
            viewHolder.tripDuration = convertView.findViewById(R.id.trip_duration);
            viewHolder.tripDestinations = convertView.findViewById(R.id.trip_destinations);
            viewHolder.viewTrip = convertView.findViewById(R.id.view_trip_button);

            result = convertView;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.tripName.setText(plan.getName());
        viewHolder.tripDate.setText(plan.getDate());
        viewHolder.tripDuration.setText(plan.getDuration() + " days");
        viewHolder.tripDestinations.setText(plan.getDestinationString());

        viewHolder.viewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlanDetailActivity.class);
                intent.putExtra("trip_plan", plan);
                intent.putParcelableArrayListExtra("selected", ((ArrayList<? extends Parcelable>) plan.getDestinations()));
                intent.putExtra("destinations", plan.getDestinationString());
                context.startActivity(intent);

            }
        });
        return result;

    }


}
