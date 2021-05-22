package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Access2 extends AppCompatActivity {

    private CheckBox cba, cb1, cb2, cb3;
    private Button btn;
    private RelativeLayout load;
    private TextView view1, view2, view3;

    private AlertDialog alertDialog;

    private FirebaseFirestore db;
    private String myUid;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_2);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        setInit();
    }

    private void setInit() {
        load = findViewById(R.id.access2_loading);
        load.setVisibility(View.GONE);
        load.bringToFront();
        cba = findViewById(R.id.access2_checkbox);
        cb1 = findViewById(R.id.access2_checkbox1);
        cb2 = findViewById(R.id.access2_checkbox2);
        cb3 = findViewById(R.id.access2_checkbox3);
        btn = findViewById(R.id.access2_btn);
        view1 = findViewById(R.id.access2_view1);
        view2 = findViewById(R.id.access2_view2);
        view3 = findViewById(R.id.access2_view3);

        checkAll();

        setOnClickBtn();
        setOnClickViewer();
    }

    private void checkAll() {
        cba.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//checked
                    cb1.setChecked(true);
                    cb2.setChecked(true);
                    cb3.setChecked(true);
                } else {//no checked
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });
    }

    private void setOnClickBtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.VISIBLE);
                Log.d("Access2>>>", "onClick");
                if (cb1.isChecked() && cb2.isChecked() && cb3.isChecked()) {
                    updateFirestore();
                } else {
                    dialog();
                }
            }
        });
    }

    private void updateFirestore() {
        Map<String, Object> map = new HashMap<>();
        map.put("위치기반서비스이용약관", getTime());
        map.put("개인정보처리방침", getTime());
        map.put("개인정보수집및이용동의", getTime());
        db.collection("users").document(myUid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();
            }
        });
    }

    private void dialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.if_not_access_2, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Access2.this);
        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();

        if (!Access2.this.isFinishing()) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            alertDialog.show();
        }

        /*Unable to add window -- token android.os.BinderProxy@7c9958a is not valid; is your activity running?*/

        Button okbtn = layout.findViewById(R.id.access2_dialog_okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });
    }

    private void setOnClickViewer() {
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Access2>>>", "view1 onClick");
                db.collection("version").document("access2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String link = documentSnapshot.get("위치기반서비스이용약관").toString();
                        Log.d("Access2>>>", "view1: " + link);
                    }
                });
            }
        });

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Access2>>>", "view2 onClick");
                db.collection("version").document("access2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String link = documentSnapshot.get("개인정보처리방침").toString();
                        Log.d("Access2>>>", "view1: " + link);
                    }
                });
            }
        });

        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Access2>>>", "view3 onClick");
                db.collection("version").document("access2").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String link = documentSnapshot.get("개인정보수집및이용동의").toString();
                        Log.d("Access2>>>", "view1: " + link);
                    }
                });
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
