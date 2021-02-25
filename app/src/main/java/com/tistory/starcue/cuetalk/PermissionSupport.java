package com.tistory.starcue.cuetalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {
    private Context context;
    private Activity activity;

    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //저장공간
            Manifest.permission.ACCESS_FINE_LOCATION, //위치
            Manifest.permission.CALL_PHONE, //전화
            Manifest.permission.WRITE_CONTACTS //주소록
    };
    private List<String> permissionList = new ArrayList<>();

    private final int MUTIPLE_PERMISSIONS = 1023;

    public PermissionSupport(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            return false;
        }
        return true;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), MUTIPLE_PERMISSIONS);
    }

    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MUTIPLE_PERMISSIONS && (grantResults.length > 0)) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    return false;
                }
            }
        }
        return true;
    }
}
