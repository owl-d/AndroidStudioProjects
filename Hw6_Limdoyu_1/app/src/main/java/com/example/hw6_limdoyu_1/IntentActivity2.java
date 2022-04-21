package com.example.hw6_limdoyu_1;

import static com.example.hw6_limdoyu_1.IntentActivity.birth;

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

        int age = 2022 - birth + 1;

        textview.setText("나이 : " + Integer.toString(age));

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), IntentActivity.class);
                startActivity(intent);
            }
        });
    }
}