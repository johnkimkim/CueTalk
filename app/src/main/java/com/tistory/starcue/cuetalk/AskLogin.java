//package com.tistory.starcue.cuetalk;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Display;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class AskLogin extends AppCompatActivity {
//
//    DatabaseHandler databaseHandler;
//    private SQLiteDatabase sqLiteDatabase;
//
//    private FirebaseAuth mAuth;
//    private FirebaseUser mCurrentUser;
//
//    private DatabaseReference reference;
//    private FirebaseDatabase database;
//
//    FirebaseFirestore db;
//
//    Button yes, no;
//
//    String myUid;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.ask_login);
//
//        setDialog();
//        setFirebase();
//
//
//
//        yes = findViewById(R.id.yes_btn);
//        no = findViewById(R.id.no_btn);
//
//        setOnClickYes();
//        setOnClickNo();
//
//    }
//
//    private void setDialog() {
//        final Dialog dialog = new Dialog(this);
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
//        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        layoutParams.dimAmount = 0.7f;
//
//        //set dialog size
//        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = (int) (display.getWidth() * 1);
//        int height = (int) (display.getHeight() * 0.2);
////        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
////        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
////        layoutParams.width = width;
////        layoutParams.height = height;
//        dialog.getWindow().setAttributes(layoutParams);
//    }
//
//    private void setFirebase() {
//        mAuth = FirebaseAuth.getInstance();
//        mCurrentUser = mAuth.getCurrentUser();
//        db = FirebaseFirestore.getInstance();
//        myUid = mAuth.getUid();
//    }
//
//    private void setOnClickYes() {
//        yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                yes.setEnabled(false);
//                no.setEnabled(false);
//                setfirestoredata();
//            }
//        });
//    }
//
//    private void setOnClickNo() {
//        no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                yes.setEnabled(false);
//                no.setEnabled(false);
//                mAuth.signOut();
//                startActivity(new Intent(AskLogin.this, PhoneNumber.class));
//            }
//        });
//    }
//
//    private void setfirestoredata() {
//
//        databaseHandler.setDB(AskLogin.this);
//        databaseHandler = new DatabaseHandler(this);
//        sqLiteDatabase = databaseHandler.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("select uniqueField from uniqueTable where _rowid_ = 1", null);
//        cursor.moveToFirst();
//        String uniquestring = cursor.getString(0);
//
//        String user_uid = mAuth.getUid();
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("unique", uniquestring);
//
//        db.collection("users").document(user_uid)
//                .update(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        checkUser();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }
//
////    private void checkUser() {
////        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////            @Override
////            public void onSuccess(DocumentSnapshot documentSnapshot) {
////                String name = documentSnapshot.getString("name").toString();
////            }
////        }).addOnFailureListener(new OnFailureListener() {
////            @Override
////            public void onFailure(@NonNull Exception e) {
////
////            }
////        });
////    }
//
//    private void checkSql() {
//        Cursor cursor = sqLiteDatabase.rawQuery("select * from id", null);
//        int i = cursor.getCount();
//        if (i == 0) {
//            String test = Integer.toString(i);
//            Log.d("splash<<<: ", test);
//            Intent intent = new Intent(AskLogin.this, LoginActivity.class);
//            startActivity(intent);
//        } else {
//            String test = Integer.toString(i);
//            Log.d("splash<<<: ", test);
//            Intent intent = new Intent(AskLogin.this, MainActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    private void checkUser() {
//        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.get("name") == null) {
//                    goToLoginActivity();
//                } else {
//                    goToMain();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//    }
//
//    private void goToMain() {
//        Intent intent = new Intent(AskLogin.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    private void goToLoginActivity() {
//        Intent intent = new Intent(AskLogin.this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//
//
//    @Override
//    public void onBackPressed() {
//
//    }
//}
