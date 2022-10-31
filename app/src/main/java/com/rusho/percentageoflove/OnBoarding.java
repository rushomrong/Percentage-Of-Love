package com.rusho.percentageoflove;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.rusho.percentageoflove.dashboard.HomeView;
import com.rusho.percentageoflove.helperclass.SliderAdapter;
import com.rusho.percentageoflove.internet.CheckInternet;

public class OnBoarding extends AppCompatActivity {

    LinearLayout indicator_layout;
    ViewPager viewPager;
    Button backbtn, nextbtn, skipbtn;
    TextView[] dots;


    SliderAdapter sliderAdapter;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_boarding);

        backbtn = findViewById(R.id.backbtn);
        nextbtn = findViewById(R.id.nextbtn);
        skipbtn = findViewById(R.id.skipbtn);
        interstitialAds();

        backbtn.setOnClickListener(v -> {

            if (getitem(0) > 0) {

                viewPager.setCurrentItem(getitem(-1), true);

            }

        });

        //Next button calling for the next intent
        /*this is the button where we want to show our next part or pages*/
        nextbtn.setOnClickListener(v -> {

            if (getitem(0) < 3)

                viewPager.setCurrentItem(getitem(1), true);

            else {

                //Checking Internet Connection
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(this)) {
                    showCustomDialog();
                    return;
                } else {
                    if (mInterstitialAd !=null){
                        mInterstitialAd.show(OnBoarding.this);
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                startActivity(new Intent(getApplicationContext(), HomeView.class));
                                mInterstitialAd = null;
                                interstitialAds();
                            }
                        });

                    } else {
                        Intent intent = new Intent(getApplicationContext(),  HomeView.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }

        });

        //Skip button calling for the next intent
        /*this is the button where we want to show our next part or pages*/
        skipbtn.setOnClickListener(v -> {

            //Checking Internet Connection
            CheckInternet checkInternet = new CheckInternet();
            if (!checkInternet.isConnected(this)) {
                showCustomDialog();
                return;
            } else {
                if (mInterstitialAd !=null){
                    mInterstitialAd.show(OnBoarding.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(new Intent(getApplicationContext(), HomeView.class));
                            mInterstitialAd = null;
                            interstitialAds();

                        }
                    });
                } else {
                    Intent intent = new Intent(getApplicationContext(),  HomeView.class);
                    startActivity(intent);
                    finish();
                }
            }

        });

        //Initialize the methods of
        viewPager = findViewById(R.id.slider);
        indicator_layout = findViewById(R.id.indicator_layout);

        //Call slider
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setUpindicator(0);
        }
        viewPager.addOnPageChangeListener(viewlistener);


    }

    /*Google Admob  Ads Part*/
    private void interstitialAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.AdsUnit_SKPNXTBTN), adRequest,
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

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(OnBoarding.this).inflate(
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
            startActivity(new Intent(getApplicationContext(), OnBoarding.class));
            finish();
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    }

    private int getitem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUpindicator(int position) {

        dots = new TextView[4];
        indicator_layout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));//gathering the dots from the html part
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.black, getApplicationContext().getTheme()));
            indicator_layout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.buton1, getApplicationContext().getTheme()));

    }

    //Dots view changer calling
    /*This is the part for the HTML dots of the love calculator changing color one after another*/

    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onPageSelected(int position) {

            setUpindicator(position);

            if (position > 0) {

                backbtn.setVisibility(View.VISIBLE);
            } else {

                backbtn.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };
}
