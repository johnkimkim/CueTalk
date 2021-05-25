package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
                        checkCurrentUser();
                    }
                } else {//그냥 넘어가기
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
                        firestore.collection("blacklist").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                List<String> count = new ArrayList<>();
                                for (DocumentSnapshot snapshot : value.getDocuments()) {
                                    count.add(snapshot.getId());
                                    if (count.size() == value.size()) {
                                        if (count.contains(myPhoneNumber)) {
                                            blacklistDialog();
                                        } else {
                                            checkUser();
                                            Log.d("SplashActivity>>>", "unique same");
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
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        TextView title = layout.findViewById(R.id.blacklist_dialog_title);
        Button btn = layout.findViewById(R.id.blacklist_dialog_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
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
