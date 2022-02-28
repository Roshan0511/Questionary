package com.roshan.questionary.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.ConfirmationBeforeDeletingBinding;

import java.util.Objects;

public class DeleteDialog extends DialogFragment {

    ConfirmationBeforeDeletingBinding binding;
    Context context;
    FirebaseDatabase database;
    String postId;

    public DeleteDialog(Context context, String postId) {
        this.context = context;
        this.postId = postId;
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Objects.requireNonNull(getDialog()).getWindow() != null) {
            getDialog().getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ConfirmationBeforeDeletingBinding.inflate(inflater, container, false);

        binding.cancelBtn.setOnClickListener(v -> dismiss());
        binding.deleteBtn.setOnClickListener(v -> {
//            PostAdapter postAdapter = new PostAdapter();
//            postAdapter.deleteData(postId);

            dismiss();
        });

        return binding.getRoot();
    }
}
