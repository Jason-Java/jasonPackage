package com.jason.jasonuitools.adapter;

import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewSparseArray;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        viewSparseArray = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId) {
        View view = viewSparseArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }


    public void setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
    }

    public void setText(int viewId, SpannableStringBuilder text) {
        TextView view = getView(viewId);
        view.setText(text);
    }

    public void setTextSize(int viewId, float size) {
        TextView view = getView(viewId);
        view.setTextSize(size);
    }

    public void setTextColor(int viewId, @ColorInt int colorId) {
        TextView view = getView(viewId);
        view.setTextColor(colorId);
    }
    public void getText(int viewId) {
        TextView view = getView(viewId);
        view.getText();
    }

    public void setBtnOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
    }

    public void setImage(int viewId, @DrawableRes int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
    }

    public void setVisibility(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
    }


}
