package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {
    //declare Biding because we have enabled viewBinding in gradle so it will be generated automatically
    private ActivitySigninBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // call binding
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    private void setListeners(){
        // Here we can see binding contain all direct references to all views that have an ID in the layout
        binding.textCreateNewAccount.setOnClickListener(v ->
                // Here we use setOnClickListeners (it just an event to perform the next action)
                // And now we use it when we lick to the text "Create New Account" it will direct to SignUpActitvity view
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
    }
}