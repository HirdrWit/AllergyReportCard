package edu.wit.mobileapp.allergyreportcard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearbyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> names;
    private final  ArrayList<String> address;
    private final  ArrayList<String> id_list;
    private String TAG = "NearbyListAdapter";
//    private final Integer[] photos;

//    public NearbyListAdapter(Activity context, String[] names, String[] address, Integer[] photos) {
    public NearbyListAdapter(Activity context, ArrayList<String> id_list, ArrayList<String> names, ArrayList<String> address) {
        super(context, R.layout.nearby_listview, names);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.id_list=id_list;
        this.names=names;
        this.address=address;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.nearby_listview, null,true);

        ImageView bussinessImage = (ImageView) rowView.findViewById(R.id.photo);
        TextView titleText = (TextView) rowView.findViewById(R.id.places_name);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.places_address);

        titleText.setText(names.get(position));
        subtitleText.setText(address.get(position));

        /////

        // Define a Place ID.
        String placeId = id_list.get(position);

// Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();
        PlacesClient placesClient = Places.createClient(context);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // Get the photo metadata.
            if(place.getPhotoMetadatas() == null) {
                bussinessImage.setImageResource(R.drawable.image_off);
                return;
            }
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                // Get the attribution text.
                String attributions = photoMetadata.getAttributions();

                // Create a FetchPhotoRequest.
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(1600) // Optional.
                        .setMaxHeight(1600) // Optional.
                        .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();

                bitmap = Bitmap.createScaledBitmap(
                        bitmap, 1200, 400, false);
                bussinessImage.setImageBitmap(bitmap);

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });
        });
        //
        return rowView;

    }



}
