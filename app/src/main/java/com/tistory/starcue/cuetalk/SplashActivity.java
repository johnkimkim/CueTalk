package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;

    String myUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        setdb();
        checkPermission();


    }

    private void checkPermission() {
        //check permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //has permission

                checkHasUnique();
            } else {
                Intent intent = new Intent(SplashActivity.this, Access.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            checkHasUnique();
        }
    }

    private void checkCurrentUser() {
        if (mCurrentUser == null) {
            goToPhoneNumber();
            Log.d("SplashActivity>>>", "mCurrentUser == null");
        } else {
            Log.d("SplashActivity>>>", "mCurrentUser != null");
            checkSameUnique();
        }
    }

    private void checkHasUnique() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from uniqueTable", null);
        int i = cursor.getCount();
        if (i == 0) {
            String unique = UUID.randomUUID().toString();
            databaseHandler.insertUnique(unique);
            checkCurrentUser();
        } else {
            checkCurrentUser();
        }
    }

    private void goToPhoneNumber() {
        startActivity(new Intent(SplashActivity.this, PhoneNumber.class));
        finish();
    }

    private void checkLoginState() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String loginstate = documentSnapshot.get("loginstate").toString();
                if (loginstate.equals("yes")) {//로그인중
                    goToAsk();
                } else if (loginstate.equals("no")) {//로그인중 아님
                    setNewUniqueAndLoginStateFirestore();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void checkSameUnique() {
        Log.d("SplashActivity>>>", "checkSameUnique");
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("unique") == null) {
                    Log.d("SplashActivity>>>", "unique == null");
                    setNewUniqueAndLoginStateFirestore();
                } else {
                    String uniqueInFirestore = documentSnapshot.get("unique").toString();
                    if (uniqueInFirestore.equals(getUniqueInSql())) {
//                    goToMain();
                        checkUser();
                        Log.d("SplashActivity>>>", "unique same");
                    } else {
//                        checkLoginState();
//                    goToAsk();//다른기기에서 로그인
//                    checkUser();
                        Log.d("SplashActivity>>>", "unique not same");
                        checkNewField();

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void checkNewField() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("new") == null) {
                    checkUser();
                    Log.d("SplashActivity>>>", "new field == null");
                } else {
                    Log.d("SplashActivity>>>", "new field != null");
                    deleteNewField();
                    mAuth.signOut();
                    goToPhoneNumber();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void deleteNewField() {
        DocumentReference dr = db.collection("users").document(myUid);
        Map<String, Object> deleteFieldData = new HashMap<>();
        deleteFieldData.put("new", FieldValue.delete());
        dr.update(deleteFieldData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void checkUser() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("name") == null) {
                    goToLoginActivity();
                } else {
                    goToMain();
                    Log.d("SplashActivity>>>", "go to main");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setdb() {
        databaseHandler.setDB(SplashActivity.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        myUid = mAuth.getUid();
        Log.d("SplashActivity>>>", "my uid: " + myUid);
    }

    private String getUniqueInSql() {
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniqueInSql = cursor.getString(0);
        Log.d("SplashActivity>>>", "my unique: " + uniqueInSql);
        return uniqueInSql;
    }

    private void goToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAsk() {
        startActivity(new Intent(SplashActivity.this, AskLogin.class));
    }

    private void setNewUniqueAndLoginStateFirestore() {
        Map<String, Object> updateUnique = new HashMap<>();
        updateUnique.put("unique", getUniqueInSql());
        db.collection("users").document(myUid).update(updateUnique).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
