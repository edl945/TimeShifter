package timeshifter.dtcq.yunlong.dtcqtimeshifter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;

import java.util.Iterator;
import java.util.List;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.util.Log;
import java.net.SocketException;
import android.os.Build;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 类说明：  Apk工具类
 *
 * @author  Cundong
 * @version 1.1
 * @date    2013-9-6
 */
public class ApkUtils {

    /**
     * 获取已安装apk的PackageInfo
     *
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getInstalledApkPackageInfo(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);

        Iterator<PackageInfo> it = apps.iterator();
        while (it.hasNext()) {
            PackageInfo packageinfo = it.next();
            String thisName = packageinfo.packageName;
            if (thisName.equals(packageName)) {
                return packageinfo;
            }
        }

        return null;
    }

    /**
     * 判断apk是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return installed;
    }

    public static String getInstalledPackageName(Context context, String appname)
    {
        PackageManager pageManage = context.getPackageManager();
        List<PackageInfo> packages = pageManage.getInstalledPackages(0);
        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            String pagName = packageInfo.packageName;
            if (appName.indexOf(appname) != -1)
                return pagName;
        }
        return "";
    }

    /**
     * 获取已安装Apk文件的源Apk文件
     * 如：/data/app/com.sina.weibo-1.apk
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getSourceApkPath(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return null;

        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            return appInfo.sourceDir;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 安装Apk
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static void uninstallApk(Context context, String packageName)
    {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean silentInstallApk(Context context, String path)
    {
        boolean result = false;
        Process process = null;
        OutputStream out = null;
        InputStream in = null;
        String state = null;
        try {
            // 请求root
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            // 调用安装，将文件写入到process里面
            out.write(("pm install -r " + path + "\n").getBytes());
            //这里拿到输出流，开始安装操作
            in = process.getInputStream();
            int len = 0;
            byte[] bs = new byte[256];
            while (-1 != (len = in.read(bs))) {
                state = new String(bs, 0, len);
                if (state.equals("Success\n")) {
                    // 安装成功后的操作
                    result = true;
                    //Log.e("成功", state);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean silentUninstallApk(Context context, String packageName)
    {
        boolean result = false;
        Process process = null;
        OutputStream out = null;
        InputStream in = null;
        String state = null;
        try {
            // 请求root
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            // 调用安装，将文件写入到process里面
            out.write(("pm uninstall " + packageName + "\n").getBytes());
            //这里拿到输出流，开始安装操作
            in = process.getInputStream();
            int len = 0;
            byte[] bs = new byte[256];
            while (-1 != (len = in.read(bs))) {
                state = new String(bs, 0, len);
                if (state.equals("Success\n")) {
                    // 安装成功后的操作
                    result = true;
                    //Log.e("成功", state);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getMacAddress(Context context) {
        int apiLevel =  Build.VERSION.SDK_INT;
        String macAddress = null;
        if (apiLevel >= 23){
            Log.i("----This is a android 6 or later,api level --%d--","");
        }
        /*
        if (isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            Log.i("----getMacAddress--0--", "" + wifiInfo.getMacAddress());
            macAddress = wifiInfo.getMacAddress();
            return macAddress;
        } else {
            Log.i("----AccessWifiStateNotAuthorized--0--", "");
        }
        */

        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "020000000002";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "020000000002";
        }
        Log.i("----getMacAddress--1--", "" + buf.toString());
        return macAddress;
    }



}