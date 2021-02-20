package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FLogin extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private Button btn;
    private ProgressBar progressBar;

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flogin);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (mCurrentUser == null) {
            sendUserToLogin();
        } else {

//            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//            Log.d("unique: ", id);

            setinit();
            setOnClickbtn();

            setFirebaseFirestore();
//            checkForLogin();
//            getdata();
            checkDevice();
        }


    }

    private void setinit() {
        btn = findViewById(R.id.floginbtn);
        progressBar = findViewById(R.id.flogin_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void setOnClickbtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                db = FirebaseFirestore.getInstance();
//
//                String user_uid = mAuth.getUid();
//
//                Map<String, Object> user = new HashMap<>();
//                user.put("uid", user_uid);
//                user.put("loginstatus", false);
//
//                db.collection("users").document(user_uid)
//                        .set(user)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//                mAuth.signOut();
//                sendUserToLogin();

            }
        });
    }

    private void setFirebaseFirestore() {

        //get unique
        databaseHandler.setDB(FLogin.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniquestring = cursor.getString(0);
        //get unique



//        String user_uid = mAuth.getUid();
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("uid", user_uid);
//        user.put("unique", uniquestring);
//
//        db.collection("users").document(user_uid)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
    }

    private void getdata() {
        String user_uid = mAuth.getUid();
        DocumentReference documentReference = db.collection("users")
                .document(user_uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        boolean loginstatus = documentSnapshot.getBoolean("loginstatus");
                        if (loginstatus) {
                            //다른기기 로그아웃 다이얼로그
                            asklogin();
                            Log.d("loginstatus", "true");
                        } else {
                            //다른기기없음
                            checkSql();
                            Log.d("loginstatus", "false");
                        }
                    } else {
                        Log.d("getdata document: ", "null");
                    }
                } else {
                    Log.d("getdata: ", "false, ", task.getException());
                }
            }
        });
    }

    private void checkDevice() {
        databaseHandler.setDB(FLogin.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniquestring = cursor.getString(0);
        Log.d(">>>uniquestring", uniquestring);

        String user_uid = mAuth.getUid();
        DocumentReference documentReference = db.collection("users")
                .document(user_uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String unique = documentSnapshot.getString("unique");
                        Log.d(">>>unique", unique);
                        if (unique.equals(uniquestring)) {
                            Log.d(">>>", "same");
                            checkSql();
                        } else {
                            Log.d(">>>", "not same");
                            asklogin();
                        }
                    } else {
                        ifNullDocument();
                        Log.d("getdata document: ", "null");
                    }
                } else {
                    Log.d("getdata: ", "false, ", task.getException());
                }
            }
        });
    }

    private void checkForLogin() {
        String user_uid = mAuth.getUid();
        DocumentReference documentReference = db.collection("users")
                .document(user_uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {

                        Toast.makeText(FLogin.this, "exists", Toast.LENGTH_SHORT).show();//store에 uid 있음
                    } else {

                        Toast.makeText(FLogin.this, "exists else", Toast.LENGTH_SHORT).show();//store에 uid 없음
                    }
                } else {
                    Toast.makeText(FLogin.this, "get failed with", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            sendUserToLogin();
        }
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(FLogin.this, PhoneNumber.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void asklogin() {
        startActivity(new Intent(FLogin.this, AskLogin.class));
    }

    private void gotohome() {
        startActivity(new Intent(FLogin.this, LoginActivity.class));
        finish();
    }

    private void ifNullDocument() {

        //get unique
        databaseHandler.setDB(FLogin.this);
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

        db.collection("users").document(user_uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkSql();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkSql() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from id", null);
        int i = cursor.getCount();
        if (i == 0) {
            String test = Integer.toString(i);
            Log.d("splash<<<: ", test);
            Intent intent = new Intent(FLogin.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            String test = Integer.toString(i);
            Log.d("splash<<<: ", test);
            Intent intent = new Intent(FLogin.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
