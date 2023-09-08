package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

        // Initialize the PreferenceManager
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Retrieve the stored name from PreferenceManager
        String name = preferenceManager.getString(Constants.KEY_NAME, "");

        // Set the retrieved name as the text for the TextView
        binding.textView.setText(name);
    }
}