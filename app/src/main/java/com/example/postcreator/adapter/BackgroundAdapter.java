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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> stringArrayListCatID;
    private OnCategoryClickInterface clickInterface;

    public BackgroundAdapter(Context mContext, ArrayList<String> stringArrayListCatID, OnCategoryClickInterface clickInterface) {
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

        AssetManager assetManager = mContext.getAssets();
        try (
                InputStream inputStream = assetManager.open(stringArrayListCatID.get(position))
        ) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            myViewHolder.raw_image.setImageBitmap(bitmap);
        } catch (IOException ex) {
            //ignored
        }

        myViewHolder.raw_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterface.onCategoryClick(1,stringArrayListCatID.get(position));
            }
        });

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
