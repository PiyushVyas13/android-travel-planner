package com.example.travelplanner.ui.plan.triplist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.travelplanner.PlanDetailActivity;
import com.example.travelplanner.R;
import com.example.travelplanner.adapters.TripAdapter;
import com.example.travelplanner.database.TripPlan;

import java.util.ArrayList;
import java.util.List;

public class TripListFragment extends Fragment {

    private TripListViewModel mViewModel;
    private ListView listView;

    public static TripListFragment newInstance() {
        return new TripListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trip_list, container, false);

        listView = root.findViewById(R.id.my_trips_list);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<TripPlan> plans = TripPlan.getAllPlans(getContext());
        TripAdapter adapter = new TripAdapter(plans, getContext());
        listView.setAdapter(adapter);
    }


}