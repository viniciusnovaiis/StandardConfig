package br.com.pdasolucoes.standardconfig.utils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import br.com.pdasolucoes.standardconfig.managers.AuthManager;

public class MyApplication extends MultiDexApplication implements DialogInterface.OnShowListener {

    private static MyApplication instance;

    private static boolean correctVersion = false;
    private static ResultToken resultToken;

    public static MyApplication getInstance() {
        return instance;
    }

    public static void setInstance(MyApplication instance) {
        MyApplication.instance = instance;
    }

    public interface ResultToken {
        void onToken(String token);
    }

    public static void setOnResultTokeListener(ResultToken resultTokeListener) {
        resultToken = resultTokeListener;
    }


    @Override
    public void onCreate() {
        MyApplication.instance = this;
        super.onCreate();

        MultiDex.install(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {


                AuthManager.launchService();
                NavigationHelper.setCurrentAppCompat((AppCompatActivity) activity);

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull final Activity activity) {
                NavigationHelper.setCurrentAppCompat((AppCompatActivity) activity);

                IntentFilter filter = new IntentFilter(Service.ACTION);
                instance.registerReceiver(receiver, filter);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                getInstance().unregisterReceiver(receiver);
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                clearReferences(activity);
            }

        });

    }


    private void clearReferences(Activity activity) {
        Activity currActivity = NavigationHelper.getCurrentAppCompat();
        if (currActivity != null && currActivity.equals(activity)) {
            NavigationHelper.setCurrentAppCompat(null);
        }
    }

    public static String getStringResource(int resourceId) {
        return MyApplication.instance.getResources().getString(resourceId);
    }


    public static void closeKeyBoard(Context context) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = ((Activity) context).getCurrentFocus();
        if (focusedView != null) {
            if (inputMethodManager != null) {
                (inputMethodManager).hideSoftInputFromWindow(
                        focusedView.getWindowToken(), 0);
            }
        }
    }

    @Deprecated
    public static boolean isCorrectVersion() {
        return correctVersion;
    }

    @Deprecated
    public static void setCorrectVersion(boolean correctVersion) {
        MyApplication.correctVersion = correctVersion;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        NavigationHelper.setCurrentDialog(dialog);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                if (resultToken != null )
                    resultToken.onToken(resultValue);
            }
        }
    };
}
