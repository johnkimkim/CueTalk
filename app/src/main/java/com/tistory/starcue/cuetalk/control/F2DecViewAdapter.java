package com.tistory.starcue.cuetalk.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class F2DecViewAdapter extends RecyclerView.Adapter<F2DecViewAdapter.CustomViewHolder> {
    ArrayList<F2DecViewItem> arrayList = new ArrayList<>();
    List<String> f2roomnamelist = new ArrayList<>();
    RequestManager requestManager;
    public F2DecViewAdapter(ArrayList<F2DecViewItem> arrayList, List<String> f2roomnamelist, RequestManager requestManager) {
        this.arrayList = arrayList;
        this.f2roomnamelist = f2roomnamelist;
        this.requestManager = requestManager;
    }
    @NonNull
    @NotNull
    @Override
    public F2DecViewAdapter.CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_f2dec_view_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull F2DecViewAdapter.CustomViewHolder holder, int position) {
        requestManager
                .load(arrayList.get(position).getPic())
                .override(150, 150)
                .centerCrop()
                .into(holder.img);
        holder.age.setText(arrayList.get(position).getAge());
        holder.category.setText(arrayList.get(position).getCategory());
        holder.cuz.setText(arrayList.get(position).getCuz());
        holder.dectime.setText("신고시간: " + arrayList.get(position).getDectime());
        holder.messege.setText(arrayList.get(position).getMessege());
        holder.name.setText(arrayList.get(position).getName());
        holder.sex.setText(arrayList.get(position).getSex());
        holder.time.setText(arrayList.get(position).getTime());
        holder.whodec.setText("신고자: " + arrayList.get(position).getWhodec());
        holder.userUid.setText("피신고자: " + arrayList.get(position).getUid());

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeePicDialog.seePicDialog(view.getContext(), arrayList.get(position).getPic());
            }
        });

        holder.whodec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlActivity.decroomnameedit.setText(f2roomnamelist.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView age, category, cuz, dectime, messege, name, sex, time, whodec, userUid;

        public CustomViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.img = itemView.findViewById(R.id.control_f2dec_view_img);
            this.age = itemView.findViewById(R.id.control_f2dec_view_age);
            this.category = itemView.findViewById(R.id.control_f2dec_view_category);
            this.cuz = itemView.findViewById(R.id.control_f2dec_view_cuz);
            this.dectime = itemView.findViewById(R.id.control_f2dec_view_dectime);
            this.messege = itemView.findViewById(R.id.control_f2dec_view_messege);
            this.name = itemView.findViewById(R.id.control_f2dec_view_name);
            this.sex = itemView.findViewById(R.id.control_f2dec_view_sex);
            this.time = itemView.findViewById(R.id.control_f2dec_view_time);
            this.whodec = itemView.findViewById(R.id.control_f2dec_view_whodec);
            this.userUid = itemView.findViewById(R.id.control_f2dec_view_useruid);


        }
    }
}
