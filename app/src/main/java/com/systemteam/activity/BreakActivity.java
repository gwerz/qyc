package com.systemteam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.util.Constant;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.BUNDLE_TYPE_MENU;
import static com.systemteam.util.Constant.REQUEST_CODE;

public class BreakActivity extends BaseActivity {
    private int mType = -1;
    private LinearLayout mLlLock;
    private TableLayout mTlBreak;
    private TextView mTvCode;
    private String mCarNo;
    private CheckBox[] mCbArray;
    private Car mCar;
    private EditText mEtDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        mContext = this;
        mType = getIntent().getIntExtra(BUNDLE_TYPE_MENU, 0);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTlBreak = (TableLayout) findViewById(R.id.tl_break);
        mLlLock = (LinearLayout) findViewById(R.id.ll_lock);
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
        if(mType == 0){
            initToolBar(this, R.string.break_lock_title);
            mTlBreak.setVisibility(View.GONE);
            mLlLock.setVisibility(View.VISIBLE);
        }else {
            initToolBar(this, R.string.break_title);
            mTlBreak.setVisibility(View.VISIBLE);
            mLlLock.setVisibility(View.GONE);
        }
        mCbArray = new CheckBox[8];
        mCbArray[0] = (CheckBox) findViewById(R.id.cb1);
        mCbArray[1] = (CheckBox) findViewById(R.id.cb2);
        mCbArray[2] = (CheckBox) findViewById(R.id.cb3);
        mCbArray[3] = (CheckBox) findViewById(R.id.cb4);
        mCbArray[4] = (CheckBox) findViewById(R.id.cb5);
        mCbArray[5] = (CheckBox) findViewById(R.id.cb6);
        mCbArray[6] = (CheckBox) findViewById(R.id.cb7);
        mCbArray[7] = (CheckBox) findViewById(R.id.cb8);
        mEtDescription = (EditText) findViewById(R.id.et_description);
    }

    @Override
    protected void initData() {
        checkCarExist(mCarNo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_icon:
                finish();
                break;
        }
    }

    public void doBreakSubmit(View view){
        if(mCarNo == null || TextUtils.isEmpty(mCarNo)){
            toast(getString(R.string.break_carNo_no));
            return;
        }else {
            if(mCar == null){
                toast(getString(R.string.break_car_no));
                return;
            }
            Car newCar = new Car();
            if(mType == 0){
                newCar.setStatus(Constant.BREAK_STATUS_LOCK);
            }else {
                int status = -1;
                for(int i = 0; i < mCbArray.length; i++){
                    if(mCbArray[i].isChecked()){
                        status *= 10;
                        status += (i + 1);
                    }
                }
                newCar.setStatus(status);
            }
            String description = String.valueOf(mEtDescription.getText());
            if(!TextUtils.isEmpty(description)){
                newCar.setMark(description);
            }
            addSubscription(newCar.update(mCar.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        toast(getString(R.string.break_submit_success));
                    }else{
                        toast(getString(R.string.submit_faile));
                        loge(e);
                    }
                }
            }));
        }
    }

    public void gotoScan(View view){
        startActivityForResult(new Intent(BreakActivity.this, QRCodeScanActivity.class),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mCarNo = data.getStringExtra(BUNDLE_KEY_CODE);
            mTvCode.setText(getString(R.string.break_carNo) + mCarNo);
        }
    }

    private void checkCarExist(String carNo) {
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", carNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                if(e==null){
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                    }
                }else{
                    loge(e);
                }
            }
        }));
    }
}
