package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivitySigninBinding;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity {
    //declare Biding because we have enabled viewBinding in gradle so it will be generated automatically
    private ActivitySigninBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
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
        binding.buttonSignin.setOnClickListener(v ->{
            if(isValid()){
                signIn();
            }
        });

    }
    private void signIn(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                    }
                    else {
                        loading(false);
                        showToast("Chiu thua roi ban e");
                    }
                });
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignin.setVisibility(View.INVISIBLE);
            binding.processBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.processBar.setVisibility(View.INVISIBLE);
            binding.buttonSignin.setVisibility(View.VISIBLE);
        }
    }
    private  void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private Boolean isValid(){
        if(binding.inputEmail.getText().toString().trim().isEmpty() || binding.inputPassword.getText().toString().trim().isEmpty() ){
            showToast("Enter Email or Password");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter Valid Email");
            return false;
        } else {
            return true;
        }
    }
}