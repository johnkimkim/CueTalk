package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.fragment.Fragment1;
import com.tistory.starcue.cuetalk.fragment.Fragment2;
import com.tistory.starcue.cuetalk.fragment.Fragment3;
import com.tistory.starcue.cuetalk.fragment.Fragment4;
import com.tistory.starcue.cuetalk.fragment.Fragment5;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GpsTracker gpsTracker;

    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;


    RelativeLayout btn1, btn2, btn3, btn4, btn5;
    ImageView btn1img, btn2img, btn3img, btn4img, btn5img;
    public static TextView btn4count;
    public static TextView btn5count;
    public static RelativeLayout loading;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    DatabaseReference reference;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setdb();
        setinit();
        setViewPager();

        updateGps();

//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull @NotNull Task<String> task) {
//                String token = task.getResult();
//                Log.d("MainActivity>>>", "my token: " + token);
//            }
//        });
    }

    //init
    private void setinit() {
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn4count = findViewById(R.id.btn4count);
//        btn4count.bringToFront();
        btn5 = findViewById(R.id.btn5);
        btn5count = findViewById(R.id.btn5count);
        loading = findViewById(R.id.main_load);
        loading.bringToFront();
        btn1img = findViewById(R.id.btn1img);
        btn2img = findViewById(R.id.btn2img);
        btn3img = findViewById(R.id.btn3img);
        btn4img = findViewById(R.id.btn4img);
        btn5img = findViewById(R.id.btn5img);

        btn1.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(0, false);
        });
        btn2.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(1, false);
        });
        btn3.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(2, false);
        });
        btn4.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(3, false);
        });
        btn5.setOnClickListener(view -> {
            viewPager.setCurrentItem(4, false);
        });

    }

    //set ViewPager
    private void setViewPager() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Fragment1 fragment1 = new Fragment1();
        sectionsPagerAdapter.addItem(fragment1);
        Fragment2 fragment2 = new Fragment2();
        sectionsPagerAdapter.addItem(fragment2);
        Fragment3 fragment3 = new Fragment3();
        sectionsPagerAdapter.addItem(fragment3);
        Fragment4 fragment4 = new Fragment4();
        sectionsPagerAdapter.addItem(fragment4);
        Fragment5 fragment5 = new Fragment5();
        sectionsPagerAdapter.addItem(fragment5);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.d("MainActivity>>>", "position: " + i);
                if (i == 0) {
                    btn1img.setImageResource(R.drawable.btn1selected);
                    btn2img.setImageResource(R.drawable.btn2defult);
                    btn3img.setImageResource(R.drawable.btn3defult);
                    btn4img.setImageResource(R.drawable.btn4defult);
                    btn5img.setImageResource(R.drawable.btn5defult);
                } else if (i == 1) {
                    btn1img.setImageResource(R.drawable.btn1defult);
                    btn2img.setImageResource(R.drawable.btn2selected);
                    btn3img.setImageResource(R.drawable.btn3defult);
                    btn4img.setImageResource(R.drawable.btn4defult);
                    btn5img.setImageResource(R.drawable.btn5defult);
                } else if (i == 2) {
                    btn1img.setImageResource(R.drawable.btn1defult);
                    btn2img.setImageResource(R.drawable.btn2defult);
                    btn3img.setImageResource(R.drawable.btn3selected);
                    btn4img.setImageResource(R.drawable.btn4defult);
                    btn5img.setImageResource(R.drawable.btn5defult);
                } else if (i == 3) {
                    btn1img.setImageResource(R.drawable.btn1defult);
                    btn2img.setImageResource(R.drawable.btn2defult);
                    btn3img.setImageResource(R.drawable.btn3defult);
                    btn4img.setImageResource(R.drawable.btn4selected);
                    btn5img.setImageResource(R.drawable.btn5defult);
                } else if (i == 4) {
                    btn1img.setImageResource(R.drawable.btn1defult);
                    btn2img.setImageResource(R.drawable.btn2defult);
                    btn3img.setImageResource(R.drawable.btn3defult);
                    btn4img.setImageResource(R.drawable.btn4defult);
                    btn5img.setImageResource(R.drawable.btn5selected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        int i = viewPager.getCurrentItem();
        if (i == 0) {
            btn1img.setImageResource(R.drawable.btn1selected);
            btn2img.setImageResource(R.drawable.btn2defult);
            btn3img.setImageResource(R.drawable.btn3defult);
            btn4img.setImageResource(R.drawable.btn4defult);
            btn5img.setImageResource(R.drawable.btn5defult);
        } else if (i == 1) {
            btn1img.setImageResource(R.drawable.btn1defult);
            btn2img.setImageResource(R.drawable.btn2selected);
            btn3img.setImageResource(R.drawable.btn3defult);
            btn4img.setImageResource(R.drawable.btn4defult);
            btn5img.setImageResource(R.drawable.btn5defult);
        } else if (i == 2) {
            btn1img.setImageResource(R.drawable.btn1defult);
            btn2img.setImageResource(R.drawable.btn2defult);
            btn3img.setImageResource(R.drawable.btn3selected);
            btn4img.setImageResource(R.drawable.btn4defult);
            btn5img.setImageResource(R.drawable.btn5defult);
        } else if (i == 3) {
            btn1img.setImageResource(R.drawable.btn1defult);
            btn2img.setImageResource(R.drawable.btn2defult);
            btn3img.setImageResource(R.drawable.btn3defult);
            btn4img.setImageResource(R.drawable.btn4selected);
            btn5img.setImageResource(R.drawable.btn5defult);
        } else if (i == 4) {
            btn1img.setImageResource(R.drawable.btn1defult);
            btn2img.setImageResource(R.drawable.btn2defult);
            btn3img.setImageResource(R.drawable.btn3defult);
            btn4img.setImageResource(R.drawable.btn4defult);
            btn5img.setImageResource(R.drawable.btn5selected);
        }

    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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

    public static class CustomViewPager extends ViewPager {

        private boolean isEnabled;

        public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.isEnabled = false;//viewpage 스크롤 여부
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (this.isEnabled) {
                return super.onTouchEvent(ev);
            }
            return false;
        }

        @Override
        public boolean onInterceptHoverEvent(MotionEvent event) {
            if (this.isEnabled) {
                return super.onInterceptHoverEvent(event);
            }
            return false;
        }

        public void setPagingEnabled(boolean isEnabled) {//control view pager swipe. if true can, if false can't
            this.isEnabled = isEnabled;
        }

//        @Override
//        protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {//f1 viewpager scroll lock
//            if (v != this && v instanceof ViewPager) {
//                return true;
//            }
//            return super.canScroll(v, checkV, dx, x, y);
//        }

        @Override
        protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {//f1 viewpager scroll lock
            return true;
        }

    }

    private void updateGps() {
        gpsTracker = new GpsTracker(MainActivity.this);
        gpsTracker.updateFirestoreGps(db, myUid);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setdb() {
        databaseHandler.setDB(MainActivity.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        myUid = mAuth.getUid();
//        databaseHandler.adressdelete();
    }

//    private void setNameSql() {
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from nameTable", null);
//        int count = cursor.getCount();
//        if (count == 0) {
//            db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    String name = documentSnapshot.get("name").toString();
//                    databaseHandler.insertName(name);
//                }
//            });
//        }
//    }
}