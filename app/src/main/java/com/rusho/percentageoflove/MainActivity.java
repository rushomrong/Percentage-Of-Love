package com.rusho.percentageoflove;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIMER = 5000;

    ImageView splash_bg,logo;
    TextView developedBy;

    //Animation
    Animation sideanim,bottomanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        splash_bg = findViewById(R.id.splash_bg);
        developedBy = findViewById(R.id.developedBy);
        logo = findViewById(R.id.logo);

        //Animation
        sideanim = AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomanim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //Set Animation
        splash_bg.setAnimation(sideanim);
        developedBy.setAnimation(bottomanim);
        logo.setAnimation(sideanim);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(MainActivity.this, OnBoarding.class);
            startActivity(intent);
            finish();

        },SPLASH_TIMER);

    }
}