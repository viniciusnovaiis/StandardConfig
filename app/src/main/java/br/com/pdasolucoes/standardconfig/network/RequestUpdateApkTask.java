package br.com.pdasolucoes.standardconfig.network;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.pdasolucoes.standardconfig.BuildConfig;
import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.interfaces.IRequest;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.MyApplication;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;
import br.com.pdasolucoes.standardconfig.utils.PermissionHelper;

public class RequestUpdateApkTask extends AsyncTaskRunner<Void, Void, Object> {

    private IRequest request;

    public RequestUpdateApkTask(IRequest request) {
        super(request);
        this.request = request;

    }

    protected Object doInBackground(Void... params) {

        if (!NetworkManager.isNetworkOnline())
            return MessageConfiguration.NetworkError;

        Context context = NavigationHelper.getCurrentAppCompat();
        if (context == null)
            return MessageConfiguration.ContextViewError;

        try {

            String service = this.request.getService();
            String action = this.request.getAction();

            String baseUrl =
                    ConfigurationHelper
                            .loadPreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "")
                            .concat("/")
                            .concat(ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.Directory, "")
                                    .concat("/"));


            URL url = new URL(baseUrl + service + "/" + action);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();

            File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(PATH, Environment.DIRECTORY_DOWNLOADS);
            if (!file.exists())
                file.mkdirs();
            File outputFile = new File(file, action);
            if (outputFile.exists())
                outputFile.delete();
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            Uri uri;
            final File file1 = new File(file, action);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                uri = FileProvider.getUriForFile(context, MyApplication.getInstance().getPackageName() + ".provider", file1);
            } else {
                uri = Uri.fromFile(file1);
            }

            return new JSONObject().put("uri", uri);
        } catch (Exception e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        }
    }
}
