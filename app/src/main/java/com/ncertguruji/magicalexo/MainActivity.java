package com.ncertguruji.magicalexo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.potyvideo.library.AndExoPlayerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndExoPlayerView andExoPlayerView = findViewById(R.id.andExoPlayerView);
        andExoPlayerView.setSource("http://class6sanskrit.ncertguruji.com/vid/ch3.mp4");
        getSupportActionBar().hide();
    }
}