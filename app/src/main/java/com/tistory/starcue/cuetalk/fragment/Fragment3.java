package com.tistory.starcue.cuetalk.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.adpater.F3Adapter;
import com.tistory.starcue.cuetalk.item.F3Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class Fragment3 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SharedPreferences sharedPreferences;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private GpsTracker gpsTracker;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private String myUid;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private F3Adapter adapter;

    private ArrayList<F3Item> arrayList;

    RadioGroup f3radioGroup1, f3radioGroup2;
    RadioButton f3radioall, f3radio1, f3radio2, f3radio3, f3radio4, f3radio5, f3radio6, f3radio7, f3radio8;
    Button write;
    SwipeRefreshLayout swipeRefreshLayout;
    public static CheckBox f3fragdec;
    private ProgressBar progressBar;

    private AlertDialog alertDialog;
    ImageView dialogImageView;
    Button dialogBtn, dialogyes, dialogno;
    EditText dialogEditText;
    ProgressBar dialogProgressBar;
    TextView dialogcount;
    RadioGroup radioGroup1, radioGroup2;
    RadioButton radio1, radio2, radio3, radio4, radio5, radio6;
    boolean group = false;

    Uri imageUri;
    String picUri, messege, category;
    String age, sex, name, latitude, longitude, myPic;

    private int page;

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

        gpsTracker = new GpsTracker(getActivity());

        setfirebase();
        setinit(rootView);

        return rootView;
    }

    private void setfirebase() {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
    }

    private void setinit(ViewGroup v) {
        recyclerView = v.findViewById(R.id.fragment3_recyclerview);
        f3radioGroup1 = v.findViewById(R.id.f3radio_group1);
        f3radioGroup2 = v.findViewById(R.id.f3radio_group2);
        f3radioall = v.findViewById(R.id.f3radio_all);
        f3radio1 = v.findViewById(R.id.f3radio1);
        f3radio2 = v.findViewById(R.id.f3radio2);
        f3radio3 = v.findViewById(R.id.f3radio3);
        f3radio4 = v.findViewById(R.id.f3radio4);
        f3radio5 = v.findViewById(R.id.f3radio5);
        f3radio6 = v.findViewById(R.id.f3radio6);
        f3radio7 = v.findViewById(R.id.f3radio7);
        f3radio8 = v.findViewById(R.id.f3radio8);
        write = v.findViewById(R.id.fragment3_write);
        f3fragdec = v.findViewById(R.id.f3fragdec);
        f3fragdec.setChecked(false);
        f3fragdec.setBackgroundResource(R.drawable.sirenicon);
        swipeRefreshLayout = v.findViewById(R.id.f3_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = v.findViewById(R.id.fragment3_progress_bar);

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDialog();
            }
        });

        setRecyclerView();
//        setCategory();
        setDec();
    }

    private void setDec() {
        f3fragdec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f3fragdec.isChecked()) {
                    f3fragdec.setBackgroundResource(R.drawable.sirendefaulticon);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    f3fragdec.setBackgroundResource(R.drawable.sirenicon);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        arrayList = new ArrayList<>();

        adapter = new F3Adapter(arrayList, getActivity(), Glide.with(Fragment3.this), getActivity());
        recyclerView.setAdapter(adapter);

    }

    private void getDataListAll() {
        firestore.collection("f3messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                Log.d("Fragment3>>>", "getDataListAll");
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    F3Item f3Item = snapshot.toObject(F3Item.class);
                    arrayList.add(f3Item);
                    if (arrayList.size() == 2) {
                        Log.d("Fragment3>>>", "user pic: " + arrayList.get(1).getPic());
                    }
                    setListTimeSort(arrayList);
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDataList1() {
        firestore.collection("f3messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    if (snapshot.get("category").equals("1")) {
                        F3Item f3Item = snapshot.toObject(F3Item.class);
                        arrayList.add(f3Item);
                        setListTimeSort(arrayList);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDataList2() {
        firestore.collection("f3messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    if (snapshot.get("category").equals("2")) {
                        F3Item f3Item = snapshot.toObject(F3Item.class);
                        arrayList.add(f3Item);
                        setListTimeSort(arrayList);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDataListMy() {
        firestore.collection("f3messege").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                arrayList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    if (snapshot.get("uid").equals(myUid)) {
                        F3Item f3Item = snapshot.toObject(F3Item.class);
                        arrayList.add(f3Item);
                        setListTimeSort(arrayList);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void writeDialog() {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.f3write_dialog, null);
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
        int y = (int) (size.y * 0.8);
        window.setLayout(x, y);

        int xx = (int) (size.x * 0.22);

        dialogImageView = layout.findViewById(R.id.f3write_img);
        dialogBtn = layout.findViewById(R.id.f3write_add_btn);
        dialogEditText = layout.findViewById(R.id.f3write_edit);
        dialogyes = layout.findViewById(R.id.f3write_ok);
        dialogno = layout.findViewById(R.id.f3write_no);
        dialogProgressBar = layout.findViewById(R.id.fragment3_dialog_progress_bar);
        dialogcount = layout.findViewById(R.id.f3write_text_count);
        radioGroup1 = layout.findViewById(R.id.f3dialog_radiogroup1);
        radioGroup2 = layout.findViewById(R.id.f3dialog_radiogroup2);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        radio5 = layout.findViewById(R.id.radio5);
        radio6 = layout.findViewById(R.id.radio6);

//        if (radio1.isChecked()) {
//            radio1.setBackgroundColor(getResources().getColor(R.color.base1));
//        } else {
//            radio1.setBackgroundColor(getResources().getColor(R.color.white));
//        }

        ViewGroup.LayoutParams params = dialogImageView.getLayoutParams();
        params.width = xx;
        params.height = xx;
        dialogImageView.setLayoutParams(params);

        dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = dialogEditText.getText().toString();
                dialogcount.setText(input.length() + " / 20");
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

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooosePic();
            }
        });

        dialogyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                alertDialog.setCancelable(false);
                messege = dialogEditText.getText().toString();

                if (!radio1.isChecked() && !radio2.isChecked() && !radio3.isChecked() && !radio4.isChecked() && !radio5.isChecked() && !radio6.isChecked()) {
                    Toast.makeText(getActivity(), "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (picUri == null) {
                    Toast.makeText(getActivity(), "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (messege.equals("")) {
                    Toast.makeText(getActivity(), "메시지를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Fragment3>>>", "get pic uri: " + picUri);

                    dialogLoading();
                    firstUploadPicInStorage();
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

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != -1 && group) {
                    group = false;
                    radioGroup2.clearCheck();
                }
                group = true;
                if (radio1.isChecked()) {
                    category = "핸폰";
                } else if (radio2.isChecked()) {
                    category = "컴터";
                } else if (radio3.isChecked()) {
                    category = "패션";
                }
            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != -1 && group) {
                    group = false;
                    radioGroup1.clearCheck();
                }
                group = true;
                if (radio4.isChecked()) {
                    category = "미용";
                } else if (radio5.isChecked()) {
                    category = "전자";
                } else if (radio6.isChecked()) {
                    category = "티켓";
                }
            }
        });
    }

    private void chooosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            picUri = imageUri.toString();
//            dialogImageView.setImageURI(imageUri);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(8));
            Glide.with(Fragment3.this).load(imageUri).override(150, 150)
                    .apply(requestOptions)
                    .into(dialogImageView);
        }
    }

    private void firstUploadPicInStorage() {
        if (imageUri != null) { //pic 변경 및 신규등록
            storageReference = FirebaseStorage.getInstance().getReference();
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setTitle("이미지 업로드 중...");
            pd.show();

            String uid = mAuth.getUid();
            StorageReference riversRef = storageReference.child("fragment3/" + uid + "/" + uid);
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Fragment3>>>", "test 1");
                    //upload pic in firestore
                    storageReference.child("fragment3/" + uid + "/" + uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Fragment3>>>", "test 2");
                            String picUri = uri.toString();
                            write(picUri);
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });//upload pic in firestore
//                    relativeLayout.setVisibility(View.GONE);
//                    alertDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
//                    relativeLayout.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "업로드실패", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Pro: " + (int) progressPercent + "%");
                }
            });
        }
    }

    private void write(String storageUri) {
        Map<String, Object> map = new HashMap<>();
        firestore.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                age = documentSnapshot.get("age").toString();
                sex = documentSnapshot.get("sex").toString();
                name = documentSnapshot.get("name").toString();
                if (documentSnapshot.get("pic") == null) {
                    if (sex.equals("남자")) {
                        myPic = nullPic;
                    } else {
                        myPic = nullPicF;
                    }
                } else {
                    myPic = documentSnapshot.get("pic").toString();
                }

                double Dlatitude = gpsTracker.getLatitude();
                double Dlongitude = gpsTracker.getLongitude();

                latitude = String.valueOf(Dlatitude);
                longitude = String.valueOf(Dlongitude);
                Log.d("Fragment3>>>", "get string latitude: " + latitude);
                Log.d("Fragment3>>>", "get string longitude: " + longitude);

                map.put("uid", myUid);
                map.put("pic", myPic);
                map.put("age", age);
                map.put("sex", sex);
                map.put("name", name);
                map.put("category", category);
                map.put("latitude", latitude);
                map.put("longitude", longitude);
                map.put("messege", messege);
                map.put("time", getTime());
                map.put("ppic", storageUri);

//                uploadPic(map);

                firestore.collection("f3messege").document(myUid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialogFinishLoading();
                        Toast.makeText(getActivity(), "중고장터 글쓰기 성공", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                        if (page == 0) {
                            getDataListAll();
                        } else if (page == 1) {
                            getDataList1();
                        } else if (page == 2) {
                            getDataList2();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogFinishLoading();
                        Toast.makeText(getActivity(), "중고장터 글쓰기 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);//hh = 12, HH = 24
        String date = format.format(mDate);
        return date;
    }

//    private void setCategory() {
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//
//                page = 0;
//                getDataListAll();
//            }
//        });
//
//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                page = 1;
//                getDataList1();
//            }
//        });
//
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                page = 2;
//                getDataList2();
//            }
//        });
//
//        my.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                page = 3;
//                getDataListMy();
//            }
//        });
//    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment3>>>", "get page in onResume: " + page);
        getPage();
        f3fragdec.setBackgroundResource(R.drawable.sirenicon);
        progressBar.setVisibility(View.VISIBLE);
        if (page == 0) {
            getDataListAll();
        } else if (page == 1) {
            getDataList1();
        } else if (page == 2) {
            getDataList2();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        f3fragdec.setChecked(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        savePage();
        arrayList.clear();
    }

    private void savePage() {
        sharedPreferences = getActivity().getSharedPreferences("pageSP3", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("page3", page);
        editor.apply();
        Log.d("Fragment3>>>", "savePage");
    }

    private void getPage() {
        sharedPreferences = getActivity().getSharedPreferences("pageSP3", Context.MODE_PRIVATE);
        page = sharedPreferences.getInt("page3", 0);
        Log.d("Fragment3>>>", "get page in getPage: " + page);
    }

    @Override
    public void onRefresh() {
        if (page == 0) {
            getDataListAll();
        } else if (page == 1) {
            getDataList1();
        } else if (page == 2) {
            getDataList2();
        }
    }

    private static class Descending implements Comparator<F3Item> {
        @Override
        public int compare(F3Item f3Item, F3Item t1) {
            return t1.getTime().compareTo(f3Item.getTime());
        }
    }

    private void setListTimeSort(ArrayList<F3Item> arrayList) {
        Log.d("Fragment3>>>", "setListTimeSort");
        Descending descending = new Descending();
        Collections.sort(arrayList, descending);
    }

    private void hideKeyboard(View v) {
        InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void dialogLoading() {
        dialogImageView.setEnabled(false);
        dialogBtn.setEnabled(false);
        dialogyes.setEnabled(false);
        dialogno.setEnabled(false);
        dialogEditText.setEnabled(false);
    }

    private void dialogFinishLoading() {
        dialogImageView.setEnabled(true);
        dialogBtn.setEnabled(true);
        dialogyes.setEnabled(true);
        dialogno.setEnabled(true);
        dialogEditText.setEnabled(true);
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