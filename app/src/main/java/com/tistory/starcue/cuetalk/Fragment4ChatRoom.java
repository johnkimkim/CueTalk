package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.tistory.starcue.cuetalk.adpater.F4ReAdapter;
import com.tistory.starcue.cuetalk.fragment.Fragment4;
import com.tistory.starcue.cuetalk.item.F4ChatRoomItem;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment4ChatRoom extends AppCompatActivity {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    //    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<F4ChatRoomItem> arrayList;
    private F4ChatRoomAdapter adapter;

    RecyclerView recyclerView;
    Button sendimg, sendmsg, gobackbtn, outroombtn;
    EditText editText;
    ProgressBar progressBar;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    String myUid, userUid, myUiduserUid, userUidmyUid;
    String getroomname;
    String myName, myPic, myTime;

    Uri imageUri;
    String picUri;

    AlertDialog alertDialogC;
    AlertDialog alertDialogD;

    boolean isOpen;
    boolean outAlready;
    boolean finishedAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment4_chat_room);

        isOpen = false;
        outAlready = false;
        setinit();
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
        userUid = intent.getStringExtra("child");
        myUiduserUid = myUid + userUid;
        userUidmyUid = userUid + myUid;
    }

    private void setinit() {
        recyclerView = findViewById(R.id.fragment4_chat_room_recyclerview);
        sendimg = findViewById(R.id.fragment4_chat_room_sendimage);
        sendmsg = findViewById(R.id.fragment4_chat_room_sendbutton);
        gobackbtn = findViewById(R.id.fragment4_chat_room_backbtn);
        outroombtn = findViewById(R.id.fragment4_chat_room_outroom);
        editText = findViewById(R.id.fragment4_chat_room_edittext);
        progressBar = findViewById(R.id.fragment4_chat_room_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        setdb();

        setRecyclerView();

        setOutroombtn();
    }

    private void setdb() {
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        myUid = mAuth.getUid();
        getMyProfile();
        getUserUid();
        setSendmsg();
        setSendimg();
    }

    private void setState() {
        Map<String, Object> setState = new HashMap<>();
        setState.put("/messege/" + getroomname + "/" + myUid + "/state/", "2");//채팅중
        reference.updateChildren(setState);
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
        adapter = new F4ChatRoomAdapter(Fragment4ChatRoom.this, arrayList);
        recyclerView.setAdapter(adapter);

        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

        reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("Fragment4ChatRoom>>>", snapshot.getKey());
                    Log.d("Fragment4ChatRoom>>>", myUiduserUid);
                    Log.d("Fragment4ChatRoom>>>", userUidmyUid);
                    if (snapshot.getKey().equals(myUiduserUid)) {
                        getroomname = myUiduserUid;
                        setRecyclerviewList();
                    } else if (snapshot.getKey().equals(userUidmyUid)) {
                        getroomname = userUidmyUid;
                        setRecyclerviewList();
                    }
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
        getNewMessege();
        checkUserIsChat();
        setState();

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
                progressBar.setVisibility(View.GONE);

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
                F4ChatRoomItem chatRoomItem = snapshot.getValue(F4ChatRoomItem.class);
                arrayList.add(chatRoomItem);
                adapter.notifyDataSetChanged();

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 100);

                progressBar.setVisibility(View.GONE);
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

    private void setSendmsg() {
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = editText.getText().toString();

                Map<String, Object> sendmsg = new HashMap<>();
                Map<String, Object> lastmsg = new HashMap<>();
                sendmsg.put("messege", messege);
                sendmsg.put("name", myName);
                sendmsg.put("time", getTime());
                sendmsg.put("read", "1");

                lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lastmessege/", messege);
                lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lasttime/", getTime());
                reference.child("messege").child(getroomname).child("msg").push().updateChildren(sendmsg);
                reference.updateChildren(lastmsg);

                editText.setText("");

//                reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                    @Override
//                    public void onSuccess(DataSnapshot dataSnapshot) {
//                        int i = (int) dataSnapshot.getChildrenCount() + 1;
//                        Map<String, Object> sendmsg = new HashMap<>();
//                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/messege/", messege);
//                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/name/", myName);
//                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/pic/", myPic);
//                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/time/", getTime());
//                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/read/", "1");
//
//                        sendmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lastmessege/", messege);
//                        sendmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lasttime/", getTime());
//
//                        reference.updateChildren(sendmsg);
//
//                        editText.setText("");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(Fragment4ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                });
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
                                    dialogImage(picUri);
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

    private void dialogImage(String picUri) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.send_iamge_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Fragment4ChatRoom.this);
        builder.setView(layout);
        alertDialogC = builder.create();
        Log.d("Fragment4ChatRoom>>>", "dialogImage");

        if (!Fragment4ChatRoom.this.isFinishing()) {
            Log.d("Fragment4ChatRoom>>>", "dialogImage2");
            alertDialogC.show();
            //set size
            WindowManager.LayoutParams layoutParams = alertDialogC.getWindow().getAttributes();
            layoutParams.copyFrom(alertDialogC.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialogC.getWindow().setAttributes(layoutParams);
        }

        ImageView imageView = layout.findViewById(R.id.dialog_img_set);
        Button okbtn = layout.findViewById(R.id.send_image_okbtn);

        Glide.with(Fragment4ChatRoom.this).load(picUri).override(100, 100).centerCrop().into(imageView);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogC.dismiss();
                sendMessegePic(picUri);
            }
        });
    }

    private void sendMessegePic(String uri) {
        Log.d("Fragment4ChatRoom>>>", "time: " + getTime());
        editText.setText("");

        sendMessegeMapPic(uri, myName, myPic);
    }

    private void sendMessegeMapPic(String uri, String myName, String myPic) {
        Map<String, Object> sendmsgpic = new HashMap<>();
        Map<String, Object> lastmsg = new HashMap<>();
        sendmsgpic.put("uri", uri);
        sendmsgpic.put("name", myName);
        sendmsgpic.put("pic", myPic);
        sendmsgpic.put("time", getTime());
        sendmsgpic.put("read", "1");

        lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lastmessege/", "사진이 전송되었습니다.");
        lastmsg.put("/messege/" + getroomname + "/lastmsg" + getroomname + "/lasttime/", getTime());
        reference.child("messege").child(getroomname).child("msg").push().updateChildren(sendmsgpic);
        reference.updateChildren(lastmsg);

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

    private void goOutDialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.go_out_chat_room_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Fragment4ChatRoom.this);
        builder.setView(layout);
        alertDialogD = builder.create();

        if (!Fragment4ChatRoom.this.isFinishing()) {
            alertDialogD.show();
            //set size
            WindowManager.LayoutParams layoutParams = alertDialogD.getWindow().getAttributes();
            layoutParams.copyFrom(alertDialogD.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialogD.getWindow().setAttributes(layoutParams);
        }

        Button okbtn = layout.findViewById(R.id.go_out_chat_room_dialog_okbtn);
        Button cancel = layout.findViewById(R.id.go_out_chat_room_dialog_cancelbtn);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Fragment4ChatRoom>>>", "first out onclick");
                goOutSetIsChat();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogD.dismiss();
            }
        });
    }

    private void userGoOutDialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.go_out_user_chat_room_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Fragment4ChatRoom.this);
        builder.setView(layout);
        alertDialogD = builder.create();

        if (!Fragment4ChatRoom.this.isFinishing()) {
            alertDialogD.show();
            Log.d("Fragment4ChatRoom>>>", "dialogImageD show");
            //set size
            WindowManager.LayoutParams layoutParams = alertDialogD.getWindow().getAttributes();
            layoutParams.copyFrom(alertDialogD.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialogD.getWindow().setAttributes(layoutParams);
        }

        Button okbtn = layout.findViewById(R.id.fragment4_chat_room_dialog_user_out_okbtn);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Fragment4ChatRoom>>>", "okbtn onclick");
                deleteImageISendB();
            }
        });
    }

    private void deleteAllChatRoom() {
        reference.getRef().child("messege").child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        outAlready = true;
                        alertDialogD.dismiss();
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

    private void goOutSetIsChat() {
        reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int i = (int) dataSnapshot.getChildrenCount() + 1;
                Map<String, Object> outmap = new HashMap<>();
                outmap.put("/messege/" + getroomname + "/" + myUid + "/ischat/", "2");
                outmap.put("/messege/" + getroomname + "/msg/" + i + "/messege/", "tkdeoqkddlskrkskrk");
                outmap.put("/messege/" + getroomname + "/msg/" + i + "/time/", "tkdeoqkddlskrkskrk");
                reference.updateChildren(outmap);
                Log.d("Fragment4ChatRoom>>>", "set ischat success");
                reference.getRef().child("myroom").child(myUid).child(getroomname).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteImageISend();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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
                                alertDialogD.dismiss();
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
                    alertDialogD.dismiss();
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
    protected void onPause() {
        super.onPause();

//        if (!outAlready) {
//            setOutState();
//        }
        setOutState();

    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss");
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

}
