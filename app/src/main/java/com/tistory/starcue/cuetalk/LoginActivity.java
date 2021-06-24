package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    private EditText editid;
    private Button loginbtn;
    private String[] age;
    NumberPicker picker;
    RadioButton radiomale, radiofemale;
    String agestring;
    String sexstring;
    RadioGroup radioGroup;
    Spinner agespin;
    RelativeLayout relativeLayout;
    ImageView pic;
    CircularDotsLoader picpro;
    Button addpic;
    Uri imageUri;
    TextView editidcount;

    String[] items = {"나이", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70"};

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        databaseHandler.setDB(LoginActivity.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setinit();
        setagespin();
        setLoginbtn();
    }

    private void setinit() {
        editid = findViewById(R.id.longin_activity_editid);
        loginbtn = findViewById(R.id.loginbtn);
        radiomale = findViewById(R.id.login_activity_sexmale);
        radiofemale = findViewById(R.id.login_activity_sexfemale);
        radioGroup = findViewById(R.id.login_activity_radiogroup);
        agespin = findViewById(R.id.longin_activity_agespin);
        relativeLayout = findViewById(R.id.progresslayout);
        pic = findViewById(R.id.login_profile_image);
        picpro = findViewById(R.id.login_activity_pic_progress);
        addpic = findViewById(R.id.login_add_image);
        editidcount = findViewById(R.id.login_activity_name_count);

        relativeLayout.setVisibility(View.GONE);

        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooosePic();
            }
        });

        setEditTextWithCount();

        Glide.with(LoginActivity.this)
                .load("https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullUser.png?alt=media&token=4c9daa69-6d03-4b19-a793-873f5739f3a1")
                .circleCrop()
                .override(150, 150)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        picpro.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        picpro.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(pic);
    }

    private void setEditTextWithCount() {
        editid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = editid.getText().toString();
                editidcount.setText(input.length() + " / 5");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void chooosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            picpro.setVisibility(View.VISIBLE);
            imageUri = data.getData();
            Glide.with(LoginActivity.this)
                    .load(imageUri)
                    .override(150, 150)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            picpro.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            picpro.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .circleCrop()
                    .into(pic);
//            pic.setImageURI(imageUri);
        }
    }

    private void uploadPic() {

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCancelable(false);
        pd.setTitle("이미지 업로드 중...");
        pd.show();

        String uid = mAuth.getUid();

        if (imageUri != null) {
            StorageReference riversRef = storageReference.child("images/" + uid + "/" + uid + "count1");
            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();

                            //upload pic in firestore
                            storageReference.child("images/" + uid + "/" + uid + "count1").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String picUri = uri.toString();
                                            uploadPicInStore(picUri);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });//upload pic in firestore
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "업로드실패", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage("Pro: " + (int) progressPercent + "%");
                        }
                    });
        }

    }

    private void ifNullPic() {
        String uid = mAuth.getUid();
        Map<String, Object> map = new HashMap<>();
        if (sexstring.equals("남자")) {
            map.put("pic", nullPic);
        } else {
            map.put("pic", nullPicF);
        }
        db.collection("users").document(uid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                goToMain();
            }
        });
    }

    private void setLoginbtn() {
        loginbtn.setOnClickListener(view -> {
            relativeLayout.setVisibility(View.VISIBLE);
            hideKeyboard(view);
            if (radiomale.isChecked()) {
                sexstring = "남자";
            } else if (radiofemale.isChecked()) {
                sexstring = "여자";
            } else {
                sexstring = "";
            }

            String name = editid.getText().toString();

            int i = Integer.parseInt(agestring);

            if (name.equals("")) {
                Toast.makeText(LoginActivity.this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);
            } else if (name.matches(".*[ㄱ-ㅎ ㅏ-ㅣ]+.*")) {
                Toast.makeText(LoginActivity.this, "올바른 닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);
            } else if (i == 0) {
                Toast.makeText(LoginActivity.this, "나이를 선택해주세요", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);
            } else if (sexstring.equals("")) {
                Toast.makeText(LoginActivity.this, "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);
            } else {

                updateUser(name, sexstring, agestring);

            }

//            mAuth.signOut();

        });
    }

    private void setagespin() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.change_profile_spinner_layout, R.id.change_profile_spinner_layout_text, items);
        arrayAdapter.setDropDownViewResource(R.layout.change_profile_spinner_layout);
        agespin.setAdapter(arrayAdapter);
        agespin.setDropDownWidth(WindowManager.LayoutParams.MATCH_PARENT);

        agespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    agestring = "0";
                } else {
                    agestring = Integer.toString(i + 19);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateUser(String name, String sex, String age) {
//        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
//        cursor.moveToFirst();
//        String uniquestring = cursor.getString(0);
//        //get unique

        String user_uid = mAuth.getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("sex", sex);
        user.put("age", age);
        user.put("notify", "on");

        db.collection("users").document(user_uid)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (imageUri != null) {
                            uploadPic();
                        } else {
                            ifNullPic();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "네트워크문제로 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPicInStore(String uri) {
        String user_iod = mAuth.getUid();
        Map<String, Object> userPic = new HashMap<>();
        userPic.put("pic", uri);
        db.collection("users").document(user_iod)
                .update(userPic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        goToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void hideKeyboard(View v) {
        InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
