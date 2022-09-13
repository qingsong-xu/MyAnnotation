package com.xmaroon.myannotation;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xmaroon.annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_test)
    TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XButterknife.bind(this);

        tvTest.setText("修改了");
    }
}