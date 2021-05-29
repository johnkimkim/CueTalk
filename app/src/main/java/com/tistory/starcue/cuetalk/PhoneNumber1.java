package com.tistory.starcue.cuetalk;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PhoneNumber1 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    private String myUid;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private String mAuthVerificationId;

    private EditText phone1edit;
    private Button okbtn;
    private RelativeLayout load;

    LottieAnimationView lottie;

    private AlertDialog alertDialog;
    private Button dialogok, dialogno;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonenumber1);


        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        setdb();

        setinit();
        setOnClickbtn();
    }

    private void setdb() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        databaseHandler.setDB(PhoneNumber1.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
    }

    private void setinit() {
        phone1edit = findViewById(R.id.edittext1);
        okbtn = findViewById(R.id.okbtn1);
        okbtn.setEnabled(false);
        load = findViewById(R.id.phonenumber1_load);
        load.setVisibility(View.GONE);
        load.bringToFront();
        lottie = findViewById(R.id.phonenumber1_lottie);
        lottie.playAnimation();
        setLottie();

        phone1edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String count = charSequence.toString();
                Log.d("PhoneNumber1>>>", count);
                if (count.length() < 6) {
                    okbtn.setEnabled(false);
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//키보드내리기
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//키보드내리기
                    okbtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setLottie() {
        Handler handler = new Handler();
        lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lottie.playAnimation();
                    }
                }, 2500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void setOnClickbtn() {
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);

                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//키보드내리기
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//키보드내리기

                String otp = phone1edit.getText().toString();
                if (otp.isEmpty()) {
                    Toast.makeText(PhoneNumber1.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    okbtn.setEnabled(false);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneNumber1.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//
//                            FirebaseUser user = task.getResult().getUser();
//                            // ...
                            checkSameUnique();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(">>>", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneNumber1.this, "인증코드를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                                okbtn.setEnabled(true);
                            }
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            goBackSplashActivity();
        }
    }

    private void goBackSplashActivity() {
        load.setVisibility(View.GONE);
        okbtn.setEnabled(false);
        Intent intent = new Intent(PhoneNumber1.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    private void checkSameUnique() {
        Log.d("PhoneNumber1>>>", "checkSameUnique");
        myUid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("unique") == null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("개인정보처리방침", getTime());
                    map.put("위치기반서비스이용약관", getTime());
                    map.put("개인정보수집및이용동의", getTime());
                    db.collection("users").document(myUid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            goBackSplashActivity();
                        }
                    });
                    Log.d("SplashActivity>>>", "unique == null");
                } else {
                    String uniqueInFirestore = documentSnapshot.get("unique").toString();
                    if (uniqueInFirestore.equals(getUniqueInSql())) {
//                    goToMain();
                        checkUser();
                        Log.d("SplashActivity>>>", "unique same");
                    } else {
                        goToAsk();
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
                    goToLoginActivity();
                } else {
                    goToMain();
                    Log.d("PhoneNumber1>>>", "go to main");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(PhoneNumber1.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToMain() {
        Intent intent = new Intent(PhoneNumber1.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAsk() {
//        startActivity(new Intent(PhoneNumber1.this, AskLogin.class));
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.logout_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PhoneNumber1.this);
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

        dialogok = layout.findViewById(R.id.logout_dialog_okbtn);
        dialogno = layout.findViewById(R.id.logout_dialog_cancelbtn);

        dialogno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(PhoneNumber1.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dialogok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                alertDialog.dismiss();
                setfirestoredata();
            }
        });
    }

    private void setfirestoredata() {

        databaseHandler.setDB(PhoneNumber1.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniquestring = cursor.getString(0);

        String user_uid = mAuth.getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("unique", uniquestring);

        db.collection("users").document(user_uid)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private String getUniqueInSql() {
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniqueInSql = cursor.getString(0);
        Log.d("SplashActivity>>>", "my unique: " + uniqueInSql);
        return uniqueInSql;
    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }
}
