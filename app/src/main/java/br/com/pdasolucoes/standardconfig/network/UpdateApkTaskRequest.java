package br.com.pdasolucoes.standardconfig.network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;
import androidx.multidex.BuildConfig;

import org.json.JSONObject;

import java.io.File;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public class UpdateApkTaskRequest extends JsonRequestBase {

    String mobileNameApk;
    String paste;

    public UpdateApkTaskRequest(String paste, String mobileNameApk) {
        this.mobileNameApk = mobileNameApk.replaceAll(".apk", "");
        this.paste = paste;
    }

    @Override
    public JSONObject getBody() {
        return null;
    }


    @Override
    protected RequestInfo getRequestInfo() {
        return new RequestInfo(
                paste,
                mobileNameApk.concat(".apk"),
                RequestType.OnLine,
                R.string.download_apk
        );
    }

    @Override
    public void processResult(Object data) {
        Context context = NavigationHelper.getCurrentAppCompat();
        if (context == null)
            return;

        JSONObject uri = (JSONObject) data;
        //Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setDataAndType(Uri.parse(uri.optString("uri")), "application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }

    @Override
    public void processError(MessageConfiguration result) {

    }
}
