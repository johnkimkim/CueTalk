package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

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

        return rootView;
    }

    private void setdb(String adress) {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        databaseHandler.adressinsert(adress);
    }

}