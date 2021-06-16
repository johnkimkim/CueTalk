package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.item.ChatRoomItem;
import com.tistory.starcue.cuetalk.item.F4ChatRoomItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class DecDialog {
    public static AlertDialog alertDialog;
    private static TextView count;
    private static RadioGroup radioGroup;
    private static RadioButton radio1, radio2, radio3, radio4, radio5, radio6, radio7;
    private static EditText editText;
    private static Button yesbtn, nobtn;
    private static RelativeLayout dec1, dec2;
    private static Button dec2btn;
    private static RelativeLayout progressBar;
    private static String category;
    private static LinearLayout decmain;

    public static void F2DecDialog(Context context, String userUid, String myUid, Activity activity) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.8);
        window.setLayout(x, y);

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        radio5 = layout.findViewById(R.id.radio5);
        radio6 = layout.findViewById(R.id.radio6);
        radio7 = layout.findViewById(R.id.radio7);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        count = layout.findViewById(R.id.dec_dialog_edit_count);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);
        dec1 = layout.findViewById(R.id.dec_1);
        dec2 = layout.findViewById(R.id.dec_2);
        dec2btn = layout.findViewById(R.id.dec_2_okbtn);
        progressBar = layout.findViewById(R.id.dec_dialog_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        decmain = layout.findViewById(R.id.dec_main);

        dec2.setVisibility(View.INVISIBLE);

        edittextcountset(editText, count);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.setCancelable(false);
                progressBar.setVisibility(View.VISIBLE);
                hideKB(context, decmain);
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "폭력";
                } else if (radio3.isChecked()) {
                    category = "자해자살";
                } else if (radio4.isChecked()) {
                    category = "부적절홍보";
                } else if (radio5.isChecked()) {
                    category = "기타권리";
                } else if (radio6.isChecked()) {
                    category = "개인정보";
                } else if (radio7.isChecked()) {
                    category = "기타";
                }


                if (category.equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 유형을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 사유을 간단히 적어주세요", Toast.LENGTH_SHORT).show();

                } else {
                    F2SaveDec(context, userUid, myUid, editText.getText().toString(), category);
                }
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        dec2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    private static void F2SaveDec(Context context, String userUid, String myUid, String cuz, String category) {
        Animation out = AnimationUtils.loadAnimation(context, R.anim.dec_fadeout);
        Animation in = AnimationUtils.loadAnimation(context, R.anim.dec_fadein);
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
                String time = documentSnapshot.get("time").toString();
                String latitude = documentSnapshot.get("latitude").toString();
                String longitude = documentSnapshot.get("longitude").toString();
                Map<String, Object> map = new HashMap<>();
                map.put("uid", userUid);
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
                        dec1.startAnimation(out);
                        out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dec1.setVisibility(View.INVISIBLE);
                                dec2.startAnimation(in);
                                in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        dec2.setVisibility(View.VISIBLE);
                                        alertDialog.setCancelable(true);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
            }
        });
    }

    public static void F3DecDialog(Context context, String userUid, String myUid, Activity activity) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.8);
        window.setLayout(x, y);

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        radio5 = layout.findViewById(R.id.radio5);
        radio6 = layout.findViewById(R.id.radio6);
        radio7 = layout.findViewById(R.id.radio7);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        count = layout.findViewById(R.id.dec_dialog_edit_count);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);
        dec1 = layout.findViewById(R.id.dec_1);
        dec2 = layout.findViewById(R.id.dec_2);
        dec2btn = layout.findViewById(R.id.dec_2_okbtn);
        progressBar = layout.findViewById(R.id.dec_dialog_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        decmain = layout.findViewById(R.id.dec_main);

        dec2.setVisibility(View.INVISIBLE);

        edittextcountset(editText, count);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setCancelable(false);
                progressBar.setVisibility(View.VISIBLE);
                hideKB(context, decmain);
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "폭력";
                } else if (radio3.isChecked()) {
                    category = "자해자살";
                } else if (radio4.isChecked()) {
                    category = "부적절홍보";
                } else if (radio5.isChecked()) {
                    category = "기타권리";
                } else if (radio6.isChecked()) {
                    category = "개인정보";
                } else if (radio7.isChecked()) {
                    category = "기타";
                }

                if (category.equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 유형을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 사유을 간단히 적어주세요", Toast.LENGTH_SHORT).show();
                } else {
                    F3SaveDec(context, userUid, myUid, editText.getText().toString(), category);
                }
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        dec2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private static void F3SaveDec(Context context, String userUid, String myUid, String cuz, String category) {
        Animation out = AnimationUtils.loadAnimation(context, R.anim.dec_fadeout);
        Animation in = AnimationUtils.loadAnimation(context, R.anim.dec_fadein);
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
                String time = documentSnapshot.get("time").toString();
                String latitude = documentSnapshot.get("latitude").toString();
                String longitude = documentSnapshot.get("longitude").toString();
                Map<String, Object> map = new HashMap<>();
                map.put("uid", userUid);
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
                        dec1.startAnimation(out);
                        out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dec1.setVisibility(View.INVISIBLE);
                                dec2.startAnimation(in);
                                in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        dec2.setVisibility(View.VISIBLE);
                                        alertDialog.setCancelable(true);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
            }
        });
    }

    public static void ChatRoomDecDialog(Context context, String userUid, String myUid, String where, Activity activity) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.8);
        window.setLayout(x, y);

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        radio5 = layout.findViewById(R.id.radio5);
        radio6 = layout.findViewById(R.id.radio6);
        radio7 = layout.findViewById(R.id.radio7);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        count = layout.findViewById(R.id.dec_dialog_edit_count);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);
        dec1 = layout.findViewById(R.id.dec_1);
        dec2 = layout.findViewById(R.id.dec_2);
        dec2btn = layout.findViewById(R.id.dec_2_okbtn);
        progressBar = layout.findViewById(R.id.dec_dialog_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        decmain = layout.findViewById(R.id.dec_main);

        dec2.setVisibility(View.INVISIBLE);

        edittextcountset(editText, count);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setCancelable(false);
                progressBar.setVisibility(View.VISIBLE);
                hideKB(context, decmain);
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "폭력";
                } else if (radio3.isChecked()) {
                    category = "자해자살";
                } else if (radio4.isChecked()) {
                    category = "부적절홍보";
                } else if (radio5.isChecked()) {
                    category = "기타권리";
                } else if (radio6.isChecked()) {
                    category = "개인정보";
                } else if (radio7.isChecked()) {
                    category = "기타";
                }

                if (category.equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 유형을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 사유을 간단히 적어주세요", Toast.LENGTH_SHORT).show();
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

        dec2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private static void ChatRoomSaveDec(Context context, String userUid, String myUid, String where, String cuz, String category) {
        Animation out = AnimationUtils.loadAnimation(context, R.anim.dec_fadeout);
        Animation in = AnimationUtils.loadAnimation(context, R.anim.dec_fadein);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> map = new HashMap<>();
        map.put("userUid", userUid);
        map.put("myUid", myUid);
        map.put("cuz", cuz);
        map.put("category", category);
        reference.child("chatroomdec").child(where).child(myUid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    if (snapshot1.child("uid").getValue() != null) {
                                        Log.d("DecDialog>>>", "count listL: " + count.size() + " / snapshot size: " + snapshot.getChildrenCount());
                                        map1.put("messege", snapshot1.child("messege").getValue(String.class));
                                        map1.put("uid", snapshot1.child("uid").getValue(String.class));
                                        map1.put("time", snapshot1.child("time").getValue(String.class));
                                        map1.put("uri", snapshot1.child("uri").getValue(String.class));
                                    }

                                    reference.child("chatroomdec").child(where).child("messege").push().updateChildren(map1);
                                    if (count.size() + 1 == snapshot.getChildrenCount()) {
                                        count.clear();
                                        dec1.startAnimation(out);
                                        out.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                dec1.setVisibility(View.INVISIBLE);
                                                dec2.startAnimation(in);
                                                in.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        dec2.setVisibility(View.VISIBLE);
                                                        alertDialog.setCancelable(true);
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
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



    public static void F4ChatRoomDecDialog(Context context, String userUid, String myUid, String getroomname, Activity activity) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.8);
        window.setLayout(x, y);

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        radio5 = layout.findViewById(R.id.radio5);
        radio6 = layout.findViewById(R.id.radio6);
        radio7 = layout.findViewById(R.id.radio7);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        count = layout.findViewById(R.id.dec_dialog_edit_count);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);
        dec1 = layout.findViewById(R.id.dec_1);
        dec2 = layout.findViewById(R.id.dec_2);
        dec2btn = layout.findViewById(R.id.dec_2_okbtn);
        progressBar = layout.findViewById(R.id.dec_dialog_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        decmain = layout.findViewById(R.id.dec_main);

        dec2.setVisibility(View.INVISIBLE);

        edittextcountset(editText, count);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setCancelable(false);
                progressBar.setVisibility(View.VISIBLE);
                hideKB(context, decmain);
                if (radio1.isChecked()) {
                    category = "음란";
                } else if (radio2.isChecked()) {
                    category = "폭력";
                } else if (radio3.isChecked()) {
                    category = "자해자살";
                } else if (radio4.isChecked()) {
                    category = "부적절홍보";
                } else if (radio5.isChecked()) {
                    category = "기타권리";
                } else if (radio6.isChecked()) {
                    category = "개인정보";
                } else if (radio7.isChecked()) {
                    category = "기타";
                }

                if (category.equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 유형을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (editText.getText().toString().equals("")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "신고 사유을 간단히 적어주세요", Toast.LENGTH_SHORT).show();
                }
                F4ChatRoomSaveDec(context, userUid, myUid, editText.getText().toString(), category, getroomname);
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        dec2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private static void F4ChatRoomSaveDec(Context context, String userUid, String myUid, String cuz, String category, String getroomname) {
        Animation out = AnimationUtils.loadAnimation(context, R.anim.dec_fadeout);
        Animation in = AnimationUtils.loadAnimation(context, R.anim.dec_fadein);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> map = new HashMap<>();
        map.put("userUid", userUid);
        map.put("myUid", myUid);
        map.put("cuz", cuz);
        map.put("category", category);
        reference.child("messegedec").child(getroomname).child(myUid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.getRef().child("messege").child(getroomname).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                            if (snapshot1.getKey().equals("msg")) {
                                Map<String, Object> map1 = new HashMap<>();
                                ArrayList<F4ChatRoomItem> count = new ArrayList<>();
                                for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                    F4ChatRoomItem f4ChatRoomItem = snapshot2.getValue(F4ChatRoomItem.class);
                                    count.add(f4ChatRoomItem);
                                    map1.put("messege", snapshot2.child("messege").getValue(String.class));
                                    map1.put("uid", snapshot2.child("uid").getValue(String.class));
                                    map1.put("name", snapshot2.child("name").getValue(String.class));
                                    map1.put("time", snapshot2.child("time").getValue(String.class));
                                    map1.put("uri", snapshot2.child("uri").getValue(String.class));
                                    reference.child("messegedec").child(getroomname).child("messege").push().updateChildren(map1);
                                    Log.d("DecDialog>>>", "count size: " + count.size() + " and snapshot size: " + snapshot1.getChildrenCount());
                                    if (count.size() == snapshot1.getChildrenCount()) {
                                        Log.d("DecDialog>>>", "onSuccess");
                                        count.clear();
                                        dec1.startAnimation(out);
                                        out.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                dec1.setVisibility(View.INVISIBLE);
                                                dec2.startAnimation(in);
                                                in.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        dec2.setVisibility(View.VISIBLE);
                                                        alertDialog.setCancelable(true);
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    private static String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    private static void hideKB(Context context, LinearLayout decmain) {
        InputMethodManager manager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(decmain.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static void edittextcountset(EditText edit, TextView count) {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = edit.getText().toString();
                count.setText(input.length() + " / 20");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit.setOnKeyListener(new View.OnKeyListener() {//줄바꿈 하나만 허용
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (edit.getText().toString().contains("\n")) {
                    if (i == keyEvent.KEYCODE_ENTER) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

}
