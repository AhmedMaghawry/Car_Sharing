package com.tarek.carsharing.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tarek.carsharing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;




public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;                        //Firebase authentication
    private EditText editTextEmail, editTextPassword; //Boxes' names



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setTitle("Login Page");

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.textViewSignup).setOnClickListener(this); // sign up  button change activity
        findViewById(R.id.buttonLogin).setOnClickListener(this);    // login in 1st activity

    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {   // not logged in
            finish();
            startActivity(new Intent(this, HomeActivity.class)); //go to Homepage
        }
    }

    private void userLogin() { // log in methods : email&password validation
        String email = editTextEmail.getText().toString().trim(); //email variable declaration
        String password = editTextPassword.getText().toString().trim(); //password variable declaration

        if (email.isEmpty()) { //check for empty email box
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus(); //redirect input to the email box
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {//check email pattern
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();//redirect input to the email box
            return;
        }

        if (password.isEmpty()) { //check for empty password box
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();//redirect input to the password box
            return;
        }

        if (password.length() < 6) { //check the length of the password, must be 6+ characters
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();//redirect input to the password box
            return;
        }

        //Utils.showLoading(this);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            // firebase check if right goes to next page --> home

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Utils.hideLoading();
                //Toast.makeText(LoginActivity.this, "Main A 11", Toast.LENGTH_SHORT).show();
                if (task.isSuccessful()) {
                    finish();


                    //**************************************       home
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class); //declare new intent
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // to clear the stack and avoid any interference between activities
                    finish();
                    startActivity(intent);//redirect to Homepage
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show(); //show a toast with the specific error
                }
            }
        });
    }
    // to check if already log in or not (save user infos in the app)
    @Override                        // will be called when choosing on click listener
    // will end activity1 and start activity 2
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup: //if the user clicks the Sign Up button
                startActivity(new Intent(this, SignupActivity.class));//redirect the user to MainActivity2 which is the sign up page
                break;

            case R.id.buttonLogin:  //if the user clicks the Login button
                userLogin();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(LoginActivity.this, "Main A 11 Destroy", Toast.LENGTH_SHORT).show();
        //Utils.hideLoading();
    }

    @Override  // the result of permissions  (allow/deny)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: { // Allow
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(LoginActivity.this, "GOOD", Toast.LENGTH_SHORT).show();
                    break;
                } else { // Deny
                    Toast.makeText(LoginActivity.this, "Not GOOD", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    onDestroy(); //  Loza: we can add page  to ask permissions
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}


