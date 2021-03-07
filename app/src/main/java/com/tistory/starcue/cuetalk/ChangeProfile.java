package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChangeProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    String myUid;

    private EditText editid;
    private Button yesbtn, nobtn;
    private String[] age;
    NumberPicker picker;
    RadioButton radiomale, radiofemale;
    String agestring;
    String sexstring;
    RadioGroup radioGroup;
    Spinner agespin;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;

    String[] items = {"나이", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70"};

    ImageView imageView;
    Button addImageBtn;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        myUid = mAuth.getUid();

        setinit();
        setPic();
        setagespin();
        setYesBtn();
        setNoBtn();

    }

    private void setinit() {
        editid = findViewById(R.id.change_profile_editid);
        yesbtn = findViewById(R.id.change_profile_yesbtn);
        nobtn = findViewById(R.id.change_profile_nobtn);
        radiomale = findViewById(R.id.change_profile_sexmale);
        radiofemale = findViewById(R.id.change_profile_sexfemale);
        radioGroup = findViewById(R.id.change_profile_radiogroup);
        agespin = findViewById(R.id.change_profile_agespin);
        relativeLayout = findViewById(R.id.change_profile_progresslayout);
        progressBar = findViewById(R.id.change_profile_progress_bar);
        imageView = findViewById(R.id.change_profile_image);
        addImageBtn = findViewById(R.id.add_image);

        relativeLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        setView();

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooosePic();
            }
        });
    }

    private void setView() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                editid.setText(documentSnapshot.get("name").toString());
                String age = documentSnapshot.get("age").toString();
                int ageint = Integer.parseInt(age);
                agespin.setSelection(ageint - 19);
                String sex = documentSnapshot.get("sex").toString();
                if (sex.equals("남자")) {
                    radioGroup.check(R.id.change_profile_sexmale);
                } else if (sex.equals("여자")) {
                    radioGroup.check(R.id.change_profile_sexfemale);
                }
                setPic();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadPic() {

        if (imageUri != null) {
            final ProgressDialog pd = new ProgressDialog(ChangeProfile.this);
            pd.setTitle("이미지 업로드 중...");
            pd.show();

            String uid = mAuth.getUid();
            StorageReference riversRef = storageReference.child("images/" + uid);
            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //upload pic in firestore
                            storageReference.child("images/" + uid).getDownloadUrl()
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
                            pd.dismiss();
                            goToMain();
                            Snackbar.make(findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ChangeProfile.this, "업로드실패", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage("Pro: " + (int) progressPercent + "%");
                        }
                    });
        } else {
            goToMain();
        }


    }

    private void setYesBtn() {
        yesbtn.setOnClickListener(view -> {

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
                Toast.makeText(ChangeProfile.this, "name", Toast.LENGTH_SHORT).show();
            } else if (i == 0) {
                Toast.makeText(ChangeProfile.this, "age", Toast.LENGTH_SHORT).show();
            } else if (sexstring.equals("")) {
                Toast.makeText(ChangeProfile.this, "sex", Toast.LENGTH_SHORT).show();
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                updateUser(name, sexstring, agestring);
                uploadPic();
            }

//            mAuth.signOut();

        });
    }

    private void setNoBtn() {
        nobtn.setOnClickListener(view -> onBackPressed());
    }

    private void setagespin() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChangeProfile.this, android.R.layout.select_dialog_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        agespin.setAdapter(arrayAdapter);

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

        String user_uid = mAuth.getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("sex", sex);
        user.put("age", age);

        db.collection("users").document(user_uid)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        relativeLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangeProfile.this, "네트워크문제로 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setPic() {
//        Glide.with(getActivity()).load("gs://cuetalk-c4d03.appspot.com/images/03aUD74hz4MjcbcZcpSMc2KfZWs2").into(pic);
        String uid = mAuth.getUid();
        StorageReference storageRef = storage.getReference().child("images/" + uid);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri == null) {
                    Log.d("ChangeProfile>>>", "setPic == null");
                }
                Log.d("ChangeProfile>>>", "setPic");
                Glide.with(ChangeProfile.this)
                        .load(uri.toString())
                        .override(600, 600)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground)
                        .circleCrop()
                        .into(imageView);
                relativeLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ChangeProfile>>>", "load pic fail");
                relativeLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
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

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(ChangeProfile.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(ChangeProfile.this, "ok", Toast.LENGTH_SHORT).show();
        finish();
    }
}
