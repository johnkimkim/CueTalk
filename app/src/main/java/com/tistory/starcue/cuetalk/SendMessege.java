package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SendMessege {

    private static final String serverKey = " AAAAqHwsNuA:APA91bEhOL4uoOR3d0Ys1qbFflQelzTPwaxBFLRI5Prx7tCor-KoivdXAKpLjz_PDlFctKT1iVPhwgXcPq8ioYh_TvaqSHPPjhCc98M5z7g9i3reg8Cqjbn-J0LbXXi0pSeMJa8KuYRk";
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";

    AlertDialog alertDialog;
    EditText editText;
    Button okbtn, nobtn;
    Context context;

    private FirebaseFirestore db;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    String myUid;
    ImageView userPic;
    TextView tuserName, tuserSex, tuserAge;
    CircularDotsLoader picload;

    private GpsTracker gpsTracker;

    public SendMessege(Context context) {
        this.context = context;
    }

    public void setSendMessegeDialog(Context context, String userUid, Activity activity) {
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
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.6);
        window.setLayout(x, y);

        editText = layout.findViewById(R.id.send_messege_edit);
        okbtn = layout.findViewById(R.id.send_messege_ok);
        nobtn = layout.findViewById(R.id.send_messege_no);
        userPic = layout.findViewById(R.id.send_messege_user_pic);
        tuserName = layout.findViewById(R.id.send_messege_user_name);
        tuserSex = layout.findViewById(R.id.send_messege_user_sex);
        tuserAge = layout.findViewById(R.id.send_messege_user_age);
        picload = layout.findViewById(R.id.send_messege_user_pic_load);
        picload.bringToFront();
        picload.setVisibility(View.VISIBLE);

        db.collection("users").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(context).load(documentSnapshot.get("pic").toString())
                        .override(150, 150)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                picload.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                picload.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .circleCrop()
                        .into(userPic);
                tuserName.setText(documentSnapshot.get("name").toString());
                if (documentSnapshot.get("sex").toString().equals("남자")) {
                    tuserSex.setTextColor(context.getResources().getColor(R.color.male));
                    tuserSex.setText(documentSnapshot.get("sex").toString());
                } else {
                    tuserSex.setTextColor(context.getResources().getColor(R.color.female));
                    tuserSex.setText(documentSnapshot.get("sex").toString());
                }
                tuserAge.setText(documentSnapshot.get("age").toString());
            }
        });

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(context, "메시지를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.loading.setVisibility(View.VISIBLE);
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

                Map<String, Object> messegeMap = new HashMap<>();
                Map<String, Object> fmsg = new HashMap<>();
                Map<String, Object> firstmsg = new HashMap<>();

                fmsg.put("messege/", "dlqwkddhksfycjtaptlwl");
                fmsg.put("time/", "dlqwkddhksfycjtaptlwl");
                fmsg.put("uid/", "dlqwkddhksfycjtaptlwl");
                fmsg.put("read/", "dlqwkddhksfycjtaptlwl");

                messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/uid/", myUid);
                messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/ischat/", "1");
                messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/state/", "1");
                messegeMap.put("/myroom/" + myUid + "/" + myUid + userUid, myUid + userUid);

//                messegeMap.put("/messege/" + myUid + userUid + "/" + myUid + "/pic/", pic);

                firstmsg.put("messege", messege);
//                firstmsg.put("name", name);
                firstmsg.put("time", getTime());
                firstmsg.put("read", "1");
                firstmsg.put("uid", myUid);

                messegeMap.put("/messege/" + myUid + userUid + "/lastmsg" + myUid + userUid + "/lastmessege/", messege);
                messegeMap.put("/messege/" + myUid + userUid + "/lastmsg" + myUid + userUid + "/lasttime/", getTime());


                updateUserInRoom(messegeMap, userUid, myUid + userUid, fmsg, firstmsg);

                String roomkey = myUid + userUid;
                sendNotify(userUid, messege, name, roomkey, pic);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "네트워크 문제로 메시지 전송에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                reference.getRef().child("messege").child(myUid + userUid).removeValue();
            }
        });

    }

    public void updateUserInRoom(Map<String, Object> messegeMap, String userUid, String key, Map<String, Object> fmsg, Map<String, Object> firstmsg) {
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
                messegeMap.put("/messege/" + key + "/" + userUid + "/uid/", userUid);
//                messegeMap.put("/messege/" + key + "/" + userUid + "/name/", userName);
//                messegeMap.put("/messege/" + key + "/" + userUid + "/sex/", userSex);
//                messegeMap.put("/messege/" + key + "/" + userUid + "/age/", userAge);
                messegeMap.put("/messege/" + key + "/" + userUid + "/ischat/", "1");
                messegeMap.put("/messege/" + key + "/" + userUid + "/state/", "1");
                messegeMap.put("/myroom/" + userUid + "/" + key + "/", key);
//                messegeMap.put("/messege/" + key + "/" + userUid + "/pic/", userPic);

//                messegeMap.put("/messege/" + key + "/" + userUid + "/latitude/", latitudeS);
//                messegeMap.put("/messege/" + key + "/" + userUid + "/longitude/", longitudeS);
                reference.updateChildren(messegeMap);
                reference.child("messege").child(myUid + userUid).child("msg").push().updateChildren(fmsg);
                reference.child("messege").child(myUid + userUid).child("msg").push().updateChildren(firstmsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.loading.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "네트워크 문제로 메시지 전송에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        reference.getRef().child("messege").child(key).removeValue();
                    }
                });
//                reference.updateChildren(userUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        reference.child("messege").child(myUid+userUid).child("msg").push().updateChildren(fmsg);
//                        reference.child("messege").child(myUid+userUid).child("msg").push().updateChildren(firstmsg).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                MainActivity.loading.setVisibility(View.GONE);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(context, "네트워크 문제로 메시지 전송에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
//                                reference.getRef().child("messege").child(myUid+userUid).removeValue();
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "네트워크 문제로 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                        reference.getRef().child("messege").child(myUid+userUid).removeValue();
//                    }
//                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "네트워크 문제로 메시지 전송에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                reference.getRef().child("messege").child(myUid + userUid).removeValue();
            }
        });

    }

    public void sendMessege(DatabaseReference reference, String key, String myUid, String pic, String name, String sex, String age, String latitude, String longitude) {
        Map<String, Object> messegeMap = new HashMap<>();

        messegeMap.put("/messege/" + key + "/" + myUid + "/uid/", myUid);
//        messegeMap.put("/messege/" + key + "/" + myUid + "/name/", name);
//        messegeMap.put("/messege/" + key + "/" + myUid + "/sex/", sex);
//        messegeMap.put("/messege/" + key + "/" + myUid + "/age/", age);
//        messegeMap.put("/messege/" + key + "/" + myUid + "/latitude/", latitude);
//        messegeMap.put("/messege/" + key + "/" + myUid + "/longitude/", longitude);
//        if (pic != null) {
//            messegeMap.put("/messege/" + key + "/" + myUid + "/pic/", pic);
//        }
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    private void hideKeyboard(View v) {
        InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void sendNotify(String userUid, String messege, String myName, String roomname, String myPic) {
        db.collection("users").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                reference.getRef().child("messege").child(roomname).child(userUid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String state = dataSnapshot.child("state").getValue(String.class);
                        if (state.equals("1")) {
                            String userToken = documentSnapshot.get("token").toString();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject root = new JSONObject();
//                                        JSONObject notification = new JSONObject();
                                        JSONObject data = new JSONObject();
//                                        notification.put("body", messege);
//                                        notification.put("title", getString(R.string.app_name));
                                        data.put("messege", messege);
                                        data.put("name", myName);
                                        data.put("pic", myPic);
                                        data.put("uid", myUid);
                                        Log.d("Fragment4ChatRoom>>>", myName);
//                                        root.put("notification", notification);
                                        root.put("data", data);
                                        root.put("to", userToken);

                                        URL Url = new URL(FCM_MESSAGE_URL);
                                        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                                        connection.setRequestMethod("POST");
                                        connection.setDoOutput(true);
                                        connection.setDoInput(true);
                                        connection.addRequestProperty("Authorization", "key=" + serverKey);
                                        connection.setRequestProperty("Accept", "application/json");
                                        connection.setRequestProperty("Content-type", "application/json");
                                        OutputStream os = connection.getOutputStream();
                                        os.write(root.toString().getBytes("utf-8"));
                                        os.flush();
                                        connection.getResponseCode();

                                        Log.d("Fragment4ChatRoom>>>", "send notify");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                });

            }
        });
    }

}
