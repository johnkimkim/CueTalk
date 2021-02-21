package com.tistory.starcue.cuetalk;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AdressRoom extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adress_room);

        setInit();

    }

    private void setInit() {
        recyclerView = findViewById(R.id.adress_room_recycelrview);
    }
}
