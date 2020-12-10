package br.com.pdasolucoes.standardconfig.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;

import br.com.pdasolucoes.standardconfig.enums.FragmentTag;


public class NavigationHelper {

    private static WeakReference<AppCompatActivity> weakAppCompat;
    private static WeakReference<DialogInterface> weakDialog;

    public static AppCompatActivity getCurrentAppCompat() {
        if (NavigationHelper.weakAppCompat == null) {
            return null;
        }

        return NavigationHelper.weakAppCompat.get();
    }

    public static DialogInterface getCurrentDialog() {
        if (NavigationHelper.weakDialog == null) {
            return null;
        }

        return NavigationHelper.weakDialog.get();
    }

    public static void setCurrentDialog(DialogInterface dialog) {
        NavigationHelper.weakDialog = new WeakReference<>(dialog);
    }


    public static void setCurrentAppCompat(AppCompatActivity activity) {
        NavigationHelper.weakAppCompat = new WeakReference<>(activity);
    }

    public static void startActivity(Class<? extends AppCompatActivity> activityClass) {

        AppCompatActivity appCompatActivity = getCurrentAppCompat();

        if (appCompatActivity == null)
            return;

        getCurrentAppCompat().startActivity(new Intent(appCompatActivity, activityClass));
        //getCurrentAppCompat().finish();
    }

    public static Boolean hasVisibleFragment(FragmentTag tag) {
        Fragment fragment = NavigationHelper.getFragment(tag);
        return (fragment != null && fragment.isVisible());

    }

    public static Fragment getFragment(FragmentTag tag) {
        AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();
        if (activity == null) {
            return null;
        }

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag.getTag());

        try {
            return tag.getFragmentClass().cast(fragment);
        } catch (Exception e) {
            return null;
        }
    }

    public static Fragment showFragment(FragmentTag tag, Integer containerId, boolean addToBackStack) {
        AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();
        if (activity == null) {
            return null;
        }

        if (addToBackStack)
            return showFragment(activity, tag, containerId, true);
        else
            return showFragment(activity, tag, containerId, false);
    }

    private static Fragment showFragment(AppCompatActivity activity, FragmentTag tag, Integer containerId, boolean addToBackStack) {
        if (NavigationHelper.hasVisibleFragment(tag)) {
            return null;
        }

        androidx.fragment.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        androidx.fragment.app.Fragment fragment = null;

        try {
            fragment = tag.getFragmentClass().newInstance();

            if (addToBackStack) {
                if (containerId == null) {
                    fragmentManager
                            .beginTransaction()
                            .add(fragment, tag.getTag())
                            .addToBackStack(tag.getTag())
                            .commit();
                } else {
                    fragmentManager
                            .beginTransaction()
                            .replace(containerId, fragment, tag.getTag())
                            .addToBackStack(tag.getTag()).commit();
                }
            } else {
                if (containerId == null) {
                    fragmentManager.beginTransaction().add(fragment, tag.getTag()).commit();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(containerId, fragment, tag.getTag()).commit();
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }

    public static void showDialog(int title, int msg, int strPositive, DialogInterface.OnClickListener positive, int strNegative, DialogInterface.OnClickListener negative, int strNeutral, DialogInterface.OnClickListener neutral) {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();

        if (appCompatActivity == null)
            return;


        AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
        if (title != -1)
            builder.setTitle(appCompatActivity.getString(title));
        if (msg != -1)
            builder.setMessage(appCompatActivity.getString(msg));

        if (positive != null) {
            builder.setPositiveButton(strPositive, positive);
        } else {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (negative != null && strNegative != -1) {
            builder.setNegativeButton(strNegative, negative);
        } else {
            builder.setNeutralButton(strNegative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (strNeutral != -1 && neutral != null) {
            builder.setNeutralButton(strNeutral, neutral);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static void showDialog(String title, String msg, int strPositive, DialogInterface.OnClickListener positive, int strNegative, DialogInterface.OnClickListener negative, int strNeutral, DialogInterface.OnClickListener neutral) {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();

        if (appCompatActivity == null)
            return;


        AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
        builder.setTitle(title);
        builder.setMessage(msg);

        if (positive != null) {
            builder.setPositiveButton(strPositive, positive);
        } else {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (negative != null && strNegative != -1) {
            builder.setNegativeButton(strNegative, negative);
        }

        if (strNeutral != -1 && neutral != null) {
            builder.setNeutralButton(strNeutral, neutral);
        }

        builder.create().show();

    }

    public static void showDialog(String title, String msg, String strPositive, DialogInterface.OnClickListener positive) {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();

        if (appCompatActivity == null)
            return;


        AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
        builder.setTitle(title);
        builder.setMessage(msg);

        if (positive != null) {
            builder.setPositiveButton(strPositive, positive);
        } else {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.create().show();

    }

    public static void showDialog(int title, int msg, int strPositive, DialogInterface.OnClickListener positive, int strNegative, DialogInterface.OnClickListener negative) {

        NavigationHelper.showDialog(title, msg, strPositive, positive, strNegative, negative, -1, null);

    }

    public static void showDialog(int title, int msg, int strPositive, DialogInterface.OnClickListener positive, int strNegative) {

        NavigationHelper.showDialog(title, msg, strPositive, positive, strNegative, null, -1, null);

    }

    public static void showConfirmDialog(int title, int msg) {

        NavigationHelper.showDialog(title, msg, -1, null, -1, null, -1, null);

    }

    public static void showConfirmDialog(String title, String msg) {

        NavigationHelper.showDialog(title, msg, -1, null, -1, null, -1, null);

    }

    public static void showDialog(int title, String[] list, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative, DialogInterface.OnMultiChoiceClickListener select) {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();
        if (appCompatActivity == null)
            return;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(appCompatActivity);
        builderSingle.setTitle(appCompatActivity.getString(title));

        boolean[] checkedItems = new boolean[list.length];

        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = false;
        }

        builderSingle.setMultiChoiceItems(list, checkedItems, select);
        builderSingle.setNegativeButton("Cancelar", negative);
        builderSingle.setPositiveButton("Confirmar", positive);

        builderSingle.show();

    }


    public static ProgressDialog showProgressDialogStatus(int title, int msg) {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();
        if (appCompatActivity == null)
            return null;

        ProgressDialog progressDialog = new ProgressDialog(appCompatActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        if (title == -1)
            progressDialog.setTitle("");
        else
            progressDialog.setTitle(appCompatActivity.getString(title));

        if (msg == -1)
            progressDialog.setMessage("");
        else
            progressDialog.setMessage(appCompatActivity.getString(msg));

        progressDialog.setProgress(0);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setCancelable(false);

        return progressDialog;
    }

    public static void showToastShort(int msg) {
        Toast.makeText(NavigationHelper.getCurrentAppCompat(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void backFragment() {
        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();

        if (appCompatActivity == null)
            return;

        FragmentManager fragmentManager = appCompatActivity
                .getSupportFragmentManager();

        if (fragmentManager == null)
            return;


        fragmentManager.popBackStackImmediate();
    }
}
