package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.adpater.AdressRoomAdapter;
import com.tistory.starcue.cuetalk.adpater.BottomSheetAdapter;
import com.tistory.starcue.cuetalk.item.AdressRoomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private DatabaseReference myReferance;
    private DatabaseReference whoRef;
    private DatabaseReference deleteRef;
    FirebaseFirestore db;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    String name, sex, age, pic;
    String uid;
    String adress;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    double latitude;
    double longitude;

    //bottom sheet
    public static BottomSheetBehavior bottomSheetBehavior;
    LinearLayout linearLayout;
    RelativeLayout titlebar;
    RecyclerView recyclerViewBottom;
    ImageView upanddown;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    boolean isSetList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_room);

        isSetList = false;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        databaseHandler.setDB(AdressRoom.this);
        databaseHandler = new DatabaseHandler(AdressRoom.this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
//        databaseHandler.deleteWhere();

        adress = getAdress();

//        adress = getIntent().getStringExtra("adress");
//        Log.d("AdressRoom>>>", "get intent adress: " + adress);
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
        Intent intent = getIntent();
        bottomAdapter = new BottomSheetAdapter(bottomList, AdressRoom.this, Glide.with(AdressRoom.this));
        recyclerViewBottom.setAdapter(bottomAdapter);

        database = FirebaseDatabase.getInstance();
        whoRef = database.getReference("대화신청").child(uid);

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

//        String adress = getIntent().getStringExtra("adress");

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AdressRoom.this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        adapter = new AdressRoomAdapter(arrayList, AdressRoom.this, Glide.with(AdressRoom.this));
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        reference.getRef().child("adressRoom").child(adress).addChildEventListener(new ChildEventListener() {
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
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {//db변경시
                AdressRoomItem adressRoomItem = snapshot.getValue(AdressRoomItem.class);
                String key = snapshot.getKey();
                int index = keyList.indexOf(key);
                arrayList.set(index, adressRoomItem);
                Log.d("AdressRoom>>>", "onChildChange : " + Integer.toString(index));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {//db제거시
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
        reference.getRef().child("adressRoom").child(adress).addListenerForSingleValueEvent(new ValueEventListener() {//최초 list 불러오기
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //db받아오는곳
                arrayList.clear();//기존list겹치지않게 제거
                keyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//반복문으로 데이터추출
                    String key = snapshot.getKey();
                    keyList.add(key);
                    int index = keyList.indexOf(key);
                    Log.d("AdressRoom>>>", "test index: " + index);
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

        reference.getRef().child("adressRoom").child(adress).child(uid).addValueEventListener(new ValueEventListener() {//db변경시
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long l = dataSnapshot.child("ischat").getValue(Long.class);
                if (l != null) {
                    int i = l.intValue();
                    if (i == 2) {
                        progressBar.setVisibility(View.VISIBLE);
                        reference.getRef().child("adressRoom").child(adress).child(uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                String where = dataSnapshot.child("where").getValue(String.class);//error?

                                deleteBottomList();

                                upanddown.setBackgroundResource(R.drawable.bottom_up);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                                Intent intent1 = new Intent(AdressRoom.this, ChatRoom.class);
                                intent1.putExtra("intentwhere", where);
                                Log.d("AdressRoom>>>", "start activity");
                                startActivity(intent1);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                }
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

    private void getUser() {//get my data
        DocumentReference documentReference = db.collection("users")
                .document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                gpsTracker = new GpsTracker(AdressRoom.this);
                latitude = gpsTracker.getLatitude();//위도
                longitude = gpsTracker.getLongitude();//경도
                String latitudeS = String.valueOf(latitude);//AdressRoom Activity 입장할때 실시간 위치
                String longitudeS = String.valueOf(longitude);

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
    }

    private void goToMain() {
        Log.d("AdressRoom>>>", "goToMain");
        deleteBottomList();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.getRef().child("adressRoom").child(adress).child(uid).removeValue();
        reference.getRef().child("대화신청").child(uid).removeValue();
        databaseHandler.adressdelete();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            upanddown.setBackgroundResource(R.drawable.bottom_up);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            super.onBackPressed();
            goToMain();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void deleteBottomList() {
        whoRef = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i < userList.size(); i++) {
            String s = userList.get(i);
            whoRef.getRef().child("대화신청").child(s).child(uid).removeValue();
        }
    }

    private String getAdress() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        cursor.close();
        return adress;
    }

}
