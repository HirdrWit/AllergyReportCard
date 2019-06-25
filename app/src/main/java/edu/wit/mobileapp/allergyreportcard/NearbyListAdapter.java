package edu.wit.mobileapp.allergyreportcard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NearbyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> names;
    private final  ArrayList<String> address;
//    private final Integer[] photos;

//    public NearbyListAdapter(Activity context, String[] names, String[] address, Integer[] photos) {
    public NearbyListAdapter(Activity context, ArrayList<String> names, ArrayList<String> address) {
        super(context, R.layout.nearby_listview, names);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.names=names;
        this.address=address;
//        this.photos=photos;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.nearby_listview, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.places_name);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.places_address);

        titleText.setText(names.get(position));
//        imageView.setImageResource(photos[position]);
        subtitleText.setText(address.get(position));

        return rowView;

    };
}
