package com.roshan.questionary.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Activities.CommentActivity;
import com.roshan.questionary.Models.NotificationModel;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.NotificationRvBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    Context context;
    List<NotificationModel> list;
    FirebaseDatabase database;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public NotificationAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_rv, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.viewHolder holder, int position) {
        NotificationModel model = list.get(position);

        holder.binding.dateRvNotification.setText(new SimpleDateFormat("d MMM, h:mm aaa")
                .format(new Date(Long.parseLong(model.getNotificationAt()+""))));

        database.getReference()
                .child("Users")
                .child(model.getNotificationBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        assert userModel != null;

                        Glide.with(context)
                                .load(userModel.getProfilePic())
                                .placeholder(R.drawable.placeholder)
                                .into(holder.binding.profilePicNotification);

                        if (model.getType().equals("like")){
                            holder.binding.titleNotification.setText(Html.fromHtml("<b>" + userModel.getName() + "</b>" +
                                    " liked your question."));
                        }
                        else if (model.getType().equals("comment")){
                            holder.binding.titleNotification.setText(Html.fromHtml("<b>" + userModel.getName() + "</b>" +
                                    " commented on your question."));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        holder.binding.notificationItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostID());
            intent.putExtra("userId", model.getPostedBY());
            context.startActivity(intent);
        });


        holder.binding.notificationItem.setOnLongClickListener(v -> {
            Toast.makeText(context, "Single click for viewing question", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder {
        NotificationRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationRvBinding.bind(itemView);
        }
    }
}
