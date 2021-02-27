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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AdressRoomAdapter extends RecyclerView.Adapter<AdressRoomAdapter.CustomViewHolder> {

    private ArrayList<AdressRoomItem> arrayList;
    private Context context;

    AdressRoomAdapter(ArrayList<AdressRoomItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        Context context = parent.getContext();
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adress_room_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
        Glide.with(holder.imageView)
                .load(arrayList.get(position).getPic())
                .override(150, 150)
                .circleCrop()
                .into(holder.imageView);
        holder.name.setText(arrayList.get(position).getName());
        holder.sex.setText(arrayList.get(position).getSex());
        holder.age.setText(arrayList.get(position).getAge());
        holder.km.setText(arrayList.get(position).getKm());


    }

    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        TextView sex;
        TextView age;
        TextView km;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.adress_room_layout_pic);
            this.name = itemView.findViewById(R.id.adress_room_layout_name);
            this.sex = itemView.findViewById(R.id.adress_room_layout_sex);
            this.age = itemView.findViewById(R.id.adress_room_layout_age);
            this.km = itemView.findViewById(R.id.adress_room_layout_km);
        }
    }
}
