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
import com.roshan.questionnaire.Activities.MainActivity;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.FragmentSignInBinding;

public class SignInFragment extends Fragment {

    FragmentSignInBinding binding;
    FirebaseAuth auth;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();


        userLoginStatus();

        binding.loginBtn.setOnClickListener(v -> {
            checkEmptyStatus();
        });

        binding.signUpLogIn.setOnClickListener(v -> {
            addFragment(new SignUpFragment(),true,"SignUp");
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

                    Toast.makeText(requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar4.setVisibility(View.GONE);
                });
    }



    //Login user status ------------------------------>

    private void userLoginStatus(){
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }
}