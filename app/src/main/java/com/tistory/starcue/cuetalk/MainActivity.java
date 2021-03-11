package com.tistory.starcue.cuetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;


    Button btn1, btn2, btn3, btn4, btn5;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setdb();
        setViewPager();
        setinit();

    }

    //init
    private void setinit() {
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);

        btn1.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(0);
        });
        btn2.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(1);
        });
        btn3.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(2);
        });
        btn4.setOnClickListener((View v) -> {
            viewPager.setCurrentItem(3);
        });
        btn5.setOnClickListener(view -> {
            viewPager.setCurrentItem(4);
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
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationX(page.getWidth() * -position);
                if (position <= -1.0F || position >= 1.0F) {
                    page.setAlpha(0.0F);
                } else if (position == 0.0F) {
                    page.setAlpha(1.0F);
                } else {
                    page.setAlpha(1.0F - Math.abs(position));
                }

            }
        });

//        int i = viewPager.getCurrentItem();
//        if (i == 0) {
//            btn1.setBackgroundColor(getResources().getColor(R.color.gray));
//            btn2.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn3.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn4.setBackgroundColor(getResources().getColor(R.color.purple_500));
//        } else if (i == 1) {
//            btn2.setBackgroundColor(getResources().getColor(R.color.gray));
//            btn1.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn3.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn4.setBackgroundColor(getResources().getColor(R.color.purple_500));
//        } else if (i == 2) {
//            btn3.setBackgroundColor(getResources().getColor(R.color.gray));
//            btn1.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn2.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn4.setBackgroundColor(getResources().getColor(R.color.purple_500));
//        } else if (i == 3) {
//            btn4.setBackgroundColor(getResources().getColor(R.color.gray));
//            btn1.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn2.setBackgroundColor(getResources().getColor(R.color.purple_500));
//            btn3.setBackgroundColor(getResources().getColor(R.color.purple_500));
//        }
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public SectionsPagerAdapter(FragmentManager fm) {
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
        myUid = mAuth.getUid();

        setNameSql();
    }

    private void setNameSql() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from nameTable", null);
        int count = cursor.getCount();
        if (count == 0) {
            db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.get("name").toString();
                    databaseHandler.insertName(name);
                }
            });
        }
    }
}