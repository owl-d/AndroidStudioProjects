package com.example.hw6_limdoyu_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class IntentActivity extends AppCompatActivity {

    static int temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        Button button = (Button)findViewById(R.id.btn);
        EditText edittext = (EditText)findViewById(R.id.et);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                temperature = Integer.parseInt(edittext.getText().toString());

                Intent intent = new Intent(getApplicationContext(), IntentActivity2.class);
                startActivity(intent);
            }
        });
    }
}