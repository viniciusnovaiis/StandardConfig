package br.com.pdasolucoes.standardconfig.managers;

import android.content.Intent;
import android.util.Log;

import br.com.pdasolucoes.standardconfig.model.Autenticacao;
import br.com.pdasolucoes.standardconfig.service.AuthRefreshTokenPost;
import br.com.pdasolucoes.standardconfig.service.AuthenticationGet;
import br.com.pdasolucoes.standardconfig.service.AuthenticationPost;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.MyApplication;
import br.com.pdasolucoes.standardconfig.utils.Service;
import br.com.pdasolucoes.standardconfig.utils.TimerVerifyToken;

public class AuthManager {


    public static void timerControlToken(Autenticacao a) {

        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.RefreshToken, a.getRefreshToken());
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Token, a.getRefreshToken());

        long tenMinutesMilesegundos = 600000;//10 MINUTOS
        long timeInterval = (a.getExpiration().getTime() - a.getCreated().getTime()) > tenMinutesMilesegundos ?
                (a.getExpiration().getTime() - a.getCreated().getTime()) - tenMinutesMilesegundos :
                (a.getExpiration().getTime() - a.getCreated().getTime());


        TimerVerifyToken timerVerifyToken = new TimerVerifyToken(timeInterval, tenMinutesMilesegundos);
        timerVerifyToken.setOnTimerProgreesListener(new TimerVerifyToken.OnTimerProgrees() {
            @Override
            public void onTick(long l) {

                NetworkManager.sendRequest(new AuthenticationGet());
            }

            @Override
            public void onFinish() {
                NetworkManager.sendRequest(new AuthRefreshTokenPost());
            }
        });
        timerVerifyToken.start();
    }

    public static void logoutUser() {
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserCode, -1);
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserName, "");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserCodeFilial, "-1");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserNameFilial, "");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserCodeProfile, -1);
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserNameProfile, "");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.UserLogin, "");
        ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.IsLoggedIn, false);
    }

    public static void launchService() {

        Intent i = new Intent(MyApplication.getInstance(), Service.class);
        i.putExtra("token", ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.Token, ""));
        MyApplication.getInstance().startService(i);
    }

    public static void AuthApi(String login, String password) {
        NetworkManager.sendRequest(new AuthenticationPost(login, password));
    }

    public static void AuthApi(String login, String password, byte[] passwordBinary) {
        NetworkManager.sendRequest(new AuthenticationPost(login, password, passwordBinary));
    }
}
