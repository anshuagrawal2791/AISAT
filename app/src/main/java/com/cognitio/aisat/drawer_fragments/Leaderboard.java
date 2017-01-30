package com.cognitio.aisat.drawer_fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.Constants;
import com.cognitio.aisat.R;
import com.cognitio.aisat.VolleySingleton;
import com.facebook.TestUserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class Leaderboard extends Fragment {

    TableLayout t1;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;
    TextView class_;
    Random r = new Random();



    public Leaderboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_leaderboard, container, false);

        t1 = (TableLayout)v.findViewById(R.id.main_table);


        TableRow tr_head = new TableRow(getContext());
        tr_head.setId(r.nextInt(1000+1));
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        TextView label_date = new TextView(getContext());
        label_date.setId(r.nextInt(1000+1));
        label_date.setText("Rank");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        label_date.setGravity(Gravity.CENTER);

        tr_head.addView(label_date);// add the column to the table row here

        TextView label_weight_kg = new TextView(getContext());
        label_weight_kg.setId(r.nextInt(1000+1));// define id that must be unique
        label_weight_kg.setText("Name"); // set the text for the header
        label_weight_kg.setTextColor(Color.WHITE); // set the color
        label_weight_kg.setPadding(5, 5, 5, 5);
        label_weight_kg.setGravity(Gravity.CENTER);

        tr_head.addView(label_weight_kg); // add the column to the table row here

        TextView points = new TextView(getContext());
        points.setId(r.nextInt(1000+1));// define id that must be unique
        points.setText("Points"); // set the text for the header
        points.setTextColor(Color.WHITE); // set the color
        points.setPadding(5, 5, 5, 5);
        points.setGravity(Gravity.CENTER);

        tr_head.addView(points); // add the column to the table row here


        t1.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        volleySingleton = VolleySingleton.getinstance(getContext());
        requestQueue = volleySingleton.getrequestqueue();
        profile = getActivity().getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,Context.MODE_PRIVATE);
        getLeaderboard();

        class_ = (TextView)v.findViewById(R.id.class_leader);
        class_.setText("Class: "+profile.getString(Constants.CLASS,"def"));

        return v;
    }

    private void getLeaderboard() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LEADERBOARD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.e("resp",response);
                try {
                    JSONObject a = new JSONObject(response);
                    JSONArray res = a.getJSONArray("result");
                    for(int i =0;i<res.length();i++){
                        JSONObject current = res.getJSONObject(i);
                        int rank = i+1;
                        String name = current.getString("name").split(" ")[0];
                        double points = Double.parseDouble(current.getJSONObject("scores").getString("total"));
                        Log.e("+++++",rank+name+points);

                        TableRow tr = new TableRow(getContext());
                        if(i%2!=0) tr.setBackgroundColor(Color.GRAY);
                        tr.setId(100+i);
                        tr.setLayoutParams(new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

                        TextView labelDATE = new TextView(getContext());
                        labelDATE.setId(200+i);
                        labelDATE.setText(rank+"");
                        labelDATE.setPadding(2, 0, 5, 0);
                        labelDATE.setTextColor(Color.WHITE);
                        labelDATE.setGravity(Gravity.CENTER);
                        tr.addView(labelDATE);

                        TextView labelDATE2 = new TextView(getContext());
                        labelDATE2.setId(200+i);
                        labelDATE2.setText(name);
                        labelDATE2.setPadding(2, 0, 5, 0);
                        labelDATE2.setTextColor(Color.WHITE);
                        labelDATE2.setGravity(Gravity.CENTER);
                        tr.addView(labelDATE2);

                        TextView labelDATE3 = new TextView(getContext());
                        labelDATE3.setId(200+i);
                        labelDATE3.setText(String.format("%.02f",points)+"");
                        labelDATE3.setPadding(2, 0, 5, 0);
                        labelDATE3.setTextColor(Color.WHITE);
                        labelDATE3.setGravity(Gravity.CENTER);
                        tr.addView(labelDATE3);



                        t1.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("err",error.toString());
                Toast.makeText(getContext(),"Network Error",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
//                params.put(Constants.ID,profile.getString(Constants.ID,"default"));
                params.put(Constants.CLASS,profile.getString(Constants.CLASS,"default"));

                Log.e("params",params.toString());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}
