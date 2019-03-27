package org.andriodtown.jelly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class JellyActivity extends AppCompatActivity {

    ArrayList<String> data = new ArrayList<>();
    private ImageView btn_homeBack;
    private ImageView btn_my;
    private RecyclerView list_mainHorizon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jelly);

        initView();
        initRecycler();
    }

    public void initView(){
        btn_homeBack = findViewById(R.id.btn_homeBack);
        btn_my = findViewById(R.id.btn_my);
        list_mainHorizon = findViewById(R.id.list_mainHorizon);
    }

    public void initRecycler(){
        MainHorizonAdapter mainHorizonadapter = new MainHorizonAdapter(this, data);
        list_mainHorizon.setAdapter(mainHorizonadapter);
        list_mainHorizon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    public void home_back(View v){
        finish();
        overridePendingTransition(R.anim.stay,R.anim.anim_slide_from_top);
    }


    public void record(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
