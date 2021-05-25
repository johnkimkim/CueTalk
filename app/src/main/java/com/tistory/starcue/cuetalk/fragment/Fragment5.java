package com.tistory.starcue.cuetalk.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.ChangeProfile;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;
import com.tistory.starcue.cuetalk.SplashActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Fragment5 extends Fragment {

    ImageView pic;
    TextView name, age, sex;
    Button reset, logout, deleteUser;
    DatabaseHandler databaseHandler;
    SwitchCompat switchCompat;
    private SQLiteDatabase sqLiteDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    String myUid;

    String myUri;

    private AlertDialog alertDialog;

    Button logoutDialogOkBtn, logoutDialogNoBtn;
    Button deleteUserDialogOkBtn, deleteUserDialogNoBtn;
    private ProgressBar progressBar;

    private TextView appversion;
    private RelativeLayout updatelayout;
    private Button updatebtn;

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

    public Fragment5() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment5, container, false);

        Log.d("Fragment5>>>", "start onCreate");

        name = rootView.findViewById(R.id.profilename);
        age = rootView.findViewById(R.id.profileage);
        sex = rootView.findViewById(R.id.profilesex);
        reset = rootView.findViewById(R.id.resetprofile);
        logout = rootView.findViewById(R.id.logout_btn);
        pic = rootView.findViewById(R.id.fragment5image);
        appversion = rootView.findViewById(R.id.nowversiontextview);
        updatelayout = rootView.findViewById(R.id.f5updatelayout);
        updatebtn = rootView.findViewById(R.id.updatebtn);
        setOnClickPic();
        deleteUser = rootView.findViewById(R.id.fragment5_delete_user);
        switchCompat = rootView.findViewById(R.id.f5switch);

        setFirebse();
        setdb();
        setSwitch();
        setView();
        reset_profile();
        logoutBtn();
        deleteUser();


        return rootView;
    }

    private void checkAppVersion() {
        String nowVersion = getAppVersion();
        appversion.setText(nowVersion);
        db.collection("version").document("version").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String messege = documentSnapshot.get("f5messege").toString();
                String url = documentSnapshot.get("url").toString();
                if (!documentSnapshot.get("version").toString().equals(nowVersion)) {
                    MainActivity.btn5count.setVisibility(View.VISIBLE);
                    MainActivity.btn5count.setText("1");
                    updatelayout.setVisibility(View.VISIBLE);
                } else {
                    MainActivity.btn5count.setVisibility(View.INVISIBLE);
                    updatelayout.setVisibility(View.GONE);
                }
                setOnClickUpdateLayout(messege, url);
            }
        });
    }

    private void setOnClickUpdateLayout(String messege, String url) {
        updatebtn.setText(messege);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }
        });
    }

    private void setOnClickPic() {
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeePicDialog.seePicDialog(getActivity(), myUri);
            }
        });
    }

    private void setFirebse() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        myUid = mAuth.getUid();

        checkAppVersion();
    }

    private void setdb() {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();

//        Cursor cursorid = sqLiteDatabase.rawQuery("select * from id where _rowid_ = 1", null);
//        cursorid.moveToFirst();
//        int i = cursorid.getCount();
//        if (i != 0) {
//            Cursor cursor = sqLiteDatabase.rawQuery("select * from id where _rowid_ = 1", null);
//            cursor.moveToFirst();
//            name.setText(cursor.getString(0));
//            age.setText(cursor.getString(1));
//            sex.setText(cursor.getString(2));
//            cursor.close();
//        }


    }

    private void setSwitch() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String s = documentSnapshot.get("notify").toString();
                if (s.equals("on")) {
                    switchCompat.setChecked(true);
                } else {
                    switchCompat.setChecked(false);
                }
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainActivity.loading.setVisibility(View.VISIBLE);
                Map<String, Object> map = new HashMap<>();
                if (b) {
                    map.put("notify", "on");
                    db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            MainActivity.loading.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getActivity(), "네트워크 오류로 실패있습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            switchCompat.setChecked(false);
                        }
                    });
                } else {
                    map.put("notify", "off");
                    db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            MainActivity.loading.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getActivity(), "네트워크 오류로 실패있습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            switchCompat.setChecked(true);
                        }
                    });
                }
            }
        });
    }

    private void setView() {
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.get("name").toString());
                sex.setText(documentSnapshot.get("sex").toString());
                age.setText(documentSnapshot.get("age").toString());

                myUri = documentSnapshot.get("pic").toString();
                Glide.with(getActivity()).load(myUri).override(300, 300).placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground)
                        .circleCrop().into(pic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void reset_profile() {
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangeProfile.class);
                startActivity(intent);
            }
        });
    }

    private void logoutBtn() {
        logout.setOnClickListener(view -> {
            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) vi.inflate(R.layout.logout_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(layout);
            alertDialog = builder.create();

            alertDialog.show();
            //set size
            WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);

            logoutDialogOkBtn = layout.findViewById(R.id.logout_dialog_okbtn);
            logoutDialogNoBtn = layout.findViewById(R.id.logout_dialog_cancelbtn);
            progressBar = layout.findViewById(R.id.logout_dialog_progress_bar);

            logoutDialogNoBtn.setOnClickListener(view1 -> alertDialog.dismiss());

            logoutDialogOkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.setCancelable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    logout.setEnabled(false);
                    logoutDialogOkBtn.setEnabled(false);
                    logoutDialogNoBtn.setEnabled(false);
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), SplashActivity.class));
                }
            });
        });
    }

    private void deleteUser() {
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout = (LinearLayout) vi.inflate(R.layout.delete_user_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(layout);
                alertDialog = builder.create();

                alertDialog.show();
                //set size
                WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                alertDialog.getWindow().setAttributes(layoutParams);

                deleteUserDialogOkBtn = layout.findViewById(R.id.delete_user_dialog_okbtn);
                deleteUserDialogNoBtn = layout.findViewById(R.id.delete_user_dialog_cancelbtn);
                progressBar = layout.findViewById(R.id.delete_user_dialog_progress_bar);

                deleteUserDialogNoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                deleteUserDialogOkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.setCancelable(false);
                        progressBar.setVisibility(View.VISIBLE);
                        deleteUser.setEnabled(false);
                        deleteUserDialogOkBtn.setEnabled(false);
                        deleteUserDialogNoBtn.setEnabled(false);
//                        deleteDocument();
                        saveOneMonthUser();
                    }
                });
            }
        });
    }

    private void saveOneMonthUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String myPhoneNumber = firebaseUser.getPhoneNumber();
        Map<String, Object> map = new HashMap<>();
        map.put("phonenumber", myPhoneNumber);
        map.put("uid", myUid);
        db.collection("deleteUser").document(myUid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deletef2();
            }
        });
    }

    private void deletef2() {
        //delete f2
        db.collection("f2messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        db.collection("f2messege").document(myUid).delete();
                    }
                    deletef3();
                }
            }
        });

    }

    private void deletef3() {
        db.collection("f3messege").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        db.collection("f3messege").document(myUid).delete();
                    }
                    deleteUsersInFirestore();
                }
            }
        });
    }

    private void deleteUsersInFirestore() {
        db.collection("users").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lastDeleteUser();
            }
        });
    }

    private void lastDeleteUser() {
        mCurrentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseHandler.uniquedelete();
                alertDialog.dismiss();
                startActivity(new Intent(getActivity(), SplashActivity.class));
            }
        });
//        mAuth.signOut();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAppVersion();
    }

    public String getAppVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(Fragment5.this.getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionName;
    }

}