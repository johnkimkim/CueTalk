package com.tistory.starcue.cuetalk.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SendMessege;
import com.tistory.starcue.cuetalk.adpater.F4ReAdapter;
import com.tistory.starcue.cuetalk.item.F4MessegeItem;
import com.tistory.starcue.cuetalk.item.LastListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Fragment4 extends Fragment {

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private ArrayList<F4MessegeItem> arrayList;//회원정보list
    private ArrayList<F4MessegeItem> beforeArrayList;//회원정보 위치변경 전 list
    private ArrayList<LastListItem> lastList;//마지막메시지 list
    private ArrayList<LastListItem> beforeLastList;//마지막메시지 변경 전 list
    private List<String> arrayKeyList;//회원정보 key list
    private List<String> beforeArrayKeyList;//회원정보 변경 전 key list
    private List<String> lastKeyList;//마지막메시지 key list
    private List<String> beforeLastkeyList;//마지막메시지 변경전 key list
    private List<String> countList;
    private List<String> beforeCountList;
    private List<String> countKeyList;
    private List<String> beforeCountKeyList;
    private RecyclerView.LayoutManager layoutManager;

    private F4ReAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    FirebaseFirestore db;
    String myUid;
    String intentString;

    GpsTracker gpsTracker;

    SendMessege sendMessege;

    private ProgressBar progressBar;
    private RelativeLayout nullchat;

    boolean setAready;
    public static boolean stayf4chatroom;

    //    private boolean setAlready;
    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment4, container, false);

        sendMessege = new SendMessege(getActivity());
        stayf4chatroom = false;
//        setAlready = false;

        setFirebase(rootView);

        return rootView;
    }

    private void setinit(ViewGroup v) {
        recyclerView = v.findViewById(R.id.fragment4_recyclerview);
        progressBar = v.findViewById(R.id.fragment4_progress_bar);
        nullchat = v.findViewById(R.id.fragment4_if_null_chat);
        nullchat.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        setRecyclerView();
    }

    private void setFirebase(ViewGroup v) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        myUid = mAuth.getUid();

        if (firebaseUser != null) {
            setinit(v);
        }
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

        countList = new ArrayList<>();
        countKeyList = new ArrayList<>();
        beforeCountList = new ArrayList<>();
        beforeCountKeyList = new ArrayList<>();

        adapter = new F4ReAdapter(arrayList, lastList, lastKeyList, countList, getActivity(), Glide.with(Fragment4.this), getActivity());
        recyclerView.setAdapter(adapter);

        reference.getRef().child("messege").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("Fragment4>>>", "state: added");
                if (snapshot.getKey().contains(myUid)) {
                    String roomKey = snapshot.getKey();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().contains("lastmsg")) {
                            if (!lastKeyList.contains(snapshot1.getKey())) {
                                reference.getRef().child("messege").child(snapshot.getKey()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        String userUid = snapshot.getKey().replace(myUid, "");
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            Log.d("Fragment4>>>", "testtest" + dataSnapshot1.child(myUid).child("ischat").getValue(String.class));
                                            if (dataSnapshot1.getKey().equals(myUid)) {
                                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                                    if (dataSnapshot2.getKey().equals("ischat")) {
                                                        if (dataSnapshot2.getValue(String.class).equals("1")) {
                                                            for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {
                                                                if (dataSnapshot3.getKey().contains("lastmsg")) {
                                                                    if (!lastKeyList.contains(snapshot1.getKey())) {
                                                                        for (DataSnapshot dataSnapshot4 : dataSnapshot.getChildren()) {
                                                                            if (dataSnapshot4.getKey().contains("lastmsg")) {
                                                                                if (!lastKeyList.contains(dataSnapshot4.getKey())) {
                                                                                    String key = dataSnapshot4.getKey();
                                                                                    LastListItem lastListItem = dataSnapshot4.getValue(LastListItem.class);
                                                                                    lastList.add(lastListItem);
                                                                                    lastKeyList.add(key);
                                                                                }
                                                                            } else if (dataSnapshot4.getKey().equals("msg")) {

                                                                                //add count
                                                                                int i = 0;
                                                                                int alli = 0;
                                                                                for (DataSnapshot dataSnapshot5 : dataSnapshot4.getChildren()) {
                                                                                    alli += 1;
                                                                                    Log.d("Fragment4>>>", "get userUid: " + userUid);
                                                                                    if (dataSnapshot5.child("uid").getValue(String.class).equals(userUid)
                                                                                            && dataSnapshot5.child("read").getValue(String.class).equals("1")) {
                                                                                        i += 1;
                                                                                    }

                                                                                    if (alli == dataSnapshot4.getChildrenCount()) {
                                                                                        if (!countKeyList.contains(roomKey)) {
                                                                                            countList.add(Integer.toString(i));
                                                                                            countKeyList.add(roomKey);
                                                                                        }
                                                                                    }
                                                                                }
                                                                                setMainBtn4Count(countList);
                                                                                //add count

                                                                            } else if (!dataSnapshot4.getKey().equals("msg")
                                                                                    && !dataSnapshot4.getKey().equals(myUid)
                                                                                    && !dataSnapshot4.getKey().contains("lastmsg")) {
                                                                                if (!arrayKeyList.contains(dataSnapshot4.getKey())) {
                                                                                    String key = dataSnapshot4.getKey();
                                                                                    F4MessegeItem f4MessegeItem = dataSnapshot4.getValue(F4MessegeItem.class);
                                                                                    arrayList.add(f4MessegeItem);
                                                                                    arrayKeyList.add(key);
                                                                                }
                                                                            }
                                                                        }
                                                                        setAddListSort();
                                                                        adapter.notifyDataSetChanged();
                                                                        progressBar.setVisibility(View.GONE);
                                                                        setNullChat();
                                                                    }
                                                                }
                                                            }

                                                        }
                                                    }
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
                Log.d("Fragment4>>>", "state: changed");
                if (snapshot.getKey().contains(myUid)) {
                    String roomKey = snapshot.getKey();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals(myUid)) {
                            String ischat = snapshot1.child("ischat").getValue(String.class);
                            if (ischat.equals("2")) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    if (snapshot2.getKey().contains("lastmsg")) {
                                        if (lastKeyList.contains(snapshot2.getKey())) {
                                            String key = snapshot2.getKey();
                                            int index = lastKeyList.indexOf(key);
                                            lastList.remove(index);
                                            arrayList.remove(index);
                                            arrayKeyList.remove(index);
                                            lastKeyList.remove(index);
                                            countList.remove(index);
                                            countKeyList.remove(index);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                setNullChat();
                            } else {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    if (snapshot2.getKey().contains("lastmsg")) {
                                        if (lastKeyList.contains(snapshot2.getKey())) {
                                            String key = snapshot2.getKey();
                                            LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                            if (!lastList.contains(lastListItem)) {
                                                int index = lastKeyList.indexOf(key);
                                                lastList.set(index, lastListItem);
                                                setListTimeSort();
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    } else if (snapshot2.getKey().equals("msg")) {
                                        String userUid = snapshot.getKey().replace(myUid, "");
                                        //change count
//                                        List<String> count = new ArrayList<>();
//                                        List<String> allCount = new ArrayList<>();
                                        int i = 0;
                                        int alli = 0;
                                        for (DataSnapshot dataSnapshot3 : snapshot2.getChildren()) {
                                            alli += 1;
//                                            allCount.add(dataSnapshot3.getKey());
//                                            if (dataSnapshot3.child("uid").getValue(String.class).equals("dlqwkddhksfycjtaptlwl")
//                                                    && dataSnapshot3.child("read").getValue(String.class).equals("dlqwkddhksfycjtaptlwl")
//                                                    && dataSnapshot3.child("time").getValue(String.class).equals("dlqwkddhksfycjtaptlwl")
//                                                    && dataSnapshot3.child("messege").getValue(String.class).equals("dlqwkddhksfycjtaptlwl")) {
//                                                count.add("1");
//                                            } else if (!dataSnapshot3.child("uid").getValue(String.class).equals(myUid)
//                                                    && dataSnapshot3.child("read").getValue(String.class).equals("1")) {
//                                                count.add("1");
//                                            }

                                            if (dataSnapshot3.child("uid").getValue(String.class).equals(userUid)
                                                    && dataSnapshot3.child("read").getValue(String.class).equals("1")) {
                                                i += 1;
                                            }

                                            if (alli == snapshot2.getChildrenCount()) {
                                                if (countKeyList.contains(roomKey)) {
                                                    countList.set(countKeyList.indexOf(roomKey), Integer.toString(i));
                                                }
                                            }
                                        }
                                        setNullChat();
                                        setMainBtn4Count(countList);
                                        adapter.notifyDataSetChanged();
                                        //change count

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
                if (snapshot.getKey().contains(myUid)) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            int indexlast = lastKeyList.indexOf(key);
                            if (lastKeyList.contains(key)) {
                                lastList.remove(indexlast);
                                lastKeyList.remove(indexlast);
                            }
                        } else if (snapshot1.getKey().equals("msg")) {
                            //remove count
                            String key = snapshot.getKey();
                            int indexcount = countKeyList.indexOf(key);
                            if (countKeyList.contains(key)) {
                                countList.remove(indexcount);
                                countKeyList.remove(indexcount);
                            }
                            //remove count
                        } else if (!snapshot1.getKey().equals(myUid) && !snapshot1.getKey().contains("lastmsg")) {
                            String key = snapshot1.getKey();
                            int indexarray = arrayKeyList.indexOf(key);
                            if (arrayKeyList.contains(key)) {
                                arrayList.remove(indexarray);
                                arrayKeyList.remove(indexarray);
                            }
                        }
                    }
                    setNullChat();
                    setMainBtn4Count(countList);
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

        firstSetList();

    }

    private void firstSetList() {
        arrayList.clear();
        arrayKeyList.clear();
        lastList.clear();
        lastKeyList.clear();
        countList.clear();
        countKeyList.clear();
        reference.getRef().child("myroom").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {//최초실행
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    reference.getRef().child("messege").child(dataSnapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("Fragment4>>>", "state: first");
                            String roomKey = snapshot.getKey();
                            String userUid = snapshot.getKey().replace(myUid, "");
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.getKey().equals(myUid)) {
                                    if (snapshot1.child("ischat").getValue().equals("1")) {
                                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                            if (!snapshot2.getKey().equals(myUid)) {
                                                if (snapshot2.getKey().contains("lastmsg")) {
                                                    String key = snapshot2.getKey();
                                                    if (!lastKeyList.contains(key)) {
                                                        LastListItem lastListItem = snapshot2.getValue(LastListItem.class);
                                                        lastList.add(lastListItem);
                                                        lastKeyList.add(key);
                                                    }
                                                } else if (snapshot2.getKey().equals("msg")) {

                                                    //first count
//                                                    List<String> count = new ArrayList<>();
//                                                    List<String> allCount = new ArrayList<>();
                                                    int alli = 0;
                                                    int i = 0;
                                                    for (DataSnapshot dataSnapshot3 : snapshot2.getChildren()) {
                                                        alli += 1;
//                                                        allCount.add(dataSnapshot3.getKey());
//                                                        if (dataSnapshot3.child("uid").getValue(String.class).equals("dlqwkddhksfycjtaptlwl")
//                                                                && dataSnapshot3.child("read").getValue(String.class).equals("dlqwkddhksfycjtaptlwl")) {
//                                                            count.add("1");
//                                                            Log.d("Fragment4>>>", "add test1");
//                                                        } else if (!dataSnapshot3.child("uid").getValue(String.class).equals(myUid)
//                                                                && dataSnapshot3.child("read").getValue(String.class).equals("1")) {
//                                                            count.add("1");
//                                                        }

                                                        if (dataSnapshot3.child("uid").getValue(String.class).equals(userUid)
                                                                && dataSnapshot3.child("read").getValue(String.class).equals("1")) {
                                                            i += 1;
                                                        }

                                                        if (alli == snapshot2.getChildrenCount()) {
                                                            if (!countKeyList.contains(roomKey)) {
                                                                countList.add(Integer.toString(i));
                                                                countKeyList.add(roomKey);
                                                            }
                                                        }
                                                    }
                                                    //first count

                                                } else {
                                                    String key = snapshot2.getKey();
                                                    if (!arrayKeyList.contains(key)) {
                                                        F4MessegeItem f4MessegeItem = snapshot2.getValue(F4MessegeItem.class);
                                                        arrayList.add(f4MessegeItem);
                                                        arrayKeyList.add(key);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            setNullChat();
                            setListTimeSort();
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Fragment4>>>", "addListenerForSingleValueEvent finished");
                            Log.d("Fragment4>>>", "countList size: " + countList.size());
                            setAready = true;
                            setMainBtn4Count(countList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //layout 설정 후 view 변경 가능한 단계
        setNullChat();
    }

    @Override
    public void onPause() {
        super.onPause();
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("state", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("state", stayF4);
//        editor.commit();
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onStop() {
        super.onStop();
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
            beforeCountList.clear();
            beforeCountKeyList.clear();
            beforeLastList.addAll(lastList);
            beforeArrayList.addAll(arrayList);
            beforeLastkeyList.addAll(lastKeyList);
            beforeArrayKeyList.addAll(arrayKeyList);

            beforeCountList.addAll(countList);
            beforeCountKeyList.addAll(countKeyList);
            Descending descending = new Descending();
            Collections.sort(lastList, descending);

            List<Integer> beforeIndex = new ArrayList<>();
            for (int i = 0; i < lastList.size(); i++) {
                int index = beforeLastList.indexOf(lastList.get(i));
                beforeIndex.add(i, index);
                arrayList.set(i, beforeArrayList.get(beforeIndex.get(i)));
                lastKeyList.set(i, beforeLastkeyList.get(beforeIndex.get(i)));
                arrayKeyList.set(i, beforeArrayKeyList.get(beforeIndex.get(i)));
                countList.set(i, beforeCountList.get(beforeIndex.get(i)));
                countKeyList.set(i, beforeCountKeyList.get(beforeIndex.get(i)));
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
            beforeCountList.clear();
            beforeCountKeyList.clear();
            beforeLastList.addAll(lastList);
            beforeArrayList.addAll(arrayList);
            beforeLastkeyList.addAll(lastKeyList);
            beforeArrayKeyList.addAll(arrayKeyList);

            beforeCountList.addAll(countList);
            beforeCountKeyList.addAll(countKeyList);
            Descending descending = new Descending();
            Collections.sort(lastList, descending);

            List<Integer> beforeIndex = new ArrayList<>();
            for (int i = 0; i < lastList.size(); i++) {
                int index = beforeLastList.indexOf(lastList.get(i));
                beforeIndex.add(i, index);

                arrayList.set(i, beforeArrayList.get(beforeIndex.get(i)));
                lastKeyList.set(i, beforeLastkeyList.get(beforeIndex.get(i)));
                arrayKeyList.set(i, beforeArrayKeyList.get(beforeIndex.get(i)));
                countList.set(i, beforeCountList.get(beforeIndex.get(i)));
                countKeyList.set(i, beforeCountKeyList.get(beforeIndex.get(i)));
            }

//            for (int i = 0; i < beforeIndex.size(); i++) {
//            }

            adapter.notifyDataSetChanged();
        }
    }

    private void setMainBtn4Count(List<String> countList) {

        MainActivity.btn4count.setText("");
        MainActivity.btn4count.setVisibility(View.GONE);
        List<String> ls = new ArrayList<>();
        int allcount = 0;
        for (int i = 0; i < countList.size(); i++) {
            ls.add("1");
            allcount += Integer.parseInt(countList.get(i));
            Log.d("Fragment4>>>", "get countList.size() " + countList.size());
            if (ls.size() == countList.size()) {
                if (Integer.toString(allcount).equals("0")) {
                    MainActivity.btn4count.setVisibility(View.GONE);
                    MainActivity.btn4count.setText("");
                } else {
                    MainActivity.btn4count.setVisibility(View.VISIBLE);
                    MainActivity.btn4count.setText(Integer.toString(allcount));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gpsTracker = new GpsTracker(getActivity());
        gpsTracker.updateFirestoreGps(db, myUid);
    }

    private void setNullChat() {
        if (arrayList == null || arrayList.size() == 0) {
            nullchat.setVisibility(View.VISIBLE);
        } else {
            nullchat.setVisibility(View.GONE);
        }
    }

}