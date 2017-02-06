

package com.hustunique.parsingplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by JianGuo on 1/16/17.
 * Util class
 */

public final class Util {

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Write <tt>data</tt> to the <tt>context</tt> application package
     *
     * @param directory the parent directory
     * @param filename the filename
     * @param content  the content of data
     */
    public static String writeToFile(File directory, String filename, String content)
            throws FileNotFoundException {
        File f = new File(directory, filename);
        FileOutputStream fos = new FileOutputStream(f);
        try {
            if (!f.exists()) f.createNewFile();
            byte[] contentBytes = content.getBytes();
            fos.write(contentBytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.getAbsolutePath();
    }


    /**
     * Read data from target filename saved privately in <tt>context</tt> application package
     *
     * @param filename the file name
     * @param context  context
     * @return Stream data
     */
    @Deprecated
    public static String readFromFile(String filename, Context context) {
        return context.getFileStreamPath(filename).getAbsolutePath();
    }

    /**
     * RC4 encryption
     * Refer https://zh.wikipedia.org/wiki/RC4
     *
     * @param b1
     * @param b2
     * @return decoded byte array
     */
    public static byte[] rc4(byte[] b1, byte[] b2) {
        byte[] result = new byte[b2.length];

        int[] s = new int[256];
        for (int i = 0; i < 256; i++) {
            s[i] = i;
        }
        int t = 0;
        int tmp;
        for (int i = 0; i < 256; i++) {
            t = (t + s[i] + (b1[i % b1.length] & 0xff)) % 256;
            tmp = s[i];
            s[i] = s[t];
            s[t] = tmp;
        }
        int x = 0, y = 0;
        for (int i = 0; i < b2.length; i++) {
            y = (y + 1) % 256;
            x = (x + s[y]) % 256;
            tmp = s[x];
            s[x] = s[y];
            s[y] = tmp;
            result[i] = (byte) ((b2[i] & 0xff) ^ s[(s[x] + s[y]) % 256]);
        }
        return result;
    }


    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Util.isExternalStorageRemovable() ? Util.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }


    public static class ParsingThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new ConcatThread(r);
        }

        private class ConcatThread extends Thread {

            ConcatThread(Runnable r) {
                super(r);
            }

            @Override
            public void run() {
                Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
                super.run();
            }
        }
    }
}