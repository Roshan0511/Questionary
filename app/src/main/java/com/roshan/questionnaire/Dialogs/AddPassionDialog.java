package com.roshan.questionnaire.Dialogs;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionnaire.Fragments.ProfileFragment;
import com.roshan.questionnaire.Models.UserModel;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.AddPassionDialogBinding;

import java.util.Objects;

public class AddPassionDialog extends DialogFragment {

    AddPassionDialogBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    public void onStart() {
        super.onStart();

        if (Objects.requireNonNull(getDialog()).getWindow() != null){
            getDialog().getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddPassionDialogBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        database.getReference()
                .child("Users")
                .child(Objects.requireNonNull(auth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            assert userModel != null;

                            binding.saveBioBtn.setOnClickListener(v -> {
                                if (binding.etBio.getText().toString().trim().isEmpty()){
                                    binding.etBio.setError("This field must not be empty!");
                                }
                                else {
                                    database.getReference()
                                            .child("Users")
                                            .child(Objects.requireNonNull(auth.getUid()))
                                            .child("summary")
                                            .setValue(binding.etBio.getText().toString());

                                    ProfileFragment fragment = new ProfileFragment();
                                    assert getFragmentManager() != null;
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.linearLayout, fragment);
                                    transaction.commit();

                                    dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        binding.cancelBtn.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }
}
