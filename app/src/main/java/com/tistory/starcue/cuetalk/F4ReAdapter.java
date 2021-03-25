package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class F4ReAdapter extends RecyclerView.Adapter<F4ReAdapter.CustomViewHolder> {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private ArrayList<F4MessegeItem> arrayList;
    private ArrayList<LastListItem> lastList;
    private Context context;

    FirebaseAuth mAuth;
    String myUid;

    GpsTracker gpsTracker;

    F4ReAdapter(ArrayList<F4MessegeItem> arrayList, ArrayList<LastListItem> lastList, Context context) {
        this.arrayList = arrayList;
        this.lastList = lastList;
        this.context = context;
    }

    @NonNull
    @Override
    public F4ReAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment4_recyclerview_layout, parent, false);
        F4ReAdapter.CustomViewHolder holder = new F4ReAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull F4ReAdapter.CustomViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        if (arrayList.get(position).getPic() == null) {
            if (arrayList.get(position).getSex().equals("남자")) {
                Glide.with(holder.pic).load(nullPic).override(150, 150).circleCrop().into(holder.pic);
            } else {
                Glide.with(holder.pic).load(nullPicF).override(150, 150).circleCrop().into(holder.pic);
            }
        } else {
            Glide.with(holder.pic).load(arrayList.get(position).getPic()).override(150, 150).circleCrop().into(holder.pic);
        }

        holder.name.setText(arrayList.get(position).getName());
        holder.sex.setText(arrayList.get(position).getSex());
        holder.age.setText(arrayList.get(position).getAge());
        holder.time.setText(lastList.get(position).getLasttime());
        holder.messege.setText(lastList.get(position).getLastmessege());

        String latitudeS = arrayList.get(position).getLatitude();//set km
        String longitudeS = arrayList.get(position).getLongitude();
        double latitude = Double.parseDouble(latitudeS);
        double longitude = Double.parseDouble(longitudeS);
        gpsTracker = new GpsTracker(context);
        double myLatitude = gpsTracker.getLatitude();
        double myLongitude = gpsTracker.getLongitude();


        double ddd = getDistance(myLatitude, myLongitude, latitude, longitude);
        if (arrayList.get(position).getUid().equals(myUid)) {
            holder.km.setText("0m");
        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 50) {
            holder.km.setText("-50m");
        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 1000) {
            int i = (int) Math.floor(ddd);
            holder.km.setText(Integer.toString(i) + "m");
        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd >= 1000) {
            int i = (int) Math.floor(ddd) / 1000;
            holder.km.setText(Integer.toString(i) + "km");
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, arrayList.get(position).getUid(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name, sex, age, km, messege, time;
        RelativeLayout relativeLayout;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.pic = itemView.findViewById(R.id.f4re_pic);
            this.name = itemView.findViewById(R.id.f4re_name);
            this.sex = itemView.findViewById(R.id.f4re_sex);
            this.age = itemView.findViewById(R.id.f4re_age);
            this.km = itemView.findViewById(R.id.f4re_km);
            this.messege = itemView.findViewById(R.id.f4re_messege);
            this.time = itemView.findViewById(R.id.f4re_time);
            this.relativeLayout = itemView.findViewById(R.id.f4_recyclerview);
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
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
}
