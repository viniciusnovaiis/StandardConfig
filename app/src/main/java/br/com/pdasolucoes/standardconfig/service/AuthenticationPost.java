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
import br.com.pdasolucoes.standardconfig.utils.TimerVerifyToken;

public class AuthenticationPost extends JsonRequestBase {


    private final String login;
    private final String password;
    private byte[] passwordBinary;

    public AuthenticationPost(String login, String password, byte[] passwordBinary) {

        this.login = login;
        this.password = password;
        this.passwordBinary = passwordBinary;

        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "http://10.0.2.2");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Directory, ":40001");

        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserLogin, login);
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserPassword, password);
    }

    public AuthenticationPost(String login, String password) {

        this.login = login;
        this.password = password;

        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "http://10.0.2.2");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Directory, ":40001");

        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserLogin, login);
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserPassword, password);
    }

    @Override
    public JSONObject getBody() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Login", login);

        if (passwordBinary != null)
            jsonObject.put("Password", passwordBinary);
        else
            jsonObject.put("Password", password);

        return jsonObject;
    }

    @Override
    protected RequestInfo getRequestInfo() {
        return new RequestInfo("api/Autenticacao",
                RequestType.OnLine,
                R.string.autenticando,
                MethodRequest.POST,
                0);
    }

    @Override
    public void processResult(Object data) {
        String response = data.toString();
        AuthManager.timerControlToken(new Gson().fromJson(response, Autenticacao.class));

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
