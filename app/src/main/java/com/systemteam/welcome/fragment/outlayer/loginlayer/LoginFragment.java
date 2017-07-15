package com.systemteam.welcome.fragment.outlayer.loginlayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.fragment.BaseFragment;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import rx.Subscriber;

import static com.systemteam.BaseActivity.log;
import static com.systemteam.BaseActivity.loge;


/**
 * 登录
 */
public class LoginFragment extends BaseFragment {
    private EditText mEtPhone, mEtPsd;
    private Button mBtnSend;
    private String mPhone, mPwd;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_login, null);
        mContext = getActivity();
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(view);
        initData();
        return view;
    }

    //TODO login
    @Override
    public void onClick(View v) {
        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        mPhone = String.valueOf(mEtPhone.getText());
        mPwd = String.valueOf(mEtPsd.getText());
        if(TextUtils.isEmpty(mPhone)){
            Toast.makeText(getActivity(), R.string.smssdk_write_mobile_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()){
            case R.id.btn_login:
                if(TextUtils.isEmpty(mPwd)){
                    Toast.makeText(mContext, R.string.welcomanim_title_psw_hint,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                testLogin();
                break;
            case R.id.iv_qq:
                onLoginQQ(v);
                break;
            case R.id.iv_weibo:
                onLoginWeibo(v);
                break;
            case R.id.iv_wechat:
                onLoginWechat(v);
                break;
        }
    }

    @Override
    protected void initView(View view) {
        TextView tv_down = (TextView)view.findViewById(R.id.tv_down);
        tv_down.setOnClickListener(this);
        view.findViewById(R.id.btn_login).setOnClickListener(this);

        mEtPhone = (EditText) view.findViewById(R.id.et_phone);
        mEtPsd = (EditText) view.findViewById(R.id.et_psw);
        mBtnSend = (Button) view.findViewById(R.id.btn_login);
        mBtnSend.setOnClickListener(this);
        view.findViewById(R.id.iv_qq).setOnClickListener(this);
        view.findViewById(R.id.iv_wechat).setOnClickListener(this);
        view.findViewById(R.id.iv_weibo).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    /**
     * 注意下如果返回206错误 一般是多设备登录导致
     */
    private void testLogin() {
        final BmobUser user = new BmobUser();
        user.setMobilePhoneNumber(mPhone);
        user.setPassword(mPwd);
        //login回调
		/*user.login(new SaveListener<BmobUser>() {

			@Override
			public void done(BmobUser bmobUser, BmobException e) {
				if(e==null){
					toast(user.getUsername() + "登陆成功");
					testGetCurrentUser();
				}else{
					loge(e);
				}
			}
		});*/
        //v3.5.0开始新增加的rx风格的Api
        user.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
            @Override
            public void onCompleted() {
                log("----onCompleted----");
            }

            @Override
            public void onError(Throwable e) {
                loge(new BmobException(e));
            }

            @Override
            public void onNext(BmobUser bmobUser) {
                toast(mContext, bmobUser.getUsername() + "登陆成功");
                testGetCurrentUser();
            }
        });
    }

    /**
     * 获取本地用户
     */
    private void testGetCurrentUser() {
		MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
		if (myUser != null) {
			log("本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
					+ ",age = "+ myUser.getAge());
		} else {
			toast(mContext, "本地用户为null,请登录。");
		}
		BikeApplication application = (BikeApplication) getActivity().getApplication();
        application.setmUser(myUser);
        //V3.4.5版本新增加getObjectByKey方法获取本地用户对象中某一列的值
        /*String username = (String) BmobUser.getObjectByKey("username");
        Integer age = (Integer) BmobUser.getObjectByKey("age");
        Boolean sex = (Boolean) BmobUser.getObjectByKey("sex");
        JSONArray hobby= (JSONArray) BmobUser.getObjectByKey("hobby");
        JSONArray cards= (JSONArray) BmobUser.getObjectByKey("cards");
        JSONObject banker= (JSONObject) BmobUser.getObjectByKey("banker");
        JSONObject mainCard= (JSONObject) BmobUser.getObjectByKey("mainCard");
        log("username："+username+",\nage："+age+",\nsex："+ sex);
        log("hobby:"+(hobby!=null?hobby.toString():"为null")+"\ncards:"+(cards!=null ?cards.toString():"为null"));
        log("banker:"+(banker!=null?banker.toString():"为null")+"\nmainCard:"+(mainCard!=null ?mainCard.toString():"为null"));*/
    }

    public void onLoginWeibo(View view){
        /*ILoginManager iLoginManager = new WeiboLoginManager(getActivity());
        iLoginManager.login(new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> userInfo) {
                //TODO
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }
        });*/
    }

    public void onLoginWechat(View view){
        /*ILoginManager iLoginManager = new WechatLoginManager(getActivity());
        iLoginManager.login(new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> userInfo) {
                //TODO
            }

            @Override
            public void onError() {
                //TODO
            }

            @Override
            public void onCancel() {
                //TODO
            }
        });*/
    }

    public void onLoginQQ(View view){
        /*ILoginManager iLoginManager = new QQLoginManager(getActivity());
        iLoginManager.login(new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> userInfo) {
                //TODO
            }

            @Override
            public void onError() {
                //TODO
            }

            @Override
            public void onCancel() {
                //TODO
            }
        });*/
    }
}
