package com.tistory.starcue.cuetalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

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
    String myName;
    String myAge;
    String myPicUri;

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private EditText editText;
    private Button yesbtn, nobtn;
    RadioButton radiomale, radiofemale;
    String newSex;
    String newName;
    String newAge;
    String newPicUri;
    RadioGroup radioGroup;
    Spinner agespin;
    RelativeLayout load;
    CircularDotsLoader loadInPic;

    String[] items = {"나이", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70"};

    ImageView imageView;
    CheckBox deletePic;
    Button addImageBtn;
    TextView editcount;
    Uri imageUri;
    TextView myPn;
    Button changePnBtn;

    boolean willdelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

//        SharedPreferences sharedPreferences = getSharedPreferences("willdelete", MODE_PRIVATE);
//        willdelete = sharedPreferences.getBoolean("willdelete", false);

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
        editText = findViewById(R.id.change_profile_editid);
        editcount = findViewById(R.id.change_profile_name_count);
        yesbtn = findViewById(R.id.change_profile_yesbtn);
        nobtn = findViewById(R.id.change_profile_nobtn);
        radiomale = findViewById(R.id.change_profile_sexmale);
        radiofemale = findViewById(R.id.change_profile_sexfemale);
        radioGroup = findViewById(R.id.change_profile_radiogroup);
        agespin = findViewById(R.id.change_profile_agespin);
        load = findViewById(R.id.change_profile_progresslayout);
        load.setVisibility(View.VISIBLE);
        loadInPic = findViewById(R.id.change_profile_pic_progress);
        loadInPic.setVisibility(View.VISIBLE);
        imageView = findViewById(R.id.change_profile_image);
        addImageBtn = findViewById(R.id.add_image);
        deletePic = findViewById(R.id.change_profile_delete_pic);
        myPn = findViewById(R.id.change_profile_my_phonenumber);
        changePnBtn = findViewById(R.id.change_profile_change_phonenumber_btn);
        String pn = mCurrentUser.getPhoneNumber().substring(9, 13);
        myPn.setText("010-****-" + pn);
        setOnClickChangePhoneNumber();

        setView();

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooosePic();
            }
        });

        deletePic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    deletePic.setEnabled(false);
                    setDeletePicButton();
                }
            }
        });

        setEditTextWithCount();
    }

    private void setEditTextWithCount() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = editText.getText().toString();
                editcount.setText(input.length() + " / 5");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setOnClickChangePhoneNumber() {
        changePnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeProfile.this, ChangePhoneNumber.class);
                startActivity(intent);
            }
        });
    }

    private void setView() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                myName = documentSnapshot.get("name").toString();
                editText.setText(documentSnapshot.get("name").toString());
                myAge = documentSnapshot.get("age").toString();
                int ageint = Integer.parseInt(myAge);
                agespin.setSelection(ageint - 19);
                myPicUri = documentSnapshot.get("pic").toString();
                if (!myPicUri.equals(nullPic) && !myPicUri.equals(nullPicF)) {
                    deletePic.setChecked(true);
                    deletePic.setEnabled(true);
                } else {
                    deletePic.setChecked(false);
                    deletePic.setEnabled(false);
                }
                mySex = documentSnapshot.get("sex").toString();
                if (mySex.equals("남자")) {
                    radioGroup.check(R.id.change_profile_sexmale);
                } else if (mySex.equals("여자")) {
                    radioGroup.check(R.id.change_profile_sexfemale);
                }
                Glide.with(ChangeProfile.this)
                        .load(myPicUri)
                        .override(300, 300)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loadInPic.setVisibility(View.GONE);
                                load.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loadInPic.setVisibility(View.GONE);
                                load.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .circleCrop()
                        .into(imageView);
                load.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void setYesBtn() {
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                /*경우의 수
                 * 기존 정보랑 바꼈을때
                 * 이미지 변경
                 * 이미지 삭제
                 * 이미지 추가*/
                hideKeyboard(view);
                if (radiomale.isChecked()) {
                    newSex = "남자";
                } else if (radiofemale.isChecked()) {
                    newSex = "여자";
                } else {
                    newSex = "";
                }

                newName = editText.getText().toString();

                int i = Integer.parseInt(newAge);

                if (newName.equals("")) {
                    Toast.makeText(ChangeProfile.this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (newName.matches(".*[ㄱ-ㅎ ㅏ-ㅣ]+.*")) {
                    Toast.makeText(ChangeProfile.this, "올바른 닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (i == 0) {
                    Toast.makeText(ChangeProfile.this, "나이를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (newSex.equals("")) {
                    Toast.makeText(ChangeProfile.this, "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    load.setVisibility(View.VISIBLE);
                    updateUser(newName, newSex, newAge);
                }
            }
        });
    }

    private void updateUser(String name, String sex, String age) {

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("sex", sex);
        user.put("age", age);

        db.collection("users").document(myUid)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (myPicUri.equals(nullPic) || myPicUri.equals(nullPicF)) {//원래 pic 없는 상태에서
                            if (deletePic.isChecked()) {//pic 추가했을때
                                noPicAddNewPic();
                            } else {//pic 추가 안했을때
                                noPicNoNewPic();
                            }
                        } else {//원래 pic 있는 상태에서
                            if (deletePic.isChecked()) {
                                if (imageUri != null) {//pic변경할때
                                    hasPicChangePic();
                                } else {//pic 안바꿀때
                                    hasPicNochangePic();
                                }
                            } else {//pic 삭제할때
                                hasPicDeletePic();
                            }
                        }

//                        if (imageUri != null) {//새로운 이미지 있을때
//                            uploadPic();
//                        } else {//새로운이미지 없을때
//                            ifNoAddPic();
//                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        load.setVisibility(View.GONE);
                        Toast.makeText(ChangeProfile.this, "네트워크문제로 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void noPicAddNewPic() {
        final ProgressDialog pd = new ProgressDialog(ChangeProfile.this);
        pd.setCancelable(false);
        pd.setTitle("이미지 업로드 중...");
        pd.show();

        StorageReference storageReference1 = storageReference.child("images/" + myUid);
        storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) { //storage에 image file 여부 확인
                if (listResult.getItems().size() == 0) {
                    noPicAddNewPic2(myUid + "count1", pd);
                } else {
                    for (StorageReference listResult1 : listResult.getItems()) {
                        if (listResult1.getName().equals(myUid + "count1")) {
                            noPicAddNewPic2(myUid + "count2", pd);
                            storageReference.child("images/" + myUid + "/" + myUid + "count1").delete();
                        } else if (listResult1.getName().equals(myUid + "count2")) {
                            noPicAddNewPic2(myUid + "count1", pd);
                            storageReference.child("images/" + myUid + "/" + myUid + "count2").delete();
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ChangeProfile>>>", "onFailure");
            }
        });
    }

    private void noPicAddNewPic2(String fileName, ProgressDialog pd) {
        StorageReference riversRef = storageReference.child("images/" + myUid + "/" + fileName);
        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //upload pic in firestore
                storageReference.child("images/" + myUid + "/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("ChangeProfile>>>", "test upload onSuccess");
                        newPicUri = uri.toString();
                        updateFirestoreUserPic(newPicUri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });//upload pic in firestore
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();
//                goToMain();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                load.setVisibility(View.GONE);
                Toast.makeText(ChangeProfile.this, "업로드실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Pro: " + (int) progressPercent + "%");
            }
        });
    }

    private void noPicNoNewPic() {
        Map<String, Object> map = new HashMap<>();
        if (newSex.equals("남자")) {
            map.put("pic", nullPic);
        } else {
            map.put("pic", nullPicF);
        }
        db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                f2change();
            }
        });
    }

    private void hasPicChangePic() {
        final ProgressDialog pd = new ProgressDialog(ChangeProfile.this);
        pd.setCancelable(false);
        pd.setTitle("이미지 업로드 중...");
        pd.show();

        StorageReference storageReference1 = storageReference.child("images/" + myUid);
        storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) { //storage에 image file 여부 확인
                for (StorageReference listResult1 : listResult.getItems()) {
                    if (listResult1.getName().equals(myUid + "count1")) {
                        noPicAddNewPic2(myUid + "count2", pd);
                        storageReference.child("images/" + myUid + "/" + myUid + "count1").delete();
                    } else if (listResult1.getName().equals(myUid + "count2")) {
                        noPicAddNewPic2(myUid + "count1", pd);
                        storageReference.child("images/" + myUid + "/" + myUid + "count2").delete();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ChangeProfile>>>", "onFailure");
            }
        });
    }

    private void hasPicNochangePic() {
        Map<String, Object> map = new HashMap<>();
        if (newSex.equals("남자")) {
            map.put("pic", nullPic);
        } else {
            map.put("pic", nullPicF);
        }
        db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                f2change();
            }
        });
    }

    private void hasPicDeletePic() {
        StorageReference storageReference1 = storageReference.child("images/" + myUid);
        storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) { //storage에 image file 여부 확인
                for (StorageReference listResult1 : listResult.getItems()) {
                    if (listResult1.getName().equals(myUid + "count1")) {
                        storageReference.child("images/" + myUid + "/" + myUid + "count1").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String, Object> map = new HashMap<>();
                                if (newSex.equals("남자")) {
                                    map.put("pic", nullPic);
                                } else {
                                    map.put("pic", nullPicF);
                                }
                                db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        f2change();
                                    }
                                });
                            }
                        });
                    } else if (listResult1.getName().equals(myUid + "count2")) {
                        storageReference.child("images/" + myUid + "/" + myUid + "count2").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String, Object> map = new HashMap<>();
                                if (newSex.equals("남자")) {
                                    map.put("pic", nullPic);
                                } else {
                                    map.put("pic", nullPicF);
                                }
                                db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        f2change();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ChangeProfile>>>", "onFailure");
            }
        });
    }


    /**/

    private void uploadPic() {//pic 변경 및 신규등록
        final ProgressDialog pd = new ProgressDialog(ChangeProfile.this);
        pd.setCancelable(false);
        pd.setTitle("이미지 업로드 중...");
        pd.show();

        StorageReference storageReference1 = storageReference.child("images/" + myUid);
        storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) { //storage에 image file 여부 확인
                if (listResult.getItems().size() == 0) {
                    uploadPic2(myUid + "count1", pd);
                } else {
                    for (StorageReference listResult1 : listResult.getItems()) {
                        Log.d("ChangeProfile>>>", "check3");
                        if (listResult1.getName().equals(myUid + "count1")) {
                            uploadPic2(myUid + "count2", pd);
                            storageReference.child("images/" + myUid + "/" + myUid + "count1").delete();
                        } else if (listResult1.getName().equals(myUid + "count2")) {
                            uploadPic2(myUid + "count1", pd);
                            storageReference.child("images/" + myUid + "/" + myUid + "count2").delete();
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ChangeProfile>>>", "onFailure");
            }
        });
    }

    private void ifNoAddPic() {
        if (!deletePic.isChecked()) {//사진 삭제할때
            Log.d("ChangeProfile>>>", "testtest1");
            Map<String, Object> map = new HashMap<>();
            if (newSex.equals("남자")) {
                map.put("pic", nullPic);
            } else {
                map.put("pic", nullPicF);
            }
            db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {//store변경
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("ChangeProfile>>>", "testtest2");
                    StorageReference storageReference1 = storageReference.child("images/" + myUid);
                    storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            if (listResult.getItems().size() != 0) {
                                for (StorageReference listResult1 : listResult.getItems()) {
                                    if (listResult1.getName().equals(myUid + "count1")) {
                                        deleteFileInStorage(myUid + "count1");
                                    } else if (listResult1.getName().equals(myUid + "count2")) {
                                        deleteFileInStorage(myUid + "count2");
                                    }
                                }
                            } else {
                                Log.d("ChangeProfile>>>", "testtest4");
                                f2change();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ChangeProfile>>>", "onFailure");
                        }
                    });
                }
            });
        } else {//원래 pic 없는 상태로 나머지만 변경할때
            Map<String, Object> map = new HashMap<>();
            if (newSex.equals("남자")) {
                map.put("pic", nullPic);
            } else {
                map.put("pic", nullPicF);
            }
            db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    f2change();
                }
            });
        }
    }

    private void deleteFileInStorage(String filename) {//storage에 파일 삭제
        storageReference.child("images/" + myUid + "/" + filename).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                f2change();
            }
        });
    }

    private void setNoBtn() {
        nobtn.setOnClickListener(view -> onBackPressed());
    }

    private void setDeletePicButton() {
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
            imageView.setImageURI(imageUri);//glide 사용하기
            deletePic.setEnabled(true);
            deletePic.setChecked(true);
        }
    }

    private void uploadPic2(String fileName, ProgressDialog pd) {//storage에 image file 업로드
        StorageReference riversRef = storageReference.child("images/" + myUid + "/" + fileName);
        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //upload pic in firestore
                storageReference.child("images/" + myUid + "/" + fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("ChangeProfile>>>", "test upload onSuccess");
                        newPicUri = uri.toString();
                        updateFirestoreUserPic(newPicUri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });//upload pic in firestore
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "이미지 업로드 성공", Snackbar.LENGTH_LONG).show();
//                goToMain();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                load.setVisibility(View.GONE);
                Toast.makeText(ChangeProfile.this, "업로드실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Pro: " + (int) progressPercent + "%");
            }
        });
    }

    private void setagespin() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChangeProfile.this, R.layout.change_profile_spinner_layout, R.id.change_profile_spinner_layout_text, items);
        arrayAdapter.setDropDownViewResource(R.layout.change_profile_spinner_layout);
        agespin.setAdapter(arrayAdapter);
        agespin.setDropDownWidth(WindowManager.LayoutParams.MATCH_PARENT);

        agespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    newAge = "0";
                } else {
                    newAge = Integer.toString(i + 19);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateFirestoreUserPic(String uri) {
        Map<String, Object> userPic = new HashMap<>();
        userPic.put("pic", uri);
        Log.d("ChangeProfile>>>", "test updateFirestoreUser");
        db.collection("users").document(myUid)
                .update(userPic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {//my profile update in storage
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ChangeProfile>>>", "test updateFirestoreUser onSuccess");
                        f2change();
                    }
                });
    }

    private void f2change() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", newName);
        map.put("sex", newSex);
        map.put("age", newAge);

        if (newPicUri != null) {
            map.put("pic", newPicUri);
        } else {
            if (newSex.equals("남자")) {
                map.put("pic", nullPic);
            } else {
                map.put("pic", nullPicF);
            }
        }
        db.collection("f2messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        //f2에 내 글 있을때
                        Log.d("ChangeProfile>>>", "test change f2 success");
                        db.collection("f2messege").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                f3change();
                            }
                        });
                    } else {
                        //f2에 내 글 없을때
                        Log.d("ChangeProfile>>>", "test f2 null");
                        f3change();
                    }
                }
            }
        });
    }

    private void f3change() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", newName);
        map.put("sex", newSex);
        map.put("age", newAge);
        if (newPicUri != null) {
            map.put("pic", newPicUri);
            Log.d("ChangeProfile>>>", "newPicUri have f3");
        } else {
            Log.d("ChangeProfile>>>", "newPicUri null f3");
            if (newSex.equals("남자")) {
                map.put("pic", nullPic);
            } else {
                map.put("pic", nullPicF);
            }
        }
        db.collection("f3messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        //f3에 내 글 있을때
                        db.collection("f3messege").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                goToMain();
                            }
                        });
                    } else {
                        //f3에 내 글 없을때
                        goToMain();
                    }
                }
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
//        SharedPreferences sharedPreferences = getSharedPreferences("willdelete", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("willdelete", willdelete);
//        editor.commit();
    }

    private void hideKeyboard(View v) {
        InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
