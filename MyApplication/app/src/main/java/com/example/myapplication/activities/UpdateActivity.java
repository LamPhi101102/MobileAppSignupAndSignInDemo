package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.ActivitySigninBinding;
import com.example.myapplication.databinding.ActivityUpdateBinding;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity {
    private ActivityUpdateBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        // call binding
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    private void setListeners(){
        binding.buttonChange.setOnClickListener(v ->{
            if(isValid()){
                String id = preferenceManager.getString(Constants.KEY_USER_ID, "");
                changePass(id);
            }
        });

    }
    private void changePass(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(id);
        userRef.update("password",binding.inputPassword.getText().toString()).addOnSuccessListener(res ->{
            showToast("Update Success");
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    private boolean isValid(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        String password = preferenceManager.getString(Constants.KEY_PASSWORD, "");
        if(!binding.inputOldPassword.getText().toString().equals(password)){
            showToast("Wrong pass");
            return false;
        }
        else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            showToast("New passwords are not same ");
            return false;
        }
        return true;
    }
    private  void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}