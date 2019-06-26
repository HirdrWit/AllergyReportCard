package edu.wit.mobileapp.allergyreportcard;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

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
    private String review_category_builder;
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
        review_count = 0;
        review_category_builder = "Reviewed: ";
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.food_listview, null,true);

        TextView usernameText = (TextView) rowView.findViewById(R.id.username);
        TextView reviewText = (TextView) rowView.findViewById(R.id.AL_review);
        ImageView reviewImage = (ImageView) rowView.findViewById(R.id.review_image);
        TextView reviewed_categories = (TextView)rowView.findViewById(R.id.reviewed_categories) ;


        usernameText.setText(username_AL.get(position));
        reviewText.setText(AL_review_AL.get(position));
        int score = getTotalScore(position);
        Log.v(TAG, "Message "+ AL_review_AL.get(position) +" score: " + String.valueOf(score));
        Drawable img_value = setImageScore(score);
        reviewImage.setImageDrawable(img_value);
        reviewed_categories.setText(review_category_builder);
        return rowView;

    }

    private Drawable setImageScore(int score) {
        if(score>80){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getApplicationContext().getDrawable(R.drawable.alpha_a_circle_outline);
            } else {
                return context.getResources().getDrawable(R.drawable.alpha_a_circle_outline);
            }
        }
        else if(score > 60){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getApplicationContext().getDrawable(R.drawable.alpha_b_circle_outline);
            } else {
                return context.getResources().getDrawable(R.drawable.alpha_b_circle_outline);
            }
        }
        else if(score > 40){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getApplicationContext().getDrawable(R.drawable.alpha_c_circle_outline);
            } else {
                return context.getResources().getDrawable(R.drawable.alpha_c_circle_outline);
            }
        }
        else if(score > 20){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getApplicationContext().getDrawable(R.drawable.alpha_d_circle_outline);
            } else {
                return context.getResources().getDrawable(R.drawable.alpha_d_circle_outline);
            }
        }
        else if(score>0){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getApplicationContext().getDrawable(R.drawable.alpha_f_circle_outline);
            } else {
                return context.getResources().getDrawable(R.drawable.alpha_f_circle_outline);
            }
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getApplicationContext().getDrawable(R.drawable.help_circle_outline);
            } else {
                return context.getResources().getDrawable(R.drawable.help_circle_outline);
            }
        }

    }

    private int getTotalScore(int position) {
        int dairy_rating = getScore(dairy_AL.get(position));
        buildReviewCategory(dairy_rating, "Dairy|");
        int eggs_rating = getScore(eggs_AL.get(position));
        buildReviewCategory(eggs_rating, "Eggs|");
        int fish_rating = getScore(fish_AL.get(position));
        buildReviewCategory(fish_rating, "Fish|");
        int shellfish_rating = getScore(shellfish_AL.get(position));
        buildReviewCategory(shellfish_rating, "Shellfish|");
        int peanuts_rating = getScore(peanuts_AL.get(position));
        buildReviewCategory(peanuts_rating, "Peanuts|");
        int tree_nuts_rating = getScore(tree_nuts_AL.get(position));
        buildReviewCategory(tree_nuts_rating, "Tree Nuts|");
        int wheat_rating = getScore(wheat_AL.get(position));
        buildReviewCategory(wheat_rating, "Wheat|");
        int soy_rating = getScore(soy_AL.get(position));
        buildReviewCategory(soy_rating, "Soy|");
        int ff_cleaning_rating = getScore(ff_cleaning_AL.get(position));
        buildReviewCategory(ff_cleaning_rating, "Fragrance Free Cleaning Products|");
        int ff_staff_rating = getScore(ff_staff_AL.get(position));
        buildReviewCategory(ff_staff_rating, "Fragrance Free Staff|");
        int seasame_rating = getScore(sesame_AL.get(position));
        buildReviewCategory(seasame_rating, "Sesame|");
        int rice_rating = getScore(rice_AL.get(position));
        buildReviewCategory(rice_rating, "Rice|");
        int sulphites_rating = getScore(sulphites_AL.get(position));
        buildReviewCategory(sulphites_rating, "Sulphites|");
        int es_rating = getScore(es_AL.get(position));
        buildReviewCategory(es_rating, "Electrical Sensitivity|");

        int sum = dairy_rating + eggs_rating + fish_rating + shellfish_rating + peanuts_rating + tree_nuts_rating + wheat_rating
                + soy_rating + ff_cleaning_rating + ff_staff_rating + seasame_rating + rice_rating + sulphites_rating + es_rating;
        if(review_count>0) {
            return(sum / review_count);
        }

        return 0;
    }

    private void buildReviewCategory(int rating, String s) {
        if(rating>0){
            review_category_builder = review_category_builder + " " +s;
        }
    }

    private int getScore(String s) {
        /*A = 100 (80 - 100)
          B = 80  (60 - 80)
          C = 60  (40 - 60)
          D = 40  (20 - 40)
          F = 20  (0 - 20)*/
        if(s.equals("A")){
            review_count +=1;
            return 100;
        }
        else if(s.equals("B")){
            review_count +=1;
            return 80;
        }
        else if(s.equals("C")){
            review_count +=1;
            return 60;
        }
        else if(s.equals("D")){
            review_count +=1;
            return 40;
        }
        else if(s.equals("F")){
            review_count +=1;
            return 20;
        }
        else{
            return 0;
        }
    }
}
