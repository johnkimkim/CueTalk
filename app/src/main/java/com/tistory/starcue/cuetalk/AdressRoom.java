package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.tistory.starcue.cuetalk.Fragment1.spinner;

public class AdressRoom extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdressRoomAdapter adapter;
    private BottomSheetAdapter bottomAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> keyList = new ArrayList<String>();
    private List<String> bottomKeyList = new ArrayList<String>();
    private ArrayList<AdressRoomItem> arrayList;
    private ArrayList<AdressRoomItem> bottomList;
    private FirebaseDatabase database;
    private View view;

    public static List<String> userList = new ArrayList<String>();

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private DatabaseReference whoRef;
    private DatabaseReference deleteRef;
    FirebaseFirestore db;

    String name, sex, age, pic;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    double latitude;
    double longitude;

    //bottom sheet
    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout linearLayout;
    RelativeLayout titlebar;
    RecyclerView recyclerViewBottom;
    ImageView upanddown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_room);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


//        getGps();

        setBottomSheet();
        setInit();
        getUser();
//        setRecyclerView();

    }

    private void setBottomSheet() {
        this.titlebar = findViewById(R.id.titlebar);
        this.recyclerViewBottom = findViewById(R.id.sheetlist);
        this.upanddown = findViewById(R.id.upanddown);
        upanddown.setBackgroundResource(R.drawable.bottom_up);
        linearLayout = findViewById(R.id.bottom_sheet_id);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        titlebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    upanddown.setBackgroundResource(R.drawable.bottom_up);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    upanddown.setBackgroundResource(R.drawable.bottom_down);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        recyclerViewBottom.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AdressRoom.this);
        recyclerViewBottom.setLayoutManager(layoutManager);
        bottomList = new ArrayList<>();
        bottomAdapter = new BottomSheetAdapter(bottomList, AdressRoom.this);
        recyclerViewBottom.setAdapter(bottomAdapter);

        String uid = mAuth.getUid();
        database = FirebaseDatabase.getInstance();
        whoRef = database.getReference("chatting").child(uid);

        whoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AdressRoomItem adressRoomItem = snapshot.getValue(AdressRoomItem.class);
                bottomList.add(adressRoomItem);
                bottomAdapter.notifyDataSetChanged();
                String key = snapshot.getKey();
                bottomKeyList.add(key);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
                upanddown.startAnimation(animation);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        upanddown.clearAnimation();
                    }
                }, 1800);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = bottomKeyList.indexOf(key);
                bottomList.remove(index);
                bottomKeyList.remove(index);
                bottomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        whoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //db받아오는곳
                bottomList.clear();//기존list겹치지않게 제거
                bottomKeyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//반복문으로 데이터추출
                    String key = snapshot.getKey();
                    bottomKeyList.add(key);
                    Log.d("AdressRoom>>>", "addListenerForSingleValueEvent");
                    AdressRoomItem adressRoomItem = snapshot.getValue(AdressRoomItem.class);
                    bottomList.add(adressRoomItem);
                }
                bottomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        whoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                TextView textView = findViewById(R.id.title);
                textView.setText(Integer.toString(size));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRecyclerView() {

        String adress = getIntent().getStringExtra("adress");
        String uid = mAuth.getUid();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AdressRoom.this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        Intent intent = getIntent();
        adapter = new AdressRoomAdapter(arrayList, AdressRoom.this, intent);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("adressRoom").child(adress);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {//db추가시
                Log.d("AdressRoom>>>", "onChildAdded");
                AdressRoomItem adressRoomItem = snapshot.getValue(AdressRoomItem.class);
                arrayList.add(adressRoomItem);
                adapter.notifyDataSetChanged();
                String key = snapshot.getKey();
                keyList.add(key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = keyList.indexOf(key);
                Log.d("AdressRoom>>>", Integer.toString(index));
                arrayList.remove(index);
                keyList.remove(index);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {//최초 list 불러오기
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //db받아오는곳
                arrayList.clear();//기존list겹치지않게 제거
                keyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//반복문으로 데이터추출
                    String key = snapshot.getKey();
                    keyList.add(key);
                    Log.d("AdressRoom>>>", "addListenerForSingleValueEvent");
                    AdressRoomItem adressRoomItem = snapshot.getValue(AdressRoomItem.class);
                    arrayList.add(adressRoomItem);
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //db가져오다가 오류발생시
                Log.e("AdressRoom>>>", String.valueOf(error.toException()));
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setInit() {
        recyclerView = findViewById(R.id.adress_room_recycelrview);
        progressBar = findViewById(R.id.adress_room_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getUser() {
        String uid = mAuth.getUid();
        DocumentReference documentReference = db.collection("users")
                .document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                gpsTracker = new GpsTracker(AdressRoom.this);
                latitude = gpsTracker.getLatitude();//위도
                longitude = gpsTracker.getLongitude();//경도
                String latitudeS = String.valueOf(latitude);
                String longitudeS = String.valueOf(longitude);
                Toast.makeText(AdressRoom.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

                pic = documentSnapshot.getString("pic");
                name = documentSnapshot.getString("name");
                sex = documentSnapshot.getString("sex");
                age = documentSnapshot.getString("age");
                int ischat = 1;
                updateAdressRoom(pic, uid, name, sex, age, latitudeS, longitudeS, ischat);

                setRecyclerView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdressRoom.this, "입장실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdressRoom(String picUri, String uid, String name, String sex, String age, String latitude, String longitude, int ischat) {
        String adress = getIntent().getStringExtra("adress");
        reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/uid/", uid);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/pic/", picUri);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/name/", name);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/sex", sex);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/age/", age);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/latitude/", latitude);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/longitude/", longitude);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/ischat/", ischat);
        reference.updateChildren(updateUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("AdressRoom>>>", "onStop");
        String s = getIntent().getStringExtra("adress");
        String uid = mAuth.getUid();
        deleteBottomList();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.getRef().child("adressRoom").child(s).child(uid).removeValue();
        reference.getRef().child("chatting").child(uid).removeValue();
        userList.clear();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        spinner.setSelection(0);
    }

    private void deleteBottomList() {
        String myUid = mAuth.getUid();
        whoRef = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i < userList.size(); i++) {
            String s = userList.get(i);
            whoRef.getRef().child("chatting").child(s).child(myUid).removeValue();
        }
    }


//    private void chatListener(DataSnapshot dataSnapshot) {
//        String name, sex, age, km, uri;
//        Iterator i = dataSnapshot.getChildren().iterator();
//
//        while (i.hasNext()) {
//            name = (String) ((DataSnapshot) i.next()).getValue();
//            sex = (String) ((DataSnapshot) i.next()).getValue();
//            age = (String) ((DataSnapshot) i.next()).getValue();
//            km = (String) ((DataSnapshot) i.next()).getValue();
//            uri = (String) ((DataSnapshot) i.next()).getValue();
//
//            //유저이름, 메시지를 가져와서 array에 추가
//            arrayList.add(arrayList.set(i, name).setName(););
//        }
//
//        //변경된값으로 리스트뷰 갱신
//        arrayAdapter.notifyDataSetChanged();
//    }
}
