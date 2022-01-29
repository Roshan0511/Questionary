package com.roshan.questionary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.roshan.questionary.Dialogs.ExitAppDialog;
import com.roshan.questionary.Fragments.HomeFragment;
import com.roshan.questionary.Fragments.NotificationFragment;
import com.roshan.questionary.Fragments.PostFragment;
import com.roshan.questionary.Fragments.ProfileFragment;
import com.roshan.questionary.Fragments.SearchFragment;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo data = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & data != null) && (wifi.isConnected() | data.isConnected())) {
            callingFragments();
        }
        else {
            binding.progressBar2.setVisibility(View.VISIBLE);
            Toast toast = Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG);
            toast.show();
        }
    }



    //Calling Fragments ---------------------->

    @SuppressLint("NonConstantResourceId")
    private void callingFragments(){
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.linearLayout, homeFragment);
        transaction.commit();

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.home:
                    if (binding.bottomNavigationBar.getSelectedItemId()==R.id.home){
                        return false;
                    }
                    fragment = new HomeFragment();
                    break;

                case R.id.search:
                    if (binding.bottomNavigationBar.getSelectedItemId()==R.id.search){
                        return false;
                    }
                    fragment = new SearchFragment();
                    break;

                case R.id.post:
                    if (binding.bottomNavigationBar.getSelectedItemId()==R.id.post){
                        return false;
                    }
                    fragment = new PostFragment();
                    break;

                case R.id.notification:
                    if (binding.bottomNavigationBar.getSelectedItemId()==R.id.notification){
                        return false;
                    }
                    fragment = new NotificationFragment();
                    break;

                case R.id.profile:
                    if (binding.bottomNavigationBar.getSelectedItemId()==R.id.profile){
                        return false;
                    }
                    fragment = new ProfileFragment();
                    break;
            }

            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            assert fragment != null;
            transaction1.replace(R.id.linearLayout, fragment);
            transaction1.commit();

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.bottomNavigationBar.getSelectedItemId()==R.id.home ||
                binding.bottomNavigationBar.getSelectedItemId()==R.id.post ||
                binding.bottomNavigationBar.getSelectedItemId()==R.id.profile){

            ExitAppDialog dialog = new ExitAppDialog();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);

        }
        else {
            binding.bottomNavigationBar.setSelectedItemId(R.id.home);
        }
    }
}