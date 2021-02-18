package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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

public class Splash extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    SharedPreferences preferences;
    boolean firstLogin;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

//        setDB();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        preferences = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        firstLogin = preferences.getBoolean("checkFirst", false);

        checkfirst();

    }

    private void setDB() {
        databaseHandler.setDB(Splash.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

//        new Handler().postDelayed(() -> {
//            Cursor cursor = sqLiteDatabase.rawQuery("select * from id", null);
//            int i = cursor.getCount();
//            if (i == 0) {
//                String test = Integer.toString(i);
//                Log.d("splash<<<: ", test);
//                Intent intent = new Intent(Splash.this, LoginActivity.class);
//                startActivity(intent);
//            } else {
//                String test = Integer.toString(i);
//                Log.d("splash<<<: ", test);
//                Intent intent = new Intent(Splash.this, MainActivity.class);
//                startActivity(intent);
//            }
//            cursor.close();
//        }, 1200);

    }

    private void checkfirst() {
        if (!firstLogin) { //최초실행
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(">>>", "first");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("checkFirst", true);
                    editor.apply();

                    String unique = UUID.randomUUID().toString();

                    databaseHandler.setDB(Splash.this);
                    databaseHandler = new DatabaseHandler(Splash.this);
                    sqLiteDatabase = databaseHandler.getWritableDatabase();
                    databaseHandler.insertUnique(unique);

                    Log.d(">>>", unique);

                    checkUser();
                }
            }, 500);
        } else { //최초실행아님
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUser();
                    Log.d(">>>", "not first");
                }
            }, 500);
        }
    }

    private void checkUser() {
        if (mCurrentUser == null) {
            goToPhoneNumber();
        } else {
            checkDevice();
        }
    }

    private void goToFLoing() {
        startActivity(new Intent(Splash.this, FLogin.class));
        finish();
    }

    private void goToPhoneNumber() {
        startActivity(new Intent(Splash.this, PhoneNumber.class));
        finish();
    }

    private void checkDevice() {
        databaseHandler.setDB(Splash.this);
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
                            goToFLoing();
                        } else {
                            Log.d(">>>", "not same");
                            //다른기기에서 로그인해서 로그아웃됨.
                            mAuth.signOut();
                            goToPhoneNumber();
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

    private void ifNullDocument() {

        //get unique
        databaseHandler.setDB(Splash.this);
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
                        goToFLoing();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
