package com.roshan.questionary.Dialogs;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.roshan.questionary.Fragments.EditPostFragment;
import com.roshan.questionary.Models.PostModel;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.BottomSheetHomeOfficialsBinding;

import java.io.File;

public class BottomSheetDialogForOfficials extends BottomSheetDialogFragment {

    BottomSheetHomeOfficialsBinding binding;
    Context context;
    String postId;
    String userId;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String url;

    public BottomSheetDialogForOfficials(Context context, String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
        this.context = context;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = BottomSheetHomeOfficialsBinding.inflate(inflater, container, false);

        loadData();

        binding.editPostHome.setOnClickListener(v -> {
            editClicked();
            dismiss();
        });



        binding.sharePostHome.setOnClickListener(v -> {

            Intent intent2 = new Intent();
            intent2.setAction(Intent.ACTION_SEND);
            intent2.setType("text/plain");
            intent2.putExtra(Intent.EXTRA_TEXT, "Here your Question " + url);
            startActivity(Intent.createChooser(intent2, "Share via"));

            dismiss();
        });

        binding.deletePostHome.setOnClickListener(v -> {
            DeleteDialog dialog = new DeleteDialog(context, postId);
            dialog.show(((FragmentActivity)context).getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
            dismiss();
        });



        binding.downloadPostHome.setOnClickListener(v -> {
            downloadFile(url);
            Toast.makeText(context, "Downloading....", Toast.LENGTH_LONG).show();

            dismiss();
        });

        return binding.getRoot();
    }


    private void loadData(){
        database.getReference().child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    PostModel model = snapshot.getValue(PostModel.class);
                    assert model != null;
                    url = model.getQuestionImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Download Image ---------------------------->

    private void downloadFile(String url){
        try {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setAllowedOverRoaming(false)
                    .setTitle("Questionary")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator
                            + "Questionary" + ".jpg");

            manager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // Edit Post --------------------------------------->

    private void editClicked(){
        Bundle bundle = new Bundle();
        bundle.putString("postId",postId); // Put anything what you want
        bundle.putString("userId", userId);

        EditPostFragment fragment = new EditPostFragment();
        fragment.setArguments(bundle);

        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.linearLayout, fragment)
                .commit();
    }
}
