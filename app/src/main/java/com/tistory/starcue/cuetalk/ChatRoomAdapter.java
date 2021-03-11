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

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.CustomViewHolder>{

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;

    private ArrayList<ChatRoomItem> arrayList;

    private Context context;

    ChatRoomAdapter(Context context, ArrayList<ChatRoomItem> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        databaseHandler.setDB(context);
        databaseHandler = new DatabaseHandler(context);
        sqLiteDatabase = databaseHandler.getWritableDatabase();

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //내 닉네임과 같으면 오른쪽, 다르면 왼쪽

        if (arrayList.get(position).getName().equals(getMyName())) {
            holder.time1.setText(arrayList.get(position).getTime());
            holder.messege1.setText(arrayList.get(position).getMessege());
        } else {
            holder.name.setText(arrayList.get(position).getName());
            holder.messege.setText(arrayList.get(position).getMessege());
            holder.time.setText(arrayList.get(position).getTime());
        }

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView pic, pic1;
        TextView time, time1, name, messege, messege1;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.pic = itemView.findViewById(R.id.chat_room_layout_imageView1);
            this.name = itemView.findViewById(R.id.chat_room_layout_name1);
            this.time = itemView.findViewById(R.id.chat_room_layout_time1);
            this.time1 = itemView.findViewById(R.id.chat_room_layout_time2);
            this.messege = itemView.findViewById(R.id.chat_room_layout_messege1);
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
