package com.example.canpstone_sequence;

import static android.view.View.resolveSize;
import static com.example.canpstone_sequence.ETRIActivity.result;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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


public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private boolean record = false;
    private boolean record_end = false;
    private String byte_img_Stream;
    private long prev_millis_capture = 0;
    private long prev_millis_post = 0;
    private boolean first = true;
    private Camera.Size previewSize;

    private Camera camera;
    public List<Camera.Size> listPreviewSizes;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button btn_record;
    TextView tx_target;
    TextView tx_response;
    String str;

    private int RESULT_PERMISSIONS = 100;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        requestPermissionCamera();

        tx_target = (TextView) findViewById(R.id.tx_target);
        tx_target.setText("TARGET : "+result);
        tx_response = findViewById(R.id.tx_response);

        btn_record = (Button) findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "btn_record CLICK");

                if (record) {
                    Log.d("TAG", "Sending Stop");
                    record = false;
                    record_end = true;
                    btn_record.setText("PUSH TO START");
                    tx_response.setText("Sending Finish");
                }
                else {
                    refreshCamera();
                    Log.d("TAG", "Sending Start");
                    btn_record.setText("PUSH TO STOP");
                    record = true;
                }

                new Thread(new Runnable() {
                    public void run() {

                        while(record){
                            //Log.d("TAG", "Base64 Encoding : " + byte_img_Stream);

                            long now_millis_post = System.currentTimeMillis();
                            if(record && (now_millis_post - prev_millis_post > 2000)){
                                prev_millis_post = now_millis_post;
                                post_http();
                                Log.d("TAG", "post_http");

                            }
                        }
                    }
                }).start();
            }
        });

        getWindow().setFormat(PixelFormat.UNKNOWN);


        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

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
                        TextView responseText = findViewById(R.id.tx_response);
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
                        TextView responseText = findViewById(R.id.tx_response);
                        try {
                            String _returns = response.body().string();
                            JSONObject Jobject = new JSONObject(_returns);
                            JSONArray Box = Jobject.getJSONArray("box");
                            JSONArray Max_name = Jobject.getJSONArray("max_name");
                            JSONArray Label = Jobject.getJSONArray("labels");

                            Log.d("TAG", "Response Box : " + Box);
                            Log.d("TAG", "Response Max_name : " + Max_name);
                            Log.d("TAG", "Response Label : " + Label);

                            if (record) {
                                responseText.setText("Server Connect Success\n"+Max_name+"\n"+Label);
                            }
                            else {
                                responseText.setText("Server Sending Finish");
                            }

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("TAG", "PostRequeset : Upload Success");

                    }
                });
            }
        });
    }



    private boolean requestPermissionCamera() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(CameraActivity.this,
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
        Log.d("TAG", "refreshCamera");
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
        Log.d("TAG", "onDestroy");
        super.onDestroy();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("TAG", "SurfaceCreated");

        camera = Camera.open();
        camera.stopPreview();
        Camera.Parameters param = camera.getParameters();
        param.setRotation(90);
        camera.setParameters(param);
        listPreviewSizes = camera.getParameters().getSupportedPreviewSizes();

        // 카메라의 회전이 가로/세로일때 화면을 설정한다.
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            param.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            param.setRotation(90);
        } else {
            param.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            param.setRotation(0);
        }

        try {

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            // 자동포커스 설정
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {

                    }
                }
            });
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //refreshCamera();
        Log.d("TAG", "SurfaceChanged");

        camera.setPreviewCallback(new Camera.PreviewCallback(){
            @Override
            public void onPreviewFrame(byte[] data, Camera camera){

                if(first){ //처음 한 번만 실행
                    first=false;

                    //현재 SurfaceView를 캡쳐
                    Camera.Parameters parameters = camera.getParameters();
                    int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;
                    int format = parameters.getPreviewFormat();
                    YuvImage image = new YuvImage(data, format, w, h, null);

                    /// Base64 Image Encoding ////////////////////////////////////////////////////////////
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Rect area = new Rect(0, 0, w, h);
                    image.compressToJpeg(area, 100, out);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Log.d("TAG", "onActivityResult : Image Ready");

                    byte[] currentData = out.toByteArray();
                    byte_img_Stream = Base64.encodeToString(currentData, 0);
                }

                long now_millis_capture = System.currentTimeMillis();
                if(record && (now_millis_capture - prev_millis_capture > 2000)){

                    prev_millis_capture = now_millis_capture;

                    //현재 SurfaceView를 캡쳐
                    Camera.Parameters parameters = camera.getParameters();
                    int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;
                    int format = parameters.getPreviewFormat();
                    YuvImage image = new YuvImage(data, format, w, h, null);

                    /// Base64 Image Encoding ////////////////////////////////////////////////////////////
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Rect area = new Rect(0, 0, w, h);
                    image.compressToJpeg(area, 100, out);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Log.d("TAG", "surfaceChanged : Image Ready");

                    byte[] currentData = out.toByteArray();
                    byte_img_Stream = Base64.encodeToString(currentData, 0);
                }
                else if(record_end){
                    record_end = false;
                    Log.d("TAG", "ReleaseCamera");
                    releaseCamera();
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("TAG", "SurfaceDestroyed");
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    // release Camera for other applications
    private void releaseCamera() {
        // check if Camera instance exists
        if (camera != null) {
            // first stop preview
            camera.stopPreview();
            // then cancel its preview callback
            camera.setPreviewCallback(null);
        }
    }

}