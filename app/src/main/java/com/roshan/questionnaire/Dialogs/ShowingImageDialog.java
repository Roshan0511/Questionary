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
import com.roshan.questionnaire.Models.PostModel;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.ShowingImageDialogBinding;

import java.util.Objects;

public class ShowingImageDialog extends DialogFragment {
    ShowingImageDialogBinding binding;
    String postId;
    FirebaseDatabase database;

    public ShowingImageDialog(String postId) {
        this.postId = postId;
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Objects.requireNonNull(getDialog()).getWindow() != null){
            getDialog().getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShowingImageDialogBinding.inflate(inflater, container, false);

        database.getReference()
                .child("posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            PostModel model = snapshot.getValue(PostModel.class);

                            assert model != null;
                            if (getActivity()!=null && !getActivity().isFinishing()){
                            Glide.with(ShowingImageDialog.this)
                                    .load(model.getQuestionImage())
                                    .placeholder(R.drawable.placeholder)
                                    .into(binding.questionImage);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Zoomy.Builder builder = new Zoomy.Builder(getDialog())
                .target(binding.questionImage)
                .enableImmersiveMode(false)
                .animateZooming(true);
        builder.register();

        binding.closeDialog.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }
}
