package com.tistory.starcue.cuetalk;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tistory.starcue.cuetalk.Fragment1.spinner;

public class F1intent extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    ListView listView;
    String name, sex, age;
    F1intenrAdapter f1intenrAdapter;
    private ArrayList<AdressRoomItem> adressRoomItems = new ArrayList<>();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    Map<String, Object> map = new HashMap<String, Object>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1intent);

        setinit();
        setdb();
        setlistview();
        changedatasetlsitview();
        listviewOnClick();

    }

    private void setinit() {
        listView = findViewById(R.id.f1listview);
    }

    private void setdb() {
        //set firebase
        FirebaseApp.initializeApp(F1intent.this);
        //set db
        databaseHandler.setDB(F1intent.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        //get name
        Cursor cursor = sqLiteDatabase.rawQuery("select * from id where _rowid_ = 1", null);
        cursor.moveToFirst();
        name = cursor.getString(0);
        sex = cursor.getString(1);
        age = cursor.getString(2);
        cursor.close();

        addAdressRoomList(name, sex, age);
    }

    private void setlistview() {
        F1intenrAdapter f1intenrAdapter = new F1intenrAdapter(F1intent.this, adressRoomItems);
        listView.setAdapter(f1intenrAdapter);
        f1intenrAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        spinner.setSelection(0);
    }

    //특정 경로의 전체 내용에 대한 변경 사항을 읽고 수진 대기함
    //onDataChange는 Database가 변경되었을때 호출되고
    //onCancelled는 취소되었을때 호출됨
    private void changedatasetlsitview() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = getIntent().getStringExtra("adress");

                adressRoomItems.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String test = snapshot1.child(s).child(name).getKey();
                    Log.d("<<<1", test);
                    AdressRoomItem adressRoomItem = new AdressRoomItem();
                    AdressRoomItem data = snapshot1.child(s).getValue(AdressRoomItem.class);//child 설정 고치기

                    ArrayList<String> namelist = new ArrayList<>();
                    String getname = snapshot1.child(s).getValue(String.class);
                    namelist.add(getname);

                    adressRoomItem.setName(snapshot1.child(s).getValue(AdressRoomItem.class).getName());
//                    adressRoomItem.setSex(snapshot1.child(s).getValue(AdressRoomItem.class).getSex());
//                    adressRoomItem.setAge(snapshot1.child(s).getValue(AdressRoomItem.class).getAge());
//                    adressRoomItem.setAge(snapshot1.child(s).getValue(AdressRoomItem.class).getUid());
//                    adressRoomItem.setAge(snapshot1.child(s).getValue(AdressRoomItem.class).getAge());
                    adressRoomItems.add(adressRoomItem);

                }

                f1intenrAdapter = new F1intenrAdapter(F1intent.this, adressRoomItems);
                listView.setAdapter(f1intenrAdapter);
                f1intenrAdapter.notifyDataSetChanged();

////                adressRoomLists.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    AdressRoomList data = ds.child("adresslist").child(s).child(name).getValue(AdressRoomList.class);
//                    adressRoomLists.add(data);
//
////                    String as = adressRoomLists.get(0).getName();
////                    String se = adressRoomLists.get(0).getSex();
////                    String ag = adressRoomLists.get(0).getAge();
////                    Log.d("<<<", as + " " + se + " " + ag);
//                }
//
//                f1intenrAdapter = new F1intenrAdapter(F1intent.this, adressRoomLists);
//                listView.setAdapter(f1intenrAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //리스트뷰의 채팅방을 클릭했을 때 반응
    //채팅방의 이름과 입장하는 유저의 이름을 전달
    private void listviewOnClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private void addAdressRoomList(String name, String sex, String age) {
        String s = getIntent().getStringExtra("adress");
        reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/adresslist/" + s + "/" + name + "/" + "/name/", name);
        childUpdates.put("/adresslist/" + s + "/" + name + "/" +  "/sex", sex);
        childUpdates.put("/adresslist/" + s + "/" + name + "/" +  "/age/", age);
        reference.updateChildren(childUpdates);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("<<<", "pause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        String s = getIntent().getStringExtra("adress");
        reference.getRef().child("adresslist").child(s).child(name).removeValue();
        onBackPressed();
        Log.d("<<<", "stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("<<<", "destory");
    }
}
