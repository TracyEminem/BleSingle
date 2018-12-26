package com.example.tracyeminem.blesingle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView mText;
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = findViewById(R.id.tv_time);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        Intent intent = new Intent(this,TestService.class);
        startForegroundService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mText.setText(ms2Date(Long.parseLong(sharedPreferenceUtil.getTime())));
    }

    public static String ms2Date(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }
}
