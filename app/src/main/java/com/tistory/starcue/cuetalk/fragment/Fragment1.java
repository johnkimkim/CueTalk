package com.tistory.starcue.cuetalk.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.AdressRoom;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

    public Fragment1() {
        // Required empty public constructor
    }

    private void testclass(ViewGroup viewGroup) {
        String myUid;
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
        Button testbtn = viewGroup.findViewById(R.id.testbtn);
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("uid", myUid);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("deleteUser").document(myUid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        lastDeleteUser();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

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
                    reference.getRef().child("adressRoom").child(s).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() < 30) {
                                setdb(s);
                                Intent intent = new Intent(getActivity(), AdressRoom.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "대화방 인원수가 꽉 찼습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                spinner.setSelection(0);

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
        f1SectionsPagerAdapter = new F1SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        F1F1 f1f1 = new F1F1();
        f1SectionsPagerAdapter.addItem(f1f1);
        F1F2 f1f2 = new F1F2();
        f1SectionsPagerAdapter.addItem(f1f2);
        F1F3 f1f3 = new F1F3();
        f1SectionsPagerAdapter.addItem(f1f3);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        ViewGroup.LayoutParams params = viewpagerlayout.getLayoutParams();
        params.height = metrics.heightPixels / 3;

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(f1SectionsPagerAdapter);

        setViewPagerTimer();
    }

    private void setViewPagerTimer() {
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (viewPager.getCurrentItem() == 0) {
//                    viewPager.setCurrentItem(1);
//                } else if (viewPager.getCurrentItem() == 1) {
//                    viewPager.setCurrentItem(2);
//                } else if (viewPager.getCurrentItem() == 2) {
//                    viewPager.setCurrentItem(0);
//                }
//            }
//        };
//        timer.schedule(timerTask, 3000, 3000);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isRunning) {
                        countDownTimer = new CountDownTimer(10, 10) {
                            @Override
                            public void onTick(long l) {
                                if (viewPager.getCurrentItem() == 0) {
                                    viewPager.setCurrentItem(1);
                                } else if (viewPager.getCurrentItem() == 1) {
                                    viewPager.setCurrentItem(2);
                                } else if (viewPager.getCurrentItem() == 2) {
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
                    countDownTimer.cancel();
                    handler.removeCallbacksAndMessages(null);
                    viewpagerbtn.setBackgroundResource(R.drawable.play);
                } else {
                    isRunning = true;
                    setViewPagerTimer();
                    viewpagerbtn.setBackgroundResource(R.drawable.pause);
                }
            }
        });
    }


    public static class F1SectionsPagerAdapter extends FragmentPagerAdapter {
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
                        Log.d("Fragment1>>>", "check key: " + snapshot.child(adressList.get(i)).getKey() + " / " + snapshot.child(adressList.get(i)).getChildrenCount());
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

    private void setdb(String adress) {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        databaseHandler.adressinsert(adress);
    }

}