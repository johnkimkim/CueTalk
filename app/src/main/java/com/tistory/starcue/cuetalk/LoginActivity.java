package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    private EditText editid;
    private Button loginbtn;
    private String[] age;
    NumberPicker picker;
    RadioButton radiomale, radiofemale;
    String agestring;
    String sexstring;
    RadioGroup radioGroup;
    Spinner agespin;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    ImageView pic;
    Button addpic;
    Uri imageUri;

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

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
        relativeLayout = findViewById(R.id.progresslayout);
        progressBar = findViewById(R.id.progress_bar);
        pic = findViewById(R.id.login_profile_image);
        addpic = findViewById(R.id.login_add_image);

        relativeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooosePic();
            }
        });
    }

    private void chooosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            pic.setImageURI(imageUri);
        }
    }

    private void uploadPic() {

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setTitle("이미지 업로드 중...");
        pd.show();

        String uid = mAuth.getUid();
        StorageReference riversRef = storageReference.child("images/" + uid);
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();
                        goToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "업로드실패", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Pro: " + (int) progressPercent + "%");
                    }
                });
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
                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                databaseHandler.dbinsert(name, sexstring, agestring);

                updateUser(name, sexstring, agestring);
                if (imageUri != null) {
                    uploadPic();
                } else {
                    goToMain();
                }

            }

//            mAuth.signOut();

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

    private void updateUser(String name, String sex, String age) {
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

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        relativeLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "네트워크문제로 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
        finish();
    }

}
