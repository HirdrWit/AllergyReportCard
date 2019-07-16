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

public class PlaceLodgingAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private int review_count = 0;
    private ArrayList<String> ff_cleaning_AL;
    private ArrayList<String> ff_staff_AL;
    private ArrayList<String> ff_linens_AL;
    private ArrayList<String> smoke_AL;
    private ArrayList<String> floor_AL;
    private ArrayList<String> AL_review_AL;
    private ArrayList<String> username_AL;
    private String TAG = "PlaceFoodAdapter";
    private String review_category_builder;
    public PlaceLodgingAdapter(Activity context,
                               ArrayList<String> ff_cleaning_AL,
                               ArrayList<String> ff_staff_AL,
                               ArrayList<String> ff_linens_AL,
                               ArrayList<String> smoke_AL,
                               ArrayList<String> floor_AL,
                               ArrayList<String> AL_review_AL,
                               ArrayList<String> username_AL) {
        super(context, R.layout.food_listview, ff_cleaning_AL);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.ff_cleaning_AL=ff_cleaning_AL;
        this.ff_staff_AL=ff_staff_AL;
        this.ff_linens_AL=ff_linens_AL;
        this.smoke_AL=smoke_AL;
        this.floor_AL=floor_AL;
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
        int ff_cleaning_rating = getScore(ff_cleaning_AL.get(position));
        buildReviewCategory(ff_cleaning_rating, "Fragrance Free Cleaning Products|");
        int ff_staff_rating = getScore(ff_staff_AL.get(position));
        buildReviewCategory(ff_staff_rating, "Fragrance Free Staff|");
        int ff_linens_rating = getScore(ff_linens_AL.get(position));
        buildReviewCategory(ff_linens_rating, "Fragrance Free Linen|");
        int smoke_rating = getScore(smoke_AL.get(position));
        buildReviewCategory(smoke_rating, "Smoke|");
        buildReviewCategoryFloor(floor_AL.get(position));

        int sum = ff_cleaning_rating + ff_staff_rating + ff_linens_rating + smoke_rating;
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
    private void buildReviewCategoryFloor(String floor) {
        review_category_builder = review_category_builder + "Floor : " + floor + "|" ;

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
