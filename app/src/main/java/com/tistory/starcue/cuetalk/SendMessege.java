package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendMessege {

    AlertDialog alertDialog;
    EditText editText;
    Button okbtn, nobtn;
    Context context;

    private FirebaseFirestore db;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    String myUid;

    private GpsTracker gpsTracker;

    SendMessege(Context context) {
        this.context = context;
    }

    public void setSendMessegeDialog(Context context) {
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myUid = mAuth.getUid();

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.send_messege_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

        editText = layout.findViewById(R.id.send_messege_edit);
        okbtn = layout.findViewById(R.id.send_messege_ok);
        nobtn = layout.findViewById(R.id.send_messege_no);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference = db.collection("users").document(myUid);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        gpsTracker = new GpsTracker(context);
                        Double latitude = gpsTracker.getLatitude();//위도
                        Double longitude = gpsTracker.getLongitude();//경도
                        String latitudeS = String.valueOf(latitude);
                        String longitudeS = String.valueOf(longitude);

                        String pic, name, sex, age;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void firstSendMessege(String messege, String myUid, String userUid, String uri, String name, String sex, String age, String latitude, String longitude) {

        reference.child("messege").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount() + 1;
                String countString = Integer.toString(count);

                String uuid = UUID.randomUUID().toString();

                Map<String, Object> messegeMap = new HashMap<>();

                messegeMap.put("/messege/" + myUid + "/" + "pic/", uri);
                messegeMap.put("/messege/" + myUid + "/" + "name/", name);
                messegeMap.put("/messege/" + myUid + "/" + "sex/", sex);
                messegeMap.put("/messege/" + myUid + "/" + "age/", age);
                messegeMap.put("/messege/" + myUid + "/" + "latitude/", latitude);
                messegeMap.put("/messege/" + myUid + "/" + "longitude/", longitude);

                messegeMap.put("/messege/" + userUid + "/" + "pic/", uri);
                messegeMap.put("/messege/" + userUid + "/" + "name/", name);
                messegeMap.put("/messege/" + userUid + "/" + "sex/", sex);
                messegeMap.put("/messege/" + userUid + "/" + "age/", age);
                messegeMap.put("/messege/" + userUid + "/" + "latitude/", latitude);
                messegeMap.put("/messege/" + userUid + "/" + "longitude/", longitude);
                reference.updateChildren(messegeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "네트워크 문제로 메시지 전송에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void sendMessege() {
//        reference.getRef().child("messege").child();
    }

}
