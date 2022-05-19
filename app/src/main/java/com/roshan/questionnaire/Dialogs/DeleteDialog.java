package com.roshan.questionnaire.Dialogs;

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
import com.roshan.questionnaire.Adapters.BookmarkAdapter;
import com.roshan.questionnaire.Adapters.MyQuestionsAdapter;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.ConfirmationBeforeDeletingBinding;

import java.util.Objects;

public class DeleteDialog extends DialogFragment {

    ConfirmationBeforeDeletingBinding binding;
    Context context;
    FirebaseDatabase database;
    String postId, userId;

    public DeleteDialog(Context context, String postId, String userId) {
        this.context = context;
        this.postId = postId;
        this.userId = userId;
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
            MyQuestionsAdapter questionsAdapter = new MyQuestionsAdapter();
            questionsAdapter.deleteData(postId, userId);

            BookmarkAdapter bookmarkAdapter = new BookmarkAdapter();
            bookmarkAdapter.deleteData(postId, userId);

            dismiss();
        });

        return binding.getRoot();
    }
}
