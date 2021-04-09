package com.tistory.starcue.cuetalk.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tistory.starcue.cuetalk.AdressRoom;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.adpater.AdressRoomAdapter;
import com.tistory.starcue.cuetalk.adpater.F2Adapter;
import com.tistory.starcue.cuetalk.item.F2Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private RecyclerView.LayoutManager layoutManager;
    private F2Adapter adapter;
    private ArrayList<F2Item> arrayList;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String myUid;

    RecyclerView recyclerView;
    Button write;

    EditText dialogEditText;
    Button dialogyes, dialogno;
    SwipeRefreshLayout swipeRefreshLayout;

    private GpsTracker gpsTracker;

    private AlertDialog alertDialog;

    private String name, pic, sex, age, latitudeS, longitudeS, messege;

    private ProgressBar progressBar;

    public Fragment2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        setfiredb();
        setinit(rootView);

        return rootView;
    }

    private void setinit(View v) {
        recyclerView = v.findViewById(R.id.f2re);
        write = v.findViewById(R.id.writemsg);
        progressBar = v.findViewById(R.id.fragment2_progress_bar);
        swipeRefreshLayout = v.findViewById(R.id.f2_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        setWrite();
        setRecyclerView();
    }

    private void setfiredb() {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        adapter = new F2Adapter(arrayList, getActivity());
        recyclerView.setAdapter(adapter);

//        Query query = firestore.collection("f2messege").orderBy("time", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<F2Item> option = new FirestoreRecyclerOptions.Builder<F2Item>().setQuery(query, F2Item.class).build();


    }

    private void setWrite() {
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout = (LinearLayout) vi.inflate(R.layout.f2wirte_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(layout);
                alertDialog = builder.create();

                alertDialog.show();
                //set size
                WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                alertDialog.getWindow().setAttributes(layoutParams);

                dialogEditText = layout.findViewById(R.id.f2write_edit);
                dialogyes = layout.findViewById(R.id.f2write_ok);
                dialogno = layout.findViewById(R.id.f2write_no);
                progressBar = layout.findViewById(R.id.fragment2_dialog_progress_bar);

                dialogyes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String getMessege = dialogEditText.getText().toString();
                        if (!getMessege.equals("")) {
                            progressBar.setVisibility(View.VISIBLE);
                            write();
                        } else {
                            Toast.makeText(getActivity(), "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialogno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogEditText.setText("");
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void write() {
        firestore.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name = documentSnapshot.get("name").toString();
                sex = documentSnapshot.get("sex").toString();
                if (documentSnapshot.get("pic") == null) {
                    if (sex.equals("남자")) {
                        pic = nullPic;
                    } else {
                        pic = nullPicF;
                    }
                } else {
                    pic = documentSnapshot.get("pic").toString();
                }

                age = documentSnapshot.get("age").toString();

                messege = dialogEditText.getText().toString();

                gpsTracker = new GpsTracker(getActivity());
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                latitudeS = String.valueOf(latitude);
                longitudeS = String.valueOf(longitude);

                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("uid", myUid);
                map.put("sex", sex);
                map.put("messege", messege);
                map.put("latitude", latitudeS);
                map.put("longitude", longitudeS);
                map.put("pic", pic);
                map.put("time", getTime());

                firestore.collection("f2messege").document(myUid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        closeKeyBoard();
                        Toast.makeText(getActivity(), "성공", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        closeKeyBoard();
                        Toast.makeText(getActivity(), "실패, 다시시도", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                Log.d("Fragment2", name + pic + sex + age + latitude + longitude);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closeKeyBoard();
                Toast.makeText(getActivity(), "실패, 다시시도", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd hh:mm:ss");
        String date = format.format(mDate);
        return date;
    }

    private void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dialogEditText.getWindowToken(), 0);
//                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        Log.d("Fragment2>>>", "onResume");
        if (arrayList.size() == 0) {
            firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                        F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);
                        arrayList.add(f2Item);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        arrayList.clear();
    }

    @Override
    public void onRefresh() {
        firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                    F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);
                    arrayList.add(f2Item);
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void isLoading() {

    }

    private void isLoaded() {

    }
}