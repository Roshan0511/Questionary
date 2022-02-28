package com.roshan.questionary.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.roshan.questionary.Activities.MainActivity;
import com.roshan.questionary.Fragments.HomeFragment;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.BackToMainFragmentBinding;

import java.util.Objects;

public class BackHomeDialog extends DialogFragment {

    BackToMainFragmentBinding binding;

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
        binding = BackToMainFragmentBinding.inflate(inflater, container, false);

        binding.cancelBtn.setOnClickListener(v -> dismiss());

        binding.exitBtn.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();

            dismiss();
        });

        return binding.getRoot();
    }
}
