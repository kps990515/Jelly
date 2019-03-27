package org.andriodtown.jelly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2019-03-22.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        finish();
    }
}
