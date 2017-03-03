package timeshifter.dtcq.yunlong.dtcqtimeshifter;

/**
 * Created by liyunlong on 2016/10/9.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class AssetCopy {

    private static String TAG="AssetCopy";
    /**
     *
     * @param context :application context
     * @param srcPath :the path of source file
     * @param dstPath :the path of destination
     */
    public static String exportPatchZip(String patchname, Context context, String destination)
    {
        String ret = "";
        try {
            InputStream is = context.getAssets().open(patchname);
            ret = destination+"/" + patchname;
            FileOutputStream fos = new FileOutputStream(new File(ret));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    public static void mergeGameVersion(String userDefaultFileName, String lucFileName)
    {

    }
}