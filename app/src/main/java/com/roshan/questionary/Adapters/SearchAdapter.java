package com.roshan.questionary.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.roshan.questionary.Models.UserModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.SearchRvViewBinding;

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
        holder.binding.emailOnSearch.setText(userModel.getEmail());

        Glide.with(context)
                .load(userModel.getProfilePic())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.profilePicSearch);
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
