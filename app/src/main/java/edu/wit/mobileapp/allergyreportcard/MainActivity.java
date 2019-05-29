package edu.wit.mobileapp.allergyreportcard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;
    private PlacesClient placesClient;
    private TextView responseView;
    private TextView lat_long;
    private FieldSelector fieldSelector;

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

        String apiKey = getString(R.string.places_api_key);

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
        responseView = findViewById(R.id.response);

        fieldSelector =
                new FieldSelector(
                        findViewById(R.id.use_custom_fields), findViewById(R.id.custom_fields_list));

        setupAutocompleteSupportFragment();

        // UI initialization
        setLoading(false);

        lat_long = findViewById(R.id.lat_long);


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
}
