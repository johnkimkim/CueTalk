package com.tistory.starcue.cuetalk;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class F1Chat extends AppCompatActivity {

    private Button btn;
    private EditText et_msg;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private String str_name;
    private String str_mag;
    private String chat_user;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("message");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1chat);

        FirebaseApp.initializeApp(F1Chat.this);

        listView = findViewById(R.id.f1chatlistview);
        btn = findViewById(R.id.f1sendbutton);
        et_msg = findViewById(R.id.f1edittext);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        //listview 갱신 시 자동 하단스크롤
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        str_name = "Guest " + new Random().nextInt(1000);

        btn.setOnClickListener(view -> {
            //map을 사용해 name과 메시지를 가져오고, key에 값 요청
            Map<String, Object> map = new HashMap<String, Object>();

            //key로 데이터베이스 오픈
            String key = reference.push().getKey();
            reference.updateChildren(map);//채팅방 만들기

            DatabaseReference dbRef = reference.child(key);
            Map<String, Object> objectMap = new HashMap<String, Object>();
            objectMap.put("str_name", str_name);
            objectMap.put("text", et_msg.getText().toString());
            dbRef.updateChildren(objectMap);//메시지업데이트
            et_msg.setText("");
        });

        /*
        addChildEventListener는 Child에서 일어나는 변화를 감지
        - onChildAdded() = 리스트의 아이템을 검색하거나 추가가 있을 때 수신
        - onChildChange() = 리스트의 아이템의 변화가 있을때 수진
        - onChildRemoved() = 리스트의 아이템이 삭제되었을때 수신
        - onChildMoved() = 리스트의 순서가 변경되었을때 수신
         */

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatListener(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chatListener(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            chat_user = (String) ((DataSnapshot) i.next()).getValue();
            str_mag = (String) ((DataSnapshot) i.next()).getValue();

            //유저이름, 메시지를 가져와서 array에 추가
            arrayAdapter.add(chat_user + " : " + str_mag);
        }

        //변경된값으로 리스트뷰 갱신
        arrayAdapter.notifyDataSetChanged();
    }
}
