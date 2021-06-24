package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class DeleteAuth extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    EditText editText;
    Button btn;
    RelativeLayout load;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_auth_phonenumber);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        editText = findViewById(R.id.delete_auth_edittext);
        editText.setEnabled(false);
        String myPhoneNumber = mCurrentUser.getPhoneNumber();
        String s = myPhoneNumber.substring(5, 9);
        String ss = myPhoneNumber.substring(9, 13);
        editText.setText("010-" + s + "-" + ss);
        Log.d("DeleteAuth>>>", "010-" + s + "-" + ss);
        editText.setTextColor(getResources().getColor(R.color.black));
        btn = findViewById(R.id.delete_auth_okbtn);
        load = findViewById(R.id.delete_auth_phonenumber_load);
        load.setVisibility(View.GONE);

        setOnClick();
    }

    private void setOnClick() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                btn.setEnabled(false);
                String phoneNum = mCurrentUser.getPhoneNumber();

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNum)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(DeleteAuth.this)
                        .setCallbacks(mCallback)
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(DeleteAuth.this, "네트워크 오류로 실패했습니다. 인터넷 연결을 확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                load.setVisibility(View.GONE);
                btn.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Intent intent = new Intent(DeleteAuth.this, DeleteAuth1.class);
                intent.putExtra("AuthCredentials", s);
                startActivity(intent);
                finish();
            }
        };
    }
}
