package com.example.postcreator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.postcreator.activity.CreatePosterActivity;
import com.example.postcreator.activity.PostListActivity;
import com.example.postcreator.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;

    Button btn_create, btn_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        initUI();
    }

    private void initUI() {
        btn_create = findViewById(R.id.btn_create);
        btn_list = findViewById(R.id.btn_list);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreatePosterActivity.class));
            }
        });
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostListActivity.class));
            }
        });
    }
}
