package com.hp.marketingreportuser;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserTimelineFragment extends Fragment {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView recViewUserTimeline;
    ArrayList<userTimelineModel> userTimelineList;
    userTimelineAdapter userTimelineAdapter;
    TextInputEditText txtInpEditTxtDate;
    TextInputLayout txtInpLayoutDate;
    String date="", mobNo,storeName="",sort="date";
    ConstraintLayout constraintLayNoData;
    Timestamp timestamp;
    MaterialButton btnFilterTimeline,btnAddFilter,btnAddSort,btnSortTimeline;
    BottomSheetDialog bottomSheetDialogFilter,bottomSheetDialogSort;
    AutoCompleteTextView autoComTxtViewStoreName,autoComTxtViewOrder,autoComTxtViewSortBy;
    Chip storeNameChip,dateChip;
    ChipGroup chipGrpFilter;
    ArrayList<String> storeNames, storeLatitude, storeLongitude, storeEmployee, storeMobNo, visitDate;
    ArrayAdapter<String> storeNameAdapter;
    public Query.Direction order = Query.Direction.DESCENDING;
    TextView txtViewReset,txtviewResetSort,txtViewNoData;
    SwipeRefreshLayout swipeRefreshLayout;
    ExtendedFloatingActionButton efabLocation;

    public UserTimelineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_timeline, container, false);
        constraintLayNoData = root.findViewById(R.id.constraintLayNoData);
        recViewUserTimeline = root.findViewById(R.id.recViewUserTimeline);
        btnFilterTimeline = root.findViewById(R.id.btnFilterTimeline);
        recViewUserTimeline.setLayoutManager(new LinearLayoutManager(getContext()));
        mobNo = ((HomeActivity) getActivity()).mobNo;
        Log.v("mobNo",mobNo);
        chkFilterChip();
        loadData();
        bottomSheetDialogFilter = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        bottomSheetDialogFilter.setContentView(R.layout.layout_timeline_filter);
        btnAddFilter = bottomSheetDialogFilter.findViewById(R.id.btnAddFilter);
        txtInpEditTxtDate = bottomSheetDialogFilter.findViewById(R.id.txtInpEditTxtDate);
        txtInpLayoutDate = bottomSheetDialogFilter.findViewById(R.id.txtInpLayoutDate);
        txtViewNoData = root.findViewById(R.id.txtViewNoData);
        autoComTxtViewStoreName = (AutoCompleteTextView) bottomSheetDialogFilter.findViewById(R.id.autoComTxtViewStoreName);
        bottomSheetDialogSort = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        bottomSheetDialogSort.setContentView(R.layout.layout_sort);
        txtViewReset = bottomSheetDialogFilter.findViewById(R.id.txtViewReset);
        autoComTxtViewOrder = bottomSheetDialogSort.findViewById(R.id.autoComTxtViewOrder);
        autoComTxtViewSortBy = bottomSheetDialogSort.findViewById(R.id.autoComTxtViewSortBy);
        txtviewResetSort = bottomSheetDialogSort.findViewById(R.id.txtViewResetSort);
        btnAddSort = bottomSheetDialogSort.findViewById(R.id.btnAddSort);
        btnSortTimeline = root.findViewById(R.id.btnSortTimeline);
        chipGrpFilter = root.findViewById(R.id.chipGrpFilter);
        swipeRefreshLayout = root.findViewById(R.id.SwipeRefreshLayout);
        efabLocation = root.findViewById(R.id.efabLocation);
        storeNameChip = new Chip(getContext());
        dateChip = new Chip(getContext());
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<CharSequence> order = ArrayAdapter.createFromResource(getContext(),R.array.order, android.R.layout.simple_spinner_dropdown_item);
        autoComTxtViewOrder.setAdapter(order);
        ArrayAdapter<CharSequence> sort = ArrayAdapter.createFromResource(getContext(),R.array.timelineSort, android.R.layout.simple_spinner_dropdown_item);
        autoComTxtViewSortBy.setAdapter(sort);
        autoComTxtViewOrder.setDropDownBackgroundDrawable(getResources().getDrawable(R.color.black));
        autoComTxtViewSortBy.setDropDownBackgroundDrawable(getResources().getDrawable(R.color.black));
        txtInpLayoutDate.setOnClickListener(view1 -> {
            datePicker();
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        txtInpEditTxtDate.setOnClickListener(view1 -> {
            datePicker();
        });
        btnFilterTimeline.setOnClickListener(view1 -> {
            bottomSheetDialogFilter.show();
        });
        btnSortTimeline.setOnClickListener(view1 -> {
            bottomSheetDialogSort.show();
        });
        storeNameChip.setOnCloseIconClickListener(view1 -> {
            storeName="";
            loadData();
            autoComTxtViewStoreName.setText(null);
            chipGrpFilter.removeView(storeNameChip);
        });
        dateChip.setOnCloseIconClickListener(view1 -> {
            date="";
            loadData();
            txtInpEditTxtDate.setText(null);
            chipGrpFilter.removeView(dateChip);
        });
        btnAddFilter.setOnClickListener(view1 -> {
            storeName = autoComTxtViewStoreName.getText().toString().toUpperCase(Locale.ROOT);
            date = txtInpEditTxtDate.getText().toString();
            chkFilterChip();
            loadData();
            bottomSheetDialogFilter.dismiss();
        });
        btnAddSort.setOnClickListener(view1 -> {
            switch (autoComTxtViewSortBy.getText().toString()){
                case "Date":
                    this.sort = "date";
                    break;
                case "Employee Name":
                    this.sort = "empName";
                    break;
                case "Store Name":
                    this.sort = "storeName";
                    break;
            }
            if(autoComTxtViewOrder.getText().toString().equals("Ascending")){
                this.order = Query.Direction.ASCENDING;
            }else{
                this.order = Query.Direction.DESCENDING;
            }
            loadData();
        });
        txtviewResetSort.setOnClickListener(view1 -> {
            autoComTxtViewOrder.setText("Descending");
            autoComTxtViewSortBy.setText("Date");
            this.sort="date";
            this.order= Query.Direction.DESCENDING;
            loadData();
        });
        txtViewReset.setOnClickListener(view1 -> {
            txtInpEditTxtDate.setText(null);
            autoComTxtViewStoreName.setText(null);
            loadData();
        });
        txtInpEditTxtDate.setOnClickListener(view1 -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, monthOfYear);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            Date dateRepresentations = c.getTime();
                            timestamp = new Timestamp(dateRepresentations);
                            String[] dateParams = timestamp.toDate().toString().split(" ");
                            txtInpEditTxtDate.setText(dateParams[2]+" "+dateParams[1]+" "+dateParams[5]);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });
        efabLocation.setOnClickListener(view1 -> {
            Bundle dataBundle = new Bundle();
            dataBundle.putStringArrayList("storeNames", storeNames);
            dataBundle.putStringArrayList("storeLatitude", storeLatitude);
            dataBundle.putStringArrayList("storeLongitude", storeLongitude);
            dataBundle.putStringArrayList("visitDate", visitDate);
            dataBundle.putStringArrayList("storeMobNo", storeMobNo);
            dataBundle.putStringArrayList("storeEmployee", storeEmployee);
            Navigation.findNavController(view).navigate(R.id.MapsFragment, dataBundle);
        });
    }
    private void chkFilterChip() {
        try {
            chipGrpFilter.removeView(storeNameChip);
            chipGrpFilter.removeView(dateChip);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!storeName.isEmpty()){
            addFilterChip(storeNameChip,storeName);
        }
        if(!date.isEmpty()){
            addFilterChip(dateChip,date);
        }
    }

    private void addFilterChip(Chip chip, String chipText) {
        chip.setText(chipText);
        chip.setChipBackgroundColorResource(R.color.white);
        chip.setCloseIconVisible(true);
        chip.setTextColor(getResources().getColor(R.color.black));
        chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        chipGrpFilter.addView(chip);
    }

    public void loadData() {
        userTimelineList = new ArrayList<>();
        userTimelineAdapter = new userTimelineAdapter(userTimelineList, getActivity(), constraintLayNoData);
        recViewUserTimeline.setAdapter(userTimelineAdapter);
        firebaseFirestore.collection("dailyReport").orderBy(sort,order).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                storeNames =  new ArrayList<>();
                storeLatitude = new ArrayList<>();
                storeLongitude = new ArrayList<>();
                storeEmployee = new ArrayList<>();
                storeMobNo = new ArrayList<>();
                visitDate = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : list) {
                    Timestamp ctimestamp = (Timestamp) documentSnapshot.get("date");
                    String[] dateParams = ctimestamp.toDate().toString().split(" ");
                    String taskDate = dateParams[2]+" "+dateParams[1]+" "+dateParams[5];
                    if(documentSnapshot.getString("salesmanMobNo").contains(mobNo) && taskDate.contains(date) &&
                            documentSnapshot.getString("storeName").contains(storeName)){
                        if(!storeNames.contains(documentSnapshot.getString("storeName"))){
                            storeNames.add(documentSnapshot.getString("storeName"));
                            storeLatitude.add(String.valueOf(documentSnapshot.getGeoPoint("location").getLatitude()));
                            storeLongitude.add(String.valueOf(documentSnapshot.getGeoPoint("location").getLongitude()));
                            storeEmployee.add(documentSnapshot.getString("empName"));
                            storeMobNo.add(documentSnapshot.getString("storeMobNo"));
                            visitDate.add(taskDate);
                        }
                        userTimelineModel obj = documentSnapshot.toObject(userTimelineModel.class);
                        userTimelineList.add(obj);
                    }
                }
                if(userTimelineList.size()==0){
                    Log.v("heetnotify","none");
                    txtViewNoData.setVisibility(View.VISIBLE);
                    recViewUserTimeline.setVisibility(View.GONE);
                }else{
                    Log.v("heetnotify","data");
                    txtViewNoData.setVisibility(View.GONE);
                    recViewUserTimeline.setVisibility(View.VISIBLE);
                }
                storeNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,storeNames);
                autoComTxtViewStoreName.setAdapter(storeNameAdapter);
                autoComTxtViewStoreName.setThreshold(2);
                autoComTxtViewStoreName.setDropDownBackgroundDrawable(getResources().getDrawable(R.color.black));
                userTimelineAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void datePicker(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date dateRepresentations = c.getTime();
                        timestamp = new Timestamp(dateRepresentations);
                        String[] dateParams = timestamp.toDate().toString().split(" ");
                        txtInpEditTxtDate.setText(dateParams[2]+" "+dateParams[1]+" "+dateParams[5]);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}