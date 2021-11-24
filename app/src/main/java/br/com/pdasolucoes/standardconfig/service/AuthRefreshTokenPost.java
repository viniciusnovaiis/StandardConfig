package br.com.pdasolucoes.standardconfig.service;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.managers.AuthManager;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.model.Autenticacao;
import br.com.pdasolucoes.standardconfig.network.JsonRequestBase;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;

public class AuthRefreshTokenPost extends JsonRequestBase {


    @Override
    public JSONObject getBody() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("login", ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.UserLogin,""));
        jsonObject.put("refreshToken", ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.RefreshToken,""));

        return jsonObject;
    }

    @Override
    protected RequestInfo getRequestInfo() {
        return new RequestInfo("api/Autenticacao/refresh-token",
                RequestType.Backgroud,
                R.string.autenticando,
                MethodRequest.POST,
                0);
    }

    @Override
    public void processResult(Object data) {

        String message = "";
        if (((JSONObject) data).has("Message")) {
            try {
                message = ((JSONObject) data).getString("Message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (message.equals("Token inativo!") || message.equals("Refresh token inativo!")) {
                NetworkManager.sendRequest(new AuthenticationPost(
                        ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.UserLogin, ""),
                        ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.UserPassword, "")
                ));
                return;
            }
        }

        AuthManager.timerControlToken(new Gson().fromJson(data.toString(), Autenticacao.class));
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
