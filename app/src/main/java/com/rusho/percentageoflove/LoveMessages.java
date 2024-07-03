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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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
public class LoveMessages extends AppCompatActivity {

    TextInputEditText Category;
    Button searchNow,nextbtn;
    AdView mAdView;
    TextView Message;
    ImageView backPressed,shareResult;
    LottieAnimationView animationView;

    private ProgressDialog dialog;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_messages);

        setTitle("Percentage of Love");

        /*
         * Developer name : Rusho Mrong
         * Developer Details : I am a android apps developer & and front end web developer.
         * Developer Email : info@rushomrong.com
         * Website : https://www.rushomrong.com
         * */

        initialization();
        bannerads();
        interstitialAds();

        backPressed.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HomeView.class)));

        searchNow.setOnClickListener(v -> {


            //Checking Internet Connection
            CheckInternet checkInternet = new CheckInternet();
            if (!checkInternet.isConnected(this)) {
                showCustomDialog();
                return;
            }

            //check validation
            if (!validatedSearch()) {
                return;
            }

            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading Messages...");
            dialog.setCancelable(false);
            dialog.show();


            /*Ads with displaying the searched messages*/
            if (mInterstitialAd != null) {
                mInterstitialAd.show(LoveMessages.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        String getCategory = Objects.requireNonNull(Category.getText()).toString();
                        String getMessages = Objects.requireNonNull(Message.getText()).toString();
                        fetchData(getCategory,getMessages);
                        Message.setText(getMessages);
                        mInterstitialAd = null;
                        interstitialAds();

                    }
                });

            }else {
                /*if somehow google ads failed to load then directly display a searched category messages */
                String getCategory = Objects.requireNonNull(Category.getText()).toString();
                String getMessages = Objects.requireNonNull(Message.getText()).toString();
                fetchData(getCategory,getMessages);
                Message.setText(getMessages);

            }

            visibility();

        });

        /*For continuously searching messages with related category */
        nextbtn.setOnClickListener(v -> {

            //Checking Internet Connection
            CheckInternet checkInternet = new CheckInternet();
            if (!checkInternet.isConnected(this)) {
                showCustomDialog();
                return;
            }

            //check validation
            if (!validatedSearch()) {
                return;
            }

            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading Next Messages...");
            dialog.setCancelable(true);
            dialog.show();

            /*Requesting search category to getting the related data from the server*/
            String getCategory = Objects.requireNonNull(Category.getText()).toString();
            String getMessages = Objects.requireNonNull(Message.getText()).toString();
            fetchData(getCategory,getMessages);

            //Displaying all the messages from the server
            Message.setText(getMessages);

        });

        shareResult.setOnClickListener(v -> {
            String sms = Message.getText().toString();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "MSG From %tage Of Love App");
            sendIntent.putExtra(Intent.EXTRA_TEXT, sms +"\n \nDownload our app from:"+getString(R.string.playStore_Link)+ getApplication().getPackageName());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

    }

    private void interstitialAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.AdsUnit_MSGAd), adRequest,
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

    private void visibility() {
        nextbtn.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.VISIBLE);
        Message.setVisibility(View.VISIBLE);
        shareResult.setVisibility(View.VISIBLE);

    }

    /*Requesting API client for the data that are requested by the users*/
    private void fetchData(String getCategory,String getMessages) {
        String BASE_URL = getString(R.string.Rusho_SMSAPIClient) +getCategory +  "&Message=" + getMessages;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("x-rapidapi-host", getString(R.string.Rusho_RapidSMSHost))
                .addHeader("x-rapidapi-key", getString(R.string.Rusho_RapidSMSKey))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(LoveMessages.this, "Error +", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                //Converting Json data as a string for the users
                if (response.isSuccessful()) {
                    String rushomrong = Objects.requireNonNull(response.body()).string();
                    LoveMessages.this.runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(rushomrong);
                            String rm = jsonObject.getString("Message");
                            Message.setText(rm);
                            searchNow.setVisibility(View.INVISIBLE);
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

    /*
    * Restricted the Search form with the validation focus
    * */
    private boolean validatedSearch() {
        String messages = Category.getEditableText().toString().trim();

        if (messages.isEmpty()) {
            Category.setError("Please type listed category!");
            Category.requestFocus();
            return false;
        } else {
            Category.setError(null);
            return true;
        }
    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(LoveMessages.this).inflate(
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

    private void bannerads() {

        /*------ Google Ads-----------*/

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initialization() {
        Category = findViewById(R.id.Category);
        searchNow = findViewById(R.id.searchNow);
        mAdView = findViewById(R.id.adView);
        Message = findViewById(R.id.Message);
        nextbtn = findViewById(R.id.nextbtn);

        backPressed = findViewById(R.id.backPressed);

        animationView = findViewById(R.id.animationView);
        shareResult = findViewById(R.id.shareResult);

    }

}