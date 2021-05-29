package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneNumber extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private TextView mPhoneNumber;
    private Button mButton;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    private CheckBox cba, cb1, cb2, cb3;
    private RelativeLayout load;
    private TextView view1, view2, view3;

    private AlertDialog alertDialog;

    private FirebaseFirestore db;
    private String myUid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonenumber);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        setunit();
        setOnClickBtn();

//        FirebaseUser user = mAuth.getCurrentUser();
//        String pn = user.getPhoneNumber();

    }

    private void setunit() {
        mPhoneNumber = findViewById(R.id.edittext);
        mButton = findViewById(R.id.okbtn);

        load = findViewById(R.id.access2_loading);
        load.setVisibility(View.GONE);
        load.bringToFront();
        cba = findViewById(R.id.access2_checkbox);
        cb1 = findViewById(R.id.access2_checkbox1);
        cb2 = findViewById(R.id.access2_checkbox2);
        cb3 = findViewById(R.id.access2_checkbox3);
        view1 = findViewById(R.id.access2_view1);
        view2 = findViewById(R.id.access2_view2);
        view3 = findViewById(R.id.access2_view3);
        checkAll();
        setOnClickViewer();

        mButton.setEnabled(false);
        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String count = charSequence.toString();
                Log.d("PhoneNumber1>>>", count);
                if (count.length() < 11) {
                    mButton.setEnabled(false);
                } else {
                    mButton.setEnabled(true);
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//    private void setLottie() {
//        animationView.playAnimation();
//        Handler handler = new Handler();
//        animationView.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        animationView.playAnimation();
//                    }
//                }, 1500);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//    }

    private void checkAll() {
        cba.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//checked
                    cb1.setChecked(true);
                    cb2.setChecked(true);
                    cb3.setChecked(true);
                } else {//no checked
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });
    }

    private void setOnClickViewer() {
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Access2>>>", "view1 onClick");
                db.collection("version").document("access2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String link = documentSnapshot.get("위치기반서비스이용약관").toString();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        intent.setPackage("com.android.vending");
                        startActivity(intent);
                    }
                });
            }
        });

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Access2>>>", "view2 onClick");
                db.collection("version").document("access2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String link = documentSnapshot.get("개인정보처리방침").toString();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        intent.setPackage("com.android.vending");
                        startActivity(intent);
                    }
                });
            }
        });

        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Access2>>>", "view3 onClick");
                db.collection("version").document("access2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String link = documentSnapshot.get("개인정보수집및이용동의").toString();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        intent.setPackage("com.android.vending");
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void setOnClickBtn() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("blacklist").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<String> count = new ArrayList<>();
                        for (DocumentSnapshot value1 : value.getDocuments()) {
                            Log.d("Fragment1>>>", "get black list: " + value1.getId());
                            Log.d("Fragment1>>>", "get black list size: " + value.size());
                            count.add(value1.getId());
                            Log.d("Fragment1>>>", "count size: " + count.size());
                            if (count.size() == value.size()) {
                                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                                String phoneNum = "+82" + mPhoneNumber.getText().toString().substring(1);
                                Log.d("PHoneNumber>>>", phoneNum);

                                if (!cb1.isChecked() || !cb2.isChecked() || !cb3.isChecked()) {
                                    dialog();
                                }
                                if (count.contains(mPhoneNumber.getText().toString())) {
                                    Log.d("Fragment1>>>", "test2");
                                    load.setVisibility(View.GONE);
                                    Toast.makeText(PhoneNumber.this, "해당 전화번호는 정책위반으로 인해 서비스 이용이 정지되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("Fragment1>>>", "test3");
                                    load.setVisibility(View.VISIBLE);
                                    mButton.setEnabled(false);

//                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                            phoneNum,
//                            60,
//                            TimeUnit.SECONDS,
//                            PhoneNumber.this,
//                            mCallback
//                    );

                                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                                            .setPhoneNumber(phoneNum)
                                            .setTimeout(60L, TimeUnit.SECONDS)
                                            .setActivity(PhoneNumber.this)
                                            .setCallbacks(mCallback)
                                            .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);
                                }
                            }
                        }
                    }
                });


            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneNumber.this, "알 수 없는 문제로 실패했습니다. 다시 시해도해주세요", Toast.LENGTH_SHORT).show();
                load.setVisibility(View.INVISIBLE);
                mButton.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Intent intent = new Intent(PhoneNumber.this, PhoneNumber1.class);
                intent.putExtra("AuthCredentials", s);
                startActivity(intent);
                finish();
            }
        };
    }

    private void dialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.if_not_access_2, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PhoneNumber.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();

        if (!PhoneNumber.this.isFinishing()) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            alertDialog.show();
        }

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        Button okbtn = layout.findViewById(R.id.access2_dialog_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendUserToHome();
        }
    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(PhoneNumber.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
////                            // Sign in success, update UI with the signed-in user's information
////                            Log.d(TAG, "signInWithCredential:success");
////
////                            FirebaseUser user = task.getResult().getUser();
////                            // ...
////                            sendUserToHome();
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w(">>>", "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                                feedtext.setText("인증코드를 다시 확인해주세요");
//                                feedtext.setVisibility(View.VISIBLE);
//                            }
//                        }
//                        load.setVisibility(View.INVISIBLE);
//                        mButton.setEnabled(true);
//                    }
//                });
//    }

    private void sendUserToHome() {
        Intent intent = new Intent(PhoneNumber.this, FLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}
