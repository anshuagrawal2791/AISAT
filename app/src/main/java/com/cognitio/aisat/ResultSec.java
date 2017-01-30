package com.cognitio.aisat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.models.Question;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultSec extends AppCompatActivity {
    ArrayList<Question> all_questions;
    ArrayList<String> bookmarked;
    int attempted,correct;
    double score,accuracy;
    String category;
    Button play_again, home;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;
    TextView tv1,tv2,tv3,tv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_sec);

        Intent intent = getIntent();
        all_questions = intent.getParcelableArrayListExtra("all_questions");
        bookmarked=intent.getStringArrayListExtra("bookmarked");
        category = intent.getStringExtra("category");

        play_again = (Button)findViewById(R.id.play_again);
        home = (Button)findViewById(R.id.home);
        volleySingleton = VolleySingleton.getinstance(this);
        requestQueue = volleySingleton.getrequestqueue();
        profile = getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,MODE_PRIVATE);

        for(int i=0;i<all_questions.size();i++){
            Question current = all_questions.get(i);
            if(!current.getResponse().matches(" ")){
                attempted+=1;
                if(current.getResponse().matches(current.getAnswer().toLowerCase()))
                {   score+=5;
                    correct+=1;}
                else
                    score-=1.6;
            }
        }
        accuracy = (double)((double)correct/(double)attempted)*100;
        Log.e("data+++",attempted+"ddf"+correct+"ad"+score+"ad"+accuracy);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        tv4=(TextView)findViewById(R.id.tv4);
        tv1.setText("Total Score: "+ String.format("%.02f",score));
        tv2.setText("Questions Attempted: "+ attempted);
        tv3.setText("Correct Answers: "+ correct);
        tv4.setText("Accuracy: "+ String.format("%.02f",accuracy)+"%");



        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultSec.this, QuizSec.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("category",category);
                startActivity(i);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultSec.this,Home.class);
//                i.putExtra("type",1);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        sendBookmarked();


//        for(int i=0;i<all_questions.size();i++){
//            if()
//        }
    }

    private void sendBookmarked() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_BOOKMARKED_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.e("resp",response);

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

                JSONArray d = new JSONArray(bookmarked);

                params.put("bookmarked",d.toString());

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
