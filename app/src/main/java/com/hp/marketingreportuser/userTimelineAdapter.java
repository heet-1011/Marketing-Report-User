package com.hp.marketingreportuser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.DateTime;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class userTimelineAdapter extends RecyclerView.Adapter<userTimelineAdapter.myViewHolder> {

    ConstraintLayout constraintLayNoData;
    ArrayList<userTimelineModel> userTimelineModelList;
    FragmentActivity activity;
    public userTimelineAdapter(ArrayList<userTimelineModel> userTimelineModelList, FragmentActivity activity, ConstraintLayout constraintLayNoData) {
        this.userTimelineModelList = userTimelineModelList;
        this.activity = activity;
        this.constraintLayNoData = constraintLayNoData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        if(userTimelineModelList.size()!=0){
            holder.constraintLayTimeline.setAnimation(AnimationUtils.loadAnimation(activity,R.anim.recycler_view_animation));
            holder.txtViewStoreName.setText(userTimelineModelList.get(position).getStoreName().toUpperCase(Locale.ROOT));
            holder.txtViewStoreMobNo.setText(userTimelineModelList.get(position).getStoreMobNo());
            holder.txtViewemployeename.setText(userTimelineModelList.get(position).getEmpName());
            Timestamp timestamp = userTimelineModelList.get(position).getDate();
            String[] dateParams = timestamp.toDate().toString().split(" ");
            holder.txtViewDate.setText(dateParams[2]+" "+dateParams[1]+" "+dateParams[5]);
            holder.imgBtnLocation.setOnClickListener(view -> {
                ArrayList<String> storeNames = new ArrayList<>();
                storeNames.add(userTimelineModelList.get(position).getStoreName().toUpperCase(Locale.ROOT));
                ArrayList<String> storeLatitude = new ArrayList<>();
                storeLatitude.add(String.valueOf(userTimelineModelList.get(position).getLocation().getLatitude()));
                ArrayList<String> storeLongitude = new ArrayList<>();
                storeLongitude.add(String.valueOf(userTimelineModelList.get(position).getLocation().getLongitude()));
                ArrayList<String> visitDate = new ArrayList<>();
                visitDate.add(dateParams[2]+" "+dateParams[1]+" "+dateParams[5]);
                ArrayList<String> storeMobNo = new ArrayList<>();
                storeMobNo.add(userTimelineModelList.get(position).getStoreMobNo());
                ArrayList<String> storeEmployee = new ArrayList<>();
                storeEmployee.add(userTimelineModelList.get(position).getEmpName());
                Bundle dataBundle = new Bundle();
                dataBundle.putStringArrayList("storeNames",storeNames);
                dataBundle.putStringArrayList("storeLatitude",storeLatitude);
                dataBundle.putStringArrayList("storeLongitude",storeLongitude);
                dataBundle.putStringArrayList("visitDate",visitDate);
                dataBundle.putStringArrayList("storeMobNo",storeMobNo);
                dataBundle.putStringArrayList("storeEmployee",storeEmployee);
                Navigation.findNavController(view).navigate(R.id.MapsFragment,dataBundle);
            });
        }else{
            constraintLayNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return userTimelineModelList.size();
    }


    class myViewHolder extends RecyclerView.ViewHolder{
        TextView txtViewStoreName,txtViewStoreMobNo,txtViewDate,txtViewemployeename;
        ImageButton imgBtnLocation;
        ConstraintLayout constraintLayTimeline;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewStoreName = itemView.findViewById(R.id.txtViewStoreName);
            txtViewStoreMobNo = itemView.findViewById(R.id.txtViewStoreMobNo);
            imgBtnLocation = itemView.findViewById(R.id.imgBtnLocation);
            txtViewDate = itemView.findViewById(R.id.txtViewDate);
            txtViewemployeename= itemView.findViewById(R.id.txtViewemployeename);
            constraintLayTimeline = itemView.findViewById(R.id.constraintLayTimeline);
        }
    }
}
