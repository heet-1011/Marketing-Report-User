package com.hp.marketingreportuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

public class SettingsFragment extends Fragment {

    MaterialButton btnLogOut;
    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        btnLogOut = root.findViewById(R.id.btnLogOut);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().getSharedPreferences("profile", Context.MODE_PRIVATE).edit().clear().apply();
                getContext().getSharedPreferences("authentication", Context.MODE_PRIVATE).edit().clear().apply();
                getContext().getSharedPreferences("msgRecieved", Context.MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(getActivity(),AuthenticationActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}