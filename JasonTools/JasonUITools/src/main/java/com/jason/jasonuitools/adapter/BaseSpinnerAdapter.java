package com.jason.jasonuitools.adapter;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 描述：
 * </p>
 * <p>
 * Create by Jason on 2025/4/7
 * </p>
 * <p>
 * Emial: fjz19971129@163.com
 * </p>
 */
public abstract class BaseSpinnerAdapter<T> extends BaseAdapter implements SpinnerAdapter {
    private List<T> item = new ArrayList<>();
    private LayoutInflater inflater;
    private int layoutId;

    public BaseSpinnerAdapter(Activity activity, int layoutId) {
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(activity);
    }


    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public T getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);
        setView(viewHolder, item.get(position), position);
        setEvent(viewHolder, item.get(position), position);
        return convertView;
    }

    public void setItem(List<T> item) {
        if (item != null && item.size() > 0) {
            this.item.clear();
            this.item.addAll(item);
            notifyDataSetChanged();
        }
    }

    public void addItem(T data) {
        item.add(0, data);
        this.notifyDataSetChanged();
    } //删除所有的数据列表

    public void clearItem() {
        item.clear();
        notifyDataSetChanged();
    }

    //获取所有的数据列表
    public List<T> getAllItem() {
        return item;
    }

    protected abstract void setView(ViewHolder viewHolder, T item, int position);

    protected abstract void setEvent(ViewHolder viewHolder, T item, int position);


    protected  static class ViewHolder {

        public ViewHolder(View parentView) {
            this.parentView = parentView;
        }

        private View parentView;

        public void setText(int viewId, String text) {
            TextView view = parentView.findViewById(viewId);
            view.setText(text);
        }

        public void setText(int viewId, SpannableStringBuilder text) {
            TextView view = parentView.findViewById(viewId);
            view.setText(text);
        }

        public void setTextSize(int viewId, float size) {
            TextView view = parentView.findViewById(viewId);
            view.setTextSize(size);
        }

        public void setTextColor(int viewId, @ColorInt int colorId) {
            TextView view = parentView.findViewById(viewId);
            view.setTextColor(colorId);
        }

        public void getText(int viewId) {
            TextView view = parentView.findViewById(viewId);
            view.getText();
        }

        public void setBtnOnClickListener(int viewId, View.OnClickListener listener) {
            View view = parentView.findViewById(viewId);
            view.setOnClickListener(listener);
        }

        public void setImage(int viewId, @DrawableRes int drawableId) {
            ImageView view = parentView.findViewById(viewId);
            view.setImageResource(drawableId);
        }

        public void setVisibility(int viewId, int visible) {
            View view = parentView.findViewById(viewId);
            view.setVisibility(visible);
        }
    }
}