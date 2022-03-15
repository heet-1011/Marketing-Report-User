package com.hp.marketingreportuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.button.MaterialButton;

public class AuthenticationActivity extends AppCompatActivity {
    NavController navController;
    Window window;
    MaterialButton btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_authentication);
        navController = navHostFragment.getNavController();
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                switch (navDestination.getId()){
                    case R.id.SignInFragment:
                        chkInternetSpeed();
                        break;
                    case R.id.FgtPwdFragment:
                        window.setStatusBarColor(ContextCompat.getColor(AuthenticationActivity.this,R.color.secondary));
                }
            }
        });
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkInternetSpeed();
            }
        });
    }

    public boolean chkInternetSpeed() {
        if(networkChkClass.chkInternetSpeed(this)){
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
            findViewById(R.id.error).setVisibility(View.GONE);
            return true;
        }else{
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondary));
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            return false;
        }
    }
}