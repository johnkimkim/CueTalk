package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tistory.starcue.cuetalk.adpater.F4ChatRoomAdapter;
import com.tistory.starcue.cuetalk.fragment.Fragment4;
import com.tistory.starcue.cuetalk.item.F4ChatRoomItem;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fragment4ChatRoom extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private static final String serverKey = " AAAAqHwsNuA:APA91bEhOL4uoOR3d0Ys1qbFflQelzTPwaxBFLRI5Prx7tCor-KoivdXAKpLjz_PDlFctKT1iVPhwgXcPq8ioYh_TvaqSHPPjhCc98M5z7g9i3reg8Cqjbn-J0LbXXi0pSeMJa8KuYRk";
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    //    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<F4ChatRoomItem> arrayList;
    private List<String> keyList;
    private F4ChatRoomAdapter adapter;

    RecyclerView recyclerView;
    Button sendimg, sendmsg, gobackbtn, outroombtn, decbtn;
    EditText editText;
    RelativeLayout load;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    LinearLayout titlelayout, title2layout;
    TextView titleName, titleSex, titleAge, titleKm;
    String myUid, userUid, myUiduserUid, userUidmyUid, userPic, userName, userSex, userAge, userLatitude, userLongitude;
    String getroomname;
    String myName, myPic, myTime;

    Uri imageUri;
    String picUri;

    AlertDialog alertDialogA;
    AlertDialog alertDialogD;

    boolean isOpen;
    boolean outAlready;
    boolean finishedAll;
    boolean pressBack;

    String msgUid;
    String myState;

    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment4_chat_room);

        activity = Fragment4ChatRoom.this;

        isOpen = false;
        outAlready = false;
        setinit();
        Log.d("Fragment4ChatRoom>>>", "set state onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Fragment4ChatRoom>>>", "set state onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Fragment4ChatRoom>>>", "set state onReStart");
    }

    private void getMyProfile() {
        DocumentReference documentReference = db.collection("users").document(myUid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myPic = documentSnapshot.getString("pic");
                myName = documentSnapshot.getString("name");
            }
        });
    }

    private void getUserUid() {
        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid");
        userName = intent.getStringExtra("userName");
        userSex = intent.getStringExtra("userSex");
        userAge = intent.getStringExtra("userAge");
        userPic = intent.getStringExtra("userPic");
        userLatitude = intent.getStringExtra("userLatitude");
        userLongitude = intent.getStringExtra("userLongitude");
        myUiduserUid = myUid + userUid;
        userUidmyUid = userUid + myUid;
        cancelNotify(userUid);
        Log.d("Fragment4ChatRoom>>>", "get user uid: " + userUid);
        setTitle();
        setEditText();
    }

    private void cancelNotify(String userUid) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(userUid.replaceAll("[^0-9]", "")));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelNotify(userUid);

        if (getroomname != null) {
            setState();
        }

//        sharedPreferences = getSharedPreferences("saveroomkey", MODE_PRIVATE);
//        if (sharedPreferences != null) {
//            getroomname = sharedPreferences.getString("getroomname", "");
//            if (!getroomname.equals("")) {
//                setState();
//            }
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        setOutState();
//        sharedPreferences = getSharedPreferences("saveroomkey", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("getroomname", getroomname);
//        editor.commit();
    }

    private void setinit() {
        titlelayout = findViewById(R.id.chat_room_title_user_layout);
        titlelayout.setVisibility(View.INVISIBLE);
        title2layout = findViewById(R.id.fragment4_chat_room_sexagekm);
        recyclerView = findViewById(R.id.fragment4_chat_room_recyclerview);
        sendimg = findViewById(R.id.fragment4_chat_room_sendimage);
        sendmsg = findViewById(R.id.fragment4_chat_room_sendbutton);
        titleName = findViewById(R.id.fragment4_chat_room_title_name);
        titleSex = findViewById(R.id.fragment4_chat_room_title_sex);
        titleAge = findViewById(R.id.fragment4_chat_room_title_age);
        titleKm = findViewById(R.id.fragment4_chat_room_title_km);
        gobackbtn = findViewById(R.id.fragment4_chat_room_backbtn);
        outroombtn = findViewById(R.id.fragment4_chat_room_outroom);
        editText = findViewById(R.id.fragment4_chat_room_edittext);
        decbtn = findViewById(R.id.fragment4_chat_room_send_callbtn);
        load = findViewById(R.id.fragment4_chat_room_load);
        load.setVisibility(View.GONE);

        setdb();

        setRecyclerView();

        setOutroombtn();

        setGobackbtn();

        decbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecDialog.F4ChatRoomDecDialog(Fragment4ChatRoom.this, userUid, myUid, getroomname, activity);
            }
        });
    }

    private void setTitle() {
        db.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.get("uid") != null) {
                        titleName.setText(documentSnapshot.get("name").toString());
                        titleSex.setText(documentSnapshot.get("sex").toString());
                        if (documentSnapshot.get("sex").toString().equals("남자")) {
                            titleSex.setTextColor(getResources().getColor(R.color.male));
                        } else {
                            titleSex.setTextColor(getResources().getColor(R.color.female));
                        }
                        titleAge.setText(documentSnapshot.get("age").toString());
                        double userLatitude = Double.parseDouble(documentSnapshot.get("latitude").toString());
                        double userLongitude = Double.parseDouble(documentSnapshot.get("longitude").toString());
                        GpsTracker gpsTracker = new GpsTracker(Fragment4ChatRoom.this);
                        double myLatitude = gpsTracker.getLatitude();
                        double myLongitude = gpsTracker.getLongitude();
                        double ddd = getDistance(myLatitude, myLongitude, userLatitude, userLongitude);
                        if (ddd < 500) {
                            titleKm.setText("-50m");
                        } else if (ddd < 1000) {
                            int i = (int) Math.floor(ddd);
                            titleKm.setText(Integer.toString(i) + "m");
                        } else if (ddd >= 1000) {
                            int i = (int) Math.floor(ddd) / 1000;
                            titleKm.setText(Integer.toString(i) + "km");
                        }
                        titlelayout.setVisibility(View.VISIBLE);
                    } else {
                        titleName.setText("(알수없음)");
                        title2layout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setdb() {
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        myUid = mAuth.getUid();

        getUserUid();
        getMyProfile();
        setSendmsg();
        setSendimg();
    }

    private void setEditText() {
        db.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") == null) {
                        editText.setEnabled(false);
                    } else {
                        editText.setEnabled(true);
                    }
                }
            }
        });
    }

    private void setState() {
        Map<String, Object> setState = new HashMap<>();
        setState.put("/messege/" + getroomname + "/" + myUid + "/state/", "2");//채팅중
        reference.updateChildren(setState);
        Log.d("Fragment4ChatRoom>>>", "set state 2: " + getroomname);
    }

    private void setOutState() {
        reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(getroomname)) {
                    Log.d("Fragment4ChatRoom>>>", "getroomname has");
                    Map<String, Object> setState = new HashMap<>();
                    setState.put("/messege/" + getroomname + "/" + myUid + "/state/", "1");//채팅방 나감
                    reference.updateChildren(setState);
                    Log.d("Fragment4ChatRoom>>>", "set state 1 getroomname: " + getroomname);
                } else {
                    Log.d("Fragment4ChatRoom>>>", "getroomname null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Fragment4ChatRoom.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        keyList = new ArrayList<>();
//        adapter = new F4ChatRoomAdapter(Fragment4ChatRoom.this, arrayList);
//        recyclerView.setAdapter(adapter);
//
//        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

        reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("Fragment4ChatRoom>>>", snapshot.getKey());
                    Log.d("Fragment4ChatRoom>>>", myUiduserUid);
                    Log.d("Fragment4ChatRoom>>>", userUidmyUid);
                    if (snapshot.getKey().contains(myUid) && snapshot.getKey().contains(userUid)) {
                        getroomname = snapshot.getKey();
                        adapter = new F4ChatRoomAdapter(Fragment4ChatRoom.this, arrayList, getroomname, Glide.with(Fragment4ChatRoom.this), Fragment4ChatRoom.this);
                        adapter.setHasStableIds(true);
//                        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
//                        if (animator instanceof SimpleItemAnimator) {
//                            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
//                        }
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        setRecyclerviewList();
                    }
//                    if (snapshot.getKey().equals(myUiduserUid)) {
//                        getroomname = myUiduserUid;
//                        adapter = new F4ChatRoomAdapter(Fragment4ChatRoom.this, arrayList, getroomname, Glide.with(Fragment4ChatRoom.this));
//                        recyclerView.setAdapter(adapter);
//                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//                        setRecyclerviewList();
//                    } else if (snapshot.getKey().equals(userUidmyUid)) {
//                        getroomname = userUidmyUid;
//                        adapter = new F4ChatRoomAdapter(Fragment4ChatRoom.this, arrayList, getroomname, Glide.with(Fragment4ChatRoom.this));
//                        recyclerView.setAdapter(adapter);
//                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//                        setRecyclerviewList();
//                    }
                }
            }
        });

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKB();
            }
        });
    }

    private void setNewPic() {
        reference.getRef().child("messege").child(getroomname).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<>();
                    if (!dataSnapshot1.getKey().equals("msg") && !dataSnapshot1.getKey().contains("lastmsg") && !dataSnapshot1.getKey().equals(myUid)) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            if (dataSnapshot2.getKey().equals("pic")) {
                                String youPic = dataSnapshot2.getValue(String.class);
                                for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot3.getKey().equals("msg")) {
                                        Log.d("Fragment4ChatRoom>>>", "check: " + dataSnapshot3.getKey());
                                        for (DataSnapshot dataSnapshot4 : dataSnapshot3.getChildren()) {
                                            String key = dataSnapshot4.getKey();
                                            if (dataSnapshot4.child("pic").equals(arrayList.get(arrayList.size() - 1)) && dataSnapshot4.child("pic") != null) {
                                                map.put("/" + key + "/pic/", youPic);
                                            }
                                        }
                                        reference.getRef().child("messege").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                                setRecyclerviewList();
                                            }
                                        });
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    private void setRecyclerviewList() {

        userMessegeChangeReadWhenICome();
        getNewMessege();
        checkUserIsChat();
        setState();

        //상대방이 나갔을때
        reference.getRef().child("messege").child(getroomname).child("msg").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    int i = (int) snapshot1.getChildrenCount();
                    F4ChatRoomItem chatRoomItem = snapshot1.getValue(F4ChatRoomItem.class);
                    arrayList.add(chatRoomItem);

                    reference.getRef().child("messege").child(getroomname).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.getKey().equals(userUid)) {
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                        if (dataSnapshot2.getKey().equals("ischat")) {
                                            String ischat = dataSnapshot2.getValue(String.class);
                                            if (ischat.equals("2")) {
                                                Log.d("Fragment4ChatRoom>>>", "Success");
                                                if (!isOpen) {
                                                    userGoOutDialog();
                                                }
                                                isOpen = true;

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });


                }
                adapter.notifyDataSetChanged();
                load.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNewMessege() {
        reference.getRef().child("messege").child(getroomname).child("msg").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                msgUid = snapshot.child("uid").getValue(String.class);//새메시지의 uid

                if (msgUid.equals(userUid) && myState.equals("2")) {//이부분 동작안함
                    Map<String, Object> map = new HashMap<>();
                    map.put("read", "0");
                    reference.getRef().child("messege").child(getroomname).child("msg").child(snapshot.getKey()).updateChildren(map);
                }

                F4ChatRoomItem chatRoomItem = snapshot.getValue(F4ChatRoomItem.class);
                arrayList.add(chatRoomItem);
                keyList.add(snapshot.getKey());
                adapter.notifyDataSetChanged();

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 100);

                load.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                F4ChatRoomItem chatRoomItem = snapshot.getValue(F4ChatRoomItem.class);
                int index = keyList.indexOf(snapshot.getKey());
                arrayList.set(index, chatRoomItem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserIsChat() {
        reference.getRef().child("messege").child(getroomname).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userUid)) {
                    F4MessegeItem f4MessegeItem = snapshot.getValue(F4MessegeItem.class);
                    String ischat = f4MessegeItem.getIschat();
                    if (ischat.equals("2")) {
                        userGoOutDialog();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userMessegeChangeReadWhenICome() {
        reference.getRef().child("messege").child(getroomname).child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Fragment4ChatRoom>>>", "ondatachaged");
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getKey().equals("state")) {
                        if (snapshot1.getValue(String.class).equals("2")) {
                            myState = "2";
                            Map<String, Object> map = new HashMap<>();
                            map.put("read", "0");
                            reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {//messege key
                                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                            if (dataSnapshot2.getKey().equals("uid")) {//uid in msg
                                                if (dataSnapshot2.getValue(String.class).equals(userUid)) {
                                                    reference.getRef().child("messege").child(getroomname).child("msg").child(dataSnapshot1.getKey()).updateChildren(map);
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            myState = "1";
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSendmsg() {
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = editText.getText().toString();
                if (!messege.equals("")) {
                    Map<String, Object> sendmsg = new HashMap<>();
                    Map<String, Object> lastmsg = new HashMap<>();
                    sendmsg.put("messege", messege);
                    sendmsg.put("time", getTime());
                    sendmsg.put("read", "1");
                    sendmsg.put("uid", myUid);

                    lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lastmessege/", messege);
                    lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lasttime/", getTime());
                    reference.child("messege").child(getroomname).child("msg").push().updateChildren(sendmsg);
                    reference.updateChildren(lastmsg);

                    sendNotify(userUid, messege, myName);

                    editText.setText("");
                }
            }
        });
    }

    private void setSendimg() {
        sendimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Log.d("Fragment4ChatRoom>>>", "imageUri: " + imageUri);
            uploadPic();
        }
    }

    private void uploadPic() {
        if (imageUri != null) {

            String s = imageUri.toString();
            String fileName = s.replace("/", "");

            final ProgressDialog pd = new ProgressDialog(Fragment4ChatRoom.this);
            pd.setTitle("이미지 전송 중...");
            pd.show();
            pd.setCancelable(false);

            StorageReference riverRef = storageReference.child(myUid + "/" + getroomname + "/" + fileName);
            riverRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    int i = listResult.getItems().size() + 1;
                    String count = Integer.toString(i);
                    Log.d("Fragment4ChatRoom>>>", "image count: " + Integer.toString(i));
                    riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child(myUid + "/" + getroomname + "/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    picUri = uri.toString();
                                    Log.d("Fragment4ChatRoom>>>", "onSuccess: " + uri.toString());
                                    sendMessegePic(picUri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Fragment4ChatRoom>>>", "onFailure: " + e.toString());
                                }
                            });
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Fragment4ChatRoom.this, "업로드실패", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage("Pro: " + (int) progressPercent + "%");
                        }
                    });
                }
            });

        }
    }

//    private void dialogImage(String picUri) {
//        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.send_iamge_dialog, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(Fragment4ChatRoom.this);
//        builder.setView(layout);
//        alertDialogC = builder.create();
//        Log.d("Fragment4ChatRoom>>>", "dialogImage");
//
//        if (!Fragment4ChatRoom.this.isFinishing()) {
//            Log.d("Fragment4ChatRoom>>>", "dialogImage2");
//            alertDialogC.show();
//            //set size
//            WindowManager.LayoutParams layoutParams = alertDialogC.getWindow().getAttributes();
//            layoutParams.copyFrom(alertDialogC.getWindow().getAttributes());
////            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
////            layoutParams.dimAmount = 0.7f;
//
//            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            alertDialogC.getWindow().setAttributes(layoutParams);
//        }
//
//        ImageView imageView = layout.findViewById(R.id.dialog_img_set);
//        Button okbtn = layout.findViewById(R.id.send_image_okbtn);
//
//        Glide.with(Fragment4ChatRoom.this).load(picUri).override(100, 100).centerCrop().into(imageView);
//        okbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialogC.dismiss();
//                sendMessegePic(picUri);
//            }
//        });
//    }

    private void sendMessegePic(String uri) {
        Log.d("Fragment4ChatRoom>>>", "time: " + getTime());
        editText.setText("");

        sendMessegeMapPic(uri, myName, myPic);
    }

    private void sendMessegeMapPic(String uri, String myName, String myPic) {
        Map<String, Object> sendmsgpic = new HashMap<>();
        Map<String, Object> lastmsg = new HashMap<>();
        sendmsgpic.put("uri", uri);
        sendmsgpic.put("time", getTime());
        sendmsgpic.put("read", "1");
        sendmsgpic.put("uid", myUid);

        lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lastmessege/", "사진이 전송되었습니다.");
        lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lasttime/", getTime());
        reference.child("messege").child(getroomname).child("msg").push().updateChildren(sendmsgpic);
        reference.updateChildren(lastmsg);

        sendPicNotify(userUid, myName);

//        reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                int i = (int) dataSnapshot.getChildrenCount() + 1;
//                Map<String, Object> sendmsg = new HashMap<>();
//                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/uri/", uri);
//                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/name/", myName);
//                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/pic/", myPic);
//                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/time/", getTime());
//                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/read/", "1");
//
//                sendmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lastmessege/", "사진이 전송되었습니다.");
//                sendmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lasttime/", getTime());
//                reference.updateChildren(sendmsg);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(Fragment4ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void setOutroombtn() {
        outroombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goOutDialog();
            }
        });
    }

    private void goOutDialog() {//내가 나갈때
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.go_out_chat_room_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Fragment4ChatRoom.this);
        builder.setView(layout);
        alertDialogA = builder.create();

        if (!Fragment4ChatRoom.this.isFinishing()) {
            alertDialogA.show();
        }

        alertDialogA.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialogA.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        Button okbtn = layout.findViewById(R.id.go_out_chat_room_dialog_okbtn);
        Button cancel = layout.findViewById(R.id.go_out_chat_room_dialog_cancelbtn);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                okbtn.setEnabled(false);
                cancel.setEnabled(false);
                alertDialogA.dismiss();
                checkUserDelete();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogA.dismiss();
            }
        });
    }

    private void userGoOutDialog() {//상대가 나갔을때
        if (alertDialogD == null) {
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) vi.inflate(R.layout.go_out_user_chat_room_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(Fragment4ChatRoom.this);
            builder.setView(layout);
            alertDialogD = builder.create();

            if (!Fragment4ChatRoom.this.isFinishing()) {
                if (DecDialog.alertDialog != null) {
                    DecDialog.alertDialog.dismiss();
                } else if (alertDialogA != null) {
                    alertDialogA.dismiss();
                }
                alertDialogD.show();
            }
            alertDialogD.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Window window = alertDialogD.getWindow();
            int x = (int) (size.x * 0.9);
            int y = (int) (size.y * 0.3);
            window.setLayout(x, y);
            alertDialogD.setCancelable(false);

            Button okbtn = layout.findViewById(R.id.fragment4_chat_room_dialog_user_out_okbtn);

            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    load.setVisibility(View.VISIBLE);
                    alertDialogD.dismiss();
                    deleteImageISendB();
                }
            });
        }
    }

    private void deleteAllChatRoom() {
        reference.getRef().child("messege").child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        outAlready = true;
                        if (alertDialogD != null) {
                            alertDialogD.dismiss();
                        } else if (alertDialogA != null) {
                            alertDialogA.dismiss();
                        }
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void checkUserDelete() {
        db.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        goOutSetIsChat();
                    } else {
                        deleteImageISendB();
                    }
                }
            }
        });
    }

    private void goOutSetIsChat() {
        db.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("delete") == null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("messege/", "tkdeoqkddlskrkskrk");
                        map.put("time/", "tkdeoqkddlskrkskrk");
                        map.put("uid/", "tkdeoqkddlskrkskrk");
                        map.put("read/", "tkdeoqkddlskrkskrk");
                        reference.child("messege").child(getroomname).child("msg").push().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                load.setVisibility(View.VISIBLE);
                                Map<String, Object> outmap = new HashMap<>();
                                outmap.put("/messege/" + getroomname + "/" + myUid + "/ischat/", "2");
                                reference.updateChildren(outmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                deleteImageISend();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        reference.getRef().child("messege").child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deleteImageISend();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
//        Map<String, Object> map = new HashMap<>();
//        map.put("messege/", "tkdeoqkddlskrkskrk");
//        map.put("time/", "tkdeoqkddlskrkskrk");
//        map.put("uid/", "tkdeoqkddlskrkskrk");
//        map.put("read/", "tkdeoqkddlskrkskrk");
//        reference.child("messege").child(getroomname).child("msg").push().updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Map<String, Object> outmap = new HashMap<>();
//                outmap.put("/messege/" + getroomname + "/" + myUid + "/ischat/", "2");
//                reference.updateChildren(outmap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                deleteImageISend();
//                            }
//                        });
//                    }
//                });
//            }
//        });

//        reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                int i = (int) dataSnapshot.getChildrenCount() + 1;
//                Map<String, Object> outmap = new HashMap<>();
//                outmap.put("/messege/" + getroomname + "/" + myUid + "/ischat/", "2");
//                reference.updateChildren(outmap);
//                Log.d("Fragment4ChatRoom>>>", "set ischat success");
//                reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        deleteImageISend();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
    }

    private void deleteImageISend() {
        storageReference.child(myUid + "/" + getroomname + "/").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d("Fragment4ChatRoom>>>", "ddd1");
                int i = listResult.getItems().size();
                Log.d("Fragment4ChatRoom>>>", "ddd1 get count: " + i);
                if (i >= 1) {
                    for (StorageReference item : listResult.getItems()) {
                        Log.d("Fragment4ChatRoom>>>", "ddd2");
                        storageReference.child(myUid + "/" + getroomname + "/" + item.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                outAlready = true;
                                finishedAll = true;
                                Log.d("Fragment4ChatRoom>>>", "deleteImageISend success");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Fragment4ChatRoom>>>", "ddd3");
                            }
                        });
                    }
                } else {
                    outAlready = true;
                    finishedAll = true;
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fragment4ChatRoom>>>", "ddd4");
            }
        });
    }

    private void deleteImageISendB() {
        storageReference.child(myUid + "/" + getroomname + "/").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int i = listResult.getItems().size();
                if (i >= 1) {
                    for (StorageReference item : listResult.getItems()) {
                        storageReference.child(myUid + "/" + getroomname + "/" + item.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteAllChatRoom();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                } else {
                    deleteAllChatRoom();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fragment4ChatRoom>>>", "check state: " + "4");
            }
        });
    }

    private void checkOut() {
        reference.getRef().child("messege").child(getroomname).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int i = (int) dataSnapshot.getChildrenCount();
                if (i == 5) {

                } else {

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressBack = true;
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Fragment4.stayf4chatroom = false;
    }

    private void hideKB() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void sendNotify(String userUid, String messege, String myName) {
        db.collection("users").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                reference.getRef().child("messege").child(getroomname).child(userUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String state = dataSnapshot.child("state").getValue(String.class);
                        if (state.equals("1")) {
                            String userToken = documentSnapshot.get("token").toString();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject root = new JSONObject();
//                                        JSONObject notification = new JSONObject();
                                        JSONObject data = new JSONObject();
//                                        notification.put("body", messege);
//                                        notification.put("title", getString(R.string.app_name));
                                        data.put("messege", messege);
                                        data.put("name", myName);
                                        data.put("pic", myPic);
                                        data.put("uid", myUid);
                                        Log.d("Fragment4ChatRoom>>>", myName);
//                                        root.put("notification", notification);
                                        root.put("data", data);
                                        root.put("to", userToken);

                                        URL Url = new URL(FCM_MESSAGE_URL);
                                        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                                        connection.setRequestMethod("POST");
                                        connection.setDoOutput(true);
                                        connection.setDoInput(true);
                                        connection.addRequestProperty("Authorization", "key=" + serverKey);
                                        connection.setRequestProperty("Accept", "application/json");
                                        connection.setRequestProperty("Content-type", "application/json");
                                        OutputStream os = connection.getOutputStream();
                                        os.write(root.toString().getBytes("utf-8"));
                                        os.flush();
                                        connection.getResponseCode();

                                        Log.d("Fragment4ChatRoom>>>", "send notify");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                });

            }
        });
    }

    private void sendPicNotify(String userUid, String myName) {
        db.collection("users").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                reference.getRef().child("messege").child(getroomname).child(userUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String state = dataSnapshot.child("state").getValue(String.class);
                        if (state.equals("1")) {
                            String userToken = documentSnapshot.get("token").toString();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject root = new JSONObject();
//                                        JSONObject notification = new JSONObject();
                                        JSONObject data = new JSONObject();
//                                        notification.put("body", "사진이 전송되었습니다.");
//                                        notification.put("title", getString(R.string.app_name));
                                        data.put("messege", "사진이 전송되었습니다.");
                                        data.put("name", myName);
                                        data.put("pic", myPic);
                                        data.put("uid", myUid);
                                        Log.d("Fragment4ChatRoom>>>", myName);
//                                        root.put("notification", notification);
                                        root.put("data", data);
                                        root.put("to", userToken);

                                        URL Url = new URL(FCM_MESSAGE_URL);
                                        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                                        connection.setRequestMethod("POST");
                                        connection.setDoOutput(true);
                                        connection.setDoInput(true);
                                        connection.addRequestProperty("Authorization", "key=" + serverKey);
                                        connection.setRequestProperty("Accept", "application/json");
                                        connection.setRequestProperty("Content-type", "application/json");
                                        OutputStream os = connection.getOutputStream();
                                        os.write(root.toString().getBytes("utf-8"));
                                        os.flush();
                                        connection.getResponseCode();

                                        Log.d("Fragment4ChatRoom>>>", "send notify");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                });

            }
        });
    }

    private void setGobackbtn() {
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
