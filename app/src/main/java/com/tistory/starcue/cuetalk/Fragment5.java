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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragment5 extends Fragment {

    TextView name, age, sex;
    Button reset, logout;
    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;

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
        logout = rootView.findViewById(R.id.logout_btn);

        setFirebse();
        setdb();
        reset_profile();
        logoutBtn();

        return rootView;
    }

    private void setFirebse() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
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
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangeProfile.class);
                startActivity(intent);
            }
        });
    }

    private void logoutBtn() {
        logout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), PhoneNumber.class));
        });
    }

}