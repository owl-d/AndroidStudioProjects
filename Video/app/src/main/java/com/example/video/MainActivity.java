package com.example.video;

import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback { //콜백 관련 임플리먼트

    private Camera camera;
    private MediaRecorder mediaRecorder; //동영상 찍을 때 사용
    private Button btn_record;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;

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

        btn_record = (Button)findViewById(R.id.btn_record); //녹화버튼 눌렀을 때
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) { //동영상 녹화중이라면 저장하고 꺼라
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording = false;
                } else { //동영상 녹화 중이 아니라면 녹화 시작
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                            try { //중요한 부분
                                mediaRecorder = new MediaRecorder(); //녹화 객체 만들기
                                camera.unlock(); //잠겨있는 카메라 풀기
                                mediaRecorder.setCamera(camera);
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER); //동영상 녹화 시작 소리 내기
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P)); //영상 화질(해상도) 설정
                                mediaRecorder.setOrientationHint(90); //촬영 각도 맞춤
                                mediaRecorder.setOutputFile("/sdcard/test.mp4"); //저장 경로
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface()); //surfaceholder에 서페이스 가져와서 프리뷰(미리보기 화면) 세팅 : 동영상 녹화 화면 보여줌
                                mediaRecorder.prepare(); //프리뷰 화면 준비
                                mediaRecorder.start(); //프리뷰 보여주기
                                recording = true;
                            } catch (Exception e) { //예외 발생 시 동영상 녹화 끄기
                                e.printStackTrace();
                                mediaRecorder.release();
                            }
                        }
                    });
                }
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