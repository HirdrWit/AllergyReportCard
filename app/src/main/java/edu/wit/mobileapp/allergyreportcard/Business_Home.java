package edu.wit.mobileapp.allergyreportcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;

import java.util.List;

public class Business_Home extends AppCompatActivity {
    private String Name;
    private String Address;
    private String Id;
    private String PhoneNumber;
    private String OpeningHours;
    private String PriceLevel;
    private String Attributions;
    private String WebsiteUri;
    private String Types;
    private PlacesClient placesClient;
    private ImageView photoView;
    private FieldSelector fieldSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_business__home);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("place_data");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.places_temp_results);

        setValues(message);

        placesClient = Places.createClient(this);
        photoView = findViewById(R.id.photo);
        fieldSelector =
                new FieldSelector(
                        findViewById(R.id.use_custom_fields), findViewById(R.id.custom_fields_list));

        fetchPlace();

        textView.setText(message);
    }
    private void fetchPlace() {
        photoView.setImageBitmap(null);
        final boolean isFetchPhotoChecked = true;
        List<Place.Field> placeFields = getPlaceFields();
        setLoading(true);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(Id, placeFields);//added
        Task<FetchPlaceResponse> placeTask = placesClient.fetchPlace(request);

        placeTask.addOnSuccessListener(
            (response) -> {
                if (isFetchPhotoChecked) {
                    attemptFetchPhoto(response.getPlace());
                }
            });

        placeTask.addOnFailureListener(
            (exception) -> {
                exception.printStackTrace();
             });

        placeTask.addOnCompleteListener(response -> setLoading(false));
    }

    private void attemptFetchPhoto(Place place) {
        List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();
        if (photoMetadatas != null && !photoMetadatas.isEmpty()) {
            fetchPhoto(photoMetadatas.get(0));
        }
    }

    /**
     * Fetches a Bitmap using the Places API and displays it.
     *
     * @param photoMetadata from a {@link Place} instance.
     */
    private void fetchPhoto(PhotoMetadata photoMetadata) {
        photoView.setImageBitmap(null);
        setLoading(true);
        FetchPhotoRequest.Builder photoRequestBuilder = FetchPhotoRequest.builder(photoMetadata);photoRequestBuilder.setMaxHeight(500); //added
        Task<FetchPhotoResponse> photoTask = placesClient.fetchPhoto(photoRequestBuilder.build());

        photoTask.addOnSuccessListener(
            response -> {
                photoView.setImageBitmap(response.getBitmap());
            });

        photoTask.addOnFailureListener(
            exception -> {
                exception.printStackTrace();
            });

        photoTask.addOnCompleteListener(response -> setLoading(false));
    }
    private List<Place.Field> getPlaceFields() {
        return fieldSelector.getAllFields();
    }
    private void setLoading(boolean loading) {
        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
    private void setValues(String message) {
        String[] values;
        String delimiter = "__";
        values = message.split(delimiter);
        Name = values[0];
        Address = values[1];
        Id = values[2];
        PhoneNumber = values[3];
        OpeningHours = values[4];
        PriceLevel = values[5];
        Attributions = values[6];
        WebsiteUri = values[7];
        Types = values[8];
        Log.v("Business_Home", Name);
        Log.v("Business_Home", Address);
        Log.v("Business_Home", Id);
        Log.v("Business_Home", PhoneNumber);
        Log.v("Business_Home", OpeningHours);
        Log.v("Business_Home", PriceLevel);
        Log.v("Business_Home", Attributions);
        Log.v("Business_Home", WebsiteUri);
        Log.v("Business_Home", Types);
    }
}
