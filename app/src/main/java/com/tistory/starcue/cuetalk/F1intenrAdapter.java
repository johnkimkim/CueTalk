package com.tistory.starcue.cuetalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class F1intenrAdapter extends BaseAdapter {

    Context c;
    ArrayList<AdressRoomList> adressRoomLists;
    LayoutInflater inflater;

    public F1intenrAdapter(Context c, ArrayList<AdressRoomList> adressRoomLists) {
        this.c = c;
        this.adressRoomLists = adressRoomLists;
    }

    @Override
    public int getCount() {
        return adressRoomLists.size();
    }

    @Override
    public Object getItem(int i) {
        return adressRoomLists.get(i);
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
            view = inflater.inflate(R.layout.f1listviewlayout, viewGroup, false);
        }

        F1intentHoler holer = new F1intentHoler(view);
        holer.name.setText(adressRoomLists.get(i).getName());
        holer.sex.setText(adressRoomLists.get(i).getSex());
        holer.age.setText(adressRoomLists.get(i).getAge());
        return view;
    }

    public static class F1intentHoler {
        TextView name;
        TextView sex;
        TextView age;

        public F1intentHoler(View itemView) {
            name = itemView.findViewById(R.id.name);
            sex = itemView.findViewById(R.id.sex);
            age = itemView.findViewById(R.id.age);
        }
    }
}
