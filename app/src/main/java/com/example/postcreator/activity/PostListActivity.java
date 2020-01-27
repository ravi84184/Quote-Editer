package com.example.postcreator.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postcreator.R;
import com.example.postcreator.adapter.PostListAdapter;
import com.example.postcreator.database.DatabaseHelper;

public class PostListActivity extends AppCompatActivity implements PostListAdapter.OnCategoryClickInterface {

    DatabaseHelper databaseHelper;
    RecyclerView rl_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        databaseHelper = new DatabaseHelper(this);


        intiUI();

    }

    private void intiUI() {
        rl_post = findViewById(R.id.rl_post);
        rl_post.setLayoutManager(new GridLayoutManager(this, 2));
        PostListAdapter postListAdapter = new PostListAdapter(this,databaseHelper.getBookData(),this);
        rl_post.setAdapter(postListAdapter);
    }

    @Override
    public void onCategoryClick(int reqCode, String image) {

    }
}
