package com.tarek.carsharing.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tarek.carsharing.Control.mina.CloudTextGraphic;
import com.tarek.carsharing.Control.mina.GraphicOverlay;
import com.tarek.carsharing.Model.User;
import com.tarek.carsharing.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.io.IOException;
import java.util.List;

public class SignupVerificationActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;

    private ImageView imageView;

    private Uri uriProfileImage;        //uniform resources identifier image storage

    private Button nextBtn;
    private GraphicOverlay mGraphicOverlay;
    private Bitmap mSelectedImage;
    private String sid, nameEng;
    private String[] date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup_verification);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        imageView = findViewById(R.id.imageView);
        nextBtn = findViewById(R.id.ButtonNext);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo : Change true
                if ((sid != null && nameEng != null && date != null) || true) {
                    Intent prev = getIntent();
                    String email = prev.getStringExtra("email");
                    int age = prev.getIntExtra("age", 0);
                    String name = prev.getStringExtra("name");
                    String phone = prev.getStringExtra("phone");
                    String image = prev.getStringExtra("image");
                    //TODO : change here
                    //String dateFinal = date[0] + "-" + date[1] + "-" + date[2];
                    //String nid = sid;
                    String dateFinal = "24-03-2024";
                    String nid = "45565656565656565";

                    User user = new User(name, email, phone, nid, dateFinal, age, image);
                    boolean b = user.addUser();
                    Intent intent;
                    if (b) {
                        intent = new Intent(SignupVerificationActivity.this, HomeActivity.class);
                        Toast.makeText(SignupVerificationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        intent = new Intent(SignupVerificationActivity.this, LoginActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SignupVerificationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupVerificationActivity.this, "Verifying failed. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // on click lilo :heart: , save and display
        imageView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                showImageChooser();           // method permit user to select image
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
            mImageMaxWidth = imageView.getWidth();
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
                    imageView.getHeight();
        }

        return mImageMaxHeight;
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
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                mGraphicOverlay.clear();

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
                runCloudTextRecognition();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runCloudTextRecognition() {

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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully

        if (text == null) {
            showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                Log.i("elec", paragraphs.get(0).getText());
                    if (i == 2)
                        sid = paragraphs.get(0).getText();
                    else if(i == 3)
                        nameEng = paragraphs.get(0).getText();
                    else if (i == 8)
                        date = arabicToDecimal(paragraphs.get(0).getText().replace('۶','٤').replaceAll("[\n]", "").split("/"));
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(mGraphicOverlay,
                            words.get(l));
                    mGraphicOverlay.add(cloudDocumentTextGraphic);

                }
            }
        }
        //Log.i("elec", sid + " " + nameEng + " " + date[0] + "/"+date[1]+"/"+date[2]);
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
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }
}