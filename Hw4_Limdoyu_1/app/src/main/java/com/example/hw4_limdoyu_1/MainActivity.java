package com.example.hw4_limdoyu_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textview = (TextView)findViewById(R.id.name);
        textview.setText("이름 : 임도유");
        textview.setTextSize(24);
        textview.setTextColor(0xffff00ff);
        textview.setBackgroundColor(0xff0000ff);
    }
}