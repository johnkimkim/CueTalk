package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class Access extends AppCompatActivity {

    Button okbtn;

    int PERMISSION_ALL = 1;
    String[] PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //저장공간
            Manifest.permission.ACCESS_FINE_LOCATION, //위치
            Manifest.permission.CALL_PHONE, //전화
            Manifest.permission.WRITE_CONTACTS //주소록
    };
    private List permissionLists;

    final int WRITE_EXTERNAL_STORAGE = 0;
    final int ACCESS_FINE_LOCATION = 1;
    final int CALL_PHONE = 2;
    final int WRITE_CONTACTS = 3;

    private static final int MUTIPLE_PERMISSIONS = 1023;

    private PermissionSupport permission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access);

        setinit();


    }

    private void setinit() {
        okbtn = findViewById(R.id.agree_btn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(view);
            }
        });
    }

    public void checkPermission(View view) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(Access.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Access.this, "모든 권한을 허용해야 사용가능", Toast.LENGTH_LONG).show();
            }
        };

        TedPermission.with(Access.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.CALL_PHONE
                        , Manifest.permission.WRITE_CONTACTS)
                .check();
    }

}
