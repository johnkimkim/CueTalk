package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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

    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        double latitudeA = 37.11;
        double longitudeA = 127.11;
        double latitudeB = 37.22;
        double longitudeB = 127.22;

        String s = String.valueOf(getDistance(latitudeA, longitudeA, latitudeB, longitudeB));
        Log.d("Splash>>>", s);

        double d = 5.12345;
        Location locationA = new Location("pointA");
        locationA.setLatitude(37);
        locationA.setLongitude(127);
        Location locationB = new Location("pointB");
        locationB.setLatitude(38);
        locationB.setLongitude(128);

        //check permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //has permission
                mAuth = FirebaseAuth.getInstance();
                mCurrentUser = mAuth.getCurrentUser();
                db = FirebaseFirestore.getInstance();

                preferences = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
                firstLogin = preferences.getBoolean("checkFirst", false);

                checkUnique();
            } else {
                Intent intent = new Intent(Splash.this, Access.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            db = FirebaseFirestore.getInstance();

            preferences = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
            firstLogin = preferences.getBoolean("checkFirst", false);

            checkUnique();
        }

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

    private void checkUnique() {
        setDB();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from uniqueTable", null);
        int i = cursor.getCount();
        if (i == 0) {
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
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUser();
                    Log.d(">>>", "not first");
                }
            }, 500);
        }
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
