package com.roshan.questionary.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.view.KeyEvent;
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
import com.roshan.questionary.Activities.MainActivity;
import com.roshan.questionary.Dialogs.BackHomeFragment;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.FragmentEditPostBinding;

import java.util.Date;
import java.util.Objects;

public class EditPostFragment extends HomeFragment {

    FragmentEditPostBinding binding;
    String userId, postId;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri uri;

    public EditPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditPostBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        Bundle bundle = this.getArguments();
        if(bundle != null){
            userId = bundle.getString("userId");
            postId = bundle.getString("postId");
        }

        loadPostData();

        binding.backBtn.setOnClickListener(v -> pressBackButton());

        binding.updateBtn.setOnClickListener(v -> updateBtnClicked());


        binding.changeImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 11);
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==11 && resultCode == Activity.RESULT_OK){
            assert data != null;
            if (data.getData() != null) {
                uri = data.getData();
                binding.questionImage.setVisibility(View.VISIBLE);
                binding.questionImage.setImageURI(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void loadPostData(){
        database.getReference()
                .child("posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            PostModel post = snapshot.getValue(PostModel.class);

                            assert post != null;
                            binding.askQuestionET.setText(post.getQuestionTxt());
                            if (post.getQuestionImage().equals("")){
                                binding.questionImage.setVisibility(View.GONE);
                            }else {
                                Glide.with(requireActivity())
                                        .load(post.getQuestionImage())
                                        .placeholder(R.drawable.placeholder)
                                        .into(binding.questionImage);

                                binding.questionImage.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        database.getReference().child("Users")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel user = snapshot.getValue(UserModel.class);

                            assert user != null;

                            binding.userNameEdit.setText(user.getName());
                            Glide.with(requireActivity())
                                    .load(user.getProfilePic())
                                    .placeholder(R.drawable.placeholder)
                                    .into(binding.profilePicEdit);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void updateBtnClicked(){
        PostModel postModel = new PostModel();

        binding.progressBar.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (binding.askQuestionET.getText().toString().trim().equals("")){
            binding.askQuestionET.setError("Not Empty");
            binding.progressBar.setVisibility(View.GONE);
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else if (uri != null){
            final StorageReference reference = storage.getReference().child("posts")
                    .child(Objects.requireNonNull(auth.getUid()))
                    .child(new Date().getTime() + "");
            reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {

                postModel.setQuestionImage(uri.toString());
                postModel.setTime(new Date().getTime());
                postModel.setUserId(auth.getUid());
                postModel.setQuestionTxt(binding.askQuestionET.getText().toString());

                database.getReference().child("posts")
                        .child(postId)
                        .setValue(postModel).addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Posted Successfully..", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    binding.askQuestionET.setText(null);
                    binding.questionImage.setVisibility(View.GONE);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
            }));
        }
        else {
            postModel.setQuestionImage("");
            postModel.setTime(new Date().getTime());
            postModel.setUserId(auth.getUid());
            postModel.setQuestionTxt(binding.askQuestionET.getText().toString());

            database.getReference().child("posts")
                    .child(postId)
                    .setValue(postModel).addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Posted Successfully..", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                binding.askQuestionET.setText(null);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }



    private void pressBackButton(){
        BackHomeFragment backHomeFragment = new BackHomeFragment();
        backHomeFragment.show(((FragmentActivity)requireContext()).getSupportFragmentManager(), getTag());
    }


    @Override
    public void onResume() {
        super.onResume();

        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                pressBackButton();
                return true;
            }
            return false;
        });
    }
}