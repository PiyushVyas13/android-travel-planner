package com.example.travelplanner.ui.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.travelplanner.adapters.MyPagerAdapter;
import com.example.travelplanner.ui.plan.newtrip.NewTripFragment;
import com.example.travelplanner.R;
import com.example.travelplanner.databinding.FragmentPlanBinding;
import com.example.travelplanner.ui.plan.triplist.TripListFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PlanFragment extends Fragment {

    private FragmentPlanBinding binding;

    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanViewModel planViewModel =
                new ViewModelProvider(this).get(PlanViewModel.class);

        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
//        planViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = root.findViewById(R.id.view_pager);

        MyPagerAdapter adapter = new MyPagerAdapter(getParentFragment());
        viewPager2.setAdapter(adapter);

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0 -> tab.setText("New Trip");
                    case 1 -> tab.setText("My Trips");
                }

            }
        });

        mediator.attach();

        return root;
    }


}