package com.cognitio.aisat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.drawer_fragments.Bookmarked;
import com.cognitio.aisat.models.Question;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anshu on 02/11/16.
 */

public class BookmarkedAdapter extends RecyclerView.Adapter<BookmarkedAdapter.RecyclerViewHolder>  {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Question> questions = new ArrayList<>();
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;

    public BookmarkedAdapter(Context context) {
        this.context=context;
        layoutInflater = layoutInflater.from(context);
        volleySingleton = VolleySingleton.getinstance(context);
        requestQueue = volleySingleton.getrequestqueue();
        profile = context.getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,Context.MODE_PRIVATE);
    }
    public void addAll(ArrayList<Question> q){
        questions.clear();
        questions.addAll(q);
        notifyDataSetChanged();
    }

    @Override
    public BookmarkedAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarked_row,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final BookmarkedAdapter.RecyclerViewHolder holder, final int position) {
        final Question current = questions.get(position);
        HashMap<String,String> a = new HashMap<>();
        a.put("ga","General Awareness");
        a.put("vr","Verbal Reasoning");
        a.put("lr","Logical Reasoning");
        a.put("qa","Quantitative Analysis");
        a.put("la","Legal Aptitude");
        a.put("sa","Scientific Aptitude");
        a.put("ge","General English");
        a.put("m","Mathematics");
        holder.category.setText(a.get(current.getCategory()));
        holder.question.setText(current.getQuestion());
        holder.answer.setText(current.getAnswer());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.remove(position);
//                notifyItemRemoved(position);
                notifyDataSetChanged();
                Log.e("to_remove",current.getCategory());
                removeBookmarked(current.getId());
            }
        });

    }

    private void removeBookmarked(final String id) {
//        final ProgressDialog dialog = new ProgressDialog(context);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);
//        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REMOVE_BOOKMARKED_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                dialog.dismiss();
                Log.e("resp",response);
                Toast.makeText(context,"Successfully Removed",Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                Log.e("err",error.toString());
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.ID,profile.getString(Constants.ID,"default"));
                params.put("question",id);






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

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView category,question,answer;
        Button remove;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            category = (TextView)itemView.findViewById(R.id.category);
            question = (TextView)itemView.findViewById(R.id.question);
            answer = (TextView)itemView.findViewById(R.id.answer);
            remove = (Button)itemView.findViewById(R.id.remove);



        }
    }
}
