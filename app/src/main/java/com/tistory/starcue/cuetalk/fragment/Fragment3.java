package com.tistory.starcue.cuetalk.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class Fragment3 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SharedPreferences sharedPreferences;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";
    String[] items = {"category 선택", "컴퓨터/전자", "중고핸드폰", "암거나"};

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

    Button b1, b2, b3, my, write;
    SwipeRefreshLayout swipeRefreshLayout;

    private AlertDialog alertDialog;
    Spinner dialogSpinner;
    ImageView dialogImageView;
    Button dialogBtn, dialogyes, dialogno;
    EditText dialogEditText;
    ProgressBar dialogProgressBar;

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
        b1 = v.findViewById(R.id.f3b1);
        b2 = v.findViewById(R.id.f3b2);
        b3 = v.findViewById(R.id.f3b3);
        my = v.findViewById(R.id.fragment3_my);
        write = v.findViewById(R.id.fragment3_write);
        swipeRefreshLayout = v.findViewById(R.id.f3_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDialog();
            }
        });

        setRecyclerView();
        setCategory();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        adapter = new F3Adapter(arrayList, getActivity());
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
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        dialogSpinner = layout.findViewById(R.id.f3write_spinner);
        dialogImageView = layout.findViewById(R.id.f3write_img);
        dialogBtn = layout.findViewById(R.id.f3write_add_btn);
        dialogEditText = layout.findViewById(R.id.f3write_edit);
        dialogyes = layout.findViewById(R.id.f3write_ok);
        dialogno = layout.findViewById(R.id.f3write_no);
        dialogProgressBar = layout.findViewById(R.id.fragment3_dialog_progress_bar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        dialogSpinner.setAdapter(adapter);
        dialogSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    category = "0";
                } else if (i == 1) {
                    category = "1";
                } else if (i == 2) {
                    category = "2";
                } else if (i == 3) {
                    category = "3";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "nothing", Toast.LENGTH_LONG).show();
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
                messege = dialogEditText.getText().toString();

                if (category.equals("0")) {
                    Toast.makeText(getActivity(), "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (picUri == null) {
                    Toast.makeText(getActivity(), "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (messege.equals("")) {
                    Toast.makeText(getActivity(), "메시지를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Fragment3>>>", "get pic uri: " + picUri);
                    firstUploadPicInStorage();
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
            dialogImageView.setImageURI(imageUri);
        }
    }

    private void firstUploadPicInStorage() {
        if (imageUri != null) { //pic 변경 및 신규등록
            storageReference = FirebaseStorage.getInstance().getReference();
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setTitle("이미지 업로드 중...");
            pd.show();

            String uid = mAuth.getUid();
            StorageReference riversRef = storageReference.child("fragment3/" + uid);
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Fragment3>>>", "test 1");
                    //upload pic in firestore
                    storageReference.child("fragment3/" + uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        closeKeyBoard();
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

                    }
                });
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss");//hh = 12, HH = 24
        String date = format.format(mDate);
        return date;
    }

    private void setCategory() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 0;
                getDataListAll();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                getDataList1();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 2;
                getDataList2();
            }
        });

        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 3;
                getDataListMy();
            }
        });
    }

    private void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dialogEditText.getWindowToken(), 0);
//                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment3>>>", "get page in onResume: " + page);
        getPage();
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
}