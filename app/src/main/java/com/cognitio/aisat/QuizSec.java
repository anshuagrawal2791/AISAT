package com.cognitio.aisat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Collections.swap;

public class QuizSec extends AppCompatActivity {

    String category = " ";
    int type,current=0;
    ArrayList<Question> all_questions;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;
    CircleImageView dp1;
    ImageView dp2;
    ImageLoader imageLoader;
    TextView question_tv,name,class_tv,section_tv,timer_tv;
    Button b1,b2,b3,b4,chg_sec,finish;
    Animation a1,a2,zo,zi;
    CountDownTimer timer;
    ArrayList<String> bookmarked;
    ImageButton next,prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        Log.e("category", intent.getStringExtra("category"));
        category = intent.getStringExtra("category");

        profile = getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,MODE_PRIVATE);
        dp1 =(CircleImageView)findViewById(R.id.dp);
        dp2=(ImageView)findViewById(R.id.dp2);
        question_tv = (TextView)findViewById(R.id.question_tv);
        name = (TextView)findViewById(R.id.name);
        class_tv = (TextView)findViewById(R.id.class_tv);
        section_tv = (TextView)findViewById(R.id.section_tv);
        timer_tv = (TextView)findViewById(R.id.timer_tv);
        b1=(Button)findViewById(R.id.b1);
        b2=(Button)findViewById(R.id.b2);
        b3=(Button)findViewById(R.id.b3);
        b4=(Button)findViewById(R.id.b4);
        prev= (ImageButton) findViewById(R.id.prev);
        chg_sec=(Button)findViewById(R.id.chg_sec);
        next= (ImageButton) findViewById(R.id.next);
        finish=(Button)findViewById(R.id.finish);
        b1.setTag("a");
        b2.setTag("b");
        b3.setTag("c");
        b4.setTag("d");

        HashMap<String,Integer> timer_times = new HashMap<>();
        timer_times.put("m",900000);
        timer_times.put("qa",900000);
        timer_times.put("vr",600000);
        timer_times.put("lr",600000);
        timer_times.put("ga",600000);
        timer_times.put("la",600000);
        timer_times.put("sa",600000);
        timer_times.put("ge",600000);

        timer= new CountDownTimer(timer_times.get(category), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);

                timer_tv.setText(String.format("%02d"+":"+"%02d",minutes,seconds));
            }

            @Override
            public void onFinish() {
                Intent i = new Intent(QuizSec.this,ResultSec.class);
                i.putParcelableArrayListExtra("all_questions",all_questions);
                i.putStringArrayListExtra("bookmarked",bookmarked);
                i.putExtra("category",category);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizSec.this,ResultSec.class);
                i.putParcelableArrayListExtra("all_questions",all_questions);
                i.putStringArrayListExtra("bookmarked",bookmarked);
                i.putExtra("category",category);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        String a = profile.getString(Constants.NAME,"def");
        String arr[] = a.split(" ",2);
        Log.e("name",arr[0]);
        name.setText(arr[0]);
        class_tv.setText("Class: "+profile.getString(Constants.CLASS,"default"));
        a1= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.card_flip_left_in);
        a2= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.card_flip_right_in);
        zo= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        zi= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current!=0)
                {   current--;
                    startquiz(0);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current!=14)
                {   current++;
                    startquiz(1);
                }
            }
        });

        String url = profile.getString(Constants.PROFILE_PIC,"default");
        volleySingleton = VolleySingleton.getinstance(this);
        requestQueue = volleySingleton.getrequestqueue();
        imageLoader = volleySingleton.getimageloader();
        if(!url.matches("default")&&!url.matches(" ")){

            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                holder.hotel_image.setImageDrawable(null);
                    dp1.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {


                }
            });
        }
        else{
            dp1.setImageResource(R.drawable.aisat);
        }

        all_questions = new ArrayList<>();
        bookmarked = new ArrayList<>();

        dp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarked.contains(all_questions.get(current).getId())){
                    dp2.setImageResource(0);
                    dp2.setBackgroundResource(R.drawable.ic_star);
                    bookmarked.remove(all_questions.get(current).getId());
                    dp2.startAnimation(zi);
                }
                else{
//                    dp2.setBackgroundResource(R.drawable.ic_star);
                    dp2.setImageResource(0);
                    dp2.setImageResource(R.drawable.ic_star_checked);
                    bookmarked.add(all_questions.get(current).getId());
                    dp2.startAnimation(zi);

                }
            }
        });

        chg_sec.setVisibility(View.GONE);

        getQuestions(profile.getString(Constants.ID,"default"),profile.getString(Constants.CLASS,"default"),category);


    }

    private void getQuestions(final String id, final String class_, final String category) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_QUES_SECTIONAL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

                Log.e("response",response);
                try {
                    JSONObject resp = new JSONObject(response);
                    if(resp.getBoolean("res")){
//                        resp = resp.getJSONObject("questions");
                        JSONArray questions = resp.getJSONArray("questions");
                        for(int i =0;i<questions.length();i++){
                            JSONObject current = questions.getJSONObject(i);
                            all_questions.add(new Question(current.getString("question"),current.getString("option_a"),current.getString("option_b"),current.getString("option_c"),current.getString("option_d"),current.getString("answer"),current.getString("id"),current.getString("category")));
                        }

                        int n = all_questions.size();
                        Random random = new Random();
                        random.nextInt();
                        for (int i = 0; i < n; i++) {
                            int change = i + random.nextInt(n - i);
                            swap(all_questions, i, change);
                        }

                        timer.start();
                        startquiz(2);

                    }
                    else{
                        Toast.makeText(QuizSec.this,"Game Over!",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(QuizSec.this,Home.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("error",error.toString());
                Intent i = new Intent(QuizSec.this,Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(QuizSec.this,"Network Error",Toast.LENGTH_LONG).show();
                startActivity(i);
//                if(error.equals(NoConnectionError)){
//
//                }
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.ID,id);
                params.put(Constants.CLASS,class_);
                params.put("category",category);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    private void startquiz(int anim) {
        for(int i=0;i<bookmarked.size();i++){
            Log.e("bookmarked",bookmarked.get(i));
        }

        if(bookmarked.contains(all_questions.get(current).getId())){
            dp2.setImageResource(0);

            dp2.setImageResource(R.drawable.ic_star_checked);
        }
        else{
            dp2.setImageResource(0);
            dp2.setImageResource(R.drawable.ic_star);
        }
        if(anim==0){
            question_tv.startAnimation(a1);
            b1.startAnimation(a1);
            b4.startAnimation(a1);
            b2.startAnimation(a1);
            b3.startAnimation(a1);

        }
        if(anim==1){
            question_tv.startAnimation(a2);
            b1.startAnimation(a2);
            b4.startAnimation(a2);
            b2.startAnimation(a2);
            b3.startAnimation(a2);
        }

        if(all_questions.get(current).getCategory().matches("ga")){
            section_tv.setText("General Awareness");
        }
        else if(all_questions.get(current).getCategory().matches("vr"))
            section_tv.setText("Verbal Reasoning");
        else if(all_questions.get(current).getCategory().matches("lr"))
            section_tv.setText("Logical Reasoning");
        else if(all_questions.get(current).getCategory().matches("qa"))
            section_tv.setText("Quantitative Analysis");
        else if(all_questions.get(current).getCategory().matches("sa"))
            section_tv.setText("Scientific Aptitude");
        else if(all_questions.get(current).getCategory().matches("ge"))
            section_tv.setText("General English");
        else if(all_questions.get(current).getCategory().matches("m"))
            section_tv.setText("Mathematics");
        else if(all_questions.get(current).getCategory().matches("la"))
            section_tv.setText("Legal Aptitude");

        Log.e("current",current+"");
        question_tv.setText(all_questions.get(current).getQuestion());
        b1.setText(all_questions.get(current).getOption_a());
        b2.setText(all_questions.get(current).getOption_b());
        b3.setText(all_questions.get(current).getOption_c());
        b4.setText(all_questions.get(current).getOption_d());

        b1.setBackgroundColor(Color.parseColor("#d6d7d7"));
        b2.setBackgroundColor(Color.parseColor("#d6d7d7"));
        b3.setBackgroundColor(Color.parseColor("#d6d7d7"));
        b4.setBackgroundColor(Color.parseColor("#d6d7d7"));

        String resp = all_questions.get(current).getResponse();
        if(resp.matches("a"))
            b1.setBackgroundColor(Color.BLUE);
        if(resp.matches("b"))
            b2.setBackgroundColor(Color.BLUE);
        if(resp.matches("c"))
            b3.setBackgroundColor(Color.BLUE);
        if(resp.matches("d"))
            b4.setBackgroundColor(Color.BLUE);

        View.OnClickListener a = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                b1.setBackgroundColor(Color.parseColor("#d6d7d7"));
                b2.setBackgroundColor(Color.parseColor("#d6d7d7"));
                b3.setBackgroundColor(Color.parseColor("#d6d7d7"));
                b4.setBackgroundColor(Color.parseColor("#d6d7d7"));
                Question currentq = all_questions.get(current);
//                if(currentq.getResponse().matches(" "))
                if(v.getTag().toString().matches(currentq.getResponse())){
                    v.setBackgroundColor(Color.parseColor("#d6d7d7"));
                    Log.e("b",currentq.getResponse().toString());
                    currentq.setResponse(" ");
                    all_questions.set(current,currentq);
                    Log.e("a",currentq.getResponse());

                }
                else{
                    v.setBackgroundColor(Color.BLUE);
                    Log.e("b",currentq.getResponse().toString());
                    currentq.setResponse(v.getTag().toString());
                    all_questions.set(current,currentq);
                    Log.e("a",currentq.getResponse());}


            }
        };
        b1.setOnClickListener(a);
        b2.setOnClickListener(a);
        b3.setOnClickListener(a);
        b4.setOnClickListener(a);


    }
}
