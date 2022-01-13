package com.roshan.questionary.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Adapters.CommentAdapter;
import com.roshan.questionary.Models.CommentModel;
import com.roshan.questionary.Models.NotificationModel;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    String postId, userId;
    CommentAdapter adapter;
    List<CommentModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        postId = getIntent().getStringExtra("postId");
        userId = getIntent().getStringExtra("userId");

        setDataInCommentActivity();

        binding.sendCommentBtn.setOnClickListener(v -> {
            if (binding.commentsET.getText().toString().isEmpty()){
                binding.commentsET.setError("Required!");
            }
            else {
                setCommentData();
            }
        });

        adapter = new CommentAdapter(CommentActivity.this, list);
        binding.rvComment.setAdapter(adapter);
        binding.rvComment.addItemDecoration(new DividerItemDecoration(CommentActivity.this, DividerItemDecoration.VERTICAL));
        binding.rvComment.setLayoutManager(new LinearLayoutManager(this));

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            CommentModel comments = dataSnapshot.getValue(CommentModel.class);
                            list.add(comments);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CommentActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        binding.backBtn.setOnClickListener(v -> {
            back();
        });
    }


    // Set Data from Database -------------------------->

    private void setDataInCommentActivity(){
        database.getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    assert userModel != null;

                    Picasso.get()
                            .load(userModel.getProfilePic())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.profileImagePostCommentAc);

                    binding.namePostCommentAc.setText(userModel.getName());

                    binding.commentPageTitle.setText(userModel.getName()+ "'s post");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    PostModel post = snapshot.getValue(PostModel.class);
                    assert post != null;

                    binding.questionTextCommentAc.setText(post.getQuestionTxt());
                    binding.commentsCommentAc.setText(post.getCommentCount() + "");

                    if (!post.getQuestionImage().isEmpty()){
                        binding.questionImgCommentAc.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(post.getQuestionImage())
                                .placeholder(R.drawable.placeholder)
                                .into(binding.questionImgCommentAc);
                    }
                    else {
                        binding.questionImgCommentAc.setVisibility(View.GONE);
                    }

                    binding.likeCommentAc.setText(post.getLikeCount() + "");

                    binding.datePostCommentAc.setText(new SimpleDateFormat("d MMM, h:mm aaa")
                        .format(new Date(Long.parseLong(post.getTime()+""))));



                    // Setting Like Features -------------------------->

                    database.getReference().child("posts")
                            .child(postId)
                            .child("likes")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        binding.likeCommentAc.setCompoundDrawablesWithIntrinsicBounds
                                                (R.drawable.like, 0, 0, 0);
                                    }
                                    else {
                                        binding.likeCommentAc.setOnClickListener(v -> database.getReference().child("posts")
                                                .child(postId)
                                                .child("likes")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .setValue(true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        binding.likeCommentAc
                                                                .setCompoundDrawablesWithIntrinsicBounds
                                                                        (R.drawable.like, 0, 0, 0);

                                                        NotificationModel notification = new NotificationModel();
                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                        notification.setNotificationAt(new Date().getTime());
                                                        notification.setPostID(postId);
                                                        notification.setPostedBY(userId);
                                                        notification.setType("like");

                                                        database.getReference()
                                                                .child("notification")
                                                                .child(userId)
                                                                .push()
                                                                .setValue(notification);
                                                    }
                                                }));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    // Setting Comment Features ------------------------------->

    private void setCommentData(){
        CommentModel comment = new CommentModel();
        comment.setCommentText(binding.commentsET.getText().toString());
        comment.setCommentedBy(auth.getUid());
        comment.setCommentedAt(new Date().getTime());

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .push()
                .setValue(comment)
                .addOnSuccessListener(unused ->
                        database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int commentsCount = 0;
                        if (snapshot.exists()){
                            commentsCount = snapshot.getValue(Integer.class);
                        }
                        database.getReference()
                                .child("posts")
                                .child(postId)
                                .child("commentCount")
                                .setValue(commentsCount + 1)
                                .addOnSuccessListener(unused1 -> {

                                    binding.commentsET.setText("");
                                    Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();

                                    NotificationModel notification = new NotificationModel();
                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                    notification.setNotificationAt(new Date().getTime());
                                    notification.setPostID(postId);
                                    notification.setPostedBY(userId);
                                    notification.setType("comment");

                                    database.getReference()
                                            .child("notification")
                                            .child(userId)
                                            .push()
                                            .setValue(notification);

                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CommentActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                }));
    }


    // Back to Main Activity -------------------------->
    private void back(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}