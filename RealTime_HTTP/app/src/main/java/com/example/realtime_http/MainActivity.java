package com.example.realtime_http;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.notification.Condition;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Base64;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback { //콜백 관련 임플리먼트

    private Camera camera;
    private MediaRecorder mediaRecorder; //동영상 찍을 때 사용
    private Button btn_record;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;
    private ImageView imageView;

    public String bbox;
    public Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //권한 설정
        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("녹화를 위하여 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다. 설정 > 권한에서 허용해주세요.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();

        imageView = (ImageView) findViewById(R.id.image_view);

        btn_record = (Button)findViewById(R.id.btn_record); //녹화버튼 눌렀을 때
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();

                        Log.d(ContentValues.TAG, "onClick -> Run");
                        try { //중요한 부분
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(i, 0);
                            Log.d(ContentValues.TAG, "Intent : ACTION_IMAGE_CAPTURE");
                        } catch (Exception e) { //예외 발생 시 동영상 녹화 끄기
                            e.printStackTrace();
                            mediaRecorder.release();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();

            imageBitmap = (Bitmap) extras.get("data"); //찍은 사진
            Log.d("TAG", "onActivityResult : Image Ready");

            DrawOn draw = new DrawOn(this);
            setContentView(draw);

            /// Base64 Image Encoding ////////////////////////////////////////////////////////////
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            byte[] byte_image = byteArrayOutputStream.toByteArray();

            String byte_img_Stream = Base64.encodeToString(byte_image, 0);

            Log.d("TAG", "Base64 Encoding : " + byte_img_Stream);
            //////////////////////////////////////////////////////////////////////////////////////

            post_http(byte_img_Stream);
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
            pt.setStrokeWidth(5);

            int width = canvas.getWidth();
            int height = canvas.getHeight();

            Rect src = new Rect(0, 0, imageBitmap.getWidth() - 1, imageBitmap.getHeight() - 1);
            Rect dest = new Rect(0, 0, width - 1, height - 1);
            canvas.drawBitmap(imageBitmap, src, dest, null);

            //canvas.drawBitmap(imageBitmap, 0, 0, null);

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

    private void post_http(String byte_img_Stream) {
        String postUrl = "http://3.37.237.18:5000/";
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody postBody = RequestBody.create(mediaType, byte_img_Stream);

        postRequest(postUrl, postBody);
    }

    private void postRequest(String postUrl, RequestBody postBody) {
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
                            bbox = response.body().string();
                            responseText.setText("Server Connection Success\nreturn : " + bbox); //받아온 text로 대체
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        responseText.setText("Success to Connect to Server");
                        Log.d("TAG", "PostRequeset : Upload Success");

                        //setContentView(draw);

                    }
                });
            }
        });
    }

    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() { //권한 허용 되었을 때
            Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show(); //권한 허가 되었다는 토스트 띄우기

            camera = Camera.open();
            camera.setDisplayOrientation(90);
            surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(MainActivity.this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) { //권한 거부 되었을 때
            Toast.makeText(MainActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //서페이스뷰 처음 생성 되었을 때 생명 주기

    }

    private void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) { //서페이스홀더가 null이면 돌아와라
            return;
        }

        try {
            camera.stopPreview(); //카메라 초기화해주는 작업 : 카메라가 계속 켜져있거나 꺼져있을 때 받아와서 처리
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {

        camera = cam;

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { //서페이스에 변화가 있으면 이를 감지해 호출 -> 초기화 시킴
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}