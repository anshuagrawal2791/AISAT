package com.cognitio.aisat.registrationAndLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserDetails extends AppCompatActivity {
    EditText name,email,password,password2,phone;
    Spinner class2,gender;
    Button next;
    boolean error;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    SharedPreferences profile;
    SharedPreferences.Editor editor;

    String emailFromFb="";
    String nameFromFb = "";
    String profilePic=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            emailFromFb = intent.getStringExtra(Constants.EMAIL);
            nameFromFb = intent.getStringExtra(Constants.NAME);
            profilePic = intent.getStringExtra(Constants.PROFILE_PIC);

            Log.e("fb_data",emailFromFb+nameFromFb+profilePic);
        }

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);
        password= (EditText)findViewById(R.id.password);
        password2 = (EditText)findViewById(R.id.password2);

        class2 = (Spinner)findViewById(R.id.class2);
        gender = (Spinner)findViewById(R.id.gender);

        next = (Button)findViewById(R.id.next);

        if(!emailFromFb.matches(""))
            email.setText(emailFromFb);
        if(!nameFromFb.matches(""))
            name.setText(nameFromFb);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.classes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        class2.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter2);

        volleySingleton = VolleySingleton.getinstance(this);
        requestQueue = volleySingleton.getrequestqueue();

        profile = getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,MODE_PRIVATE);
        editor=profile.edit();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error=false;
                if(name.getText().toString().equals("")) {
                    name.setError("Enter Name");
                    error=true;
                }

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
                {
                    email.setError("Invalid Email");
                    //email.setText("");
                    error = true;
                }
                if(email.getText().toString().equals(""))
                {
                    email.setError("Required");
                    error = true;
                }
                if(!password2.getText().toString().equals(password.getText().toString()))
                {
                    password2.setError("Passwords don't match");
                    password2.setText("");
                    error = true;
                }
                if(password.getText().length()<5)
                {
                    password.setError("Minimum 5 characters");
                    password.setText("");
                    password2.setText("");
                }
                if(phone.getText().length()<10)
                {
                    phone.setError("Invalid Mobile Number");
                    error=true;
                }
                if(error==false){
                    check_User();
                }


            }
        });




    }

    private void check_User() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.CHECK_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respons) {
                        dialog.dismiss();
                        JSONObject response = null;
                        try {
                            response = new JSONObject(respons);
                            if(response.getBoolean("res"))
                            {
//                                editor.putString(Constants.ID,response.getString(Constants.ID));

                                editor.putString(Constants.NAME,name.getText().toString());
                                editor.putString(Constants.EMAIL,email.getText().toString());
                                editor.putString(Constants.PASSWORD,password.getText().toString());
                                editor.putString(Constants.PHONE,phone.getText().toString());
                                editor.putString(Constants.CLASS,""+(class2.getSelectedItemPosition()+6));
                                editor.putString(Constants.GENDER,gender.getSelectedItem().toString());

//                                JSONArray images = response.getJSONObject("imagesS3").getJSONArray("name");

                                editor.putString(Constants.PROFILE_PIC,profilePic);
                                editor.commit();
//                                Toast.makeText(getApplicationContext(),response.getString("response"),Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), UserDetails2.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),response.getString("response"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Internet error",Toast.LENGTH_LONG).show();
                Log.e("vollley error",error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
//                params.put(Constants.NAME,name.getText().toString());
                params.put(Constants.EMAIL,email.getText().toString());
//                params.put(Constants.PASSWORD, password.getText().toString());
//                params.put(Constants.PHONE, phone.getText().toString());
//                params.put(Constants.CLASS,""+(class2.getSelectedItemPosition()+6));
//                params.put(Constants.GENDER,gender.getSelectedItem().toString());
//                if(!profilePic.matches(""))
//                    params.put(Constants.PROFILE_PIC,profilePic);
                Log.e("params",params.toString());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
