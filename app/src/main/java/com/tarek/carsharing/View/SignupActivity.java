package com.tarek.carsharing.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


public class SignupActivity extends AppCompatActivity  implements View.OnClickListener  {

    private EditText editTextEmail, editTextPassword; // boxex

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        editTextEmail = findViewById(R.id.editTextEmail); //sets an id for the email box
        editTextPassword = findViewById(R.id.editTextPassword); //sets an id for the password box

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonSignUp).setOnClickListener(this);//activate Sign Up button
    }

    // methods for user registration in case of registration button


    private void registerUser() {

        String email = editTextEmail.getText().toString().trim(); // get the email input
        String password = editTextPassword.getText().toString().trim(); // get the password input


        if (email.isEmpty()) { //check if email is empty
            editTextEmail.setError("Email is required"); //error message
            editTextEmail.requestFocus();//redirect input
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //check if email is matching the pattern
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus(); //redirect input
            return;
        }

        if (password.isEmpty()) { //check if password is empty
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus(); //redirect input
            return;
        }

        if (password.length() < 6) { //check if password length is 6+ characters
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus(); //redirect input
            return;
        }

        startRegisteration(email, password); //if all validation are satisfied call method
    }

    private void startRegisteration(final String email, final String password) { // method for creation of
        Utils.showLoading(this);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


// check with firebase authentication , if the email exists already or any problem
// when successful , goes to profile activity

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) { //check for successful registration
                    Utils.hideLoading();
                    Intent intent = new Intent(SignupActivity.this, SignupDataActivity.class); // intent of signupData
                    intent.putExtra("email", email); // save in app
                    intent.putExtra("password", password); // save in app
                    removeAuth(email, password);
                    FirebaseAuth.getInstance().signOut();
                    startActivity(intent); // direct for  signup continue
                    finish();
                } else { // didn t  save the email and  password
                    Utils.hideLoading();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void removeAuth(String emai, String pass) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(emai, pass);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("dodo", "User account deleted.");
                                        }
                                    }
                                });

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp: // signup then direct to the rest of the signup sequences
                registerUser();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(SignupActivity.this, "Main A 2 Destroy", Toast.LENGTH_SHORT).show();
        Utils.hideLoading();
    }
}



