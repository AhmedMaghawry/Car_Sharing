package com.tarek.carsharing.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import android.widget.LinearLayout;

import com.tarek.carsharing.R;

public class ContactUsActivity extends AppCompatActivity {
    private Button bn;
    private  Button bn2;
    private  Button bn3;
    private LinearLayout l1;
    private LinearLayout l2;
    private LinearLayout l3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        l1=(LinearLayout) findViewById(R.id.l1);
        l2=(LinearLayout) findViewById(R.id.l2);
        l3=(LinearLayout) findViewById(R.id.l3);
        bn = (Button) findViewById(R.id.callbutton);
        bn2 = (Button) findViewById(R.id.fbbutton);
        bn3 = (Button) findViewById(R.id.emailbtn);
        l1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:01283557633"));
                startActivity(callIntent);
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/337592936901040"));
                    startActivity(intent);


                } catch(Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Go-Around-337592936901040")));
                    return;
                }
            }
        });
        l3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "go.around.carshare@gmail.com"});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });





    }
}
