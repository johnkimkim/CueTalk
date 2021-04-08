package com.tistory.starcue.cuetalk.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tistory.starcue.cuetalk.R;

public class Fragment2 extends Fragment {


    RecyclerView recyclerView;

    public Fragment2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        setinit(rootView);

        return rootView;
    }

    private void setinit(View v) {
        recyclerView = v.findViewById(R.id.f2re);
    }

    private void setfiredb() {

    }


}