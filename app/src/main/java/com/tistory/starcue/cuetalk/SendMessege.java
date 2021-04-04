package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendMessege {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";

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

    public void setSendMessegeDialog(Context context, String userUid) {
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

                        String edit = editText.getText().toString();

                        gpsTracker = new GpsTracker(context);
                        Double latitude = gpsTracker.getLatitude();//위도
                        Double longitude = gpsTracker.getLongitude();//경도
                        String latitudeS = String.valueOf(latitude);
                        String longitudeS = String.valueOf(longitude);

                        String myPic, myName, mySex, myAge;
                        myPic = documentSnapshot.getString("pic");
                        myName = documentSnapshot.getString("name");
                        mySex = documentSnapshot.getString("sex");
                        Log.d("SendMessege>>>", "get my Sex: " + mySex);
                        myAge = documentSnapshot.getString("age");
                        firstSendMessege(edit, myUid, userUid, myPic, myName, mySex, myAge, latitudeS, longitudeS);

                        alertDialog.dismiss();
                        Toast.makeText(context, "메시지 전송 성공", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "네트워크 오류로 인해 메시지 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    public void firstSendMessege(String messege, String myUid, String userUid, String pic, String name, String sex, String age, String latitude, String longitude) {

        reference.child("messege").child(myUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount() + 1;
                String countString = Integer.toString(count);

                Map<String, Object> messegeMap = new HashMap<>();
                messegeMap.put("/messege/" + myUid+userUid + "/msg/" + "1/" + "messege/", "dlqwkddhksfycjtaptlwl");
                messegeMap.put("/messege/" + myUid+userUid + "/msg/" + "1/" + "time/", "dlqwkddhksfycjtaptlwl");

                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/uid/", myUid);
                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/name/", name);
                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/sex/", sex);
                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/age/", age);
                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/latitude/", latitude);
                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/longitude/", longitude);
                messegeMap.put("/messege/" + myUid+userUid + "/" + myUid + "/ischat/", "1");

                if (pic != null) {
                    messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/pic/", pic);
                    messegeMap.put("/messege/" + myUid + userUid + "/msg/" + "2" + "/pic/", pic);
                    Log.d("SendMessege>>>", "if pic null");
                } else {
                    if (sex.equals("남자")) {
                        messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/pic/", nullPic);
                        messegeMap.put("/messege/" + myUid + userUid + "/msg/" + "2" + "/pic/", nullPic);
                        Log.d("SendMessege>>>", "if pic 남자: " + name);
                    } else {
                        messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/pic/", nullPicF);
                        messegeMap.put("/messege/" + myUid + userUid + "/msg/" + "2" + "/pic/", nullPicF);
                        Log.d("SendMessege>>>", "if pic 여자: " + name);
                    }
                }

                messegeMap.put("/messege/" + myUid+userUid + "/msg/" + "2" + "/messege/", messege);
                messegeMap.put("/messege/" + myUid+userUid + "/msg/" + "2" + "/name/", name);
                messegeMap.put("/messege/" + myUid+userUid + "/msg/" + "2" + "/time/", getTime());
                messegeMap.put("/messege/" + myUid+userUid + "/msg/" + "2" + "/read/", "1");


                messegeMap.put("/messege/" + myUid+userUid + "/lastmsg" + myUid+userUid + "/lastmessege/", messege);
                messegeMap.put("/messege/" + myUid+userUid + "/lastmsg" + myUid+userUid + "/lasttime/", getTime());


                reference.updateChildren(messegeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUserInRoom(userUid, myUid+userUid);
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

    public void updateUserInRoom(String userUid, String key) {
        DocumentReference documentReference = db.collection("users").document(userUid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = documentSnapshot.getString("name");
                String userSex = documentSnapshot.getString("sex");
                Log.d("SendMessege>>>", "get user sex: " + userSex);
                String userAge = documentSnapshot.getString("age");
                String userPic = documentSnapshot.getString("pic");
                Double latitude = (Double) documentSnapshot.get("latitude");
                Double longitude = (Double) documentSnapshot.get("longitude");
                String latitudeS = String.valueOf(latitude);
                String longitudeS = String.valueOf(longitude);

                Map<String, Object> userUpdate = new HashMap<>();
                userUpdate.put("/messege/" + key + "/" + userUid + "/uid/", userUid);
                userUpdate.put("/messege/" + key + "/" + userUid + "/name/", userName);
                userUpdate.put("/messege/" + key + "/" + userUid + "/sex/", userSex);
                userUpdate.put("/messege/" + key + "/" + userUid + "/age/", userAge);
                userUpdate.put("/messege/" + key + "/" + userUid + "/ischat/", "1");
                if (userPic != null) {
                    userUpdate.put("/messege/" + key + "/" + userUid + "/pic/", userPic);
                } else {
                    if (userSex.equals("남자")) {
                        userUpdate.put("/messege/" + key + "/" + userUid + "/pic/", nullPic);
                    } else {
                        userUpdate.put("/messege/" + key + "/" + userUid + "/pic/", nullPicF);
                    }
                }
                userUpdate.put("/messege/" + key + "/" + userUid + "/latitude/", latitudeS);
                userUpdate.put("/messege/" + key + "/" + userUid + "/longitude/", longitudeS);
                reference.updateChildren(userUpdate);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void sendMessege(DatabaseReference reference, String key, String myUid, String pic, String name, String sex, String age, String latitude, String longitude) {
        Map<String, Object> messegeMap = new HashMap<>();

        messegeMap.put("/messege/" + key + "/" + myUid + "/uid/", myUid);
        messegeMap.put("/messege/" + key + "/" + myUid + "/name/", name);
        messegeMap.put("/messege/" + key + "/" + myUid + "/sex/", sex);
        messegeMap.put("/messege/" + key + "/" + myUid + "/age/", age);
        messegeMap.put("/messege/" + key + "/" + myUid + "/latitude/", latitude);
        messegeMap.put("/messege/" + key + "/" + myUid + "/longitude/", longitude);
        if (pic != null) {
            messegeMap.put("/messege/" + key + "/" + myUid + "/pic/", pic);
        }
        reference.updateChildren(messegeMap);
//        reference.getRef().child("messege").child(key).child("msg").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                int count = (int) dataSnapshot.getChildrenCount() + 1;
//
//                Map<String, Object> messegeMap = new HashMap<>();
//
//                messegeMap.put("/messege/" + key + "/" + myUid + "/name/", name);
//                messegeMap.put("/messege/" + key + "/" + myUid + "/sex/", sex);
//                messegeMap.put("/messege/" + key + "/" + myUid + "/age/", age);
//                messegeMap.put("/messege/" + key + "/" + myUid + "/latitude/", latitude);
//                messegeMap.put("/messege/" + key + "/" + myUid + "/longitude/", longitude);
//                if (uri != null) {
//                    messegeMap.put("/messege/" + key + "/" + myUid + "/uri/", uri);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        String date = format.format(mDate);
        return date;
    }

}
