package com.example.db_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Item extends AppCompatActivity {
    String number, content;
    TextView text_number, text_content;
    Button but_update, but_delete;
    EditText edit;
    Boolean sw = false; // 스레드 중인지 체크하기 용도 (스레드 중인데 다시 실행되는 거 방지)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        Intent intent = getIntent();    // 인텐트 받아오기 위해서 선언
        number = intent.getStringExtra("number");   // 넘버 받아오기
        content = intent.getStringExtra("content"); // 내용 받아오기

        text_number = findViewById(R.id.item_text_number);  // 글번호
        text_content = findViewById(R.id.item_text_content);    // 내용
        but_update = findViewById(R.id.item_but_update);    // 수정 버튼
        but_delete = findViewById(R.id.item_but_delete);    // 삭제 버튼
        edit = findViewById(R.id.item_edit);    // 입력 에딧 텍스트

        text_number.setText(number);
        text_content.setText(content);

        but_update.setOnClickListener(new View.OnClickListener() { // 업데이트 버튼 클릭 설정
            @Override
            public void onClick(View v) {
                Thread_update thread = new Thread_update(); // 스레드 생성
                thread.start(); // 스레드 시작
            }
        });
        but_delete.setOnClickListener(new View.OnClickListener() { // 딜리트 버튼 클릭 설정
            @Override
            public void onClick(View v) {
                Thread_delete thread = new Thread_delete(); // 스레드 생성
                thread.start(); // 스레드 시작
            }
        });
    }

    class Thread_update extends Thread {    // 수정 버튼 누르면 실행하는 업데이트
        public void run() {
            sw = true;  // 스레드 중이므로 트루로
            if (edit.getText().toString().length() > 0) {   // 글자를 입력했는지 체크
                String text = Web.update(number, edit.getText().toString());
                runOnUiThread(new Runnable() {  // 스레드에서 Ui 수정하려면 추가
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();   // 토스트 메시지
                        if (text.equals("수정")) {
                            text_content.setText(edit.getText().toString()); // 적은 값으로 내용 변경
                            edit.setText("");   // 성공했으면 에딧텍스트 지우기
                        }
                    }
                });
            } else {    // 입력한 글자가 없을 때 실행
                runOnUiThread(new Runnable() {  // 스레드에서 Ui 수정하려면 추가
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "입력한 글자가 없음", Toast.LENGTH_SHORT).show();   // 토스트 메시지
                    }
                });
            }
            sw = false; // 스레드 끝나면 펄스로
        }
    }

    class Thread_delete extends Thread {    // 삭제 버튼 누르면 실행하는 딜리트
        public void run() {
            sw = true;  // 스레드 중이므로 트루로
            String text = Web.delete(number);
            runOnUiThread(new Runnable() {  // 스레드에서 Ui 수정하려면 추가
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();   // 토스트 메시지
                    if (text.equals("삭제")) {
                        finish();   // 삭제했으면 뷰 닫기
                    }
                }
            });
            sw = false; // 스레드 끝나면 펄스로
        }
    }
}