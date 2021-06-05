package com.tistory.starcue.cuetalk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteMyDialog {

    public static AlertDialog alertDialog;
    public static Button yesbtn, nobtn;

    public static void f2deleteMyDialog(Context context, String myUid, Activity activity) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.f2f3_delete_my, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        yesbtn = layout.findViewById(R.id.delete_my_ok);
        nobtn = layout.findViewById(R.id.delete_my_no);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setCancelable(false);
                MainActivity.loading.setVisibility(View.VISIBLE);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("f2messege").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.loading.setVisibility(View.GONE);
                        Toast.makeText(context, "삭제완료", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MainActivity.loading.setVisibility(View.GONE);
                        Toast.makeText(context, "네트워크 오류로 게시물 삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public static void f3deleteMyDialog(Context context, String myUid, Activity activity) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.f2f3_delete_my, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = alertDialog.getWindow();
        int x = (int) (size.x * 0.9);
        int y = (int) (size.y * 0.3);
        window.setLayout(x, y);

        yesbtn = layout.findViewById(R.id.delete_my_ok);
        nobtn = layout.findViewById(R.id.delete_my_no);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setCancelable(false);
                MainActivity.loading.setVisibility(View.VISIBLE);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                firestore.collection("f3messege").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//                        storageReference.child("fragment3/" + myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//                                storageReference.child("fragment3/" + myUid).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                                    @Override
//                                    public void onSuccess(ListResult listResult) {
//                                        for (StorageReference reference : listResult.getItems()) {
//                                            storageReference.child("fragment3/" + myUid + "/" + reference.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Toast.makeText(context, "삭제완료", Toast.LENGTH_SHORT).show();
//                                                    alertDialog.dismiss();
//                                                }
//                                            });
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "네트워크 오류로 게시물 삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                    }
//                });


                firestore.collection("f3messege").document(myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        storageReference.child("fragment3/" + myUid + "/" + myUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.loading.setVisibility(View.GONE);
                                alertDialog.dismiss();
                                Toast.makeText(context, "삭제완료", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MainActivity.loading.setVisibility(View.GONE);
                        Toast.makeText(context, "네트워크 오류로 게시물 삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.loading.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });
    }

}
