package timeshifter.dtcq.yunlong.dtcqtimeshifter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by liyunlong on 2016/10/13.
 */

public class RootCmd {

    private static boolean mHaveRoot = false;

    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test");
            if (ret != 1) {
                System.out.println("have root!");
                mHaveRoot = true;
            } else {
                System.out.println("not root!");
            }
        } else {
            System.out.println("mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    @SuppressWarnings("deprecation")
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            System.out.println(cmd);

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();

            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("sh");
            dos = new DataOutputStream(p.getOutputStream());
            DataInputStream ise = new DataInputStream(p.getErrorStream());
            dos.flush();
            //dos.writeBytes(cmd + "\n");
            dos.writeBytes("echo xxx \n");
            dos.flush();
            //dos.writeBytes("exit\n");
            //dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static int execRunasCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("run-as sh.lilith.dgame.lemonfat");
            dos = new DataOutputStream(p.getOutputStream());
            System.out.println(cmd);

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean upgradeRootPermission(String gameResDir) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + gameResDir;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
