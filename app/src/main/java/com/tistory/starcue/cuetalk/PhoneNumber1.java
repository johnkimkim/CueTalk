package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
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
    private TextView feedtext;
    private Button okbtn;
    private ProgressBar progressBar;

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
        feedtext = findViewById(R.id.phone1feedback);
        okbtn = findViewById(R.id.okbtn1);
        okbtn.setEnabled(false);
        progressBar = findViewById(R.id.otp_progress_bar);

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
                    okbtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setOnClickbtn() {
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String otp = phone1edit.getText().toString();
                if (otp.isEmpty()) {
                    feedtext.setText("인증코드를 입력해주세요");//필요없음
                    feedtext.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
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
                                feedtext.setText("인증코드를 다시 확인해주세요");
                                feedtext.setVisibility(View.VISIBLE);
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
        progressBar.setVisibility(View.INVISIBLE);
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
                    Log.d("SplashActivity>>>", "unique == null");
                    goBackSplashActivity();
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
        startActivity(new Intent(PhoneNumber1.this, AskLogin.class));
    }

    private String getUniqueInSql() {
        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String uniqueInSql = cursor.getString(0);
        Log.d("SplashActivity>>>", "my unique: " + uniqueInSql);
        return uniqueInSql;
    }
}
