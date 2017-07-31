package com.systemteam.util;

/**
 * Created by gaolei on 17/2/24.
 */

public class Constant {
    //默认值
    public static final float EARN_RATE_DEFAULT = 0.3f;
    public static final float COST_BASE_DEFAULT = 1.0f;
    public static final long TIME_ONCE_ACTIVE = 3 * 60 * 1000;
    public static int COUPON_DEFAULT = 3;

    public static final int STATUS_NORMAL = 0;
    public static final int BREAK_STATUS_LOCK = -1;
    public static final int BREAK_STATUS = -2;

    public static int WITHDRAW_DAYS_DEFAULT = 15;
    public static float WITHDRAW_AMOUNT_DEFAULT = 300f;
    public static final int WITHDRAW_SUCCESS = 10;
    public static final int WITHDRAW_FAIL = -1;

    public static final int USER_TYPE_CUSTOMER = 1;
    public static final int USER_TYPE_NORMAL = 0;

    public static final float SCALING_MAP = 18.0f;

    public static final String REQUEST_KEY_BY_USER = "author";
    public static final String TIME_ONCE_ACTIVE_STR = "03 : 00";
    public static final String  BUNDLE_USER = "key_user";
    public static final String  BUNDLE_TYPE_MENU = "key_type";
    public static final String  BUNDLE_KEY_CODE = "key_code";
    public static final String  BUNDLE_KEY_UNLOCK = "key_unlock";
    public static final String  BUNDLE_KEY_ALL_EARN = "key_all_earn";
    public static final String  BUNDLE_KEY_ALL_WITHDRAW = "key_all_withdraw";
    public static final String  BUNDLE_KEY_ALL_COST = "key_all_cost";
    public static final String  BUNDLE_KEY_AMOUNT = "key_amount";
    public static final int  REQUEST_IMAGE = 100;
    public static final int  REQUEST_CODE = 101;

    public static final int DISMISS_SPLASH = 0x122;
    public static final int MSG_RESPONSE_SUCCESS = 0x123;
    public static final int MSG_UPDATE_UI = 0x124;
    public static final int MSG_LOGOOUT = 0x125;
    public static final int MSG_WITHDRAW_SUCCESS = 0x126;


    public static final String SHAERD_FILE_NAME = "shared_file_name";
    public static final String ACTION_BROADCAST_ACTIVE = "com.locationreceiver";

    public static final String FORMAT_TIME = "0%s : %s";

}
