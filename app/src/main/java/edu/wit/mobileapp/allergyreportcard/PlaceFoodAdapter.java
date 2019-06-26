package edu.wit.mobileapp.allergyreportcard;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaceFoodAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private int review_count = 0;
    private ArrayList<String> dairy_AL;
    private ArrayList<String> eggs_AL;
    private ArrayList<String> fish_AL;
    private ArrayList<String> shellfish_AL;
    private ArrayList<String> peanuts_AL;
    private ArrayList<String> tree_nuts_AL;
    private ArrayList<String> wheat_AL;
    private ArrayList<String> soy_AL;
    private ArrayList<String> ff_cleaning_AL;
    private ArrayList<String> ff_staff_AL;
    private ArrayList<String> sesame_AL;
    private ArrayList<String> rice_AL;
    private ArrayList<String> sulphites_AL;
    private ArrayList<String> es_AL;
    private ArrayList<String> AL_review_AL;
    private ArrayList<String> username_AL;
    private String TAG = "PlaceFoodAdapter";
    public PlaceFoodAdapter(Activity context,
                            ArrayList<String> dairy_AL,
                            ArrayList<String> eggs_AL,
                            ArrayList<String> fish_AL,
                            ArrayList<String> shellfish_AL,
                            ArrayList<String> peanuts_AL,
                            ArrayList<String> tree_nuts_AL,
                            ArrayList<String> wheat_AL,
                            ArrayList<String> soy_AL,
                            ArrayList<String> ff_cleaning_AL,
                            ArrayList<String> ff_staff_AL,
                            ArrayList<String> sesame_AL,
                            ArrayList<String> rice_AL,
                            ArrayList<String> sulphites_AL,
                            ArrayList<String> es_AL,
                            ArrayList<String> AL_review_AL,
                            ArrayList<String> username_AL) {
        super(context, R.layout.food_listview, dairy_AL);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.dairy_AL=dairy_AL;
        this.eggs_AL=eggs_AL;
        this.fish_AL=fish_AL;
        this.shellfish_AL=shellfish_AL;
        this.peanuts_AL=peanuts_AL;
        this.tree_nuts_AL=tree_nuts_AL;
        this.wheat_AL=wheat_AL;
        this.soy_AL=soy_AL;
        this.ff_cleaning_AL=ff_cleaning_AL;
        this.ff_staff_AL=ff_staff_AL;
        this.sesame_AL=sesame_AL;
        this.rice_AL=rice_AL;
        this.sulphites_AL=sulphites_AL;
        this.es_AL=es_AL;
        this.AL_review_AL=AL_review_AL;
        this.username_AL=username_AL;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.food_listview, null,true);

        TextView usernameText = (TextView) rowView.findViewById(R.id.username);
        TextView reviewText = (TextView) rowView.findViewById(R.id.AL_review);
        ImageView reviewImage = (ImageView) rowView.findViewById(R.id.review_image);

        usernameText.setText(username_AL.get(position));
        reviewText.setText(AL_review_AL.get(position));
        int Score = getTotalScore(position);

        return rowView;

    }

    private int getTotalScore(int position) {
        int dairy_rating = getScore(dairy_AL.get(position));
        int eggs_rating = getScore(eggs_AL.get(position));
        int fish_rating = getScore(fish_AL.get(position));
        int shellfish_rating = getScore(shellfish_AL.get(position));
        int peanuts_rating = getScore(peanuts_AL.get(position));
        int tree_nuts_rating = getScore(tree_nuts_AL.get(position));
        int wheat_rating = getScore(wheat_AL.get(position));
        int soy_rating = getScore(soy_AL.get(position));
        int ff_cleaning_rating = getScore(ff_cleaning_AL.get(position));
        int ff_staff_rating = getScore(ff_staff_AL.get(position));
        int seasame_rating = getScore(sesame_AL.get(position));
        int rice_rating = getScore(rice_AL.get(position));
        int sulphites_rating = getScore(sulphites_AL.get(position));
        int es_rating = getScore(es_AL.get(position));

        return 0;
    }

    private int getScore(String s) {
        /*A = 100 (80 - 100)
          B = 80  (60 - 80)
          C = 60  (40 - 60)
          D = 40  (20 - 40)
          F = 20  (0 - 20)*/
//        if(s.)


        return 0;
    }
}
