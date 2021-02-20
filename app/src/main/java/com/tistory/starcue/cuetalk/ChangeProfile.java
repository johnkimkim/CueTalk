package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChangeProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;

    private EditText editid;
    private Button yesbtn, nobtn;
    private String[] age;
    NumberPicker picker;
    RadioButton radiomale, radiofemale;
    String agestring;
    String sexstring;
    RadioGroup radioGroup;
    Spinner agespin;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;

    String[] items = {"나이", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70"};

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        databaseHandler.setDB(ChangeProfile.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        setinit();
        setagespin();
        setYesBtn();
        setNoBtn();

    }

    private void setinit() {
        editid = findViewById(R.id.change_profile_editid);
        yesbtn = findViewById(R.id.change_profile_yesbtn);
        nobtn = findViewById(R.id.change_profile_nobtn);
        radiomale = findViewById(R.id.change_profile_sexmale);
        radiofemale = findViewById(R.id.change_profile_sexfemale);
        radioGroup = findViewById(R.id.change_profile_radiogroup);
        agespin = findViewById(R.id.change_profile_agespin);
        relativeLayout = findViewById(R.id.change_profile_progresslayout);
        progressBar = findViewById(R.id.change_profile_progress_bar);

        relativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setYesBtn() {
        yesbtn.setOnClickListener(view -> {

            if (radiomale.isChecked()) {
                sexstring = "남자";
            } else if (radiofemale.isChecked()) {
                sexstring = "여자";
            } else {
                sexstring = "";
            }

            String name = editid.getText().toString();

            int i = Integer.parseInt(agestring);

            if (name.equals("")) {
                Toast.makeText(ChangeProfile.this, "name", Toast.LENGTH_SHORT).show();
            } else if (i == 0) {
                Toast.makeText(ChangeProfile.this, "age", Toast.LENGTH_SHORT).show();
            } else if (sexstring.equals("")) {
                Toast.makeText(ChangeProfile.this, "sex", Toast.LENGTH_SHORT).show();
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                databaseHandler.dbdelete();
                databaseHandler.dbinsert(name, sexstring, agestring);

                updateUser(name, sexstring, agestring);
            }

//            mAuth.signOut();

        });
    }

    private void setNoBtn() {
        nobtn.setOnClickListener(view -> onBackPressed());
    }

    private void setagespin() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChangeProfile.this, android.R.layout.select_dialog_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        agespin.setAdapter(arrayAdapter);

        agespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    agestring = "0";
                } else {
                    agestring = Integer.toString(i + 19);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateUser(String name, String sex, String age) {
        databaseHandler.setDB(ChangeProfile.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniquestring = cursor.getString(0);
        //get unique

        String user_uid = mAuth.getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("uid", user_uid);
        user.put("unique", uniquestring);
        user.put("name", name);
        user.put("sex", sex);
        user.put("age", age);

        db.collection("users").document(user_uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        goToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        relativeLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangeProfile.this, "네트워크문제로 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(ChangeProfile.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(ChangeProfile.this, "ok", Toast.LENGTH_SHORT).show();
        finish();
    }
}
