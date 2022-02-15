package com.example.db_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Main extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {   // 스와이프 임폴트하기
    EditText edit;  // 글자 적는 에딧텍스트
    Button but; // 확인 버튼
    SwipeRefreshLayout swipe; // 스와이프
    Boolean sw = false; // 스레드 중인지 체크하기 용도 (스레드 중인데 다시 실행되는 거 방지)
    ListAdapter adapter;  // 어댑터
    RecyclerView recycler;  // 리사이클러뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        swipe = findViewById(R.id.main_swipe);   // 스와이프
        swipe.setOnRefreshListener(this);

        edit = findViewById(R.id.main_edit);    // 에딧텍스트 받아오기
        but = findViewById(R.id.main_but); // 버튼 뷰 받아오기

        but.setOnClickListener(new View.OnClickListener() { // 버튼 클릭 설정
            @Override
            public void onClick(View v) {
                Thread_insert thread = new Thread_insert(); // 인설트 스레드 생성
                thread.start(); // 스레드 시작
            }
        });

        recycler = findViewById(R.id.main_recycler);   // 리사이클러뷰 받아오기
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);

        adapter = new ListAdapter();
        recycler.setAdapter(adapter);   // UI 변경

        adapter.setOnItemClickListener(new ListAdapterClickListener() { // 어댑터 클릭 설정
            @Override
            public void onItemClick(ListAdapter.ViewHolder holder, View view, int position) {
                info item = adapter.getItem(position);

                Intent intent = new Intent(getBaseContext(), Item.class);
                intent.putExtra("number", item.getNumber());    // 인텐트에 변수 저장
                intent.putExtra("content", item.getContent());  // 인텐트에 변수 저장
                startActivity(intent); // 뷰 열기
            }
        });
    }

    @Override
    protected void onResume() { // 화면 재개될 때 리스트 최신화 하도록(처음 시작할떄도 onCreate가 끝나고 실행됨)
        super.onResume();
        select();   // 리스트 받아오는 셀렉트 함수 실행
    }

    private void select() {  // 리스트 받아오는 셀렉트 함수
        Thread_select thread = new Thread_select();
        thread.start();
    }

    class Thread_select extends Thread {    // 리스트 받아오는 스레드
        public void run() {
            if (!sw) {  // 스레드가 아닐때만 실행하게
                sw = true;  // 스레드 중이므로 트루로
                ArrayList<info> result = Web.select();  // db에서 정보 받아오기
                adapter.setItems(result);   // 어댑터에 받아온 정보 저장

                runOnUiThread(new Runnable() {  // 스레드에서 Ui 수정하려면 추가
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged(); // 어댑터 최신화
                        swipe.setRefreshing(false); // 스와이프 이미지 제거
                    }
                });
                sw = false; // 스레드 끝나면 펄스로
            }
        }
    }


    class Thread_insert extends Thread {    // 버튼 누르면 실행하는 인설트
        public void run() {
            if (!sw) {  // 스레드가 아닐때만 실행하게
                sw = true;  // 스레드 중이므로 트루로
                if (edit.getText().toString().length() > 0) {   // 글자를 입력했는지 체크
                    String text = Web.insert(edit.getText().toString());
                    runOnUiThread(new Runnable() {  // 스레드에서 Ui 수정하려면 추가
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();   // 토스트 메시지
                            if (text.equals("성공")) {
                                edit.setText("");   // 성공했으면 에딧텍스트 지우기
                                select();   // 입력했으니 리스트 최신화
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
    }

    @Override
    public void onRefresh() {   // 리사이클러뷰 아래로 당기면 스와이프 시작
        swipe.setRefreshing(true);  // 스와이프 이미지 시작
        select();   // 리스트 받아오는 셀렉트 함수 실행
    }
}