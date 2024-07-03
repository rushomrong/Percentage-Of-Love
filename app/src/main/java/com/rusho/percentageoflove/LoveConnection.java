package com.rusho.percentageoflove;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.rusho.percentageoflove.dashboard.HomeView;
import com.rusho.percentageoflove.internet.CheckInternet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/** @noinspection ALL*/
public class LoveConnection extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextInputEditText fname, sname;
    Button calculate, reCalculate;
    AdView mAdView;
    ImageView heart,backPressed;
    TextView headingCongra, fnameTV, snameTV, result, percentage;

    private ProgressDialog dialog;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_connection);

        /*
         * Developer name : Rusho Mrong
         * Developer Details : I am a android apps developer & and front end web developer.
         * Developer Email : info@rushomrong.com, rushomarak@gmail.com
         * Website : https://www.rushomrong.com
         * */

        initialization();


        bannerAds();
        interstitialAds();


        calculate.setOnClickListener(v -> {

            //Checking Internet Connection
            CheckInternet checkInternet = new CheckInternet();
            if (!checkInternet.isConnected(this)) {
                showCustomDialog();
                return;
            }

            //check validation
            if (!validatedData()) {
                return;
            }

            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait while we calculating the percentage...");
            dialog.setCancelable(false);
            dialog.show();

            String firstname = Objects.requireNonNull(fname.getText()).toString();
            String secondname = Objects.requireNonNull(sname.getText()).toString();
            fetchData(firstname, secondname);

            fnameTV.setText(firstname);
            snameTV.setText(secondname);

            visibility();

        });


    }

    private void interstitialAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.AdsUnit_Interstitial), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void bannerAds() {
        /*------ Google Ads-----------*/

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initialization() {
        fname = findViewById(R.id.fname);
        sname = findViewById(R.id.sname);
        calculate = findViewById(R.id.calculate);
        headingCongra = findViewById(R.id.headingCongra);
        fnameTV = findViewById(R.id.fnameTV);
        snameTV = findViewById(R.id.snameTV);
        result = findViewById(R.id.result);
        percentage = findViewById(R.id.percentage);
        reCalculate = findViewById(R.id.again);

        heart = findViewById(R.id.heart);

        backPressed = findViewById(R.id.backPressed);

        backPressed.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HomeView.class)));

    }

    private void visibility() {

        headingCongra.setVisibility(View.VISIBLE);
        fnameTV.setVisibility(View.VISIBLE);
        snameTV.setVisibility(View.VISIBLE);
        result.setVisibility(View.VISIBLE);
        percentage.setVisibility(View.VISIBLE);
        heart.setVisibility(View.VISIBLE);
        reCalculate.setVisibility(View.VISIBLE);

    }

    /*Checking Internet Activity*/
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(LoveConnection.this).inflate(
                R.layout.warning_dialog, findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        builder.setCancelable(false);
        ((TextView) view.findViewById(R.id.textTitle)).setText(getText(R.string.CheckTitle));
        ((TextView) view.findViewById(R.id.textMessage)).setText(getText(R.string.CheckMsg));
        ((Button) view.findViewById(R.id.buttonYes)).setText(getText(R.string.CheckYesBtn));
        ((Button) view.findViewById(R.id.buttonNo)).setText(getText(R.string.CheckNoBtn));
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning);

        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), LoveConnection.class));
            finish();
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();


    }

    /*Do not touch the API part if you don't have an access of an API key.
    * Just leave it as it is with the API key of Mine.
    * This app is based on the API connections
    * Thanks for your understanding. Your developer - Rusho Mrong
    * */
    public void fetchData(String firstname, String secondname) {

        String BASE_URL = getString(R.string.Rusho_RapidAPIClient) + secondname + "&fname=" + firstname;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("x-rapidapi-host", getString(R.string.RapidApi_Host))
                .addHeader("x-rapidapi-key", getString(R.string.Rusho_RapidApiKey))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(LoveConnection.this, "Error +", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    String rusho = Objects.requireNonNull(response.body()).string();
                    LoveConnection.this.runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(rusho);
                            String rm1 = jsonObject.getString("percentage");
                            String rm2 = jsonObject.getString("result");
                            percentage.setText(rm1 + getText(R.string.percent));
                            result.setText(rm2);
                            calculate.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }

            }
        });

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


//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//                startActivity(new Intent(getApplicationContext(), LoveConnection.class));
//                finish();
//                break;
//            case R.id.nav_aboutapps:
//                /*About apps details will be here*/
//                startActivity(new Intent(getApplicationContext(), AppInfo.class));
//                finish();
//                break;
//            case R.id.nav_developer:
//                /*About app developer details will be here*/
//                startActivity(new Intent(getApplicationContext(),DeveloperInfo.class));
//                finish();
//                break;
//            case R.id.nav_Sms:
//                startActivity(new Intent(getApplicationContext(), LoveMessages.class));
//                finish();
//                break;
//            case R.id.nav_share:
//                /*Share the app to the others*/
//
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//
//                // type of the content to be shared
//                sharingIntent.setType("text/plain");
//
//                // subject of the content. you can share anything
//                String shareSubject = "Download it now! Check your love percentage with this app";
//
//                // passing body of the content
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getApplication().getPackageName());
//
//                // passing subject of the content
//                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
//                startActivity(Intent.createChooser(sharingIntent, "Share using"));
//
//                break;
//        }
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private boolean validatedData() {
        String name = fname.getEditableText().toString().trim();
        String name2 = sname.getEditableText().toString().trim();

        if (name.isEmpty()) {
            fname.setError("Please type your name");
            fname.requestFocus();
            return false;
        } else if (name2.isEmpty()) {
            sname.setError("Enter your partner name");
            sname.requestFocus();
        } else {
            fname.setError(null);
            sname.setError(null);
            return true;
        }
        return false;
    }

    public void reCalculate(View view) {
        /*Ads with calling new activity*/
        if (mInterstitialAd != null) {
            mInterstitialAd.show(LoveConnection.this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    startActivity(new Intent(getApplicationContext(), LoveConnection.class));
                    mInterstitialAd = null;
                    interstitialAds();

                }
            });

        }else {
            Intent intent = new Intent(getApplicationContext(),  LoveConnection.class);
            startActivity(intent);
        }

    }

}
