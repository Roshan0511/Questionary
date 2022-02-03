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
import com.roshan.questionary.Dialogs.BottomSheetDialogForUnOfficials;
import com.roshan.questionary.Dialogs.ShowingImageDialog;
import com.roshan.questionary.Models.NotificationModel;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.QuestionsRvViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    Context context;
    List<PostModel> list;
    FirebaseDatabase database;

    public PostAdapter() {
        database = FirebaseDatabase.getInstance();
    }

    @SuppressLint("NotifyDataSetChanged")
    public PostAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
        database = FirebaseDatabase.getInstance();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.questions_rv_view, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        //set Data from list ---------------->

        PostModel model = list.get(position);

        holder.binding.questionMainPage.setText(model.getQuestionTxt());
        holder.binding.like.setText(model.getLikeCount() + "");
        holder.binding.comments.setText(model.getCommentCount() + "");

        holder.binding.datePostrv.setText(new SimpleDateFormat("d MMM, h:mm aaa")
                .format(new Date(Long.parseLong(model.getTime()+""))));

        if (!model.getQuestionImage().isEmpty()){
            holder.binding.questionImgrv.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(model.getQuestionImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.questionImgrv);
        }else {
            holder.binding.questionImgrv.setVisibility(View.GONE);
        }

        database.getReference().child("Users").child(model.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                assert post != null;

                if (context!=null){
                    Glide.with(context.getApplicationContext())
                            .load(post.getProfilePic())
                            .placeholder(R.drawable.placeholder)
                            .into(holder.binding.profileImageRv);
                }

                holder.binding.namePostRv.setText(post.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
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
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                        }
                        else {
                            holder.binding.like.setOnClickListener(v -> database.getReference().child("posts")
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
                                                        holder.binding.like
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

        holder.binding.comments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("userId", model.getUserId());
            context.startActivity(intent);
        });


        holder.binding.optionMenu.setOnClickListener(v ->{
            if (model.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogForOfficials(context, model.getPostId(),
                        model.getUserId());
                bottomSheetDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
            else {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogForUnOfficials(context, model.getPostId(),
                        model.getUserId());
                bottomSheetDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
            notifyDataSetChanged();
        });


        holder.binding.questionImgrv.setOnClickListener(v -> {
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
        QuestionsRvViewBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = QuestionsRvViewBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filteredList(List<PostModel> filterList){
        list = filterList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteData(String postId){
        database.getReference().child("posts")
                .child(postId)
                .removeValue();

        notifyDataSetChanged();
    }
}