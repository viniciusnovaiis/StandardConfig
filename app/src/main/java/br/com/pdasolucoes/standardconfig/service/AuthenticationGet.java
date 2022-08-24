package br.com.pdasolucoes.standardconfig.service;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.managers.AuthManager;
import br.com.pdasolucoes.standardconfig.network.JsonRequestBase;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;

public class AuthenticationGet extends JsonRequestBase {


    private final String refreshToken;

    public AuthenticationGet() {
        this.refreshToken = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.RefreshToken, "");
    }

    @Override
    public JSONObject getBody() throws JSONException {
        return null;
    }

    @Override
    protected RequestInfo getRequestInfo() {
        return new RequestInfo("api/Autenticacao?refreshToken=".concat(refreshToken),
                RequestType.Backgroud,
                R.string.autenticando,
                MethodRequest.GET,
                0);
    }

    @Override
    public void processResult(Object data) {



        try {
            ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Token, new JSONObject(data.toString()).get("accessToken").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void processError(MessageConfiguration result) {
        if (result == MessageConfiguration.NetworkError) {
            Log.e("ERRO", "REDE");
        } else if (result == MessageConfiguration.ExceptionError) {
            Log.e("ERRO", result.getExceptionErrorMessage());
        }
    }
}
