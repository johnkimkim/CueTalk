package com.tistory.starcue.cuetalk.adpater;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tistory.starcue.cuetalk.DecDialog;
import com.tistory.starcue.cuetalk.DeleteMyDialog;
import com.tistory.starcue.cuetalk.GpsTracker;
import com.tistory.starcue.cuetalk.MainActivity;
import com.tistory.starcue.cuetalk.R;
import com.tistory.starcue.cuetalk.SeePicDialog;
import com.tistory.starcue.cuetalk.SendMessege;
import com.tistory.starcue.cuetalk.fragment.Fragment2;
import com.tistory.starcue.cuetalk.item.F2Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class F2Adapter extends RecyclerView.Adapter<F2Adapter.CustomViewHolder> {

    String nullPic = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPic.png?alt=media&token=bebf132e-75b5-47c5-99b0-26d920ae3ee8";
    String nullPicF = "https://firebasestorage.googleapis.com/v0/b/cuetalk-c4d03.appspot.com/o/nullPicF.png?alt=media&token=935033f6-4ee8-44cf-9832-d15dc38c8c95";


    FirebaseAuth mAuth;
    String myUid;

    private ArrayList<F2Item> arrayList;
    private Context context;
    private Activity activity;

    private GpsTracker gpsTracker;

    private DatabaseReference reference;
    private FirebaseFirestore firestore;

    private RequestManager requestManager;

    public F2Adapter(ArrayList<F2Item> arrayList, Context context, RequestManager requestManager, Activity activity) {
        this.arrayList = arrayList;
        this.context = context;
        this.requestManager = requestManager;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.f2_list_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        requestManager
                .load(arrayList.get(position).getPic())
                .override(150, 150)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .circleCrop()
                .into(holder.imageView);
        holder.name.setText(arrayList.get(position).getName());
        holder.sex.setText(arrayList.get(position).getSex());
        if (arrayList.get(position).getSex().equals("남자")) {
            holder.sex.setTextColor(context.getResources().getColor(R.color.male));
        } else {
            holder.sex.setTextColor(context.getResources().getColor(R.color.female));
        }
        holder.age.setText(arrayList.get(position).getAge());
        holder.messege.setText(arrayList.get(position).getMessege());
//        if (arrayList.get(position).getMessege().length() > 15) {
//            holder.messege.setTextSize(12);
//        } else {
//            holder.messege.setTextSize(15);
//        }

        String time = arrayList.get(position).getTime();
//        String time2 = time.substring(11);
//        String time3 = time2.substring(0, time2.length()-3);
        try {
            holder.time.setText(getTimeFormat(getTime(), time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (arrayList.get(position).getUid().equals(myUid)) {
            holder.f2dec.setEnabled(false);
            holder.sendbtn.setBackgroundResource(R.drawable.f2f3deletebuttonimg);
        } else {
            holder.f2dec.setEnabled(true);
            holder.sendbtn.setBackgroundResource(R.drawable.f2f3sendmessageicon);
        }

        String latitudeS = arrayList.get(position).getLatitude();//set km
        String longitudeS = arrayList.get(position).getLongitude();
        double latitude = Double.parseDouble(latitudeS);
        double longitude = Double.parseDouble(longitudeS);
        gpsTracker = new GpsTracker(context);
        double myLatitude = gpsTracker.getLatitude();
        double myLongitude = gpsTracker.getLongitude();

        double ddd = getDistance(myLatitude, myLongitude, latitude, longitude);
        if (arrayList.get(position).getUid().equals(myUid)) {
            holder.km.setText("0m");
        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 50) {
            holder.km.setText("-50m");
        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd < 1000) {
            int i = (int) Math.floor(ddd);
            holder.km.setText(Integer.toString(i) + "m");
        } else if (!arrayList.get(position).getUid().equals(myUid) && ddd >= 1000) {
            int i = (int) Math.floor(ddd) / 1000;
            holder.km.setText(Integer.toString(i) + "km");
        }

        holder.sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.loading.setVisibility(View.VISIBLE);
                Log.d("Fragment2>>>", "sendbtn onClick");
                if (arrayList.get(position).getUid().equals(myUid)) {
                    MainActivity.loading.setVisibility(View.GONE);
                    DeleteMyDialog.f2deleteMyDialog(context, myUid, activity);
                } else {
                    reference = FirebaseDatabase.getInstance().getReference();
                    reference.getRef().child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Log.d("Fragment2>>>", "sendbtn onClick1");
                            int i = (int) dataSnapshot.getChildrenCount();

                            String roomkey = arrayList.get(position).getUid() + myUid;
                            String roomkey1 = myUid + arrayList.get(position).getUid();


                            if (i == 0) {
                                MainActivity.loading.setVisibility(View.GONE);
                                String userUid = arrayList.get(position).getUid();
                                SendMessege sendMessege = new SendMessege(context);
                                sendMessege.setSendMessegeDialog(context, userUid, activity);
                            } else {
                                if (dataSnapshot.hasChild(roomkey) || dataSnapshot.hasChild(roomkey1)) {
                                    MainActivity.loading.setVisibility(View.GONE);
                                    Toast.makeText(context, "이미 대화 중 입니다. 메시지함을 확인해주세요", Toast.LENGTH_SHORT).show();
                                } else {
                                    MainActivity.loading.setVisibility(View.GONE);
                                    String userUid = arrayList.get(position).getUid();
                                    SendMessege sendMessege = new SendMessege(context);
                                    sendMessege.setSendMessegeDialog(context, userUid, activity);//laskdfjkl
                                }
                            }

                        }
                    });
                }
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrayList.get(position).getPic().equals(nullPic) && !arrayList.get(position).getPic().equals(nullPicF)) {
                    SeePicDialog.seePicDialog(context, arrayList.get(position).getPic());
                }
            }
        });

        holder.f2dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecDialog.F2DecDialog(context, arrayList.get(position).getUid(), myUid, activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        CardView mainlayout;
        ImageView imageView;
        TextView name, sex, age, km, messege, time;
        Button sendbtn;
        Button f2dec;
        CardView f2deccard;
        CircularDotsLoader progressBar;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mainlayout = itemView.findViewById(R.id.f2_recyclerview);
            this.imageView = itemView.findViewById(R.id.f2re_pic);
            this.name = itemView.findViewById(R.id.f2re_name);
            this.sex = itemView.findViewById(R.id.f2re_sex);
            this.age = itemView.findViewById(R.id.f2re_age);
            this.km = itemView.findViewById(R.id.f2re_km);
            this.messege = itemView.findViewById(R.id.f2re_messege);
            this.time = itemView.findViewById(R.id.f2re_time);
            this.sendbtn = itemView.findViewById(R.id.f2re_sendmsg);
            this.f2dec = itemView.findViewById(R.id.f2dec);
            this.f2deccard = itemView.findViewById(R.id.f2dec_card);
            this.progressBar = itemView.findViewById(R.id.f2re_progress);
            Log.d("F2Adapter>>>", "customViewHolder");
            if (Fragment2.f2fragdec.isChecked()) {
                f2deccard.setVisibility(View.VISIBLE);
            } else {
                f2deccard.setVisibility(View.GONE);
            }

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int x = (int) (size.x * 0.25);
            int xx = (int) (size.x * 0.22);
            int xxx = (int) (size.x * 0.015);
            int xxxx = (int) (x / 2);
            int z = (int) (size.x * 0.14);
            int zz = (int) (size.x * 0.11);
            int zzz = (int) (size.x * 0.07);

            mainlayout.setMinimumHeight(z);

            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = zz;
            params.height = zz;
            imageView.setLayoutParams(params);

            messege.setMinHeight(zzz);
        }
    }

    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        String date = format.format(mDate);
        return date;
    }

    private String getTimeFormat(String time1, String time2) throws ParseException {//my time(after), usertime(before)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.KOREA);
        Date dtime1 = dateFormat.parse(time1);
        Date dtime2 = dateFormat.parse(time2);
        long diff = dtime1.getTime() - dtime2.getTime();
        long sec = diff / 1000;
        if (sec < 60) {//초
            return sec + "초전";
        } else if (sec < 3600) {//분, 3600초 = 1시간
            long newResult = sec / 60;
            return newResult + "분전";
        } else if (sec < 86400) {//시간, 86400초 = 1일
            long newResult = sec / 3600;
            return newResult + "시간전";
        } else if (sec < 604800) {
            long newResult = sec / 86400;
            return newResult + "일전";
        } else if (sec < 2419200) {
            long newResult = sec / 604800;
            return newResult + "주전";
        } else if (sec < 14515200) {
            long newResult = sec / 2419200;
            return newResult + "개월전";
        } else {
            return "6개월전";
        }
    }


}
