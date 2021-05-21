package com.tistory.starcue.cuetalk.f1viewpager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class F1ViewpagerIntent {

    public static void f1viewpagerintent(Context context, String field) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("f1viewpager").document(field).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String where = documentSnapshot.get("where").toString();
                String url = documentSnapshot.get("url").toString();
                if (where.equals("internet")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setPackage("com.android.chrome");//크롬으로 띄우기, 없으면 브라우저 선택
                    context.startActivity(intent);
                } else if (where.equals("playstore")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.tistory.starcue.bgnoise"));
                    intent.setPackage("com.android.vending");
                    context.startActivity(intent);
                }
            }
        });
    }

}
