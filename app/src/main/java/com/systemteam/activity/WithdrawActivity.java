package com.systemteam.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.MyCarAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.adapter.WithdrawAdapter;
import com.systemteam.bean.BankCard;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.Withdraw;
import com.systemteam.util.Utils;
import com.systemteam.view.IconEditTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_AMOUNT;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.MSG_WITHDRAW_SUCCESS;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;
import static com.systemteam.util.Constant.WITHDRAW_AMOUNT_DEFAULT;
import static com.systemteam.util.Constant.WITHDRAW_DAYS_DEFAULT;

public class WithdrawActivity extends BaseActivity implements MyCarAdapter.OnItemClickListener,
        MyCarAdapter.OnItemLongClickListener{
    private BankCard mBankCard;
    private TextView mTvUserName, mTvPhone, mTvCard, mTvInfo;
    private boolean isSave = false;
    private float mAmout = 0f;
    private Withdraw mWithdraw;
    private boolean isWithDrawSuccess = false;
    XRecyclerView routeRecyclerView;
    WithdrawAdapter routeAdapter;
    List<Object> routeList;

    private static class MyHandler extends Handler {
        private WeakReference<WithdrawActivity> mActivity;

        public MyHandler(WithdrawActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WithdrawActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_UI:
                    theActivity.initInfo(theActivity.mBankCard);
                    break;
                case MSG_WITHDRAW_SUCCESS:
                    theActivity.refreshAmout();
                    theActivity.initAdapterList();
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.withdraw_title);
        mTvUserName = (TextView) findViewById(R.id.tv_name);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mTvCard = (TextView) findViewById(R.id.tv_cardnum);
        mTvInfo = (TextView) findViewById(R.id.tv_cardinfo);

        routeRecyclerView = (XRecyclerView) findViewById(R.id.recyclerview_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        routeList = new ArrayList<>();
        routeAdapter = new WithdrawAdapter(mContext, routeList);
        routeAdapter.setOnClickListener(this);
        routeAdapter.setOnLongClickListener(this);
        routeRecyclerView.setAdapter(routeAdapter);
        routeRecyclerView.addItemDecoration(new MyRouteDividerDecoration(10));

        routeRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        routeRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallScale);
        routeRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        routeRecyclerView.setPullRefreshEnabled(false);

        routeRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                routeRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                routeRecyclerView.loadMoreComplete();
                routeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {
        mAmout = getIntent().getFloatExtra(BUNDLE_KEY_AMOUNT, 0f);
        refreshAmout();
        mUser = BmobUser.getCurrentUser(MyUser.class);
        requestInfo();
        initAdapterList();
    }

    private void refreshAmout(){
        ((TextView) findViewById(R.id.tv_amount)).setText(getString(R.string.amout_format, mAmout));
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    private void initInfo(BankCard bankCard){
        if(bankCard == null){
            toast(getString(R.string.bankcard_no_hint));
            showInputDialog();
        }else {
            mTvUserName.setText(bankCard.getUserName());
            mTvPhone.setText(bankCard.getPhone());
            mTvCard.setText(bankCard.getBankName() + "  " +bankCard.getCardNumber());
        }
    }

    @Override
    protected void onDestroy() {
        if(isWithDrawSuccess){
            setResult(RESULT_OK, new Intent());
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

    }

    public void doBankEdit(View view){
        showInputDialog();
    }

    public Dialog showInputDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.layout_bankcard_input);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final IconEditTextView ietUserName = (IconEditTextView)dialog.findViewById(R.id.iet_username);
        final IconEditTextView ietbankName = (IconEditTextView)dialog.findViewById(R.id.iet_cardname);
        final IconEditTextView ietCardNum = (IconEditTextView)dialog.findViewById(R.id.iet_cardnum);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_unlock:
                        String userName = ietUserName.getInputText();
                        String bankName = ietbankName.getInputText();
                        String cardNum = ietCardNum.getInputText();
                        if(TextUtils.isEmpty(userName)){
                            toast(mContext.getString(R.string.userName_no));
                            return;
                        }
                        if(TextUtils.isEmpty(bankName)){
                            toast(mContext.getString(R.string.userName_no));
                            return;
                        }
                        if(TextUtils.isEmpty(cardNum)){
                            toast(mContext.getString(R.string.userName_no));
                            return;
                        }
                        mBankCard = new BankCard(cardNum, bankName, mUser, userName, mUser.getMobilePhoneNumber());
                        saveOrUpdateBankCard(mBankCard, isSave);
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                    case R.id.menu_icon:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btn_unlock).setOnClickListener(listener);
        dialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        (ietUserName).setText(mUser.getUsername());
        ((IconEditTextView)dialog.findViewById(R.id.iet_phone)).setText(mUser.getMobilePhoneNumber());
        if(mBankCard != null){
            ietUserName.setText(mBankCard.getUserName());
            ietbankName.setText(mBankCard.getBankName());
            ietCardNum.setText(mBankCard.getCardNumber());
        }
        dialog.show();
//        mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        return dialog;
    }

    private void saveOrUpdateBankCard(BankCard bankCard, boolean isSave){
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        if(isSave){
            addSubscription(bankCard.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    mProgressHelper.dismissProgressDialog();
                    if(e==null){
                        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                    }else{
                        toast(getString(R.string.submit_faile));
                        loge(e);
                    }
                }
            }));
        }else {
            addSubscription(bankCard.update(mBankCard.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    mProgressHelper.dismissProgressDialog();
                    if(e==null){
                        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                    }else{
                        toast(getString(R.string.submit_faile));
                        loge(e);
                    }
                }
            }));
        }
    }

    private void requestInfo(){
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        BmobQuery<BankCard> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, mUser.getObjectId());
        addSubscription(query.findObjects(new FindListener<BankCard>() {

            @Override
            public void done(List<BankCard> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    if(object != null && object.size() > 0){
                        mBankCard = object.get(0);
                        isSave = false;
                    }else {
                        isSave = true;
                        mBankCard = null;
                    }
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                }else{
                    toast(getString(R.string.initing_fail));
                    loge(e);
                }
            }
        }));
    }

    private void initAdapterList() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<Withdraw> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, user.getObjectId());
        addSubscription(query.findObjects(new FindListener<Withdraw>() {

            @Override
            public void done(List<Withdraw> object, BmobException e) {
                if(e==null){
                    routeList.clear();
                    if(object != null && object.size() > 0){
                        routeList.add("");
                        routeList.addAll(object);
                    }
                    routeAdapter.notifyDataSetChanged();
                }else{
                    loge(e);
                }
            }
        }));
    }

    public void doSubmit(View view){
        if(checkWithdrawEnable()){
            requestWithdraw();
        }
    }

    private boolean checkWithdrawEnable(){
        if(mAmout < WITHDRAW_AMOUNT_DEFAULT){
            Utils.showDialog(mContext, getString(R.string.tip), getString(R.string.withdraw_refund));
            return false;
        }else if(routeList.size() <= 1){
            return true;
        }else {
            //TODO 判断1.最近30天内是否有提现 2.是否有为完成的提现记录
            Utils.showDialog(mContext, getString(R.string.tip), getString(R.string.withdraw_refund_content));
            return false;
        }
    }

    private void requestWithdraw(){
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        mWithdraw = new Withdraw(mUser, mBankCard, mAmout);
        addSubscription(mWithdraw.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    isWithDrawSuccess = true;
                    mAmout = 0f;
                    Utils.showDialog(mContext, getString(R.string.submit_success),
                        getString(R.string.withdraw_success_content, WITHDRAW_DAYS_DEFAULT));
                    mHandler.sendEmptyMessage(MSG_WITHDRAW_SUCCESS);
                }else{
                    Utils.showDialog(mContext, getString(R.string.fail_submit),
                            getString(R.string.withdraw_fail_content));
                    loge(e);
                }
            }
        }));
    }
}