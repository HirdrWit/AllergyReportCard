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

public class PlaceCarRentalAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private int review_count = 0;
    private ArrayList<String> ff_interior_AL= new ArrayList<>();
    private ArrayList<String> sf_interior_AL= new ArrayList<>();
    private ArrayList<String> car_interior_AL= new ArrayList<>();
    private ArrayList<String> AL_review_AL;
    private ArrayList<String> username_AL;
    private String TAG = "PlaceCarAdapter";
    private String review_category_builder;
    public PlaceCarRentalAdapter(Activity context,
                               ArrayList<String> ff_interior_AL,
                               ArrayList<String> sf_interior_AL,
                               ArrayList<String> car_interior_AL,
                               ArrayList<String> AL_review_AL,
                               ArrayList<String> username_AL) {
        super(context, R.layout.food_listview, ff_interior_AL);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.ff_interior_AL=ff_interior_AL;
        this.sf_interior_AL=sf_interior_AL;
        this.car_interior_AL=car_interior_AL;
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
        int ff_interior_rating = getScore(ff_interior_AL.get(position));
        buildReviewCategory(ff_interior_rating, "Fragrance Free Car Interior|");
        int sf_interior_rating = getScore(sf_interior_AL.get(position));
        buildReviewCategory(sf_interior_rating, "Smoke Free Car Interior|");

        buildReviewCategoryInterior(car_interior_AL.get(position));

        int sum = ff_interior_rating + sf_interior_rating;
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
    private void buildReviewCategoryInterior(String interior) {
        review_category_builder = review_category_builder + "Interior : " + interior + "|" ;

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
