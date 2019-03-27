package org.andriodtown.jelly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView btn_jelly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_jelly = findViewById(R.id.btn_jelly);
    }

    public void jelly(View v) {
        Intent intent = new Intent(this, JellyActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_from_botton,R.anim.stay);
    }

}

