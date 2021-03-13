package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private ArrayList<ChatRoomItem> arrayList;
    List<String> mytime = new ArrayList<>();
    List<String> yourtime  = new ArrayList<>();


    private Context context;

    ChatRoomAdapter(Context context, ArrayList<ChatRoomItem> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center, parent, false);
//        CustomViewHolder holder = new CustomViewHolder(view);
        View view;



        if (viewType == Code.ViewType.CENTER_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center, parent, false);
            return new CenterViewholder(view);
        } else if (viewType == Code.ViewType.LEFT_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_left, parent, false);
            return new LeftViewholder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_right, parent, false);
            return new RightViewholder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        if (holder instanceof CenterViewholder) {
            ((CenterViewholder) holder).textView.setText("입장완료");
        } else if (holder instanceof LeftViewholder) {
            ((LeftViewholder) holder).name.setText(arrayList.get(position).getName());
            ((LeftViewholder) holder).messege.setText(arrayList.get(position).getMessege());
            ((LeftViewholder) holder).time.setText(arrayList.get(position).getTime());
            Glide.with(((LeftViewholder) holder).pic)
                    .load(arrayList.get(position).getPic())
                    .override(150, 150)
                    .circleCrop()
                    .into(((LeftViewholder) holder).pic);
        } else {
            ((RightViewholder) holder).time1.setText(arrayList.get(position).getTime());
            ((RightViewholder) holder).messege1.setText(arrayList.get(position).getMessege());
        }

    }

    public int getItemViewType(int position) {
        databaseHandler.setDB(context);
        databaseHandler = new DatabaseHandler(context);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        if (arrayList.get(position).getName().equals(getMyName())) {
            return Code.ViewType.RIGHT_CONTENT;
        } else if (!arrayList.get(position).getName().equals(getMyName())) {
            return Code.ViewType.LEFT_CONTENT;
        } else {
            return Code.ViewType.CENTER_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

//    public class CustomViewHolder extends RecyclerView.ViewHolder{
//        ImageView pic, pic1;
//        TextView time, time1, name, messege, messege1;
//        public CustomViewHolder(@NonNull View itemView) {
//            super(itemView);
//            this.pic = itemView.findViewById(R.id.chat_room_layout_imageView1);
//            this.name = itemView.findViewById(R.id.chat_room_layout_name1);
//            this.time = itemView.findViewById(R.id.chat_room_layout_time1);
//            this.time1 = itemView.findViewById(R.id.chat_room_layout_time2);
//            this.messege = itemView.findViewById(R.id.chat_room_layout_messege1);
//            this.messege1 = itemView.findViewById(R.id.chat_room_layout_messege2);
//        }
//    }

    public class CenterViewholder extends RecyclerView.ViewHolder{
        TextView textView;
        public CenterViewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.center_text);
        }
    }

    public class LeftViewholder extends RecyclerView.ViewHolder{
        ImageView pic;
        TextView time, name, messege;
        public LeftViewholder(@NonNull View itemView) {
            super(itemView);
            this.pic = itemView.findViewById(R.id.chat_room_layout_imageView1);
            this.name = itemView.findViewById(R.id.chat_room_layout_name1);
            this.time = itemView.findViewById(R.id.chat_room_layout_time1);
            this.messege = itemView.findViewById(R.id.chat_room_layout_messege1);
        }
    }

    public class RightViewholder extends RecyclerView.ViewHolder{
        TextView time1, messege1;
        public RightViewholder(@NonNull View itemView) {
            super(itemView);
            this.time1 = itemView.findViewById(R.id.chat_room_layout_time2);
            this.messege1 = itemView.findViewById(R.id.chat_room_layout_messege2);
        }
    }

    public String getMyName() {
        Cursor cursor = sqLiteDatabase.rawQuery("select nameField from nameTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        return name;
    }
}
