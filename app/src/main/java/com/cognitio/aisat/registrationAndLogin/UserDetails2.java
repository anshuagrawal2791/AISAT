package com.cognitio.aisat.registrationAndLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cognitio.aisat.Constants;
import com.cognitio.aisat.Home;
import com.cognitio.aisat.R;
import com.cognitio.aisat.VolleySingleton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserDetails2 extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    EditText school,address;
    Button signup;
    String Address = "",Lat="",Lng="",city="",pin="",address_line1="",address_line2="";
    SharedPreferences profile,launch_time;
    SharedPreferences.Editor editor, editor2;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    boolean error = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details2);

        school = (EditText)findViewById(R.id.school);
        address = (EditText)findViewById(R.id.address);
        signup = (Button)findViewById(R.id.signup);

        profile = getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,MODE_PRIVATE);
        editor = profile.edit();
        launch_time = getSharedPreferences(Constants.LAUNCH_TIME_PREFERENCE_FILE,MODE_PRIVATE);
        editor2=launch_time.edit();

        volleySingleton =VolleySingleton.getinstance(this);
        requestQueue = volleySingleton.getrequestqueue();


        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(UserDetails2.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        address.setFocusable(false);

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(UserDetails2.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error = false;
                if(school.getText().toString().matches("")) {
                    error = true;
                    school.setError("Required");
                }
                if(address.getText().toString().matches("")){
                    error = true;
                    school.setError("Required");
                }
                if(error == false){
                    registerUser();
                }
            }
        });


    }

    private void registerUser() {
        final String name = profile.getString(Constants.NAME,"default");
        final String email = profile.getString(Constants.EMAIL,"default");
        final String phone = profile.getString(Constants.PHONE,"default");
        final String password = profile.getString(Constants.PASSWORD,"default");
        final String gender = profile.getString(Constants.GENDER,"default");
        final String class2 = profile.getString(Constants.CLASS,"default");
        final String profilePic = profile.getString(Constants.PROFILE_PIC," ");
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Signing up...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject resp = new JSONObject(response);
                    Log.e("resp",response);
                    if(resp.getBoolean("res")){
                        editor.putString(Constants.ID,resp.getString(Constants.ID));
                        editor.putString(Constants.ADDRESS,Address);
                        editor.putString(Constants.ADDRESS_LINE1,address_line1);
                        editor.putString(Constants.ADDRESS_LINE2,address_line2);
                        editor.putString(Constants.PIN,pin);
                        editor.putString(Constants.CITY,city);
                        editor.putString(Constants.LATITUDE,Lat);
                        editor.putString(Constants.LONGITUDE,Lng);
                        editor.putString(Constants.SCHOOL,school.getText().toString());
                        editor.commit();
                        editor2.putBoolean(Constants.FIRST_TIME,true);
                        editor2.commit();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(),resp.getString("response"),Toast.LENGTH_LONG).show();

                    }
                    else{
                        Toast.makeText(UserDetails2.this,resp.getString("response"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(UserDetails2.this,"error",Toast.LENGTH_LONG).show();
                Log.e("error",error.toString());

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.ADDRESS,Address);
                params.put(Constants.ADDRESS_LINE1,address_line1);
                params.put(Constants.ADDRESS_LINE2,address_line2);
                params.put(Constants.CITY,city);
                params.put(Constants.PIN,pin);
                params.put(Constants.LATITUDE,Lat);
                params.put(Constants.LONGITUDE,Lng);
                params.put(Constants.SCHOOL,school.getText().toString());
                params.put(Constants.NAME,name);
                params.put(Constants.EMAIL,email);
                params.put(Constants.PASSWORD, password);
                params.put(Constants.PHONE, phone);
                params.put(Constants.CLASS,class2);
                params.put(Constants.GENDER,gender);
                if(!profilePic.matches(" "))
                    params.put(Constants.PROFILE_PIC,profilePic);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PLACE_PICKER_REQUEST){
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName());
                Log.e("place",toastMsg);
                Log.e("place",place.getAddress().toString());
                Address = place.getAddress().toString();
                Lat = place.getLatLng().latitude+"";
                Lng = place.getLatLng().longitude+"";
                String[] a = Address.split(",");
                Log.e("a---",a.length+" ");
                FirebaseCrash.report(new Exception(Address));
                FirebaseCrash.report(new Exception(a.length+""));
                if(a.length>3){
                    for(int i=0;i<=a.length-4;i++){
                        if(i!=a.length-4)
                            address_line1=address_line1.concat(a[i].concat(","));
                        else
                            address_line1=address_line1.concat(a[i]);
                    }

                    for(int i=a.length-3;i<a.length;i++){
                        if(i<a.length-1)
                            address_line2=address_line2.concat(a[i].concat(","));
                        else
                            address_line2=address_line2.concat(a[i]);
                    }
                    city =a[a.length-3];
                    String[] x = a[a.length-2].split(" ");
                    pin = x[x.length-1];
                }


//                Log.e("a",a[a.length-1].toString());
                FirebaseCrash.report(new Exception(city+"---"+pin+"----"+Lat+"----"+Lng+"----"+address_line1+"----"+address_line2));
                Log.e("city pin",city+"---"+pin+"----"+Lat+"----"+Lng+"----"+address_line1+"----"+address_line2);
                address.setText(Address);
            }
        }
    }
}
