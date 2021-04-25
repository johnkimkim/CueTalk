package com.tistory.starcue.cuetalk.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.tistory.starcue.cuetalk.R;

import java.util.HashMap;
import java.util.Map;

public class Fragment1 extends Fragment {

    public static Spinner spinner;
    String[] items = {"지역 선택", "서울", "경기", "인천"};

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

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
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                firestore.collection("blacklist").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (DocumentSnapshot queryDocumentSnapshots1 : queryDocumentSnapshots.getDocuments()) {
//                            Log.d("PhoneNumber>>>", "get black list: " + queryDocumentSnapshots1.getId());
//                        }
//                    }
//                });
                firestore.collection("blacklist").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot value1 : value.getDocuments()) {
                            Log.d("Fragment1>>>", "get black list: " + value1.getId());
                            Log.d("Fragment1>>>", "get black list size: " + value.size());

                        }
                    }
                });

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        Log.d("Fragment1>>>", "start onCreate");

        spinner = rootView.findViewById(R.id.f1spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {

                } else if (i == 1) {
//                    Toast.makeText(getActivity(), "서울", Toast.LENGTH_LONG).show();
                    String s = "seoul";
                    setdb(s);
                    Intent intent = new Intent(getActivity(), AdressRoom.class);
                    startActivity(intent);
                } else if (i == 2) {
//                    Toast.makeText(getActivity(), "경기", Toast.LENGTH_LONG).show();
                    String s = "gyungi";
                    setdb(s);
                    Intent intent = new Intent(getActivity(), AdressRoom.class);
                    startActivity(intent);
                } else if (i == 3) {
                    String s = "incheon";
                    setdb(s);
//                    Toast.makeText(getActivity(), "인천", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), AdressRoom.class);
                    startActivity(intent);
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

    private void setdb(String adress) {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        databaseHandler.adressinsert(adress);
    }

}