package com.tistory.starcue.cuetalk.adpater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.Fragment4ChatRoom;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.fragment.Fragment4;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;
import com.tistory.starcue.cuetalk.item.LastListItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class F4ReAdapter extends RecyclerView.Adapter<F4ReAdapter.CustomViewHolder> {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";
    String nullUser = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullUser.png?alt=media&token=4c9daa69-6d03-4b19-a793-873f5739f3a1";

    Activity activity;

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
                       Context context, RequestManager requestManager,
                       Activity activity) {
        this.arrayList = arrayList;
        this.lastList = lastList;
        this.keyList = keyList;
        this.countList = countList;
        this.context = context;
        this.requestManager = requestManager;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment4_recyclerview_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(arrayList.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") == null) {
                        Log.d("F4ReAdapter>>>", "snapshot null");
                        holder.name.setText("(알수없음)");
                        holder.sex.setText("");
                        holder.age.setText("");
                        requestManager.load(nullUser)
                                .override(150, 150).circleCrop().into(holder.pic);

                        holder.km.setText("");
                    } else {
                        Log.d("F4ReAdapter>>>", "snapshot not null");
                        String userName = snapshot.get("name").toString();
                        String userSex = snapshot.get("sex").toString();
                        String userAge = snapshot.get("age").toString();
                        String userPic = snapshot.get("pic").toString();
                        String latitudeS = snapshot.get("latitude").toString();
                        String longitudeS = snapshot.get("longitude").toString();

                        holder.name.setText(userName);
                        holder.sex.setText(userSex);
                        if (userName.equals("남자")) {
                            holder.sex.setTextColor(context.getResources().getColor(R.color.male));
                        } else {
                            holder.sex.setTextColor(context.getResources().getColor(R.color.female));
                        }
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
                }
            }
        });

        String time = lastList.get(position).getLasttime();
        String time1 = time.substring(11);
        String time2 = time1.substring(0, time1.length() - 3);
        holder.time.setText(time2);

        holder.messege.setText(lastList.get(position).getLastmessege());

        if (countList.get(position).equals("0")) {
            holder.count.setText("");
            holder.count.setVisibility(View.INVISIBLE);
        } else {
            holder.count.setText(countList.get(position));
            holder.count.setVisibility(View.VISIBLE);
        }

//        db.collection("users").document(arrayList.get(position).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                String userName = documentSnapshot.get("name").toString();
//                String userSex = documentSnapshot.get("sex").toString();
//                String userAge = documentSnapshot.get("age").toString();
//                String userPic = documentSnapshot.get("pic").toString();
//                String latitudeS = documentSnapshot.get("latitude").toString();
//                String longitudeS = documentSnapshot.get("longitude").toString();
//                //위에 6가지는 realtime messege에 추가할 필요없음. 지우기.(f4chatroom도 마찬가지.)
//                holder.name.setText(userName);
//                holder.sex.setText(userSex);
//                holder.age.setText(userAge);
//                requestManager.load(userPic)
//                        .override(150, 150).circleCrop().into(holder.pic);
//
//                double latitude = Double.parseDouble(latitudeS);
//                double longitude = Double.parseDouble(longitudeS);
//
//                gpsTracker = new GpsTracker(context);
//                double myLatitude = gpsTracker.getLatitude();
//                double myLongitude = gpsTracker.getLongitude();
//
//                double ddd = getDistance(myLatitude, myLongitude, latitude, longitude);
//                if (arrayList.get(position).getUid().equals(myUid)) {
//                    holder.km.setText("0m");
//                } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 50) {
//                    holder.km.setText("-50m");
//                } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 1000) {
//                    int i = (int) Math.floor(ddd);
//                    holder.km.setText(Integer.toString(i) + "m");
//                } else if (!arrayList.get(position).getUid().equals(myUid) && ddd >= 1000) {
//                    int i = (int) Math.floor(ddd) / 1000;
//                    holder.km.setText(Integer.toString(i) + "km");
//                }
//            }
//        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.loading.setVisibility(View.VISIBLE);

                db.collection("users").document(arrayList.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.get("uid") != null) {
                                String userPic = snapshot.get("pic").toString();
                                String userName = snapshot.get("name").toString();
                                String userSex = snapshot.get("sex").toString();
                                String userAge = snapshot.get("age").toString();
                                String latitude = snapshot.get("latitude").toString();
                                String longitude = snapshot.get("longitude").toString();
                                Intent intent = new Intent(context, Fragment4ChatRoom.class);
                                intent.putExtra("userUid", arrayList.get(position).getUid());
                                intent.putExtra("userPic", userPic);
                                intent.putExtra("userName", userName);
                                Log.d("getUserName>>>1", userName);
                                intent.putExtra("userSex", userSex);
                                intent.putExtra("userAge", userAge);
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);
                                Fragment4.stayf4chatroom = true;
                                MainActivity.loading.setVisibility(View.GONE);
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, Fragment4ChatRoom.class);
                                intent.putExtra("userUid", arrayList.get(position).getUid());
                                intent.putExtra("userPic", "");
                                intent.putExtra("userName", "");
                                Log.d("getUserName>>>1", "");
                                intent.putExtra("userSex", "");
                                intent.putExtra("userAge", "");
                                intent.putExtra("latitude", "");
                                intent.putExtra("longitude", "");
                                Fragment4.stayf4chatroom = true;
                                MainActivity.loading.setVisibility(View.GONE);
                                context.startActivity(intent);
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
        LinearLayout linearLayout;

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
            this.linearLayout = itemView.findViewById(R.id.f4re_linear);

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int zz = (int) (size.x * 0.12);
            int zzz = (int) (size.x * 0.05);

            ViewGroup.LayoutParams params = pic.getLayoutParams();
            params.width = zz;
            params.height = zz;
            pic.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = count.getLayoutParams();
            params1.width = zzz;
            params1.height = zzz;
            count.setLayoutParams(params1);
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
