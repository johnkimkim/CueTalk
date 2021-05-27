package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class ChangePhoneNumber1 extends AppCompatActivity {

    TextView title, text, feed;
    EditText editText;
    Button okbtn;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    private String mAuthVerificationId;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_phone_number1);

        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        setInit();
        setDb();
    }

    private void setInit() {
        title = findViewById(R.id.change_phone_number1_title);
        text = findViewById(R.id.change_phone_number1_text);
        feed = findViewById(R.id.change_phone_number1_feedback);
        editText = findViewById(R.id.change_phone_number1_edittext1);
        okbtn = findViewById(R.id.change_phone_number1_okbtn1);
        progressBar = findViewById(R.id.change_phone_number1_progress_bar);

        setOnClickOkBtn();

        editText.addTextChangedListener(new TextWatcher() {
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

    private void setDb() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void setOnClickOkBtn() {
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//키보드내리기
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//키보드내리기

                String otp = editText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                okbtn.setEnabled(false);

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
                updatePhoneNumber(credential);
            }
        });
    }

    private void updatePhoneNumber(PhoneAuthCredential credential) {
        mAuth.getCurrentUser().updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //변경 성공
                    dialog();
                    Log.d("ChangePhoneNumber1", "my new Phone Number: " + mUser.getPhoneNumber());
                } else {
                    progressBar.setVisibility(View.GONE);
                    feed.setVisibility(View.VISIBLE);
                    feed.setText("네트워크 문제로 실패했습니다. 다시 시도해주세요.");
                }
            }
        });
    }

    private void dialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.success_change_phone_number, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePhoneNumber1.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();

        if (!ChangePhoneNumber1.this.isFinishing()) {
            alertDialog.show();
        }

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        Button okbtn = layout.findViewById(R.id.success_change_phone_number_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                goToMain();
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(ChangePhoneNumber1.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
