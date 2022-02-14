package br.com.pdasolucoes.standardconfig.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.network.OffLineRequest;
import br.com.pdasolucoes.standardconfig.network.RequestUpdateApkTask;
import br.com.pdasolucoes.standardconfig.network.SendRequestTask;
import br.com.pdasolucoes.standardconfig.network.SoapRequestBase;
import br.com.pdasolucoes.standardconfig.network.UpdateApkTaskRequest;
import br.com.pdasolucoes.standardconfig.network.interfaces.IRequest;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.MyApplication;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public class NetworkManager {

    public static boolean isNetworkOnline() {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = null;
            if (connectivityManager != null)
                activeNetwork = connectivityManager.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void saveOffLineRequest(SoapRequestBase request) {
//        NetworkRequestDataSource dataSource = SQLiteHelper.getInstance().getDataSource(NetworkRequestDataSource.class);
//        dataSource.addRequest(request);
//
//        pendingRequests = dataSource.getRequests();
    }

    public static void removeOffLineRequest(OffLineRequest request) {
//        NetworkRequestDataSource dataSource = SQLiteHelper.getInstance().getDataSource(NetworkRequestDataSource.class);
//        dataSource.deleteRequest(request);
//
//        pendingRequests = dataSource.getRequests();
    }

    public static void sendRequest(IRequest iRequest) {
        SendRequestTask sendRequestTask = new SendRequestTask(iRequest);
        sendRequestTask.execute();
    }

    public static void sendRequestApk(IRequest iRequest) {
        RequestUpdateApkTask requestUpdateApkTask = new RequestUpdateApkTask(iRequest);
        requestUpdateApkTask.execute();
    }

    @SuppressLint({"WrongConstant", "QueryPermissionsNeeded"})
    public static boolean isPackageInstalled(String packageName) {

        AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();

        if (activity == null)
            return false;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                for (PackageInfo p :
                        activity.getPackageManager().getInstalledPackages(PackageManager.MATCH_ALL)) {
                    if (p.packageName.equals(packageName))
                        return true;
                }
            }

            return activity.getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getVersionName(String packageName) {

        AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();

        if (activity == null)
            return "";

        try {
            return activity.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static boolean tryUpdateApk(String versionArray, String packageName, String namePaste,String nameApk) {
        String[] versions = versionArray.split("[;]");
        List<Boolean> isUpdates = new ArrayList<>();

        String currentNameVersion = getVersionName(packageName);

        for (String v : versions) {
            isUpdates.add(currentNameVersion.equals(v.trim()));
        }

        if (!isUpdates.contains(true)) {
            NetworkManager.sendRequestApk(new UpdateApkTaskRequest(namePaste, nameApk));
            return true;
        }

        return false;


    }

    public static void openApk(String packageName) {

        AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();

        if (activity == null)
            return;

        Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);


        if (launchIntent != null) {
            activity.startActivity(launchIntent);
        }
    }

    public static void updateInitialViews(View view) {
        if (view != null) {

            AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();

            if (activity== null)
                return;

            if (activity.getIntent() != null) {
                if (activity.getIntent().getExtras() != null && activity.getIntent().hasExtra(ConfigurationHelper.ConfigurationEntry.UserCode.getKeyName())) {
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserCode, activity.getIntent().getExtras().getInt(ConfigurationHelper.ConfigurationEntry.UserCode.getKeyName(), -1));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserName, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.UserName.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserCodeFilial, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.UserCodeFilial.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserNameFilial, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.UserNameFilial.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserCodeProfile, activity.getIntent().getExtras().getInt(ConfigurationHelper.ConfigurationEntry.UserCodeProfile.getKeyName(), -1));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserNameProfile, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.UserNameProfile.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserLeader, activity.getIntent().getExtras().getInt(ConfigurationHelper.ConfigurationEntry.UserLeader.getKeyName(), -1));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserLogin, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.UserLogin.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.IsLoggedIn, activity.getIntent().getExtras().getBoolean(ConfigurationHelper.ConfigurationEntry.IsLoggedIn.getKeyName(), false));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.PackageName, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.PackageName.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.MacAddress, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.MacAddress.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Token, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.Token.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.RefreshToken, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.RefreshToken.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserPassword, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.UserPassword.getKeyName(), ""));

                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.ServerAddress.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.ServerAddressApi, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.ServerAddressApi.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Directory, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.Directory.getKeyName(), ""));
                    ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Store, activity.getIntent().getExtras().getString(ConfigurationHelper.ConfigurationEntry.Store.getKeyName(), ""));
                }
            }


            String profile = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.UserNameProfile, "-");
            String userName = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.UserName, "-");
            String packageName = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.PackageName, "");

            ((TextView)view.findViewById(R.id.tvPerfil)).setText(activity.getString(R.string.perfil_dois_pontos).concat(" ").concat(profile));
            ((TextView)view.findViewById(R.id.tvUsuario)).setText(activity.getString(R.string.usuario_dois_pontos).concat(" ").concat(userName));
            ((TextView)view.findViewById(R.id.tvModulo)).setText(activity.getString(R.string.modulo_dois_pontos).concat(" ").concat(activity.getTitle().toString()));

            if (!TextUtils.isEmpty(packageName))
                ((TextView)view.findViewById(R.id.footer).findViewById(R.id.tvVersion)).setText(activity.getString(R.string.versao_description).concat(" ")
                        .concat(NetworkManager.getVersionName(packageName)));
            else
                ((TextView)view.findViewById(R.id.footer).findViewById(R.id.tvVersion)).setText(activity.getString(R.string.versao_description).concat(" ")
                        .concat(NetworkManager.getVersionName(activity.getPackageName())));
        }

    }


    public static void verifyLogin(){
        try {
            AppCompatActivity activity = NavigationHelper.getCurrentAppCompat();

            if (activity == null)
                return;

            Context con = activity.createPackageContext("br.com.pdasolucoes.basesystem",0);

            SharedPreferences preferencesBase = con.getSharedPreferences(ConfigurationHelper.Catalog.Authentication.getName(), Context.MODE_PRIVATE);
            int codeUserChildApp = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.UserCode,-1);
            int codeUserParentApp = preferencesBase.getInt(ConfigurationHelper.ConfigurationEntry.UserCode.getKeyName(),-1);

            if (codeUserChildApp != codeUserParentApp)
                activity.finish();


        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
    }

}
