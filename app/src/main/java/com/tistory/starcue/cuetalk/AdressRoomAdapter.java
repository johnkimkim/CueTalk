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
import java.util.zip.Inflater;

public class AdressRoomAdapter extends RecyclerView.Adapter<AdressRoomAdapter.ViewHolder> {

    TextView name, sex, age, km;
    ImageView pic;
    Button btn;

    private ArrayList<AdressRoomItem> mData;

    AdressRoomAdapter(ArrayList<AdressRoomItem> list) {
        mData = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.adress_room_layout, parent, false);
        AdressRoomAdapter.ViewHolder viewHolder = new AdressRoomAdapter.ViewHolder(view);

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adress_room_layout_name);
            sex = itemView.findViewById(R.id.adress_room_layout_sex);
            age = itemView.findViewById(R.id.adress_room_layout_age);
            km = itemView.findViewById(R.id.adress_room_layout_km);
            pic = itemView.findViewById(R.id.adress_room_layout_pic);
            btn = itemView.findViewById(R.id.adress_room_layout_chatbtn);
        }
    }
}
