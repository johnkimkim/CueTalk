package com.tistory.starcue.cuetalk.service;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.SplashActivity;

import org.jetbrains.annotations.NotNull;

public class CheckBackgroud extends Application {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    public static boolean isBackgroud;
    public static boolean isStartActivityAlready = false;

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackgroud = true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull @NotNull Activity activity, @Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {
                Log.d("CheckBackgroud>>>", "Check Backgroud onActivityCreated");
                isStartActivityAlready = false;
            }

            @Override
            public void onActivityStarted(@NonNull @NotNull Activity activity) {
                Log.d("CheckBackgroud>>>", "Check Backgroud onAcitivityStarted");
            }

            @Override
            public void onActivityResumed(@NonNull @NotNull Activity activity) {
                Log.d("CheckBackgroud>>>", "Check Backgroud onResumed");
                Log.d("CheckBackgroud>>>", "is open?: " + SplashActivity.splashIsOpen);
                isBackgroud = false;
                if (!SplashActivity.splashIsOpen) {
                    db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();
                    if (mUser != null) {
                        Log.d("CheckBackgroud>>>", "mUser not null");
                        String myPhoneNumber = "0" + mUser.getPhoneNumber().substring(3);
                        String myUid = mAuth.getUid();
                        db.collection("blacklist").document(myPhoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                Log.d("CheckBackgroud>>>", "test1");
                                if (task.isSuccessful()) {
                                    Log.d("CheckBackgroud>>>", "test2");
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        Log.d("CheckBackgroud>>>", "test3");
//                                        if (isStartActivityAlready) {
                                            Log.d("CheckBackgroud>>>", "test4");
                                            isStartActivityAlready = true;
                                            Intent intent = new Intent(CheckBackgroud.this, SplashActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
//                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        Log.d("CheckBackgroud>>>", "mUser null");
                    }
                }
            }

            @Override
            public void onActivityPaused(@NonNull @NotNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull @NotNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull @NotNull Activity activity, @NonNull @NotNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull @NotNull Activity activity) {

            }
        });
    }
}
