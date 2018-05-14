package com.example.hancher.testsurfaceview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private AsyncProgress mAsyncProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAsyncProgressView = findViewById(R.id.async_progress);
        mAsyncProgressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAsyncProgressView.setVisibility(View.GONE);
            }
        }, 10000);
    }
}
