package com.example.myapplication.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivitySignUpBinding;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    // this Function is used to execute an activity to choose image
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            // here I used it to execute an action and the return result
            new ActivityResultContracts.StartActivityForResult(),
            // it it Lambda ()
            result -> {
                //check if success or not (RESULT_OK is used to point that this action run without error)
                if(result.getResultCode() == RESULT_OK){
                    // check value
                    if(result.getData() != null){
                        // get date of image choosed
                        Uri imageUri = result.getData().getData();
                        try {
                            // Input is Image from URL
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            // use it to convert Uri from string to bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            // diplay image choosed
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            // encrypt this image into string base64
                            encodedImage = encodedImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void setListeners(){
        binding.textSignIn.setOnClickListener(v ->
                // it is an event that return the previous layout
                // For example when we stay at Sign in view, then we click on Create new Account, we will move to sign up view,
                // then we press Sign in text, we will return sign in view (on other words, we will return the previous action)
                onBackPressed());
        binding.buttonSignUp.setOnClickListener(v ->{
            if(isValidSignUpDetails()){
                signUp();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            // Intent is used to perform activities between applications or components
            // Intent.ACTION_PICK chooses image from albums
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // ensure we read the true uri
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // choose image from the albums of mobile
                pickImage.launch(intent);
        });
    }
    private void showToast(String message){
        // Notify short time for user
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // it is created to store user including, key (constants) - value (name, gmail...)
        //  Collection Framework to store under key and value
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    // Function is used to receive image and then convert it to string base 64
    private String encodedImage(Bitmap bitmap){
        // Bitmap lớp thể hiện một bức ảnh
        // First we determine the size
        // we fixed width
        int previewWidth = 150;
        // Calculate the height to the image not changed too much (biến dạng nhiều)
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        // then we can review the image with these size but in small size then it is easy to store and transfer
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        //ByteArrayOutputStream, here I used it to store image after compressing
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // using preview image to compress with the quality 50 and finally it will store in byteArrayOutputStream
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        // then transfer to byte arrays
        byte[] bytes = byteArrayOutputStream.toByteArray();
        // use Base64 encrypt bytes array to string based64
        return Base64.encodeToString(bytes, Base64.DEFAULT);
        // the result we will be used to transfer or store under the string
    }
    // check valid
    private Boolean isValidSignUpDetails(){
        if(encodedImage == null) {
            showToast("Please Select Profile Image");
            return false;
        }
        else if(binding.inputName.getText().toString().trim().isEmpty()){
            //trim() delete space head and back
            showToast("Please Enter Name");
            return false;
        }else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Please Enter Email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid Email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Please Enter Password");
            return false;
        }else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Please Enter Confirm Password");
            return false;
        }else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            showToast("Password is not match Confirm Password");
            return false;
        }else{
            return true;
        }
    }
    private void loading (Boolean isLoading){
        if(isLoading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}