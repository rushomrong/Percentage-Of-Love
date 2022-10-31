package com.rusho.percentageoflove.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.rusho.percentageoflove.AppInfo;
import com.rusho.percentageoflove.DeveloperInfo;
import com.rusho.percentageoflove.LoveConnection;
import com.rusho.percentageoflove.LoveMessages;
import com.rusho.percentageoflove.R;

public class HomeView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private ReviewInfo reviewInfo;
    private ReviewManager manager;
    Button btnRating;
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);
        activityReviewInfo();

        setTitle("Percentage of Love");

        /*
         * Developer name : Rusho Mrong
         * Developer Details : I am a android apps developer & and front end web developer.
         * Developer Email : info@rushomrong.com
         * Website : https://www.rushomrong.com
         * */

        btnRating = findViewById(R.id.btnRating);

        btnRating.setOnClickListener((view)-> startReviewFlow());


        /*-------------------Hooks----------------*/
        drawerLayout = findViewById(R.id.drawar_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);


        /*-------------------Hooks----------------*/
        bannerAds();

        /*-------------------Toolbar Support----------------*/
        setSupportActionBar(toolbar);
        /*-------------------Toolbar Support----------------*/


        /*-------------------Navigation Drawer Menu----------------*/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*-------------------Navigation Drawer Menu----------------*/

        navigationView.setNavigationItemSelectedListener(this);

        /*-------------------Selected default Menu----------------*/
        navigationView.setCheckedItem(R.id.nav_home);


    }

    private void bannerAds() {
        /*------ Google Ads-----------*/

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    //Apps Rating System
    void activityReviewInfo(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> managerInfotask = manager.requestReviewFlow();
        managerInfotask.addOnCompleteListener((task) -> {
           if (task.isSuccessful()){
               reviewInfo = task.getResult();
           }else {
               Toast.makeText(this, "Percentage Of Love Review Failed To Start", Toast.LENGTH_SHORT).show();
           }
        });

    }

    void startReviewFlow(){
        if (reviewInfo !=null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> Toast.makeText(this, "Rating is Completed", Toast.LENGTH_SHORT).show());
        }
    }

    public void OpenLuvCalculator(View view) {

        startActivity(new Intent(getApplicationContext(), LoveConnection.class));
        finish();
    }

    public void openGetFreeSMS(View view) {
        startActivity(new Intent(getApplicationContext(), LoveMessages.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeView.class));
                finish();
                break;
            case R.id.nav_aboutapps:
                /*About apps details will be here*/
                startActivity(new Intent(getApplicationContext(), AppInfo.class));
                finish();
                break;
            case R.id.nav_developer:
                /*About app developer details will be here*/
                startActivity(new Intent(getApplicationContext(), DeveloperInfo.class));
                finish();
                break;
            case R.id.nav_Sms:
                startActivity(new Intent(getApplicationContext(), LoveMessages.class));
                finish();
                break;
            case R.id.nav_share:
                /*Share the app to the others*/

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                // type of the content to be shared
                sharingIntent.setType("text/plain");

                // subject of the content. you can share anything
                String shareSubject = "Download it now! Check your love percentage with this app";

                // passing body of the content
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getApplication().getPackageName());

                // passing subject of the content
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    public void ShareApp(View view) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        // type of the content to be shared
        sharingIntent.setType("text/plain");

        // subject of the content. you can share anything
        String shareSubject = "Download it now! Check your love percentage with this app";

        // passing body of the content
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getApplication().getPackageName());

        // passing subject of the content
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
}