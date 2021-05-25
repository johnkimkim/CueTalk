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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment1 extends Fragment {

    RelativeLayout viewpagerlayout;
    Button viewpagerbtn;
    ViewPager viewPager;
    F1SectionsPagerAdapter f1SectionsPagerAdapter;
    CountDownTimer countDownTimer;
    boolean isRunning = true;

    public static Spinner spinner;
    ArrayAdapter<String> adapter;
    String[] items = {"지역 선택", "서울1", "서울2", "서울3", "경기1", "인천1"};
    List<String> finalAdressList = new ArrayList<>();
    List<String> adressList = new ArrayList<>();

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
        String myUid = mAuth.getUid();

        Button testbtn = viewGroup.findViewById(R.id.testbtn);
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("f2messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.get("uid") == null) {
                                Log.d("Fragment1>>>", "testtest null");
                            } else {
                                Log.d("Fragment1>>>", "testtest has");
                            }
                        }
                    }
                });
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        db = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();
        Log.d("Fragment1>>>", "start onCreate");
        setAdressList();
        setViewPager(rootView);

        spinner = rootView.findViewById(R.id.f1spinner);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, adressList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = finalAdressList.get(i);
                if (!s.equals("지역을 선택하세요")) {
                    MainActivity.loading.setVisibility(View.VISIBLE);
                    reference.getRef().child("adressRoom").child(s).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() < 30) {
                                setDbAdress(s);
                                Intent intent = new Intent(getActivity(), AdressRoom.class);
//                                intent.putExtra("adress", s);
                                startActivity(intent);
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

    private void setAdressList() {
        finalAdressList.add("지역을 선택하세요");
        finalAdressList.add("서울1");
        finalAdressList.add("서울2");
        finalAdressList.add("서울3");
        finalAdressList.add("경기1");
        finalAdressList.add("경기2");
        finalAdressList.add("인천1");
        finalAdressList.add("인천2");
        adressList.add("지역을 선택하세요");
        adressList.add("서울1");
        adressList.add("서울2");
        adressList.add("서울3");
        adressList.add("경기1");
        adressList.add("경기2");
        adressList.add("인천1");
        adressList.add("인천2");

        reference.getRef().child("adressRoom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < adressList.size(); i++) {
                    if (i > 0) {
                        int count = (int) snapshot.child(finalAdressList.get(i)).getChildrenCount();
                        adressList.set(finalAdressList.indexOf(finalAdressList.get(i)), finalAdressList.get(i) + " (" + count + "/30)");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
}