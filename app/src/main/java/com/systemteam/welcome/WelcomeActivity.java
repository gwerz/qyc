package com.systemteam.welcome;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.welcome.fragment.outlayer.LoginAnimFragment;
import com.systemteam.welcome.fragment.outlayer.WelcomAnimFragment;
import com.systemteam.welcome.utils.DisplayUtil;
import com.systemteam.welcome.view.ParentViewPager;

public class WelcomeActivity extends BaseActivity {

    private final int FRAGMENT_WELCOMEANIM = 0;
    private final int FRAGMENT_LOGINANIM = 1;

    private ImageView iv_logo;
    private ParentViewPager vp_parent;

    private float mLogoY;
    private AnimatorSet mAnimatorSet;

    private WelcomAnimFragment mWelcomAnimFragment;
    private LoginAnimFragment mLoginAnimFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findView();
        setClick();
        init();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    private void findView() {
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        vp_parent = (ParentViewPager) findViewById(R.id.vp_parent);
    }

    private void setClick() {
        vp_parent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case FRAGMENT_WELCOMEANIM:
                        break;
                    case FRAGMENT_LOGINANIM:
                        vp_parent.mLoginPageLock = true;
                        iv_logo.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(mLogoY == 0){
                                    mLogoY = ViewHelper.getY(iv_logo);
                                }
                                playLogoInAnim();
                            }
                        },500);
                        vp_parent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLoginAnimFragment.playInAnim();
                            }
                        }, 300);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void init() {
        ParentFragmentStatePagerAdapter adapter = new ParentFragmentStatePagerAdapter(getSupportFragmentManager());
        vp_parent.setAdapter(adapter);
        vp_parent.setCurrentItem(1);
    }

    private void playLogoInAnim(){
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(iv_logo, "scaleX", 1.0f, 0.5f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(iv_logo, "scaleY", 1.0f, 0.5f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(iv_logo, "y", mLogoY, DisplayUtil.dip2px(WelcomeActivity.this, 15));

        if(mAnimatorSet != null && mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(anim1).with(anim2);
        mAnimatorSet.play(anim2).with(anim3);
        mAnimatorSet.setDuration(1000);
        mAnimatorSet.start();
    }

    @Override
    public void onClick(View v) {

    }

    public class ParentFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        public ParentFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_WELCOMEANIM:
                    mWelcomAnimFragment = new WelcomAnimFragment();
                    vp_parent.setWelcomAnimFragment(mWelcomAnimFragment);
                    mWelcomAnimFragment.setWelcomAnimFragmentInterface(new WelcomAnimFragment.WelcomAnimFragmentInterface() {
                        @Override
                        public void onSkip() {
                            vp_parent.setCurrentItem(1);
                        }
                    });
                    return mWelcomAnimFragment;
                case FRAGMENT_LOGINANIM:
                    mLoginAnimFragment = new LoginAnimFragment();
                    return mLoginAnimFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * 点击返回键退出程序
     */
    private static Boolean isExit = false;
    private Handler mHandler = new Handler();
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*if (isExit == false) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                finish();
                System.exit(0);
            }*/
            finish();
        }
        return false;
    }
}
