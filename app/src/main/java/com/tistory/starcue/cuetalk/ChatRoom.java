package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tistory.starcue.cuetalk.adpater.ChatRoomAdapter;
import com.tistory.starcue.cuetalk.item.ChatRoomItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {

    private ArrayList<ChatRoomItem> arrayList;
    private ChatRoomAdapter adapter;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    StorageReference storageReference;
    String myUid;
    String myName, myPic, myLatitude, myLongitude, userLatitude, userLongitude;

    private LinearLayout titlelayout;
    private RecyclerView recyclerView;
    private Button addbtn, sendbtn, backbtn, callbtn, sendMessegeBtn;
    private TextView userName, userSex, userAge, userKm;
    private EditText edit;
    private RelativeLayout load;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    AlertDialog alertDialog;
    AlertDialog alertDialogA;
    AlertDialog alertDialogC;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    boolean imakegoout;
    boolean willSendImg;

    Uri imageUri;
    String picUri;

//    String where;
//    String wherem;

    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        where = getIntent().getStringExtra("intentwhere");//양쪽 다 where 가지고있음
////        wherem = where.substring(0, where.length() - 1);
//        Log.d("ChatRoom>>>", "test get room name: " + where);

        AdressRoom.goChatRoom = false;
        activity = ChatRoom.this;
        imakegoout = false;
        willSendImg = false;

//        startService(new Intent(ChatRoom.this, ChatRoomKillAppService.class));

        setinit();
        setdb();

        setOnClickBackbtn();

        checkDbChange();
        sendMessege();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Log.d("ChatRoom>>>", "onCreate: " + String.valueOf(this.hasWindowFocus()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ChatRoom>>>", "onStart " + String.valueOf(this.hasWindowFocus()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ChatRoom>>>", "onResume " + String.valueOf(this.hasWindowFocus()));
    }

    private void setinit() {
        titlelayout = findViewById(R.id.chat_room_title_user_layout);
        titlelayout.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.chat_room_recyclerview);
        addbtn = findViewById(R.id.chat_room_sendimage);
        sendbtn = findViewById(R.id.chat_room_sendbutton);
        edit = findViewById(R.id.chat_room_edittext);
        load = findViewById(R.id.chat_room_progress_load);
        userName = findViewById(R.id.chat_room_user_name);
        userSex = findViewById(R.id.chat_room_user_sex);
        userAge = findViewById(R.id.chat_room_user_age);
        userKm = findViewById(R.id.chat_room_user_km);
        backbtn = findViewById(R.id.chat_room_backbtn);
        callbtn = findViewById(R.id.chat_room_callbtn);
        load.setVisibility(View.GONE);
        sendMessegeBtn = findViewById(R.id.chat_room_send_messege);

        setOnAddbtn();
        setCallBtn();
    }

    private void setdb() {
        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        databaseHandler.setDB(ChatRoom.this);
        databaseHandler = new DatabaseHandler(ChatRoom.this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

//        where = getMyWhere();

        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myName = documentSnapshot.get("name").toString();
                myPic = documentSnapshot.get("pic").toString();
                myLatitude = documentSnapshot.get("latitude").toString();
                myLongitude = documentSnapshot.get("longitude").toString();
            }
        });

        sendMessegeBtnOnClick();
        setTitle();
        setRecyclerView();
    }

    private void setTitle() {
        reference.getRef().child("inchat").child(getRoomname()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals(myUid) && !snapshot.getKey().equals("messege")) {
                        db.collection("users").document(snapshot.getKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userName.setText(documentSnapshot.get("name").toString());
                                userSex.setText(documentSnapshot.get("sex").toString());
                                if (documentSnapshot.get("sex").toString().equals("남자")) {
                                    userSex.setTextColor(getResources().getColor(R.color.male));
                                } else {
                                    userSex.setTextColor(getResources().getColor(R.color.female));
                                }
                                userAge.setText(documentSnapshot.get("age").toString());
                                userLatitude = documentSnapshot.get("latitude").toString();
                                userLongitude = documentSnapshot.get("longitude").toString();

                                double myLatitudeD = Double.parseDouble(myLatitude);
                                double myLongitudeD = Double.parseDouble(myLongitude);
                                double userLatitudeD = Double.parseDouble(userLatitude);
                                double userLongitudeD = Double.parseDouble(userLongitude);
                                double ddd = getDistance(myLatitudeD, myLongitudeD, userLatitudeD, userLongitudeD);
                                if (ddd < 50) {
                                    userKm.setText("-50m");
                                } else if (ddd < 1000) {
                                    int i = (int) Math.floor(ddd);
                                    userKm.setText(Integer.toString(i) + "m");
                                } else if (ddd >= 1000) {
                                    int i = (int) Math.floor(ddd) / 1000;
                                    userKm.setText(Integer.toString(i) + "km");
                                }
                                titlelayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatRoom.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        adapter = new ChatRoomAdapter(ChatRoom.this, arrayList, Glide.with(ChatRoom.this));

        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        recyclerView.setAdapter(adapter);

        String roomname = getRoomname();
        reference.getRef().child("inchat").child(getRoomname()).child("messege").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatRoomItem chatRoomItem = snapshot.getValue(ChatRoomItem.class);//error

                arrayList.add(chatRoomItem);
                adapter.notifyDataSetChanged();

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 100);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private void sendMessege() {
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = edit.getText().toString();
                if (messege.length() == 0) {
                    Toast.makeText(ChatRoom.this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    edit.setText("");
                    sendMessegeMap(myUid, messege, myName, myPic);
                }
            }
        });
    }

    private void sendMessegeMap(String myUid, String messege, String myName, String myPic) {
        Log.d("ChatRoom>>>", "sendMessegeMap");
        Map<String, Object> sendmsg = new HashMap<>();
        sendmsg.put("/uid/", myUid);
        sendmsg.put("/messege/", messege);
        sendmsg.put("/name/", myName);
        sendmsg.put("/pic/", myPic);
        sendmsg.put("/time/", getTime());
        reference.child("inchat").child(getRoomname()).child("messege").push().updateChildren(sendmsg);
    }

    private void setCallBtn() {
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ChatRoom>>>", "get where: ");
                Log.d("ChatRoom>>>", "get where: " + getRoomname());
                reference.getRef().child("inchat").child(getRoomname()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!snapshot.getKey().equals(myUid) && !snapshot.getKey().equals("messege")) {
                                DecDialog.ChatRoomDecDialog(ChatRoom.this, snapshot.getKey(), myUid, getRoomname(), activity);
                            }
                        }
                    }
                });
            }
        });
    }

    private void sendMessegeBtnOnClick() {
        sendMessegeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.getRef().child("inchat").child(getRoomname()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!snapshot.getKey().equals(myUid) && !snapshot.getKey().equals("messege")) {

                                String userUid = snapshot.getKey();

                                reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        int i = (int) dataSnapshot.getChildrenCount();
                                        String roomkey = userUid + myUid;
                                        String roomkey1 = myUid + userUid;

                                        if (i == 0) {
                                            SendMessege sendMessege = new SendMessege(ChatRoom.this);
                                            sendMessege.setSendMessegeDialog(ChatRoom.this, userUid, activity);
                                        } else {
                                            if (dataSnapshot.hasChild(roomkey) || dataSnapshot.hasChild(roomkey1)) {
                                                Toast.makeText(ChatRoom.this, "이미 대화 중 입니다. 메시지함을 확인해주세요", Toast.LENGTH_SHORT).show();
                                            } else {
                                                SendMessege sendMessege = new SendMessege(ChatRoom.this);
                                                sendMessege.setSendMessegeDialog(ChatRoom.this, userUid, activity);
                                            }
                                        }
                                    }
                                });
                            }
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        dialog();
    }

    private void dialog() {//내가 대화방 나갈때 dialog
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.inchat_dialog_if_i_out, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
        builder.setView(layout);
        alertDialog = builder.create();

        if (!ChatRoom.this.isFinishing()) {
            if (DecDialog.alertDialog != null) {
                DecDialog.alertDialog.dismiss();
            } else if (alertDialogA != null) {
                alertDialogA.dismiss();
            }
            alertDialog.show();
        }

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        Button okbtn = layout.findViewById(R.id.inchat_dialog_if_i_out_okbtn);
        Button cancelbtn = layout.findViewById(R.id.inchat_dialog_if_i_out_cancelbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imakegoout = true;
                alertDialog.dismiss();
                load.setVisibility(View.VISIBLE);
                deleteMydb();
//                arrayList.clear();//error
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void dialogA() {//상대방이 대화방 나갔을때 dialog
        if (alertDialogA == null) {
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) vi.inflate(R.layout.inchat_dialog_if_user_out, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
            builder.setView(layout);
            builder.setCancelable(false);
            alertDialogA = builder.create();

            if (!ChatRoom.this.isFinishing()) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                } else if (DecDialog.alertDialog != null) {
                    DecDialog.alertDialog.dismiss();
                }
            }
            alertDialogA.show();

            /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

            alertDialogA.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Window window = alertDialogA.getWindow();
            int x = (int) (size.x * 0.9);
            int y = (int) (size.y * 0.3);
            window.setLayout(x, y);

            Button okbtn = layout.findViewById(R.id.inchat_dialog_if_user_out_okbtn);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imakegoout = true;
                    okbtn.setEnabled(false);
                    load.setVisibility(View.VISIBLE);
                    deleteMydbA();
                }
            });
        }
    }

    private void checkDbChange() {
        reference.getRef().child("inchat").child(getRoomname()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if (!snapshot.getKey().equals("messege") && !snapshot.getKey().equals(myUid)) {
//                    if (snapshot.child("ischat").getValue(String.class).equals("2")) {
//                        dialogA();
//                    }
//                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                dialogA();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteMydb() {//내가 먼저 대화방 나갔을때
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + getAdress().substring(0, getAdress().length() - 1) + "/" + getAdress() + "/" + myUid + "/" + "/ischat/", 1);
        updateUser.put("/adressRoom/" + getAdress().substring(0, getAdress().length() - 1) + "/" + getAdress() + "/" + myUid + "/" + "/where/", null);

        deleteMyStoragePic();

//        reference.getRef().child("inchat").child(getRoomname()).child(myUid).removeValue();
        reference.getRef().child("inchat").child(getRoomname()).removeValue();
        reference.updateChildren(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                alertDialog.dismiss();
                goToAdressRoom();
            }
        });

    }

    private void deleteMydbA() {//상대방이 대화방 나갔을때
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + getAdress().substring(0, getAdress().length() - 1) + "/" + getAdress() + "/" + myUid + "/" + "/ischat/", 1);
        updateUser.put("/adressRoom/" + getAdress().substring(0, getAdress().length() - 1) + "/" + getAdress() + "/" + myUid + "/" + "/where/", null);
        reference.updateChildren(updateUser);

        deleteMyStoragePic();
        reference.getRef().child("inchat").child(getRoomname()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alertDialogA.dismiss();
                goToAdressRoom();
            }
        });

    }

    private String getAdress() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        cursor.close();
        return adress;
    }

    private String getRoomname() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from roomname where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        cursor.close();
        return adress;
    }

    private String getMyAdress() {
        Cursor cursor = sqLiteDatabase.rawQuery("select adressField from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        cursor.close();
        return adress;
    }

    private String getMyWhere() {
        Log.d("ChatRoom>>>", "getMyWhere()");
        Cursor cursor = sqLiteDatabase.rawQuery("select whereField from whereTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String where = cursor.getString(0);
        cursor.close();
        return where;
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    private void setOnClickBackbtn() {
        backbtn.setOnClickListener(view -> dialog());
    }

    private void setOnAddbtn() {
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                willSendImg = true;
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
            Log.d("ChatRoom>>>", "imageUri: " + imageUri);
            uploadPic();
        }
    }

    private void uploadPic() {
        if (imageUri != null) {

            String s = imageUri.toString();
            String fileName = s.replace("/", "");

            final ProgressDialog pd = new ProgressDialog(ChatRoom.this);
            pd.setTitle("이미지 전송 중...");
            pd.show();
            pd.setCancelable(false);

            StorageReference riverRef = storageReference.child(myUid + "/" + fileName);
            riverRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    int i = listResult.getItems().size() + 1;
                    String count = Integer.toString(i);
                    Log.d("ChatRoom>>>", "image count: " + Integer.toString(i));
                    riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child(myUid + "/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    picUri = uri.toString();
                                    Log.d("ChatRoom>>>", "onSuccess: " + uri.toString());
                                    sendMessegePic(picUri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("ChatRoom>>>", "onFailure: " + e.toString());
                                }
                            });
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ChatRoom.this, "업로드실패", Toast.LENGTH_SHORT).show();
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

    private void sendMessegePic(String uri) {
        Log.d("ChatRoom>>>", "time: " + getTime());
        edit.setText("");
        sendMessegeMapPic(myUid, uri, myName, myPic);
    }

    private void sendMessegeMapPic(String myUid, String uri, String myName, String myPic) {
        Map<String, Object> sendimgmap = new HashMap<>();
        sendimgmap.put("uid", myUid);
        sendimgmap.put("uri", uri);
        sendimgmap.put("name", myName);
        sendimgmap.put("pic", myPic);
        sendimgmap.put("time", getTime());
        reference.child("inchat").child(getRoomname()).child("messege").push().updateChildren(sendimgmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                willSendImg = false;
            }
        });
    }

    private void deleteMyStoragePic() {
        storageReference.child(myUid + "/").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    Log.d("ChatRoom>>>", item.getName());

                    storageReference.child(myUid + "/" + item.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ChatRoom>>>", "delete pic success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ChatRoom>>>", "fail: " + e.toString());
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ChatRoom>>>", e.toString());
            }
        });
    }

    private void goToAdressRoom() {
        Intent intent = new Intent(ChatRoom.this, AdressRoom.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ChatRoom>>>", "onPause " + String.valueOf(this.hasWindowFocus()));
        //hasWindowFocus = 화면에 포커스가 있을때
        if (!imakegoout && !willSendImg) {
            if (hasWindowFocus()) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                } else if (alertDialogA != null) {
                    alertDialogA.dismiss();
                }

                deleteMyStoragePic();
                String adress = getAdress();
                String adressm = adress.substring(0, adress.length() - 1);
                reference.getRef().child("adressRoom").child(adressm).child(adress).child(myUid).removeValue();
                reference.getRef().child("inchat").child(getRoomname()).removeValue();
                Intent intent = new Intent(ChatRoom.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (alertDialogA != null) {
                Log.d("ChatRoom>>>", "onPuase asdlkfj");
                alertDialogA.dismiss();

                deleteMyStoragePic();
                String adress = getAdress();
                String adressm = adress.substring(0, adress.length() - 1);
                reference.getRef().child("adressRoom").child(adressm).child(adress).child(myUid).removeValue();
                reference.getRef().child("inchat").child(getRoomname()).removeValue();
                Intent intent = new Intent(ChatRoom.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
        } else if (alertDialogA != null) {
            alertDialogA.dismiss();
        }
        Log.d("ChatRoom>>>", "onStop " + String.valueOf(this.hasWindowFocus()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        } else if (alertDialogA != null) {
            alertDialogA.dismiss();
        }
        Log.d("ChatRoom>>>", "onDestroy " + String.valueOf(this.hasWindowFocus()));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Log.d("ChatRoom>>>", "c has Focus");
        } else {
            Log.d("ChatRoom>>>", "c null Focus");
        }
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