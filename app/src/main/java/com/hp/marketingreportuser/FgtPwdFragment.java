package com.hp.marketingreportuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FgtPwdFragment extends Fragment {

    TextInputLayout txtInpLayoutMobNo,txtInpLayoutText;
    TextInputEditText txtInpEditTxtMobNo,txtInpEditTxtText;
    MaterialButton btnSend;
    String mobNo,text;
    View root;

    public FgtPwdFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_fgt_pwd, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        txtInpLayoutMobNo = root.findViewById(R.id.txtInpLayoutMobNo);
        txtInpLayoutText = root.findViewById(R.id.txtInpLayoutText);
        txtInpEditTxtText = root.findViewById(R.id.txtInpEditTxtText);
        txtInpEditTxtMobNo = root.findViewById(R.id.txtInpEditTxtMobNo);
        btnSend = root.findViewById(R.id.btnSend);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSend.setOnClickListener(v-> getEnteredData());
        txtInpEditTxtMobNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtInpLayoutMobNo.setErrorEnabled(false);
                txtInpLayoutMobNo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        txtInpEditTxtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtInpLayoutText.setErrorEnabled(false);
                txtInpLayoutText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getEnteredData() {
        if (mobNoValid() && textValid()) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
            FirebaseFirestore.getInstance().collection("admin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : list) {
                        String fcmToken = documentSnapshot.getString("fcmToken");
                        FCMSend.pushNotification(getActivity(),getContext(), fcmToken, sharedPreferences.getString("name",""), "Salesman "+sharedPreferences.getString("name","")+"( "+sharedPreferences.getString("mobNo","")+" )"+" requesting forgot password with Message: "+text);
                    }
                }
            });
        }
    }



    private boolean mobNoValid() {
        if (!txtInpEditTxtMobNo.getText().toString().isEmpty()) {
            mobNo = txtInpEditTxtMobNo.getText().toString().trim();
            Log.v("hpname", "'" + mobNo + "'");
            if (mobNo.length() == 10) {
                if (Patterns.PHONE.matcher(mobNo).matches()) {
                    return true;
                } else {
                    txtInpLayoutMobNo.setError("Invalid Mobile No!");
                    return false;
                }
            } else {
                txtInpLayoutMobNo.setError("Mobile No. must be of 10 digit");
                return false;
            }

        } else {
            txtInpLayoutMobNo.setError("Mobile No field can't be empty.");
            return false;
        }
    }

    private boolean textValid() {
        if (!txtInpEditTxtText.getText().toString().isEmpty()) {
            text = txtInpEditTxtText.getText().toString().trim();
            return true;
        } else {
            txtInpLayoutText.setError("Password field can't be empty.");
            return false;
        }
    }
}