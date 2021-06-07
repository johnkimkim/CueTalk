package com.tistory.starcue.cuetalk.adpater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.AdressRoom;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;
import com.tistory.starcue.cuetalk.SendMessege;
import com.tistory.starcue.cuetalk.item.AdressRoomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdressRoomAdapter extends RecyclerView.Adapter<AdressRoomAdapter.CustomViewHolder> {

    private ArrayList<AdressRoomItem> arrayList;
    private Context context;

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
    String myUid;

    private AlertDialog messegeDialog;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private RequestManager requestManager;

    Activity activity;

    public AdressRoomAdapter(ArrayList<AdressRoomItem> arrayList, Context context, RequestManager requestManager, Activity activity) {
        this.arrayList = arrayList;
        this.context = context;
        this.requestManager = requestManager;
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adress_room_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        requestManager.load(arrayList.get(position).getPic()).override(150,150).circleCrop().into(holder.imageView);
        if (arrayList.get(position).getPic().equals(nullPic) && arrayList.get(position).getPic().equals(nullPicF)) {
            holder.imageView.setEnabled(false);
        } else {
            holder.imageView.setEnabled(true);
        }
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

        if (arrayList.get(position).getUid().equals(myUid)) {
            holder.btn.setEnabled(false);
            holder.btn.setText("나");
            holder.sendmsgbtn.setEnabled(false);
        } else if (arrayList.get(position).isIschat() == 2) {
            holder.btn.setEnabled(false);
            holder.btn.setText("대화중");
        } else {
            holder.btn.setEnabled(true);
            holder.btn.setText("대화신청");
        }

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useruid = arrayList.get(position).getUid();
                AdressRoom.userList.add(useruid);
                for (int i = 0; i < AdressRoom.userList.size(); i++) {
                    String testString = AdressRoom.userList.get(i);
                }

                db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection("users").document(myUid);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        gpsTracker = new GpsTracker(context);
                        Double latitude = gpsTracker.getLatitude();
                        Double longitude = gpsTracker.getLongitude();
                        String latitudeS = String.valueOf(latitude);
                        String longitudeS = String.valueOf(longitude);

                        pic = documentSnapshot.getString("pic");
                        name = documentSnapshot.getString("name");
                        sex = documentSnapshot.getString("sex");
                        age = documentSnapshot.getString("age");
                        int ischat = 1;

                        updateAdressRoom(useruid, pic, myUid, name, sex, age, latitudeS, longitudeS, ischat);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        holder.sendmsgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //쪽지보내기
                reference = FirebaseDatabase.getInstance().getReference();
                reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int i = (int) dataSnapshot.getChildrenCount();
                        String roomkey = arrayList.get(position).getUid() + myUid;
                        String roomkey1 = myUid + arrayList.get(position).getUid();
                        if (i == 0) {
                            String userUid = arrayList.get(position).getUid();
                            SendMessege sendMessege = new SendMessege(context);
                            sendMessege.setSendMessegeDialog(context, userUid, activity);
                        } else {
                            if (dataSnapshot.hasChild(roomkey) || dataSnapshot.hasChild(roomkey1)) {
                                Toast.makeText(context, "이미 대화 중 입니다. 메시지함을 확인해주세요", Toast.LENGTH_SHORT).show();
                            } else {
                                String userUid = arrayList.get(position).getUid();
                                SendMessege sendMessege = new SendMessege(context);
                                sendMessege.setSendMessegeDialog(context, userUid, activity);//laskdfjkl
                            }
                        }

                    }
                });

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = arrayList.get(position).getPic();
                if (!uri.equals(nullPic) && !uri.equals(nullPicF)) {
                    SeePicDialog.seePicDialog(context, uri);
                }
            }
        });

    }

    private void updateAdressRoom(String useruid, String picUri, String uid, String name, String sex, String age, String latitude, String longitude, int ischat) {
        reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/uid/", uid);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/pic/", picUri);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/name/", name);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/sex", sex);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/age/", age);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/latitude/", latitude);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/longitude/", longitude);
        updateUser.put("/대화신청/" + useruid + "/" + uid + "/" + "/ischat/", ischat);
        reference.updateChildren(updateUser);
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

    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView sex;
        TextView age;
        TextView km;
        Button btn, sendmsgbtn;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.adress_room_layout_pic);
            this.name = itemView.findViewById(R.id.adress_room_layout_name);
            this.sex = itemView.findViewById(R.id.adress_room_layout_sex);
            this.age = itemView.findViewById(R.id.adress_room_layout_age);
            this.km = itemView.findViewById(R.id.adress_room_layout_km);
            this.btn = itemView.findViewById(R.id.adress_room_layout_chatbtn);
            this.sendmsgbtn = itemView.findViewById(R.id.adress_room_layout_send_messege_btn);
        }
    }



}
