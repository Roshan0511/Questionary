package com.roshan.questionary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.roshan.questionary.Fragments.HomeFragment;
import com.roshan.questionary.Fragments.NotificationFragment;
import com.roshan.questionary.Fragments.PostFragment;
import com.roshan.questionary.Fragments.ProfileFragment;
import com.roshan.questionary.Fragments.SearchFragment;
import com.roshan.questionary.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            switch (item.getItemId()){
                case R.id.home:
                    HomeFragment homeFragment1 = new HomeFragment();
                    FragmentTransaction transaction12 = getSupportFragmentManager().beginTransaction();
                    transaction12.replace(R.id.linearLayout, homeFragment1);
                    transaction12.commit();
                    break;

                case R.id.search:
                    SearchFragment searchFragment = new SearchFragment();
                    FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                    transaction4.replace(R.id.linearLayout, searchFragment);
                    transaction4.commit();
                    break;

                case R.id.post:
                    PostFragment postFragment = new PostFragment();
                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                    transaction1.replace(R.id.linearLayout, postFragment);
                    transaction1.commit();
                    break;

                case R.id.notification:
                    NotificationFragment notificationFragment = new NotificationFragment();
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.linearLayout, notificationFragment);
                    transaction3.commit();
                    break;

                case R.id.profile:
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.linearLayout, profileFragment);
                    transaction2.commit();
                    break;
            }
            return true;
        });
    }
}