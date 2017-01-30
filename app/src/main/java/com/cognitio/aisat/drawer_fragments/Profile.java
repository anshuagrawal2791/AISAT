package com.cognitio.aisat.drawer_fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.Constants;
import com.cognitio.aisat.R;
import com.cognitio.aisat.VolleySingleton;
import com.github.mikephil.charting.charts.CombinedChart;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    CircleImageView dp;
    TextView name,school;
    SharedPreferences profile;
    SharedPreferences.Editor editor;
    Spinner class2;
    Button change;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    ImageLoader imageLoader;
    int change_state=0;
    CombinedChart mChart;
    CombinedData data ;
    XAxis xAxis;
    YAxis leftAxis,rightAxis;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);

        name = (TextView)v.findViewById(R.id.name_tv);
        school = (TextView)v.findViewById(R.id.school_tv);
        class2 = (Spinner)v.findViewById(R.id.class3);
        change = (Button)v.findViewById(R.id.change);
        dp = (CircleImageView)v.findViewById(R.id.dp_profile);
        profile = getActivity().getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE, Context.MODE_PRIVATE);
        editor = profile.edit();

        name.setText(profile.getString(Constants.NAME,"User"));
        school.setText(profile.getString(Constants.SCHOOL,"School"));
        Log.e("------",profile.getString(Constants.PROFILE_PIC,"def"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.classes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        class2.setAdapter(adapter);

        volleySingleton = VolleySingleton.getinstance(getContext());
        imageLoader = volleySingleton.getimageloader();
        requestQueue = volleySingleton.getrequestqueue();

        String url = profile.getString(Constants.PROFILE_PIC,"default");
        if(!url.matches("default")&&!url.matches(" ")){
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                holder.hotel_image.setImageDrawable(null);
                    dp.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {


                }
            });
        }
        else{
            dp.setImageResource(R.drawable.aisat);
        }

        class2.setSelection(Integer.parseInt(profile.getString(Constants.CLASS,"default"))-6);
        class2.setEnabled(false);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("change_state",change_state+"");
                Log.e("class___",profile.getString(Constants.CLASS,"def"));

                if(change_state==0){
                    change.setText("Save");
                    class2.setEnabled(true);
                    change_state=1;
                }
                else{
                    editor.putString(Constants.CLASS,""+(class2.getSelectedItemPosition()+6));
                    editor.commit();
                    change_state=0;
                    class2.setEnabled(false);
                    change.setText("Change");

                }
            }
        });

        mChart = (CombinedChart)v.findViewById(R.id.chart2);
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
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

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
                Log.e("value",value+"");
                return sections[(int) (value)];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        xAxis.setValueFormatter(formatter);
        data = new CombinedData();

        getScores(profile.getString(Constants.ID,"def"),profile.getString(Constants.CLASS,"def"));






        return v;
    }

    private void getScores(final String id, final String class_) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_SCORES_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("resp",response);
                dialog.dismiss();
                try {
                    JSONObject re = new JSONObject(response);

                    JSONArray res = re.getJSONArray("avg_scores");
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


                    JSONObject scores = re.getJSONObject("user_score");
                    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
                    entries1.add(new BarEntry(0f,Float.parseFloat(scores.getString("ga"))));
                    entries1.add(new BarEntry(1f,Float.parseFloat(scores.getString("vr"))));
                    entries1.add(new BarEntry(2f,Float.parseFloat(scores.getString("lr"))));
                    entries1.add(new BarEntry(3f,Float.parseFloat(scores.getString("qa"))));
                    entries1.add(new BarEntry(4f,Float.parseFloat(scores.getString("la"))));
                    entries1.add(new BarEntry(5f,Float.parseFloat(scores.getString("sa"))));
                    entries1.add(new BarEntry(6f,Float.parseFloat(scores.getString("ge"))));
                    entries1.add(new BarEntry(7f,Float.parseFloat(scores.getString("m"))));

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
                params.put(Constants.ID,id);
                params.put(Constants.CLASS,class_);

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
