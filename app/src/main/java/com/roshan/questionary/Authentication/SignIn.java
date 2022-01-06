package com.roshan.questionary.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.roshan.questionary.MainActivity;
import com.roshan.questionary.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        userLoginStatus();

        binding.loginBtn.setOnClickListener(v -> {
            checkEmptyStatus();
        });

        binding.signUpLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
        });
    }



    //Check Empty Status ------------------------------>

    private void checkEmptyStatus(){
        if (binding.emailSignIn.getEditText().getText().toString().isEmpty()
                && binding.passwordSignIn.getEditText().getText().toString().isEmpty()){
            binding.passwordSignIn.getEditText().setError("Required!");
            binding.emailSignIn.getEditText().setError("Required!");
        }
        else if(binding.emailSignIn.getEditText().getText().toString().isEmpty()){
            binding.emailSignIn.getEditText().setError("Required!");
        }
        else if(binding.passwordSignIn.getEditText().getText().toString().isEmpty()){
            binding.passwordSignIn.getEditText().setError("Required!");
        }
        else {
            login();
        }
    }



    //SignIn auth ------------------------------>

    private void login(){
        binding.progressBar4.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(binding.emailSignIn.getEditText().getText().toString(),
                binding.passwordSignIn.getEditText().getText().toString())
                .addOnSuccessListener(authResult -> {

                    binding.progressBar4.setVisibility(View.GONE);

                    Toast.makeText(SignIn.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar4.setVisibility(View.GONE);
                });
    }



    //Login user status ------------------------------>

    private void userLoginStatus(){
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}