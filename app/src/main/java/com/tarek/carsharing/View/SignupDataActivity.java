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


    private EditText firstName, lastName, age, phoneNumber; //boxes to enter data
    private Button next;        // button to go to next page

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
        final String email = intent.getStringExtra("email"); // get previous data of user "email"
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { goNext(email) ; // when clicking next , send email to next function
            }
        });
    }

    private void goNext(String email){ // get editText data and save to intent

        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String ageNum = age.getText().toString().trim();
        String number = phoneNumber.getText().toString().trim();

        if( (TextUtils.isEmpty(firstname)) || (TextUtils.isEmpty(lastname)) || (TextUtils.isEmpty(ageNum)) || (TextUtils.isEmpty(number))){
            Toast.makeText(this,"please Fill all the information needed",Toast.LENGTH_LONG).show();
            // check if all boxex are empty
        }

        else{  // boxes are filled
            if(Integer.parseInt(ageNum)>=70 ||Integer.parseInt(ageNum)<=18 || (Integer.parseInt(ageNum)/10)==0 ) //check for age validation
            {
                Toast.makeText(this,"please enter a valid age",Toast.LENGTH_LONG).show();
                age.requestFocus();
            }

            else { //check for number validation
                if(number.length()!=11)
                {
                    Toast.makeText(this,"please enter a valid phone number",Toast.LENGTH_LONG).show();
                    phoneNumber.requestFocus();
                }
                else{ // all validation are  satisfied  it saves the data in intent
                    Intent intent = new Intent(SignupDataActivity.this, SignupImageActivity.class);
                    intent.putExtra("name", firstname + " " + lastname);
                    intent.putExtra("age", Integer.parseInt(ageNum.trim()));
                    intent.putExtra("phone", number);
                    intent.putExtra("email", email);

                    startActivity(intent);
                    finish();
                }
            }

        }

    }

}