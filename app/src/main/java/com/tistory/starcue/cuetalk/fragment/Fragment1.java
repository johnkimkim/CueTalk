package com.tistory.starcue.cuetalk.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.tistory.starcue.cuetalk.AdressRoom;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.f1viewpager.F1F1;
import com.tistory.starcue.cuetalk.f1viewpager.F1F2;
import com.tistory.starcue.cuetalk.f1viewpager.F1F3;
import com.tistory.starcue.cuetalk.f1viewpager.F1F4;
import com.tistory.starcue.cuetalk.f1viewpager.F1F5;
import com.tistory.starcue.cuetalk.f1viewpager.F1F6;
import com.tistory.starcue.cuetalk.f1viewpager.F1F7;
import com.tistory.starcue.cuetalk.f1viewpager.F1F8;
import com.tistory.starcue.cuetalk.f1viewpager.F1ViewpagerIntent;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Fragment1 extends Fragment {

    RelativeLayout viewpagerlayout;
    Button viewpagerbtn;
    ViewPager viewPager;
    F1SectionsPagerAdapter f1SectionsPagerAdapter;
    CountDownTimer countDownTimer;
    boolean isRunning = true;

    public static Spinner spinner, spinner1;
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter;
    List<String> firstAdressList = new ArrayList<>();
    List<String> adressList = new ArrayList<>();
    List<String> onlyAdressList = new ArrayList<>();

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseReference reference;

    private FirebaseFirestore db;

    String myUid;
    String isnull;

    int pageInt;

    private GestureDetector detector;

    private WormDotsIndicator wormDotsIndicator;

    public Fragment1() {
        // Required empty public constructor
    }

    private void testclass(ViewGroup viewGroup) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String pn = mUser.getPhoneNumber().substring(9, 13);
        Log.d("Fragment1>>>", "testtest: " + pn);

        String myUid = mAuth.getUid();

        Button testbtn = viewGroup.findViewById(R.id.testbtn);
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testbtn.setEnabled(false);
//                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("Fragment1>>>", "testtest: " + document.get("phonenumber").toString());
//                            }
//                        }
//                    }
//                });
//                db.collection("users").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot documentSnapshot = task.getResult();
//                            if (documentSnapshot.get("name1") != null) {
//                                Log.d("Fragment1>>>", "test have");
//                            } else {
//                                Log.d("Fragment1>>>", "test null");
//                            }
//                        }
//                    }
//                });
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        db = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();
        Log.d("Fragment1>>>", "start onCreate");
        addFirstAdressList();
        setViewPager(rootView);

        spinner1 = rootView.findViewById(R.id.f1spinner1);
        spinner = rootView.findViewById(R.id.f1spinner);

//        try {
//            Field popup = Spinner.class.getDeclaredField("mPopup");
//            popup.setAccessible(true);
//            ListPopupWindow window = (ListPopupWindow) popup.get(spinner1);
//            window.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_popup_outline));
//        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }

        adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, firstAdressList);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, adressList);
        adapter1.setDropDownViewResource(android.R.layout.select_dialog_item);

        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner1.setAdapter(adapter1);
        spinner.setAdapter(adapter);
        adapter1.notifyDataSetChanged();
        adapter.notifyDataSetChanged();

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = firstAdressList.get(i);
                if (!s.equals("지역 선택")) {
                    spinner.setEnabled(true);
                    setAdressList(s);
                } else {
                    onlyAdressList.clear();
                    adressList.clear();
                    onlyAdressList.add("방 선택");
                    adressList.add("방 선택");
                    spinner.setEnabled(false);
                    spinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = onlyAdressList.get(i);
                if (!s.equals("방 선택")) {
                    MainActivity.loading.setVisibility(View.VISIBLE);
                    reference.getRef().child("adressRoom").child(s).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() < 30) {
                                setDbAdress(s);
                                Intent intent = new Intent(getActivity(), AdressRoom.class);
//                                intent.putExtra("adress", s);
                                startActivity(intent);
                                spinner1.setSelection(0);
                                spinner.setSelection(0);
                                MainActivity.loading.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "대화방 인원수가 꽉 찼습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "nothing", Toast.LENGTH_LONG).show();
            }
        });

        testclass(rootView);

        return rootView;
    }

    private void setViewPager(View view) {
        viewpagerlayout = view.findViewById(R.id.viewpagerlayout);
        viewpagerbtn = view.findViewById(R.id.viewpagerbtn);
        viewpagerbtn.setBackgroundResource(R.drawable.pause);
        viewPager = view.findViewById(R.id.f1viewpager);
        wormDotsIndicator = view.findViewById(R.id.f1dot);
        f1SectionsPagerAdapter = new F1SectionsPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager());

//        viewPager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Fragment1>>>", "viewpager onClick");
//                int position = viewPager.getCurrentItem() + 1;
//                String field = "f1f" + Integer.toString(position);
//                F1ViewpagerIntent.f1viewpagerintent(getActivity(), field);
//            }
//        });

        db.collection("f1viewpager").document("page").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String page = documentSnapshot.get("page").toString();
                pageInt = Integer.parseInt(page);
                int count = 0;

                for (int i = 1; i <= pageInt; i++) {
                    count += 1;
                    if (i == 1) {
                        F1F1 f1f1 = new F1F1();
                        f1SectionsPagerAdapter.addItem(f1f1);
                    } else if (i == 2) {
                        F1F2 f1f2 = new F1F2();
                        f1SectionsPagerAdapter.addItem(f1f2);
                    } else if (i == 3) {
                        F1F3 f1f3 = new F1F3();
                        f1SectionsPagerAdapter.addItem(f1f3);
                    } else if (i == 4) {
                        F1F4 f1f4 = new F1F4();
                        f1SectionsPagerAdapter.addItem(f1f4);
                    } else if (i == 5) {
                        F1F5 f1f5 = new F1F5();
                        f1SectionsPagerAdapter.addItem(f1f5);
                    } else if (i == 6) {
                        F1F6 f1f6 = new F1F6();
                        f1SectionsPagerAdapter.addItem(f1f6);
                    } else if (i == 7) {
                        F1F7 f1f7 = new F1F7();
                        f1SectionsPagerAdapter.addItem(f1f7);
                    } else if (i == 8) {
                        F1F8 f1f8 = new F1F8();
                        f1SectionsPagerAdapter.addItem(f1f8);
                    }
                    if (count == pageInt) {
                        viewPager.setOffscreenPageLimit(2);
                        viewPager.setAdapter(f1SectionsPagerAdapter);
                        wormDotsIndicator.setViewPager(viewPager);
                        setViewPagerTimer();
                        break;
                    }
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViewPagerTimer() {
        detector = new GestureDetector(getActivity(), new SingleTapGestureListener());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    countDownTimer = new CountDownTimer(10, 10) {
                        @Override
                        public void onTick(long l) {
                            if (viewPager.getCurrentItem() + 1 < pageInt) {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            } else if (viewPager.getCurrentItem() + 1 == pageInt) {
                                viewPager.setCurrentItem(0);
                            }
                        }

                        @Override
                        public void onFinish() {
                            setViewPagerTimer();
                        }
                    };
                    countDownTimer.start();
                }
            }
        }, 3000);

        viewpagerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    isRunning = false;
//                    countDownTimer.cancel();
                    handler.removeCallbacksAndMessages(null);
                    viewpagerbtn.setBackgroundResource(R.drawable.play);
                } else {
                    isRunning = true;
                    setViewPagerTimer();
                    viewpagerbtn.setBackgroundResource(R.drawable.pause);
                }
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int action = motionEvent.getAction();
                int action = motionEvent.getActionMasked();
                Log.d("Fragment1>>>", "action: " + action);
                detector.onTouchEvent(motionEvent);
                if (action == MotionEvent.ACTION_DOWN) {
                    isRunning = false;
//                    countDownTimer.cancel();
                    handler.removeCallbacksAndMessages(null);
                    viewpagerbtn.setBackgroundResource(R.drawable.play);
                } else if (action == MotionEvent.ACTION_MASK) {
                    Log.d("Fragment1>>>", "action1: " + action);
                }
                return false;
            }
        });
    }

    private final class SingleTapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (velocityX < 0) {
                    //오른쪽으로 이동 완료
                    Log.d("Fragment1>>>", "viewpager go right");
                } else {
                    //왼쪽으로 이동 완료
                }
            } catch (Exception e) {
                Log.d("Fragment1>>>", "onFling Error: " + e);
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {//viewpager onClick
            int position = viewPager.getCurrentItem() + 1;
            String field = "f1f" + Integer.toString(position);
            F1ViewpagerIntent.f1viewpagerintent(getActivity(), field);
            return super.onSingleTapUp(e);
        }
    }


    public static class F1SectionsPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<>();

        public F1SectionsPagerAdapter(@NonNull @NotNull FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @Override
        public Fragment getItem(int i) {
            return items.get(i);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    private void addFirstAdressList() {
        adressList.add("방 선택");
        firstAdressList.add("지역 선택");
        firstAdressList.add("서울");
        firstAdressList.add("경기");
        firstAdressList.add("인천");
        firstAdressList.add("부산");
        firstAdressList.add("광주");
        firstAdressList.add("세종");
        firstAdressList.add("대전");
        firstAdressList.add("강원");
        firstAdressList.add("충북");
        firstAdressList.add("충남");
        firstAdressList.add("전남");
        firstAdressList.add("전북");
        firstAdressList.add("경남");
        firstAdressList.add("경북");
        firstAdressList.add("제주");
    }

    private void setDbAdress(String adress) {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        databaseHandler.adressinsert(adress);
    }

    private void setDeleteAdress() {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        databaseHandler.adressdelete();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDeleteAdress();
    }

    private void setAdressList(String s) {
        onlyAdressList.clear();
        adressList.clear();
        onlyAdressList.add("방 선택");
        onlyAdressList.add(s + "1");
        onlyAdressList.add(s + "2");
        onlyAdressList.add(s + "3");
        onlyAdressList.add(s + "4");
        onlyAdressList.add(s + "5");
        adressList.add("방 선택");
        adressList.add(s + "1");
        adressList.add(s + "2");
        adressList.add(s + "3");
        adressList.add(s + "4");
        adressList.add(s + "5");

        reference.getRef().child("adressRoom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < adressList.size(); i++) {
                    if (i > 0) {
                        int count = (int) snapshot.child(onlyAdressList.get(i)).getChildrenCount();
                        adressList.set(i, onlyAdressList.get(i) + " (" + count + "/30)");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}