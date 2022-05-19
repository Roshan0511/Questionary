package com.roshan.questionnaire.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionnaire.Adapters.SearchAdapter;
import com.roshan.questionnaire.Models.UserModel;
import com.roshan.questionnaire.R;
import com.roshan.questionnaire.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    FirebaseDatabase database;
    List<UserModel> list = new ArrayList<>();
    SearchAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();

        binding.backBtn.setOnClickListener(v -> pressBackButton());

        return binding.getRoot();
    }


    private void setData(){
        database.getReference()
                .child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                assert userModel != null;
                                userModel.setUserId(dataSnapshot.getKey());
                                list.add(userModel);
                            }
                            adapter = new SearchAdapter(getContext(), list);
                            binding.searchRv.setLayoutManager(new LinearLayoutManager(getContext()));
                            binding.searchRv.setAdapter(adapter);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void filterList(){
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<UserModel> filteredList = new ArrayList<>();
                for (UserModel item: list){
                    if (item.getName().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(item);
                    }
                }
                adapter.filteredList(filteredList);
                return true;
            }
        });
    }

    private void pressBackButton() {
        MyProfileFragment fragment = new MyProfileFragment();
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.linearLayout, fragment);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        setData();

        filterList();

        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                pressBackButton();
                return true;
            }
            return false;
        });
    }
}