package com.tistory.starcue.cuetalk.adpater;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.fragment.Fragment4;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;
import com.tistory.starcue.cuetalk.Fragment4ChatRoom;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.item.LastListItem;
import com.tistory.starcue.cuetalk.R;

import java.util.ArrayList;
import java.util.List;

public class F4ReAdapter extends RecyclerView.Adapter<F4ReAdapter.CustomViewHolder> {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    ArrayList<F4MessegeItem> arrayList;
    ArrayList<LastListItem> lastList;
    List<String> keyList;
    List<String> countList;
    Context context;
    private DatabaseReference reference;
    private FirebaseFirestore db;

    FirebaseAuth mAuth;
    String myUid;

    GpsTracker gpsTracker;

    private RequestManager requestManager;

    public F4ReAdapter(ArrayList<F4MessegeItem> arrayList, ArrayList<LastListItem> lastList,
                       List<String> keyList, List<String> countList,
                       Context context, RequestManager requestManager) {
        this.arrayList = arrayList;
        this.lastList = lastList;
        this.keyList = keyList;
        this.countList = countList;
        this.context = context;
        this.requestManager = requestManager;
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

//        requestManager.load(arrayList.get(position).getPic())
//                .override(150, 150).circleCrop().into(holder.pic);
//
//        holder.name.setText(arrayList.get(position).getName());
//        holder.sex.setText(arrayList.get(position).getSex());
//        holder.age.setText(arrayList.get(position).getAge());

        String time = lastList.get(position).getLasttime();
        String time1 = time.substring(11);
        String time2 = time1.substring(0, time1.length() - 3);
        holder.time.setText(time2);

        holder.messege.setText(lastList.get(position).getLastmessege());

//        String latitudeS = arrayList.get(position).getLatitude();//set km
//        String longitudeS = arrayList.get(position).getLongitude();
//        double latitude = Double.parseDouble(latitudeS);
//        double longitude = Double.parseDouble(longitudeS);
//        gpsTracker = new GpsTracker(context);
//        double myLatitude = gpsTracker.getLatitude();
//        double myLongitude = gpsTracker.getLongitude();
//
//
//        double ddd = getDistance(myLatitude, myLongitude, latitude, longitude);
//        if (arrayList.get(position).getUid().equals(myUid)) {
//            holder.km.setText("0m");
//        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 50) {
//            holder.km.setText("-50m");
//        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 1000) {
//            int i = (int) Math.floor(ddd);
//            holder.km.setText(Integer.toString(i) + "m");
//        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd >= 1000) {
//            int i = (int) Math.floor(ddd) / 1000;
//            holder.km.setText(Integer.toString(i) + "km");
//        }

        if (countList.get(position).equals("0")) {
            holder.count.setText("");
        } else {
            holder.count.setText(countList.get(position));
        }

        db = FirebaseFirestore.getInstance();
        db.collection("users").document(arrayList.get(position).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = documentSnapshot.get("name").toString();
                String userSex = documentSnapshot.get("sex").toString();
                String userAge = documentSnapshot.get("age").toString();
                String userPic = documentSnapshot.get("pic").toString();
                String latitudeS = documentSnapshot.get("latitude").toString();
                String longitudeS = documentSnapshot.get("longitude").toString();
                //위에 6가지는 realtime messege에 추가할 필요없음. 지우기.(f4chatroom도 마찬가지.)
                holder.name.setText(userName);
                holder.sex.setText(userSex);
                holder.age.setText(userAge);
                requestManager.load(userPic)
                        .override(150, 150).circleCrop().into(holder.pic);

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
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.loading.setVisibility(View.VISIBLE);
                reference = FirebaseDatabase.getInstance().getReference();
                reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.getKey().contains(myUid) && dataSnapshot1.getKey().contains(arrayList.get(position).getUid())) {
                                Log.d("F4ReAdapter>>>", "key: " + dataSnapshot1.getKey());
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    if (dataSnapshot2.getKey().equals(arrayList.get(position).getUid())) {
                                        String yourPic = dataSnapshot2.child("pic").getValue(String.class);
                                        Toast.makeText(context, arrayList.get(position).getUid(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, Fragment4ChatRoom.class);
                                        intent.putExtra("child", arrayList.get(position).getUid());
                                        intent.putExtra("yourPic", yourPic);
                                        Fragment4.stayf4chatroom = true;
                                        MainActivity.loading.setVisibility(View.GONE);
                                        context.startActivity(intent);
                                    }
                                }
                                break;
                            }
                        }
                    }
                });

            }
        });

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name, sex, age, km, messege, time, count;
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
            this.count = itemView.findViewById(R.id.f4re_count);

        }
    }

    @Override
    public int getItemCount() {
        return (lastList != null ? lastList.size() : 0);
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
