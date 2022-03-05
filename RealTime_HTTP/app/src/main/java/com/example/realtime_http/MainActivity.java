package com.example.realtime_http;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.notification.Condition;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import android.util.Base64;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback { //콜백 관련 임플리먼트

    private Camera camera;
    private MediaRecorder mediaRecorder; //동영상 찍을 때 사용
    private Button btn_record;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;
    private ImageView imageView;

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

                        Log.d("TAG", "onClick -> Run");
                        try { //중요한 부분
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(i, 0);
                            Log.d("TAG", "Intent : ACTION_IMAGE_CAPTURE");
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

            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.d("TAG", "onActivityResult : Image Ready");

            /// Base64 Image Encoding ////////////////////////////////////////////////////////////
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            byte[] byte_image = byteArrayOutputStream.toByteArray();

            String byte_img_Stream = Base64.encodeToString(byte_image, 0);

            Log.d("TAG", "Base64 Encoding : " + byte_img_Stream);

            /// Base64 Image Decoding ///////////////////////////////////////////////////////////
            byte[] decoded_byte = Base64.decode(byte_img_Stream, 0);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decoded_byte);

            Bitmap decoded_image = BitmapFactory.decodeStream(byteArrayInputStream);

            /*
            /// HTTP POST TX ////////////////////////////////////////////////////////////////////
            try {
                URL url = new URL("IP 주소");
                String result = "";

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(1000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                Log.d("TAG", "Output Stream Closed");

                result = readStream(conn.getInputStream());

                conn.disconnect();
                Log.d("TAG", "result : " + result);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void uploadWithTransferUtility() {

        File fileToUpload = new File("/sdcard/test.mp4");
        AmazonS3 s3;
        TransferUtility transferUtility;

        // Cognito 샘플 코드. CredentialsProvider 객체 생성
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:b0554968-0272-480a-b3ed-9e4185b58d5c", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전 us-east-1
        );

        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        transferUtility = new TransferUtility(s3, getApplicationContext());

        // 업로드 실행. object: "SomeFile.mp4". 두 번째 파라메터는 Local경로 File 객체.
        TransferObserver uploadObserver = transferUtility.upload("s3testsequence/test", "test.mp4", fileToUpload);

        // 업로드 과정을 알 수 있도록 Listener를 추가할 수 있다.
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, ex.getMessage());
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