package org.andriodtown.jelly;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class EditActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    private TextureView textureView;
    private MediaPlayer mediaPlayer;

    private ConstraintLayout layout_clip;
    private HorizontalScrollView scrollView_clip;
    private HorizontalScrollView scrollView_sticker;

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
    private ImageView sticker1;
    private ImageView sticker2;
    private ImageView sticker3;
    private ImageView sticker4;
    private ImageView sticker5;
    private ImageView sticker6;
    private ImageView sticker7;
    private ImageView clip1;
    private ImageView img_camera_sticker;

    private EditText txt_edit;

    private String videoFileName;

    private AssetFileDescriptor descriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit);

        mediaPlayer = new MediaPlayer();

        layout_clip = findViewById(R.id.layout_clip);
        scrollView_clip = findViewById(R.id.scrollView_clip);
        scrollView_sticker = findViewById(R.id.scrollView_sticker);
        bar_left = findViewById(R.id.bar_left);
        textureView = findViewById(R.id.textureView);
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


        Intent intent = getIntent();
        videoFileName = intent.getExtras().getString("path");

        textureView.setSurfaceTextureListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        try {
            mediaPlayer.setDataSource(videoFileName);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
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
