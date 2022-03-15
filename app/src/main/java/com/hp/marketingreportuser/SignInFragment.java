package com.hp.marketingreportuser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignInFragment extends Fragment {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextInputEditText txtInpEditTxtMobNo, txtInpEditTxtPwd;
    private TextInputLayout txtInpLayoutMobNo, txtInpLayoutPwd;
    private MaterialButton btnSignIn;
    private String mobNo,pwd;
    public String title,msg;
    private LinearProgressIndicator prgIndicator;
    TextView txtViewFgtPwd;
    private View root;
    public SignInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_sign_in, container, false);
        initViews(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSignIn.setOnClickListener(view1 -> {
            if(((AuthenticationActivity)getActivity()).chkInternetSpeed()){
                getEnteredData();
            }
        });
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
        txtInpEditTxtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtInpLayoutPwd.setErrorEnabled(false);
                txtInpLayoutPwd.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        txtViewFgtPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.action_SignInFragment_to_FgtPwdFragment);
            }
        });
    }

    private void initViews(View root) {
        txtInpLayoutMobNo = root.findViewById(R.id.txtInpLayoutMobNo);
        txtInpLayoutPwd = root.findViewById(R.id.txtInpLayoutPwd);
        txtInpEditTxtMobNo = root.findViewById(R.id.txtInpEditTxtMobNo);
        txtInpEditTxtPwd = root.findViewById(R.id.txtInpEditTxtPwd);
        btnSignIn = root.findViewById(R.id.btnSignIn);
        prgIndicator = root.findViewById(R.id.prgIndicator);
        txtViewFgtPwd = root.findViewById(R.id.txtViewFgtPwd);
    }

    private void getEnteredData() {
        prgIndicator.setIndeterminate(true);
        if (mobNoValid() && pwdValid()){
            firebaseFirestore.collection("marketingPerson").document("+91"+mobNo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(getActivity(), "marketingPerson found yourpwd "+pwd+"getpwd "+documentSnapshot.getString("pwd"), Toast.LENGTH_LONG).show();
                        Log.v("hp", "admin found yourpwd "+pwd+"getpwd "+documentSnapshot.getString("name"));
                        if(pwd.equals(documentSnapshot.getString("pwd"))){
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("authentication", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("mobNo","+91"+mobNo);
                            editor.putString("pwd",pwd);
                            editor.apply();
                            SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                            editor1.putString("name",documentSnapshot.getString("name"));
                            editor1.putString("mobNo","+91"+mobNo);
                            editor1.putString("email",documentSnapshot.getString("email"));
                            editor1.putString("verificationDoc",documentSnapshot.getString("verificationDoc"));
                            Timestamp timestamp = (Timestamp) documentSnapshot.get("dob");
                            String[] dateParams = timestamp.toDate().toString().split(" ");
                            String dob = dateParams[2]+" "+dateParams[1]+" "+dateParams[5];
                            editor1.putString("dob",dob);
                            editor1.putString("routeAssign",documentSnapshot.getString("routeAssign"));
                            editor1.apply();
                            prgIndicator.setIndeterminate(false);
                            Intent intent = new Intent(getActivity(),HomeActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }else{
                            prgIndicator.setIndeterminate(false);
                            txtInpLayoutPwd.setError("Wrong Password!");
                        }
                    } else {
                        prgIndicator.setIndeterminate(false);
                        Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_LONG).show();
                    }
                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            firebaseFirestore.collection("marketingPerson").document("+91"+mobNo).update("fcmToken",s);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    prgIndicator.setIndeterminate(false);
                }
            });
        }
        else{
            prgIndicator.setIndeterminate(false);
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

    private boolean pwdValid() {
        if (!txtInpEditTxtPwd.getText().toString().isEmpty()) {
            pwd = txtInpEditTxtPwd.getText().toString().trim();
            Log.v("hpname", "'" + pwd + "'");
            return true;
        } else {
            txtInpLayoutPwd.setError("Password field can't be empty.");
            return false;
        }
    }


}