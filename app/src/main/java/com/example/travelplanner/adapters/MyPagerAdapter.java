package com.example.travelplanner.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.travelplanner.ui.plan.newtrip.NewTripFragment;
import com.example.travelplanner.ui.plan.triplist.TripListFragment;

public class MyPagerAdapter extends FragmentStateAdapter {

    public MyPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new NewTripFragment();
            case 1 -> new TripListFragment();
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
