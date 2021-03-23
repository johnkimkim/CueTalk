package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class F4ReAdapter extends RecyclerView.Adapter<F4ReAdapter.CustomViewHolder> {

    private ArrayList<F4MessegeItem> arrayList;
    private Context context;

    F4ReAdapter(ArrayList<F4MessegeItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public F4ReAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment4_recyclerview_layout, parent, false);
        F4ReAdapter.CustomViewHolder holder = new F4ReAdapter.CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull F4ReAdapter.CustomViewHolder holder, int position) {

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name, sex, age, km, messege, time;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.pic = itemView.findViewById(R.id.f4re_pic);
            this.name = itemView.findViewById(R.id.f4re_name);
            this.sex = itemView.findViewById(R.id.f4re_sex);
            this.age = itemView.findViewById(R.id.f4re_age);
            this.km = itemView.findViewById(R.id.f4re_km);
            this.messege = itemView.findViewById(R.id.f4re_messege);
            this.time = itemView.findViewById(R.id.f4re_time);
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
