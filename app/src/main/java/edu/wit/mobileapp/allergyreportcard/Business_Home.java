package edu.wit.mobileapp.allergyreportcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

public class Business_Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_business__home);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("place_data");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.places_temp_results);

        try {
            message = ExtractJSON.GetPlace(message); // needs work
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.v("MyData", message);
        textView.setText(message);
    }
}
