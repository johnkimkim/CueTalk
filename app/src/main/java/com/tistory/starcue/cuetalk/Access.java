package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Access extends AppCompatActivity {

    Button okbtn;

    int PERMISSION_ALL = 1;
    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //저장공간
            Manifest.permission.ACCESS_FINE_LOCATION, //위치
            Manifest.permission.CALL_PHONE, //전화
            Manifest.permission.WRITE_CONTACTS //주소록
    };

    private static final int MUTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access);

        setinit();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }

    }

    private void setinit() {
        okbtn = findViewById(R.id.agree_btn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private boolean checkPermission() {
        int result;
        List<String> permissionsList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(Access.this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(pm);
            }
        }
        if (!permissionsList.isEmpty()) {
            ActivityCompat.requestPermissions(Access.this, permissionsList.toArray(new String[permissionsList.size()]), MUTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MUTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(Access.this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast();
                            }
                        } else if(permissions[i].equals(Access.this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast();
                            }
                        } else if(permissions[i].equals(Access.this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast();
                            }
                        } else if(permissions[i].equals(Access.this.permissions[3])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast();
                            }
                        } else if(permissions[i].equals(Access.this.permissions[4])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast();
                            }
                        }
                    }
                } else {
                    showToast();
                }
                return;
            }
        }
    }

    private void showToast() {
        Toast.makeText(Access.this, "모든권한용청함", Toast.LENGTH_LONG).show();
//        finish();
    }

    public boolean hasPermissions(Context context, String... permisions) {
        if (context != null && permisions != null) {
            for (String permission : permisions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(Access.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, //저장공간
                        Manifest.permission.ACCESS_FINE_LOCATION, //위치
                        Manifest.permission.READ_PHONE_STATE, //전화
                        Manifest.permission.READ_CONTACTS //주소록
                }, 1000);
    }
}
