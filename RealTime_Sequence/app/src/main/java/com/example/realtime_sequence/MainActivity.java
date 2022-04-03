package com.example.realtime_sequence;

import static com.example.realtime_sequence.CameraPreview.byte_img_Stream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static CameraPreview surfaceView;
    private SurfaceHolder holder;
    private static Button camera_preview_button;
    private static Camera mCamera;
    private int RESULT_PERMISSIONS = 100;
    public static MainActivity getInstance;

    private Button btn_record;
    private TextView responseText;
    private boolean record = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //카메라 프리뷰를  전체화면으로 보여주기 위해 셋팅한다.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //안드로이드 6.0 이상 버전에서는 CAMERA 권한 허가를 요청한다.
        //권한 설정
        requestPermissionCamera();

        DrawOn drawon = new DrawOn(this);

        btn_record = (Button)findViewById(R.id.btn_record); //녹화버튼 눌렀을 때 전송 시작
        responseText = findViewById(R.id.responseText);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (record) {
                    Log.d("TAG", "Sending Stop");
                    record = false;
                    responseText.setText("Sending Finish");
                }
                else {
                    Log.d("TAG", "Sending Start");
                    record = true;
                }

                new Thread(new Runnable() {
                    public void run() {

                        while(record){
                            TextView responseText = findViewById(R.id.responseText);
                            Log.d("TAG", "Base64 Encoding : " + byte_img_Stream);
                            post_http();
                            //setContentView(drawon);

                            try {
                                Thread.sleep(2000); //2초 대기
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }
        });
    }

    public static Camera getCamera(){ return mCamera; }

    private void post_http() {
        Log.d("TAG", "Run post_http");
        String postUrl = "http://3.37.237.18:5000/";
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody postBody = RequestBody.create(mediaType, byte_img_Stream);

        postRequest(postUrl, postBody);
    }

    private void postRequest(String postUrl, RequestBody postBody) {
        Log.d("TAG", "Run postRequest");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()

                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server");
                        Log.d("TAG", "PostRequeset : Failure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        try {
                            Log.d("TAG", "Response String : " + response.body().string());
                            responseText.setText("Server Connect Success");
                            //responseText.setText("Server Connection Success\nreturn : " + response.body().string()); //받아온 text로 대체

                            if (record == false) {
                                responseText.setText("Server Sending Finish");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        responseText.setText("Success to Connect to Server");
                        Log.d("TAG", "PostRequeset : Upload Success");

                    }
                });
            }
        });
    }

    private void setInit(){
        Log.d("TAG", "Run setInit");
        getInstance = this;

        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        mCamera = Camera.open();

        setContentView(R.layout.activity_main);

        // SurfaceView를 상속받은 레이아웃을 정의한다.
        surfaceView = (CameraPreview) findViewById(R.id.preview);

        // SurfaceView 정의 - holder와 Callback을 정의한다.
        holder = surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        RESULT_PERMISSIONS);

            }else {
                setInit();
            }
        }else{  // version 6 이하일때
            setInit();
            return true;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (RESULT_PERMISSIONS == requestCode) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가시
                setInit();
            } else {
                // 권한 거부시
            }
            return;
        }

    }
}

class DrawOn extends View{ // 사각형 그리기
    public DrawOn(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint pt = new Paint();
        pt.setColor(Color.GREEN);
        //pt.setStrokeWidth(5);

        Rect r = new Rect();
        r.set(100, 100, 500, 500);
        canvas.drawRect(r, pt);

        canvas.drawLine(100,350,100,400, pt);
        canvas.drawLine(100,350,200,350, pt);

        canvas.drawLine(520,350,620,350, pt);
        canvas.drawLine(620,350,620,400, pt);

        canvas.drawLine(100,600,100,650, pt);
        canvas.drawLine(100,650,200,650, pt);

        canvas.drawLine(520,650,620,650, pt);
        canvas.drawLine(620,650,620,600, pt);
    }
}
