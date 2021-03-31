package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Fragment4 extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<F4MessegeItem> arrayList;
    private ArrayList<LastListItem> lastList;
    private List<String> arrayKeyList;
    private List<String> lastKeyList;
    private RecyclerView.LayoutManager layoutManager;

    private F4ReAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    FirebaseFirestore db;
    String myUid;
    String intentString;

    GpsTracker gpsTracker;

    SendMessege sendMessege;

    private ProgressBar progressBar;

    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        sendMessege = new SendMessege(getActivity());

        setFirebase();
        setinit(rootView);

        return rootView;
    }

    private void setinit(ViewGroup v) {
        recyclerView = v.findViewById(R.id.fragment4_recyclerview);
        progressBar = v.findViewById(R.id.fragment4_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        setRecyclerView();
    }

    private void setFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        myUid = mAuth.getUid();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        lastList = new ArrayList<>();
        arrayKeyList = new ArrayList<>();
        lastKeyList = new ArrayList<>();

        adapter = new F4ReAdapter(arrayList, lastList, getActivity());
        recyclerView.setAdapter(adapter);

        reference.getRef().child("messege").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().equals("finished")) {
                        if (snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            LastListItem lastListItem = snapshot1.getValue(LastListItem.class);
                            lastList.add(lastListItem);
                            lastKeyList.add(key);
                        } else {
                            String key = snapshot1.getKey();
                            F4MessegeItem f4MessegeItem = snapshot1.getValue(F4MessegeItem.class);
                            arrayList.add(f4MessegeItem);
                            arrayKeyList.add(key);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("Fragment4>>>", "snapshot list: " + snapshot.getKey());

                if (snapshot.getKey().contains(myUid)) {
                    reference.getRef().child("messege").child(snapshot.getKey()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            String ischat = dataSnapshot.child("ischat").getValue(String.class);
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.getKey().contains("lastmsg")) {
                                    if (ischat.equals("1")) {
                                        String key = snapshot1.getKey();
                                        Log.d("Fragment4>>>", "lastmsg change string: " + key);
                                        LastListItem lastListItem = snapshot1.getValue(LastListItem.class);
                                        int index = lastKeyList.indexOf(key);
                                        Log.d("Fragment4>>>", "lastmsg change index: " + index);
                                        lastList.set(index, lastListItem);
                                    } else if (ischat.equals("2")) {
                                        String keysnapshot = "lastmsg" + snapshot.getKey();
                                        int index = lastKeyList.indexOf(keysnapshot);
                                        Log.d("Fragment4>>>", "index lastkeylist string: " + keysnapshot);
                                        Log.d("Fragment4>>>", "index lastkeylist: " + index);
                                        arrayList.remove(index);
                                        arrayKeyList.remove(index);
                                        lastList.remove(index);
                                        lastKeyList.remove(index);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Log.d("Fragment4>>>", "removed: " + snapshot.getKey());//getroomname
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().equals("finished")) {
//                        String key = snapshot1.getKey();
//                        LastListItem lastListItem = snapshot1.getValue(LastListItem.class);
//                        lastList.remove(lastListItem);
//                        lastKeyList.remove(key);
//                    } else {
//                        String key = snapshot1.getKey();
//                        F4MessegeItem f4MessegeItem = snapshot1.getValue(F4MessegeItem.class);
//                        arrayList.remove(f4MessegeItem);
//                        arrayKeyList.remove(key);
//                    }
//                }
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.getRef().child("messege").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Fragment4>>>", "onDataChangezzz");

                arrayList.clear();
                arrayKeyList.clear();
                lastList.clear();
                lastKeyList.clear();
                for (DataSnapshot sn : snapshot.getChildren()) {
                    if (sn.getKey().contains(myUid)) {
                        for (DataSnapshot snapshot1 : sn.getChildren()) {
                            if (snapshot1.getKey().equals(myUid)) {
                                if (snapshot1.child("ischat").getValue().equals("1")) {
                                    for (DataSnapshot snapshot2 : sn.getChildren()) {
                                        if (!snapshot2.getKey().equals("msg") && !snapshot2.getKey().equals(myUid) && !snapshot2.getKey().equals("finished")) {
                                            if (snapshot2.getKey().contains("lastmsg")) {
                                                String key = snapshot2.getKey();
                                                LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                                lastList.add(lastListItem);
                                                lastKeyList.add(key);
                                                Log.d("fragment4>>>", "ok last key string: " + key);
                                                Log.d("Fragment4>>>", "ok last key index: " + lastKeyList.indexOf(key));
                                            } else {
                                                String key = snapshot2.getKey();
                                                F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
                                                arrayList.add(f4MessegeItem);
                                                arrayKeyList.add(key);
                                                Log.d("fragment4>>>", "ok array key string: " + key);
                                                Log.d("Fragment4>>>", "ok array key index: " + arrayKeyList.indexOf(key));
                                            }
                                        }
                                    }


                                }
                            }

                        }
                    }
                }
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//최초set

        reference.getRef().child("messege").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //count check
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    if (snapshot1.getKey().contains(myUid)) {
//                        if (snap) {
//                        }
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}