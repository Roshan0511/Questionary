package com.roshan.questionnaire.Authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.roshan.questionnaire.Activities.MainActivity;
import com.roshan.questionnaire.Models.UserModel;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    FragmentSignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.signupBtn.setOnClickListener(v -> {
            checkEmptyStatus();
        });

        binding.signUpLogIn.setOnClickListener(v -> {
            addFragment(new SignInFragment(),true,"SignIn");
        });

        return binding.getRoot();
    }


    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getFragmentManager();
        assert manager != null;
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.frame_login_container, fragment, tag);
        ft.commitAllowingStateLoss();
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
                    Toast.makeText(getContext(), "Register Successfully", Toast.LENGTH_SHORT).show();

                    UserModel user = new UserModel(binding.userNameSignup.getEditText().getText().toString(),
                            binding.emailSignUp.getEditText().getText().toString(),
                            binding.passwordSignUp.getEditText().getText().toString());

                    database.getReference().child("Users").child(auth.getCurrentUser().getUid()).setValue(user);

                    binding.progressBar5.setVisibility(View.GONE);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar5.setVisibility(View.GONE);
                });
    }
}