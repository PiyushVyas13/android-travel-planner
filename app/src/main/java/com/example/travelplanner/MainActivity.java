package com.example.travelplanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.travelplanner.adapters.PlaceAdapter;
import com.example.travelplanner.database.Destination;
import com.example.travelplanner.database.Place;
import com.example.travelplanner.expslider.SliderAdapter;
import com.example.travelplanner.expslider.SliderData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.travelplanner.databinding.ActivityMainBinding;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    AutoCompleteTextView atView;
    SliderView sliderView;
    ArrayAdapter<String> adapter;
    ArrayList<SliderData> sliderDataArrayList;
    TextView destName, destDesc, placesTitle;

    Destination destination;

    List<Place> places;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_explore, R.id.navigation_plan, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

            atView = findViewById(R.id.atView);
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
            SliderAdapter sliderAdapter = new SliderAdapter(MainActivity.this, sliderDataArrayList);
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
            PlaceAdapter placeAdapter = new PlaceAdapter(places, strings,  MainActivity.this);
            listView.setAdapter(placeAdapter);
            Log.i("FetchPlaceTask", "Done");
        }
    }

}