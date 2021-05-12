package com.tistory.starcue.cuetalk.fragment;

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

import java.net.URI;

public class F1F1 extends Fragment {

    private ImageView imageView;
    private FirebaseFirestore db;

    public F1F1() {
        // Required empty public constructor
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.f1f1, container, false);

        db = FirebaseFirestore.getInstance();

        imageView = rootView.findViewById(R.id.f1f1img);

        db.collection("f1viewpager").document("f1f1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.get("url").toString();
                Log.d("F1F1>>>", url);
                Uri uri = Uri.parse(url);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(23));
                Glide.with(F1F1.this).load(uri).override(300, 250).fitCenter().apply(requestOptions).into(imageView);
            }
        });

        return rootView;
    }
}