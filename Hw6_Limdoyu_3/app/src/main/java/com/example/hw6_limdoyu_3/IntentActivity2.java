package com.example.hw6_limdoyu_3;

import static com.example.hw6_limdoyu_3.IntentActivity.korean;
import static com.example.hw6_limdoyu_3.IntentActivity.math;
import static com.example.hw6_limdoyu_3.IntentActivity.english;

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
        TextView tv_sum = (TextView)findViewById(R.id.tv_sum);
        TextView tv_avg = (TextView)findViewById(R.id.tv_avg);

        int sum = korean + math + english;
        float avg = sum/3;

        tv_sum.setText("총점 : " + Integer.toString(sum));
        tv_avg.setText("평균: " + Float.toString(avg));

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), IntentActivity.class);
                startActivity(intent);
            }
        });
    }
}