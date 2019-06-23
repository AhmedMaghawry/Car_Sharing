package com.tarek.carsharing.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tarek.carsharing.R;

public class Promocode extends AppCompatActivity {


  private   TextView enterPromo ;
//    Button addPromo = findViewById(R.id.addPromo);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocode);
        enterPromo = findViewById(R.id.enterpromo);

    }
}



