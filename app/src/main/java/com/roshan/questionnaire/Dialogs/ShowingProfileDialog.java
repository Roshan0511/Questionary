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

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionnaire.Models.UserModel;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.ShowingProfileDialogBinding;

import java.util.Objects;

public class ShowingProfileDialog extends DialogFragment {

    ShowingProfileDialogBinding binding;
    FirebaseDatabase database;
    String userId;

    public ShowingProfileDialog(String userId) {
        this.userId = userId;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Objects.requireNonNull(getDialog()).getWindow() != null){
            getDialog().getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );

            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShowingProfileDialogBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();

        database.getReference().child("Users")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel user = snapshot.getValue(UserModel.class);

                            if (getActivity()!=null && !getActivity().isFinishing()){
                                assert user != null;
                                Glide.with(ShowingProfileDialog.this)
                                        .load(user.getProfilePic())
                                        .placeholder(R.drawable.placeholder)
                                        .into(binding.profilePicSearch);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        Zoomy.Builder builder = new Zoomy.Builder(getDialog())
                .target(binding.profilePicSearch)
                .enableImmersiveMode(false)
                .animateZooming(true);
        builder.register();

        binding.closeDialog.setOnClickListener(v -> dismiss());


        return binding.getRoot();
    }
}
