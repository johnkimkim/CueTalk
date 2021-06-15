package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChangePhoneNumber extends AppCompatActivity {

    private RelativeLayout load;
    TextView title;
    EditText editText;
    Button okbtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_phone_number);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setInit();

    }

    private void setInit() {
        load = findViewById(R.id.change_phone_number_load);
        load.setVisibility(View.GONE);
        title = findViewById(R.id.change_phone_number_title);
        editText = findViewById(R.id.change_phone_number_edittext);
        okbtn = findViewById(R.id.change_phone_number_okbtn);
        okbtn.setEnabled(false);

        setEditText();
        setOnClickOkBtn();
    }

    private void setEditText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String count = charSequence.toString();
                Log.d("PhoneNumber1>>>", count);
                if (count.length() < 11) {
                    okbtn.setEnabled(false);
                } else {
                    okbtn.setEnabled(true);
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setOnClickOkBtn() {
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("blacklist").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        List<String> count = new ArrayList<>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            count.add(snapshot.getId());
                            if (count.size() == value.size()) {
                                String phoneNum = "+82" + editText.getText().toString().substring(1);
                                if (count.contains(editText.getText().toString())) {
                                    load.setVisibility(View.GONE);
                                    Toast.makeText(ChangePhoneNumber.this, "해당 전화번호는 정책위반으로 인해 서비스 이용이 정지되었습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                            List<String> phonelist = new ArrayList<>();
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    phonelist.add(document.get("phonenumber").toString());
                                                    if (phonelist.size() == task.getResult().size()) {
                                                        if (phonelist.contains(editText.getText().toString())) {
                                                            load.setVisibility(View.GONE);
                                                            Toast.makeText(ChangePhoneNumber.this, "해당 번호는 이미 존제하는 번호입니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            okbtn.setEnabled(false);
                                                            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                                                                    .setPhoneNumber(phoneNum)
                                                                    .setTimeout(60L, TimeUnit.SECONDS)
                                                                    .setActivity(ChangePhoneNumber.this)
                                                                    .setCallbacks(mCallback)
                                                                    .build();
                                                            PhoneAuthProvider.verifyPhoneNumber(options);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                load.setVisibility(View.GONE);
                Toast.makeText(ChangePhoneNumber.this, "네트워크 오류로 실패했습니다. 인터넷 연결을 확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                okbtn.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Intent intent = new Intent(ChangePhoneNumber.this, ChangePhoneNumber1.class);
                intent.putExtra("AuthCredentials", s);
                startActivity(intent);
                finish();
            }
        };
    }

}
