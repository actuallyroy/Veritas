package com.elselse.veritas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class IntroActivity extends Activity {

    int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_activiy);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageView prevBtn = findViewById(R.id.prevBtn),
                introClipArt = findViewById(R.id.introClipArt);
        TextView introTxt = findViewById(R.id.introTxt);
        MaterialButton continueBtn = findViewById(R.id.continueBtn);
        LinearLayout skipBtn = findViewById(R.id.skipBtn);
        SharedPreferences sharedPreferences = getSharedPreferences("intro", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("intro", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }



        int[] introClipArts = {R.drawable.intro_1, R.drawable.intro_2, R.drawable.intro_3};
        String[] introText = {"Stay safe from fake news and don’t let it spread", "Get the latest news analysis from trusted sources", "Easy to use"};

        nextBtn.setOnClickListener(v -> {
            if (i < introClipArts.length) {
                introClipArt.setImageResource(introClipArts[i]);
                introTxt.setText(introText[i]);
                i++;
            }

            if (i == introClipArts.length) {
                continueBtn.setVisibility(MaterialButton.VISIBLE);
                nextBtn.setVisibility(ImageButton.GONE);
            }else{
                continueBtn.setVisibility(MaterialButton.GONE);
                nextBtn.setVisibility(ImageButton.VISIBLE);
            }

            if (i > 1) {
                prevBtn.setVisibility(ImageButton.VISIBLE);
            }else{
                prevBtn.setVisibility(ImageButton.GONE);
            }

            if(i > 2){
                skipBtn.setVisibility(LinearLayout.GONE);
            }else{
                skipBtn.setVisibility(LinearLayout.VISIBLE);
            }

        });

        prevBtn.setOnClickListener(v -> {
            if (i > 1) {
                i--;
                introClipArt.setImageResource(introClipArts[i - 1]);
                introTxt.setText(introText[i - 1]);
            }

            if (i == introClipArts.length) {
                continueBtn.setVisibility(MaterialButton.VISIBLE);
                nextBtn.setVisibility(ImageButton.GONE);
            }else{
                continueBtn.setVisibility(MaterialButton.GONE);
                nextBtn.setVisibility(ImageButton.VISIBLE);
            }

            if (i > 1) {
                prevBtn.setVisibility(ImageButton.VISIBLE);
            }else{
                prevBtn.setVisibility(ImageButton.GONE);
            }

            if(i > 2){
                skipBtn.setVisibility(LinearLayout.GONE);
            }else{
                skipBtn.setVisibility(LinearLayout.VISIBLE);
            }
        });

        continueBtn.setOnClickListener(v -> {
            continueToMain();
        });

        skipBtn.setOnClickListener(v -> {
            continueToMain();
        });
    }

    public void continueToMain(){
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        SharedPreferences sharedPreferences = getSharedPreferences("intro", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("intro", true);
        editor.apply();
        finish();
    }
}