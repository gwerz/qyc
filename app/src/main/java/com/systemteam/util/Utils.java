package com.systemteam.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.systemteam.R;
import com.systemteam.bean.Config;
import com.systemteam.custom.SelectDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.systemteam.util.Constant.UPGRADE_URL;

public class Utils {
    private static PowerManager.WakeLock mWakeLock;
    public static InputMethodManager imm;
    Activity activity;
    public Utils(Activity activity){
        this.activity=activity;
        if(imm==null)
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    public  void hideIMM() {
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public  void showIMM() {
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    // 距离转换
    public static String distanceFormatter(int distance) {
        if (distance < 1000) {
            return distance + "米";
        } else if (distance % 1000 == 0) {
            return distance / 1000 + "公里";
        } else {
            DecimalFormat df = new DecimalFormat("0.0");
            int a1 = distance / 1000; // 十位

            double a2 = distance % 1000;
            double a3 = a2 / 1000; // 得到个位

            String result = df.format(a3);
            double total = Double.parseDouble(result) + a1;
            return total + "公里";
        }
    }

    // 时间转换
    public static String timeFormatter(int minute) {
        if (minute < 60) {
            return minute + "分钟";
        } else if (minute % 60 == 0) {
            return minute / 60 + "小时";
        } else {
            int hour = minute / 60;
            int minute1 = minute % 60;
            return hour + "小时" + minute1 + "分钟";
        }
    }

    /**
     * ms 转换成 00:00:00
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        time = time/ 1000;
        String strHour = "" + (time / 3600);
        String strMinute = "" + time % 3600 / 60;
        String strSecond = "" + time % 3600 % 60;

        strHour = strHour.length() < 2 ? "0" + strHour : strHour;
        strMinute = strMinute.length() < 2 ? "0" + strMinute : strMinute;
        strSecond = strSecond.length() < 2 ? "0" + strSecond : strSecond;

        String strRsult = "";

        if (!strHour.equals("00")) {
            strRsult += strHour + ":";
        }

        strRsult += strMinute + ":";

        strRsult += strSecond;

        return strRsult;
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static void setSpannableStr(TextView textView, String str, int startIndex, int endIndex,
                                       float proporation, int color) {
        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(proporation);
        spannableString.setSpan(sizeSpan01, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
    }

    public static String getDateFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static void acquireWakeLock(Context context) {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            mWakeLock.setReferenceCounted(true);
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    public static void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    /**
     *判断当前应用程序处于前台还是后台
     */
    public static boolean isTopActivity(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(1000);
        if (myList.size() <= 0) {
            return false;
        }
        int size = myList.size();
        for (int i = 0; i < size; i++) {
            String mName = myList.get(i).service.getClassName().toString();
//            LogTool.d("mName="+mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static boolean isGpsOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps ) {
            return true;
        }

        return false;
    }
    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void turnGPSOn(Context context) {
        Intent intent = new Intent("Android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static void showDialog(Context context){
        SelectDialog selectDialog = new SelectDialog(context, R.style.dialog);//创建Dialog并设置样式主题
        Window win = selectDialog.getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        params.x = -80;//设置x坐标
//        params.y = -60;//设置y坐标
        params.gravity = Gravity.CENTER;
        win.setAttributes(params);
        selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        selectDialog.show();

    }
    public static final String APP_BAIDU_MAP = "com.baidu.BaiduMap";
    public static final String APP_AMAP = "com.autonavi.minimap";
    /**
     * 检测是否有某个应用
     * */
    public static boolean hasApp(Context ctx, String packageName) {
        PackageManager manager = ctx.getPackageManager();
        List<PackageInfo> apps = manager.getInstalledPackages(0);
        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                if (apps.get(i).packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void showDialog(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    /**
     * 手机号验证
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isEmail(String email){
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    public static float formatDouble2(double d) {
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.floatValue();
    }

    private static final double EARTH_RADIUS = 6378137;
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     */
    public static double GetDistance(double lng1, double lat1, double lng2, double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.abs(Math.round(s * 10000) / 10000);
        return s;
    }

    //TODO 校验文案，不能出现小骑手相关的文字
    public static void showProtocol(Context context, int typeDialog){
        final Dialog dialog = new Dialog(context, R.style.MyDialog);
        dialog.setContentView(R.layout.layout_protocol_pay);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.menu_icon:
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        int title = R.string.setting_pay;
        int content = R.string.protocol_pay;
        String url = "http://1.rockingcar.applinzi.com/LegalStatement.html";
        switch (typeDialog){
            case Constant.GUIDE_TYPE_PAY:
                title = R.string.setting_pay;
                content = R.string.protocol_pay;
                url = "http://1.rockingcar.applinzi.com/PayStatement.html";
                break;
            case Constant.GUIDE_TYPE_PROCOTOL:
                title = R.string.setting_protocol;
                content = R.string.protocol_user;
                url = "http://1.rockingcar.applinzi.com/LegalStatement.html";
                break;
        }
        WebView webview = (WebView) dialog.findViewById(R.id.wv_content);
        webview.loadUrl(url);
        ((TextView)dialog.findViewById(R.id.tv_title)).setText(context.getString(title));
        /*TextView tvCotent = (TextView)dialog.findViewById(R.id.tv_content);
        (tvCotent).setText(context.getString(content));
        tvCotent.setMovementMethod(ScrollingMovementMethod.getInstance());*/
        dialog.show();
    }

    private static ArrayList<Marker> markers = new ArrayList<Marker>();

    /**
     * 添加模拟测试的车的点
     */
    public static void addEmulateData(AMap amap, LatLng center) {
        if (markers.size() == 0) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                    .fromResource(R.drawable.bike_icon);

            for (int i = 0; i < 10; i++) {
                double latitudeDelt;
                double longtitudeDelt;
                /*if(i%2==0) {
                    latitudeDelt = (Math.random() - 0.5) * 0.1;
                    longtitudeDelt = (Math.random() - 0.5) * 0.1;
                }else{
                    latitudeDelt = (Math.random() - 0.5) * 0.01;
                    longtitudeDelt = (Math.random() - 0.5) * 0.01;
                }*/
                if(i%2==0) {
                    latitudeDelt = (Math.random() - 0.5) * 0.03;
                    longtitudeDelt = (Math.random() - 0.5) * 0.03;
                }else{
                    latitudeDelt = (Math.random() - 0.5) * 0.003;
                    longtitudeDelt = (Math.random() - 0.5) * 0.003;
                }
                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.setFlat(true);
//                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.icon(bitmapDescriptor);

                markerOptions.position(new LatLng(center.latitude + latitudeDelt, center.longitude + longtitudeDelt));
                Marker marker = amap.addMarker(markerOptions);
                markers.add(marker);
            }
        } else {
            for (Marker marker : markers) {
                double latitudeDelt = (Math.random() - 0.5) * 0.1;
                double longtitudeDelt = (Math.random() - 0.5) * 0.1;
                marker.setPosition(new LatLng(center.latitude + latitudeDelt, center.longitude + longtitudeDelt));

            }
        }
    }

    /**
     * 移除marker
     */
    public static void removeMarkers() {
        for (Marker marker : markers) {
            marker.remove();
            marker.destroy();
        }
        markers.clear();
    }

    public static boolean isSuperAdmin(String phoneNum){
        if(phoneNum != null &&
                (phoneNum.equalsIgnoreCase("15811112222") ||
                        phoneNum.equalsIgnoreCase("15817438761") ||
                        phoneNum.equalsIgnoreCase("15812121214") ||
                        phoneNum.equalsIgnoreCase("15814551455"))) {
            return true;
        }
        return false;
    }

    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean checkUpgrade(Activity activity, Config config, boolean isByUser){
        if(config == null || config.getValue() == null){
            return false;
        }
        int value = config.getValue().intValue();
        int versonCode = Utils.getVersionCode(activity);
        int typeUpgrade = -1;
        if(config.getMin() != null && config.getMin().intValue() > versonCode){
            typeUpgrade = Constant.TYPE_UPGRADE_FORCE;
        }else if(versonCode < value){
            typeUpgrade = Constant.TYPE_UPGRADE_NORMAL;
            if(config.getType() != null && config.getType().intValue() == Constant.TYPE_UPGRADE_FORCE){
                typeUpgrade = Constant.TYPE_UPGRADE_FORCE;
            }
        }
        if(typeUpgrade > -1){
            showUpgradeDialog(activity, typeUpgrade, isByUser);
            return true;
        }else {
            return false;
        }
    }

    public static void showUpgradeDialog(final Activity activity, int type, boolean isByUser) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final String strNow = sdf.format(new Date());
        String strDate = ProtocolPreferences.getDateUpgrade(activity);
        if(type == Constant.TYPE_UPGRADE_NORMAL && strNow.equalsIgnoreCase(strDate) && !isByUser){
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.new_version_title));
        if(type == Constant.TYPE_UPGRADE_FORCE){
            alertDialog.setMessage(activity.getString(R.string.new_version_content_force));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.new_version_force),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri content_url = Uri.parse(UPGRADE_URL);
                            intent.setData(content_url);
                            activity.startActivity(intent);
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                            System.exit(0);
                        }
                    });
            alertDialog.setCancelable(false);
        }else {
            alertDialog.setMessage(activity.getString(R.string.new_version_content));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.new_version_upgrade),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri content_url = Uri.parse(UPGRADE_URL);
                            intent.setData(content_url);
                            activity.startActivity(intent);
                        }
                    });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            ProtocolPreferences.setDateUpgrade(activity, strNow);
        }
        alertDialog.show();
    }

}
