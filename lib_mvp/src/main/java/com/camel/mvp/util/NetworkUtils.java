package com.camel.mvp.util;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Logger;
import static android.content.Context.WIFI_SERVICE;


/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/11/10
 *    desc   : 工具类: 网络相关
 *             API  : 检查网络是否可用 / 检查网络类型 / 获取 IP 地址 等
 * </pre>
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private NetworkUtils() {
    }

    /**
     * 判断网络是否可用
     *
     * @return 是否可用
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    /**
     * 判断网络是否连接
     *
     * @return 是否连接
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * 判断当前网络是否连接并且是否可用
     *
     * @return 是否连接并且可用
     */
    public static boolean isConnectedAndAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return isConnected(context) && isAvailable(context);
    }

    /**
     * 判断移动数据是否打开
     *
     * @return 是否打开
     */
    public static boolean getDataEnabled(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查当前网络类型是否为移动网络
     *
     * @return 是否为移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前网络类型是否为 移动4G
     *
     * @return 是否为 移动4G
     */
    public static boolean isMobile4G(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 判断 WIFI 是否打开
     *
     * @return 是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 检查当前网络类型是否为 WIFI
     *
     * @return 是否为 WIFI
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 WIFI 是否已连接
     *
     * @return 是否已连接
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 打开或关闭 WIFI
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>}</p>
     *
     * @param enabled 是否可用 (可用即为打开)
     */
    @RequiresPermission(Manifest.permission.CHANGE_WIFI_STATE)
    public static void setWifiEnabled(Context context,boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 获取网络运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return 网络运营商名称
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    /**
     * 获取 IP地址
     *
     * @return IP地址
     */
    public static String getIPAddress() {
        return getIPAddress(true);
    }

    /**
     * 获取 IP地址
     *
     * @param useIPv4 是否使用 IPv4 地址
     * @return IP地址
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                // 防止小米手机返回10.0.2.15
                if (!ni.isUp()) continue;
                for (Enumeration<InetAddress> addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        boolean isIPv4 = hostAddress.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) return hostAddress;
                        } else {
                            if (!isIPv4) {
                                int index = hostAddress.indexOf('%');
                                return index < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, index).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 定义一个方法用于获取当前连接的wifi信息
     */
    public static WifiInfo getWifiInfo(Context context){
        WifiManager wifi_service = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        return wifiInfo;
    }

    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 判断以太网网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isEthernetConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mInternetNetWorkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            boolean hasInternet = !ObjectUtils.isAnyOneNull(mInternetNetWorkInfo) && mInternetNetWorkInfo.isConnected() && mInternetNetWorkInfo.isAvailable();
            return hasInternet;
        }
        return false;
    }

    public static String getEthernetMac(Context context){
        String ethernetMac = "";
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mInternetNetWorkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            if (mInternetNetWorkInfo != null) {
                ethernetMac = mInternetNetWorkInfo.getExtraInfo();
            }
        return ethernetMac;
    }


    /**
     * 定义一个方法用于获取以太网有线网络的ip地址
     * @param context
     * @return
     */
    public static String getEthernetIp(Context context) {
        String ethernetIp = "";
        try {
            //读取以太网ip信息
            Process pp = Runtime.getRuntime().exec("ifconfig eth0");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 30; i++) {
                String str = input.readLine();
                if (str != null) {
                    if (str.contains("inet addr")){
                        ethernetIp = str.substring(str.indexOf(":") + 1,str.indexOf("B") - 2);
//                        Log.e(TAG,"获取到的以太网信息ip地址为：  " + ethernetIp);
                        break;
                    }
                    //以太网的Mac地址
//                    else if (str.contains("HWaddr")){
//                        String macAddress = str.substring(str.indexOf("HWaddr") + 6,
//                                str.length());
//                        Log.e(TAG,"获取到的以太网信息Mac地址为：  " + macAddress);
//                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (Exception ex) {
            //赋予默认值
            Log.e(TAG,"获取以太网信息失败：  原因：" + ex.getMessage());
            ex.printStackTrace();
        }
        return ethernetIp;
    }


}
