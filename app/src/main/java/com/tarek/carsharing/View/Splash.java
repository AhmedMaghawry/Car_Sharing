package com.tarek.carsharing.View;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.tarek.carsharing.R;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread()
        {

            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

  myThread.start();

    }

}
