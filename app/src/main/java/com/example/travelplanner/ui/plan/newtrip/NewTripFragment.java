package com.example.travelplanner.ui.plan.newtrip;

import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.travelplanner.database.Destination;
import com.example.travelplanner.PlanDetailActivity;
import com.example.travelplanner.R;
import com.example.travelplanner.database.TripPlan;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewTripFragment extends Fragment {

    private NewTripViewModel mViewModel;

    EditText startDate, endDate, tripName;
    AutoCompleteTextView atv;
    Button addDest, createPlanBtn;

    List<String> destinations;
    List<String> selected;
    TripPlan plan;
    ListView listView;
    private static Intent intent;

    public static NewTripFragment newInstance() {
        return new NewTripFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_new_trip, container, false);

        startDate = root.findViewById(R.id.edit_text_start_date);
        endDate = root.findViewById(R.id.edit_text_end_date);
        atv = root.findViewById(R.id.destination_autocomplete);
        listView = root.findViewById(R.id.destination_listview);
        addDest = root.findViewById(R.id.add_destination_button);
        createPlanBtn = root.findViewById(R.id.button_create_trip_plan);
        tripName = root.findViewById(R.id.edit_text_trip_name);
        intent = new Intent(getActivity(), PlanDetailActivity.class);
        destinations = Destination.getDestinationNames(getContext());
        ArrayAdapter<String> destAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, destinations);
        atv.setAdapter(destAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();

                builder.setTitleText("Enter Start Date");

                MaterialDatePicker datePicker = builder.build();

                datePicker.show(getActivity().getSupportFragmentManager(), "Start Date Picker");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Date date = new Date((Long) selection);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String selectedDate = format.format(date);
                        startDate.setText(selectedDate);
                    }
                });
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();

                builder.setTitleText("Enter End Date");

                MaterialDatePicker datePicker = builder.build();

                datePicker.show(getActivity().getSupportFragmentManager(), "End Date Picker");

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Date date = new Date((Long) selection);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String selectedDate = format.format(date);
                        endDate.setText(selectedDate);
                    }
                });
            }
        });

        selected = new ArrayList<>();
        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selected);
        listView.setAdapter(destinationAdapter);

        addDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected.add(atv.getText().toString());
                destinations.remove(atv.getText().toString());
                destinationAdapter.notifyDataSetChanged();
                atv.setText("");
            }
        });

        createPlanBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(createPlan()){
                    SuccessDialogFragment dialogFragment = new SuccessDialogFragment();
                    dialogFragment.show(getFragmentManager(), "SuccessDialogFragment");
                }

            }
        });
    }

    public boolean checkDetails(String planName, String startDate, String endDate, List<Destination> destinations){
        if (TextUtils.isEmpty(planName)) {
            // Plan name is empty
            return false;
        }

        if (TextUtils.isEmpty(startDate)) {
            // Start date is empty
            return false;
        }

        if (TextUtils.isEmpty(endDate)) {
            // End date is empty
            return false;
        }

        if (destinations == null || destinations.size() == 0) {
            // No destinations are selected
            return false;
        }

        // All details are valid
        return true;
    }

    public boolean createPlan(){
        String planName = tripName.getText().toString();
        String start = startDate.getText().toString();
        String end = endDate.getText().toString();

        List<Destination> selectedDestinations = new ArrayList<>();
        for(String str : selected){
            selectedDestinations.add(new Destination(getContext(), str));
        }

        if(checkDetails(planName, start, end, selectedDestinations)){
            try{
                plan = new TripPlan(getContext(), planName, start, end, selectedDestinations);
                intent.putExtra("trip_plan", plan);
                intent.putParcelableArrayListExtra("selected", (ArrayList<? extends Parcelable>) plan.getDestinations());
                intent.putExtra("destinations", plan.getDestinationString());
            }catch (Exception e){
                Snackbar.make(getView(), "An Error Occured while creating plan", Snackbar.LENGTH_SHORT).show();
                return false;
            }


        }else{
            Snackbar.make(getView(), "Invalid input. Please try again.", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static class SuccessDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Success")
                    .setMessage("The trip has been added successfully!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when OK button is clicked
                            startActivity(intent);
                        }
                    });

            return builder.create();
        }
    }
}