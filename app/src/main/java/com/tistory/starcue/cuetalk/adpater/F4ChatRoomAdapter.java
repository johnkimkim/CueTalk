package com.tistory.starcue.cuetalk.adpater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.Code;
import com.tistory.starcue.cuetalk.DatabaseHandler;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;
import com.tistory.starcue.cuetalk.item.F4ChatRoomItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class F4ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DatabaseHandler databaseHandler;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String myUid;
    String userUid;
    String userName, userSex, userAge, userPic, userLatitude, userLongitude;
    Intent intent;

    String getroomname;

    private ArrayList<F4ChatRoomItem> arrayList;
    List<String> mytime = new ArrayList<>();
    List<String> yourtime = new ArrayList<>();

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";
    String nullUser = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullUser.png?alt=media&token=4c9daa69-6d03-4b19-a793-873f5739f3a1";

    private RequestManager requestManager;

    private Context context;

    public F4ChatRoomAdapter(Context context, ArrayList<F4ChatRoomItem> arrayList, String getroomname, RequestManager requestManager) {
        this.arrayList = arrayList;
        this.context = context;
        this.getroomname = getroomname;
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

        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        String time = arrayList.get(position).getTime();
        String time1 = time.substring(11);
        String time2 = time1.substring(0, time1.length() - 3);

        db.collection("users").document(arrayList.get(position).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.get("uid") != null) {
                        //RightImageViewholder
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
                            if (arrayList.get(position).getRead().equals("1")) {
                                ((RightImageViewholder) holder).read.setText("1");
                            } else {
                                ((RightImageViewholder) holder).read.setText("");
                            }
                            //LeftImageViewholder
                        } else if (holder instanceof LeftImageViewholder) {
                            ((LeftImageViewholder) holder).name.setText(userName);
                            ((LeftImageViewholder) holder).time.setText(time2);
                            requestManager
                                    .load(userPic)
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
                                    String uri = userPic;
                                    if (!uri.equals(nullPic) && !uri.equals(nullPicF)) {
                                        SeePicDialog.seePicDialog(context, uri);
                                    }
                                }
                            });
                            ((LeftImageViewholder) holder).image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String uri = arrayList.get(position).getUri();
                                    SeePicDialog.seePicDialog(context, uri);
                                }
                            });
                            //RightViewholder
                        } else if (holder instanceof RightViewholder) {
                            ((RightViewholder) holder).time1.setText(time2);
                            ((RightViewholder) holder).messege1.setText(arrayList.get(position).getMessege());
                            if (arrayList.get(position).getRead().equals("1")) {
                                ((RightViewholder) holder).read2.setText("1");
                            } else {
                                ((RightViewholder) holder).read2.setText("");
                            }
                        }
                        //LeftViewholder
                        else if (holder instanceof LeftViewholder) {
                            ((LeftViewholder) holder).name.setText(userName);
                            ((LeftViewholder) holder).messege.setText(arrayList.get(position).getMessege());
                            ((LeftViewholder) holder).time.setText(time2);
                            Log.d("F4chatRoomAdapter>>>", "get time in arrayList: " + arrayList.get(position).getTime() + " / " + time2);
                            requestManager//error. requast manager?사용해서 this받기
                                    .load(userPic)
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
                                    String uri = userPic;
                                    if (!uri.equals(nullPic) && !uri.equals(nullPicF) && !uri.equals(nullUser)) {
                                        SeePicDialog.seePicDialog(context, uri);
                                    }
                                }
                            });
                        } else if (holder instanceof CenterViewholder) {
                            ((CenterViewholder) holder).textView.setText("입장완료");
                        } else if (holder instanceof CenterBottomViewholder) {
                            ((CenterBottomViewholder) holder).titletext.setText("상대방이 대화방을 나갔습니다.");
                        }
                    } else {
                        if (holder instanceof RightImageViewholder) {
                            ((RightImageViewholder) holder).timepic.setText(time2);
                            requestManager
                                    .load(arrayList.get(position).getUri())
                                    .override(150, 150)
                                    .centerCrop()
                                    .into(((RightImageViewholder) holder).imagepic);
                            if (arrayList.get(position).getRead().equals("1")) {
                                ((RightImageViewholder) holder).read.setText("1");
                            } else {
                                ((RightImageViewholder) holder).read.setText("");
                            }
                        } else if (holder instanceof LeftImageViewholder) {
                            ((LeftImageViewholder) holder).name.setText("(알수없음)");
                            ((LeftImageViewholder) holder).time.setText(time2);
                            requestManager
                                    .load(nullUser)
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
                            ((LeftImageViewholder) holder).image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String uri = arrayList.get(position).getUri();
                                    SeePicDialog.seePicDialog(context, uri);
                                }
                            });
                            ((LeftImageViewholder) holder).picli.setEnabled(false);
                        } else if (holder instanceof RightViewholder) {
                            ((RightViewholder) holder).time1.setText(time2);
                            ((RightViewholder) holder).messege1.setText(arrayList.get(position).getMessege());
                            if (arrayList.get(position).getRead().equals("1")) {
                                ((RightViewholder) holder).read2.setText("1");
                            } else {
                                ((RightViewholder) holder).read2.setText("");
                            }
                        }
                        else if (holder instanceof LeftViewholder) {
                            ((LeftViewholder) holder).name.setText("(알수없음)");
                            ((LeftViewholder) holder).messege.setText(arrayList.get(position).getMessege());
                            ((LeftViewholder) holder).time.setText(time2);
                            Log.d("F4chatRoomAdapter>>>", "get time in arrayList: " + arrayList.get(position).getTime() + " / " + time2);
                            requestManager//error. requast manager?사용해서 this받기
                                    .load(nullUser)
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
                            ((LeftViewholder) holder).picl.setEnabled(false);
                        } else if (holder instanceof CenterViewholder) {
                            ((CenterViewholder) holder).textView.setText("입장완료");
                        } else if (holder instanceof CenterBottomViewholder) {
                            ((CenterBottomViewholder) holder).titletext.setText("상대방이 대화방을 나갔습니다.");
                        }
                    }
                }
            }
        });

    }

    public int getItemViewType(int position) {
        databaseHandler.setDB(context);
        databaseHandler = new DatabaseHandler(context);
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        intent = ((Activity) context).getIntent();
        userUid = intent.getStringExtra("userUid");
        userName = intent.getStringExtra("userName");
        userSex = intent.getStringExtra("userSex");
        userAge = intent.getStringExtra("userAge");
        userPic = intent.getStringExtra("userPic");
        userLatitude = intent.getStringExtra("userLatitude");
        userLongitude = intent.getStringExtra("userLongitude");
        Log.d("F4ChatRoomAdapter>>>", "yourUid: " + userUid);

        Log.d("getUserName>>>3", userName);
        if (arrayList.get(position).getUri() == null
                && arrayList.get(position).getTime().equals("dlqwkddhksfycjtaptlwl")
                && arrayList.get(position).getMessege().equals("dlqwkddhksfycjtaptlwl")) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: CENTER_CONTENT");
            return Code.ViewType.CENTER_CONTENT;
        } else if (arrayList.get(position).getUri() == null
                && arrayList.get(position).getTime().equals("tkdeoqkddlskrkskrk")
                && arrayList.get(position).getMessege().equals("tkdeoqkddlskrkskrk")) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: CENTER_BOTTOM_CONTENT");
            return Code.ViewType.CENTER_BOTTOM_CONTENT;
        } else if (arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() != null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: RIGHT_IMAGE");
            return Code.ViewType.RIGHT_IMAGE;
        } else if (arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() == null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: RIGHT_CONTENT");
            return Code.ViewType.RIGHT_CONTENT;
        } else if (!arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() != null) {
            Log.d("F4ChatRoomAdapter>>>", "viewType: LEFT_IMAGE");
            return Code.ViewType.LEFT_IMAGE;
        } else if (!arrayList.get(position).getUid().equals(myUid) && arrayList.get(position).getUri() == null) {
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
        CircularDotsLoader progressBar;

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
        TextView time1, messege1, read2;

        public RightViewholder(@NonNull View itemView) {
            super(itemView);
            this.time1 = itemView.findViewById(R.id.chat_room_layout_time2);
            this.messege1 = itemView.findViewById(R.id.chat_room_layout_messege2);
            this.read2 = itemView.findViewById(R.id.chat_room_layout_read2);
        }
    }

    public class LeftImageViewholder extends RecyclerView.ViewHolder {
        ImageView picli, image;
        TextView time, name;
        CircularDotsLoader userpicprogress, progressBar;

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
        TextView timepic, read;
        ImageView imagepic;
        CircularDotsLoader progressBar;

        public RightImageViewholder(@NonNull View itemView) {
            super(itemView);
            this.timepic = itemView.findViewById(R.id.chat_room_layout_time2_img);
            this.imagepic = itemView.findViewById(R.id.chat_room_layout_messege2_img);
            this.read = itemView.findViewById(R.id.chat_room_layout_read);
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
