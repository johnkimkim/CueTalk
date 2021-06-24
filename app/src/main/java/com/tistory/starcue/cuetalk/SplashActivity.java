package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;

    String myUid;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//no dark mode

        setdb();
        checkPermission();
        Log.d("SplashActivity>>>", "app version: " + getAppVersion());

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
            checkAppVersion();
        } else {
            checkAppVersion();
        }
    }

    private void checkAppVersion() {
        db.collection("version").document("version").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String must = documentSnapshot.get("must").toString();
                String version = documentSnapshot.get("version").toString();
                String messege = documentSnapshot.get("splashmessege").toString();
                String url = documentSnapshot.get("url").toString();
                String nowVersion = getAppVersion();
                if (must.equals("yes")) {//업데이트 후 넘어가기
                    if (!nowVersion.equals(version)) {//새버전 있을때
                        updateDialog(messege, url);
                    } else {//현제 최신버전일때
                        checkFix();
                    }
                } else {//그냥 넘어가기
                    checkFix();
                }
            }
        });
    }

    private void checkFix() {
        db.collection("version").document("fix").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String date = documentSnapshot.get("date").toString();
                String startTime = documentSnapshot.get("starttime").toString();
                String endTime = documentSnapshot.get("endtime").toString();
                if (documentSnapshot.get("fix").toString().equals("yes")) {//서버점검중
                    fixDialog(date, startTime, endTime);
                } else {
                    checkCurrentUser();
                }
            }
        });
    }

    private void updateDialog(String messege, String url) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.update_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();

        if (!SplashActivity.this.isFinishing()) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            alertDialog.show();
        }

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        TextView textView = layout.findViewById(R.id.update_dialog_title);
        textView.setText(messege);
        Button okbtn = layout.findViewById(R.id.update_dialog_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }
        });

    }

    private void fixDialog(String date, String start, String end) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.fix_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();

        if (!SplashActivity.this.isFinishing()) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            alertDialog.show();
        }

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        TextView dateText = layout.findViewById(R.id.fix_dialog_date);
        TextView startText = layout.findViewById(R.id.fix_dialog_start_time);
        TextView endText = layout.findViewById(R.id.fix_dialog_end_time);
        dateText.setText(date);
        startText.setText(start);
        endText.setText(end);
        Button okbtn = layout.findViewById(R.id.fix_dialog_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //앱종료
                moveTaskToBack(true);
                finishAndRemoveTask();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    private void goToPhoneNumber() {
        Intent intent = new Intent(SplashActivity.this, PhoneNumber.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
                    if (uniqueInFirestore.equals(getUniqueInSql())) {//here check black list
//                    goToMain();

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        String myPhoneNumber = "0" + mAuth.getCurrentUser().getPhoneNumber().substring(3);
                        Log.d("Splash>>>", "get my phone number: " + myPhoneNumber);
//                        firestore.collection("blacklist").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                List<String> count = new ArrayList<>();
//                                for (DocumentSnapshot snapshot : value.getDocuments()) {
//                                    count.add(snapshot.getId());
//                                    if (count.size() == value.size()) {
//                                        if (count.contains(myPhoneNumber)) {
//                                            blacklistDialog();
//                                        } else {
//                                            checkUser();
//                                            Log.d("SplashActivity>>>", "unique same");
//                                        }
//                                    }
//                                }
//                            }
//                        });

                        firestore.collection("blacklist").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<String> pnlist = new ArrayList<>();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    pnlist.add(snapshot.getId());
                                    if (pnlist.size() == queryDocumentSnapshots.size()) {
                                        if (pnlist.contains(myPhoneNumber)) {
                                            blacklistDialog();
                                        } else {
                                            checkUser();
                                        }
                                    }
                                }
                            }
                        });

                    } else {
                        mAuth.signOut();
                        logoutDialog();
                        Log.d("SplashActivity>>>", "unique not same");

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void checkUser() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("name") == null) {
                    Log.d("SplashActivity>>>", "4");
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
        mCurrentUser = mAuth.getCurrentUser();//error 1time
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



    private void setNewUniqueAndLoginStateFirestore() {
        Map<String, Object> updateUnique = new HashMap<>();
        Log.d("SplashActivity>>>", "2");
        updateUnique.put("unique", getUniqueInSql());
        updateUnique.put("uid", myUid);
        Log.d("SplashActivity>>>", "3");
        db.collection("users").document(myUid).update(updateUnique).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SplashActivity>>>", "1");
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                blacklistDialog();
                Log.d("SplashActivity>>>", e.toString());
            }
        });
    }

    private void logoutDialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.login_another_device, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setView(layout);
        alertDialog = builder.create();

        if (!SplashActivity.this.isFinishing()) {
            alertDialog.show();
        }

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        Button okbtn = layout.findViewById(R.id.login_another_device_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okbtn.setEnabled(false);
                alertDialog.dismiss();
                goToPhoneNumber();
            }
        });
    }

    private void blacklistDialog() {
        LayoutInflater vi = (LayoutInflater) SplashActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.blacklist_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        String myPhoneNumber = "0" + mAuth.getCurrentUser().getPhoneNumber().substring(3);

        db.collection("blacklist").document(myPhoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    String logout = snapshot.get("logout").toString();
                    if (!logout.equals("yes")) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("logout", "yes");
                        db.collection("blacklist").document(myPhoneNumber).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String, Object> map1 = new HashMap<>();
                                map1.put(myPhoneNumber, myUid);
                                db.collection("deletelogoutalready").document(myPhoneNumber).set(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mAuth.signOut();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        Button btn = layout.findViewById(R.id.blacklist_dialog_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPhoneNumber();
            }
        });
    }

    public String getAppVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(SplashActivity.this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionName;
    }
}
