package com.example.hancher.testsurfaceview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mRoot;
    private FrameLayout mSurfaceLayout;
    private AsyncProgress mAsyncProgressView;

    private TextView mTvAboveSurface;

    private TextView mTvShow;
    private TextView mTvHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRoot = findViewById(R.id.root);
        mSurfaceLayout = findViewById(R.id.surface_layout);
        mAsyncProgressView = findViewById(R.id.async_progress);
        mTvAboveSurface = findViewById(R.id.above_surface);
        mTvShow = findViewById(R.id.tv_show);
        mTvHide = findViewById(R.id.tv_hide);

        mTvShow.setOnClickListener(this);
        mTvHide.setOnClickListener(this);
        mTvAboveSurface.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.equals(mTvAboveSurface)) {
            Toast.makeText(this, "click above TextView", Toast.LENGTH_SHORT).show();
        } else if (v.equals(mTvShow)) {
            mSurfaceLayout.setVisibility(View.VISIBLE);
            mAsyncProgressView.showAndStart();
        } else if (v.equals(mTvHide)) {
//            mSurfaceLayout.setVisibility(View.GONE);  //单纯调用这个不会生效
            mAsyncProgressView.hide();
        }
    }
}
