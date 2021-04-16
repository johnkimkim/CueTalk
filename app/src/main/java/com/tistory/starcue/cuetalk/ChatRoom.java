package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.google.firebase.database.ValueEventListener;
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
import java.util.Map;

public class ChatRoom extends AppCompatActivity {

    private ArrayList<ChatRoomItem> arrayList;
    private ChatRoomAdapter adapter;

    private DatabaseReference reference;
    private DatabaseReference reference1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    StorageReference storageReference;
    String myUid;

    private RecyclerView recyclerView;
    private Button addbtn, sendbtn, backbtn, callbtn, sendMessegeBtn;
    private TextView title;
    private EditText edit;
    private ProgressBar progressbar;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    AlertDialog alertDialog;
    AlertDialog alertDialogA;
    AlertDialog alertDialogC;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private boolean isClickBtn = false;

    Uri imageUri;
    String picUri;

    String where;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        setinit();
        setdb();

        setOnClickBackbtn();

        setRecyclerView();
        checkDbChange();
        sendMessege();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatRoom.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        adapter = new ChatRoomAdapter(ChatRoom.this, arrayList);

        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        recyclerView.setAdapter(adapter);

        reference.getRef().child("inchat").child(where).child("messege").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("ChatRoom>>>", "messege get key: " + snapshot.getKey());
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
        Log.d("ChatRoom>>>", "time: " + getTime());
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messege = edit.getText().toString();
                if (messege.length() == 0) {
                    Toast.makeText(ChatRoom.this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    edit.setText("");
                    reference.getRef().child("adressRoom").child(getMyAdress()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            String myName = dataSnapshot.child("name").getValue(String.class);
                            if (dataSnapshot.child("pic").getValue() != null) {
                                Log.d("ChatRoom>>>", "pic have");
                                String myPic = dataSnapshot.child("pic").getValue(String.class);
                                sendMessegeMap(messege, myName, myPic);
                            } else {
                                Log.d("ChatRoom>>>", "pic null");
                                if (dataSnapshot.child("sex").getValue(String.class).equals("남자")) {
                                    sendMessegeMap(messege, myName, nullPic);
                                } else {
                                    sendMessegeMap(messege, myName, nullPicF);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });
    }

    private void sendMessegeMap(String messege, String myName, String myPic) {
        Map<String, Object> sendmsg = new HashMap<>();
        sendmsg.put("/messege/", messege);
        sendmsg.put("/name/", myName);
        sendmsg.put("/pic/", myPic);
        sendmsg.put("/time/", getTime());
        reference.child("inchat").child(where).child("messege").push().updateChildren(sendmsg);

//        reference.getRef().child("inchat").child(where).child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                int count = (int) dataSnapshot.getChildrenCount() + 1;
//                Map<String, Object> sendMessege = new HashMap<>();
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "messege" + "/", messege);
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "name" + "/", myName);
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "pic" + "/", myPic);
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "time" + "/", getTime());
//                reference.updateChildren(sendMessege);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void setinit() {
        recyclerView = findViewById(R.id.chat_room_recyclerview);
        addbtn = findViewById(R.id.chat_room_sendimage);
        sendbtn = findViewById(R.id.chat_room_sendbutton);
        edit = findViewById(R.id.chat_room_edittext);
        progressbar = findViewById(R.id.chat_room_progress_bar);
        backbtn = findViewById(R.id.chat_room_backbtn);
        callbtn = findViewById(R.id.chat_room_callbtn);
        title = findViewById(R.id.chat_room_title_user);
        progressbar.setVisibility(View.GONE);
        sendMessegeBtn = findViewById(R.id.chat_room_send_messege);

        setOnAddbtn();
    }

    private void sendMessegeBtnOnClick() {
        sendMessegeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                reference.getRef().child("inchat").child(where).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
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
                                            sendMessege.setSendMessegeDialog(ChatRoom.this, userUid);
                                        } else {
                                            if (dataSnapshot.hasChild(roomkey) || dataSnapshot.hasChild(roomkey1)) {
                                                Toast.makeText(ChatRoom.this, "이미 대화 중 입니다. 메시지함을 확인해주세요", Toast.LENGTH_SHORT).show();
                                            } else {
                                                SendMessege sendMessege = new SendMessege(ChatRoom.this);
                                                sendMessege.setSendMessegeDialog(ChatRoom.this, userUid);
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

    private void dialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.inchat_dialog_if_i_out, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
        builder.setView(layout);
        alertDialog = builder.create();

        if (!ChatRoom.this.isFinishing()) {
            alertDialog.show();
            //set size
            WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        Button okbtn = layout.findViewById(R.id.inchat_dialog_if_i_out_okbtn);
        Button cancelbtn = layout.findViewById(R.id.inchat_dialog_if_i_out_cancelbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickBtn = true;
                okbtn.setEnabled(false);
                progressbar.setVisibility(View.VISIBLE);
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

    private void dialogA() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.inchat_dialog_if_user_out, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialogA = builder.create();

        if (!ChatRoom.this.isFinishing()) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            alertDialogA.show();
            progressbar.setVisibility(View.GONE);
        }

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        Button okbtn = layout.findViewById(R.id.inchat_dialog_if_user_out_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okbtn.setEnabled(false);
                progressbar.setVisibility(View.VISIBLE);
                deleteMydbA();
            }
        });
    }

    private void dialogImage(String picUri) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.send_iamge_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
        builder.setView(layout);
        alertDialogC = builder.create();

        if (!ChatRoom.this.isFinishing()) {
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

        Glide.with(ChatRoom.this).load(picUri).override(100, 100).centerCrop().into(imageView);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogC.dismiss();
                sendMessegePic(picUri);
            }
        });
    }

    private void checkDbChange() {
        reference.getRef().child("inchat").child(where).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                reference.getRef().child("inchat").child(where).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressbar.setVisibility(View.VISIBLE);
                        int test = (int) snapshot.child("messege").getChildrenCount();
                        if (test == 0) {
                            int i = (int) snapshot.getChildrenCount();
                            String s = Integer.toString(i);
                            Log.d("ChatRoom>>>", "count " + s);
                            if (i == 1) {
                                reference.getRef().child("adressRoom").child(getMyAdress()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        Long l = dataSnapshot.child("ischat").getValue(Long.class);
                                        if (l != null) {
                                            int i = l.intValue();
                                            if (i == 2) {
                                                dialogA();
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            int i = (int) snapshot.getChildrenCount();
                            String s = Integer.toString(i);
                            Log.d("ChatRoom>>>", "count " + s);
                            if (i == 2) {
                                reference.getRef().child("adressRoom").child(getMyAdress()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        Long l = dataSnapshot.child("ischat").getValue(Long.class);
                                        if (l != null) {
                                            int i = l.intValue();
                                            if (i == 2) {
                                                dialogA();
                                            }
                                        }
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

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteMydb() {//방장 삭제
        String adress = getAdress();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/ischat/", 1);
        reference.updateChildren(updateUser);

        deleteMyStoragePic();

        reference.getRef().child("inchat").child(where).child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isClickBtn = true;

                Map<String, Object> updateUser1 = new HashMap<>();
                updateUser1.put("/adressRoom/" + adress + "/" + myUid + "/" + "/where/", null);
                reference.updateChildren(updateUser1);
                alertDialog.dismiss();
                goToAdressRoom();

                progressbar.setVisibility(View.GONE);
            }
        });

    }

    private void deleteMydbA() {
        String adress = getAdress();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/ischat/", 1);
        reference.updateChildren(updateUser);

        deleteMyStoragePic();
        reference.getRef().child("inchat").child(where).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String, Object> updateUser1 = new HashMap<>();
                updateUser1.put("/adressRoom/" + adress + "/" + myUid + "/" + "/where/", null);
                reference.updateChildren(updateUser1);
                alertDialogA.dismiss();
                goToAdressRoom();
                progressbar.setVisibility(View.GONE);
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
        where = getIntent().getStringExtra("intentwhere");

        sendMessegeBtnOnClick();

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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss");
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
        reference.getRef().child("adressRoom").child(getMyAdress()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String myName = dataSnapshot.child("name").getValue(String.class);
                if (dataSnapshot.child("pic").getValue() != null) {
                    Log.d("ChatRoom>>>", "pic have");
                    String myPic = dataSnapshot.child("pic").getValue(String.class);
                    sendMessegeMapPic(uri, myName, myPic);
                } else {
                    Log.d("ChatRoom>>>", "pic null");
                    if (dataSnapshot.child("sex").getValue(String.class).equals("남자")) {
                        sendMessegeMapPic(uri, myName, nullPic);
                    } else {
                        sendMessegeMapPic(uri, myName, nullPicF);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void sendMessegeMapPic(String uri, String myName, String myPic) {
        Map<String, Object> sendimgmap = new HashMap<>();
        sendimgmap.put("uri", uri);
        sendimgmap.put("name", myName);
        sendimgmap.put("pic", myPic);
        reference.child("inchat").child(where).child("messege").push().updateChildren(sendimgmap);

//        reference.getRef().child("inchat").child(where).child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                int count = (int) dataSnapshot.getChildrenCount() + 1;
//                Map<String, Object> sendMessege = new HashMap<>();
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "uri" + "/", uri);
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "name" + "/", myName);
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "pic" + "/", myPic);
//                sendMessege.put("/inchat/" + where + "/" + "messege" + "/" + count + "/" + "time" + "/", getTime());
//                reference.updateChildren(sendMessege);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void deleteMyStoragePic() {
        isClickBtn = true;
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

}
