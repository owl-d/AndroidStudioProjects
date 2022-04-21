package com.example.hw6_limdoyu_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class IntentActivity extends AppCompatActivity {

    static int korean;
    static int math;
    static int english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        Button button = (Button)findViewById(R.id.btn);
        EditText et_kor = (EditText)findViewById(R.id.et_korean);
        EditText et_math = (EditText)findViewById(R.id.et_math);
        EditText et_eng = (EditText)findViewById(R.id.et_english);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                korean = Integer.parseInt(et_kor.getText().toString());
                math = Integer.parseInt(et_math.getText().toString());
                english = Integer.parseInt(et_eng.getText().toString());

                Intent intent = new Intent(getApplicationContext(), IntentActivity2.class);
                startActivity(intent);
            }
        });
    }
}