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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Fragment4ChatRoom extends AppCompatActivity {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

//    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatRoomItem> arrayList;
    private ChatRoomAdapter adapter;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment4_chat_room);

        setinit();
    }

    private void getMyProfile() {
        DocumentReference documentReference = db.collection("users").document(myUid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("pic") == null) {
                    if (documentSnapshot.getString("sex").equals("남자")) {
                        myPic = nullPic;
                    } else {
                        myPic = nullPicF;
                    }
                } else {
                    myPic = documentSnapshot.getString("pic");
                }
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

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Fragment4ChatRoom.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        adapter = new ChatRoomAdapter(Fragment4ChatRoom.this, arrayList);
        recyclerView.setAdapter(adapter);

        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                if (bottom < oldBottom) {
//                    view.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
//                        }
//                    }, 100);
//                }
//            }
//        });

        reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("Fragment4ChatRoom>>>", snapshot.getKey());
                    Log.d("Fragment4ChatRoom>>>", myUiduserUid);
                    Log.d("Fragment4ChatRoom>>>", userUidmyUid);
                    if (snapshot.getKey().equals(myUiduserUid)) {
                        getroomname = myUiduserUid;
                        setRecyclerviewList(getroomname);
                    } else if (snapshot.getKey().equals(userUidmyUid)) {
                        getroomname = userUidmyUid;
                        setRecyclerviewList(getroomname);
                    }
                }
            }
        });
    }

    private void setRecyclerviewList(String roomName) {
        getroomname = roomName;
        reference.getRef().child("messege").child(roomName).child("msg").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Log.d("Fragment4ChatRoom>>>", snapshot1.getKey());
                    ChatRoomItem chatRoomItem = snapshot1.getValue(ChatRoomItem.class);
                    arrayList.add(chatRoomItem);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getNewMessege();
    }

    private void getNewMessege() {
        reference.getRef().child("messege").child(getroomname).child("msg").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatRoomItem chatRoomItem = snapshot.getValue(ChatRoomItem.class);
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

    private void setSendmsg() {
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = editText.getText().toString();
                reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int i = (int) dataSnapshot.getChildrenCount() + 1;
                        Map<String, Object> sendmsg = new HashMap<>();
                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/messege/", messege);
                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/name/", myName);
                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/pic/", myPic);
                        sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/time/", getTime());

                        sendmsg.put("/messege/" + getroomname + "/lastmsg/" + "lastmessege/", messege);
                        sendmsg.put("/messege/" + getroomname + "/lastmsg/" + "lasttime/", getTime());
                        reference.updateChildren(sendmsg);
                        editText.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Fragment4ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
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
            Log.d("ChatRoom>>>", "imageUri: " + imageUri);
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
                    Log.d("ChatRoom>>>", "image count: " + Integer.toString(i));
                    riverRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child(myUid + "/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    picUri = uri.toString();
                                    Log.d("ChatRoom>>>", "onSuccess: " + uri.toString());
                                    dialogImage(picUri);
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

        if (!Fragment4ChatRoom.this.isFinishing()) {
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
        Log.d("ChatRoom>>>", "time: " + getTime());
        editText.setText("");

        sendMessegeMapPic(uri, myName, myPic);
    }

    private void sendMessegeMapPic(String uri, String myName, String myPic) {
        reference.getRef().child("messege").child(getroomname).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int i = (int) dataSnapshot.getChildrenCount() + 1;
                Map<String, Object> sendmsg = new HashMap<>();
                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/uri/", uri);
                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/name/", myName);
                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/pic/", myPic);
                sendmsg.put("/messege/" + getroomname + "/msg/" + i + "/time/", getTime());

                sendmsg.put("/messege/" + getroomname + "/lastmsg/" + "lastmessege/", "사진이 전송되었습니다.");
                sendmsg.put("/messege/" + getroomname + "/lastmsg/" + "lasttime/", getTime());
                reference.updateChildren(sendmsg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Fragment4ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOutroombtn() {
        outroombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.getRef().child("messege").child(getroomname).child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StorageReference removeStorage = storageReference.child(myUid + "/" + getroomname);
                        removeStorage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        String date = format.format(mDate);
        return date;
    }
}
