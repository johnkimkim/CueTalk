package com.tistory.starcue.cuetalk;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AdressRoom extends AppCompatActivity {

    RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    FirebaseFirestore db;

    String name, sex, age;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_room);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setInit();
        getUser();

    }

    private void setInit() {
        recyclerView = findViewById(R.id.adress_room_recycelrview);
    }

    private void getUser() {
        String uid;
        uid = mAuth.getUid();
        DocumentReference documentReference = db.collection("users")
                .document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name = documentSnapshot.getString("name");
                sex = documentSnapshot.getString("sex");
                age = documentSnapshot.getString("age");
                updateAdressRoom(uid, name, sex, age);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdressRoom.this, "입장실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdressRoom(String uid, String name, String sex, String age) {
        String adress = getIntent().getStringExtra("adress");
        reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" + "/name/", name);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" +  "/sex", sex);
        updateUser.put("/adressRoom/" + adress + "/" + uid + "/" +  "/age/", age);
        reference.updateChildren(updateUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        String s = getIntent().getStringExtra("adress");
        String uid = mAuth.getUid();
        reference.getRef().child("adressRoom").child(s).child(uid).removeValue();
        onBackPressed();
    }
}
