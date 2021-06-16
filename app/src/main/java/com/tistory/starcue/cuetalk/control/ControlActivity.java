package com.tistory.starcue.cuetalk.control;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ControlActivity extends AppCompatActivity {

    public static List<String> f1decList;
    public static ArrayList<F2DecViewItem> f2decList;
    public static ArrayList<F3DecViewItem> f3decList;
    public static List<String> f4decList;

//    public static ArrayList<F1DecListItem> chatroomViewList;

    RecyclerView.LayoutManager layoutManager;

    DecListAdapter decListAdapter;
    F4DecListAdapter f4DecListAdapter;
    public static F1DecListViewAdapter f1DecViewListAdapter;
    public static F2DecViewAdapter f2DecViewAdapter;
    public static F3DecListAdapter f3DecListAdapter;

    FirebaseFirestore db;
    DatabaseReference reference;
    StorageReference storageReference;

    Button f1btn, f2btn, f3btn, f4btn, btn5, clear, bottom1, bottom2;
    public static RelativeLayout f1decviewlayout, f4decviewlayout;
    public static TextView f1decviewCategory, f1decviewCuz, f1decviewWhodec, f1decviewUserUid;
    public static TextView f4decviewCategory, f4decviewCuz, f4decviewWhodec, f4decviewUserUid;
    public static RecyclerView f1declist, f2declist, f3declist, f4declist, f1decview, f4decview;
    public static RelativeLayout load;

    Context context;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);

        context = ControlActivity.this;

        setInit();
        setDb();
    }

    private void setDb() {
        reference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        topBtnOnClick();
    }

    private void setInit() {
        f1decList = new ArrayList<>();
        f2decList = new ArrayList<>();
        f3decList = new ArrayList<>();
        f4decList = new ArrayList<>();

        load = findViewById(R.id.control_load);
        load.setVisibility(View.GONE);
        f1decviewlayout = findViewById(R.id.control_f1dec__list_layout);
        f4decviewlayout = findViewById(R.id.control_f4dec__list_layout);
        f1btn = findViewById(R.id.control_btn1);
        f2btn = findViewById(R.id.control_btn2);
        f3btn = findViewById(R.id.control_btn3);
        f4btn = findViewById(R.id.control_btn4);
        btn5 = findViewById(R.id.control_btn5);
        clear = findViewById(R.id.control_clear);
        bottom1 = findViewById(R.id.control_bottom_btn1);
        bottom2 = findViewById(R.id.control_bottom_btn2);

        f1declist = findViewById(R.id.control_f1dec_list);
        f2declist = findViewById(R.id.control_f2dec_list);
        f3declist = findViewById(R.id.control_f3dec_list);
        f4declist = findViewById(R.id.control_f4dec_list);
        f1decview = findViewById(R.id.control_f1_dec_view);
        f4decview = findViewById(R.id.control_f4_dec_view);

        f1decviewCategory = findViewById(R.id.control_f1dec_view_category);
        f1decviewCuz = findViewById(R.id.control_f1dec_view_cuz);
        f1decviewWhodec = findViewById(R.id.control_f1dec_view_whodec);
        f1decviewUserUid = findViewById(R.id.control_f1dec_view_useruid);
        f4decviewCategory = findViewById(R.id.control_f4dec_view_category);
        f4decviewCuz = findViewById(R.id.control_f4dec_view_cuz);
        f4decviewWhodec = findViewById(R.id.control_f4dec_view_whodec);
        f4decviewUserUid = findViewById(R.id.control_f4dec_view_useruid);
        allGone();
    }

    private void topBtnOnClick() {
        f1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f1decList.clear();
                f2decList.clear();
                f3decList.clear();
                f4decList.clear();
                load.setVisibility(View.VISIBLE);
                allGone();
                reference.getRef().child("chatroomdec").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            f1decList.add(snapshot.getKey());
                            count += 1;
                            if (count == dataSnapshot.getChildrenCount()) {
                                getF1DecList();
                            }
                        }
                    }
                });
            }
        });
        f2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f1decList.clear();
                f2decList.clear();
                f3decList.clear();
                f4decList.clear();
                load.setVisibility(View.VISIBLE);
                allGone();
                db.collection("f2dec").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            count += 1;
                            F2DecViewItem f2DecViewItem = snapshot.toObject(F2DecViewItem.class);
                            f2decList.add(f2DecViewItem);

                            if (count == queryDocumentSnapshots.size()) {
                                getF2DecList();
                            }
                        }
                    }
                });
            }
        });
        f3btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f1decList.clear();
                f2decList.clear();
                f3decList.clear();
                f4decList.clear();
                load.setVisibility(View.VISIBLE);
                allGone();
                db.collection("f3dec").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            count += 1;
                            F3DecViewItem f3DecViewItem = snapshot.toObject(F3DecViewItem.class);
                            f3decList.add(f3DecViewItem);
                            if (count == queryDocumentSnapshots.size()) {
                                getF3DecList();
                            }
                        }
                    }
                });
            }
        });
        f4btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f1decList.clear();
                f2decList.clear();
                f3decList.clear();
                f4decList.clear();
                load.setVisibility(View.VISIBLE);
                allGone();
                reference.getRef().child("messegedec").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            f4decList.add(snapshot.getKey());
                            count += 1;
                            if (count == dataSnapshot.getChildrenCount()) {
                                getF4DecList();
                            }
                        }
                    }
                });
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allGone();
            }
        });
    }

    private void getF1DecList() {
        layoutManager = new LinearLayoutManager(ControlActivity.this);
        f1declist.setLayoutManager(layoutManager);
        decListAdapter = new DecListAdapter(f1decList, ControlActivity.this);
        f1declist.setAdapter(decListAdapter);
        decListAdapter.notifyDataSetChanged();
        f1declist.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    private void getF2DecList() {
        DescendingF2 descending = new DescendingF2();
        Collections.sort(f2decList, descending);
        layoutManager = new LinearLayoutManager(ControlActivity.this);
        f2declist.setLayoutManager(layoutManager);
        f2DecViewAdapter = new F2DecViewAdapter(f2decList, Glide.with(ControlActivity.this));
        f2declist.setAdapter(f2DecViewAdapter);
        f2DecViewAdapter.notifyDataSetChanged();
        f2declist.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    private void getF3DecList() {
        DescendingF3 descending = new DescendingF3();
        Collections.sort(f3decList, descending);
        layoutManager = new LinearLayoutManager(ControlActivity.this);
        f3declist.setLayoutManager(layoutManager);
        f3DecListAdapter = new F3DecListAdapter(f3decList, Glide.with(ControlActivity.this));
        f3declist.setAdapter(f3DecListAdapter);
        f3DecListAdapter.notifyDataSetChanged();;
        f3declist.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    private void getF4DecList() {
        layoutManager = new LinearLayoutManager(ControlActivity.this);
        f4declist.setLayoutManager(layoutManager);
        f4DecListAdapter = new F4DecListAdapter(f4decList, ControlActivity.this);
        f4declist.setAdapter(f4DecListAdapter);
        f4DecListAdapter.notifyDataSetChanged();
        f4declist.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    public static void getF1DecListView(Context context, ArrayList<F1DecListItem> arrayList, String category, String cuz, String whodec, String userUid) {
//        allClearList();
        f1declist.setVisibility(View.GONE);
        f1decviewlayout.setVisibility(View.VISIBLE);

        f1decviewCategory.setText(category);
        f1decviewCuz.setText(cuz);
        f1decviewWhodec.setText("신고자: " + whodec);
        f1decviewUserUid.setText("피신고자: " + userUid);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        f1decview.setLayoutManager(layoutManager);
        f1DecViewListAdapter = new F1DecListViewAdapter(arrayList, Glide.with(context));
        f1decview.setAdapter(f1DecViewListAdapter);
        f1DecViewListAdapter.notifyDataSetChanged();
        load.setVisibility(View.GONE);
        Log.d("ControlActivity>>>", "arrayList get size: " + arrayList.size());
    }

    public static void getF4DecListView(Context context, ArrayList<F1DecListItem> arrayList, String category, String cuz, String whodec, String userUid) {
        f4decviewlayout.setVisibility(View.VISIBLE);

        f4decviewCategory.setText(category);
        f4decviewCuz.setText(cuz);
        f4decviewWhodec.setText("신고자: " + whodec);
        f4decviewUserUid.setText("피신고자" + userUid);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        f4decview.setLayoutManager(layoutManager);
        f1DecViewListAdapter = new F1DecListViewAdapter(arrayList, Glide.with(context));
        f4decview.setAdapter(f1DecViewListAdapter);
        f1DecViewListAdapter.notifyDataSetChanged();
        load.setVisibility(View.GONE);
    }

    private void allGone() {
        f1declist.setVisibility(View.GONE);
        f2declist.setVisibility(View.GONE);
        f3declist.setVisibility(View.GONE);
        f4declist.setVisibility(View.GONE);
        f1decviewlayout.setVisibility(View.GONE);
        f4decviewlayout.setVisibility(View.GONE);
    }

    class DescendingF2 implements Comparator<F2DecViewItem> {
        @Override
        public int compare(F2DecViewItem f2DecViewItem, F2DecViewItem t1) {
            return f2DecViewItem.getDectime().compareTo(t1.getDectime());
        }
    }
    class DescendingF3 implements Comparator<F3DecViewItem> {
        @Override
        public int compare(F3DecViewItem f3DecViewItem, F3DecViewItem t1) {
            return f3DecViewItem.getDectime().compareTo(t1.getDectime());
        }
    }
//    public static void allClearList() {
//        f1decList.clear();
//        f2decList.clear();
//        f3decList.clear();
//        f4decList.clear();
//
//        chatroomViewList.clear();
//    }
}