package com.example.travelplanner.ui.explore;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.travelplanner.database.Destination;
import com.example.travelplanner.database.Place;
import com.example.travelplanner.adapters.PlaceAdapter;
import com.example.travelplanner.R;
import com.example.travelplanner.databinding.FragmentExploreBinding;
import com.example.travelplanner.expslider.SliderAdapter;
import com.example.travelplanner.expslider.SliderData;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    AutoCompleteTextView atView;
    ArrayAdapter<String> adapter;
    Destination destination;
    List<Place> places;
    ListView listView;
    TextView destName, destDesc, placesTitle;
    SliderView sliderView;
    ArrayList<SliderData> sliderDataArrayList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExploreViewModel exploreViewModel =
                new ViewModelProvider(this).get(ExploreViewModel.class);

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        atView = root.findViewById(R.id.atView);
        listView = root.findViewById(R.id.places_list);
        destName = root.findViewById(R.id.destination_name);
        destDesc = root.findViewById(R.id.destination_description);
        placesTitle = root.findViewById(R.id.places_title);
        sliderView = root.findViewById(R.id.slider);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Destination.getDestinationNames(getContext()));
        atView.setAdapter(adapter);
        atView.setThreshold(2);

        atView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                destination = new Destination(getContext(), selected);
                places = destination.getPlaces(getContext());



                destName.setText(destination.getName());
                destDesc.setText(destination.getDescription());
                placesTitle.setText("Places to visit in " + destination.getName());


                ImageUrlTask task = new ImageUrlTask();
                FetchPlaceTask fetchTask = new FetchPlaceTask();

                task.execute(destination);
                fetchTask.execute(places);



            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class ImageUrlTask extends AsyncTask<Destination, Integer, List<String>> {



        @Override
        protected List<String> doInBackground(Destination... destinations) {
            Log.i("Async", "DoInBackground");
            return destinations[0].getImageUrls();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
//            for(String url : strings){
//                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
//            }
//            Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();

            sliderDataArrayList = new ArrayList<>();

            for(String url : strings) {
                sliderDataArrayList.add(new SliderData(url));
            }
            SliderAdapter sliderAdapter = new SliderAdapter(getContext(), sliderDataArrayList);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setSliderAdapter(sliderAdapter);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();
            Log.i("Async", "onPostExecute");
        }
    }

    public class FetchPlaceTask extends AsyncTask<List<Place>, Void, List<String>> {


        @Override
        protected List<String> doInBackground(List<Place>... lists) {
            List<String> urls = new ArrayList<>();
            Log.i("FetchPlaceTask", "Hello Bye");
            for(Place place : lists[0]){
                urls.add(place.getImageUrls().get(0));
                Log.i("FetchPlaceTask", "Hello");
            }
            return urls;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            PlaceAdapter placeAdapter = new PlaceAdapter(places, strings,  getContext());
            listView.setAdapter(placeAdapter);
            Log.i("FetchPlaceTask", "Done");
        }
    }
}