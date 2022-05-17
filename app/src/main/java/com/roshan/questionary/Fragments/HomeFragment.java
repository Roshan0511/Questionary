package com.roshan.questionary.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Adapters.QuestionsAdapter;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    List<PostModel> list;
    QuestionsAdapter questionsAdapter;
    ProgressDialog pd;

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

        initView();

//        setDataForPostAdapter();

//        adapter = new PostAdapter(getContext(), list);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            if (list.size()!=0){
                list.clear();
                setDataForPostAdapter();
            } else {
                setDataForPostAdapter();
            }
            Toast.makeText(requireContext(), "Refresh..", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (list.size()!=0){
            list.clear();
            setDataForPostAdapter();
        } else {
            setDataForPostAdapter();
        }
    }

    private void initView(){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

        pd = new ProgressDialog(requireContext(), R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please Wait...");
        pd.setCancelable(false);
    }

    private void updateUI() {
        questionsAdapter = new QuestionsAdapter(getContext(), list);
        binding.rvHome.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHome.setAdapter(questionsAdapter);
    }


    //Set Data for Post Adapter ----------------------------->

    private void setDataForPostAdapter(){
        pd.show();
        binding.card.setVisibility(View.GONE);
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (pd.isShowing()){
                    pd.dismiss();
                }

                list.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        PostModel post = dataSnapshot.getValue(PostModel.class);
                        assert post != null;
                        post.setPostId(dataSnapshot.getKey());
                        list.add(post);

                        updateUI();
                    }
                }
                questionsAdapter.notifyDataSetChanged();

                binding.card.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}