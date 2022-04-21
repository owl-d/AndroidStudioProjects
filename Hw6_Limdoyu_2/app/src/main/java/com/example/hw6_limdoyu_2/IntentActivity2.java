package com.example.hw6_limdoyu_2;

import static com.example.hw6_limdoyu_2.IntentActivity.temperature;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IntentActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent2);

        Button button = (Button)findViewById(R.id.btn);
        TextView textview = (TextView)findViewById(R.id.tv);

        float F_temp = (float) (temperature * 1.8 + 32);

        textview.setText("화씨 온도 : " + Float.toString(F_temp));

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), IntentActivity.class);
                startActivity(intent);
            }
        });
    }
}