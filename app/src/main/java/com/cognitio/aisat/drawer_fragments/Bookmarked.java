package com.cognitio.aisat.drawer_fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.BookmarkedAdapter;
import com.cognitio.aisat.Constants;
import com.cognitio.aisat.R;
import com.cognitio.aisat.VolleySingleton;
import com.cognitio.aisat.models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Bookmarked extends Fragment {

    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;
    ArrayList<Question> bookmarked;
    RecyclerView recycler;
    BookmarkedAdapter myadapter;

    public Bookmarked() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bookmarked, container, false);

        volleySingleton = VolleySingleton.getinstance(getContext());
        requestQueue = volleySingleton.getrequestqueue();
        profile = getActivity().getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE, Context.MODE_PRIVATE);

        bookmarked = new ArrayList<>();

        recycler = (RecyclerView)v.findViewById(R.id.recycler);
        myadapter = new BookmarkedAdapter(getContext());
//        recycler = (RecyclerView)v.findViewById(R.id.recyclerp);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recycler.setAdapter(myadapter);
        getBookmarked();


        return  v;
    }

    private void getBookmarked() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_QUES_BOOKMARKED_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.e("resp",response);
                try {
                    JSONArray questions = new JSONArray(response);
                    for(int i =0;i<questions.length();i++){
                        JSONObject current = questions.getJSONObject(i);
                        bookmarked.add(new Question(current.getString("question"),current.getString("option_a"),current.getString("option_b"),current.getString("option_c"),current.getString("option_d"),current.getString("answer"),current.getString("id"),current.getString("category")));
                    }
                    myadapter.addAll(bookmarked);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("err",error.toString());

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.ID,profile.getString(Constants.ID,"default"));




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
