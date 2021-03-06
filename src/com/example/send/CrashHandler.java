package com.example.send;

/**
 * Created by root on 15-11-13.
 */
import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

public class CrashHandler implements UncaughtExceptionHandler{

    private static CrashHandler crashHandler;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO Auto-generated method stub
        if (crashHandler != null) {
            try {
                //将crash log写入文件
                FileOutputStream fileOutputStream = new FileOutputStream(MainApplication.SEND_PATH + "crash_log.txt", true);
                PrintStream printStream = new PrintStream(fileOutputStream);
                printStream.print("log start:\n");
                Date date = new Date();
                printStream.print("Date:" + date.toString());
                ex.printStackTrace(printStream);
                printStream.print("\n\n\n");
                printStream.flush();
                printStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //设置默认处理器
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private CrashHandler() {}

    //单例
    public static CrashHandler instance(Context context) {
        if (crashHandler == null) {
            synchronized (context) {
                crashHandler = new CrashHandler();
            }
        }
        return crashHandler;
    }
}
