package com.roshan.questionary.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Adapters.NotificationAdapter;
import com.roshan.questionary.Models.NotificationModel;
import com.roshan.questionary.databinding.FragmentNotificationBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    NotificationAdapter adapter;
    List<NotificationModel> list = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        getDataFromDatabase();
        adapter = new NotificationAdapter(getContext(), list);
        binding.notificationRvList.setAdapter(adapter);
        binding.notificationRvList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.notificationRvList.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }


    private void getDataFromDatabase(){
        database.getReference()
                .child("notification")
                .child(Objects.requireNonNull(auth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                NotificationModel model = dataSnapshot.getValue(NotificationModel.class);
                                assert model != null;
                                model.setNotificationId(dataSnapshot.getKey());
                                if (!model.getPostID().isEmpty()){
                                    list.add(model);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

//    private void pressBackButton(){
//        Intent intent = new Intent(requireActivity(), MainActivity.class);
//        startActivity(intent);
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        requireView().setFocusableInTouchMode(true);
//        requireView().requestFocus();
//        requireView().setOnKeyListener((v, keyCode, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                pressBackButton();
//                return true;
//            }
//            return false;
//        });
//    }
}