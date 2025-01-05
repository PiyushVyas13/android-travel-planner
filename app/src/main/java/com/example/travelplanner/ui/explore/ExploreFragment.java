package com.example.travelplanner.ui.explore;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.travelplanner.database.Destination;
import com.example.travelplanner.database.Place;
import com.example.travelplanner.adapters.PlaceAdapter;
import com.example.travelplanner.R;
import com.example.travelplanner.databinding.FragmentExploreBinding;
import com.example.travelplanner.expslider.SliderAdapter;
import com.example.travelplanner.expslider.SliderData;
import com.google.android.material.carousel.CarouselLayoutManager;


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
    ArrayList<SliderData> sliderDataArrayList;
    ViewPager2 viewPager2;
    Handler sliderHandler = new Handler();


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
        viewPager2 = root.findViewById(R.id.viewPager);


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Destination.getDestinationNames(getContext()));
        atView.setAdapter(adapter);
        atView.setThreshold(2);

        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleX(0.85f + r * 0.15f);
            page.setScaleY(0.85f + r * 0.15f);
        }));

        viewPager2.setPageTransformer(compositePageTransformer);

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

            SliderAdapter sliderAdapter  = new SliderAdapter(requireContext(), sliderDataArrayList);
            viewPager2.setAdapter(sliderAdapter);
            startAutoSlider();

            Log.i("Async", "onPostExecute");
        }
    }

    private void startAutoSlider() {
        Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                if(currentItem < sliderDataArrayList.size() - 1) {
                    viewPager2.setCurrentItem(currentItem + 1);
                } else {
                    viewPager2.setCurrentItem(0);
                }
                sliderHandler.postDelayed(this, 3000);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacksAndMessages(null);

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