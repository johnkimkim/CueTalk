package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class AdressRoomAdapter extends RecyclerView.Adapter<AdressRoomAdapter.CustomViewHolder> {

    private ArrayList<AdressRoomItem> arrayList;
    private Context context;
    private Intent intent;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference reference;

    private GpsTracker gpsTracker;

    double latitude;
    double longitude;
    String name, sex, age, pic;

    AdressRoomAdapter(ArrayList<AdressRoomItem> arrayList, Context context, Intent intent) {
        this.arrayList = arrayList;
        this.context = context;
        this.intent = intent;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        Context context = parent.getContext();
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adress_room_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
        Glide.with(holder.imageView)
                .load(arrayList.get(position).getPic())
                .override(150, 150)
                .circleCrop()
                .into(holder.imageView);
        holder.name.setText(arrayList.get(position).getName());
        holder.sex.setText(arrayList.get(position).getSex());
        holder.age.setText(arrayList.get(position).getAge());


        String latitudeS = arrayList.get(position).getLatitude();//set km
        String longitudeS = arrayList.get(position).getLongitude();
        double latitude = Double.parseDouble(latitudeS);
        double longitude = Double.parseDouble(longitudeS);
        gpsTracker = new GpsTracker(context);
        double myLatitude = gpsTracker.getLatitude();
        double myLongitude = gpsTracker.getLongitude();

        int i = (int) Math.floor(getDistance(myLatitude, myLongitude, latitude, longitude));
//        String howkm = Double.toString(Math.floor(getDistance(myLatitude, myLongitude, latitude, longitude)));
        String howkm = Integer.toString(i);

        holder.km.setText(howkm + "km");

//        holder.km.setText(howkm);
//        Log.d("AdressRoomAdapter>>>", howkm);//set km

        mAuth = FirebaseAuth.getInstance();
        String myUid = mAuth.getUid();
        if (arrayList.get(position).getUid().equals(myUid)) {
            holder.btn.setEnabled(false);
            holder.btn.setBackgroundColor(Color.GRAY);
        }

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useruid = arrayList.get(position).getUid();
                Log.d("AdressRoomAdapter>>>", "userUid: " + useruid);
                updateAdressRoom(myUid, useruid);
            }
        });

    }

    private void updateAdressRoom(String myUid, String yourUid) {
        reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/chatting/" + "/" + myUid + "/" + "/" + yourUid + "/", yourUid);
        reference.updateChildren(updateUser);
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
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        TextView sex;
        TextView age;
        TextView km;
        Button btn;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.adress_room_layout_pic);
            this.name = itemView.findViewById(R.id.adress_room_layout_name);
            this.sex = itemView.findViewById(R.id.adress_room_layout_sex);
            this.age = itemView.findViewById(R.id.adress_room_layout_age);
            this.km = itemView.findViewById(R.id.adress_room_layout_km);
            this.btn = itemView.findViewById(R.id.adress_room_layout_chatbtn);
        }
    }
}
