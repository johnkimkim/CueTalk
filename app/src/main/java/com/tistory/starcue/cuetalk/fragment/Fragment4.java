package com.tistory.starcue.cuetalk.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SendMessege;
import com.tistory.starcue.cuetalk.adpater.F4ReAdapter;
import com.tistory.starcue.cuetalk.item.F2Item;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;
import com.tistory.starcue.cuetalk.item.LastListItem;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Fragment4 extends Fragment {

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private ArrayList<F4MessegeItem> arrayList;
    private ArrayList<F4MessegeItem> beforeArrayList;
    private ArrayList<LastListItem> lastList;
    private ArrayList<LastListItem> beforeLastList;
    private List<String> arrayKeyList;
    private List<String> beforeArrayKeyList;
    private List<String> lastKeyList;
    private List<String> beforeLastkeyList;
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

    boolean setAready;
    public static boolean stayf4chatroom;

    //    private boolean setAlready;
    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        Log.d("Fragment4>>>", "start onCreate");

        sendMessege = new SendMessege(getActivity());
        stayf4chatroom = false;
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
        beforeLastList = new ArrayList<>();
        beforeArrayList = new ArrayList<>();
        beforeArrayKeyList = new ArrayList<>();
        beforeLastkeyList = new ArrayList<>();
//        arrayList.clear();
//        arrayKeyList.clear();
//        lastList.clear();
//        lastKeyList.clear();
//        beforeLastList.clear();
//        beforeArrayList.clear();
//        beforeLastkeyList.clear();
//        beforeArrayKeyList.clear();
        adapter = new F4ReAdapter(arrayList, lastList, getActivity());
        recyclerView.setAdapter(adapter);

        reference.getRef().child("messege").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {//추가됐을때만 쓰기로 수정하기. if !lastkeylist.constains(getkey)
                Log.d("Fragment4>>>", "add");
                if (snapshot.getKey().contains(myUid)) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().contains("lastmsg")) {
                            if (!lastKeyList.contains(snapshot1.getKey())) {
                                Log.d("Fragment4>>>", "add: " + snapshot.getKey());
                                reference.getRef().child("messege").child(snapshot.getKey()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot1.getKey().contains("lastmsg")) {
                                                if (!lastKeyList.contains(snapshot1.getKey())) {
                                                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                                        if (dataSnapshot2.getKey().contains("lastmsg")) {
                                                            if (!lastKeyList.contains(dataSnapshot2.getKey())) {
                                                                String key = dataSnapshot2.getKey();
                                                                LastListItem lastListItem = dataSnapshot2.getValue(LastListItem.class);
                                                                lastList.add(lastListItem);
                                                                lastKeyList.add(key);
                                                                Log.d("Fragment4>>>", "add last add, key: " + key);
                                                            }
                                                        } else if (!dataSnapshot2.getKey().equals("msg") && !dataSnapshot2.getKey().equals(myUid) && !dataSnapshot2.getKey().contains("lastmsg")) {
                                                            if (!arrayKeyList.contains(dataSnapshot2.getKey())) {
                                                                String key = dataSnapshot2.getKey();
                                                                F4MessegeItem f4MessegeItem = dataSnapshot2.getValue(F4MessegeItem.class);
                                                                arrayList.add(f4MessegeItem);
                                                                arrayKeyList.add(key);
                                                                Log.d("Fragment4>>>", "add array add, key: " + key);
                                                            }
                                                        }
                                                    }
                                                    setAddListSort();
                                                    adapter.notifyDataSetChanged();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //경우의수:
                //내가 혼자 나갔을때: if ischat = 2
                //새로운 메시지 왔을때
                Log.d("Fragment4>>>", "onchild change: " + snapshot.getKey());//room key
                if (snapshot.getKey().contains(myUid)) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals(myUid)) {
                            String ischat = snapshot1.child("ischat").getValue(String.class);
                            if (ischat.equals("2")) {
                                Log.d("Fragment4>>>", "change ischat; " + ischat);
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    if (snapshot2.getKey().contains("lastmsg")) {
                                        if (lastKeyList.contains(snapshot2.getKey())) {
                                            Log.d("Fragment4>>>", "array size: " + arrayList.size());
                                            String key = snapshot2.getKey();
                                            int index = lastKeyList.indexOf(key);
                                            lastList.remove(index);
                                            arrayList.remove(index);
                                            arrayKeyList.remove(index);
                                            lastKeyList.remove(index);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            } else {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    if (snapshot2.getKey().contains("lastmsg")) {
                                        if (lastKeyList.contains(snapshot2.getKey())) {
                                            Log.d("Fragment4>>>", "onchild change if key lasgmsg");
                                            String key = snapshot2.getKey();
                                            LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                            if (!lastList.contains(lastListItem)) {
                                                int index = lastKeyList.indexOf(key);
                                                lastList.set(index, lastListItem);
                                                setListTimeSort();
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    } else if (!snapshot2.getKey().equals(myUid) && !snapshot2.getKey().equals("msg") && !snapshot2.getKey().contains("lastmsg")) {
                                        F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
                                        String yourUid = f4MessegeItem.getUid();
                                        for (int i = 0; i < arrayList.size(); i++) {
                                            if (arrayList.get(i).getUid().equals(yourUid)) {
                                                arrayList.set(i, f4MessegeItem);
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
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
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Fragment4>>>", "addListenerForSingleValueEvent finished");
                            setAready = true;
                            setListTimeSort();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


//        reference.getRef().child("messege").addValueEventListener(new ValueEventListener() {//나혼자 먼저 나갔을때
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("Fragment4>>>", "addValueEventListener: " + snapshot.getKey());//messege
//
//                for (DataSnapshot snapshota : snapshot.getChildren()) {
//                    if (snapshota.getKey().contains(myUid)) {
//                        Log.d("Fragment4>>>", "333: " + snapshota.getKey());
//                        for (DataSnapshot snapshot1 : snapshota.getChildren()) {
//                            if (snapshot1.getKey().equals(myUid)) {
//                                if (snapshot1.child("ischat").equals("2")) {
//                                    for (DataSnapshot snapshot2 : snapshota.getChildren()) {
//                                        if (snapshot2.getKey().contains("lastmsg")) {
//                                            String key = snapshot2.getKey();
//                                            int indexlast = lastKeyList.indexOf(key);
//                                            lastList.remove(indexlast);
//                                            lastKeyList.remove(indexlast);
//                                            Log.d("Fragment4>>>", "ischat 2 last remove");
//                                        } else if (!snapshot1.getKey().equals("msg") && !snapshot1.getKey().equals(myUid) && !snapshot1.getKey().contains("lastmsg")) {
//                                            String key = snapshot2.getKey();
//                                            int indexarray = arrayKeyList.indexOf(key);
//                                            arrayList.remove(indexarray);
//                                            arrayKeyList.remove(indexarray);
//                                            Log.d("Fragment4>>>", "ischat 2 array remove");
//                                        }
//                                    }
//
//                                    adapter.notifyDataSetChanged();
//                                    progressBar.setVisibility(View.GONE);
//                                }
//                            }
//                        }
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment4>>>", "state: onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment4>>>", "state: onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("state", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("state", stayF4);
//        editor.commit();
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        Log.d("Fragment4>>>", "state: onPause: " + arrayList.size());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!stayf4chatroom) {

        }
        Log.d("Fragment4>>>", "state: onStop " + arrayList.size());
    }

    private static class Descending implements Comparator<LastListItem> {
        @Override
        public int compare(LastListItem lastListItem, LastListItem t1) {
            return t1.getLasttime().compareTo(lastListItem.getLasttime());
        }
    }

    private void setListTimeSort() {
        if (arrayList.size() != 0) {
            beforeLastList.clear();
            beforeArrayList.clear();
            beforeLastkeyList.clear();
            beforeArrayKeyList.clear();
            beforeLastList.addAll(lastList);
            beforeArrayList.addAll(arrayList);
            beforeLastkeyList.addAll(lastKeyList);
            beforeArrayKeyList.addAll(arrayKeyList);
            Descending descending = new Descending();
            Collections.sort(lastList, descending);

            List<Integer> beforeIndex = new ArrayList<>();
            for (int i = 0; i < lastList.size(); i++) {
                int index = beforeLastList.indexOf(lastList.get(i));
                beforeIndex.add(i, index);
                arrayList.set(i, beforeArrayList.get(beforeIndex.get(i)));
                lastKeyList.set(i, beforeLastkeyList.get(beforeIndex.get(i)));
                arrayKeyList.set(i, beforeArrayKeyList.get(beforeIndex.get(i)));
            }

//            for (int i = 0; i < beforeIndex.size(); i++) {
//            }
        }
    }

    private void setAddListSort() {
        if (arrayList.size() != 0) {
            beforeLastList.clear();
            beforeArrayList.clear();
            beforeLastkeyList.clear();
            beforeArrayKeyList.clear();
            beforeLastList.addAll(lastList);
            beforeArrayList.addAll(arrayList);
            beforeLastkeyList.addAll(lastKeyList);
            beforeArrayKeyList.addAll(arrayKeyList);
            Descending descending = new Descending();
            Collections.sort(lastList, descending);

            List<Integer> beforeIndex = new ArrayList<>();
            for (int i = 0; i < lastList.size(); i++) {
                int index = beforeLastList.indexOf(lastList.get(i));
                beforeIndex.add(i, index);
                Log.d("Fragment4>>>", "get arraylist size: " + arrayList.size());
                Log.d("Fragment4>>>", "get before array size: " + beforeArrayList.size());
                arrayList.set(i, beforeArrayList.get(beforeIndex.get(i)));
                lastKeyList.set(i, beforeLastkeyList.get(beforeIndex.get(i)));
                arrayKeyList.set(i, beforeArrayKeyList.get(beforeIndex.get(i)));
            }

//            for (int i = 0; i < beforeIndex.size(); i++) {
//            }

            adapter.notifyDataSetChanged();
        }
    }

}