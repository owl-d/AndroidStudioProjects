package com.example.serverlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    EditText textID, textPW, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button button = findViewById(R.id.button);
        textID = findViewById(R.id.edittext);
        textPW = findViewById(R.id.edittext2);
        message = findViewById(R.id.mgText);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = textID.getText().toString(); //텍스트뷰에서 아이디값을 가져온다.
                String pw = textPW.getText().toString(); //텍스트뷰에서 비밀번호값을 가져온다.
                String Emessage = message.getText().toString(); //텍스트뷰에서 메세지를 가져온다.

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시
                                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, Login.class); //로그인 객체로 가기
                                startActivity(intent);

                            } else {//실패시

                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(id, pw, Emessage, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(registerRequest);

            }
        });
    }
}