package br.com.pdasolucoes.standardconfig.utils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.managers.AuthManager;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyApplication extends Application implements DialogInterface.OnShowListener {

    private static MyApplication instance;

    private static boolean correctVersion = false;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        MyApplication.instance = this;
        super.onCreate();

        //MultiDex.install(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
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
                Toast.makeText(NavigationHelper.getCurrentAppCompat(), resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
