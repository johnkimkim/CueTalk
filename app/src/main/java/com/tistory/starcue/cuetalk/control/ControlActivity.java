package com.tistory.starcue.cuetalk.control;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.R;

import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends AppCompatActivity {

    public static List<String> f1decList;
    public static List<String> f2decList;
    public static List<String> f3decList;
    public static List<String> f4decList;

//    public static ArrayList<F1DecListItem> chatroomViewList;

    RecyclerView.LayoutManager layoutManager;

    DecListAdapter decListAdapter;
    public static F1DecListViewAdapter f1DecViewListAdapter;

    FirebaseFirestore db;
    DatabaseReference reference;
    StorageReference storageReference;

    Button f1btn, f2btn, f3btn, f4btn, clear, bottom1, bottom2;
    public static RelativeLayout f1decviewlayout;
    public static TextView f1decviewCategory, f1decviewCuz, f1decviewWhodec, f1decviewUserUid;
    public static RecyclerView f1declist, f2declist, f3declist, f4declist, f1decview, f4decview;
    public static LinearLayout f2decview, f3decview;
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
        f1btn = findViewById(R.id.control_btn1);
        f2btn = findViewById(R.id.control_btn2);
        f3btn = findViewById(R.id.control_btn3);
        f4btn = findViewById(R.id.control_btn4);
        clear = findViewById(R.id.control_clear);
        bottom1 = findViewById(R.id.control_bottom_btn1);
        bottom2 = findViewById(R.id.control_bottom_btn2);

        f1declist = findViewById(R.id.control_f1dec_list);
        f2declist = findViewById(R.id.control_f2dec_list);
        f3declist = findViewById(R.id.control_f3dec_list);
        f4declist = findViewById(R.id.control_f4dec_list);
        f1decview = findViewById(R.id.control_f1_dec_view);
        f2decview = findViewById(R.id.control_f2_dec_view);
        f3decview = findViewById(R.id.control_f3_dec_view);
        f4decview = findViewById(R.id.control_f4_dec_view);

        f1decviewCategory = findViewById(R.id.control_f1dec_view_category);
        f1decviewCuz = findViewById(R.id.control_f1dec_view_cuz);
        f1decviewWhodec = findViewById(R.id.control_f1dec_view_whodec);
        f1decviewUserUid = findViewById(R.id.control_f1dec_view_useruid);
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

            }
        });
        f3btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        f4btn.setOnClickListener(new View.OnClickListener() {
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

    private void allGone() {
        f1declist.setVisibility(View.GONE);
        f2declist.setVisibility(View.GONE);
        f3declist.setVisibility(View.GONE);
        f4declist.setVisibility(View.GONE);
        f2decview.setVisibility(View.GONE);
        f3decview.setVisibility(View.GONE);
        f4decview.setVisibility(View.GONE);
        f1decviewlayout.setVisibility(View.GONE);
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