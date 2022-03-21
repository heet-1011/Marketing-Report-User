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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SettingsFragment extends Fragment {

    MaterialButton btnLogOut,btnEditProfile,btnChangePwd;
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
        btnChangePwd = root.findViewById(R.id.btnChangePwd);
        btnEditProfile = root.findViewById(R.id.btnEditProfile);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile",Context.MODE_PRIVATE);
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
        btnEditProfile.setOnClickListener(view1 -> {
            FirebaseFirestore.getInstance().collection("admin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : list) {
                        String fcmToken = documentSnapshot.getString("fcmToken");
                        FCMSend.pushNotification(getActivity(),getContext(), fcmToken, sharedPreferences.getString("name",""), "Salesman "+sharedPreferences.getString("name","")+"( "+sharedPreferences.getString("mobNo","")+" )"+" requesting for editing profile");
                    }
                }
            });
        });
        btnChangePwd.setOnClickListener(view1 -> {
            FirebaseFirestore.getInstance().collection("admin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : list) {
                        String fcmToken = documentSnapshot.getString("fcmToken");
                        FCMSend.pushNotification(getActivity(),getContext(), fcmToken, sharedPreferences.getString("name",""), "Salesman "+sharedPreferences.getString("name","")+"( "+sharedPreferences.getString("mobNo","")+" )"+" requesting for changing password");
                    }
                }
            });
        });
    }
}