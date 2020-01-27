package com.example.postcreator.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postcreator.R;
import com.example.postcreator.model.PostModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.MyViewHolder> {

    private Context mContext;
    private List<PostModel> stringArrayListCatID;
    private OnCategoryClickInterface clickInterface;

    public PostListAdapter(Context mContext, List<PostModel> stringArrayListCatID, OnCategoryClickInterface clickInterface) {
        this.mContext = mContext;
        this.stringArrayListCatID = stringArrayListCatID;
        this.clickInterface = clickInterface;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_bg_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int position) {
        final byte[] image = stringArrayListCatID.get(position).getPostImage();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
       myViewHolder.raw_image.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return stringArrayListCatID.size();
    }

    public interface OnCategoryClickInterface {
        void onCategoryClick(int reqCode, String image);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView raw_image;

        public MyViewHolder(@NonNull View view) {
            super(view);

            raw_image = view.findViewById(R.id.raw_image);
        }
    }
}
