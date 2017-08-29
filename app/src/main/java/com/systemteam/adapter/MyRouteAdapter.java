package com.systemteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.UseRecord;
import com.systemteam.car.CarDetailActivity;

import java.util.List;

public class MyRouteAdapter extends BaseAdapter {

    public MyRouteAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case TYPE_TITLE:
                view = LayoutInflater.from(context).inflate(R.layout.item_my_car, null);
                break;
            case TYPE_CONTENT:
                view = LayoutInflater.from(context).inflate(R.layout.item_my_car, null);
                break;
        }
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (int) view.getTag();
                if (listener != null && getItemViewType(position) != TYPE_TITLE) {
                    listener.onItemClick(view, position);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = (int) view.getTag();
                if (longClickListener != null && getItemViewType(position) != TYPE_TITLE) {
                    longClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
        return holder;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        switch(getItemViewType(position)){
            case TYPE_TITLE:
                holder.bike_time.setText(context.getString(R.string.carNo));
                holder.bike_distance.setText(context.getString(R.string.earn));
                holder.bike_price.setText(context.getString(R.string.status));
                holder.bike_date.setVisibility(View.GONE);
                break;
            case TYPE_CONTENT:
                UseRecord routeRecord=(UseRecord)list.get(position);
//        holder.bike_time.setText(context.getString(R.string.cost_time, routeRecord.getCycle_time()));
                holder.bike_time.setText(routeRecord.getTimeUse());
//        holder.bike_distance.setText(context.getString(R.string.cost_distance, routeRecord.getCycle_distance()));
                holder.bike_distance.setText(routeRecord.getCarNo());
                if(context instanceof CarDetailActivity){
                    holder.bike_price.setText(context.getString(R.string.cost_num, String.valueOf(routeRecord.getEarn())));
                }else {
                    holder.bike_price.setText(context.getString(R.string.cost_num, String.valueOf(routeRecord.getCost())));
                }
                holder.bike_date.setText(routeRecord.getCreatedAt());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}