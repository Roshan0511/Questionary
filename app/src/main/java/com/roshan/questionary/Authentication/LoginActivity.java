package com.roshan.questionary.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.roshan.questionary.Dialogs.ExitAppDialog;
import com.roshan.questionary.R;
import com.roshan.questionary.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        addFragment(new SignInFragment(),false);

        setContentView(binding.getRoot());
    }


    public void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.frame_login_container, fragment, null);
        ft.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            ExitAppDialog dialog = new ExitAppDialog();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            dialog.setCancelable(false);
        }
        else {
            getSupportFragmentManager().popBackStack();
        }
    }
}