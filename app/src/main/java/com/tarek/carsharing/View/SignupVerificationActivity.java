package com.tarek.carsharing.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tarek.carsharing.Control.SharedValues;
import com.tarek.carsharing.Control.Utils;
import com.tarek.carsharing.Control.mina.CloudTextGraphic;
import com.tarek.carsharing.Control.mina.GraphicOverlay;
import com.tarek.carsharing.Control.onAction;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.pepperonas.materialdialog.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.tarek.carsharing.Control.Utils.sendVerCode;

public class SignupVerificationActivity extends AppCompatActivity {

   // private static final int CHOOSE_IMAGE = 101;

    private ImageView lisImage,sidImage;



    private Button nextBtn;
 //   private GraphicOverlay mGraphicOverlay;
    private Bitmap mSelectedImage;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    private String nameUser, emailUser, phoneUser, nidUser, dateFinalUser, imageUser, passwordUser;
    private int ageUser;
private Boolean flag1=false,flag2=false;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST2 = 1999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup_verification);
        Intent prev = getIntent();
        emailUser = prev.getStringExtra("email");
        ageUser = prev.getIntExtra("age", 0);
        nameUser = prev.getStringExtra("name");
        phoneUser = prev.getStringExtra("phone");
        imageUser = prev.getStringExtra("image");
        passwordUser = prev.getStringExtra("password");
       // mGraphicOverlay = findViewById(R.id.graphic_overlay);
        lisImage = findViewById(R.id.image);
        sidImage=findViewById(R.id.image2);
        nextBtn = findViewById(R.id.next);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                String code = credential.getSmsCode();
                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                showCodeEnter();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w("mob", "onVerificationFailed", e);
                Utils.hideLoading();
                Toast.makeText(SignupVerificationActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //super.onCodeSent(verificationId, token);
                Toast.makeText(SignupVerificationActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                showCodeEnter();
                Utils.hideLoading();
            }
        };


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo : Change true
                if (flag1&&flag2) {

                    //TODO : change here
                    //String dateFinal = date[0] + "-" + date[1] + "-" + date[2];
                    //String nid = sid;
                    String dateFinal = "24-03-2024";
                    String nid = "45565656565656565";

                    sendVerCode("+20" + phoneUser, mCallbacks);
                } else {
                    Toast.makeText(SignupVerificationActivity.this, "Please add the two photos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // on click lilo :heart: , save and display
        lisImage.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                captureImage(CAMERA_REQUEST);         // method permit user to select image
            }
        });
        sidImage.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                captureImage(CAMERA_REQUEST2);         // method permit user to select image
            }
        });

    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(SignupVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity

                            Toast.makeText(SignupVerificationActivity.this, "Code Correct", Toast.LENGTH_SHORT).show();
                            goCodeCorrect(credential);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(SignupVerificationActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void captureImage(int CAMERA_REQUEST) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void goCodeCorrect(PhoneAuthCredential credential) {
        removeAuth(credential);
        FirebaseAuth.getInstance().signOut();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) { //check for successful registration
                    Utils.hideLoading();
                    User user = new User(nameUser, emailUser, phoneUser, nidUser, dateFinalUser, ageUser, imageUser,5,0,0);
                    boolean b = user.addUser();
                    if (b) {
                        sendVerificationEmail();
                        Toast.makeText(SignupVerificationActivity.this, "Registration Successful with uid : " + FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                        Utils.launchActivity(SignupVerificationActivity.this, HomeActivity.class, null);
                        finish();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SignupVerificationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        Utils.launchActivity(SignupVerificationActivity.this, LoginActivity.class, null);
                        finish();
                    }
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

        Utils.launchActivity(SignupVerificationActivity.this, LoginActivity.class, null);
    }

    private void removeAuth(PhoneAuthCredential credential) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

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

                                        }
                                    }
                                });

                    }
                });
    }

    // display the image after the user selected it

    Integer mImageMaxWidth;
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = lisImage.getWidth();
        }

        return mImageMaxWidth;
    }

    Integer mImageMaxHeight;
    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    lisImage.getHeight();
        }

        return mImageMaxHeight;
    }


    private void showCodeEnter() {
        new MaterialDialog.Builder(this)
                .customView(R.layout.view_code)
                .positiveText("Confirm")
                .negativeText("Cancel")
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText code = dialog.findViewById(R.id.code);
                        if (TextUtils.isEmpty(code.getText().toString())) {
                            Toast.makeText(SignupVerificationActivity.this, "Please Enter The Code", Toast.LENGTH_SHORT).show();
                        } else {
                            verifyVerificationCode(code.getText().toString());
                        }
                    }
                }).show();
    }

    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }
    /* */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check for the image selected
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
        flag1=true;
           handlecamera(data);



        }
        else if(requestCode == CAMERA_REQUEST2 && resultCode == Activity.RESULT_OK)
        {
            flag2=true;
            handlecamera2(data);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void handlecamera(Intent data){

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        uploadImageToFirebaseStorage("License",getImageUri(getApplicationContext(),bitmap));
        lisImage.setImageBitmap(bitmap);
        //   mGraphicOverlay.clear();

        mSelectedImage = bitmap;
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);
            mSelectedImage = resizedBitmap;
        }
    }
    private void handlecamera2(Intent data){


        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        uploadImageToFirebaseStorage("NID",getImageUri(getApplicationContext(),bitmap));
        sidImage.setImageBitmap(bitmap);
        //   mGraphicOverlay.clear();

        mSelectedImage = bitmap;
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);
            mSelectedImage = resizedBitmap;
        }
    }
    private void uploadImageToFirebaseStorage(String x,Uri uriProfileImage) {
        // storage

        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference(x+"/" + phoneUser + ".jpg"); // in database folder profilepics
        //System.currentTimeMillis(), is random sequence done  by getting time in millis
        if (uriProfileImage != null) { // upload

            Utils.showLoading(this);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        // check uplod successful or not
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Utils.hideLoading();
                            Toast.makeText(SignupVerificationActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                         //   profileImageUrl = taskSnapshot.getDownloadUrl().toString(); // get the url as user informations
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { // fail to opload
                            Utils.hideLoading();
                            Toast.makeText(SignupVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

  /*  private void runCloudTextRecognition() {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText texts) {

                                processCloudTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });

    }
*/
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
   /* private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully

        if (text == null) {
            showToast("No text found");
            return;
        }
     //   mGraphicOverlay.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                Log.i("elec", paragraphs.get(0).getText());
                if (i == 2)
                    sid = paragraphs.get(0).getText();
                else if(i == 1)
                    nameEng = paragraphs.get(0).getText();
                else if (i == 8)
                    date = arabicToDecimal(paragraphs.get(0).getText().replace('۶','٤').replaceAll("[\n]", "").split("/"));
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
  *//*              for (int l = 0; l < words.size(); l++) {
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(mGraphicOverlay,
                            words.get(l));
                    mGraphicOverlay.add(cloudDocumentTextGraphic);

                }*//*
            }
        }
        if (checkName(nameEng) && checkSID(sid) && checkDate(date[0]+"/"+date[1]+"/"+date[2])){
            sidtv.setText(sid);
            nametv.setText(nameEng);
            datetv.setText(date[0]+"/"+date[1]+"/"+date[2]);
            nextBtn.setVisibility(View.VISIBLE);
        } else {
            nextBtn.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Capture Error Please Recapture", Toast.LENGTH_SHORT).show();
        }

        //Log.i("elec", sid + " " + nameEng + " " + date[0] + "/"+date[1]+"/"+date[2]);
    }*/
    private boolean checkDate(String x) {

        if(x==null)
            return false;
        if (x.isEmpty())
            return false;

        String[] strings = x.split("/");

        if (strings.length != 3)
            return false;

        for (String w : strings)
            for (char c : w.toCharArray())
                if (!Character.isDigit(c))
                    return false;

        return true;
    }

    private boolean checkSID(String x) {

        Log.i("Mo1111","enter id");
        Log.i("Mo1111",x);
        if(x==null)
            return false;
        if (x.isEmpty())
            return false;

        String[] strings = x.split(" ");

        if (strings.length != 1)
            return false;

        if (x.length() != 14)
            return false;

        for (char c : x.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }

        return true;
    }

    private boolean checkName(String x) {
        Log.i("Mo1111","enter name");
        Log.i("Mo1111",x);
        if(x==null)
            return false;
        if (x.isEmpty())
            return false;

        String[] strings = x.split(" ");

        if (strings.length < 1)
            return false;

/*        for (String f : strings) {
            if (!isStringOnlyAlphabet(f)) {
                Log.i("Mo1111",f);
                return false;
            }
        }*/

        return true;
    }

    private boolean isStringOnlyAlphabet(String str)
    {
        if (str == null || str.equals("")) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ((!(ch >= 'A' && ch <= 'Z'))
                    && (!(ch >= 'a' && ch <= 'z'))) {
                return false;
            }
        }
        return true;
    }


    private String[] arabicToDecimal(String[] numbers) {
        String[] res = new String[numbers.length];
        for (int j = 0; j < numbers.length; j++) {
            char[] chars = new char[numbers[j].length()];
            for (int i = 0; i < numbers[j].length(); i++) {
                char ch = numbers[j].charAt(i);
                if (ch >= 0x0660 && ch <= 0x0669)
                    ch -= 0x0660 - '0';
                else if (ch >= 0x06f0 && ch <= 0x06F9)
                    ch -= 0x06f0 - '0';
                chars[i] = ch;
            }
            res[j] = new String(chars);
        }

        return res;
    }


    //  selects image of the user

    /* */
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // get the image
      //  startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignupVerificationActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }



}