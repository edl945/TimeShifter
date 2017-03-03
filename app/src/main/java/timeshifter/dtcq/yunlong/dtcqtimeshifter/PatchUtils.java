package timeshifter.dtcq.yunlong.dtcqtimeshifter;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import kellinwood.security.zipsigner.ZipSigner;
import kellinwood.security.zipsigner.optional.Fingerprint;

public class PatchUtils {
    public static native int combinePatch(String oldapkpath,String newapkpath, String patchpath);
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String beginPrepareAPK();
    public static int unpackapk(String oldapkpath, String extractpath)
    {
        ZipUtil.unzip(oldapkpath, extractpath);
        return 0;
    }
    public static int combine(String extractpath, String newapkpath)
    {
        ZipUtil.zip(extractpath, newapkpath);
        return 0;
    }

    //sign
    //http://www.cnblogs.com/not-code/archive/2011/05/15/2047057.html
    public static int onflysign(String inputapk,String outputapk)
    {
        ZipSigner zipSigner = null;
        try
        {
             zipSigner = new ZipSigner();
        }
        catch (InstantiationException e){ Log.i("1","1");}
        catch (ClassNotFoundException e){Log.i("1","1");}
        catch (IllegalAccessException e){Log.i("1","1");}
        try
        {
            zipSigner.setKeymode("testkey");//设置签名模式

            // Load the key/cert pair for the built-in key "testkey"
            //zipsigner.loadKeys("testkey")
            // Get the SHA1 fingerprint of the cert
            //String testkeySHA1Fingerprint = Fingerprint.hexFingerprint("SHA1", zipsigner.getKeySet().getPublicKey().getEncoded())
        }
        catch (IOException e){Log.i("2","2");}
        catch (GeneralSecurityException e){Log.i("2","2");}
        try
        {
            zipSigner.signZip(inputapk, outputapk);
        }
        catch (IOException e){Log.i("3","3");}
        catch (GeneralSecurityException e){Log.i("3","3");}
        return 0;
    }
}