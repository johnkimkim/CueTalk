package com.tistory.starcue.cuetalk.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.DatabaseHandler;

public class ChatRoomKillAppService extends Service {

    DatabaseHandler databaseHandler;
    SQLiteDatabase sqLiteDatabase;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    StorageReference storageReference;
    int startid;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//service started
        Log.d("ChatRoomService>>>", "Service started");
        startid = startId;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {//service stoped
        super.onDestroy();
        Log.d("ChatRoomService>>>", "Service Destoryed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {//code here when kill app

        databaseHandler.setDB(ChatRoomKillAppService.this);
        databaseHandler = new DatabaseHandler(ChatRoomKillAppService.this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        String myUid = mAuth.getUid();
        reference = FirebaseDatabase.getInstance().getReference();

        Log.d("ChatRoomService>>>", "END: " + getRoomname());

        String adress = getAdress();
        String adressm = adress.substring(0, adress.length() - 1);
        reference.getRef().child("adressRoom").child(adressm).child(adress).child(myUid).removeValue();
        reference.getRef().child("inchat").child(getRoomname()).child(myUid).removeValue();

//        reference.getRef().child("inchat").child(getRoomname()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                Log.d("ChatRoomService>>>", "END 2");
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if (!snapshot.getKey().equals(myUid) && !snapshot.getKey().equals("messege")) {
//                        Log.d("ChatRoomService>>>", "END 3");
////                        deleteMyStoragePic(myUid);//adress chatroom 에서 보냈던 사진 삭제
//                        if (snapshot.child("ischat").getValue(String.class).equals("1")) {//내가 먼저 나갈때
//                            Log.d("ChatRoomService>>>", "END 4");
//
//                            Map<String, Object> updateUser = new HashMap<>();
//                            updateUser.put("/inchat/" + getRoomname() + "/" + myUid + "/ischat/", "2");
//                            reference.updateChildren(updateUser);
//
//                        } else if (snapshot.child("ischat").getValue(String.class).equals("2")) {//상대방이 먼저 나갔을때4
//                            Log.d("ChatRoomService>>>", "ischat is 2");
//
//                            reference.getRef().child("inchat").child(getRoomname()).removeValue();
//                        }
//                    }
//                }
//            }
//        });

        stopSelf();
    }

    private String getAdress() {//adress
        Cursor cursor = sqLiteDatabase.rawQuery("select * from adress where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        cursor.close();
        return adress;
    }

    private String getRoomname() {//inchat room name
        Cursor cursor = sqLiteDatabase.rawQuery("select * from roomname where _rowid_ = 1", null);
        cursor.moveToFirst();
        String adress = cursor.getString(0);
        cursor.close();
        return adress;
    }
}
