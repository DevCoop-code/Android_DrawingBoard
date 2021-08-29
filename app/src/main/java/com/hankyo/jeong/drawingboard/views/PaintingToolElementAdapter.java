package com.hankyo.jeong.drawingboard.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hankyo.jeong.drawingboard.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaintingToolElementAdapter extends RecyclerView.Adapter<PaintingToolElementAdapter.ViewHolder> {
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

        holder.textview.setText(item);
        holder.textview.setTag(item);
        holder.textview.setOnClickListener(onClickItem);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textview;

        public ViewHolder(View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.item_textview);
        }
    }
}
