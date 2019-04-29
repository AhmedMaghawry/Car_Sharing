package com.tarek.carsharing.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;

import com.tarek.carsharing.R;

public class ContactUsActivity extends AppCompatActivity {
    private Button bn;
    private  Button bn2;
    private  Button bn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        bn = (Button) findViewById(R.id.callbutton);
        bn2 = (Button) findViewById(R.id.fbbutton);
        bn3 = (Button) findViewById(R.id.emailbtn);
        bn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:01283557633"));
                startActivity(callIntent);
            }
        });
        bn2.setOnClickListener(new View.OnClickListener() {

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
        bn3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "go.around.carshare@gmail.com"});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });





    }
}
