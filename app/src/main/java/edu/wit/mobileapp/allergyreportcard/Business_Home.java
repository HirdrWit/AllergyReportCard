package edu.wit.mobileapp.allergyreportcard;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView name_TextView;
    private TextView address_TextView;
    private TextView phone_TextView;
    private TextView price_TextView;
    private TextView attribution_TextView;
    private TextView website_TextView;
    private TextView type_TextView;
    private Button button_place_phone;
    private Button button_place_website;
    private Button button_place_address;
    private String TAG = "Business_Home";
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_business__home);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        apiKey = getString(R.string.places_api_key);
        if (apiKey.equals("")) {
            Toast.makeText(this, "No API Key", Toast.LENGTH_LONG).show();
            return;
        }
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("place_data");

        // Capture the layout's TextView and set the string as its text
        name_TextView = findViewById(R.id.places_name);
//        address_TextView = findViewById(R.id.places_address);
//        phone_TextView = findViewById(R.id.places_phone);
//        price_TextView = findViewById(R.id.places_price);
//        attribution_TextView = findViewById(R.id.places_attribution);
//        website_TextView = findViewById(R.id.places_website);
        type_TextView = findViewById(R.id.places_type);
        button_place_address = findViewById(R.id.button_place_address);
        button_place_phone = findViewById(R.id.button_place_phone);
        button_place_website = findViewById(R.id.button_place_website);

        getValues(message);

        placesClient = Places.createClient(this);
        photoView = findViewById(R.id.photo);
        fieldSelector =
                new FieldSelector(
                        findViewById(R.id.use_custom_fields), findViewById(R.id.custom_fields_list));
//
        fetchPlace();
        setValues();
//        textView.setText(message);
    }

    private void setValues() {
        name_TextView.setText(Name);
//        address_TextView.setText("Address: " + Address);
//        phone_TextView.setText("Call: " + PhoneNumber);
        button_place_address.setText(Address);
        button_place_phone.setText(PhoneNumber);
//        price_TextView.setText(PriceLevel);
//        attribution_TextView.setText(Attributions);
//        website_TextView.setText("Website: " + WebsiteUri);
        button_place_website.setText(WebsiteUri);
        type_TextView.setText(Types);
    }

    private void fetchPlace() {
        photoView.setImageBitmap(null);
        final boolean isFetchPhotoChecked = true;
        List<Place.Field> placeFields = getPlaceFields();
        setLoadingPhoto(true);
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

        placeTask.addOnCompleteListener(response -> setLoadingPhoto(false));
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
        setLoadingPhoto(true);
        FetchPhotoRequest.Builder photoRequestBuilder = FetchPhotoRequest.builder(photoMetadata);
        photoRequestBuilder.setMaxHeight(500); //added
        Task<FetchPhotoResponse> photoTask = placesClient.fetchPhoto(photoRequestBuilder.build());

        photoTask.addOnSuccessListener(
                response -> {
                    photoView.setImageBitmap(response.getBitmap());
                });

        photoTask.addOnFailureListener(
                exception -> {
                    exception.printStackTrace();
                });

        photoTask.addOnCompleteListener(response -> setLoadingPhoto(false));
    }

    private List<Place.Field> getPlaceFields() {
        return fieldSelector.getAllFields();
    }

    private void setLoadingPhoto(boolean loading) {
        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
    private void setLoadingData(boolean loading) {
        findViewById(R.id.dataloading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    private void getValues(String id) {
        setLoadingData(true);
        RequestQueue requestQueue;

        Cache cache = new DiskBasedCache(getCacheDir(), 2*(1024 * 1024)); // 1MB cap

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);

// Start the queue
        requestQueue.start();
        Intent i = getIntent();
//        String type = "restaurant";
        String field = "id,photos,formatted_address,name,rating,opening_hours,geometry";
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.3376842,-71.0963798&rankby=distance&types=restaurant&fields=id,photos,formatted_address,name,rating,opening_hours,geometry&key=AIzaSyCpe7Vz_R4eRdZClnuF4jLBdAPgJmamFIM

        name_TextView = findViewById(R.id.places_name);
//        address_TextView = findViewById(R.id.places_address);
//        phone_TextView = findViewById(R.id.places_phone);
//        price_TextView = findViewById(R.id.places_price);
//        attribution_TextView = findViewById(R.id.places_attribution);
//        website_TextView = findViewById(R.id.places_website);
        type_TextView = findViewById(R.id.places_type);
        Id = id;
//&fields=name,rating,formatted_phone_number&key=YOUR_API_KEY
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlacesUrl.append("placeid=").append(id);
        googlePlacesUrl.append("&fields=").append("name,formatted_address,formatted_phone_number,price_level,types,website,type");
        googlePlacesUrl.append("&key=" + apiKey);
        Log.v(TAG, googlePlacesUrl.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, googlePlacesUrl.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, response.toString());

                        parseJSONData(response);
                        setLoadingData(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.v(TAG, error.toString());
                        setLoadingData(false);
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void parseJSONData(JSONObject data) {
        try {
            JSONObject results = data.getJSONObject("result");
            Name = results.getString("name");
            Address = results.getString("formatted_address");
            Types = results.getString("types");
            Log.v(TAG,Name);
            Log.v(TAG,Address);
            Log.v(TAG,Types);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject results = data.getJSONObject("result");
            PhoneNumber = results.getString("formatted_phone_number");
            PriceLevel = results.getString("name");
            Attributions = "";
            WebsiteUri = results.getString("website");
            Log.v(TAG,PhoneNumber);
            Log.v(TAG,PriceLevel);
            Log.v(TAG,Attributions);
            Log.v(TAG,WebsiteUri);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setValues();


    }

}