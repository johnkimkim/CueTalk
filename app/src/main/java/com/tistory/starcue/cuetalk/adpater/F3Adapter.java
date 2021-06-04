package com.tistory.starcue.cuetalk.adpater;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
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
import com.tistory.starcue.cuetalk.fragment.Fragment3;
import com.tistory.starcue.cuetalk.item.F3Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class F3Adapter extends RecyclerView.Adapter<F3Adapter.CustomViewHolder> {

    FirebaseAuth mAuth;
    String myUid;

    private ArrayList<F3Item> arrayList;
    private Context context;

    private GpsTracker gpsTracker;

    private DatabaseReference reference;
    private FirebaseFirestore firestore;

    private RequestManager requestManager;

    public F3Adapter(ArrayList<F3Item> arrayList, Context context, RequestManager requestManager) {
        this.arrayList = arrayList;
        this.context = context;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.f3_list_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
//        Glide.with(holder.imageView).load(arrayList.get(position).getPic())
//                .signature(new ObjectKey(System.currentTimeMillis()))
//                .override(150, 150).circleCrop().into(holder.imageView);

//        Glide.with(holder.imageView).load(arrayList.get(position).getPic())
//                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
//                .override(150, 150).circleCrop().into(holder.imageView);

        requestManager
                .load(arrayList.get(position).getPic())
                .override(150, 150)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.userprogress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.userprogress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .circleCrop()
                .into(holder.imageView);

        requestManager
                .load(arrayList.get(position).getPpic())
                .override(150, 150)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.writeprogress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.writeprogress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerInside()
                .into(holder.ppic);
        holder.name.setText(arrayList.get(position).getName());
        holder.sex.setText(arrayList.get(position).getSex());
        holder.age.setText(arrayList.get(position).getAge());
        holder.messege.setText(arrayList.get(position).getMessege());

        String time = arrayList.get(position).getTime();
//        String time2 = time.substring(11);
//        String time3 = time2.substring(0, time2.length()-3);
        try {
            holder.time.setText(getTimeFormat(getTime(), time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (arrayList.get(position).getUid().equals(myUid)) {
            holder.f3dec.setEnabled(false);
            holder.sendbtn.setBackgroundResource(R.drawable.f2f3deletebuttonimg);
        } else {
            holder.f3dec.setEnabled(true);
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
                MainActivity.loading.setVisibility(View.GONE);
                Log.d("Fragment2>>>", "sendbtn onClick");
                if (arrayList.get(position).getUid().equals(myUid)) {
                    MainActivity.loading.setVisibility(View.GONE);
                    DeleteMyDialog.f3deleteMyDialog(context, myUid);
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
                                sendMessege.setSendMessegeDialog(context, userUid, view);
                            } else {
                                if (dataSnapshot.hasChild(roomkey) || dataSnapshot.hasChild(roomkey1)) {
                                    MainActivity.loading.setVisibility(View.GONE);
                                    Toast.makeText(context, "이미 대화 중 입니다. 메시지함을 확인해주세요", Toast.LENGTH_SHORT).show();
                                } else {
                                    MainActivity.loading.setVisibility(View.GONE);
                                    String userUid = arrayList.get(position).getUid();
                                    SendMessege sendMessege = new SendMessege(context);
                                    sendMessege.setSendMessegeDialog(context, userUid, view);//laskdfjkl
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
                Log.d("F3Adapter>>>", "get pic uri: " + arrayList.get(position).getPic());
                SeePicDialog.seePicDialog(context, arrayList.get(position).getPic());
            }
        });

        holder.ppic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeePicDialog.seePicDialog(context, arrayList.get(position).getPpic());
            }
        });

        holder.f3dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecDialog.F3DecDialog(context, arrayList.get(position).getUid(), myUid);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, ppic;
        TextView name, sex, age, km, messege, time;
        Button sendbtn;
        Button f3dec;
        CardView f3deccard;
        CircularDotsLoader userprogress, writeprogress;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.f3re_pic);
            this.ppic = itemView.findViewById(R.id.f3re_ppic);
            this.name = itemView.findViewById(R.id.f3re_name);
            this.sex = itemView.findViewById(R.id.f3re_sex);
            this.age = itemView.findViewById(R.id.f3re_age);
            this.km = itemView.findViewById(R.id.f3re_km);
            this.messege = itemView.findViewById(R.id.f3re_messege);
            this.time = itemView.findViewById(R.id.f3re_time);
            this.sendbtn = itemView.findViewById(R.id.f3re_sendmsg);
            this.f3dec = itemView.findViewById(R.id.f3dec);
            this.f3deccard = itemView.findViewById(R.id.f3dec_card);
            this.userprogress = itemView.findViewById(R.id.f3re_userpic_progress);
            this.writeprogress = itemView.findViewById(R.id.f3re_write_progress);
            if (Fragment3.f3fragdec.isChecked()) {
                f3deccard.setVisibility(View.VISIBLE);
            } else {
                f3deccard.setVisibility(View.GONE);
            }
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
