package com.hp.marketingreportuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    LineChart lineChart;
    TextView txtViewTodaysVisits, txtViewTotalVisits, txtViewStores, txtViewMostVisited;
    TextInputLayout txtInpLayoutYear;
    TextInputEditText txtInpEditTxtYear;
    float Jan = 0, Feb = 0, Mar = 0, Apr = 0, May = 0, Jun = 0, Jul = 0, Aug = 0, Sep = 0, Oct = 0, Nov = 0, Dec = 0;
    SwipeRefreshLayout SwipeRefreshLayout;
    View root;
    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        lineChart = root.findViewById(R.id.lineChart);
        txtViewTotalVisits = root.findViewById(R.id.txtViewTotalVisits);
        txtViewTodaysVisits = root.findViewById(R.id.txtViewTodaysVisits);
        txtViewStores = root.findViewById(R.id.txtViewStores);
        SwipeRefreshLayout = root.findViewById(R.id.SwipeRefreshLayout);
        txtInpLayoutYear = root.findViewById(R.id.txtInpLayoutYear);
        txtInpEditTxtYear = root.findViewById(R.id.txtInpEditTxtYear);
        txtInpEditTxtYear.setText(String.valueOf(Calendar.getInstance().getWeekYear()));
        ((HomeActivity)getActivity()).loadData(String.valueOf(txtInpEditTxtYear.getText()));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel viewModel = new ViewModelProvider(getActivity()).get(viewModel.class);
        viewModel.getTotalStores().observe(getActivity(), item -> {
            txtViewStores.setText(item.toString());
        });
        viewModel.getTodayVisits().observe(getActivity(), item -> {
            txtViewTodaysVisits.setText(item.toString());
        });
        viewModel.getTotalVisits().observe(getActivity(), item -> {
            txtViewTotalVisits.setText(item.toString());
        });
        viewModel.getMonthlyDataCount().observe(getActivity(), item ->{
            Jan = item.get("Jan");
            Feb = item.get("Feb");
            Mar = item.get("Mar");
            Apr = item.get("Apr");
            May = item.get("May");
            Jun = item.get("Jun");
            Jul = item.get("Jul");
            Aug = item.get("Aug");
            Sep = item.get("Sep");
            Oct = item.get("Oct");
            Nov = item.get("Nov");
            Dec = item.get("Dec");
            loadGraphData();
        });

        txtInpEditTxtYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearPicker();
            }
        });
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((HomeActivity) requireActivity()).loadData(String.valueOf(txtInpEditTxtYear.getText()));
            }
        });
    }

    private void loadGraphData() {
        LineDataSet lineDataSet = new LineDataSet(dataValue(), "Visits Per Month");
        lineDataSet.setLineWidth(3);
        lineDataSet.setColor(getResources().getColor(R.color.primary));
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setCircleColors(getResources().getColor(R.color.primary));
        lineDataSet.setCircleHoleColor(getResources().getColor(R.color.primary));
        lineDataSet.setValueTextSize(12);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.graph_gradient));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        Description description = new Description();
        description.setText("");
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(getResources().getColor(R.color.black));
        legend.setTextSize(16);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.setBackground(getResources().getDrawable(R.drawable.round_bg));
        lineChart.setPadding(10, 10, 10, 10);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setLabelCount(13);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisLeft().setGranularity(1.0f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setStartAtZero(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setVisibleYRangeMinimum(0, YAxis.AxisDependency.LEFT);
        SwipeRefreshLayout.setRefreshing(false);
    }

    private List<Entry> dataValue() {
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.add(new Entry(1, Jan));
        dataValues.add(new Entry(2, Feb));
        dataValues.add(new Entry(3, Mar));
        dataValues.add(new Entry(4, Apr));
        dataValues.add(new Entry(5, May));
        dataValues.add(new Entry(6, Jun));
        dataValues.add(new Entry(7, Jul));
        dataValues.add(new Entry(8, Aug));
        dataValues.add(new Entry(9, Sep));
        dataValues.add(new Entry(10, Oct));
        dataValues.add(new Entry(11, Nov));
        dataValues.add(new Entry(12, Dec));
        return dataValues;
    }

    public void yearPicker(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        final View customLayout = getLayoutInflater().inflate(R.layout.layout_year_picker, null);
        NumberPicker numberPicker = customLayout.findViewById(R.id.yearPicker);
        numberPicker.setMinValue(2000);
        numberPicker.setMaxValue(2100);
        numberPicker.setValue(Integer.parseInt(txtInpEditTxtYear.getText().toString().trim()));
        MaterialButton btnSubmit = customLayout.findViewById(R.id.btnSubmit);
        alertDialog.setView(customLayout);
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                txtInpEditTxtYear.setText(String.valueOf(numberPicker.getValue()));
                ((HomeActivity)getActivity()).loadData(String.valueOf(numberPicker.getValue()));
            }
        });
    }

}