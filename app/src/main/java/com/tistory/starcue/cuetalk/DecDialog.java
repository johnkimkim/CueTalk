package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.item.ChatRoomItem;
import com.tistory.starcue.cuetalk.item.F2Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DecDialog {
    private static AlertDialog alertDialog;
    private static TextView title;
    private static RadioGroup radioGroup;
    private static RadioButton radio1, radio2, radio3, radio4;
    private static EditText editText;
    private static Button yesbtn, nobtn;
    private static String category;

    public static void F2DecDialog(Context context, String userUid, String myUid) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
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

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);



        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "스팸";
                } else if (radio3.isChecked()) {
                    category = "폭력";
                } else if (radio4.isChecked()) {
                    category = "기타";
                } else {
                    category = "";
                }

                if (category.equals("")) {
                    Toast.makeText(context, "category 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    Toast.makeText(context, "cuz 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    F2SaveDec(userUid, myUid, editText.getText().toString(), category);
                }
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }
    private static void F2SaveDec(String userUid, String myUid, String cuz, String category) {
        Log.d("DecDialog>>>", "F2dec click ok");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("f2messege").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.get("name").toString();
                String sex = documentSnapshot.get("sex").toString();
                String age = documentSnapshot.get("age").toString();
                String messege = documentSnapshot.get("messege").toString();
                String pic = documentSnapshot.get("pic").toString();
                String time =documentSnapshot.get("time").toString();
                String latitude = documentSnapshot.get("latitude").toString();
                String longitude = documentSnapshot.get("longitude").toString();
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("sex", sex);
                map.put("age", age);
                map.put("messege", messege);
                map.put("pic", pic);
                map.put("time", time);
                map.put("latitude", latitude);
                map.put("longitude", longitude);
                map.put("cuz", cuz);
                map.put("category", category);
                map.put("whodec", myUid);
                map.put("dectime", getTime());
                firestore.collection("f2dec").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DecDialog>>>", "F2dec success");
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }


    public static void F3DecDialog(Context context, String userUid, String myUid) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
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

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);



        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "스팸";
                } else if (radio3.isChecked()) {
                    category = "폭력";
                } else if (radio4.isChecked()) {
                    category = "기타";
                } else {
                    category = "";
                }

                if (category.equals("")) {
                    Toast.makeText(context, "category 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    Toast.makeText(context, "cuz 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    F3SaveDec(userUid, myUid, editText.getText().toString(), category);
                }
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
    private static void F3SaveDec(String userUid, String myUid, String cuz, String category) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("f3messege").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.get("name").toString();
                String sex = documentSnapshot.get("sex").toString();
                String age = documentSnapshot.get("age").toString();
                String messege = documentSnapshot.get("messege").toString();
                String pic = documentSnapshot.get("pic").toString();
                String ppic = documentSnapshot.get("ppic").toString();
                String time =documentSnapshot.get("time").toString();
                String latitude = documentSnapshot.get("latitude").toString();
                String longitude = documentSnapshot.get("longitude").toString();
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("sex", sex);
                map.put("age", age);
                map.put("messege", messege);
                map.put("pic", pic);
                map.put("ppic", ppic);
                map.put("time", time);
                map.put("latitude", latitude);
                map.put("longitude", longitude);
                map.put("cuz", cuz);
                map.put("category", category);
                map.put("whodec", myUid);
                map.put("dectime", getTime());
                firestore.collection("f3dec").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //need to make ppic backup
                        Log.d("DecDialog>>>", "F3dec success");
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }


    public static void ChatRoomDecDialog(Context context, String userUid, String myUid, String where) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
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

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "스팸";
                } else if (radio3.isChecked()) {
                    category = "폭력";
                } else if (radio4.isChecked()) {
                    category = "기타";
                }

                if (category.equals("")) {
                    Toast.makeText(context, "카테고리 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    Toast.makeText(context, "이유 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                ChatRoomSaveDec(context, userUid, myUid, where, editText.getText().toString(), category);
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private static void ChatRoomSaveDec(Context context, String userUid, String myUid, String where, String cuz, String category) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> map = new HashMap<>();
        map.put("userUid", userUid);
        map.put("myUid", myUid);
        map.put("cuz", cuz);
        map.put("category", category);
        reference.child("chatroomdec").child(myUid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.getRef().child("inchat").child(where).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals("messege")) {
                                Map<String, Object> map1 = new HashMap<>();
                                ArrayList<ChatRoomItem> count = new ArrayList<>();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    ChatRoomItem chatRoomItem = snapshot1.getValue(ChatRoomItem.class);
                                    count.add(chatRoomItem);
                                    Log.d("DecDialog>>>", "count listL: " + count.size() + " / snapshot size: " + snapshot.getChildrenCount());
                                    map1.put("messege", snapshot1.child("messege").getValue(String.class));
                                    map1.put("name", snapshot1.child("name").getValue(String.class));
                                    map1.put("time", snapshot1.child("time").getValue(String.class));
                                    reference.child("chatroomdec").child(myUid).push().updateChildren(map1);
                                    if (count.size() == snapshot.getChildrenCount()) {
                                        count.clear();
                                        Toast.makeText(context, "신고완료", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                                break;
                            }
                        }
                    }
                });
            }
        });
    }


    public static void F4ChatRoomDecDialog(Context context) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
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

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);

        F4ChatRoomyesOrNo();
    }
    private static void F4ChatRoomyesOrNo() {
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                F4ChatRoomSaveDec();
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
    private static void F4ChatRoomSaveDec() {

    }

    private static String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss");
        String date = format.format(mDate);
        return date;
    }
}
