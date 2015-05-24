package com.intmainreturn0.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lz on 15-5-23.
 */
public class MyListAdapter extends BaseAdapter {
    private Activity context;
    private List<Info> list;

    public MyListAdapter(Activity context, List<Info> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.list_single, null);
        Info info = list.get(position);
        TextView textView = (TextView) itemView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) itemView
                .findViewById(R.id.img);
        TextView msgView= (TextView) itemView.findViewById(R.id.msg);
        textView.setText(info.getText());
        imageView.setImageBitmap(info.getBitmap());
        msgView.setText(info.msg);
        return itemView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}