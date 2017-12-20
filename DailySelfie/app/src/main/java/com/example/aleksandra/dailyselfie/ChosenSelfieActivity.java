package com.example.aleksandra.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ChosenSelfieActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_selfie);

        Intent intent = getIntent();
        String selfiePath = intent.getStringExtra(DailySelfieActivity.SELFIE_PATH);

        ImageView image = (ImageView) findViewById(R.id.selfie);
        image.setImageBitmap(DailySelfieActivity.loadPic(selfiePath, false));
    }
}
