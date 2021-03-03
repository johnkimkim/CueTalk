package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.CustomViewHolder>{

    private ArrayList<AdressRoomItem> bottomList;
    private GpsTracker gpsTracker;
    private Context context;

    BottomSheetAdapter(ArrayList<AdressRoomItem> bottomList, Context context) {
        this.bottomList = bottomList;
        this.context = context;
    }

    @NonNull
    @Override
    public BottomSheetAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_list_layout, parent, false);
        BottomSheetAdapter.CustomViewHolder holder = new BottomSheetAdapter.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetAdapter.CustomViewHolder holder, int position) {
        Glide.with(holder.imageView)
                .load(bottomList.get(position).getPic())
                .override(150, 150)
                .circleCrop()
                .into(holder.imageView);
        holder.name.setText(bottomList.get(position).getName());
        holder.sex.setText(bottomList.get(position).getSex());
        holder.age.setText(bottomList.get(position).getAge());

        String latitudeS = bottomList.get(position).getLatitude();//set km
        String longitudeS = bottomList.get(position).getLongitude();
        double latitude = Double.parseDouble(latitudeS);
        double longitude = Double.parseDouble(longitudeS);
        gpsTracker = new GpsTracker(context);
        double myLatitude = gpsTracker.getLatitude();
        double myLongitude = gpsTracker.getLongitude();

        int i = (int) Math.floor(getDistance(myLatitude, myLongitude, latitude, longitude));
        String howkm = Integer.toString(i);
        holder.km.setText(howkm + "km");
    }

    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    @Override
    public int getItemCount() {
        return (bottomList != null ? bottomList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView sex;
        TextView age;
        TextView km;
        Button btn;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.bottom_sheet_layout_pic);
            this.name = itemView.findViewById(R.id.bottom_sheet_layout_name);
            this.sex = itemView.findViewById(R.id.bottom_sheet_layout_sex);
            this.age = itemView.findViewById(R.id.bottom_sheet_layout_age);
            this.km = itemView.findViewById(R.id.bottom_sheet_layout_km);
            this.btn = itemView.findViewById(R.id.bottom_sheet_layout_chatbtn);
        }
    }

}
