package com.roshan.questionary.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roshan.questionary.Dialogs.AddPassionDialog;
import com.roshan.questionary.Dialogs.LogOutDialog;
import com.roshan.questionary.Dialogs.ShowingProfileDialog;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.FragmentProfileBinding;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        //Initialization ------------------->

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        setDataInFragment();


        //Click Event ------------------------>

        binding.profileImage.setOnClickListener(v -> {
            ShowingProfileDialog dialog = new ShowingProfileDialog(auth.getUid());
            dialog.show(((FragmentActivity)requireContext()).getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        });

        binding.changeProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 11);
        });

        binding.logout.setOnClickListener(v -> {
            LogOutDialog dialog = new LogOutDialog();
            dialog.show(((FragmentActivity)requireContext()).getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        });

        binding.myQuestionTxt.setOnClickListener(v -> {
            MyQuestionsFragment fragment = new MyQuestionsFragment();
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.linearLayout, fragment);
            transaction.commit();
        });

        binding.recommendation.setOnClickListener(v -> {
            Fragment fragment = new RecommendationFragment();
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.linearLayout, fragment);
            transaction.commit();
        });

        binding.editSummaryBtn.setOnClickListener(v -> {
            AddPassionDialog dialog = new AddPassionDialog();
            dialog.show(((FragmentActivity) requireContext()).getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        });

        return binding.getRoot();
    }


    //Activity Result for Image Selector ----------------->

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            assert data != null;
            if (data.getData()!=null) {
                binding.progressBar.setVisibility(View.VISIBLE);
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Uri uri = data.getData();
                binding.profileImage.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("profile_image")
                        .child(Objects.requireNonNull(auth.getUid()));

                reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().
                            addOnSuccessListener(uri1 -> database.getReference().child("Users").
                                    child(auth.getUid()).child("profilePic").setValue(uri1.toString()));

                    binding.progressBar.setVisibility(View.GONE);
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                });
            }
        }
    }



    //Setting Image and Name when this fragment is open ---------------------->

    private void setDataInFragment(){
        binding.progressBar.setVisibility(View.VISIBLE);

        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            UserModel user = snapshot.getValue(UserModel.class);

                            assert user != null;

                            if (getContext() != null){
                                Glide.with(requireActivity())
                                        .load(user.getProfilePic())
                                        .placeholder(R.drawable.placeholder)
                                        .into(binding.profileImage);
                            }

                            binding.userNameProfile.setText(user.getName());

                            binding.userEmail.setText(user.getEmail());

                            if (user.getSummary() == null){
                                binding.summary.setVisibility(View.GONE);
                            }
                            else {
                                binding.summary.setText(user.getSummary());
                                binding.summary.setVisibility(View.VISIBLE);
                            }
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}