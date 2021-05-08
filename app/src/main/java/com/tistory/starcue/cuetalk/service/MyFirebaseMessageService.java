package com.tistory.starcue.cuetalk.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.fragment.Fragment4;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("MessegeService>>>", "new token: " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String myUid = mAuth.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String state = documentSnapshot.get("notify").toString();
                if (state.equals("on")) {
//                        String body = remoteMessage.getNotification().getBody();

                    Map<String, String> data = remoteMessage.getData();
                    String title = data.get("messege");
                    String name = data.get("name");
                    String pic = data.get("pic");
                    String userUid = data.get("uid");
                    Log.d("MessageService>>>", "notification pic: " + pic);

                    Intent intent = new Intent(MyFirebaseMessageService.this, Fragment4.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessageService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    String channelId = "Channel ID";
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessageService.this, channelId);


                    int largeIconSize = Math.round(64 * getApplicationContext().getResources().getDisplayMetrics().density);
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(pic)
                            .override(largeIconSize, largeIconSize)
                            .circleCrop()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                    notificationBuilder.setLargeIcon(resource);
                                    notificationBuilder.setAutoCancel(true);
                                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                                    notificationBuilder.setContentTitle(name);
                                    notificationBuilder.setContentText(title);
                                    notificationBuilder.setAutoCancel(true);
                                    notificationBuilder.setSound(defaultSoundUri);
                                    notificationBuilder.setContentIntent(pendingIntent);
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        String channelName = "Channel Name";
                                        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                        notificationManager.createNotificationChannel(channel);
                                    }
                                    int num = Integer.parseInt(userUid.replaceAll("[^0-9]", ""));
                                    Log.d("MessageService>>>", "num: " + num);
                                    notificationManager.notify(num, notificationBuilder.build());
                                }

                                @Override
                                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                                }
                            });


                }
            }
        });


    }

}