package com.tistory.starcue.cuetalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Fragment5 extends Fragment {

    ImageView pic;
    TextView name, age, sex;
    Button reset, logout, deleteUser;
    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    public Fragment5() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment5, container, false);

        name = rootView.findViewById(R.id.profilename);
        age = rootView.findViewById(R.id.profileage);
        sex = rootView.findViewById(R.id.profilesex);
        reset = rootView.findViewById(R.id.resetprofile);
        logout = rootView.findViewById(R.id.logout_btn);
        pic = rootView.findViewById(R.id.fragment5image);
        deleteUser = rootView.findViewById(R.id.fragment5_delete_user);

        setFirebse();
        setPic();
        setdb();
        reset_profile();
        logoutBtn();
        deleteUser();

        return rootView;
    }

    private void setFirebse() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void setdb() {
        databaseHandler.setDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        Cursor cursorid = sqLiteDatabase.rawQuery("select * from id where _rowid_ = 1", null);
        cursorid.moveToFirst();
        int i = cursorid.getCount();
        if (i != 0) {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from id where _rowid_ = 1", null);
            cursor.moveToFirst();
            name.setText(cursor.getString(0));
            age.setText(cursor.getString(1));
            sex.setText(cursor.getString(2));
            cursor.close();
        }


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
            mAuth.signOut();
            startActivity(new Intent(getActivity(), PhoneNumber.class));
        });
    }

    private void deleteUser() {
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDocument();
                deleteImage();
                mAuth.signOut();
                mCurrentUser.delete();
                databaseHandler.dbdelete();
                startActivity(new Intent(getActivity(), Splash.class));
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
                Glide.with(getActivity())
                        .load(uri.toString())
                        .override(600, 600)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground)
                        .circleCrop()
                        .into(pic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fragment5>>>", "load pic fail");
            }
        });
    }

    private void deleteDocument() {
//        String dsa = mCurrentUser.getUid();
        String unique = mAuth.getUid();
        db.collection("users").document(unique)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "document삭제 완료", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "document삭제 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteImage() {
        String uid = mAuth.getUid();
        StorageReference storageRef = storage.getReference().child("images/" + uid);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}