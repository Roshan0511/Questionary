package com.roshan.questionary.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Dialogs.DeleteCommentDialog;
import com.roshan.questionary.Models.CommentModel;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.CommentRvViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {

    Context context;
    List<CommentModel> list;
    String postId;
    FirebaseDatabase database;

    public CommentAdapter(Context context, List<CommentModel> list, String postId) {
        this.context = context;
        this.list = list;
        this.postId = postId;
        database = FirebaseDatabase.getInstance();
    }

    public CommentAdapter() {
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_rv_view, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel model = list.get(position);

        holder.binding.commentRvForList.setText(model.getCommentText());
        holder.binding.dateRvForList.setText(new SimpleDateFormat("d MMM, h:mm aaa")
                .format(new Date(Long.parseLong(model.getCommentedAt()+""))));

        database.getReference()
                .child("Users")
                .child(model.getCommentedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);

                        assert user != null;
                        holder.binding.nameRvForList.setText(user.getName());

                        if (context != null){
                            Glide.with(context.getApplicationContext())
                                    .load(user.getProfilePic())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.binding.profilePicRvForList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        holder.binding.cardView2.setOnLongClickListener(v -> {
            if (model.getCommentedBy().equals(FirebaseAuth.getInstance().getUid())){

                DeleteCommentDialog dialog = new DeleteCommentDialog(postId, model.getCommentId());
                dialog.show(((FragmentActivity)context).getSupportFragmentManager(), dialog.getTag());
                dialog.setCancelable(false);

                return true;
            }
            else {
                Toast.makeText(context, "You can't handle this answer", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder{
        CommentRvViewBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentRvViewBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteComment(String postId, String commentId){
        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .child(commentId)
                .removeValue();



        database.getReference()
                .child("posts")
                .child(postId)
                .child("commentCount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                        .setValue(commentsCount - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        notifyDataSetChanged();
    }
}
