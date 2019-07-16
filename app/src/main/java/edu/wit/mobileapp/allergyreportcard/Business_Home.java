package edu.wit.mobileapp.allergyreportcard;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Business_Home extends AppCompatActivity {
    private String Name;
    private String Address;
    private String Id;
    private String PhoneNumber;
    private String PriceLevel;
    private String Attributions;
    private String WebsiteUri;
    private String Types ="";
    private PlacesClient placesClient;
    private ImageView photoView;
    private FieldSelector fieldSelector;
    private TextView name_TextView;
    private TextView type_TextView;
    private Button button_place_phone;
    private Button button_place_website;
    private Button button_place_address;
    private String TAG = "Business_Home_Log";
    private String apiKey;
    private String place_id;
    private String dairy_text;
    private String eggs_text;
    private String fish_text;
    private String shellfish_text;
    private String peanuts_text;
    private String tree_nuts_text;
    private String wheat_text;
    private String soy_text;
    private String ff_cleaning_text;
    private String ff_linens_text;
    private String ff_staff_text;
    private String smoke_text;
    private String floor_text;
    private String sesame_text;
    private String rice_text;
    private String sulphites_text;
    private String es_text;
    private String text_review_text;
    private Boolean already_loaded_reviews = false;
    private ArrayList<String> dairy_AL= new ArrayList<>();
    private ArrayList<String> eggs_AL= new ArrayList<>();
    private ArrayList<String> fish_AL= new ArrayList<>();
    private ArrayList<String> shellfish_AL= new ArrayList<>();
    private ArrayList<String> peanuts_AL= new ArrayList<>();
    private ArrayList<String> tree_nuts_AL= new ArrayList<>();
    private ArrayList<String> wheat_AL= new ArrayList<>();
    private ArrayList<String> soy_AL= new ArrayList<>();
    private ArrayList<String> ff_cleaning_AL= new ArrayList<>();
    private ArrayList<String> ff_staff_AL= new ArrayList<>();
    private ArrayList<String> ff_linens_AL= new ArrayList<>();
    private ArrayList<String> smoke_AL= new ArrayList<>();
    private ArrayList<String> sesame_AL= new ArrayList<>();
    private ArrayList<String> rice_AL= new ArrayList<>();
    private ArrayList<String> sulphites_AL= new ArrayList<>();
    private ArrayList<String> es_AL= new ArrayList<>();
    private ArrayList<String> AL_review_AL= new ArrayList<>();
    private ArrayList<String> username_AL= new ArrayList<>();
    private ArrayList<String> floor_AL= new ArrayList<>();

    public static final String DAIRY_KEY = "dairy";
    public static final String EGGS_KEY = "eggs";
    public static final String FISH_KEY = "fish";
    public static final String SHELLFISH_KEY = "shellfish";
    public static final String PEANUTS_KEY = "peanuts";
    public static final String TREE_NUTS_KEY = "tree_nuts";
    public static final String WHEAT_KEY = "wheat";
    public static final String SOY_KEY = "soy";
    public static final String FF_C_KEY = "fragrance_free_cleaning";
    public static final String FF_S_KEY = "fragrance_free_staff";
    public static final String FF_L_KEY = "fragrance_free_linens";
    public static final String SMOKE_KEY = "smoke";
    public static final String SESAME_KEY = "sesame";
    public static final String RICE_KEY = "rice";
    public static final String SULPHITES_KEY = "sulphites";
    public static final String ES_KEY = "electrical_sensitivity";
    public static final String FLOOR_KEY = "floor";
    public static final String TEXT_KEY = "text_review";
    public static final String USERNAME_KEY = "user_name";
    public static final String USER_ID_KEY = "user_id";
    private int tot_review_count = 0;

    private DocumentReference mDocRef;
    private CollectionReference mColRef;
    TextView mQuoteTextView;
    private PopupWindow popupWindow;
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
        place_id = intent.getStringExtra("place_data");
        // Capture the layout's TextView and set the string as its text
        name_TextView = findViewById(R.id.places_name);
        type_TextView = findViewById(R.id.places_type);
        button_place_address = findViewById(R.id.button_place_address);
        button_place_phone = findViewById(R.id.button_place_phone);
        button_place_website = findViewById(R.id.button_place_website);

        getValues(place_id);

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
    public void openReview(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView;
        Log.v(TAG,Types);
        if(Types.contains("food") || Types.contains("restaurant")) {
            popupView = inflater.inflate(R.layout.food_review_prompt, null);
            Log.v(TAG,"Food Review Prompt");
        }
        else if(Types.contains("lodging")){
            popupView = inflater.inflate(R.layout.hotel_review_prompt, null);
            Log.v(TAG,"Lodging Review Prompt");
        }
        else if(Types.contains("car_rental")){
            popupView = inflater.inflate(R.layout.food_review_prompt, null);
            Log.v(TAG,"Car Review Prompt");
        }
        else{
            popupView = inflater.inflate(R.layout.food_review_prompt, null);
        }
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button close_review =  (Button) popupView.findViewById(R.id.review_prompt_close);
        close_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button save_review =  (Button) popupView.findViewById(R.id.review_prompt_save);
        save_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Types.contains("food") || Types.contains("restaurant")) {
                    RadioGroup dairy_radioGroup = (RadioGroup) popupView.findViewById(R.id.dairy_RadioGroup);
                    RadioGroup eggs_radioGroup = (RadioGroup) popupView.findViewById(R.id.eggs_RadioGroup);
                    RadioGroup fish_radioGroup = (RadioGroup) popupView.findViewById(R.id.fish_RadioGroup);
                    RadioGroup shellfish_radioGroup = (RadioGroup) popupView.findViewById(R.id.shellfish_RadioGroup);
                    RadioGroup peanuts_radioGroup = (RadioGroup) popupView.findViewById(R.id.peanuts_RadioGroup);
                    RadioGroup tree_nuts_radioGroup = (RadioGroup) popupView.findViewById(R.id.tree_nuts_RadioGroup);
                    RadioGroup wheat_radioGroup = (RadioGroup) popupView.findViewById(R.id.wheat_RadioGroup);
                    RadioGroup soy_radioGroup = (RadioGroup) popupView.findViewById(R.id.soy_RadioGroup);
                    RadioGroup ff_cleaning_products_radioGroup = (RadioGroup) popupView.findViewById(R.id.ff_cleaning_products_RadioGroup);
                    RadioGroup ff_staff_radioGroup = (RadioGroup) popupView.findViewById(R.id.ff_staff_RadioGroup);
                    RadioGroup sesame_radioGroup = (RadioGroup) popupView.findViewById(R.id.sesame_RadioGroup);
                    RadioGroup rice_radioGroup = (RadioGroup) popupView.findViewById(R.id.rice_RadioGroup);
                    RadioGroup sulphites_radioGroup = (RadioGroup) popupView.findViewById(R.id.sulphites_RadioGroup);
                    RadioGroup es_radioGroup = (RadioGroup) popupView.findViewById(R.id.es_RadioGroup);
                    EditText text_review = (EditText) popupView.findViewById(R.id.text_review);

                    int dairySelectedId = dairy_radioGroup.getCheckedRadioButtonId();
                    int eggsSelectedId = eggs_radioGroup.getCheckedRadioButtonId();
                    int fishSelectedId = fish_radioGroup.getCheckedRadioButtonId();
                    int shellfishSelectedId = shellfish_radioGroup.getCheckedRadioButtonId();
                    int peanutsSelectedId = peanuts_radioGroup.getCheckedRadioButtonId();
                    int tree_nuts_SelectedId = tree_nuts_radioGroup.getCheckedRadioButtonId();
                    int wheatSelectedId = wheat_radioGroup.getCheckedRadioButtonId();
                    int soySelectedId = soy_radioGroup.getCheckedRadioButtonId();
                    int ff_cleaning_SelectedId = ff_cleaning_products_radioGroup.getCheckedRadioButtonId();
                    int ff_staff_SelectedId = ff_staff_radioGroup.getCheckedRadioButtonId();
                    int sesameSelectedId = sesame_radioGroup.getCheckedRadioButtonId();
                    int riceSelectedId = rice_radioGroup.getCheckedRadioButtonId();
                    int sulphitesSelectedId = sulphites_radioGroup.getCheckedRadioButtonId();
                    int esSelectedId = es_radioGroup.getCheckedRadioButtonId();


                    RadioButton dairy_radioButton = (RadioButton) popupView.findViewById(dairySelectedId);
                    RadioButton eggs_radioButton = (RadioButton) popupView.findViewById(eggsSelectedId);
                    RadioButton fish_radioButton = (RadioButton) popupView.findViewById(fishSelectedId);
                    RadioButton shellfish_radioButton = (RadioButton) popupView.findViewById(shellfishSelectedId);
                    RadioButton peanuts_radioButton = (RadioButton) popupView.findViewById(peanutsSelectedId);
                    RadioButton tree_nuts_radioButton = (RadioButton) popupView.findViewById(tree_nuts_SelectedId);
                    RadioButton wheat_radioButton = (RadioButton) popupView.findViewById(wheatSelectedId);
                    RadioButton soy_radioButton = (RadioButton) popupView.findViewById(soySelectedId);
                    RadioButton ff_cleaning_radioButton = (RadioButton) popupView.findViewById(ff_cleaning_SelectedId);
                    RadioButton ff_staff_radioButton = (RadioButton) popupView.findViewById(ff_staff_SelectedId);
                    RadioButton sesame_radioButton = (RadioButton) popupView.findViewById(sesameSelectedId);
                    RadioButton rice_radioButton = (RadioButton) popupView.findViewById(riceSelectedId);
                    RadioButton sulphites_radioButton = (RadioButton) popupView.findViewById(sulphitesSelectedId);
                    RadioButton es_radioButton = (RadioButton) popupView.findViewById(esSelectedId);

                    dairy_text = dairy_radioButton.getText().toString();
                    eggs_text = eggs_radioButton.getText().toString();
                    fish_text = fish_radioButton.getText().toString();
                    shellfish_text = shellfish_radioButton.getText().toString();
                    peanuts_text = peanuts_radioButton.getText().toString();
                    tree_nuts_text = tree_nuts_radioButton.getText().toString();
                    wheat_text = wheat_radioButton.getText().toString();
                    soy_text = soy_radioButton.getText().toString();
                    ff_cleaning_text = ff_cleaning_radioButton.getText().toString();
                    ff_staff_text = ff_staff_radioButton.getText().toString();
                    sesame_text = sesame_radioButton.getText().toString();
                    rice_text = rice_radioButton.getText().toString();
                    sulphites_text = sulphites_radioButton.getText().toString();
                    es_text = es_radioButton.getText().toString();
                    text_review_text = text_review.getText().toString();
                    if( TextUtils.isEmpty(text_review.getText())){
                        text_review.setError( "Please write a review." );

                        Context context = getApplicationContext();
                        CharSequence text = "Please write a review.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else{
                        saveFoodReview();
                        popupWindow.dismiss();
                    }
                }
                else if(Types.contains("lodging")){

                    RadioGroup ff_cleaning_products_radioGroup = (RadioGroup) popupView.findViewById(R.id.fragrance_free_cleaning_RadioGroup);
                    RadioGroup ff_staff_radioGroup = (RadioGroup) popupView.findViewById(R.id.fragrance_free_staff_RadioGroup);
                    RadioGroup ff_linen_radioGroup = (RadioGroup) popupView.findViewById(R.id.fragrance_free_linens_RadioGroup);
                    RadioGroup smoke_radioGroup = (RadioGroup) popupView.findViewById(R.id.smoke_free_RadioGroup);
                    RadioGroup floor_radioGroup = (RadioGroup) popupView.findViewById(R.id.floor_RadioGroup);
                    EditText text_review = (EditText) popupView.findViewById(R.id.text_review);

                    int ff_cleaning_SelectedId = ff_cleaning_products_radioGroup.getCheckedRadioButtonId();
                    int ff_staff_SelectedId = ff_staff_radioGroup.getCheckedRadioButtonId();
                    int ff_linen_SelectedId = ff_linen_radioGroup.getCheckedRadioButtonId();
                    int smokeSelectedId = smoke_radioGroup.getCheckedRadioButtonId();
                    int floorSelectedId = floor_radioGroup.getCheckedRadioButtonId();


                    RadioButton ff_cleaning_radioButton = (RadioButton) popupView.findViewById(ff_cleaning_SelectedId);
                    RadioButton ff_staff_radioButton = (RadioButton) popupView.findViewById(ff_staff_SelectedId);
                    RadioButton ff_linen_radioButton = (RadioButton) popupView.findViewById(ff_linen_SelectedId);
                    RadioButton smoke_radioButton = (RadioButton) popupView.findViewById(smokeSelectedId);
                    RadioButton floor_radioButton = (RadioButton) popupView.findViewById(floorSelectedId);

                    ff_cleaning_text = ff_cleaning_radioButton.getText().toString();
                    ff_staff_text = ff_staff_radioButton.getText().toString();
                    ff_linens_text = ff_linen_radioButton.getText().toString();
                    smoke_text = smoke_radioButton.getText().toString();
                    floor_text = floor_radioButton.getText().toString();
                    text_review_text = text_review.getText().toString();
                    if( TextUtils.isEmpty(text_review.getText())){
                        text_review.setError( "Please write a review." );

                        Context context = getApplicationContext();
                        CharSequence text = "Please write a review.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else{
                        saveHotelReview();
                        popupWindow.dismiss();
                    }
                }
                else if(Types.contains("car_rental")){

                    Log.v(TAG,"Not Working");
                }
                else{
                    Log.v(TAG,"Not Working");
                }
            }
            });
    }

    public void saveFoodReview() { //set to boolean
        mColRef = FirebaseFirestore.getInstance().collection("review/place_id/" + place_id);
        Map<String,Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(USERNAME_KEY, "Beta User");
        dataToSave.put(USER_ID_KEY, "000000");
        dataToSave.put(DAIRY_KEY, dairy_text);
        dataToSave.put(EGGS_KEY, eggs_text);
        dataToSave.put(FISH_KEY, fish_text);
        dataToSave.put(SHELLFISH_KEY, shellfish_text);
        dataToSave.put(PEANUTS_KEY, peanuts_text);
        dataToSave.put(TREE_NUTS_KEY, tree_nuts_text);
        dataToSave.put(WHEAT_KEY, wheat_text);
        dataToSave.put(SOY_KEY, soy_text);
        dataToSave.put(FF_C_KEY, ff_cleaning_text);
        dataToSave.put(FF_S_KEY, ff_staff_text);
        dataToSave.put(SESAME_KEY, sesame_text);
        dataToSave.put(RICE_KEY, rice_text);
        dataToSave.put(SULPHITES_KEY, sulphites_text);
        dataToSave.put(ES_KEY, es_text);
        dataToSave.put(TEXT_KEY, text_review_text);

        mColRef.add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                FetchNewReview();
                Log.v(TAG, "no problems sending review");
            }
        });

    }
    public void saveHotelReview() { //set to boolean
        mColRef = FirebaseFirestore.getInstance().collection("review/place_id/" + place_id);
        Map<String,Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(USERNAME_KEY, "Beta User");
        dataToSave.put(USER_ID_KEY, "000000");
        dataToSave.put(FF_C_KEY, ff_cleaning_text);
        dataToSave.put(FF_S_KEY, ff_staff_text);
        dataToSave.put(FF_L_KEY, ff_linens_text);
        dataToSave.put(SMOKE_KEY, smoke_text);
        dataToSave.put(FLOOR_KEY, floor_text);
        dataToSave.put(TEXT_KEY, text_review_text);

        mColRef.add(dataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                FetchNewReview();
                Log.v(TAG, "no problems sending review");
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        if(!already_loaded_reviews) {
            already_loaded_reviews = true;
            mColRef = FirebaseFirestore.getInstance().collection("review/place_id/" + place_id);
            mColRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (Types.contains("food") || Types.contains("restaurant")) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        dairy_AL.add(document.getData().get("dairy").toString());
                                        eggs_AL.add(document.getData().get("eggs").toString());
                                        fish_AL.add(document.getData().get("fish").toString());
                                        shellfish_AL.add(document.getData().get("shellfish").toString());
                                        peanuts_AL.add(document.getData().get("peanuts").toString());
                                        tree_nuts_AL.add(document.getData().get("tree_nuts").toString());
                                        wheat_AL.add(document.getData().get("wheat").toString());
                                        soy_AL.add(document.getData().get("soy").toString());
                                        ff_cleaning_AL.add(document.getData().get("fragrance_free_cleaning").toString());
                                        ff_staff_AL.add(document.getData().get("fragrance_free_staff").toString());
                                        sesame_AL.add(document.getData().get("sesame").toString());
                                        rice_AL.add(document.getData().get("rice").toString());
                                        sulphites_AL.add(document.getData().get("sulphites").toString());
                                        es_AL.add(document.getData().get("electrical_sensitivity").toString());
                                        AL_review_AL.add(document.getData().get("text_review").toString());
                                        username_AL.add(document.getData().get("user_name").toString());

//                                    Log.v(TAG, document.getData().get("dairy").toString());
                                    }
                                    setFoodAdapter();
                                }
                                if (Types.contains("lodging")) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ff_cleaning_AL.add(document.getData().get("fragrance_free_cleaning").toString());
                                        ff_staff_AL.add(document.getData().get("fragrance_free_staff").toString());
                                        ff_linens_AL.add(document.getData().get("fragrance_free_linens").toString());
                                        smoke_AL.add(document.getData().get("smoke").toString());
                                        floor_AL.add(document.getData().get("floor").toString());
                                        AL_review_AL.add(document.getData().get("text_review").toString());
                                        username_AL.add(document.getData().get("user_name").toString());

//                                    Log.v(TAG, document.getData().get("dairy").toString());
                                    }
                                    Log.v(TAG, "Called setLodgingAdapter()");

                                    setLodgingAdapter();
                                }
                            } else {
                                Log.v(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void setFoodAdapter() {
        ListView list;
        PlaceFoodAdapter adapter=new PlaceFoodAdapter(this,
                dairy_AL,
                eggs_AL,
                fish_AL,
                shellfish_AL,
                peanuts_AL,
                tree_nuts_AL,
                wheat_AL,
                soy_AL,
                ff_cleaning_AL,
                ff_staff_AL,
                sesame_AL,
                rice_AL,
                sulphites_AL,
                es_AL,
                AL_review_AL,
                username_AL);
        list=(ListView)findViewById(R.id.list_view);
        list.setAdapter(adapter);
        getOverallFoodReviewImage();
    }
    private void setLodgingAdapter() {
        ListView list;
        PlaceLodgingAdapter adapter=new PlaceLodgingAdapter(this,
                ff_cleaning_AL,
                ff_staff_AL,
                ff_linens_AL,
                smoke_AL,
                floor_AL,
                AL_review_AL,
                username_AL);
        list=(ListView)findViewById(R.id.list_view);
        list.setAdapter(adapter);
        getOverallLodgingReviewImage();
    }
    private void getOverallFoodReviewImage() {
        int score = getTotalFoodScore();
        ImageView reviewImage = (ImageView) findViewById(R.id.review_image);
        Drawable img_value = setImageScore(score);
        reviewImage.setImageDrawable(img_value);
    }
    private void getOverallLodgingReviewImage() {
        int score = getTotalLodgingScore();
        ImageView reviewImage = (ImageView) findViewById(R.id.review_image);
        Drawable img_value = setImageScore(score);
        reviewImage.setImageDrawable(img_value);
    }
    private Drawable setImageScore(int score) {
        if(score>80){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return getApplicationContext().getDrawable(R.drawable.alpha_a_circle_outline);
            } else {
                return getResources().getDrawable(R.drawable.alpha_a_circle_outline);
            }
        }
        else if(score > 60){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return getApplicationContext().getDrawable(R.drawable.alpha_b_circle_outline);
            } else {
                return getResources().getDrawable(R.drawable.alpha_b_circle_outline);
            }
        }
        else if(score > 40){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return getApplicationContext().getDrawable(R.drawable.alpha_c_circle_outline);
            } else {
                return getResources().getDrawable(R.drawable.alpha_c_circle_outline);
            }
        }
        else if(score > 20){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return getApplicationContext().getDrawable(R.drawable.alpha_d_circle_outline);
            } else {
                return getResources().getDrawable(R.drawable.alpha_d_circle_outline);
            }
        }
        else if(score>0){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return getApplicationContext().getDrawable(R.drawable.alpha_f_circle_outline);
            } else {
                return getResources().getDrawable(R.drawable.alpha_f_circle_outline);
            }
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return getApplicationContext().getDrawable(R.drawable.help_circle_outline);
            } else {
                return getResources().getDrawable(R.drawable.help_circle_outline);
            }
        }

    }
    private int getTotalFoodScore() {
        tot_review_count=0;
        int dairy_rating = getScore(dairy_AL);
        int eggs_rating = getScore(eggs_AL);
        int fish_rating = getScore(fish_AL);
        int shellfish_rating = getScore(shellfish_AL);
        int peanuts_rating = getScore(peanuts_AL);
        int tree_nuts_rating = getScore(tree_nuts_AL);
        int wheat_rating = getScore(wheat_AL);
        int soy_rating = getScore(soy_AL);
        int ff_cleaning_rating = getScore(ff_cleaning_AL);
        int ff_staff_rating = getScore(ff_staff_AL);
        int seasame_rating = getScore(sesame_AL);
        int rice_rating = getScore(rice_AL);
        int sulphites_rating = getScore(sulphites_AL);
        int es_rating = getScore(es_AL);
        int sum = dairy_rating + eggs_rating + fish_rating + shellfish_rating + peanuts_rating + tree_nuts_rating + wheat_rating
                + soy_rating + ff_cleaning_rating + ff_staff_rating + seasame_rating + rice_rating + sulphites_rating + es_rating;
        if(tot_review_count>0) {
            Log.v("BUG",String.valueOf(sum));
            Log.v("BUG",String.valueOf(tot_review_count));
            return(sum / tot_review_count-1);
        }
        return 0;
    }

    private int getTotalLodgingScore() {
        tot_review_count=0;
        int ff_cleaning_rating = getScore(ff_cleaning_AL);
        int ff_staff_rating = getScore(ff_staff_AL);
        int ff_linens_rating = getScore(ff_linens_AL);
        int smoke_rating = getScore(smoke_AL);
        int sum = ff_cleaning_rating + ff_staff_rating +ff_linens_rating + smoke_rating;

        if(tot_review_count>0) {
            Log.v("BUG",String.valueOf(sum));
            Log.v("BUG",String.valueOf(tot_review_count));
            return(sum / tot_review_count-1);
        }
        return 0;
    }
    private int getScore(ArrayList<String> al) {
        int tot = 0;
        int review_count = 0;
        for(int i = 0; i<al.size(); i++){
            if(al.get(i).equals("A")){
                review_count +=1;
                tot += 100;
            }
            else if(al.get(i).equals("B")){
                review_count +=1;
                tot += 80;
            }
            else if(al.get(i).equals("C")){
                review_count +=1;
                tot += 60;
            }
            else if(al.get(i).equals("D")){
                review_count +=1;
                tot += 40;
            }
            else if(al.get(i).equals("F")){
                review_count +=1;
                tot += 20;
            }
            else{
                tot += 0;
            }
        }
        if(tot>0){
            tot_review_count+=1;
            Log.v(TAG,al.toString());
            return tot/review_count;
        }
        return 0;
    }

    public void FetchNewReview(){
        mColRef = FirebaseFirestore.getInstance().collection("review/place_id/" + place_id);
        mColRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Types.contains("food") || Types.contains("restaurant")) {
                                dairy_AL.add(dairy_text);
                                eggs_AL.add(eggs_text);
                                fish_AL.add(fish_text);
                                shellfish_AL.add(shellfish_text);
                                peanuts_AL.add(peanuts_text);
                                tree_nuts_AL.add(tree_nuts_text);
                                wheat_AL.add(wheat_text);
                                soy_AL.add(soy_text);
                                ff_cleaning_AL.add(ff_cleaning_text);
                                ff_staff_AL.add(ff_staff_text);
                                sesame_AL.add(sesame_text);
                                rice_AL.add(rice_text);
                                sulphites_AL.add(sulphites_text);
                                es_AL.add(es_text);
                                AL_review_AL.add(text_review_text);
                                username_AL.add("Beta User");
                                setFoodAdapter();

                            }
                            else if (Types.contains("lodging")){
                                ff_cleaning_AL.add(ff_cleaning_text);
                                ff_staff_AL.add(ff_staff_text);
                                ff_linens_AL.add(ff_linens_text);
                                smoke_AL.add(smoke_text);
                                floor_AL.add(floor_text);
                                AL_review_AL.add(text_review_text);
                                username_AL.add("Beta User");
                                setLodgingAdapter();

                            }
                        } else {
                            Log.v(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void setValues() {
        name_TextView.setText(Name);
        button_place_address.setText(Address);
        button_place_address.setTextColor(0);
        button_place_phone.setText(PhoneNumber);
        button_place_phone.setTextColor(0);
        button_place_website.setText(WebsiteUri);
        button_place_website.setTextColor(0);
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
        else{
            photoView.setImageResource(R.drawable.image_off);
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
                    Log.v(TAG,"No Image");
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