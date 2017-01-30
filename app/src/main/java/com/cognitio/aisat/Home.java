package com.cognitio.aisat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cognitio.aisat.drawer_fragments.Bookmarked;
import com.cognitio.aisat.drawer_fragments.Dashboard;
import com.cognitio.aisat.Fragments.Sections;
import com.cognitio.aisat.drawer_fragments.Leaderboard;
import com.cognitio.aisat.drawer_fragments.Profile;
import com.cognitio.aisat.registrationAndLogin.FirstScreen;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Dashboard.OnTestSelectedListener{

    SharedPreferences profile,prefs;
    SharedPreferences.Editor editor,editor2;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Fragment select_test;
    VolleySingleton volleySingleton;
    ImageLoader imageLoader;
    TextView name,email;
    CircleImageView dp;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        volleySingleton = VolleySingleton.getinstance(this);
        imageLoader = volleySingleton.getimageloader();
        prefs = getApplicationContext().getSharedPreferences(
                Constants.LAUNCH_TIME_PREFERENCE_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit();
        profile = getApplicationContext().getSharedPreferences(Constants.PROFILE_PREFERENCE_FILE,Context.MODE_PRIVATE);
        editor2 = profile.edit();
        if (!prefs.getBoolean(Constants.FIRST_TIME,false)) {
            // <---- run your one time code here
            Intent intent = new Intent(this,FirstScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);


        }



//        Button logout = (Button)findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editor.putBoolean(Constants.FIRST_TIME,false);
//                editor.commit();
//                editor2.clear();
//                editor2.commit();
//                LoginManager.getInstance().logOut();
//                Intent intent = new Intent(Home.this,FirstScreen.class );
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });

        Map<String,?> keys = profile.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.e("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }


//        select_test = (Fragment)findViewById(R.id.select_test);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container,new Dashboard()).commit();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);

        navigationView.setNavigationItemSelectedListener(this);
        name = (TextView)headerView.findViewById(R.id.header_name);
        //prof_pic = (ImageView)headerView.findViewById(R.id.prof_pic);
//        companyName.setText("new_text");
        email = (TextView)headerView.findViewById(R.id.header_email);
        dp = (CircleImageView) headerView.findViewById(R.id.header_dp);

        name.setText(profile.getString(Constants.NAME,"Cognitio"));
        email.setText(profile.getString(Constants.EMAIL,"Cognitio"));
        if(!profile.getString(Constants.PROFILE_PIC,"default").matches("default")&&!profile.getString(Constants.PROFILE_PIC,"default").matches(" ")){
            String url = profile.getString(Constants.PROFILE_PIC,"default");
            Log.e("url",url);
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


//            dp.setImageBitmap(BitmapFactory.decodeFile(profile.getString(Constants.PROFILE_IMAGE_URI,"default")));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragment_container,new Dashboard()).commit();
            title="AISAT";

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragment_container,new Profile()).commit();
            title = "Profile";

        } else if (id == R.id.nav_slideshow) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragment_container,new Leaderboard()).commit();
            title = "Leaderboard";


        } else if (id == R.id.nav_slideshow2) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragment_container,new Bookmarked()).commit();
            title = "Bookmarked";


        } else if (id == R.id.nav_manage) {
            editor.putBoolean(Constants.FIRST_TIME,false);
            editor.commit();
            editor2.clear();
            editor2.commit();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(Home.this,FirstScreen.class );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        getSupportActionBar().setTitle(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onArticleSelected(int position) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();


//        transaction.setCustomAnimations(R.anim.card_flip_left_out, R.anim.card_flip_right_in);
        transaction.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,R.anim.fragment_slide_left_enter,R.anim.fragment_slide_right_exit);
        transaction.replace(R.id.fragment_container,new Sections());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
