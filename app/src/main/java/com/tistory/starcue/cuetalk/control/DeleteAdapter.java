package com.tistory.starcue.cuetalk.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tistory.starcue.cuetalk.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.CustomViewHolder> {
    ArrayList<DeleteUserItem> arrayList;

    public DeleteAdapter(ArrayList<DeleteUserItem> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @NotNull
    @Override
    public DeleteAdapter.CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_delete_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DeleteAdapter.CustomViewHolder holder, int position) {
        holder.pn.setText(arrayList.get(position).getPhonenumber());
        holder.date.setText("탈퇴날짜: " + arrayList.get(position).getDate());
        holder.uid.setText(arrayList.get(position).getUid());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView pn, date, uid;

        public CustomViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.pn = itemView.findViewById(R.id.control_delete_layout_phonenumber);
            this.date = itemView.findViewById(R.id.control_delete_layout_date);
            this.uid = itemView.findViewById(R.id.control_delete_layout_uid);
        }
    }
}
