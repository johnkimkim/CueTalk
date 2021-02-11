package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editid;
    private Button loginbtn;
    private String[] age;
    NumberPicker picker;
    RadioButton radiomale, radiofemale;
    String agestring;
    String sexstring;
    RadioGroup radioGroup;
    Spinner agespin;

    String[] items = {"나이", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70"};

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        databaseHandler.setDB(LoginActivity.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        setinit();
        setagespin();
        setLoginbtn();
    }

    private void setinit() {
        editid = findViewById(R.id.editid);
        loginbtn = findViewById(R.id.loginbtn);
        radiomale = findViewById(R.id.sexmale);
        radiofemale = findViewById(R.id.sexfemale);
        radioGroup = findViewById(R.id.radiogroup);
        agespin = findViewById(R.id.agespin);
    }

    private void setLoginbtn() {
        loginbtn.setOnClickListener(view -> {

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
                Toast.makeText(LoginActivity.this, "name", Toast.LENGTH_SHORT).show();
            } else if (i == 0) {
                Toast.makeText(LoginActivity.this, "age", Toast.LENGTH_SHORT).show();
            } else if (sexstring.equals("")) {
                Toast.makeText(LoginActivity.this, "sex", Toast.LENGTH_SHORT).show();
            } else {
                databaseHandler.dbinsert(name, sexstring, agestring);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setagespin() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.select_dialog_item, items);
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

}
