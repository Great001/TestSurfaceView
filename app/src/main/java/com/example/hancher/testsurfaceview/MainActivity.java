package com.example.hancher.testsurfaceview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AsyncProgress mAsyncProgressView;

    private TextView mTvAboveSurface;
    private TextView mTvBottom;

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

        mTvAboveSurface = findViewById(R.id.above_surface);
        mTvBottom = findViewById(R.id.align_bottom);

        mTvBottom.setOnClickListener(this);
        mTvAboveSurface.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.equals(mTvAboveSurface)) {
            Toast.makeText(this,"click above TextView",Toast.LENGTH_SHORT).show();
        } else if(v.equals(mTvBottom)){
            Toast.makeText(this,"click bottom TextView",Toast.LENGTH_SHORT).show();
        }
    }
}
