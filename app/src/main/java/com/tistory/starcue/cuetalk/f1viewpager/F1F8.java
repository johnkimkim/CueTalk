package com.tistory.starcue.cuetalk.f1viewpager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.R;

import org.jetbrains.annotations.NotNull;

public class F1F8 extends Fragment {

    private ImageView imageView;
    private FirebaseFirestore db;

    public F1F8() {
        // Required empty public constructor
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.f1f8, container, false);

        db = FirebaseFirestore.getInstance();

        imageView = rootView.findViewById(R.id.f1f8img);

        db.collection("f1viewpager").document("page").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.get("f1f8").toString();
                Uri uri = Uri.parse(url);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(23));
                Glide.with(F1F8.this).load(uri).override(300, 250).fitCenter().apply(requestOptions).into(imageView);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("f1viewpager").document("f1f8").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String where = documentSnapshot.get("where").toString();
                        String url = documentSnapshot.get("url").toString();
                        if (where.equals("internet")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.setPackage("com.android.chrome");//크롬으로 띄우기, 없으면 브라우저 선택
                            startActivity(intent);
                        } else if (where.equals("playstore")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.tistory.starcue.bgnoise"));
                            intent.setPackage("com.android.vending");
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        return rootView;
    }
}
