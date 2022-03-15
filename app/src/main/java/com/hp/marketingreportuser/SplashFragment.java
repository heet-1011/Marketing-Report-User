package com.hp.marketingreportuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import java.util.Timer;
import java.util.TimerTask;

public class SplashFragment extends Fragment {

    public SplashFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_splash, container, false);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((AuthenticationActivity)getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("authentication", Context.MODE_PRIVATE);
                        if(sharedPreferences.contains("mobNo") && sharedPreferences.contains("pwd") && !sharedPreferences.getString("mobNo","").equals("") && !sharedPreferences.getString("pwd","").equals("")){
                            Intent intent = new Intent(getActivity(),HomeActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }else{
                            Navigation.findNavController(root).navigate(R.id.action_SplashFragment_to_SignInFragment);
                        }
                    }
                });
            }
        },3000);
        return root;
    }
}