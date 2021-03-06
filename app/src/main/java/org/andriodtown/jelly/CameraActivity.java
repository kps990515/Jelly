package org.andriodtown.jelly;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor descriptor;
    private MediaRecorder mediaRecorder;
    private android.hardware.Camera camera = null;
    private CameraDevice cameraDevice;
    private String mCameraId;
    private Size previewSize;
    private Size videoSize;
    private Size imageSize;
    private ImageReader imageReader;

    private int totalRotation;
    private CameraCaptureSession previewCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;

    private ConstraintLayout layout_clip;
    private HorizontalScrollView scrollView_clip;
    private HorizontalScrollView scrollView_sticker;

    private Chronometer chrono;

    private ImageView btn_record;
    private ImageView bar_left;
    private ImageView btn_text;
    private ImageView btn_enm;
    private ImageView btn_music;
    private ImageView btn_voice;
    private ImageView btn_picture;
    private ImageView btn_a;
    private ImageView btn_pallete;
    private ImageView btn_clip;
    private ImageView btn_sticker;
    private ImageView img_guide;
    private ImageView img_guide_text;
    private ImageView sticker1;
    private ImageView sticker2;
    private ImageView sticker3;
    private ImageView sticker4;
    private ImageView sticker5;
    private ImageView sticker6;
    private ImageView sticker7;
    private ImageView clip1;
    private ImageView img_camera_sticker;
    private ImageView img_rec;

    private EditText txt_edit;

    private boolean isRecording = false;

    private File videoFolder;
    private String videoFileName;

    private int clip=0;

    private static SparseIntArray ORIENTATION = new SparseIntArray();

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {

        }
    };

    private CameraCaptureSession.CaptureCallback previewCaputureCallback = new CameraCaptureSession.CaptureCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void process(CaptureResult captureResult){
            switch (captureState){
                case STATE_PREVIEW:
                    break;
                case STATE_WAIT_LOCK:
                    captureState =STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if(afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED){
                        Toast.makeText(getApplicationContext(), "AF Locked!", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }
    };


    static {
        ORIENTATION.append(Surface.ROTATION_0, 0);
        ORIENTATION.append(Surface.ROTATION_90, 90);
        ORIENTATION.append(Surface.ROTATION_180, 180);
        ORIENTATION.append(Surface.ROTATION_270, 270);
    }

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_RESULT = 2;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int captureState = STATE_PREVIEW;

    private static class CompareSizeByArea implements Comparator<Size> {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(Size o1, Size o2) {
            return Long.signum((long) o1.getWidth() * o1.getHeight() -
                    (long) o2.getWidth() * o2.getHeight());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        createVideoFolder();

        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

        chrono = findViewById(R.id.chrono);
        layout_clip = findViewById(R.id.layout_clip);
        scrollView_clip = findViewById(R.id.scrollView_clip);
        scrollView_sticker = findViewById(R.id.scrollView_sticker);
        img_guide = findViewById(R.id.img_guide);
        img_guide_text = findViewById(R.id.img_guide_text);
        bar_left = findViewById(R.id.bar_left);
        textureView = findViewById(R.id.textureView);
        btn_record = findViewById(R.id.btn_record);
        btn_text = findViewById(R.id.btn_text);
        btn_enm = findViewById(R.id.btn_enm);
        btn_music = findViewById(R.id.btn_music);
        btn_voice = findViewById(R.id.btn_voice);
        btn_picture = findViewById(R.id.btn_picture);
        btn_a = findViewById(R.id.btn_a);
        btn_pallete = findViewById(R.id.btn_pallete);
        txt_edit = findViewById(R.id.txt_edit);
        btn_clip = findViewById(R.id.btn_clip);
        btn_sticker = findViewById(R.id.btn_sticker);
        img_camera_sticker = findViewById(R.id.img_camera_sticker);
        img_rec = findViewById(R.id.img_rec);

        sticker1 = findViewById(R.id.sticker1);
        sticker2 = findViewById(R.id.sticker2);
        sticker3 = findViewById(R.id.sticker3);
        sticker4 = findViewById(R.id.sticker4);
        sticker5 = findViewById(R.id.sticker5);
        sticker6 = findViewById(R.id.sticker6);
        sticker7 = findViewById(R.id.sticker7);
        clip1=findViewById(R.id.clip1);
        clip1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        Glide.with(getApplicationContext()).asGif().load(R.raw.img_clip1).into(clip1);
                        return true;
                    case MotionEvent.ACTION_UP :
                        clip1.setImageResource(R.mipmap.img_clip1);
                        return false;
                }
                return false;
            }
        });
        Glide.with(this).asGif().load(R.raw.img_sticker1).into(sticker1);
        Glide.with(this).asGif().load(R.raw.img_sticker2).into(sticker2);
        Glide.with(this).asGif().load(R.raw.img_sticker3).into(sticker3);
        Glide.with(this).asGif().load(R.raw.img_sticker4).into(sticker4);
        Glide.with(this).asGif().load(R.raw.img_sticker5).into(sticker5);
        Glide.with(this).asGif().load(R.raw.img_sticker6).into(sticker6);
        Glide.with(this).asGif().load(R.raw.img_sticker7).into(sticker7);

        img_guide.setVisibility(VISIBLE);
        img_guide_text.setVisibility(VISIBLE);

        btn_record.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(isRecording){

                    chrono.stop();
                    chrono.setVisibility(GONE);
                    img_rec.setVisibility(GONE);

                    isRecording = false;
                    btn_record.setImageResource(R.mipmap.img_record_button);
                    showLeftBar();
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    startPreview();

                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra("path", videoFileName);
                    startActivity(intent);

                }else{
                    isRecording = true;
                    btn_record.setImageResource(R.mipmap.img_recording_button);
                    hideLeftBar();
                    checkWriteStoragePermission();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        setupCamera(width, height);
        connectCamera();
        transformImage(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        //if(mediaPlayer!=null && mediaPlayer.isPlaying()){
        //    mediaPlayer.pause();
        //}
        super.onPause();
        closeCamera();
        stopBackgroundThread();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        /*if(mediaPlayer!=null){
            mediaPlayer.start();
        }*/
        super.onResume();

        startBackgroundThread();

        if (textureView.isAvailable()) {
            setupCamera(textureView.getWidth(), textureView.getHeight());
            connectCamera();
            transformImage(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            if(isRecording){
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mediaRecorder.start();

                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.setVisibility(VISIBLE);
                img_rec.setVisibility(VISIBLE);
                chrono.start();
            }else{
                startPreview();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = totalRotation == 90 || totalRotation == 270;
                int rotatedWidth = width;
                int rotateHeight = height;
                if (swapRotation) {
                    rotatedWidth = height;
                    rotateHeight = width;
                }
                previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotateHeight);
                videoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotateHeight);
                imageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth, rotateHeight);
                imageReader = ImageReader.newInstance(imageSize.getWidth(), imageSize.getHeight(), ImageFormat.JPEG,1);
                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraId, cameraDeviceCallback, backgroundHandler);
                } else {
                    if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        Toast.makeText(this,
                                "Video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[] {android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    }, REQUEST_CAMERA_PERMISSION_RESULT);
                }

            } else {
                cameraManager.openCamera(mCameraId, cameraDeviceCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecord(){
        try {
            setupMediaRecorder();
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mediaRecorder.getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureRequestBuilder.addTarget(previewSurface);
            captureRequestBuilder.addTarget(recordSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPreview(){
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
// 4032 3024 504 378
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            previewCaptureSession = session;
                            try {
                                previewCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "Unable to setup camera preview", Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCamera(){
        if(cameraDevice!=null){
            cameraDevice.close();
            cameraDevice=null;
        }
    }


    private void startBackgroundThread(){
        backgroundHandlerThread = new HandlerThread("Camera");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopBackgroundThread(){
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation){
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATION.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Size chooseOptimalSize(Size[] chocies, int width, int height){
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : chocies){
            if(option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height){
                bigEnough.add(option);
            }
        }
        if(bigEnough.size() > 0){
            return Collections.min(bigEnough, new CompareSizeByArea());
        }else{
            return chocies[0];
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CAMERA_PERMISSION_RESULT){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "권한필요", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isRecording = true;
                btn_record.setImageResource(R.mipmap.img_recording_button);
                hideLeftBar();
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "권한필요", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createVideoFolder(){
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        videoFolder = new File(movieFile, "camera2VideoImage");
        if(!videoFolder.exists()){
            videoFolder.mkdirs();
        }
    }

    private File createVideoFileName() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_" + timeStamp + "_";
        File videoFile = File.createTempFile(prepend, ".mp4", videoFolder);
        videoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkWriteStoragePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
                isRecording = true;
                btn_record.setImageResource(R.mipmap.img_recording_button);
                hideLeftBar();
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mediaRecorder.start();

                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.setVisibility(VISIBLE);
                img_rec.setVisibility(VISIBLE);
                chrono.start();
            }else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "권한필요", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        }else{
            isRecording = true;
            btn_record.setImageResource(R.mipmap.img_recording_button);
            hideLeftBar();
            try {
                createVideoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startRecord();
            mediaRecorder.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupMediaRecorder() throws IOException{
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(videoFileName);
        mediaRecorder.setVideoEncodingBitRate(5000000);
        mediaRecorder.setVideoFrameRate(300);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setOrientationHint(totalRotation-180);
        mediaRecorder.prepare();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void lockFocus(){
        captureState = STATE_WAIT_LOCK;
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            previewCaptureSession.capture(captureRequestBuilder.build(), previewCaputureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void transformImage(int width, int height) {
        if(previewSize == null || textureView == null) {
            return;
        }
        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();

        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float)width / previewSize.getWidth(),
                    (float)height / previewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    public void showLeftBar(){
        bar_left.setVisibility(VISIBLE);
        btn_text.setVisibility(VISIBLE);
        btn_enm.setVisibility(VISIBLE);
        btn_music.setVisibility(VISIBLE);
        btn_voice.setVisibility(VISIBLE);
        btn_picture.setVisibility(VISIBLE);
    }


    public void hideLeftBar(){
        bar_left.setVisibility(GONE);
        btn_text.setVisibility(GONE);
        btn_enm.setVisibility(GONE);
        btn_music.setVisibility(GONE);
        btn_voice.setVisibility(GONE);
        btn_picture.setVisibility(GONE);
        img_guide.setVisibility(GONE);
        img_guide_text.setVisibility(GONE);
    }

    public void text(View v){
        if(btn_a.getVisibility() == GONE){
            txt_edit.setVisibility(VISIBLE);
            btn_a.setVisibility(VISIBLE);
            btn_pallete.setVisibility(VISIBLE);
        }else{
            if(txt_edit.getText().toString().length()==0){
                txt_edit.setVisibility(GONE);
                btn_a.setVisibility(GONE);
                btn_pallete.setVisibility(GONE);
            }else{
                btn_a.setVisibility(GONE);
                btn_pallete.setVisibility(GONE);
            }

        }

    }

    public void enm(View v){
        if(layout_clip.getVisibility() == GONE){
            layout_clip.setVisibility(VISIBLE);
        }else{
            layout_clip.setVisibility(GONE);
        }
    }

    public void clip(View v){
        btn_clip.setImageResource(R.mipmap.img_clip_white);
        btn_sticker.setImageResource(R.mipmap.img_sticker_grey);
        scrollView_clip.setVisibility(VISIBLE);
        scrollView_sticker.setVisibility(GONE);
    }

    public void sticker(View v){
        btn_clip.setImageResource(R.mipmap.img_clip_grey);
        btn_sticker.setImageResource(R.mipmap.img_sticker_white);
        scrollView_clip.setVisibility(GONE);
        scrollView_sticker.setVisibility(VISIBLE);
    }

    public void duckson(View v){
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker1).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }
    public void sregi(View v){
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker2).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }

    public void swings(View v){
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker3).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }

    public void yeongja(View v){
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker4).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }

    public void sinmyo(View v){
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker5).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }

    public void heart(View v){
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker6).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }

    public void banana(View v) {
        img_camera_sticker.setVisibility(VISIBLE);
        Glide.with(this).asGif().load(R.raw.img_sticker7).into(img_camera_sticker);
        layout_clip.setVisibility(GONE);
    }
}
