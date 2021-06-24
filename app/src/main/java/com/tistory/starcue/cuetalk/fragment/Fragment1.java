package com.tistory.starcue.cuetalk.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Fragment1 extends Fragment {

    Context context;

    RelativeLayout viewpagerlayout;
    Button viewpagerbtn;
    ViewPager viewPager;
    F1SectionsPagerAdapter f1SectionsPagerAdapter;
    CountDownTimer countDownTimer;
    boolean isRunning = true;

    public static Button btn1, btn2;
    AlertDialog alertDialog1, alertDialog2;
    GridView btn1grid;
    ListView btn2listview;
    EditText edit;
    TextView editcount;
    GridListAdapter adapter1;
    Btn2ListAdapter adapter2;
    ArrayList<String> btn1list = new ArrayList<>();
    List<String> firstAdressList = new ArrayList<>();
    List<String> adressList = new ArrayList<>();
    List<String> onlyAdressList = new ArrayList<>();

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseReference reference;

    private FirebaseFirestore db;

    FirebaseAuth mAuth;
    String myUid;
    String isnull;

    int pageInt;

    private GestureDetector detector;

    private WormDotsIndicator wormDotsIndicator;

    public Fragment1() {
        // Required empty public constructor
    }

    private void testclass(ViewGroup viewGroup) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String pn = mUser.getPhoneNumber().substring(9, 13);
        String myUi = mAuth.getUid();
        Log.d("Fragment1>>>", "testtest: " + pn);

        Button testbtn1 = viewGroup.findViewById(R.id.testbtn1);
        Button testbtn2 = viewGroup.findViewById(R.id.testbtn2);
        testbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject root = new JSONObject();
//                                        JSONObject notification = new JSONObject();
                            JSONObject data = new JSONObject();
//                                        notification.put("body", messege);
//                                        notification.put("title", getString(R.string.app_name));
                            data.put("messege", "신고로 인해 ");
                            data.put("name", "큐톡");
                            data.put("pic", "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullUser.png?alt=media&token=4c9daa69-6d03-4b19-a793-873f5739f3a1");
                            data.put("uid", myUid);
                            Log.d("Fragment4ChatRoom>>>", "운영자");
//                                        root.put("notification", notification);
                            root.put("data", data);
                            root.put("to", "cAkBWY-USIuu-F_plxqD0g:APA91bHoq0y6TWzsefKjT3PP-hzA_JDv4m_EV5KMsSU4mGxkbcPCg17aks3Pd9PkIaJ1HCGL-iiMkwe87vBKkkvmjP8Jv_dQR7b6HleVhPJiev9XAyGs7XgFZ0dUiDMclNXRpUKfy9C6");

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
            }
        });
        testbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        Log.d("Fragment1>>>", "start onCreate");
        setViewPager(rootView);

        edit = rootView.findViewById(R.id.f1_edittext);
        editcount = rootView.findViewById(R.id.f1_edit_count);
        btn1 = rootView.findViewById(R.id.f1btn1);
        btn2 = rootView.findViewById(R.id.f1btn2);
        btn1.setText("지역 선택");
        btn2.setText("방 선택");
        btn2.setEnabled(false);

        setEditCount();
        btn1OnClick();
        btn2OnClick();

        testclass(rootView);

        return rootView;
    }

    private void setEditCount() {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = edit.getText().toString();
                editcount.setText(input.length() + " / 10");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit.setOnKeyListener(new View.OnKeyListener() {//줄바꿈 금지
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == keyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
    }

    private void setEditText() {
        db.collection("users").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("f1edit") == null) {
                        edit.setText("");
                    } else {
                        edit.setText(snapshot.get("f1edit").toString());
                    }
                }
            }
        });
    }

    private void btn1OnClick() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                btn1dialog();
            }
        });
    }

    private void btn2OnClick() {
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                btn2dialog();
            }
        });
    }

    private void btn1dialog() {
        btn1list.clear();
        btn1list.add("서울");
        btn1list.add("경기");
        btn1list.add("인천");
        btn1list.add("부산");
        btn1list.add("광주");
        btn1list.add("세종");
        btn1list.add("대전");
        btn1list.add("강원");
        btn1list.add("충북");
        btn1list.add("충남");
        btn1list.add("전남");
        btn1list.add("전북");
        btn1list.add("경남");
        btn1list.add("경북");
        btn1list.add("제주");
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.f1btn1dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        alertDialog1 = builder.create();

        //set size
        WindowManager.LayoutParams layoutParams = alertDialog1.getWindow().getAttributes();
        layoutParams.copyFrom(alertDialog1.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog1.getWindow().setAttributes(layoutParams);
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//layout backgroud drawable 적용시키기

        alertDialog1.show();

        btn1grid = layout.findViewById(R.id.f1btn1dialog_gridview);
        adapter1 = new GridListAdapter();
        btn1grid.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
    }

    private void btn2dialog() {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.f1btn2dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        alertDialog2 = builder.create();

        //set size
        WindowManager.LayoutParams layoutParams = alertDialog2.getWindow().getAttributes();
        layoutParams.copyFrom(alertDialog2.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog2.getWindow().setAttributes(layoutParams);
        alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog2.show();

        btn2listview = layout.findViewById(R.id.f1btn2dialog_listview);

        adapter2 = new Btn2ListAdapter();
        btn2listview.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

        //btn2 count
        reference.getRef().child("adressRoom").child(btn1.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < adressList.size(); i++) {
                    int count = (int) snapshot.child(onlyAdressList.get(i)).getChildrenCount();
                    adressList.set(i, onlyAdressList.get(i) + " (" + count + "/30)");
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        setEditText();
        databaseHandler.roomnamedelete();
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
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class GridListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return btn1list.size();
        }

        @Override
        public Object getItem(int i) {
            return btn1list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            context = viewGroup.getContext();
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.f1btn1dialog_item, viewGroup, false);
            }

            Button btn = view.findViewById(R.id.f1btn1dialog_textview);

            btn.setText(btn1list.get(i));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onlyAdressList.clear();
                    adressList.clear();
                    onlyAdressList.add(btn1list.get(i) + "1");
                    onlyAdressList.add(btn1list.get(i) + "2");
                    onlyAdressList.add(btn1list.get(i) + "3");
                    onlyAdressList.add(btn1list.get(i) + "4");
                    onlyAdressList.add(btn1list.get(i) + "5");
                    adressList.add(btn1list.get(i) + "1");
                    adressList.add(btn1list.get(i) + "2");
                    adressList.add(btn1list.get(i) + "3");
                    adressList.add(btn1list.get(i) + "4");
                    adressList.add(btn1list.get(i) + "5");
                    btn1.setText(btn1list.get(i));
                    if (!btn1.getText().toString().equals("지역 선택")) {
                        btn2.setEnabled(true);
                    }

                    alertDialog1.dismiss();
                }
            });
            return view;
        }
    }

    public class Btn2ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return adressList.size();
        }

        @Override
        public Object getItem(int i) {
            return adressList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            context = viewGroup.getContext();
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.f1btn2dialog_item, viewGroup, false);
            }

            Button btn = view.findViewById(R.id.f1btn2dialog_btn);

            btn.setText(adressList.get(i));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog2.dismiss();
                    MainActivity.loading.setVisibility(View.VISIBLE);
                    reference.getRef().child("adressRoom").child(btn1.getText().toString()).child(onlyAdressList.get(i)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (edit.getText().toString().length() != 0) {
                                if (dataSnapshot.getChildrenCount() < 30) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("f1edit", edit.getText().toString());
                                    db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            setDbAdress(onlyAdressList.get(i));
                                            Intent intent = new Intent(getActivity(), AdressRoom.class);
                                            startActivity(intent);
                                            MainActivity.loading.setVisibility(View.GONE);
                                            btn1.setText("지역 선택");
                                            btn2.setText("방 선택");
                                            btn2.setEnabled(false);
                                        }
                                    });
                                } else {
                                    MainActivity.loading.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "대화방 인원수가 꽉 찼습니다", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                MainActivity.loading.setVisibility(View.GONE);
                                edit.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                Toast.makeText(getActivity(), "인사말을 입력해주세요", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            return view;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        btn1.setText("지역 선택");
        btn2.setText("방 선택");
        if (btn2.isEnabled()) {
            btn2.setEnabled(false);
        }
        onlyAdressList.clear();
        adressList.clear();
    }
}