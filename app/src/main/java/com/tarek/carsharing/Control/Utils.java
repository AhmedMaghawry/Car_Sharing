package com.tarek.carsharing.Control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.tarek.carsharing.View.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.tarek.carsharing.Control.Constants.CODE_SP;

public class Utils {

    public final static String TAG = "Utils";
    public static SweetAlertDialog pDialog;

    public static boolean isInternetConnected(Context ctx) {
        if (ctx != null) {
            ConnectivityManager connectivityMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityMgr != null) {
                NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void launchActivity(Context context, Class classToGo, Bundle bundle) {
        Intent intent = new Intent(context, classToGo);
        if (bundle != null)
            intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void showMessage(Context context, String title, String message) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(title);
        pDialog.setContentText(message);
        pDialog.setConfirmText("OK");
        pDialog.show();
    }

    public static void showError(Context context, String title, String message) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText(title);
        pDialog.setContentText(message);
        pDialog.setConfirmText("OK");
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
            }
        });
        pDialog.show();
    }

    public static void showLoading(Activity c) {
        hideLoading();
        pDialog = new SweetAlertDialog(c, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0EAE95"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void hideLoading() {
        if (pDialog != null && pDialog.isShowing()) {
            try {
                pDialog.dismiss();
                pDialog = null;
            } catch (Exception e) {
                pDialog = null;
                Log.e(TAG, "Utils:: hideProgressDialog: ", e);
            }
        }
    }

    public static void sendVerCode(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacl) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                callbacl);        // OnVerificationStateChangedCallbacks
    }

    public static void signInWithPhoneAuthCredential(final Activity context, final PhoneAuthCredential credential) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("mob", "signInWithCredential:success");
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();

                            //FirebaseUser user = task.getResult().getUser();

                            SharedValues.saveValue(context, CODE_SP, credential.getSmsCode());

                            Utils.launchActivity(context, HomeActivity.class, null);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("mob", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }

                            Utils.showError(context, "Invalid Code", "The Code Entered is Invalid");
                        }
                    }
                });
    }

}