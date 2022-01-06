package com.roshan.questionary.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.roshan.questionary.MainActivity;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.signupBtn.setOnClickListener(v -> {
            checkEmptyStatus();
        });

        binding.signUpLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
            finish();
        });

    }



    //Check Empty Status ------------------------------>

    private void checkEmptyStatus(){
        if (binding.userNameSignup.getEditText().getText().toString().isEmpty()
                && binding.emailSignUp.getEditText().getText().toString().isEmpty()
                && binding.passwordSignUp.getEditText().getText().toString().isEmpty()){
            binding.passwordSignUp.getEditText().setError("Required!");
            binding.emailSignUp.getEditText().setError("Required!");
            binding.userNameSignup.getEditText().setError("Required!");
        }
        else if(binding.userNameSignup.getEditText().getText().toString().isEmpty()){
            binding.userNameSignup.getEditText().setError("Required!");
        }
        else if(binding.passwordSignUp.getEditText().getText().toString().isEmpty()){
            binding.passwordSignUp.getEditText().setError("Required!");
        }
        else if(binding.emailSignUp.getEditText().getText().toString().isEmpty()){
            binding.emailSignUp.getEditText().setError("Required!");
        }
        else {
            signup();
        }
    }



    //SignUp auth ------------------------------>

    private void signup(){

        binding.progressBar5.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(binding.emailSignUp.getEditText().getText().toString(),
                binding.passwordSignUp.getEditText().getText().toString())
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(SignUp.this, "Register Successfully", Toast.LENGTH_SHORT).show();

                    UserModel user = new UserModel(binding.userNameSignup.getEditText().getText().toString(),
                            binding.emailSignUp.getEditText().getText().toString(),
                            binding.passwordSignUp.getEditText().getText().toString());

                    database.getReference().child("Users").child(auth.getCurrentUser().getUid()).setValue(user);

                    binding.progressBar5.setVisibility(View.GONE);

                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar5.setVisibility(View.GONE);
                });
    }
}