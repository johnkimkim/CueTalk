package com.tistory.starcue.cuetalk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class KillAppService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//service started
        Log.d("KillAppService>>>", "Service started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {//service stoped
        super.onDestroy();
        Log.d("KillAppService>>>", "Service Destoryed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {//code here when kill app
        Log.d("KillAppService>>>", "END");
    }
}
