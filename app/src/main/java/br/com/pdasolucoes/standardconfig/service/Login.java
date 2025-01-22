package br.com.pdasolucoes.standardconfig.service;


import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.network.JsonRequestBase;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public class Login extends JsonRequestBase {

    private String user;
    private String pass;
    private String method;

    public Login(String user, String pass, String method) {
        this.user = user;
        this.pass = pass;
        this.method = method;
    }

    public Login() {
    }

    @Override
    public JSONObject getBody() throws JSONException {

        JSONObject json = new JSONObject();
        json.put("Login", "102030");
        json.put("Password", Base64.encodeToString(MD5("909090"), Base64.DEFAULT).replaceAll("\n", ""));


        return json;
    }

    @Override
    protected RequestInfo getRequestInfo() {
        return new RequestInfo(
                "Autenticacao",
                RequestType.OnLine,
                R.string.autenticando,
                MethodRequest.POST,
                0);
    }

    @Override
    public void processResult(Object data) {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();

        if (appCompatActivity == null)
            return;

        try {
            Log.i("data", data.toString());
        } catch (Exception ex) {
            NavigationHelper.showConfirmDialog(appCompatActivity.getString(R.string.error_auth),
                    ex.getMessage());
        }
    }

    @Override
    public void processError(MessageConfiguration result) {

    }

    public byte[] MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            return md.digest(md5.getBytes());
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
