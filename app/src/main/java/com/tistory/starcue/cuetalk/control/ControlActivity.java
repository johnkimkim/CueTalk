package com.tistory.starcue.cuetalk.control;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SplashActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ControlActivity extends AppCompatActivity {

    public static List<String> f1decList;
    public static ArrayList<F2DecViewItem> f2decList;
    public static ArrayList<F3DecViewItem> f3decList;
    public static List<String> f4decList;
    public static ArrayList<DeleteUserItem> deleteList;

//    public static ArrayList<F1DecListItem> chatroomViewList;

    RecyclerView.LayoutManager layoutManager;

    DecListAdapter decListAdapter;
    F4DecListAdapter f4DecListAdapter;
    public static F1DecListViewAdapter f1DecViewListAdapter;
    public static F2DecViewAdapter f2DecViewAdapter;
    public static F3DecListAdapter f3DecListAdapter;
    public static DeleteAdapter deleteAdapter;

    FirebaseFirestore db;
    DatabaseReference reference;
    StorageReference storageReference;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    Button f1btn, f2btn, f3btn, f4btn, btn5, clear, bottom;
    public static RelativeLayout f1decviewlayout, f4decviewlayout, deleteUserLayout;
    public static TextView f1decviewCategory, f1decviewCuz, f1decviewWhodec, f1decviewUserUid;
    public static TextView f4decviewCategory, f4decviewCuz, f4decviewWhodec, f4decviewUserUid;
    public static RecyclerView f1declist, f2declist, f3declist, f4declist, f1decview, f4decview, deletelist;
    public static RelativeLayout load;
    EditText edit, deleteEdit;
    Button deleteBtn;

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
        deleteList = new ArrayList<>();

        load = findViewById(R.id.control_load);
        load.setVisibility(View.GONE);
        f1decviewlayout = findViewById(R.id.control_f1dec__list_layout);
        f4decviewlayout = findViewById(R.id.control_f4dec__list_layout);
        deleteUserLayout = findViewById(R.id.control_delete_user_layout);
        f1btn = findViewById(R.id.control_btn1);
        f2btn = findViewById(R.id.control_btn2);
        f3btn = findViewById(R.id.control_btn3);
        f4btn = findViewById(R.id.control_btn4);
        btn5 = findViewById(R.id.control_btn5);
        clear = findViewById(R.id.control_clear);
        deleteEdit = findViewById(R.id.control_delete_edittext);
        deleteBtn = findViewById(R.id.control_delete_btn);
        deletelist = findViewById(R.id.control_delete_list);
        edit = findViewById(R.id.control_activity_edittext);
        bottom = findViewById(R.id.control_bottom_btn);

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
                deleteList.clear();
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
                deleteList.clear();
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
                deleteList.clear();
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
                deleteList.clear();
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
                f1decList.clear();
                f2decList.clear();
                f3decList.clear();
                f4decList.clear();
                deleteList.clear();
                load.setVisibility(View.VISIBLE);
                allGone();
                db.collection("deleteUser").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            count += 1;
                            DeleteUserItem deleteUserItem = snapshot.toObject(DeleteUserItem.class);
                            deleteList.add(deleteUserItem);
                            if (count == queryDocumentSnapshots.size()) {
                                Log.d("ControlActivity>>>", "count: " + queryDocumentSnapshots.size());
                                Log.d("ControlActivity>>>", "list size: " + deleteList.size());
                                getDeleteList();
                            }
                        }
                    }
                });
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f1decList.clear();
                f2decList.clear();
                f3decList.clear();
                f4decList.clear();
                deleteList.clear();
                allGone();
            }
        });
    }

    private void btn() {
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit.getText().toString().length() == 0) {
                    Toast.makeText(ControlActivity.this, "UID를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    saveOneMonthUser(edit.getText().toString());
                }
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
        f3DecListAdapter.notifyDataSetChanged();
        ;
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

    private void getDeleteList() {
        DescendingDelete descendingDelete = new DescendingDelete();
        Collections.sort(deleteList, descendingDelete);
        layoutManager = new LinearLayoutManager(ControlActivity.this);
        deletelist.setLayoutManager(layoutManager);
        deleteAdapter = new DeleteAdapter(deleteList);
        deletelist.setAdapter(deleteAdapter);
        deleteAdapter.notifyDataSetChanged();
        deleteUserLayout.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteEdit.getText().toString().length() == 0) {
                    Toast.makeText(ControlActivity.this, "UID입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("deleteUser").document(deleteEdit.getText().toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ControlActivity.this, "회원 완전 삭제 성공", Toast.LENGTH_SHORT).show();
                            deleteEdit.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(ControlActivity.this, "회원 완전 삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
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

    private void saveOneMonthUser(String uid) {
        db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String myPhoneNumber = documentSnapshot.get("phonenumber").toString();
                String blackpn = "010" + myPhoneNumber.substring(3);
                Map<String, Object> map1 = new HashMap<>();
                map1.put(blackpn, blackpn);
                db.collection("blacklist").document(blackpn).set(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("phonenumber", myPhoneNumber);
                        map.put("uid", uid);
                        map.put("date", getTime());
                        db.collection("deleteUser").document(getTime()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                deleteStorageF4chatroomImg(uid);
                            }
                        });
                    }
                });
            }
        });
    }

    private void deleteStorageF4chatroomImg(String uid) {
        reference.getRef().child("myroom").child(uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        count += 1;
                        storageReference.child("/" + uid + "/" + snapshot.getKey()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                int i = listResult.getItems().size();
                                for (StorageReference item : listResult.getItems()) {
                                    storageReference.child("/" + uid + "/" + snapshot.getKey() + "/" + item.getName()).delete();
                                }
                            }
                        });
                        if (count == dataSnapshot.getChildrenCount()) {
                            deletef2(uid);
                        }
                    }
                } else {
                    deletef2(uid);
                }
            }
        });
    }

    private void deletef2(String uid) {
        //delete f2
        db.collection("f2messege").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        db.collection("f2messege").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                deletef3(uid);
                            }
                        });
                    } else {
                        deletef3(uid);
                    }
                }
            }
        });

    }

    private void deletef3(String uid) {
        db.collection("f3messege").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        db.collection("f3messege").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                deleteF3Image(uid);
                            }
                        });
                    } else {
                        deleteRealtimeMyroom(uid);
                    }
                }
            }
        });
    }

    private void deleteF3Image(String uid) {
        storageReference.child("/fragment3/" + uid + "/" + uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteRealtimeMyroom(uid);
            }
        });
    }

    private void deleteRealtimeMyroom(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.getRef().child("myroom").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sendMessege(uid);
            }
        });
    }

    private void sendMessege(String uid) {
        db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String token = documentSnapshot.get("token").toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject root = new JSONObject();
//                                        JSONObject notification = new JSONObject();
                            JSONObject data = new JSONObject();
//                                        notification.put("body", messege);
//                                        notification.put("title", getString(R.string.app_name));
                            data.put("messege", "신고로 인해 계정이 정지되었습니다.");
                            data.put("name", "큐톡");
                            data.put("pic", "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullUser.png?alt=media&token=4c9daa69-6d03-4b19-a793-873f5739f3a1");
                            data.put("uid", "20123988");
//                                        root.put("notification", notification);
                            root.put("data", data);
                            root.put("to", token);

                            URL Url = new URL("https://fcm.googleapis.com/fcm/send");
                            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            connection.addRequestProperty("Authorization", "key=" + " AAAAqHwsNuA:APA91bEhOL4uoOR3d0Ys1qbFflQelzTPwaxBFLRI5Prx7tCor-KoivdXAKpLjz_PDlFctKT1iVPhwgXcPq8ioYh_TvaqSHPPjhCc98M5z7g9i3reg8Cqjbn-J0LbXXi0pSeMJa8KuYRk");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setRequestProperty("Content-type", "application/json");
                            OutputStream os = connection.getOutputStream();
                            os.write(root.toString().getBytes("utf-8"));
                            os.flush();
                            connection.getResponseCode();

                            Log.d("Fragment4ChatRoom>>>", "send notify");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                lastDeleteUser(uid);
            }
        });
    }

    private void lastDeleteUser(String uid) {
        databaseHandler.setDB(ControlActivity.this);
        databaseHandler = new DatabaseHandler(ControlActivity.this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        db.collection("users").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                userAuthDelete();
            }
        });
//        mAuth.signOut();
    }

    private void userAuthDelete() {

        FirebaseUser mCurrentUser;
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseHandler.uniquedelete();
                startActivity(new Intent(ControlActivity.this, SplashActivity.class));
                MainActivity.loading.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                userAuthDelete();
            }
        });
    }

    private void allGone() {
        f1declist.setVisibility(View.GONE);
        f2declist.setVisibility(View.GONE);
        f3declist.setVisibility(View.GONE);
        f4declist.setVisibility(View.GONE);
        f1decviewlayout.setVisibility(View.GONE);
        f4decviewlayout.setVisibility(View.GONE);
        deleteUserLayout.setVisibility(View.GONE);
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

    class DescendingDelete implements Comparator<DeleteUserItem> {
        @Override
        public int compare(DeleteUserItem deleteUserItem, DeleteUserItem t1) {
            return deleteUserItem.getDate().compareTo(t1.getDate());
        }
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }
}