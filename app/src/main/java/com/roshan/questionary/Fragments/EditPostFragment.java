package com.roshan.questionary.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roshan.questionary.Activities.MainActivity;
import com.roshan.questionary.Dialogs.BackHomeDialog;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.FragmentEditPostBinding;

import java.util.Date;
import java.util.Objects;

public class EditPostFragment extends HomeFragment {

    FragmentEditPostBinding binding;
    String userId, postId;
    String imageUrl;
    long postTime;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri uri;
    ProgressDialog pd;
    private RequestManager imageLoader;

    public EditPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditPostBinding.inflate(inflater, container, false);

        initView();
        setData();

        Bundle bundle = this.getArguments();
        if(bundle != null){
            userId = bundle.getString("userId");
            postId = bundle.getString("postId");
        }

        binding.backBtn.setOnClickListener(v -> pressBackButton());

        binding.updateBtn.setOnClickListener(v -> {
            pd.show();
            requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            updateBtnClicked();
        });


        binding.changeImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 11);
        });

        return binding.getRoot();
    }

    private void initView() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        pd = new ProgressDialog(requireContext(), R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
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


    private void setData(){
        pd.show();

        database.getReference()
                .child("posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (pd.isShowing()){
                            pd.dismiss();
                        }
                        if (snapshot.exists()){
                            PostModel post = snapshot.getValue(PostModel.class);

                            assert post != null;
                            binding.askQuestionET.setText(post.getQuestionTxt());
                            if (post.getQuestionImage().equals("")){
                                binding.questionImage.setVisibility(View.GONE);
                            }else {
                                imageUrl = post.getQuestionImage();
                                postTime = post.getTime();

                                if (getActivity()!=null && !getActivity().isFinishing()) {
                                    Glide.with(EditPostFragment.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.placeholder)
                                            .into(binding.questionImage);

                                    binding.questionImage.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        if (pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                });

        database.getReference().child("Users")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (pd.isShowing()){
                            pd.dismiss();
                        }
                        if (snapshot.exists()){
                            UserModel user = snapshot.getValue(UserModel.class);

                            assert user != null;

                            if (getActivity()!=null && !getActivity().isFinishing()) {
                                binding.userNameEdit.setText(user.getName());
                                Glide.with(EditPostFragment.this)
                                        .load(user.getProfilePic())
                                        .placeholder(R.drawable.placeholder)
                                        .into(binding.profilePicEdit);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                });
    }



    private void updateBtnClicked(){

        if (uri != null){
            final StorageReference reference = storage.getReference().child("posts")
                    .child(Objects.requireNonNull(auth.getUid()))
                    .child(new Date().getTime() + "");
            reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri ->
                    database.getReference()
                    .child("posts")
                    .child(postId)
                    .child("questionImage")
                    .setValue(uri.toString())));
        }


        if (binding.askQuestionET.getText().toString().trim().isEmpty()){
            binding.askQuestionET.setError("Required!");
        }
        else {
            database.getReference()
                    .child("posts")
                    .child(postId)
                    .child("questionTxt")
                    .setValue(binding.askQuestionET.getText().toString());
        }

        binding.askQuestionET.setText(null);
        binding.questionImage.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (pd.isShowing()){
            pd.dismiss();
        }

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }


    private void pressBackButton(){
        BackHomeDialog backHomeDialog = new BackHomeDialog();
        backHomeDialog.show(((FragmentActivity)requireContext()).getSupportFragmentManager(), getTag());
        backHomeDialog.setCancelable(false);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (imageLoader == null) {
            imageLoader = Glide.with(EditPostFragment.this);
        }
        else {
            imageLoader.onDestroy();
        }
    }
}