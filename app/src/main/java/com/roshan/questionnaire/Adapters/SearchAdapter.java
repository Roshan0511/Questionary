package com.roshan.questionnaire.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.roshan.questionnaire.Dialogs.ShowingProfileDialog;
import com.roshan.questionnaire.Fragments.UserProfileDetails;
import com.roshan.questionnaire.Models.UserModel;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.SearchRvViewBinding;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder> {

    Context context;
    List<UserModel> list;

    public SearchAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_rv_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.viewHolder holder, int position) {
        UserModel userModel = list.get(position);

        holder.binding.nameOnSearch.setText(userModel.getName());
        holder.binding.descriptionOnSearch.setText(userModel.getSummary());

        if (holder.binding.descriptionOnSearch.getText().equals("")){
            holder.binding.descriptionOnSearch.setVisibility(View.GONE);
        } else {
            holder.binding.descriptionOnSearch.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(userModel.getProfilePic())
                .placeholder(R.drawable.man)
                .into(holder.binding.profilePicSearch);

        holder.binding.profilePicSearch.setOnClickListener(v -> {
            ShowingProfileDialog dialog = new ShowingProfileDialog(userModel.getUserId());
            dialog.show(((FragmentActivity)context).getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        });

        holder.binding.searchItem.setOnClickListener(v -> {
            UserProfileDetails userDetails = new UserProfileDetails(userModel.getUserId());
            FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.linearLayout, userDetails);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        SearchRvViewBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchRvViewBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filteredList(List<UserModel> filterList){
        list = filterList;
        notifyDataSetChanged();
    }
}
