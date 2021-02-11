package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Fragment5 extends Fragment {

    TextView name, age, sex;
    Button reset;
    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    public Fragment5() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment5, container, false);

        name = rootView.findViewById(R.id.profilename);
        age = rootView.findViewById(R.id.profileage);
        sex = rootView.findViewById(R.id.profilesex);
        reset = rootView.findViewById(R.id.resetprofile);

        setdb();
        reset_profile();

        return rootView;
    }

    private void setdb() {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from id where _rowid_ = 1", null);
        cursor.moveToFirst();
        name.setText(cursor.getString(0));
        age.setText(cursor.getString(1));
        sex.setText(cursor.getString(2));
        cursor.close();
    }

    private void reset_profile() {
        reset.setOnClickListener(view -> {
            databaseHandler.setDB(getActivity());
            databaseHandler = new DatabaseHandler(getActivity());
            sqLiteDatabase = databaseHandler.getWritableDatabase();
            databaseHandler.dbdelete();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
    }

}