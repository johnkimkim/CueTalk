package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumber extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private TextView mPhoneNumber;
    private Button mButton;
    private ProgressBar mProgressBar;
    private TextView feedtext;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonenumber);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        setunit();
        setOnClickBtn();

    }

    private void setunit() {
        mPhoneNumber = findViewById(R.id.edittext);
        mButton = findViewById(R.id.okbtn);
        mProgressBar = findViewById(R.id.login_progress_bar);
        feedtext = findViewById(R.id.login_feedback);
        feedtext.setVisibility(View.GONE);
    }

    private void setOnClickBtn() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = "+82" + mPhoneNumber.getText().toString();

                if (phoneNum.isEmpty()) {
                    feedtext.setVisibility(View.VISIBLE);
                    feedtext.setText("전화번호를 입력해주세요");
                } else {
                    feedtext.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
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
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                feedtext.setText("실패!");
                feedtext.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mButton.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Intent intent = new Intent(PhoneNumber.this, PhoneNumber1.class);
                intent.putExtra("AuthCredentials", s);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneNumber.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//
//                            FirebaseUser user = task.getResult().getUser();
//                            // ...
                            sendUserToHome();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(">>>", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                feedtext.setText("인증코드를 다시 확인해주세요");
                                feedtext.setVisibility(View.VISIBLE);
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mButton.setEnabled(true);
                    }
                });
    }

    private void sendUserToHome() {
        Intent intent = new Intent(PhoneNumber.this, FLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
