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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Activities.CommentActivity;
import com.roshan.questionary.Dialogs.BottomSheetDialogForOfficials;
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

    public MyQuestionsAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
        database = FirebaseDatabase.getInstance();
    }

    public MyQuestionsAdapter() {
        database = FirebaseDatabase.getInstance();
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

        holder.binding.giveAnswer.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("userId", model.getUserId());
            context.startActivity(intent);
        });

        holder.binding.questionItem.setOnLongClickListener(v -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogForOfficials(context.getApplicationContext()
                    , model.getPostId(), model.getUserId());
            bottomSheetDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
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
