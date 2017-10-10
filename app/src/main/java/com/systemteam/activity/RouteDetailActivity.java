package com.systemteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;

/**
 * @Description 使用完成后点击toolbar返回值Main界面，Main界面没有退出使用中模式
 * @author scofield.hhl@gmail.com
 * @time 2017/8/18
 */
public class RouteDetailActivity extends BaseActivity {

    TextView total_time, total_distance, total_price;
    public static boolean completeRoute = false;
    private String mCarNo;

    public void onCreate(Bundle savedInstanceState) {
        LogTool.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        mContext = this;
        initView();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_again:
                if(!TextUtils.isEmpty(mCarNo)){
                    Intent intent = new Intent(mContext, Main2Activity.class);
                    intent.putExtra(BUNDLE_KEY_CODE, mCarNo);
                    startActivity(intent);
                }else {
                    Utils.showDialog(mContext,
                            getString(R.string.error_lock_failed), getString(R.string.break_car_no));
                }
            break;
        }
    }


    public void onDestroy() {
        LogTool.d("onDestroy");
        super.onDestroy();
        completeRoute = false;
    }

    @Override
    protected void initView() {
        initToolBar(RouteDetailActivity.this, R.string.route_detail);
        total_time = (TextView) findViewById(R.id.total_time);
        total_distance = (TextView) findViewById(R.id.total_distance);
        total_price = (TextView) findViewById(R.id.total_pricce);
    }

    protected void initToolBar(Activity act, int titleId) {
        mToolbar = (Toolbar) act.findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) act.findViewById(R.id.toolbar_title);
        mToolbar.getVisibility();
        mToolbar.setTitle("");
        if (titleId == 0) {
            mToolbarTitle.setText("");
        } else {
            mToolbarTitle.setTextColor(ContextCompat.getColor(act, R.color.white));
            mToolbarTitle.setText(titleId);
        }

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.mipmap.return_icon);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRoute = true;
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String time = intent.getStringExtra("totalTime");
        String distance = intent.getStringExtra("totalDistance");
        String price = intent.getStringExtra("totalPrice");

        total_time.setText(getString(R.string.bike_time)+ " ：" + time);
        total_distance.setText(getString(R.string.bike_distance)+ " ：" + String.valueOf(distance));
        total_price.setText(getString(R.string.bike_price)+ " ：" +
                getString(R.string.cost_num, String.valueOf(price)));
        mCarNo = intent.getStringExtra(BUNDLE_KEY_CODE);
    }

    public void finishActivity(View view) {
        completeRoute = true;
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            completeRoute = true;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
