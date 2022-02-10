package com.roshan.questionary.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Activities.CommentActivity;
import com.roshan.questionary.Dialogs.BottomSheetDialogForOfficials;
import com.roshan.questionary.Dialogs.ShowingImageDialog;
import com.roshan.questionary.Fragments.MyQuestionsFragment;
import com.roshan.questionary.Models.NotificationModel;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.MyQuestionPostViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MyQuestionsAdapter extends RecyclerView.Adapter<MyQuestionsAdapter.viewHolder> {

    Context context;
    List<PostModel> list;
    FirebaseDatabase database;

    public MyQuestionsAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public MyQuestionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_question_post_view, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyQuestionsAdapter.viewHolder holder, int position) {

        PostModel model = list.get(position);

        holder.binding.mqQuestionText.setText(model.getQuestionTxt());
        holder.binding.mqLike.setText(model.getLikeCount() + "");
        holder.binding.mqComments.setText(model.getCommentCount() + "");

        holder.binding.mqDatePostrv.setText(new SimpleDateFormat("d MMM, h:mm aaa")
                .format(new Date(Long.parseLong(model.getTime()+""))));

        if (!model.getQuestionImage().isEmpty()){
            holder.binding.mqQuestionImgrv.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(model.getQuestionImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.mqQuestionImgrv);
        }else {
            holder.binding.mqQuestionImgrv.setVisibility(View.GONE);
        }

        database.getReference().child("Users").child(model.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                assert post != null;

                Glide.with(context)
                        .load(post.getProfilePic())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.binding.mqProfileImageRv);

                holder.binding.mqNamePostRv.setText(post.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        holder.binding.mqOptionMenu.setOnClickListener(v -> {
            BottomSheetDialogFragment fragment = new BottomSheetDialogForOfficials(context, model.getPostId(), model.getUserId());
            fragment.show(((FragmentActivity)context).getSupportFragmentManager(), fragment.getTag());
        });

        // Like Method --------------------------->

        database.getReference().child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.mqLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                        }
                        else {
                            holder.binding.mqLike.setOnClickListener(v -> database.getReference().child("posts")
                                    .child(model.getPostId())
                                    .child("likes")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(true)
                                    .addOnSuccessListener(unused ->
                                            database.getReference().child("posts")
                                                    .child(model.getPostId())
                                                    .child("likeCount")
                                                    .setValue(model.getLikeCount() + 1)
                                                    .addOnSuccessListener(unused1 -> {
                                                        holder.binding.mqLike
                                                                .setCompoundDrawablesWithIntrinsicBounds
                                                                        (R.drawable.like, 0, 0, 0);

                                                        NotificationModel notification = new NotificationModel();
                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                        notification.setNotificationAt(new Date().getTime());
                                                        notification.setPostID(model.getPostId());
                                                        notification.setPostedBY(model.getUserId());
                                                        notification.setType("like");

                                                        database.getReference()
                                                                .child("notification")
                                                                .child(model.getUserId())
                                                                .push()
                                                                .setValue(notification);
                                                    })));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        holder.binding.mqComments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("userId", model.getUserId());
            context.startActivity(intent);
        });

        holder.binding.mqQuestionImgrv.setOnClickListener(v -> {
            ShowingImageDialog dialog = new ShowingImageDialog(model.getPostId());
            dialog.show(((FragmentActivity)context).getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder{
        MyQuestionPostViewBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MyQuestionPostViewBinding.bind(itemView);
        }
    }
}
