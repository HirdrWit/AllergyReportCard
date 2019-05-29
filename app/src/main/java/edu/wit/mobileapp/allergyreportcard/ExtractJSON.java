package edu.wit.mobileapp.allergyreportcard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtractJSON {

    public static String GetPlace(String json) throws JSONException {
        final JSONObject obj = new JSONObject(json);
        final JSONArray places_data = obj.getJSONArray("Place");
        final int n = places_data.length();
        for (int i = 0; i < n; ++i) {
            final JSONObject person = places_data.getJSONObject(i);
            Log.v("MyData",person.getString("address"));
            Log.v("MyData",person.getString("name"));
        }

        return "Working";
    }
}
