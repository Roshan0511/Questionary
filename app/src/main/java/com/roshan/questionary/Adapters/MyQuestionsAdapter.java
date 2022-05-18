package com.roshan.questionary.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Activities.CommentActivity;
import com.roshan.questionary.Dialogs.BottomSheetDialogForOfficials;
import com.roshan.questionary.Dialogs.DeleteDialog;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.HomepageQuestionViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyQuestionsAdapter extends RecyclerView.Adapter<MyQuestionsAdapter.viewHolder> {

    Context context;
    List<PostModel> list;
    FirebaseDatabase database;
    FirebaseAuth auth;

    public MyQuestionsAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public MyQuestionsAdapter() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyQuestionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_question_view, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyQuestionsAdapter.viewHolder holder, int position) {

        PostModel model = list.get(position);

        holder.binding.question.setText(model.getQuestionTxt());
        holder.binding.category.setText(model.getSubject());

        holder.binding.time.setText(new SimpleDateFormat("d MMM, h:mm aaa")
                .format(new Date(Long.parseLong(model.getTime()+""))));

        if (!model.getQuestionImage().isEmpty()){
            holder.binding.imageChecker.setVisibility(View.VISIBLE);
        }else {
            holder.binding.imageChecker.setVisibility(View.GONE);
        }

        database.getReference().child("Users").child(model.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                assert post != null;

                Glide.with(context)
                        .load(post.getProfilePic())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.binding.userProfilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        database.getReference()
                .child("bookmarks")
                .child(auth.getUid())
                .child(model.getPostId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.bookmark.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_bookmark_added_24,
                                    0, 0, 0);
                        }
                        else {
                            holder.binding.bookmark.setOnClickListener(v -> {
                                database.getReference()
                                        .child("bookmarks")
                                        .child(auth.getUid())
                                        .child(model.getPostId())
                                        .setValue(true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                holder.binding.bookmark.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_bookmark_added_24,
                                                        0, 0, 0);

                                                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                    }
                });


        holder.binding.giveAnswer.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("userId", model.getUserId());
            context.startActivity(intent);
        });

        holder.binding.questionItem.setOnLongClickListener(v -> {
            DialogFragment deleteDialog = new DeleteDialog(context.getApplicationContext(), model.getPostId());
            deleteDialog.setCancelable(false);
            deleteDialog.show(((FragmentActivity)context).getSupportFragmentManager(), deleteDialog.getTag());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder{
        HomepageQuestionViewBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomepageQuestionViewBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteData(String postId){
        database.getReference().child("posts")
                .child(postId)
                .removeValue();

        notifyDataSetChanged();
    }
}
