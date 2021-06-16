package com.tistory.starcue.cuetalk.control;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class F1DecListViewAdapter extends RecyclerView.Adapter<F1DecListViewAdapter.CustomViewHolder> {
    FirebaseFirestore db;
    ArrayList<F1DecListItem> arrayList;
    RequestManager requestManager;
    String pic;

    public F1DecListViewAdapter(ArrayList<F1DecListItem> arrayList, RequestManager requestManager) {
        this.arrayList = arrayList;
        this.requestManager = requestManager;
    }

    @NonNull
    @NotNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_f1_dec_view_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CustomViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();

        Log.d("F1DecListViewAdapter>>>", "get uid: " + arrayList.get(position).getUid());

        db.collection("users").document(arrayList.get(position).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                requestManager
                        .load(documentSnapshot.get("pic").toString())
                        .override(150, 150)
                        .centerCrop()
                        .into(holder.img);
                holder.name.setText(documentSnapshot.get("name").toString());
                holder.sex.setText(documentSnapshot.get("sex").toString());
                holder.age.setText(documentSnapshot.get("age").toString());
                pic = documentSnapshot.get("pic").toString();
            }
        });

        holder.messege.setText(arrayList.get(position).getMessege());
        holder.time.setText(arrayList.get(position).getTime());

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeePicDialog.seePicDialog(view.getContext(), pic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView messege, name, sex, age, time;

        public CustomViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.img = itemView.findViewById(R.id.control_f1_dec_view_layout_image);
            this.messege = itemView.findViewById(R.id.control_f1_dec_view_layout_messege);
            this.name = itemView.findViewById(R.id.control_f1_dec_view_layout_name);
            this.sex = itemView.findViewById(R.id.control_f1_dec_view_layout_sex);
            this.age = itemView.findViewById(R.id.control_f1_dec_view_layout_age);
            this.time = itemView.findViewById(R.id.control_f1_dec_view_layout_time);
        }
    }
}
