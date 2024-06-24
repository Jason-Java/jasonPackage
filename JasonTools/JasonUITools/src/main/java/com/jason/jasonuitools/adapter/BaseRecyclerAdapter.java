package com.jason.jasonuitools.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月23日
 */
public abstract  class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder>
{

    protected ArrayList<T> itemList = new ArrayList<>();
    protected Activity activity;
    private  int layoutId;

    public BaseRecyclerAdapter(Activity activity,int layoutId)
    {
        this.activity = activity;
        this.layoutId=layoutId;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(layoutId, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount()
    {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {
        setView(holder,getItem(position), position);
        Event(holder,getItem(position), position);
    }

    protected abstract void setView(@NonNull RecyclerViewHolder holder, T item, int position);

    protected abstract void Event(@NonNull RecyclerViewHolder holder, T item, int position);

    public T getItem(int position) {
        return itemList.get(position);
    }

    public ArrayList<T> getItemAll() {
        return itemList;
    }

    //设置数据
    public void setItemList(ArrayList<T> i)
    {
        if (i != null && i.size() > 0)
        {
            itemList.clear();
            itemList.addAll(i);
            notifyDataSetChanged();
        }
    }

    //添加数据
    public void addItem(ArrayList<T> i)
    {
        itemList.add((T) i);
    }

    //清空数据
    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

}
