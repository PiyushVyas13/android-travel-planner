package com.example.travelplanner.ui.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelplanner.database.Destination;

public class ExploreViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<Destination> destination;

    public ExploreViewModel() {
        mText = new MutableLiveData<>();
        destination = new MutableLiveData<>();

        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}