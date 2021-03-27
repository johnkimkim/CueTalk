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
    private List<String> keyList;
    private RecyclerView.LayoutManager layoutManager;

    private F4ReAdapter adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    FirebaseFirestore db;
    String myUid;

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
        keyList = new ArrayList<>();

        adapter = new F4ReAdapter(arrayList, lastList, getActivity());
        recyclerView.setAdapter(adapter);

        reference.getRef().child("messege").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String key = snapshot1.getKey();
                    assert key != null;
                    Log.d("fragment4>>>", key);
                    if (key.contains(myUid)) {
                        Log.d("fragment4>>>", "2");
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            if (!snapshot2.getKey().equals("msg") && !snapshot2.getKey().equals("lastmsg") && !snapshot2.getKey().equals(myUid)) {
                                F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
                                arrayList.add(f4MessegeItem);
                            }
                            if (snapshot2.getKey().equals("lastmsg")) {
                                LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                keyList.add(snapshot2.getKey());
                                lastList.add(lastListItem);
                            }
                        }
                        Log.d("fragment4>>>", "3");
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });//최초set

        reference.getRef().child("messege").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String key = snapshot1.getKey();
                    assert key != null;
                    Log.d("fragment4>>>", key);
                    if (key.contains(myUid)) {
                        Log.d("fragment4>>>", "2");
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            if (snapshot2.getKey().equals("lastmsg")) {
                                LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                int index = keyList.indexOf(snapshot2.getKey());
                                lastList.set(index, lastListItem);
                            }
                        }
                        Log.d("fragment4>>>", "3");
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        reference.getRef().child("messege").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                F4MessegeItem f4MessegeItem = snapshot.getValue(F4MessegeItem.class);
//                arrayList.add(f4MessegeItem);
//                adapter.notifyDataSetChanged();
//                String key = snapshot.getKey();
//                keyList.add(key);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                String key = snapshot.getKey();
//                int index = keyList.indexOf(key);
//                arrayList.remove(index);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

}