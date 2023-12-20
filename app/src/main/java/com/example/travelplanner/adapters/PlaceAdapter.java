package com.example.travelplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelplanner.database.Place;
import com.example.travelplanner.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceAdapter extends ArrayAdapter<Place> implements View.OnClickListener{

    private List<Place> dataSet;

    Context mContext;
    List<String> urls;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtVersion;
        ImageView info;


    }

    public PlaceAdapter(List<Place> data, List<String> urls, Context context) {
        super(context, R.layout.list_item_place, data);
        this.dataSet = data;
        this.mContext=context;
        this.urls = urls;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Place dataModel=(Place)object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Place dataModel = getItem(position);
        String url = urls.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_place, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.place_name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.place_description);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.place_image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtType.setText(dataModel.getDescription());
        Picasso.get().load(url).into(viewHolder.info);
        // Return the completed view to render on screen
        return result;
    }
}