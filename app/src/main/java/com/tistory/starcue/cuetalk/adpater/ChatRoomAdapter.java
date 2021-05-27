package com.tistory.starcue.cuetalk.adpater;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.tistory.starcue.cuetalk.Code;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;
import com.tistory.starcue.cuetalk.item.ChatRoomItem;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;
    private FirebaseAuth mAuth;
    String myUid;

    private ArrayList<ChatRoomItem> arrayList;
    List<String> mytime = new ArrayList<>();
    List<String> yourtime = new ArrayList<>();

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";


    private Context context;

    private RequestManager requestManager;

    public ChatRoomAdapter(Context context, ArrayList<ChatRoomItem> arrayList, RequestManager requestManager) {
        this.arrayList = arrayList;
        this.context = context;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center, parent, false);
//        CustomViewHolder holder = new CustomViewHolder(view);
        View view;

        if (viewType == Code.ViewType.RIGHT_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_img_right, parent, false);
            return new RightImageViewholder(view);
        } else if (viewType == Code.ViewType.LEFT_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_img_left, parent, false);
            return new LeftImageViewholder(view);
        } else if (viewType == Code.ViewType.RIGHT_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_right, parent, false);
            return new RightViewholder(view);
        } else if (viewType == Code.ViewType.LEFT_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_left, parent, false);
            return new LeftViewholder(view);
        } else if (viewType == Code.ViewType.CENTER_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center, parent, false);
            return new CenterViewholder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_layout_center_bottom, parent, false);
            return new CenterBottomViewholder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String time = arrayList.get(position).getTime();
        String time1 = time.substring(11);
        String time2 = time1.substring(0, time1.length() - 3);

        if (holder instanceof RightImageViewholder) {
            ((RightImageViewholder) holder).timepic.setText(time2);
            requestManager
                    .load(arrayList.get(position).getUri())
                    .override(150, 150)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((RightImageViewholder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((RightImageViewholder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(((RightImageViewholder) holder).imagepic);
        } else if (holder instanceof LeftImageViewholder) {
            ((LeftImageViewholder) holder).name.setText(arrayList.get(position).getName());
            ((LeftImageViewholder) holder).time.setText(time2);
            requestManager
                    .load(arrayList.get(position).getPic())
                    .override(150, 150)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((LeftImageViewholder) holder).userpicprogress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((LeftImageViewholder) holder).userpicprogress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .circleCrop()
                    .into(((LeftImageViewholder) holder).picli);
            requestManager
                    .load(arrayList.get(position).getUri())
                    .override(150, 150)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((LeftImageViewholder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((LeftImageViewholder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(((LeftImageViewholder) holder).image);
            ((LeftImageViewholder) holder).picli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = arrayList.get(position).getPic();
                    SeePicDialog.seePicDialog(context, uri);
                }
            });
            ((LeftImageViewholder) holder).image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = arrayList.get(position).getUri();
                    SeePicDialog.seePicDialog(context, uri);
                }
            });
            if (arrayList.get(position).getPic().equals(nullPic) || arrayList.get(position).getPic().equals(nullPicF)) {
                ((LeftImageViewholder) holder).picli.setEnabled(false);
            }
        } else if (holder instanceof RightViewholder) {
            ((RightViewholder) holder).time1.setText(time2);//error
            ((RightViewholder) holder).messege1.setText(arrayList.get(position).getMessege());
        }
        else if (holder instanceof LeftViewholder) {
            ((LeftViewholder) holder).name.setText(arrayList.get(position).getName());
            ((LeftViewholder) holder).messege.setText(arrayList.get(position).getMessege());
            ((LeftViewholder) holder).time.setText(time2);
            requestManager
                    .load(arrayList.get(position).getPic())
                    .override(150, 150)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((LeftViewholder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((LeftViewholder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .circleCrop()
                    .into(((LeftViewholder) holder).picl);
            ((LeftViewholder) holder).picl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = arrayList.get(position).getPic();
                    SeePicDialog.seePicDialog(context, uri);
                }
            });
            if (arrayList.get(position).getPic().equals(nullPic) || arrayList.get(position).getPic().equals(nullPicF)) {
                ((LeftViewholder) holder).picl.setEnabled(false);
            }
        } else if (holder instanceof CenterViewholder) {
            ((CenterViewholder) holder).textView.setText("입장완료");
        } else if (holder instanceof CenterBottomViewholder) {
            ((CenterBottomViewholder) holder).titletext.setText("상대방이 대화방을 나갔습니다.");
        }


    }

    public int getItemViewType(int position) {
        databaseHandler.setDB(context);
        databaseHandler = new DatabaseHandler(context);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        Log.d("chatroomadapter>>>", "arrayList.get Name: " + arrayList.get(position).getName());

        if (arrayList.get(position).getName() == null
                && arrayList.get(position).getPic() == null
                && arrayList.get(position).getUri() == null
                && arrayList.get(position).getTime().equals("dlqwkddhksfycjtaptlwl")
                && arrayList.get(position).getMessege().equals("dlqwkddhksfycjtaptlwl")) {
            Log.d("ChatRoomAdapter>>>", "viewType: CENTER_CONTENT");
            return Code.ViewType.CENTER_CONTENT;
        } else if (arrayList.get(position).getName() == null
                && arrayList.get(position).getPic() == null
                && arrayList.get(position).getUri() == null
                && arrayList.get(position).getTime().equals("tkdeoqkddlskrkskrk")
                && arrayList.get(position).getMessege().equals("tkdeoqkddlskrkskrk")) {
            Log.d("ChatRoomAdapter>>>", "viewType: CENTER_BOTTOM_CONTENT");
            return Code.ViewType.CENTER_BOTTOM_CONTENT;
        } else if (arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() != null) {
            Log.d("ChatRoomAdapter>>>", "viewType: RIGHT_IMAGE");
            return Code.ViewType.RIGHT_IMAGE;
        } else if (arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() == null) {
            Log.d("ChatRoomAdapter>>>", "viewType: RIGHT_CONTENT");
            return Code.ViewType.RIGHT_CONTENT;
        } else if (!arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() != null) {
            Log.d("ChatRoomAdapter>>>", "viewType: LEFT_IMAGE");
            return Code.ViewType.LEFT_IMAGE;
        } else if (!arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() == null) {
            Log.d("ChatRoomAdapter>>>", "viewType: LEFT_CONTENT");
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
        ProgressBar progressBar;

        public LeftViewholder(@NonNull View itemView) {
            super(itemView);
            this.picl = itemView.findViewById(R.id.chat_room_layout_imageView1);
            this.name = itemView.findViewById(R.id.chat_room_layout_name1);
            this.time = itemView.findViewById(R.id.chat_room_layout_time1);
            this.messege = itemView.findViewById(R.id.chat_room_layout_messege1);
            this.progressBar = itemView.findViewById(R.id.chat_room_layout_progress);
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
        ProgressBar userpicprogress, progressBar;

        public LeftImageViewholder(@NonNull View itemView) {
            super(itemView);
            this.picli = itemView.findViewById(R.id.chat_room_layout_imageView1_img);
            this.image = itemView.findViewById(R.id.chat_room_layout_messege1_img);
            this.name = itemView.findViewById(R.id.chat_room_layout_name1_img);
            this.time = itemView.findViewById(R.id.chat_room_layout_time1_img);
            this.progressBar = itemView.findViewById(R.id.chat_room_layout_progress_image);
            this.userpicprogress = itemView.findViewById(R.id.chat_room_layout_userpic_progress_image);
        }
    }

    public class RightImageViewholder extends RecyclerView.ViewHolder {
        TextView timepic;
        ImageView imagepic;
        ProgressBar progressBar;

        public RightImageViewholder(@NonNull View itemView) {
            super(itemView);
            this.timepic = itemView.findViewById(R.id.chat_room_layout_time2_img);
            this.imagepic = itemView.findViewById(R.id.chat_room_layout_messege2_img);
            this.progressBar = itemView.findViewById(R.id.chat_room_layout_right_image_progress);
        }
    }

    public class CenterBottomViewholder extends RecyclerView.ViewHolder {
        TextView titletext;
        public CenterBottomViewholder(@NonNull View itemView) {
            super(itemView);
            titletext = itemView.findViewById(R.id.center_bottom_text);
        }
    }

}
