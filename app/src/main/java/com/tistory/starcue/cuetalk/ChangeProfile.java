package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChangeProfile extends AppCompatActivity {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference reference;
    String myUid;
    String mySex;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private EditText editid;
    private Button yesbtn, nobtn;
    private String[] age;
    NumberPicker picker;
    RadioButton radiomale, radiofemale;
    String myName;
    String agestring;
    String sexstring;
    RadioGroup radioGroup;
    Spinner agespin;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;

    String[] items = {"나이", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70"};

    ImageView imageView;
    Button deletePic;
    Button addImageBtn;
    Uri imageUri;

    boolean willdelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("willdelete", MODE_PRIVATE);
        willdelete = sharedPreferences.getBoolean("willdelete", false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference();
        myUid = mAuth.getUid();

        databaseHandler.setDB(ChangeProfile.this);
        databaseHandler = new DatabaseHandler(this);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        setinit();
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
        deletePic = findViewById(R.id.change_profile_delete_pic);

        setView();

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooosePic();
            }
        });

        deletePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeletePic();
            }
        });

    }

    private void setView() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myName = documentSnapshot.get("name").toString();
                editid.setText(documentSnapshot.get("name").toString());
                String age = documentSnapshot.get("age").toString();
                int ageint = Integer.parseInt(age);
                agespin.setSelection(ageint - 19);
                String sex = documentSnapshot.get("sex").toString();
                String picUri = documentSnapshot.get("pic").toString();
                if (sex.equals("남자")) {
                    radioGroup.check(R.id.change_profile_sexmale);
                } else if (sex.equals("여자")) {
                    radioGroup.check(R.id.change_profile_sexfemale);
                }
                Log.d("ChangeProfile>>>", "get pic uri: " + picUri);
                Glide.with(ChangeProfile.this).load(picUri).override(300, 300).circleCrop().into(imageView);
                relativeLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setDeletePic() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.get("pic").equals(nullPic) && !documentSnapshot.get("pic").equals(nullPicF)) {
                    mySex = documentSnapshot.get("sex").toString();
                    Log.d("ChangeProfile>>>", "mySex: " + mySex);
                    if (mySex.equals("남자")) {
                        Glide.with(imageView).load(nullPic).signature(new ObjectKey(System.currentTimeMillis()))
                                .circleCrop().into(imageView);
                    } else {
                        Glide.with(imageView).load(nullPicF).signature(new ObjectKey(System.currentTimeMillis()))
                                .circleCrop().into(imageView);
                    }
                    willdelete = true;
                }
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
        if (imageUri != null) { //pic 변경 및 신규등록
            final ProgressDialog pd = new ProgressDialog(ChangeProfile.this);
            pd.setTitle("이미지 업로드 중...");
            pd.show();

            String uid = mAuth.getUid();
            StorageReference riversRef = storageReference.child("images/" + uid);
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //upload pic in firestore
                    storageReference.child("images/" + uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String picUri = uri.toString();
                            uploadPicInStore(picUri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });//upload pic in firestore
                    pd.dismiss();
                    relativeLayout.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();
                    goToMain();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    relativeLayout.setVisibility(View.GONE);
                    Toast.makeText(ChangeProfile.this, "업로드실패", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Pro: " + (int) progressPercent + "%");
                }
            });
        } else {
            if (willdelete) {//사진 삭제변경할때
                Map<String, Object> map = new HashMap<>();
                if (mySex.equals("남자")) {
                    map.put("pic", nullPic);
                } else {
                    map.put("pic", nullPicF);
                }
                db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {//store변경
                    @Override
                    public void onSuccess(Void aVoid) {
                        storageReference.child("images/" + myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                reference.getRef().child("myroom").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        int i = (int) dataSnapshot.getChildrenCount();
                                        if (i > 0) {
                                            Map<String, Object> map1 = new HashMap<>();//실시간 채팅 pic 변경
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (mySex.equals("남자")) {
                                                    map1.put("/messege/" + snapshot.getKey() + "/" + myUid + "/" + "pic/", nullPic);
                                                } else {
                                                    map1.put("/messege/" + snapshot.getKey() + "/" + myUid + "/" + "pic/", nullPicF);
                                                }
                                                reference.updateChildren(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {


                                                        //f2 change pic
                                                        f2changepic();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    }
                                                });
                                            }
                                        } else {
                                            //f2 change pic
                                            f2changepic();
                                        }
                                    }
                                });
                            }
                        });

//                        Log.d("ChangeProfile>>>", "db.updqte success");

                    }
                });
            }
        }


    }

    private void f2changepic() {
        //f2 change pic
        Map<String, Object> map2 = new HashMap<>();
        if (mySex.equals("남자")) {
            map2.put("pic", nullPic);
        } else {
            map2.put("pic", nullPicF);
        }

        db.collection("f2messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        db.collection("f2messege").document(myUid).update(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {//storage mypic delete
                                goToMain();
                            }
                        });
                    } else {
                        goToMain();
                    }
                } else {
                    Log.d("ChangeProfile>>>", "onFailed2");
                }
            }
        });


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
            }

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
        databaseHandler.changeName(name);

        db.collection("users").document(user_uid)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadPic();
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

    private void uploadPicInStore(String uri) {
        String user_iod = mAuth.getUid();
        Map<String, Object> userPic = new HashMap<>();
        userPic.put("pic", uri);
        db.collection("users").document(user_iod)
                .update(userPic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {//my profile update in storage
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("f2messege").document(myUid).update(userPic).addOnSuccessListener(new OnSuccessListener<Void>() {//f2messege update pic
                            @Override
                            public void onSuccess(Void aVoid) {
                                reference.getRef().child("myroom").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        //realtime messege update pic
                                        int i = (int) dataSnapshot.getChildrenCount();
                                        Map<String, Object> map = new HashMap<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            map.put("/messege/" + snapshot.getKey() + "/" + myUid + "/pic/", uri);
                                            if (map.size() == i) {
                                                reference.updateChildren(map);
                                                Log.d("ChangeProfile>>>", "add map finished");
                                                goToMain();
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        goToMain();
                                    }
                                });
                            }
                        });
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


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("willdelete", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("willdelete", willdelete);
        editor.commit();
    }
}
