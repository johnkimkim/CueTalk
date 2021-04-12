package com.tistory.starcue.cuetalk.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SendMessege;
import com.tistory.starcue.cuetalk.adpater.F4ReAdapter;
import com.tistory.starcue.cuetalk.item.F2Item;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;
import com.tistory.starcue.cuetalk.item.LastListItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    protected boolean isVisible;

    //    private boolean setAlready;
    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        sendMessege = new SendMessege(getActivity());
//        setAlready = false;

        setFirebase();
        setinit(rootView);
        Log.d("Fragment4>>>", "state: onCreateView");

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

                Log.d("Fragment4>>>", "onChildAdded snapshot key: " + snapshot.getKey());
                if (snapshot.getKey().contains(myUid)) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals(myUid)) {
                            String ischat = snapshot1.child("ischat").getValue(String.class);
                            if (ischat.equals("1")) {
                                reference.getRef().child("messege").child(snapshot.getKey()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getKey().contains("lastmsg")) {
                                                if (!lastKeyList.contains(dataSnapshot1.getKey())) {
                                                    String key = dataSnapshot1.getKey();
                                                    LastListItem lastListItem = dataSnapshot1.getValue(LastListItem.class);
                                                    lastList.add(lastListItem);
                                                    lastKeyList.add(key);
                                                    Log.d("Fragment4>>>", "last add, key: " + key);
                                                }
                                            } else if (!dataSnapshot1.getKey().equals("msg") && !dataSnapshot1.getKey().equals(myUid) && !dataSnapshot1.getKey().contains("lastmsg")) {
                                                if (!arrayKeyList.contains(dataSnapshot1.getKey())) {
                                                    String key = dataSnapshot1.getKey();
                                                    F4MessegeItem f4MessegeItem = dataSnapshot1.getValue(F4MessegeItem.class);
                                                    arrayList.add(f4MessegeItem);
                                                    arrayKeyList.add(key);
                                                    Log.d("Fragment4>>>", "array add, key: " + key);
                                                }
                                            }
                                        }

                                        setListTimeSort(lastList, lastList);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d("Fragment4>>>", "onchild change: " + snapshot.getKey());
                if (snapshot.getKey().contains(myUid)) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals(myUid)) {
                            String test = snapshot1.child("ischat").getValue(String.class);
                            Log.d("Fragment4>>>", "myuid get : " + test);
                            if (snapshot1.child("ischat").getValue().equals("2")) {
                                for (DataSnapshot snapshota : snapshot.getChildren()) {
                                    if (snapshota.getKey().contains("lastmsg")) {
                                        String key = snapshota.getKey();
                                        Log.d("Fragment4>>>", "last remove, key: " + key);
                                        int index = lastKeyList.indexOf(key);
                                        if (lastKeyList.contains(key)) {
                                            lastList.remove(index);
                                            lastKeyList.remove(index);
                                        }
                                    } else if (!snapshota.getKey().equals("msg") && !snapshota.getKey().equals(myUid) && !snapshota.getKey().contains("lastmsg")) {
                                        String key = snapshota.getKey();
                                        Log.d("Fragment4>>>", "array remove, key: " + key);
                                        int index = arrayKeyList.indexOf(key);
                                        if (arrayKeyList.contains(key)) {
                                            arrayList.remove(index);
                                            arrayKeyList.remove(index);
                                        }
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("Fragment4>>>", "onChildRemoved");
                if (snapshot.getKey().contains(myUid)) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            int indexlast = lastKeyList.indexOf(key);
                            Log.d("Fragment4>>>", "get lastkey: " + key);
                            if (lastKeyList.contains(key)) {
                                Log.d("Fragment4>>>", "last key 포함");
                                lastList.remove(indexlast);
                                lastKeyList.remove(indexlast);
                            }
                        } else if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            int indexarray = arrayKeyList.indexOf(key);
                            Log.d("Fragment4>>>", "get arraykey: " + key);
                            if (arrayKeyList.contains(key)) {
                                Log.d("Fragment4>>>", "array key 포함");
                                arrayList.remove(indexarray);
                                arrayKeyList.remove(indexarray);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.getRef().child("myroom").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    reference.getRef().child("messege").child(dataSnapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            arrayList.clear();
                            arrayKeyList.clear();
                            lastList.clear();
                            lastKeyList.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.getKey().equals(myUid)) {
                                    if (snapshot1.child("ischat").getValue().equals("1")) {
                                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                            if (!snapshot2.getKey().equals("msg") && !snapshot2.getKey().equals(myUid)) {
                                                if (snapshot2.getKey().contains("lastmsg")) {
                                                    String key = snapshot2.getKey();
                                                    Log.d("Fragment4>>>", "last add key: " + key);
                                                    LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                                    lastList.add(lastListItem);
                                                    lastKeyList.add(key);
                                                } else {
                                                    String key = snapshot2.getKey();
                                                    Log.d("Fragment4>>>", "array add key: " + key);
                                                    F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
                                                    arrayList.add(f4MessegeItem);
                                                    arrayKeyList.add(key);
                                                }
                                            }
                                        }

                                        setListTimeSort(lastList, lastList);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Fragment4>>>", "addListenerForSingleValueEvent finished");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        reference.getRef().child("messege").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getKey().contains(myUid)) {
//                    Log.d("Fragment4>>>", "333: " + snapshot.getKey());
//                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                        if (snapshot1.getKey().equals(myUid)) {
//                            if (snapshot1.child("ischat").equals("2")) {
//                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
//                                    if (snapshot2.getKey().contains("lastmsg")) {
//                                        String key = snapshot2.getKey();
//                                        int indexlast = lastKeyList.indexOf(key);
//                                        lastList.remove(indexlast);
//                                        lastKeyList.remove(indexlast);
//                                        Log.d("Fragment4>>>", "ischat 2 last remove");
//                                    } else if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().contains("lastmsg")) {
//                                        String key = snapshot2.getKey();
//                                        int indexarray = arrayKeyList.indexOf(key);
//                                        arrayList.remove(indexarray);
//                                        arrayKeyList.remove(indexarray);
//                                        Log.d("Fragment4>>>", "ischat 2 array remove");
//                                    }
//                                }
//
//                                adapter.notifyDataSetChanged();
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                }

//                arrayList.clear();
//                arrayKeyList.clear();
//                lastList.clear();
//                lastKeyList.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    if (snapshot1.getKey().equals(myUid)) {
//                        Log.d("Fragment4>>>", "onDataChange snapshot1 key: " + snapshot1.getKey());
//                        if (snapshot1.child("ischat").getValue().equals("1")) {
//                            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
//                                if (!snapshot2.getKey().equals("msg") && !snapshot2.getKey().equals(myUid)) {
//                                    if (snapshot2.getKey().contains("lastmsg")) {
//                                        String key = snapshot2.getKey();
//                                        LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
//                                        lastList.add(lastListItem);
//                                        lastKeyList.add(key);
//                                        Log.d("Fragment4>>>", "add last list finished");
//                                    } else {
//                                        String key = snapshot2.getKey();
//                                        F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
//                                        arrayList.add(f4MessegeItem);
//                                        arrayKeyList.add(key);
//                                        Log.d("Fragment4>>>", "addValueEventListener finished");
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                adapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);
//                Log.d("Fragment4>>>", "addValueEventListener finished");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment4>>>", "state: onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        Log.d("Fragment4>>>", "state: onPause");
    }

    private static class Descending implements Comparator<LastListItem> {
        @Override
        public int compare(LastListItem lastListItem, LastListItem t1) {
            return t1.getLasttime().compareTo(lastListItem.getLasttime());
        }
    }

    private void setListTimeSort(ArrayList<LastListItem> prev, ArrayList<LastListItem> lastListItems) {
        Descending descending = new Descending();
        Collections.sort(lastListItems, descending);

        for (int i = 1; i <= lastListItems.size(); i++) {
            Log.d("Fragment4>>>", "get position: " + i);
            LastListItem lastListItem = lastListItems.get(i);
            int a = prev.indexOf(lastListItem);//기존position

            String b = lastKeyList.get(a);
            F4MessegeItem f4MessegeItem = arrayList.get(a);
            String c = arrayKeyList.get(a);

            lastKeyList.set(i, b);
            arrayList.set(i, f4MessegeItem);
            arrayKeyList.set(i, c);

        }
    }

}