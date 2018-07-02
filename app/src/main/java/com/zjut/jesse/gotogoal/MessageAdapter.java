package com.zjut.jesse.gotogoal;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jesse-PC on 2018/6/28.
 */

public class MessageAdapter extends BaseAdapter {

    private LinkedList<Message> msg;
    private Context context;
    private ViewHolder viewHolder;

    public MessageAdapter(LinkedList<Message> msg, Context context) {
        this.msg = msg;
        this.context = context;
    }

    @Override
    public int getCount() {
        return msg.size();
    }

    @Override
    public Object getItem(int position) {
        return msg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refresh(LinkedList<Message> msg) {
        this.msg = msg;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_layout, null);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.content);
            viewHolder.heart_num = (TextView) convertView.findViewById(R.id.heart_num);
            viewHolder.heart_btn = (ImageButton) convertView.findViewById(R.id.heart_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (msg.get(position).getHeart()) {
            viewHolder.heart_btn.setBackgroundResource(R.drawable.heart_on);
        } else {
            viewHolder.heart_btn.setBackgroundResource(R.drawable.heart_off);
        }
        viewHolder.author.setText(msg.get(position).getAuthor());
        viewHolder.time.setText(msg.get(position).getTime());
        viewHolder.content.setText(msg.get(position).getContent());
        viewHolder.heart_num.setText(String.valueOf(msg.get(position).getHeartNum()));
        viewHolder.heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemAddHeartListener.onAddHeartClick(position);
            }
        });

        return convertView;
    }

    public interface onItemAddHeartListener {
        void onAddHeartClick(int i);
    }

    private onItemAddHeartListener mOnItemAddHeartListener;

    public void setOnItemAddHeartListener(onItemAddHeartListener mOnItemAddHeartListener) {
        this.mOnItemAddHeartListener = mOnItemAddHeartListener;
    }


    public final static class ViewHolder {
        ImageButton heart_btn;
        TextView author, time, content, heart_num;
    }



}
