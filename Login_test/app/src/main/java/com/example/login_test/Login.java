package com.example.login_test;

import androidx.appcompat.app.AppCompatActivity;

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

public class Login extends AppCompatActivity {

    EditText textID, textPW;
    Button buttonBringit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        textID = findViewById(R.id.edittext3);
        textPW = findViewById(R.id.edittext4);

        buttonBringit = findViewById(R.id.button2);



        buttonBringit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Lid = textID.getText().toString();
                String Lpw = textPW.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시

                                String mg = jsonObject.getString("mg"); //전달받은 json객체에서 내가 넣어둔 mg를 꺼내기

                                Toast.makeText(getApplicationContext(), "메세지 : " + mg, Toast.LENGTH_SHORT).show();

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

                LoginRequest loginRequest = new LoginRequest(Lid, Lpw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);

            }
        });
    }
}