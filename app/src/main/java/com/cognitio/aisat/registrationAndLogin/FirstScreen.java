package com.cognitio.aisat.registrationAndLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirstScreen extends AppCompatActivity {
    EditText email,password;
    Button login;
    TextView signup;
    LoginButton loginButton;
    SharedPreferences profile,launch_time;
    SharedPreferences.Editor editor,editor2;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    CallbackManager callbackManager;
    boolean error = false;
    GoogleApiClient mGoogleApiClient;
    final int  RC_SIGN_IN = 1;
    GoogleSignInOptions gso;
    SignInButton signInButton;
    Button dummy_fb,dummy_google;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        signup = (TextView)findViewById(R.id.signup);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        dummy_fb = (Button)findViewById(R.id.dummy_fb);
        dummy_google = (Button)findViewById(R.id.dummy_g);


        dummy_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        dummy_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        loginButton.setReadPermissions("email","public_profile");

        profile = getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,MODE_PRIVATE);
        editor = profile.edit();
        launch_time = getSharedPreferences(Constants.LAUNCH_TIME_PREFERENCE_FILE,MODE_PRIVATE);
        editor2 = launch_time.edit();

        volleySingleton = VolleySingleton.getinstance(this);
        requestQueue = volleySingleton.getrequestqueue();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error = false;
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
                if(password.getText().toString().equals(""))
                {
                    password.setError("Required");
                    error = true;
                }

                if(error == false) {
                    loginUser(email.getText().toString(),password.getText().toString(),"0");
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreen.this,UserDetails.class);
                startActivity(intent);
            }
        });


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("googlesignin","connection failed");
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code

                AccessToken accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    final JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                final JSONObject jsonObject = response.getJSONObject();
                                String nombre = "";
                                String email = "";
                                String id = "";
                                String profilePicUrl="";
                                try {
                                    nombre = jsonObject.getString("name");
                                    email =  jsonObject.getString("email");
                                    if (jsonObject.has("picture")) {
                                        profilePicUrl = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                                        // set profile image to imageview using Picasso or Native methods
                                    }
                                    Log.e("name",nombre);
                                    Log.e("email",email);
//                                    Log.e("id",loginResult.getAccessToken().get);

                                    loginOrSignup(email,nombre,profilePicUrl);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("graph_error",e.toString());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();



            }

            @Override
            public void onCancel() {
                // App code
                Log.e("fb_login","cancel");

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("fb_login",exception.toString());

            }
        });






    }

    private void loginOrSignup(final String email, final String nombre, final String profilePicUrl) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.CHECK_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respons) {
                        dialog.dismiss();
                        Log.e("response",respons.toString());
                        JSONObject response = null;
                        try {
                            response = new JSONObject(respons);
                        } catch (JSONException e) {
                            Log.e("error",e.toString());
                        }
                        try {
                            if(response.getBoolean("res"))
                            {

                                Intent intent = new Intent(FirstScreen.this,UserDetails.class);
                                intent.putExtra("email",email);
                                intent.putExtra("name",nombre);
                                intent.putExtra(Constants.PROFILE_PIC,profilePicUrl);
                                startActivity(intent);



                                //Toast.makeText(getApplicationContext(),"Successfully Registered",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                loginUser(email,"crap","1");
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                Log.e("vollley error",error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.EMAIL,email);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }



    private void loginUser(final String email_string, final String password_string,final String fb) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");
        dialog.show();
        Log.e("aa",email_string);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject resp = new JSONObject(response);
                    Log.e("login",resp.toString());
                    if(resp.getBoolean("res")){
                        editor.putString(Constants.EMAIL,resp.getString(Constants.EMAIL));
                        editor.putString(Constants.ID,resp.getString(Constants.ID));
                        editor.putString(Constants.NAME,resp.getString(Constants.NAME));
                        editor.putString(Constants.PHONE,resp.getString(Constants.PHONE));
                        editor.putString(Constants.CLASS,resp.getString(Constants.CLASS));
                        editor.putString(Constants.GENDER,resp.getString(Constants.GENDER));
                        editor.putString(Constants.CITY,resp.getString(Constants.CITY));
                        editor.putString(Constants.SCHOOL,resp.getString(Constants.SCHOOL));
                        editor.putString(Constants.PIN,resp.getString(Constants.PIN));
                        editor.putString(Constants.ADDRESS_LINE1,resp.getString(Constants.ADDRESS_LINE1));
                        editor.putString(Constants.ADDRESS_LINE2,resp.getString(Constants.ADDRESS_LINE2));
                        editor.putString(Constants.ADDRESS,resp.getString(Constants.ADDRESS));
                        editor.putString(Constants.LONGITUDE,resp.getJSONArray("location").getString(0));
                        editor.putString(Constants.LATITUDE,resp.getJSONArray("location").getString(1));

                        JSONArray images = resp.getJSONObject("imagesS3").getJSONArray("name");

                        if(images.length()>0)
                        editor.putString(Constants.PROFILE_PIC,images.get(images.length()-1).toString());
                        else editor.putString(Constants.PROFILE_PIC," ");
//                        Log.e("image",resp.getJSONObject(Constants.IMAGESS3).getJSONArray(Constants.NAME).get(0).toString());
                        editor.commit();
                        editor2.putBoolean(Constants.FIRST_TIME,true);
                        editor2.commit();
                        Toast.makeText(getApplicationContext(),resp.getString("response"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);



                    }
                    else{
                        Toast.makeText(FirstScreen.this,resp.getString("response"),Toast.LENGTH_LONG).show();
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

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.EMAIL,email_string);
                params.put(Constants.PASSWORD,password_string);
                params.put("fb",fb);
                Log.e("aa",params.toString());
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
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
//        Log.e("fb_result",data.getDataString());

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("googlesignin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("googlesignin----",acct.getDisplayName());
            Log.e("googlesignin----",acct.getEmail());
            FirebaseCrash.report(new Exception(acct.getDisplayName()));
//            FirebaseCrash.report(new Exception(acct.getPhotoUrl().toString()));
            if(acct.getPhotoUrl()!=null){
            Log.e("googlesignin----",acct.getPhotoUrl().toString());
            loginOrSignup(acct.getEmail(),acct.getDisplayName(),acct.getPhotoUrl().toString());}
            else
                loginOrSignup(acct.getEmail(),acct.getDisplayName()," ");
//            updateUI(true);
        }
        else{
            Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show();
        }
    }


}
