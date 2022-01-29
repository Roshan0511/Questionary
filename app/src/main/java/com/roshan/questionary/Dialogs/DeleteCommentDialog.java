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
import com.roshan.questionary.Adapters.CommentAdapter;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.DeleteCommentDialogBinding;

import java.util.Objects;

public class DeleteCommentDialog extends DialogFragment {

    DeleteCommentDialogBinding binding;
    FirebaseDatabase database;
    String postId, commentId;

    public DeleteCommentDialog(String postId, String commentId) {
        this.postId = postId;
        this.commentId = commentId;
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
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DeleteCommentDialogBinding.inflate(inflater, container, false);

        binding.cancelBtn.setOnClickListener(v -> dismiss());

        binding.deleteBtn.setOnClickListener(v -> {
            CommentAdapter commentAdapter = new CommentAdapter();
            commentAdapter.deleteComment(postId, commentId);

            dismiss();
        });

        return binding.getRoot();
    }
}
