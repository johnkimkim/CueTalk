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
import com.tistory.starcue.cuetalk.item.F4MessegeItem;
import com.tistory.starcue.cuetalk.item.LastListItem;

import java.util.ArrayList;
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
                                        Log.d("Fragment4>>>", "last add");
                                    }
                                } else if (!dataSnapshot1.getKey().equals("msg") && !dataSnapshot1.getKey().equals(myUid) && !dataSnapshot1.getKey().contains("lastmsg")) {
                                    if (!arrayKeyList.contains(dataSnapshot1.getKey())) {
                                        String key = dataSnapshot1.getKey();
                                        F4MessegeItem f4MessegeItem = dataSnapshot1.getValue(F4MessegeItem.class);
                                        arrayList.add(f4MessegeItem);
                                        arrayKeyList.add(key);
                                        Log.d("Fragment4>>>", "array add");
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d("Fragment4>>>", "onchild Change");

//                if (snapshot.getKey().contains(myUid)) {
//                    reference.getRef().child("messege").child(snapshot.getKey()).child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                        @Override
//                        public void onSuccess(DataSnapshot dataSnapshot) {
//                            String ischat = dataSnapshot.child("ischat").getValue(String.class);
//                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                                if (snapshot1.getKey().contains("lastmsg")) {
//                                    if (ischat.equals("1")) {
//                                        String key = snapshot1.getKey();
//                                        Log.d("Fragment4>>>", "lastmsg change string: " + key);
//                                        LastListItem lastListItem = snapshot1.getValue(LastListItem.class);
//                                        int index = lastKeyList.indexOf(key);
//                                        Log.d("Fragment4>>>", "lastmsg change index: " + index);
//                                        lastList.set(index, lastListItem); //error if send messege
//                                    } else if (ischat.equals("2")) {
//                                        String keysnapshot = "lastmsg" + snapshot.getKey();
//                                        int index = lastKeyList.indexOf(keysnapshot);
//                                        if (lastKeyList.contains(keysnapshot)) {
//                                            Log.d("Fragment4>>>", "index lastkeylist string: " + keysnapshot);
//                                            Log.d("Fragment4>>>", "index lastkeylist: " + index);
//                                            arrayList.remove(index);
//                                            arrayKeyList.remove(index);
//                                            lastList.remove(index);
//                                            lastKeyList.remove(index);
//                                        }
//
//                                    }
//                                }
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().contains(myUid)) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            int indexlast = lastKeyList.indexOf(key);
                            lastList.remove(indexlast);
                            lastKeyList.remove(indexlast);
                        } else if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            int indexarray = arrayKeyList.indexOf(key);
                            arrayList.remove(indexarray);
                            arrayKeyList.remove(indexarray);
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
                                                    LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                                    lastList.add(lastListItem);
                                                    lastKeyList.add(key);
                                                } else {
                                                    String key = snapshot2.getKey();
                                                    F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
                                                    arrayList.add(f4MessegeItem);
                                                    arrayKeyList.add(key);
                                                }
                                            }
                                        }
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

//        reference.getRef().child("myroom").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    Log.d("Fragment4>>>", "get room key: " + snapshot1.getKey());
//                    reference.getRef().child("messege").child(snapshot1.getKey()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                        @Override
//                        public void onSuccess(DataSnapshot dataSnapshot) {
//                            arrayList.clear();
//                            arrayKeyList.clear();
//                            lastList.clear();
//                            lastKeyList.clear();
//                            for (DataSnapshot sn : snapshot.getChildren()) {
//                                if (sn.getKey().contains(myUid)) {
//                                    for (DataSnapshot snapshot1 : sn.getChildren()) {
//                                        if (snapshot1.getKey().equals(myUid)) {
//                                            if (snapshot1.child("ischat").getValue().equals("1")) {
//                                                for (DataSnapshot snapshot2 : sn.getChildren()) {
//                                                    if (!snapshot2.getKey().equals("msg") && !snapshot2.getKey().equals(myUid) && !snapshot2.getKey().equals("finished")) {
//                                                        if (snapshot2.getKey().contains("lastmsg")) {
//                                                            String key = snapshot2.getKey();
//                                                            LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
//                                                            lastList.add(lastListItem);
//                                                            lastKeyList.add(key);
//                                                            Log.d("fragment4>>>", "ok last key string: " + key);
//                                                            Log.d("Fragment4>>>", "ok last key index: " + lastKeyList.indexOf(key));
//                                                        } else {
//                                                            String key = snapshot2.getKey();
//                                                            F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
//                                                            arrayList.add(f4MessegeItem);
//                                                            arrayKeyList.add(key);
//                                                            Log.d("fragment4>>>", "ok array key string: " + key);
//                                                            Log.d("Fragment4>>>", "ok array key index: " + arrayKeyList.indexOf(key));
//                                                        }
//                                                    }
//                                                }
//
//
//                                            }
//                                        }
//
//                                    }
//                                }
//                            }
//                            adapter.notifyDataSetChanged();
//
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        reference.getRef().child("messege").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("Fragment4>>>", "onDataChangezzz");
//
//                arrayList.clear();
//                arrayKeyList.clear();
//                lastList.clear();
//                lastKeyList.clear();
//                for (DataSnapshot sn : snapshot.getChildren()) {
//                    if (sn.getKey().contains(myUid)) {
//                        for (DataSnapshot snapshot1 : sn.getChildren()) {
//                            if (snapshot1.getKey().equals(myUid)) {
//                                if (snapshot1.child("ischat").getValue().equals("1")) {
//                                    for (DataSnapshot snapshot2 : sn.getChildren()) {
//                                        if (!snapshot2.getKey().equals("msg") && !snapshot2.getKey().equals(myUid) && !snapshot2.getKey().equals("finished")) {
//                                            if (snapshot2.getKey().contains("lastmsg")) {
//                                                String key = snapshot2.getKey();
//                                                LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
//                                                lastList.add(lastListItem);
//                                                lastKeyList.add(key);
//                                                Log.d("fragment4>>>", "ok last key string: " + key);
//                                                Log.d("Fragment4>>>", "ok last key index: " + lastKeyList.indexOf(key));
//                                            } else {
//                                                String key = snapshot2.getKey();
//                                                F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
//                                                arrayList.add(f4MessegeItem);
//                                                arrayKeyList.add(key);
//                                                Log.d("fragment4>>>", "ok array key string: " + key);
//                                                Log.d("Fragment4>>>", "ok array key index: " + arrayKeyList.indexOf(key));
//                                            }
//                                        }
//                                    }
//
//
//                                }
//                            }
//
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });//최초set

        reference.getRef().child("messege").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().contains(myUid)) {
                    Log.d("Fragment4>>>", "ischat 2 snapshot key: " + snapshot.getKey());
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals(myUid)) {
                            if (snapshot1.child("ischat").equals("2")) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    if (snapshot2.getKey().contains("lastmsg")) {
                                        String key = snapshot2.getKey();
                                        int indexlast = lastKeyList.indexOf(key);
                                        lastList.remove(indexlast);
                                        lastKeyList.remove(indexlast);
                                        Log.d("Fragment4>>>", "ischat 2 last remove");
                                    } else if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().contains("lastmsg")) {
                                        String key = snapshot2.getKey();
                                        int indexarray = arrayKeyList.indexOf(key);
                                        arrayList.remove(indexarray);
                                        arrayKeyList.remove(indexarray);
                                        Log.d("Fragment4>>>", "ischat 2 array remove");
                                    }
                                }

                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                }

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

}