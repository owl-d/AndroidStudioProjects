package com.example.camera_surfaceview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity
        implements SurfaceHolder.Callback {
    @SuppressWarnings("deprecation")
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button button;
    String str;

    private int RESULT_PERMISSIONS = 100;

    @SuppressWarnings("deprecation")
    Camera.PictureCallback jpegCallback;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionCamera();

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, jpegCallback);
            }
        });

        getWindow().setFormat(PixelFormat.UNKNOWN);


        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);



        jpegCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    str = String.format("/sdcard/%d.jpg",
                            System.currentTimeMillis());
                    outStream = new FileOutputStream(str);

                    outStream.write(data);
                    outStream.close();
                }

                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                finally {
                }

                Toast.makeText(getApplicationContext(),
                        "Picture Saved", Toast.LENGTH_LONG).show();
                refreshCamera();

                Intent intent = new Intent(MainActivity.this,
                        ResultActivity.class);
                intent.putExtra("strParamName", str);
                startActivity(intent);
            }
        };
    }

    private boolean requestPermissionCamera() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        RESULT_PERMISSIONS);

            }else {
            }
        }else{  // version 6 이하일때
            return true;
        }

        return true;
    }


    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        camera = Camera.open();
        camera.stopPreview();
        Camera.Parameters param = camera.getParameters();
        param.setRotation(90);
        camera.setParameters(param);

        try {

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}