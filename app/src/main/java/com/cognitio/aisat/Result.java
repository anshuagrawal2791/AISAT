package com.cognitio.aisat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.models.Question;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result extends AppCompatActivity {
    ArrayList<Question> all_questions;
    ArrayList<String> answered_correctly;
    ArrayList<String> answered_wrongly;
    ArrayList<String> questions_played;
    ArrayList<String> bookmarked;
    Map<String,Double> scores;

    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;
    CombinedChart mChart;
    CombinedData data ;
    XAxis xAxis;
    YAxis leftAxis,rightAxis;
    Button play_again,home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        all_questions = intent.getParcelableArrayListExtra("all_questions");
        bookmarked=intent.getStringArrayListExtra("bookmarked");
        volleySingleton = VolleySingleton.getinstance(this);
        requestQueue = volleySingleton.getrequestqueue();
        profile = getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,MODE_PRIVATE);
        answered_correctly = new ArrayList<>();
        answered_wrongly = new ArrayList<>();
        questions_played = new ArrayList<>();

        play_again = (Button)findViewById(R.id.play_again);
        home = (Button)findViewById(R.id.home);

        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Result.this,Quiz.class);
                i.putExtra("type",1);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Result.this,Home.class);
//                i.putExtra("type",1);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        scores = new HashMap<>();
        scores.put("ga",0.0);
        scores.put("vr",0.0);
        scores.put("lr",0.0);
        scores.put("qa",0.0);
        scores.put("la",0.0);
        scores.put("sa",0.0);
        scores.put("ge",0.0);
        scores.put("m",0.0);
        for(int i=0;i<all_questions.size();i++){
            Question current = all_questions.get(i);
            questions_played.add(current.getId());
            if(!current.getResponse().matches(" ")){
                if(current.getAnswer().toLowerCase().matches(current.getResponse())){
                    answered_correctly.add(current.getId());
                    double a = scores.get(current.getCategory());
                    scores.put(current.getCategory(),a+5);
                }
                else{
                    answered_wrongly.add(current.getId());
                    double a = scores.get(current.getCategory());
                    scores.put(current.getCategory(),a-1.6);
                }
            }

        }
        for (String name: scores.keySet()){

            String key =name.toString();
            String value = scores.get(name).toString();
            Log.e("scores",key + " " + value);

        }
        for(int i =0;i<answered_wrongly.size();i++)
            Log.e("wrongly",answered_wrongly.get(i));
        for(int i =0;i<answered_correctly.size();i++)
            Log.e("rightly",answered_correctly.get(i));

        mChart = (CombinedChart) findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.parseColor("#1f656a"));
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
//        rightAxis.setAxisMinimum(-); // this replaces setStartAtZero(true)

        leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        xAxis = mChart.getXAxis();
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        final String[] sections = new String[] { "G", "V", "L", "Q","LE","S","E","M","9","10"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                Log.e("value",value+"");
                return sections[(int) (value)];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        xAxis.setValueFormatter(formatter);
        data = new CombinedData();

        sendScores();




//        Log.e("bookmarked",""+all_questions.size()+"--"+bookmarked.size());

    }

    private void sendScores() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RESULT_COMPLETE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("resp",response);
                dialog.dismiss();
                try {
                    JSONArray res = new JSONArray(response);
                    JSONObject resp = res.getJSONObject(0);
                    Log.e("resp2",resp.toString());
//                    List<BarEntry> entries = new ArrayList<>();
//                    entries.add(new BarEntry(0,resp.getLong("ga"),"General Awareness"));
                    LineData d = new LineData();

                    ArrayList<Entry> entries = new ArrayList<Entry>();

//                    for (int index = 0; index < itemcount; index++)
                    Log.e("ga",Float.parseFloat(resp.getString("ga"))+"");
                    entries.add(new Entry(0f, Float.parseFloat(resp.getString("ga"))));
                    entries.add(new Entry(1f, Float.parseFloat(resp.getString("vr"))));
                    entries.add(new Entry(2f, Float.parseFloat(resp.getString("lr"))));
                    entries.add(new Entry(3f, Float.parseFloat(resp.getString("qa"))));
                    entries.add(new Entry(4f, Float.parseFloat(resp.getString("la"))));
                    entries.add(new Entry(5f, Float.parseFloat(resp.getString("sa"))));
                    entries.add(new Entry(6f, Float.parseFloat(resp.getString("ge"))));
                    entries.add(new Entry(7f, Float.parseFloat(resp.getString("m"))));
                    LineDataSet set = new LineDataSet(entries, "Average Score");
                    set.setColor(Color.rgb(240, 238, 70));
                    set.setLineWidth(2.5f);
                    set.setCircleColor(Color.rgb(240, 238, 70));
                    set.setCircleRadius(5f);
                    set.setFillColor(Color.rgb(240, 238, 70));
                    set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//                    set.setDrawValues(true);
                    set.setValueTextSize(10f);
                    set.setValueTextColor(Color.rgb(240, 238, 70));

                    set.setAxisDependency(YAxis.AxisDependency.LEFT);
                    d.addDataSet(set);

                    ArrayList<BarEntry> entries1 = new ArrayList<>();
                    entries1.add(new BarEntry(0f,Float.parseFloat(scores.get("ga").toString())));
                    entries1.add(new BarEntry(1f,Float.parseFloat(scores.get("vr").toString())));
                    entries1.add(new BarEntry(2f,Float.parseFloat(scores.get("lr").toString())));
                    entries1.add(new BarEntry(3f,Float.parseFloat(scores.get("qa").toString())));
                    entries1.add(new BarEntry(4f,Float.parseFloat(scores.get("la").toString())));
                    entries1.add(new BarEntry(5f,Float.parseFloat(scores.get("sa").toString())));
                    entries1.add(new BarEntry(6f,Float.parseFloat(scores.get("ge").toString())));
                    entries1.add(new BarEntry(7f,Float.parseFloat(scores.get("m").toString())));

                    BarDataSet set1 = new BarDataSet(entries1, "Your Score");
                    set1.setColor(Color.rgb(60, 220, 78));
                    set1.setValueTextColor(Color.rgb(60, 220, 78));
                    set1.setValueTextSize(10f);
                    set1.setAxisDependency(YAxis.AxisDependency.LEFT);

                    BarData d2 = new BarData(set1);

                    data.setData(d);
                    data.setData(d2);
                    xAxis.setAxisMaximum(data.getXMax());
                    leftAxis.setAxisMaximum(data.getYMax()+0.25f);
                    rightAxis.setAxisMaximum(data.getYMax()+0.25f);
                    leftAxis.setAxisMinimum(data.getYMin()-0.25f);
                    rightAxis.setAxisMinimum(data.getYMin()-0.25f);

                    mChart.setData(data);
                    mChart.invalidate();
//                    data.setValueTypeface(mTfLight);



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
                params.put(Constants.CLASS,profile.getString(Constants.CLASS,"default"));
                JSONArray a = new JSONArray(questions_played);
                JSONArray b = new JSONArray(answered_correctly);
                JSONArray c = new JSONArray(answered_wrongly);
                JSONArray d = new JSONArray(bookmarked);
                params.put("questions_played",a.toString());
                params.put("answered_correctly",b.toString());
                params.put("answered_wrongly",c.toString());
                params.put("bookmarked",d.toString());
                for (String name: scores.keySet()){

                    String key =name.toString();
                    String value = scores.get(name).toString();
                    Log.e("scores",key + " " + value);
                    params.put(key,value);

                }
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
