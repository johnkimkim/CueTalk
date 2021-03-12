package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {

    private ArrayList<ChatRoomItem> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    private ChatRoomAdapter adapter;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String myUid;

    private RecyclerView recyclerView;
    private Button addbtn, sendbtn;
    private EditText edit;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    AlertDialog alertDialog;
    AlertDialog alertDialogA;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);



        setinit();
        setdb();

        setinit();
        setRecyclerView();
        checkDbChange();
        sendMessege();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ChatRoom.this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        adapter = new ChatRoomAdapter(ChatRoom.this, arrayList);

//        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

        recyclerView.setAdapter(adapter);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });

        reference.getRef().child("inchat").child(getMyWhere()).child("messege").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatRoomItem chatRoomItem = snapshot.getValue(ChatRoomItem.class);//error
                arrayList.add(chatRoomItem);
                adapter.notifyDataSetChanged();

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                }, 100);
//                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
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
//                    db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            if (documentSnapshot.get("pic") != null) {
//                                Log.d("ChatRoom>>>", "pic have");
//
//                            } else {
//                                Log.d("ChatRoom>>>", "pic null");
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
                    reference.getRef().child("adressRoom").child(getMyAdress()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            String myName = dataSnapshot.child("name").getValue().toString();
                            if (dataSnapshot.child("pic").getValue() != null) {
                                Log.d("ChatRoom>>>", "pic have");
                                String myPic = dataSnapshot.child("pic").getValue().toString();
                                sendMessegeMap(messege, myName, myPic);
                            } else {
                                Log.d("ChatRoom>>>", "pic null");
                                if (dataSnapshot.child("sex").getValue().toString().equals("남자")) {
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
        reference.getRef().child("inchat").child(getMyWhere()).child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount() + 1;
                Map<String, Object> sendMessege = new HashMap<>();
                sendMessege.put("/inchat/" + getMyWhere() + "/" + "messege" + "/" + count + "/" + "messege" + "/", messege);
                sendMessege.put("/inchat/" + getMyWhere() + "/" + "messege" + "/" + count + "/" + "name" + "/", myName);
                sendMessege.put("/inchat/" + getMyWhere() + "/" + "messege" + "/" + count + "/" + "pic" + "/", myPic);
                sendMessege.put("/inchat/" + getMyWhere() + "/" + "messege" + "/" + count + "/" + "time" + "/", getTime());
                reference.updateChildren(sendMessege);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatRoom.this, "인터넷 연결이 불안정해 메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setinit() {
        recyclerView = findViewById(R.id.chat_room_recyclerview);
        addbtn = findViewById(R.id.chat_room_sendimage);
        sendbtn = findViewById(R.id.chat_room_sendbutton);
        edit = findViewById(R.id.chat_room_edittext);
    }

    @Override
    public void onBackPressed() {
        dialog();
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
                deleteMydb();
                databaseHandler.deleteWhere();
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
        alertDialogA = builder.create();

        if (!ChatRoom.this.isFinishing()) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            alertDialogA.show();
        }

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        Button okbtn = layout.findViewById(R.id.inchat_dialog_if_user_out_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMydbA();
                databaseHandler.deleteWhere();
//                arrayList.clear();
            }
        });
    }

    private void checkDbChange() {
        String adress = getAdress();
        String myUid = mAuth.getUid();

        reference.getRef().child("adressRoom").child(adress).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String where = dataSnapshot.child("where").getValue(String.class);
                Log.d("ChatRoom>>>", "get where: " + where);
                reference.getRef().child("inchat").child(where).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        int i = (int) snapshot.getChildrenCount();
//                        Log.d("ChatRoom>>>", Integer.toString(i));
////                        dialogA();
                        reference.getRef().child("inchat").child(where).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int test = (int) snapshot.child("messege").getChildrenCount();
                                if (test == 0) {
                                    int i = (int) snapshot.getChildrenCount();
                                    String s = Integer.toString(i);
                                    Log.d("ChatRoom>>>", "count " + s);
                                    if (i == 1) {
                                        dialogA();
                                    }
                                } else {
                                    int i = (int) snapshot.getChildrenCount();
                                    String s = Integer.toString(i);
                                    Log.d("ChatRoom>>>", "count " + s);
                                    if (i == 2) {
                                        dialogA();
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteMydb() {//방장 삭제
        String adress = getAdress();
        String myUid = mAuth.getUid();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/ischat/", 1);
        reference.updateChildren(updateUser);

        reference.getRef().child("adressRoom").child(adress).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String where = dataSnapshot.child("where").getValue(String.class);//success

                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/where/", null);
                reference.updateChildren(updateUser);
                alertDialog.dismiss();
                ChatRoom.this.finish();
                reference.getRef().child("inchat").child(where).child(myUid).removeValue();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteMydbA() {//방장 삭제
        String adress = getAdress();
        String myUid = mAuth.getUid();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/ischat/", 1);
        reference.updateChildren(updateUser);

        reference.getRef().child("adressRoom").child(adress).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String where = dataSnapshot.child("where").getValue(String.class);//success
                reference.getRef().child("inchat").child(where).removeValue();
                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("/adressRoom/" + adress + "/" + myUid + "/" + "/where/", null);
                reference.updateChildren(updateUser);
                alertDialogA.dismiss();
                ChatRoom.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String getAdress() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        return adress;
    }

    private void setdb() {
        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        databaseHandler.setDB(ChatRoom.this);
        databaseHandler = new DatabaseHandler(ChatRoom.this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        myUid = mAuth.getUid();
    }

    private String getMyAdress() {
        Cursor cursor = sqLiteDatabase.rawQuery("select adressField from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String where = cursor.getString(0);
        return where;
    }

    private String getMyWhere() {
        Cursor cursor = sqLiteDatabase.rawQuery("select whereField from whereTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String where = cursor.getString(0);
        return where;
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        String date = format.format(mDate);
        return date;
    }

    private String getDate() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        String date = format.format(mDate);
        return date;
    }

}
