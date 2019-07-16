package edu.wit.mobileapp.allergyreportcard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonArray;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;
    private PlacesClient placesClient;
    private TextView responseView;
    private TextView lat_long;
    private String Latitude = String.valueOf(R.string.latitude_525_boston), Longitude = String.valueOf(R.string.longitude_525_boston);
    private FieldSelector fieldSelector;
    private FusedLocationProviderClient client;
    private String apiKey;
    private String TAG = "MainActivityLog";
    private ArrayList<String> id_list = new ArrayList<>();
    private ArrayList<String> name_list = new ArrayList<>();
    private ArrayList<String> vicinity_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Retrieve a PlacesClient (previously initialized - see MainActivity)

        apiKey = getString(R.string.places_api_key);

        if (apiKey.equals("")) {
            Toast.makeText(this, "No API Key", Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        // Set up view objects
//        lat_long = findViewById(R.id.lat_long);
        fieldSelector =
                new FieldSelector(
                        findViewById(R.id.use_custom_fields), findViewById(R.id.custom_fields_list));

        setupAutocompleteSupportFragment();


        // UI initialization
        setLoading(false);

        requestPermission();

        Button restaurant_button = (Button) findViewById(R.id.button_restaurant_nearby);
        restaurant_button.setOnClickListener(v -> {
            loadNearByPlaces("restaurant");
        });
        Button hotel_button = (Button) findViewById(R.id.button_hotel_nearby);
        hotel_button.setOnClickListener(v -> {
            loadNearByPlaces("lodging");
        });
        Button amusement_button = (Button) findViewById(R.id.button_car_rental_nearby);
        amusement_button.setOnClickListener(v -> {
            loadNearByPlaces("car_rental");
        });
//        Button favorites_button = (Button) findViewById(R.id.button_favorites);
//        favorites_button.setOnClickListener(v -> {
////            lat_long.setText("No Favorites");
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermission();
    }

    private void setupAutocompleteSupportFragment() {
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_support_fragment);
        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());

    }

    @NonNull
    private PlaceSelectionListener getPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
//                responseView.setText(
//                        StringUtil.stringifyAutocompleteWidget(place, isDisplayRawResultsChecked()));
                Intent intent = new Intent(MainActivity.this, Business_Home.class);

                String message = StringUtil.stringifyAutocompleteWidget(place, isDisplayRawResultsChecked());
                intent = intent.putExtra("place_data", message);

                startActivity(intent);
            }

            @Override
            public void onError(Status status) {
                responseView.setText(status.getStatusMessage());
            }

        };

    }

    /**
     * Called when AutocompleteActivity finishes
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(intent);
//                responseView.setText(
//                        StringUtil.stringifyAutocompleteWidget(place, isDisplayRawResultsChecked()));


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(intent);
                responseView.setText(status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        // Required because this class extends AppCompatActivity which extends FragmentActivity
        // which implements this method to pass onActivityResult calls to child fragments
        // (eg AutocompleteFragment).
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void startAutocompleteActivity() {
        Intent autocompleteIntent =
                new Autocomplete.IntentBuilder(getMode(), getPlaceFields())
                        .setInitialQuery(getQuery())
                        .setCountry(getCountry())
                        .setLocationBias(getLocationBias())
                        .setLocationRestriction(getLocationRestriction())
                        .setTypeFilter(getTypeFilter())
                        .build(MainActivity.this);
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void findAutocompletePredictions() {
        setLoading(true);

        FindAutocompletePredictionsRequest.Builder requestBuilder =
                FindAutocompletePredictionsRequest.builder()
                        .setQuery(getQuery())
                        .setCountry(getCountry())
                        .setLocationBias(getLocationBias())
                        .setLocationRestriction(getLocationRestriction())
                        .setTypeFilter(getTypeFilter());

//    if (isUseSessionTokenChecked()) {
//      requestBuilder.setSessionToken(AutocompleteSessionToken.newInstance());
//    }

        Task<FindAutocompletePredictionsResponse> task =
                placesClient.findAutocompletePredictions(requestBuilder.build());

        task.addOnSuccessListener(
                (response) ->
                        responseView.setText(StringUtil.stringify(response, isDisplayRawResultsChecked())));

        task.addOnFailureListener(
                (exception) -> {
                    exception.printStackTrace();
                    responseView.setText(exception.getMessage());
                });

        task.addOnCompleteListener(response -> setLoading(false));
    }

    //////////////////////////
    // Helper methods below //
    //////////////////////////

    private List<Place.Field> getPlaceFields() {
//    if (((CheckBox) findViewById(R.id.use_custom_fields)).isChecked()) {
//      return fieldSelector.getSelectedFields();
//    } else {
        return fieldSelector.getAllFields();
//    }
    }

    private String getQuery() {
//    return ((TextView) findViewById(R.id.autocomplete_query)).getText().toString();
        return "";
    }

    private String getHint() {
//
        return "";
    }

    private String getCountry() {
//    return ((TextView) findViewById(R.id.autocomplete_country)).getText().toString();
        return "";
    }

    @Nullable
    private LocationBias getLocationBias() {
//    return getBounds(
//        R.id.autocomplete_location_bias_south_west, R.id.autocomplete_location_bias_north_east);
//  }
        return getBounds(
                0, 0);
    }

    @Nullable
    private LocationRestriction getLocationRestriction() {
//    return getBounds(
//        R.id.autocomplete_location_restriction_south_west,
//        R.id.autocomplete_location_restriction_north_east);
        return getBounds(
                0, 0);
    }

    @Nullable
    private RectangularBounds getBounds(int resIdSouthWest, int resIdNorthEast) {
        String southWest = ((TextView) findViewById(resIdSouthWest)).getText().toString();
        String northEast = ((TextView) findViewById(resIdNorthEast)).getText().toString();
        if (TextUtils.isEmpty(southWest) && TextUtils.isEmpty(northEast)) {
            return null;
        }

        LatLngBounds bounds = StringUtil.convertToLatLngBounds(southWest, northEast);
        if (bounds == null) {
//            showErrorAlert(R.string.error_alert_message_invalid_bounds);
            return null;
        }

        return RectangularBounds.newInstance(bounds);
    }

    @Nullable
    private TypeFilter getTypeFilter() {
//    Spinner typeFilter = findViewById(R.id.autocomplete_type_filter);
//    return typeFilter.isEnabled() ? (TypeFilter) typeFilter.getSelectedItem() : null;
        return null;
    }

    private AutocompleteActivityMode getMode() {
//    boolean isOverlayMode =
//        ((CheckBox) findViewById(R.id.autocomplete_activity_overlay_mode)).isChecked();
//    return isOverlayMode ? AutocompleteActivityMode.OVERLAY : AutocompleteActivityMode.FULLSCREEN;
        return AutocompleteActivityMode.OVERLAY;
    }

    private boolean isDisplayRawResultsChecked() {
        return ((CheckBox) findViewById(R.id.display_raw_results)).isChecked();
    }

    private void setLoading(boolean loading) {
        findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
    private void setLoadingNearby(boolean loading) {
        findViewById(R.id.loading_nearby).setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    private void showErrorAlert(@StringRes int messageResId) {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            client = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Latitude = String.valueOf(R.string.latitude_525_boston);
//                Longitude = String.valueOf(R.string.longitude_525_boston);
                Latitude = "42.3376835";
                Longitude = "-71.0963538";
                Log.v(TAG,"here 1");
                Log.v(TAG,"Longitude:" + Longitude);
                Log.v(TAG,"Latitude:" + Latitude);
                loadNearByPlaces("restaurant");
                return;
            }
            client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.v("Error","Error 1");
                        Latitude = Double.toString(location.getLatitude());
                        Longitude = Double.toString(location.getLongitude());
                        loadNearByPlaces("restaurant");
//                        getNearByLocations(Longitude, Latitude);
                    }
//                    else {
//                        Latitude = "42.3376835";
//                        Longitude = "-71.0963538";
//                        loadNearByPlaces("restaurant");
//                    }
                }
            });
        }
    }

    private void getNearByLocations(String longitude, String latitude) {
//        Log.v(TAG,"Longitude:" + Longitude);
//        Log.v(TAG,"Latitude:" + Latitude);
    }

    private void loadNearByPlaces(String type)
    {
        setLoadingNearby(true);
        RequestQueue requestQueue;

// Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), (1024)); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

// Start the queue
        requestQueue.start();
        Intent i = getIntent();
//        String type = "restaurant";
        String field = "id,photos,formatted_address,name,rating,opening_hours,geometry";
                //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.3376842,-71.0963798&rankby=distance&types=restaurant&fields=id,photos,formatted_address,name,rating,opening_hours,geometry&key=AIzaSyCpe7Vz_R4eRdZClnuF4jLBdAPgJmamFIM
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(Latitude).append(",").append(Longitude);
        googlePlacesUrl.append("&rankby=").append("distance");
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&fields=").append(field);
//        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + apiKey);
        Log.v(TAG,"Longitude:" + Longitude);
        Log.v(TAG,"Latitude:" + Latitude);
        Log.v(TAG, googlePlacesUrl.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, googlePlacesUrl.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        lat_long.setText("Response: " + response.toString());
                        parseJSONData(response);
                        setLoadingNearby(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.v(TAG, error.toString());
                        setLoadingNearby(false);
                    }
                });
        requestQueue.add(jsonObjectRequest);

    }
    private void parseJSONData(JSONObject data){
        id_list.clear();
        name_list.clear();
        vicinity_list.clear();
//        Log.v(TAG,data.toString());

        try {
            JSONArray results = data.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                id_list.add(result.getString("place_id"));
                name_list.add(result.getString("name"));
                vicinity_list.add(result.getString("vicinity"));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        setNearbyLocationView();
    }

    private void setNearbyLocationView() {
        ListView list;
        ArrayList<String> temp_id_list = limitSize(id_list);
        ArrayList<String> temp_name_list = limitSize(name_list);
        ArrayList<String> temp_vicinity_list = limitSize(vicinity_list);
        NearbyListAdapter adapter=new NearbyListAdapter(this, temp_id_list,temp_name_list,temp_vicinity_list);
        list=(ListView)findViewById(R.id.list_view);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(MainActivity.this, Business_Home.class);
            intent = intent.putExtra("place_data", id_list.get(position));

            startActivity(intent);
        });

    }

    private ArrayList<String> limitSize(ArrayList<String> AL) {
        ArrayList<String> temp = new ArrayList<>();
        for(int i =0; i<6;i++){
            temp.add(AL.get(i));
        }
        return temp;
    }

}
