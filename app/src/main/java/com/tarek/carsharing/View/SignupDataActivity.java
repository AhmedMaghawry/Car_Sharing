package com.tarek.carsharing.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tarek.carsharing.R;

public class SignupDataActivity extends AppCompatActivity {


    private EditText firstName, lastName, age, phoneNumber;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_data);

        firstName = findViewById(R.id.uFirstName);
        lastName = findViewById(R.id.uLastName);
        age = findViewById(R.id.uAge);
        phoneNumber = findViewById(R.id.phoneNumber);
        next = findViewById(R.id.ButtonNext);

        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext(email);
            }
        });
    }

    private void goNext(String email){

        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String ageNum = age.getText().toString().trim();
        String number = phoneNumber.getText().toString().trim();

        if( (!TextUtils.isEmpty(firstname)) && (!TextUtils.isEmpty(lastname)) && (!TextUtils.isEmpty(ageNum)) && (!TextUtils.isEmpty(number))){

            Intent intent = new Intent(SignupDataActivity.this, SignupImageActivity.class);
            intent.putExtra("name", firstname + " " + lastname);
            intent.putExtra("age", Integer.parseInt(ageNum.trim()));
            intent.putExtra("phone", number);
            intent.putExtra("email", email);

            startActivity(intent);
            finish();

        }
        else{
            Toast.makeText(this,"please Fill all the information needed",Toast.LENGTH_LONG).show();
        }

    }

}