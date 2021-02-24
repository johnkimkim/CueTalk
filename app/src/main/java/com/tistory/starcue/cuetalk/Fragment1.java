package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Fragment1 extends Fragment {

    public static Spinner spinner;
    String[] items = {"지역 선택", "서울", "경기", "인천"};

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
                    Intent intent = new Intent(getActivity(), AdressRoom.class);
                    intent.putExtra("adress", s);
                    startActivity(intent);
                } else if (i == 2) {
//                    Toast.makeText(getActivity(), "경기", Toast.LENGTH_LONG).show();
                    String s = "gyungi";
                    Intent intent = new Intent(getActivity(), AdressRoom.class);
                    intent.putExtra("adress", s);
                    startActivity(intent);
                } else if (i == 3) {
                    String s = "incheon";
//                    Toast.makeText(getActivity(), "인천", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), AdressRoom.class);
                    intent.putExtra("adress", s);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "nothing", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

}