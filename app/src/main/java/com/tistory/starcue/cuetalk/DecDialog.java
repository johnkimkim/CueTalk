package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class DecDialog {
    private static AlertDialog alertDialog;
    private static TextView title;
    private static RadioGroup radioGroup;
    private static RadioButton radio1, radio2, radio3, radio4;
    private static EditText editText;
    private static Button yesbtn, nobtn;


    public static void DicDialog(Context context) {

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) vi.inflate(R.layout.dec_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();

        alertDialog.show();
        //set size
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            layoutParams.dimAmount = 0.7f;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        radioGroup = layout.findViewById(R.id.dec_radio_group);
        radio1 = layout.findViewById(R.id.radio1);
        radio2 = layout.findViewById(R.id.radio2);
        radio3 = layout.findViewById(R.id.radio3);
        radio4 = layout.findViewById(R.id.radio4);
        editText = layout.findViewById(R.id.dec_dialog_edit);
        yesbtn = layout.findViewById(R.id.dec_dialog_ok);
        nobtn = layout.findViewById(R.id.dec_dialog_no);

        yesOrNo();
    }

    private static void yesOrNo() {
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDec();
            }
        });

        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private static void saveDec() {

    }

}
