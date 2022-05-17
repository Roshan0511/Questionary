package com.roshan.questionary.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Adapters.CommentAdapter;
import com.roshan.questionary.Dialogs.BottomSheetDialogForUnOfficials;
import com.roshan.questionary.Dialogs.ShowingImageDialog;
import com.roshan.questionary.Models.AnswerModel;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.ActivityCommentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    String postId, userId;
    CommentAdapter adapter;
    ProgressDialog pd;
    List<AnswerModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        postId = getIntent().getStringExtra("postId");
        userId = getIntent().getStringExtra("userId");

        initView();

        setDataInCommentActivity();

        setBookmarkData();

        binding.commentsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuestionCommentActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });


        binding.optionMenuCommentAc.setOnClickListener(v -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogForUnOfficials(CommentActivity.this
                    , postId, userId);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        });

        database.getReference()
                .child("posts")
                .child(postId)
                .child("answers")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                AnswerModel answers = dataSnapshot.getValue(AnswerModel.class);
                                assert answers != null;
                                answers.setAnswerId(Objects.requireNonNull(dataSnapshot.getKey()));
                                list.add(answers);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CommentActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        // setting list in adapter
        adapter = new CommentAdapter(CommentActivity.this, list, postId);
        binding.rvComment.setAdapter(adapter);
        binding.rvComment.setLayoutManager(new LinearLayoutManager(this));


        binding.questionImg.setOnClickListener(v -> {
            ShowingImageDialog dialog = new ShowingImageDialog(postId);
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        });


        binding.backBtn.setOnClickListener(v -> back());
    }


    private void initView() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        pd = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please Wait...");
        pd.setCancelable(false);
    }



    // Set Data from Database -------------------------->

    private void setDataInCommentActivity(){
        pd.show();

        database.getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                if (snapshot.exists()){
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;

                    if(getApplicationContext() != null){
                        Glide.with(getApplicationContext())
                                .load(userModel.getProfilePic())
                                .placeholder(R.drawable.placeholder)
                                .into(binding.profileImagePostCommentAc);
                    }

                    binding.name.setText(userModel.getName());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                if (snapshot.exists()){
                    PostModel post = snapshot.getValue(PostModel.class);
                    assert post != null;

                    binding.questionTxt.setText(post.getQuestionTxt());
                    binding.subjectToolbar.setText(post.getSubject());

                    if (!post.getQuestionImage().isEmpty()){
                        binding.questionImg.setVisibility(View.VISIBLE);

                        Glide.with(getApplicationContext())
                                .load(post.getQuestionImage())
                                .placeholder(R.drawable.placeholder)
                                .into(binding.questionImg);
                    }
                    else {
                        binding.questionImg.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setBookmarkData(){
        pd.show();

        database.getReference()
                .child("bookmarks")
                .child(Objects.requireNonNull(auth.getUid()))
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (pd.isShowing()){
                            pd.dismiss();
                        }

                        if (snapshot.exists()){
                            binding.bookmarkCommentAc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_bookmark_added_24,
                                    0,0,0);
                        }

                        else {
                            binding.bookmarkCommentAc.setOnClickListener(v -> {
                                database.getReference()
                                        .child("bookmarks")
                                        .child(Objects.requireNonNull(auth.getUid()))
                                        .child(postId)
                                        .setValue(true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.bookmarkCommentAc
                                                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_bookmark_added_24,
                                                        0,0,0);

                                                Toast.makeText(CommentActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (pd.isShowing()){
                            pd.dismiss();
                        }
                        Toast.makeText(CommentActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Back to Main Activity -------------------------->
    public void back(){
        onBackPressed();
    }
}