package com.example.realtime_sequence;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    static String byte_img_Stream;
    static long prev_millis = 0;
    static boolean first = true;

    private Camera mCamera;
    public List<Camera.Size> listPreviewSizes;
    private Camera.Size previewSize;
    private Context context;


    // SurfaceView 생성자
    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mCamera = MainActivity.getCamera();
        if(mCamera == null){
            mCamera = Camera.open();
        }
        listPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

    }

    //  SurfaceView 생성시 호출
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            // 카메라 객체를 사용할 수 있게 연결한다.
            if(mCamera  == null){
                mCamera  = Camera.open();
            }

            // 카메라 설정
            Camera.Parameters parameters = mCamera .getParameters();

            // 카메라의 회전이 가로/세로일때 화면을 설정한다.
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }

            mCamera.setParameters(parameters);

            mCamera.setPreviewDisplay(surfaceHolder);

            // 카메라 미리보기를 시작한다.
            mCamera.startPreview();

            // 자동포커스 설정
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {

                    }
                }
            });
        } catch (IOException e) {
        }
    }

    // SurfaceView 의 크기가 바뀌면 호출
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {

        // 카메라 화면을 회전 할 때의 처리
        if (surfaceHolder.getSurface() == null){
            // 프리뷰가 존재하지 않을때
            return;
        }
        // 프리뷰를 다시 설정한다.
        try {
            mCamera.stopPreview();

            Camera.Parameters parameters = mCamera.getParameters();

            // 화면 회전시 사진 회전 속성을 맞추기 위해 설정한다.
            int rotation = MainActivity.getInstance.getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_0) {
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            }else if(rotation == Surface.ROTATION_90){
                mCamera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }else if(rotation == Surface.ROTATION_180){
                mCamera.setDisplayOrientation(270);
                parameters.setRotation(270);
            }else{
                mCamera.setDisplayOrientation(180);
                parameters.setRotation(180);
            }

            // 변경된 화면 넓이를 설정한다.
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            mCamera.setParameters(parameters);

            // 새로 변경된 설정으로 프리뷰를 시작한다
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

            mCamera.setPreviewCallback(new Camera.PreviewCallback(){
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

                    long now_millis = System.currentTimeMillis();
                    if(MainActivity.record && (now_millis - prev_millis > 2000)){

                        prev_millis = now_millis;

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

                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // SurfaceView가 종료시 호출
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            // 카메라 미리보기를 종료한다.
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    // 화면이 회전할 때 화면 사이즈를 구한다.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (listPreviewSizes != null) {
            previewSize = getPreviewSize(listPreviewSizes, width, height);
        }
    }
    public Camera.Size getPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}