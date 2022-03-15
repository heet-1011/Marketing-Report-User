package com.hp.marketingreportuser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddUserTaskFragment extends Fragment {

    TextInputEditText txtInpEditTxtEmpName, txtInpEditTxtMobNo,txtInpEditTxtDate,txtInpEditTxtLocation;
    TextInputLayout txtInpLayoutStoreName, txtInpLayoutEmpName, txtInpLayoutMobNo;
    AutoCompleteTextView autoComTxtViewStoreName;
    MaterialButton btnSubmit, btnViewLocation;
    String storeName,empName,storeMobNo;
    Double latitude,longitude;
    ArrayList<String> storeList;

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private viewModel viewModel;
    Timestamp timestamp;

    public AddUserTaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_user_task, container, false);
        initViews(root);
        loadStoreNames();
        final Calendar c = Calendar.getInstance();
        Date dateRepresentation = c.getTime();
        int mYear = c.get(Calendar.YEAR);
        String mMonth = c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.ENGLISH).substring(0,3);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        timestamp = new Timestamp(dateRepresentation);
        txtInpEditTxtDate.setText(mDay+" "+mMonth+" "+mYear);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(viewModel.class);
        viewModel.getItem().observe(getActivity(), item->{
            txtInpEditTxtLocation.setText(item);
            btnViewLocation.setVisibility(View.VISIBLE);
        });
        viewModel.getLatitude().observe(getActivity(),latitude->{
            this.latitude = latitude;
        });
        viewModel.getLongitude().observe(getActivity(),longitude->{
            this.longitude = longitude;
        });
        txtInpEditTxtLocation.setOnClickListener(view1 -> {
            ((HomeActivity) getActivity()).getLocation();
        });
        btnViewLocation.setOnClickListener(view1 -> {
            if(txtInpEditTxtLocation.getText().toString().equals("Click to get Location")){
                Toast.makeText(getContext(),"Get Location First",Toast.LENGTH_SHORT).show();
            }else{
                String url = "https://www.google.com/maps/search/?api=1&query=" + latitude + "%2C" + longitude;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEnteredData(view);
            }
        });
    }


    private void getEnteredData(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.ProgressDialog);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Adding your task...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_Medium);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.color.primary));
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(storeNameValid() && empNameValid() && mobNoValid() && locValid()){
            String mobNo = ((HomeActivity)getActivity()).mobNo;
            Map<String,Object> taskDataMap = new HashMap<>();
            taskDataMap.put("storeName",storeName.toUpperCase(Locale.ROOT));
            taskDataMap.put("storeMobNo",storeMobNo);
            taskDataMap.put("empName",empName.toUpperCase(Locale.ROOT));
            taskDataMap.put("location",new GeoPoint(latitude,longitude));
            taskDataMap.put("date",timestamp);
            taskDataMap.put("salesmanMobNo",mobNo);
            taskDataMap.put("salesmanName",((HomeActivity)getActivity()).name.toUpperCase(Locale.ROOT));
            firebaseFirestore.collection("dailyReport").document().set(taskDataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    reset();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private boolean storeNameValid() {
        if(!autoComTxtViewStoreName.getText().toString().isEmpty()){
            storeName = autoComTxtViewStoreName.getText().toString().trim();
            return true;
        }else{
            txtInpLayoutStoreName.setError("Name field can't be empty");
            return false;
        }
    }

    private boolean empNameValid() {
        if(!txtInpEditTxtEmpName.getText().toString().isEmpty()){
            empName = txtInpEditTxtEmpName.getText().toString().trim();
            return true;
        }else{
            txtInpLayoutEmpName.setError("Name field can't be empty");
            return false;
        }
    }

    private boolean mobNoValid() {
        if(!txtInpEditTxtMobNo.getText().toString().isEmpty() && txtInpEditTxtMobNo.getText().toString().trim().length()==10){
            storeMobNo = txtInpEditTxtMobNo.getText().toString().trim();
            return true;
        }else{
            txtInpLayoutMobNo.setError("Invalid Mobile No.");
            return false;
        }
    }

    private boolean locValid() {
        if(!txtInpEditTxtLocation.getText().toString().isEmpty()){
            return true;
        }else{
            Toast.makeText(getContext(),"Get Location First",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void reset(){
        autoComTxtViewStoreName.setText(null);
        txtInpEditTxtEmpName.setText(null);
        txtInpEditTxtMobNo.setText(null);
        txtInpEditTxtLocation.setText("Click to get Location");
    }
    private void initViews(View root) {
        txtInpLayoutStoreName = root.findViewById(R.id.txtInpLayoutStoreName);
        txtInpLayoutEmpName = root.findViewById(R.id.txtInpLayoutEmpName);
        txtInpLayoutMobNo = root.findViewById(R.id.txtInpLayoutMobNo);
        autoComTxtViewStoreName = root.findViewById(R.id.autoComTxtViewStoreName);
        txtInpEditTxtEmpName = root.findViewById(R.id.txtInpEditTxtEmpName);
        txtInpEditTxtMobNo = root.findViewById(R.id.txtInpEditTxtMobNo);
        btnSubmit = root.findViewById(R.id.btnSubmit);
        btnViewLocation = root.findViewById(R.id.btnViewLocation);
        txtInpEditTxtLocation = root.findViewById(R.id.txtInpEditTxtLocation);
        txtInpEditTxtDate = root.findViewById(R.id.txtInpEditTxtDate);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        reset();
    }


    private void loadStoreNames() {
        storeList = new ArrayList<String>();
        firebaseFirestore.collection("dailyReport").orderBy("storeName").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : list) {
                        if (!storeList.contains(documentSnapshot.getString("storeName"))) {
                            storeList.add(documentSnapshot.getString("storeName"));
                        }
                }
            }
        });
        ArrayAdapter storeNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,storeList);
        autoComTxtViewStoreName.setAdapter(storeNameAdapter);
        autoComTxtViewStoreName.setThreshold(3);
        autoComTxtViewStoreName.setDropDownBackgroundDrawable(getResources().getDrawable(R.color.black));
    }
}