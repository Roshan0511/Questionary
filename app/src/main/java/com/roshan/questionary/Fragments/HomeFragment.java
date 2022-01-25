package com.roshan.questionary.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Adapters.PostAdapter;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    List<PostModel> list;
    List<PostModel> filteredList;
    PostAdapter adapter;

    FirebaseAuth auth;
    FirebaseDatabase database;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        searchFilter();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        list = new ArrayList<>();

        setDataForPostAdapter();

        adapter = new PostAdapter(getContext(), list);
        binding.rvHome.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHome.setAdapter(adapter);

        binding.refresh.setOnClickListener(v -> {
            binding.rvHome.smoothScrollToPosition(0);
        });

        return binding.getRoot();
    }


    //Set Data for Post Adapter ----------------------------->

    private void setDataForPostAdapter(){
        binding.progressBar3.setVisibility(View.VISIBLE);
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        PostModel post = dataSnapshot.getValue(PostModel.class);
                        assert post != null;
                        post.setPostId(dataSnapshot.getKey());
                        list.add(post);
                    }
                    if (list.isEmpty()){
                        binding.empty.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.empty.setVisibility(View.GONE);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progressBar3.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchFilter(){
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList = new ArrayList<>();
                for (PostModel item: list){
                    if (item.getQuestionTxt().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(item);
                    }
                }

                adapter.filteredList(filteredList);
                if (filteredList.isEmpty()){
                    binding.empty.setVisibility(View.VISIBLE);
                }
                else {
                    binding.empty.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }
}