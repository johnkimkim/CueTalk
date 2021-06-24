package com.tistory.starcue.cuetalk;

import android.animation.Animator;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DeleteAuth1 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore db;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    EditText editText;
    Button btn;
    RelativeLayout load;
    LottieAnimationView lottie;

    String myUid;

    private String mAuthVerificationId;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_auth_phonenumber1);

        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        databaseHandler.setDB(DeleteAuth1.this);
        databaseHandler = new DatabaseHandler(DeleteAuth1.this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        myUid = mAuth.getUid();

        editText = findViewById(R.id.delete_auth_edittext1);
        btn = findViewById(R.id.delete_auth_okbtn1);
        load = findViewById(R.id.delete_auth_phonenumber1_load);
        load.setVisibility(View.GONE);
        lottie = findViewById(R.id.delete_auth_phonenumber1_lottie);
        lottie.playAnimation();
        setLottie();

        setEditText();
        onClickBtn();
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
                if (count.length() < 6) {
                    btn.setEnabled(false);
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);//키보드내리기
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//키보드내리기
                    btn.setEnabled(true);
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

    private void onClickBtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                btn.setEnabled(false);
                String otp = editText.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otp);
                deleteAuthWithPhoneAuthCredential(credential);
            }
        });
    }

    private void deleteAuthWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mCurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            saveOneMonthUser();
                        }
                    });
                } else {
                    load.setVisibility(View.GONE);
                    Toast.makeText(DeleteAuth1.this, "인증코드를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    btn.setEnabled(true);
                }
            }
        });
    }

    private void saveOneMonthUser() {
        Log.d("DeleteAuth1>>>", "saveOneMonthUser");
        String myPhoneNumber = mCurrentUser.getPhoneNumber();
        Map<String, Object> map = new HashMap<>();
        map.put("phonenumber", myPhoneNumber);
        map.put("uid", myUid);
        map.put("date", getTime());
        db.collection("deleteUser").document(getTime()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DeleteAuth1>>>", "saveOneMonthUser Success");
                deleteStorageF4chatroomImg();
            }
        });
    }

    private void deleteStorageF4chatroomImg() {
        Log.d("DeleteAuth1>>>", "deleteStorageF4chatroomImg");
        reference.getRef().child("myroom").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    Log.d("DeleteAuth1>>>", "deleteStorageF4chatroomImg Success");
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        count += 1;
                        storageReference.child("/" + myUid + "/" + snapshot.getKey()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                int i = listResult.getItems().size();
                                if (i >= 1) {
                                    for (StorageReference item : listResult.getItems()) {
                                        storageReference.child("/" + myUid + "/" + snapshot.getKey() + "/" + item.getName()).delete();
                                    }
                                }
                            }
                        });
                        if (count == dataSnapshot.getChildrenCount()) {
                            deletef2();
                        }
                    }
                } else {
                    deletef2();
                }
            }
        });
    }

    private void deletef2() {
        Log.d("DeleteAuth1>>>", "deletef2");
        //delete f2
        db.collection("f2messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        db.collection("f2messege").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DeleteAuth1>>>", "deletef2 Success");
                                deletef3();
                            }
                        });
                    } else {
                        Log.d("DeleteAuth1>>>", "deletef2 Success");
                        deletef3();
                    }
                }
            }
        });

    }

    private void deletef3() {
        Log.d("DeleteAuth1>>>", "deletef3");
        db.collection("f3messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        db.collection("f3messege").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DeleteAuth1>>>", "deletef3 Success");
                                deleteF3Image();
                            }
                        });
                    } else {
                        Log.d("DeleteAuth1>>>", "deletef3 Success");
                        deleteRealtimeMyroom();
                    }
                }
            }
        });
    }

    private void deleteF3Image() {
        Log.d("DeleteAuth1>>>", "deleteF3Image");
        storageReference.child("/fragment3/" + myUid + "/" + myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DeleteAuth1>>>", "deleteF3Image Success");
                deleteRealtimeMyroom();
            }
        });
    }

    private void deleteRealtimeMyroom() {
        Log.d("DeleteAuth1>>>", "deleteRealtimeMyroom");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.getRef().child("myroom").child(myUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DeleteAuth1>>>", "deleteRealtimeMyroom Success");
                lastDeleteUser();
            }
        });
    }

    private void lastDeleteUser() {
        Log.d("DeleteAuth1>>>", "lastDeleteUser");
        db.collection("users").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DeleteAuth1>>>", "lastDeleteUser Success");
                databaseHandler.uniquedelete();
                startActivity(new Intent(DeleteAuth1.this, SplashActivity.class));
                MainActivity.loading.setVisibility(View.GONE);
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }
}
