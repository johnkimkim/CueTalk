package com.tistory.starcue.cuetalk.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.adpater.F2Adapter;
import com.tistory.starcue.cuetalk.item.F2Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SharedPreferences sharedPreferences;

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
    Button btn1, btn2, btn3, btn4, btn5;
    public static CheckBox f2fragdec;

    EditText dialogEditText;
    Button dialogyes, dialogno;
    TextView dialogcount;
    RelativeLayout dialogload;
    SwipeRefreshLayout swipeRefreshLayout;

    private GpsTracker gpsTracker;

    private AlertDialog alertDialog;

    private String name, pic, sex, age, latitudeS, longitudeS, messege;

    private ProgressBar progressBar;

    int page;

    public Fragment2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        Log.d("Fragment2>>>", "start onCreate");

        gpsTracker = new GpsTracker(getActivity());

        setfiredb();
        setinit(rootView);

        return rootView;
    }

    private void setinit(View v) {
        recyclerView = v.findViewById(R.id.f2re);
        btn1 = v.findViewById(R.id.f2btn1);
        btn2 = v.findViewById(R.id.f2btn2);
        btn3 = v.findViewById(R.id.f2btn3);
        btn4 = v.findViewById(R.id.f2btn4);
        btn5 = v.findViewById(R.id.f2btn5);
        write = v.findViewById(R.id.writemsg);
        f2fragdec = v.findViewById(R.id.f2fragdec);
        f2fragdec.setChecked(false);
        f2fragdec.setBackgroundResource(R.drawable.sirenicon);
        progressBar = v.findViewById(R.id.fragment2_progress_bar);
        progressBar.bringToFront();
        swipeRefreshLayout = v.findViewById(R.id.f2_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        setWrite();
        setRecyclerView();
        setBtn();
        setDec();
    }

    private void setDec() {
        f2fragdec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f2fragdec.isChecked()) {
                    f2fragdec.setBackgroundResource(R.drawable.sirendefaulticon);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    f2fragdec.setBackgroundResource(R.drawable.sirenicon);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setfiredb() {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
    }

    private void setBtn() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                btn1.setBackgroundResource(R.drawable.button_defult);
                btn1.setTextColor(getResources().getColor(R.color.white));
                btn2.setBackgroundResource(R.drawable.button_empty);
                btn2.setTextColor(getResources().getColor(R.color.black));
                btn3.setBackgroundResource(R.drawable.button_empty);
                btn3.setTextColor(getResources().getColor(R.color.black));
                btn4.setBackgroundResource(R.drawable.button_empty);
                btn4.setTextColor(getResources().getColor(R.color.black));
                btn5.setBackgroundResource(R.drawable.button_empty);
                btn5.setTextColor(getResources().getColor(R.color.black));
                progressBar.setVisibility(View.VISIBLE);
                setListBtn1();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 2;
                btn1.setBackgroundResource(R.drawable.button_empty);
                btn1.setTextColor(getResources().getColor(R.color.black));
                btn2.setBackgroundResource(R.drawable.button_defult);
                btn2.setTextColor(getResources().getColor(R.color.white));
                btn3.setBackgroundResource(R.drawable.button_empty);
                btn3.setTextColor(getResources().getColor(R.color.black));
                btn4.setBackgroundResource(R.drawable.button_empty);
                btn4.setTextColor(getResources().getColor(R.color.black));
                btn5.setBackgroundResource(R.drawable.button_empty);
                btn5.setTextColor(getResources().getColor(R.color.black));
                progressBar.setVisibility(View.VISIBLE);
                setListBtn2();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 3;
                btn1.setBackgroundResource(R.drawable.button_empty);
                btn1.setTextColor(getResources().getColor(R.color.black));
                btn2.setBackgroundResource(R.drawable.button_empty);
                btn2.setTextColor(getResources().getColor(R.color.black));
                btn3.setBackgroundResource(R.drawable.button_defult);
                btn3.setTextColor(getResources().getColor(R.color.white));
                btn4.setBackgroundResource(R.drawable.button_empty);
                btn4.setTextColor(getResources().getColor(R.color.black));
                btn5.setBackgroundResource(R.drawable.button_empty);
                btn5.setTextColor(getResources().getColor(R.color.black));
                progressBar.setVisibility(View.VISIBLE);
                setListBtn3();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 4;
                btn1.setBackgroundResource(R.drawable.button_empty);
                btn1.setTextColor(getResources().getColor(R.color.black));
                btn2.setBackgroundResource(R.drawable.button_empty);
                btn2.setTextColor(getResources().getColor(R.color.black));
                btn3.setBackgroundResource(R.drawable.button_empty);
                btn3.setTextColor(getResources().getColor(R.color.black));
                btn4.setBackgroundResource(R.drawable.button_defult);
                btn4.setTextColor(getResources().getColor(R.color.white));
                btn5.setBackgroundResource(R.drawable.button_empty);
                btn5.setTextColor(getResources().getColor(R.color.black));
                progressBar.setVisibility(View.VISIBLE);
                setListBtn4();
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 5;
                btn1.setBackgroundResource(R.drawable.button_empty);
                btn1.setTextColor(getResources().getColor(R.color.black));
                btn2.setBackgroundResource(R.drawable.button_empty);
                btn2.setTextColor(getResources().getColor(R.color.black));
                btn3.setBackgroundResource(R.drawable.button_empty);
                btn3.setTextColor(getResources().getColor(R.color.black));
                btn4.setBackgroundResource(R.drawable.button_empty);
                btn4.setTextColor(getResources().getColor(R.color.black));
                btn5.setBackgroundResource(R.drawable.button_defult);
                btn5.setTextColor(getResources().getColor(R.color.white));
                progressBar.setVisibility(View.VISIBLE);
                setListBtn5();
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        adapter = new F2Adapter(arrayList, getActivity(), Glide.with(Fragment2.this), getActivity());
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
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                Window window = alertDialog.getWindow();
                int x = (int) (size.x * 0.9);
                int y = (int) (size.y * 0.5);
                window.setLayout(x, y);

                dialogEditText = layout.findViewById(R.id.f2write_edit);
                dialogyes = layout.findViewById(R.id.f2write_ok);
                dialogno = layout.findViewById(R.id.f2write_no);
                dialogcount = layout.findViewById(R.id.f2write_text_count);
                dialogload = layout.findViewById(R.id.f2write_load);
                dialogload.setVisibility(View.GONE);

                dialogEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String input = dialogEditText.getText().toString();
                        dialogcount.setText(input.length() + " / 50");
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                dialogEditText.setOnKeyListener(new View.OnKeyListener() {//줄바꿈 방지
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (getCharNumber(dialogEditText.getText().toString(), "\n") == 2) {
                            if (i == keyEvent.KEYCODE_ENTER) {
                                return true;
                            }
                        }
                        return false;
                    }
                });

                dialogyes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String getMessege = dialogEditText.getText().toString();
                        if (!getMessege.equals("")) {
                            hideKeyboard(view);
                            dialogload.setVisibility(View.VISIBLE);
                            alertDialog.setCancelable(false);
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

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                latitudeS = String.valueOf(latitude);
                longitudeS = String.valueOf(longitude);

                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("uid", myUid);
                map.put("sex", sex);
                map.put("age", age);
                map.put("messege", messege);
                map.put("latitude", latitudeS);
                map.put("longitude", longitudeS);
                map.put("pic", pic);
                map.put("time", getTime());

                firestore.collection("f2messege").document(myUid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "성공", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        if (page == 1) {
                            setListBtn1();
                        } else if (page == 2) {
                            setListBtn2();
                        } else if (page == 3) {
                            setListBtn3();
                        } else if (page == 4) {
                            setListBtn4();
                        } else if (page == 5) {
                            setListBtn5();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "네트워크 오류로 인해 글 작성에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        dialogload.setVisibility(View.GONE);
                        alertDialog.setCancelable(true);
                    }
                });

                Log.d("Fragment2", name + pic + sex + age + latitude + longitude);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "네트워크 오류로 인해 글 작성에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                dialogload.setVisibility(View.GONE);
                alertDialog.setCancelable(true);
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPage();
        progressBar.setVisibility(View.VISIBLE);
        f2fragdec.setBackgroundResource(R.drawable.sirenicon);
        Log.d("Fragment2>>>", "onResume");
        if (arrayList.size() == 0) {
            if (page == 1) {
                btn1.setBackgroundResource(R.drawable.button_defult);
                btn1.setTextColor(getResources().getColor(R.color.white));
                setListBtn1();
            } else if (page == 2) {
                btn2.setBackgroundResource(R.drawable.button_defult);
                btn2.setTextColor(getResources().getColor(R.color.white));
                setListBtn2();
            } else if (page == 3) {
                btn3.setBackgroundResource(R.drawable.button_defult);
                btn3.setTextColor(getResources().getColor(R.color.white));
                setListBtn3();
            } else if (page == 4) {
                btn4.setBackgroundResource(R.drawable.button_defult);
                btn4.setTextColor(getResources().getColor(R.color.white));
                setListBtn4();
            } else if (page == 5) {
                btn5.setBackgroundResource(R.drawable.button_defult);
                btn5.setTextColor(getResources().getColor(R.color.white));
                setListBtn5();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        f2fragdec.setChecked(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        savePage();
        arrayList.clear();
    }

    @Override
    public void onRefresh() {
        Log.d("Fragment1>>>", "onRefresh page: " + page);
        progressBar.setVisibility(View.VISIBLE);
        if (page == 1) {
            setListBtn1();
        } else if (page == 2) {
            setListBtn2();
        } else if (page == 3) {
            setListBtn3();
        } else if (page == 4) {
            setListBtn4();
        } else if (page == 5) {
            setListBtn5();
        }
    }


    private void isLoading() {
        //모든버튼 setVisitibl.gone
    }

    private void isLoaded() {

    }

    private void savePage() {
        sharedPreferences = getActivity().getSharedPreferences("pageSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("page", page);
        editor.apply();
        Log.d("Fragment2>>>", "savePage");
    }

    private void getPage() {
        sharedPreferences = getActivity().getSharedPreferences("pageSP", Context.MODE_PRIVATE);
        page = sharedPreferences.getInt("page", 1);
        Log.d("Fragment2>>>", "get page: " + page);
    }

    private void setListBtn1() {
        firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                    F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);

                    arrayList.add(f2Item);
                    setListTimeSort(arrayList);
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setListBtn2() {
        firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                    F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);
                    String userLatitude = queryDocumentSnapshots1.get("latitude").toString();
                    String userLongitude = queryDocumentSnapshots1.get("longitude").toString();
                    double userLatitudeD = Double.parseDouble(userLatitude);
                    double userLongitudeD = Double.parseDouble(userLongitude);

                    double myLatitude = gpsTracker.getLatitude();
                    double myLongitude = gpsTracker.getLongitude();
                    double kmDouble = getDistance(myLatitude, myLongitude, userLatitudeD, userLongitudeD);
                    int i = (int) Math.floor(kmDouble);
                    Log.d("Fragment2>>>", "how many km; " + i);
                    if (kmDouble <= 30000) {
                        arrayList.add(f2Item);
                        setListTimeSort(arrayList);
                    }

                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setListBtn3() {
        firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                    F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);
                    String userLatitude = queryDocumentSnapshots1.get("latitude").toString();
                    String userLongitude = queryDocumentSnapshots1.get("longitude").toString();
                    double userLatitudeD = Double.parseDouble(userLatitude);
                    double userLongitudeD = Double.parseDouble(userLongitude);

                    double myLatitude = gpsTracker.getLatitude();
                    double myLongitude = gpsTracker.getLongitude();
                    double kmDouble = getDistance(myLatitude, myLongitude, userLatitudeD, userLongitudeD);
                    int i = (int) Math.floor(kmDouble);
                    Log.d("Fragment2>>>", "how many km; " + i);
                    if (kmDouble <= 15000) {
                        arrayList.add(f2Item);
                        setListTimeSort(arrayList);
                    }

                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setListBtn4() {
        firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                    F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);
                    String userLatitude = queryDocumentSnapshots1.get("latitude").toString();
                    String userLongitude = queryDocumentSnapshots1.get("longitude").toString();
                    double userLatitudeD = Double.parseDouble(userLatitude);
                    double userLongitudeD = Double.parseDouble(userLongitude);

                    double myLatitude = gpsTracker.getLatitude();
                    double myLongitude = gpsTracker.getLongitude();
                    double kmDouble = getDistance(myLatitude, myLongitude, userLatitudeD, userLongitudeD);
                    int i = (int) Math.floor(kmDouble);
                    Log.d("Fragment2>>>", "how many km; " + i);
                    if (kmDouble <= 6000) {
                        arrayList.add(f2Item);
                        setListTimeSort(arrayList);
                    }

                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setListBtn5() {
        firestore.collection("f2messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
                    if (queryDocumentSnapshots1.get("uid").equals(myUid)) {
                        F2Item f2Item = queryDocumentSnapshots1.toObject(F2Item.class);
                        arrayList.add(f2Item);
                        setListTimeSort(arrayList);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    private static class Descending implements Comparator<F2Item> {
        @Override
        public int compare(F2Item f2Item, F2Item t1) {
            return t1.getTime().compareTo(f2Item.getTime());
        }
    }

    private void setListTimeSort(ArrayList<F2Item> arrayList) {
        Descending descending = new Descending();
        Collections.sort(arrayList, descending);
    }

    private void hideKeyboard(View v) {
        InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    int getCharNumber(String string, String string1) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            char ss = string.charAt(i);
            String s = Character.toString(ss);
            if (s.equals(string1)) {
                count++;
            }
        }
        return count;
    }
}