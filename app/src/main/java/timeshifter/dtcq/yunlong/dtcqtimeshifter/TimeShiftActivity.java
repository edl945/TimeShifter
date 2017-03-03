package timeshifter.dtcq.yunlong.dtcqtimeshifter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.String;

import android.content.pm.ActivityInfo;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.WindowManager;
import android.os.Environment;
import android.text.TextUtils;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Handler;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

import android.content.pm.PackageInfo;

//new lib
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.containers.Permissions;
import java.util.concurrent.TimeoutException;


import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.net.MalformedURLException;
import android.net.Uri;

public class TimeShiftActivity extends AppCompatActivity {

    private Context mContext = null;
    private EditText mLogView = null;
    private Button btnTryInstall = null;
    private Button btnCleanAll = null;
    private TextView tvRoot = null;
    private static boolean mFullAccessRoot = false;

    public static String DTCQ_PACKAGENAME = "i.cant.help.you";
    public static final String PATH = Environment.getExternalStorageDirectory() + File.separator;
    public static final String PATCH_PATH =  "timepackage.zip";
    public static final String TEMPPATH =  "tempzip";
    public static String PATCH_ABS_PATH =  PATCH_PATH;
    public static String EXT_STORAGE_ROOT =  "";
    public static String NEW_APK_PATH = "combined_newgamepackage.apk";
    public static String NEW_APK_PATH_SIGNED = "signed_combined_newgamepackage.apk";

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    private static int version = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_shift);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mContext = getApplicationContext();
        mLogView = (EditText) findViewById(R.id.txLogView);
        btnCleanAll = (Button) findViewById(R.id.btnCleanAll);
        btnTryInstall = (Button) findViewById(R.id.btnTryInstall);
        tvRoot = (TextView) findViewById(R.id.txRoot);
        TextView txRootEx = (TextView) findViewById(R.id.txRootEx);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//keep light on

        EXT_STORAGE_ROOT = this.getExternalFilesDir(null).getAbsolutePath();
        NEW_APK_PATH = EXT_STORAGE_ROOT + File.separator + NEW_APK_PATH;
        NEW_APK_PATH_SIGNED = EXT_STORAGE_ROOT + File.separator + NEW_APK_PATH_SIGNED;

        if (!isRootSystem()){
            tvRoot.setVisibility(View.INVISIBLE);
        }else
            tvRoot.setVisibility(View.VISIBLE);

        findADtcqPackage();
        refreshButtons();

        mFullAccessRoot = rootCheck();
        if(mFullAccessRoot)
            txRootEx.setVisibility(View.VISIBLE);
        else
            txRootEx.setVisibility(View.INVISIBLE);

        checkVersion();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/" };
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    public void refreshButtons()
    {
        File file = new File(NEW_APK_PATH_SIGNED);
        if( !file.exists() ){
            btnTryInstall.setVisibility(View.INVISIBLE);
        }else
            btnTryInstall.setVisibility(View.VISIBLE);

        file = new File(NEW_APK_PATH_SIGNED);
        if( !file.exists() ){
            btnCleanAll.setVisibility(View.INVISIBLE);
        }else
            btnCleanAll.setVisibility(View.VISIBLE);
    }

    public void findADtcqPackage()
    {
        refreshUI(true);
        String[] packagelist = {
                "sh.lilith.dgame.s37wan",
                "sh.lilith.dgame.s49app",
                "sh.lilith.dgame.s91",
                "sh.lilith.dgame.anzhi",
                "sh.lilith.dgame.dangle",
                "sh.lilith.dgame.duowan",
                "sh.lilith.dgame.DK",
                "sh.lilith.dgame.guopan",
                "sh.lilith.dgame.jinshan",
                "sh.lilith.dgame.kaopu",
                "sh.lilith.dgame.kugou",
                "sh.lilith.dgame.kuwo",
                "sh.lilith.dgame.lemonfat",
                "sh.lilith.dgame.liantong",
                "sh.lilith.dgame.liantong",
                "sh.lilith.dgame.lenovo",
                "sh.lilith.dgame.lemon",
                "sh.lilith.dgame.pad.lenovo",
                "sh.lilith.dgame.n49app",
                "sh.lilith.dgame.pptv",
                "sh.lilith.dgame.putao",
                "sh.lilith.dgame.sogou",
                "sh.lilith.dgame.tuyoo",
                "sh.lilith.dgame.uc",
                "sh.lilith.dgame.vivo",
                "sh.lilith.dgame.yidongmm",
                "sh.lilith.dgame.yidongjidi",
                "sh.lilith.dgame.mi",
                "sh.lilith.dgame.yingyongbao",
                "sh.lilith.dgame.yyh",
                "sh.lilith.dgame.yiwan",
                "sh.lilith.dgame.ewan2",
                "sh.lilith.dgame.youmi",
                "sh.lilith.dgame.youyi",
                "sh.lilith.dgame.aiyouxi",
                "sh.lilith.dgame.leshi",
                "sh.lilith.dgame.pps",
                "sh.lilith.dgame.meizu",
                "sh.lilith.dgame.am",
                "sh.lilith.dgame.huawei",
                "sh.lilith.dgame.jididingzhi",
                "sh.lilith.dgame.s360",
                "sh.lilith.dgame.zy",
                "sh.lilith.dgame.yiwan.leshi",
                "com.lemongame.tw.dota",
        };
        pushNewLog(getCpuAtchitectureString());
        pushNewLog("检查小冰冰安装包是否存在!");
        Button btn = (Button) findViewById(R.id.btnStart);
        for (String str : packagelist)
        {
            if (ApkUtils.isInstalled(mContext, str)) {
                DTCQ_PACKAGENAME = str;
                refreshUI(false);
                btn.setVisibility(View.VISIBLE);
                pushNewLog("发现小冰冰:" + DTCQ_PACKAGENAME);
                return;
            }
        }
        refreshUI(false);
        btn.setVisibility(View.INVISIBLE);
        String xx = ApkUtils.getInstalledPackageName(mContext,"小冰冰传奇");
        if (xx != "")
            pushNewLog("疑似？小冰冰:" + xx);
        else
            pushNewLog("未发现小冰冰App:" + DTCQ_PACKAGENAME);
    }

    public void onBtnStart(View view)
    {
        refreshUI(true);
        if (!ApkUtils.isInstalled(mContext, DTCQ_PACKAGENAME)) {
            Toast.makeText(mContext, "没找到安装包!", Toast.LENGTH_LONG).show();
        }
        else
        {
            new PatchApkTask().execute();
        }
    };

    public void onGoToUpdate(View view)
    {
        Intent intent = new Intent();
        intent.setData(Uri.parse("http://weibo.com/yourtimelord"));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent); //启动浏览器
    }

    public void onTryInstall(View view)
    {
        File file = new File(NEW_APK_PATH_SIGNED);
        if(file.exists()){
            if (!mFullAccessRoot){
                ApkUtils.installApk(mContext, NEW_APK_PATH_SIGNED);
                ApkUtils.uninstallApk(mContext, DTCQ_PACKAGENAME);
            }else
            {
                if (!ApkUtils.silentUninstallApk(mContext, DTCQ_PACKAGENAME))
                    return;
                if (!ApkUtils.silentInstallApk(mContext, NEW_APK_PATH_SIGNED))
                    return;
                IOUtil.copyFile(EXT_STORAGE_ROOT + "/Cocos2dxPrefsFile.xml", EXT_STORAGE_ROOT + "/Cocos2dxPrefsFile.xml", true);
            }
        }
    }

    public void onCleanCache(View view)
    {
        IOUtil.deleteDir(new File(NEW_APK_PATH_SIGNED));
        btnCleanAll.setVisibility(View.INVISIBLE);
        btnTryInstall.setVisibility(View.INVISIBLE);
    }

    public void refreshUI(boolean begin)
    {
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        Button btn = (Button) findViewById(R.id.btnStart);
        if (begin)
        {
            pb.setVisibility(View.VISIBLE);
            btn.setClickable(false);
        }else
        {
            pb.setVisibility(View.INVISIBLE);
            btn.setClickable(true);
        }
    };

    public void checkVersion()
    {
        ApkUtils.getMacAddress(mContext);

        new Thread() {
            @Override
            public void run() {
                // 定义一个URL对象
                URL url;
                try {
                    url = new URL("http://47.89.9.57/version.txt");
                    // 打开该URL的资源输入流
                    InputStream is = url.openStream();
                    // 从InputStream中解析出图片
                    String ret = IOUtil.Inputstr2Str_Reader(is, null);
                    Integer versionNumer = Integer.parseInt(ret.trim());

                    // 发送消息
                    Message msg = new Message();
                    if (version < versionNumer)
                        msg.obj = "有新版本了，补丁的更完善了！\n去微博看看吧..";
                    else
                        msg.obj = null;

                    msg.what = 2;
                    mHandler.sendMessage(msg);//发送message值给Handler接收

                    is.close();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }.start();
    }

    public void pushNewLog(String str)
    {
        mLogView.setText(mLogView.getText().toString() + "\n"+ str) ;
    }

    private class PatchApkTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pushNewLog("我要开始工作了!");
        }
        @Override
        protected Integer doInBackground(String... params) {
            sendMsg("还有4步，正在：处理补丁文件!");
            PATCH_ABS_PATH = AssetCopy.exportPatchZip(PATCH_PATH, mContext, EXT_STORAGE_ROOT);
            //PatchUtils.beginPrepareAPK();// Example of a call to a native method
            PackageInfo packageInfo = ApkUtils.getInstalledApkPackageInfo(mContext, DTCQ_PACKAGENAME);
            if (packageInfo != null) {
                String oldApkSource = ApkUtils.getSourceApkPath(mContext, DTCQ_PACKAGENAME);
                sendMsg("发现已安装的:" + oldApkSource);
                if (!TextUtils.isEmpty(oldApkSource)) {
                    String tempdir =  EXT_STORAGE_ROOT + File.separator + TEMPPATH;
                    sendMsg("还有3步，正在：解包!");

                    IOUtil.deleteDir(new File(tempdir));            //remove old res
                    PatchUtils.unpackapk(oldApkSource, tempdir);    //unpack all original res
                    if (mFullAccessRoot) {
                        tryMergePatch(tempdir);                         //merge all patch files
                    }
                    PatchUtils.unpackapk(PATCH_ABS_PATH, tempdir);  //merge timeshift res
                    //AssetCopy.mergeGameVersion(EXT_STORAGE_ROOT + "/Cocos2dxPrefsFile.xml", tempdir + "/assets/dgame-config.luc");
                    sendMsg("还有2步，正在：合并新apk!");
                    IOUtil.deleteDir(new File(PATCH_ABS_PATH));
                    PatchUtils.combine(tempdir + File.separator, NEW_APK_PATH);
                    IOUtil.deleteDir(new File(tempdir));
                    sendMsg("还有1步，正在：签名apk!");

                    System.gc();

                    int loopCount = 3;
                    boolean signSucceed = false;
                    while(loopCount > 0) {
                        PatchUtils.onflysign(NEW_APK_PATH, NEW_APK_PATH_SIGNED);
                        double filesize = FileSizeUtil.getFileOrFilesSize(NEW_APK_PATH, 3);
                        double filesize_signed = FileSizeUtil.getFileOrFilesSize(NEW_APK_PATH_SIGNED, 3);
                        loopCount = loopCount -1;
                        if (Math.abs(filesize - filesize_signed) < 5)
                        {
                            signSucceed = true;
                            break;
                        }else
                            sendMsg("签名apk失败，正在重试");

                        if(loopCount<=0)
                            break;
                    }
                    if(!signSucceed){
                        sendMsg("签名apk失败，要么重启手机干干净净的再试一次");
                        return 0;
                    }

                    IOUtil.deleteDir(new File(NEW_APK_PATH));
                    if (!mFullAccessRoot){
                        ApkUtils.installApk(mContext, NEW_APK_PATH_SIGNED);
                        ApkUtils.uninstallApk(mContext, DTCQ_PACKAGENAME);
                    }
                    else
                    {
                        sendMsg("其实还有2步，卸载原版小冰冰传奇!");
                        if (!ApkUtils.silentUninstallApk(mContext, DTCQ_PACKAGENAME))
                            return 1;
                        sendMsg("这是最后1步，安装原模型版本!");
                        if (!ApkUtils.silentInstallApk(mContext, NEW_APK_PATH_SIGNED))
                            return 1;
                        sendMsg("真的是最后一步，恢复登陆信息");
                        IOUtil.copyFile(EXT_STORAGE_ROOT + "/Cocos2dxPrefsFile.xml", EXT_STORAGE_ROOT + "/Cocos2dxPrefsFile.xml", true);
                    }

                } else {
                    sendMsg("你安装过小冰冰传奇么?");
                }
            } else {
                sendMsg("我根本不知道发生了什么错误!");
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(mFullAccessRoot)
                pushNewLog("直接启动游戏即可，已经安装完毕!");
            else
                pushNewLog("新的安装包准备好了!");
            refreshUI(false);
            refreshButtons();
        }

        private void sendMsg(String msgs)
        {
            Message msg = new Message();
            msg.obj = msgs;
            msg.what = 1;
            mHandler.sendMessage(msg);//发送message值给Handler接收
        }
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case 1:
                    pushNewLog((String)msg.obj);
                    break;
                case 2:
                    if(msg.obj != null)
                        Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_LONG).show();
                    else
                    {
                        TextView txNewVersion = (TextView) findViewById(R.id.txNewVersion);
                        txNewVersion.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    };

    public class Person {
        private String name;
        private Integer order;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getOrder() {
            return order;
        }
        public void setOrder(Integer order) {
            this.order = order;
        }
    }

    private boolean tryMergePatch(String unpackedSourceDir)
    {
        try {
            //备份UserDefault
            String userdefault = "Cocos2dxPrefsFile.xml";
            String backupdir =  EXT_STORAGE_ROOT + File.separator;
            String OriginalDefault = "/data/data/" + DTCQ_PACKAGENAME + "/shared_prefs/" + userdefault;
            String patchFilesRoot = "/data/data/" + DTCQ_PACKAGENAME + "/files";
            String tempdir =  backupdir + TEMPPATH;
            File source = new File(OriginalDefault);
            //not work
            RootCmd.upgradeRootPermission(OriginalDefault);
            RootCmd.upgradeRootPermission(patchFilesRoot);
            RootCmd.upgradeRootPermission(tempdir);

            if(source.exists()){
                IOUtil.copyFile(OriginalDefault, backupdir + userdefault, true);
            }

            //获取补丁版本列表
            List<Person> versionList = new ArrayList<Person>();
            source = new File(patchFilesRoot);
            if (source.isDirectory()) {
                for (File file : source.listFiles()) {
                    if (file.isDirectory()) {
                        Person version = new Person();
                        String versionDir = file.getName();
                        version.setName(versionDir);
                        Integer versionNumer = Integer.parseInt(versionDir.substring(versionDir.lastIndexOf('.') + 1, versionDir.length()));
                        version.setOrder(versionNumer);
                        versionList.add(version);
                    }
                }
            }
            Collections.sort(versionList, new Comparator<Person>() {
                public int compare(Person arg0, Person arg1) {
                    return arg0.getOrder().compareTo(arg1.getOrder());
                }
            });

            //目录copy
            for (Person p : versionList) {
                IOUtil.copyDirectory(patchFilesRoot + "/" + p.getName() + "/patch", unpackedSourceDir + "/assets", false); ///5.0.002/patch
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        }
        return false;
    }

/**
 *
 * [获取cpu类型和架构]
 *
 * @return
 * 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构，第三个参数标识是不是neon指令集
 */
    private static Object[] mArmArchitecture =  {-1, -1, -1};
    public static String getCpuAtchitectureString()
    {
        String ret = "";
        getCpuArchitecture();
        for (Object s : mArmArchitecture)
            ret = ret + s.toString();
        return ret;
    }
    public static Object[] getCpuArchitecture() {
        if ((Integer) mArmArchitecture[1] != -1) {
            return mArmArchitecture;
        }
        try {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            try {
                String nameProcessor = "Processor";
                String nameFeatures = "Features";
                String nameModel = "model name";
                String nameCpuFamily = "cpu family";
                while (true) {
                    String line = br.readLine();
                    String[] pair = null;
                    if (line == null) {
                        break;
                    }
                    pair = line.split(":");
                    if (pair.length != 2)
                        continue;
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.compareTo(nameProcessor) == 0) {
                        String n = "";
                        for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++) {
                            String temp = val.charAt(i) + "";
                            if (temp.matches("\\d")) {
                                n += temp;
                            } else {
                                break;
                            }
                        }
                        mArmArchitecture[0] = "ARM";
                        mArmArchitecture[1] = Integer.parseInt(n);
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameFeatures) == 0) {
                        if (val.contains("neon")) {
                            mArmArchitecture[2] = "neon";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameModel) == 0) {
                        if (val.contains("Intel")) {
                            mArmArchitecture[0] = "INTEL";
                            mArmArchitecture[2] = "atom";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameCpuFamily) == 0) {
                        mArmArchitecture[1] = Integer.parseInt(val);
                        continue;
                    }
                }
            } finally {
                br.close();
                ir.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mArmArchitecture;
    }

    private static boolean rootCheck()
    {
        if (RootTools.isRootAvailable())
        {
            System.out.println("Root found.\n");
        }
        else
        {
            System.out.println("Root not found");
            return false;
        }
        try
        {
            Shell.startRootShell();
        }
        catch (IOException e2)
        {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return false;
        }
        catch (TimeoutException e)
        {
            System.out.println("[ TIMEOUT EXCEPTION! ]\n");
            e.printStackTrace();
            return false;
        }
        catch (RootDeniedException e)
        {
            System.out.println("[ ROOT DENIED EXCEPTION! ]\n");
            e.printStackTrace();
            return false;
        }

        try
        {
            if (!RootTools.isAccessGiven())
            {
                System.out.println("ERROR: No root access to this device.\n");
                return false;
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR: could not determine root access to this device.\n");
            return false;
        }
        return true;
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("p7zip-lib");
    }

}
