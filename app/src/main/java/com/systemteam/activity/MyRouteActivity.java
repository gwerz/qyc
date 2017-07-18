package com.systemteam.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.adapter.MyRouteAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.BaseActivity;
import com.systemteam.bean.RouteRecord;
import com.systemteam.database.RouteDBHelper;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyRouteActivity extends BaseActivity implements MyRouteAdapter.OnItemClickListener {

    XRecyclerView routeRecyclerView;
    MyRouteAdapter routeAdapter;
    List<Object> routeList;
    String TABLE_NAME = "cycle_route";
    int PageId = 0, PageSize = 10;
    long itemCount = 0;
    SQLiteDatabase db;
    TextView no_route;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);
        mContext = this;
        initView();
        initData();
    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(MyRouteActivity.this, RouteDetailActivity.class);
        RouteRecord routeRecord = (RouteRecord)routeList.get(position);
//        bundle.putParcelable("routeContent",routeRecord );
        Log.d("gaolei", "getCycle_date------pass-------" + routeRecord.getCycle_date());
        Log.d("gaolei", "getCycle_points----pass---------" + routeRecord.getCycle_points());
        Bundle bundle = new Bundle();
        bundle.putString("totalTime", routeRecord.getCycle_time());
        bundle.putString("totalDistance", routeRecord.getCycle_distance());
        bundle.putString("totalPrice", routeRecord.getCycle_price());
        bundle.putString("routePoints", routeRecord.getCycle_points());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /*
     * 读取指定ID的分页数据
     * SQL:Select * From TABLE_NAME Limit 9 Offset 10;
     * 表示从TABLE_NAME表获取数据，跳过10行，取9行
     */
    public List<Object> loadPage() {
        if (routeList.size() >= itemCount) {
            routeRecyclerView.setNoMore(true);
            return null;
        }
        String sql = "select * from " + TABLE_NAME + " order by route_id DESC" +
                " " + "limit " + String.valueOf(PageSize) + " offset " + PageId * PageSize;
        Cursor cursor = db.rawQuery(sql, null);
        Log.d("gaolei", "PageId--------------" + PageId);

        Log.d("gaolei", "cursor.getCount()--------------" + cursor.getCount());
        Log.d("gaolei", "routeList.size()--------------" + routeList.size());
        Log.d("gaolei", "itemCount--------------" + itemCount);

        while (cursor.moveToNext()) {
            RouteRecord point = new RouteRecord();
            point.setCycle_date(cursor.getString(cursor
                    .getColumnIndex("cycle_date")));
            point.setCycle_time(cursor.getString(cursor
                    .getColumnIndex("cycle_time")));
            point.setCycle_distance(cursor.getString(cursor
                    .getColumnIndex("cycle_distance")));
            point.setCycle_price(cursor.getString(cursor
                    .getColumnIndex("cycle_price")));
            point.setCycle_points(cursor.getString(cursor
                    .getColumnIndex("cycle_points")));
            routeList.add(point);
        }
        PageId++;
        cursor.close();
        return routeList;

    }

    public long getItemCount() {
        String sql = "select count(*) from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void initView() {
        initToolBar(MyRouteActivity.this, R.string.route);
        routeRecyclerView = (XRecyclerView) findViewById(R.id.recyclerview_route);
        no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        routeList = getAllPoints();
        routeList = new ArrayList<>();

        RouteDBHelper helper = new RouteDBHelper(this);
        db = helper.getWritableDatabase();
        itemCount = getItemCount();
        routeList = loadPage();
        if (routeList != null) {
            routeAdapter = new MyRouteAdapter(this, routeList);
            routeRecyclerView.setAdapter(routeAdapter);
            routeRecyclerView.addItemDecoration(new MyRouteDividerDecoration(10));
            routeAdapter.setOnClickListener(this);
        }else{
            no_route.setVisibility(View.VISIBLE);
        }

        routeRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        routeRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallScale);
        routeRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        routeRecyclerView.setPullRefreshEnabled(false);
//        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
//        routeRecyclerView.addHeaderView(header);

        routeRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                Toast.makeText(MyRouteActivity.this, "onRefresh", Toast.LENGTH_SHORT).show();
                routeRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
//                Toast.makeText(MyRouteActivity.this, "onLoadMore", Toast.LENGTH_SHORT).show();
                loadPage();
                routeRecyclerView.loadMoreComplete();
                routeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

    }
}
