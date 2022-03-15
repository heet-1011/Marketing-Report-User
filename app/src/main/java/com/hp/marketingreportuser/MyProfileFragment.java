package com.hp.marketingreportuser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class MyProfileFragment extends Fragment {
    TextInputEditText txtInpEditTxtName, txtInpEditTxtMobNo, txtInpEditTxtEmail, txtInpEditTxtDob,txtInpEditTxtRouteAssign;

    public MyProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initViews(root);
        loadData();
        return root;
    }
    private void loadData() {
        txtInpEditTxtName.setText(((HomeActivity) getActivity()).name.toUpperCase(Locale.ROOT));
        txtInpEditTxtMobNo.setText(((HomeActivity) getActivity()).mobNo);
        txtInpEditTxtEmail.setText(((HomeActivity) getActivity()).email);
        txtInpEditTxtDob.setText(((HomeActivity) getActivity()).dob);
        txtInpEditTxtRouteAssign.setText(((HomeActivity)getActivity()).routeAssign);
    }

    private void initViews(View root) {
        txtInpEditTxtName = root.findViewById(R.id.txtInpEditTxtName);
        txtInpEditTxtMobNo = root.findViewById(R.id.txtInpEditTxtMobNo);
        txtInpEditTxtEmail = root.findViewById(R.id.txtInpEditTxtEmail);
        txtInpEditTxtDob = root.findViewById(R.id.txtInpEditTxtDob);
        txtInpEditTxtRouteAssign = root.findViewById(R.id.txtInpEditTxtRouteAssign);
    }
}