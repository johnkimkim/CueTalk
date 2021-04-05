package com.tistory.starcue.cuetalk.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tistory.starcue.cuetalk.item.AdressRoomItem;
import com.tistory.starcue.cuetalk.R;

import java.util.ArrayList;

public class F1intenrAdapter extends BaseAdapter {

    Context c;
    ArrayList<AdressRoomItem> adressRoomItems;
    LayoutInflater inflater;

    public F1intenrAdapter(Context c, ArrayList<AdressRoomItem> adressRoomItems) {
        this.c = c;
        this.adressRoomItems = adressRoomItems;
    }

    @Override
    public int getCount() {
        return adressRoomItems.size();
    }

    @Override
    public Object getItem(int i) {
        return adressRoomItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.adress_room_layout, viewGroup, false);
        }

        F1intentHoler holer = new F1intentHoler(view);
        holer.name.setText(adressRoomItems.get(i).getName());
        holer.sex.setText(adressRoomItems.get(i).getSex());
        holer.age.setText(adressRoomItems.get(i).getAge());
        return view;
    }

    public static class F1intentHoler {
        TextView name;
        TextView sex;
        TextView age;

        public F1intentHoler(View itemView) {
            name = itemView.findViewById(R.id.adress_room_layout_name);
            sex = itemView.findViewById(R.id.adress_room_layout_sex);
            age = itemView.findViewById(R.id.adress_room_layout_age);
        }
    }
}
