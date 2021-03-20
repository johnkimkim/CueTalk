package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.CustomViewHolder>{

    private ArrayList<AdressRoomItem> bottomList;
    private GpsTracker gpsTracker;
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference countRef;
    private FirebaseFirestore db;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

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

        databaseHandler.setDB(context);
        databaseHandler = new DatabaseHandler(context);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        if (bottomList.get(position).getPic() == null) {
            if (bottomList.get(position).getSex().equals("남자")) {
                Glide.with(holder.imageView)
                        .load(nullPic)
                        .override(150, 150)
                        .circleCrop()
                        .into(holder.imageView);
            } else {
                Glide.with(holder.imageView)
                        .load(nullPicF)
                        .override(150, 150)
                        .circleCrop()
                        .into(holder.imageView);
            }
            holder.imageView.setEnabled(false);
        } else {
            Glide.with(holder.imageView)
                    .load(bottomList.get(position).getPic())
                    .override(150, 150)
                    .circleCrop()
                    .into(holder.imageView);
            holder.imageView.setEnabled(true);
        }

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

//        database = FirebaseDatabase.getInstance();
//        reference = database.getReference("chatting").child(adress);

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdressRoom.progressBar.setVisibility(View.VISIBLE);
                AdressRoom.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                String adress = getAdress();
                mAuth = FirebaseAuth.getInstance();
                String myUid = mAuth.getUid();
                String userUid = bottomList.get(position).getUid();
                String userPic = bottomList.get(position).getPic();
                String userName = bottomList.get(position).getName();
                String userSex = bottomList.get(position).getSex();
                String userAge = bottomList.get(position).getAge();
                String userLatitude = bottomList.get(position).getLatitude();
                String userLongitude = bottomList.get(position).getLongitude();
                reference = FirebaseDatabase.getInstance().getReference();

//                databaseHandler.insertWhere(myUid);

                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/ischat/", 2);
                updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/where/", myUid);
                updateUser.put("/adressRoom/" + adress + "/" + userUid + "/" + "/ischat/", 2);
                updateUser.put("/adressRoom/" + adress + "/" + userUid + "/" + "/where/", myUid);
                reference.updateChildren(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        creatChatting(myUid, myUid, userUid, userPic, userName, userSex, userAge, userLatitude, userLongitude);
                    }
                });

            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                String myUid = mAuth.getUid();
                String userUid = bottomList.get(position).getUid();

                reference = FirebaseDatabase.getInstance().getReference();
                reference.getRef().child("chatting").child(myUid).child(userUid).removeValue();
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myUri = bottomList.get(position).getPic();
                SeePicDialog.seePicDialog(context, myUri);
            }
        });
    }

    private void creatChatting(String where, String myUid, String userUid, String userPic, String userName, String userSex, String userAge, String userLatitude, String userLongitude) {
        db = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();

        DocumentReference documentReference = db.collection("users")
                .document(myUid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                gpsTracker = new GpsTracker(context);
                Double latitude = gpsTracker.getLatitude();//위도
                Double longitude = gpsTracker.getLongitude();//경도
                String latitudeS = String.valueOf(latitude);
                String longitudeS = String.valueOf(longitude);
                Toast.makeText(context, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

                String pic = documentSnapshot.getString("pic");
                String name = documentSnapshot.getString("name");
                String sex = documentSnapshot.getString("sex");
                String age = documentSnapshot.getString("age");

                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("/inchat/" + where + "/" + myUid + "/" + "/pic/", pic);
                updateUser.put("/inchat/" + where + "/" + myUid + "/" + "/name/", name);
                updateUser.put("/inchat/" + where + "/" + myUid + "/" + "/sex/", sex);
                updateUser.put("/inchat/" + where + "/" + myUid + "/" + "/age/", age);
                updateUser.put("/inchat/" + where + "/" + myUid + "/" + "/latitude/", latitudeS);
                updateUser.put("/inchat/" + where + "/" + myUid + "/" + "/longitude/", longitudeS);

                updateUser.put("/inchat/" + where + "/" + userUid + "/" + "/pic/", userPic);
                updateUser.put("/inchat/" + where + "/" + userUid + "/" + "/name/", userName);
                updateUser.put("/inchat/" + where + "/" + userUid + "/" + "/sex/", userSex);
                updateUser.put("/inchat/" + where + "/" + userUid + "/" + "/age/", userAge);
                updateUser.put("/inchat/" + where + "/" + userUid + "/" + "/latitude/", userLatitude);
                updateUser.put("/inchat/" + where + "/" + userUid + "/" + "/longitude/", userLongitude);
                reference.updateChildren(updateUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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
        Button btn, deleteBtn;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.bottom_sheet_layout_pic);
            this.name = itemView.findViewById(R.id.bottom_sheet_layout_name);
            this.sex = itemView.findViewById(R.id.bottom_sheet_layout_sex);
            this.age = itemView.findViewById(R.id.bottom_sheet_layout_age);
            this.km = itemView.findViewById(R.id.bottom_sheet_layout_km);
            this.btn = itemView.findViewById(R.id.bottom_sheet_layout_chatbtn);
            this.deleteBtn = itemView.findViewById(R.id.bottom_sheet_layout_delete);
        }
    }

    private String getAdress() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        return adress;
    }

}
