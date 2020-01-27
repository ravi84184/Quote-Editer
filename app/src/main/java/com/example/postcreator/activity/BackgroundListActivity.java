package com.example.postcreator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postcreator.R;
import com.example.postcreator.adapter.BackgroundAdapter;

import java.util.ArrayList;

public class BackgroundListActivity extends AppCompatActivity implements BackgroundAdapter.OnCategoryClickInterface {


    RecyclerView rv_bg;
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_list);


        list.add("bg1.jpg");
        list.add("bg2.jpg");
        list.add("bg3.jpeg");
        list.add("bg4.jpeg");

        intiUI();

    }

    private void intiUI() {
        rv_bg = findViewById(R.id.rv_bg);
        rv_bg.setLayoutManager(new GridLayoutManager(this, 2));

        rv_bg.setAdapter(new BackgroundAdapter(this, list, this));
    }

    @Override
    public void onCategoryClick(int reqCode, String image) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",image);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
