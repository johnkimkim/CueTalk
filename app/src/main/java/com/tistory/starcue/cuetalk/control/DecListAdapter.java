package com.tistory.starcue.cuetalk.control;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tistory.starcue.cuetalk.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DecListAdapter extends RecyclerView.Adapter<DecListAdapter.CustomViewHolder> {
    FirebaseFirestore db;
    DatabaseReference reference;
    StorageReference storageReference;
    List<String> uidList;
    Context context;
    ArrayList<F1DecListItem> arrayList = new ArrayList<>();
    String category, cuz, whodec, userUid;

    public DecListAdapter(List<String> uidList, Context context) {
        this.uidList = uidList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_dec_list_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CustomViewHolder holder, int position) {
        holder.decUid.setText(uidList.get(position));
        holder.decUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlActivity.load.setVisibility(View.VISIBLE);
                reference = FirebaseDatabase.getInstance().getReference();
                db = FirebaseFirestore.getInstance();
                storageReference = FirebaseStorage.getInstance().getReference();
                reference.getRef().child("chatroomdec").child(uidList.get(position)).child("messege").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            count += 1;
                            Log.d("DecListAdapter>>>", "get snapshot: " + snapshot.getKey());
                            F1DecListItem f1DecListItem = snapshot.getValue(F1DecListItem.class);
                            arrayList.add(f1DecListItem);
                            if (count == snapshot.getChildrenCount()) {
                                ControlActivity.f1declist.setVisibility(View.GONE);
                                reference.getRef().child("chatroomdec").child(uidList.get(position)).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                            if (!snapshot1.getKey().equals("messege")) {
                                                category = snapshot1.child("category").getValue(String.class);
                                                cuz = snapshot1.child("cuz").getValue(String.class);
                                                whodec = snapshot1.child("myUid").getValue(String.class);
                                                userUid = snapshot1.child("userUid").getValue(String.class);
                                                ControlActivity.getF1DecListView(context, arrayList, category, cuz, whodec, userUid);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (uidList != null ? uidList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView decUid;

        public CustomViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.decUid = itemView.findViewById(R.id.control_dec_list_layout);
        }
    }
}
