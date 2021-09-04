package com.hankyo.jeong.drawingboard.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hankyo.jeong.drawingboard.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaintingToolElementAdapter extends RecyclerView.Adapter<PaintingToolElementAdapter.ViewHolder> {
    private static final String TAG = "PaintingToolElementAdapter";

    private ArrayList<String> itemList;
    private Context context;
    private View.OnClickListener onClickItem;

    public PaintingToolElementAdapter(Context context, ArrayList<String> itemList, View.OnClickListener onClickItem) {
        this.context = context;
        this.itemList = itemList;
        this.onClickItem = onClickItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.paintingtool_element_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaintingToolElementAdapter.ViewHolder holder, int position) {
        String item = itemList.get(position);

//        holder.textview.setText(item);
//        holder.textview.setTag(item);
        holder.imgBtn.setOnClickListener(onClickItem);
//        int color = Integer.parseInt(item, 16);
//        color = 0xFF4CB8FB;
        holder.imgBtn.setBackgroundColor(Color.parseColor(item));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageButton imgBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            imgBtn = itemView.findViewById(R.id.painting_item_btn);
        }
    }
}
