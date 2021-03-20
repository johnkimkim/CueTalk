package com.tistory.starcue.cuetalk;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

public class SeePicDialog {

    public static void seePicDialog(Context context, String uri) {
        AlertDialog alertDialog;

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.see_pic_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        ImageView gradient = layout.findViewById(R.id.see_pic_layout_gradient);
        gradient.setVisibility(View.GONE);
        Button cancelBtn = layout.findViewById(R.id.see_pic_layout_finish);
        cancelBtn.setVisibility(View.GONE);

        alertDialog.show();
        //set size
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        layoutParams.dimAmount = 0.7f;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth());
        int height = (int) (display.getHeight());

        ImageView imageView = layout.findViewById(R.id.see_pic_layout_imageview);
        imageView.setMinimumWidth(width);
        imageView.setMinimumHeight(height);
        Glide.with(context).load(uri).centerInside().into(imageView);


//        Animation fadein = new AlphaAnimation(0, 1);
//        Animation fadeout = new AlphaAnimation(1, 0);
//        Animation.AnimationListener fadeinLi = new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                gradient.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        };
//        Animation.AnimationListener fadeoutLi = new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                gradient.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        };
//        fadein.setAnimationListener(fadeinLi);
//        fadeout.setAnimationListener(fadeoutLi);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gradient.getVisibility() == View.GONE) {
                    gradient.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                } else {
                    gradient.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
