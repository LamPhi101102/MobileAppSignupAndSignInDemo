package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        // Initialize the PreferenceManager
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Retrieve the stored name from PreferenceManager
        String name = preferenceManager.getString(Constants.KEY_NAME, "");
        String email = preferenceManager.getString(Constants.KEY_EMAIL, "");
        String image = preferenceManager.getString(Constants.KEY_IMAGE, "");

        // Set the retrieved name as the text for the TextView
        binding.welcome.setText("Welcome back "+name);
        binding.textName.setText(name);
        binding.textEmail.setText(email);
        binding.imageProfile.setImageBitmap(getUserImage(image));

    }
    private void setListeners(){
        binding.buttonUpdate.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UpdateActivity.class)));
    }
    private Bitmap getUserImage(String image){
        byte[] bytes = Base64.decode(image,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}