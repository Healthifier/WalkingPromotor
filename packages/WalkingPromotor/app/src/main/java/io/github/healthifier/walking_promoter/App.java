package io.github.healthifier.walking_promoter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.deploygate.sdk.DeployGate;

public class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DeployGate.install(this);
        Test.Companion.test();
    }

    /**
     * バージョンコードを取得する
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        int versionCode = 0;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionCode;
    }

    /**
     * バージョン名を取得する
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionName;
    }

    public static void logDebug(Object message) {
        if (BuildConfig.DEBUG) {
            Log.d(Constant.TAG, message.toString());
        }
    }

    public static void logError(Object message) {
        Log.e(Constant.TAG, message.toString());
    }

    public static void xAssert(boolean cond) {
        if (BuildConfig.DEBUG && !cond) {
            throw new AssertionError();
        }
    }

    public static void xAssert(boolean cond, String message) {
        if (BuildConfig.DEBUG && !cond) {
            throw new AssertionError(message);
        }
    }
}
