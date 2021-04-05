package com.tistory.starcue.cuetalk.adpater;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.tistory.starcue.cuetalk.Code;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.item.F4ChatRoomItem;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;

import java.util.ArrayList;
import java.util.List;

public class F4ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;
    private FirebaseAuth mAuth;
    String myUid;

    private ArrayList<F4ChatRoomItem> arrayList;
    List<String> mytime = new ArrayList<>();
    List<String> yourtime = new ArrayList<>();

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";


    private Context context;

    public F4ChatRoomAdapter(Context context, ArrayList<F4ChatRoomItem> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center, parent, false);
//        CustomViewHolder holder = new CustomViewHolder(view);
        View view;

        if (viewType == Code.ViewType.RIGHT_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_img_right, parent, false);
            return new F4ChatRoomAdapter.RightImageViewholder(view);
        } else if (viewType == Code.ViewType.LEFT_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_img_left, parent, false);
            return new F4ChatRoomAdapter.LeftImageViewholder(view);
        } else if (viewType == Code.ViewType.RIGHT_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_right, parent, false);
            return new F4ChatRoomAdapter.RightViewholder(view);
        } else if (viewType == Code.ViewType.LEFT_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_left, parent, false);
            return new F4ChatRoomAdapter.LeftViewholder(view);
        } else if (viewType == Code.ViewType.CENTER_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center, parent, false);
            return new F4ChatRoomAdapter.CenterViewholder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center_bottom, parent, false);
            return new F4ChatRoomAdapter.CenterBottomViewholder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof F4ChatRoomAdapter.RightImageViewholder) {
            ((F4ChatRoomAdapter.RightImageViewholder) holder).timepic.setText(arrayList.get(position).getTime());
            Glide.with(((F4ChatRoomAdapter.RightImageViewholder) holder).imagepic)
                    .load(arrayList.get(position).getUri())
                    .override(150, 150)
                    .centerCrop()
                    .into(((F4ChatRoomAdapter.RightImageViewholder) holder).imagepic);
        } else if (holder instanceof F4ChatRoomAdapter.LeftImageViewholder) {
            ((F4ChatRoomAdapter.LeftImageViewholder) holder).name.setText(arrayList.get(position).getName());
            ((F4ChatRoomAdapter.LeftImageViewholder) holder).time.setText(arrayList.get(position).getTime());
            Glide.with(((F4ChatRoomAdapter.LeftImageViewholder) holder).picli)
                    .load(arrayList.get(position).getPic())
                    .override(150, 150)
                    .circleCrop()
                    .into(((F4ChatRoomAdapter.LeftImageViewholder) holder).picli);
            Glide.with(((F4ChatRoomAdapter.LeftImageViewholder) holder).image)
                    .load(arrayList.get(position).getUri())
                    .override(150, 150)
                    .centerCrop()
                    .into(((F4ChatRoomAdapter.LeftImageViewholder) holder).image);
            ((F4ChatRoomAdapter.LeftImageViewholder) holder).picli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = arrayList.get(position).getPic();
                    SeePicDialog.seePicDialog(context, uri);
                }
            });
            ((F4ChatRoomAdapter.LeftImageViewholder) holder).image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = arrayList.get(position).getUri();
                    SeePicDialog.seePicDialog(context, uri);
                }
            });
            if (arrayList.get(position).getPic().equals(nullPic) || arrayList.get(position).getPic().equals(nullPicF)) {
                ((F4ChatRoomAdapter.LeftImageViewholder) holder).picli.setEnabled(false);
            }
        } else if (holder instanceof F4ChatRoomAdapter.RightViewholder) {
            ((F4ChatRoomAdapter.RightViewholder) holder).time1.setText(arrayList.get(position).getTime());//error
            ((F4ChatRoomAdapter.RightViewholder) holder).messege1.setText(arrayList.get(position).getMessege());
        }
        else if (holder instanceof F4ChatRoomAdapter.LeftViewholder) {
            ((F4ChatRoomAdapter.LeftViewholder) holder).name.setText(arrayList.get(position).getName());
            ((F4ChatRoomAdapter.LeftViewholder) holder).messege.setText(arrayList.get(position).getMessege());
            ((F4ChatRoomAdapter.LeftViewholder) holder).time.setText(arrayList.get(position).getTime());
            Glide.with(((F4ChatRoomAdapter.LeftViewholder) holder).picl)
                    .load(arrayList.get(position).getPic())
                    .override(150, 150)
                    .circleCrop()
                    .into(((F4ChatRoomAdapter.LeftViewholder) holder).picl);
            ((F4ChatRoomAdapter.LeftViewholder) holder).picl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = arrayList.get(position).getPic();
                    SeePicDialog.seePicDialog(context, uri);
                }
            });
            if (arrayList.get(position).getPic().equals(nullPic) || arrayList.get(position).getPic().equals(nullPicF)) {
                ((F4ChatRoomAdapter.LeftViewholder) holder).picl.setEnabled(false);
            }
        } else if (holder instanceof F4ChatRoomAdapter.CenterViewholder) {
            ((F4ChatRoomAdapter.CenterViewholder) holder).textView.setText("입장완료");
        } else if (holder instanceof F4ChatRoomAdapter.CenterBottomViewholder) {
            ((F4ChatRoomAdapter.CenterBottomViewholder) holder).titletext.setText("상대방이 대화방을 나갔습니다.");
        }


    }

    public int getItemViewType(int position) {
        databaseHandler.setDB(context);
        databaseHandler = new DatabaseHandler(context);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        if (arrayList.get(position).getName() == null
                && arrayList.get(position).getPic() == null
                && arrayList.get(position).getUri() == null
                && arrayList.get(position).getTime().equals("dlqwkddhksfycjtaptlwl")
                && arrayList.get(position).getMessege().equals("dlqwkddhksfycjtaptlwl")) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: CENTER_CONTENT");
            return Code.ViewType.CENTER_CONTENT;
        } else if (arrayList.get(position).getName() == null
                && arrayList.get(position).getPic() == null
                && arrayList.get(position).getUri() == null
                && arrayList.get(position).getTime().equals("tkdeoqkddlskrkskrk")
                && arrayList.get(position).getMessege().equals("tkdeoqkddlskrkskrk")) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: CENTER_BOTTOM_CONTENT");
            return Code.ViewType.CENTER_BOTTOM_CONTENT;
        } else if (arrayList.get(position).getName().equals(getMyName()) && arrayList.get(position).getUri() != null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: RIGHT_IMAGE");
            return Code.ViewType.RIGHT_IMAGE;
        } else if (arrayList.get(position).getName().equals(getMyName()) && arrayList.get(position).getUri() == null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: RIGHT_CONTENT");
            return Code.ViewType.RIGHT_CONTENT;
        } else if (!arrayList.get(position).getName().equals(getMyName()) && arrayList.get(position).getUri() != null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: LEFT_IMAGE");
            return Code.ViewType.LEFT_IMAGE;
        } else if (!arrayList.get(position).getName().equals(getMyName()) && arrayList.get(position).getUri() == null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: LEFT_CONTENT");
            return Code.ViewType.LEFT_CONTENT;
        } else {
            return 6;
        }

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CenterViewholder extends RecyclerView.ViewHolder {
        TextView textView;

        public CenterViewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.center_text);
        }
    }

    public class LeftViewholder extends RecyclerView.ViewHolder {
        ImageView picl;
        TextView time, name, messege;

        public LeftViewholder(@NonNull View itemView) {
            super(itemView);
            this.picl = itemView.findViewById(R.id.chat_room_layout_imageView1);
            this.name = itemView.findViewById(R.id.chat_room_layout_name1);
            this.time = itemView.findViewById(R.id.chat_room_layout_time1);
            this.messege = itemView.findViewById(R.id.chat_room_layout_messege1);
        }
    }

    public class RightViewholder extends RecyclerView.ViewHolder {
        TextView time1, messege1;

        public RightViewholder(@NonNull View itemView) {
            super(itemView);
            this.time1 = itemView.findViewById(R.id.chat_room_layout_time2);
            this.messege1 = itemView.findViewById(R.id.chat_room_layout_messege2);
        }
    }

    public class LeftImageViewholder extends RecyclerView.ViewHolder {
        ImageView picli, image;
        TextView time, name;

        public LeftImageViewholder(@NonNull View itemView) {
            super(itemView);
            this.picli = itemView.findViewById(R.id.chat_room_layout_imageView1_img);
            this.image = itemView.findViewById(R.id.chat_room_layout_messege1_img);
            this.name = itemView.findViewById(R.id.chat_room_layout_name1_img);
            this.time = itemView.findViewById(R.id.chat_room_layout_time1_img);
        }
    }

    public class RightImageViewholder extends RecyclerView.ViewHolder {
        TextView timepic;
        ImageView imagepic;

        public RightImageViewholder(@NonNull View itemView) {
            super(itemView);
            this.timepic = itemView.findViewById(R.id.chat_room_layout_time2_img);
            this.imagepic = itemView.findViewById(R.id.chat_room_layout_messege2_img);
        }
    }

    public class CenterBottomViewholder extends RecyclerView.ViewHolder {
        TextView titletext;
        public CenterBottomViewholder(@NonNull View itemView) {
            super(itemView);
            titletext = itemView.findViewById(R.id.center_bottom_text);
        }
    }



    public String getMyName() {
        Cursor cursor = sqLiteDatabase.rawQuery("select nameField from nameTable where _rowid_ = 1", null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        return name;
    }
}
