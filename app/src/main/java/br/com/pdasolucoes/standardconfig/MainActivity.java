package br.com.pdasolucoes.standardconfig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.com.pdasolucoes.standardconfig.managers.AuthManager;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.service.AuthenticationPost;
import br.com.pdasolucoes.standardconfig.service.Login;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.Service;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthManager.launchService();
            }
        });


        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.ServerAddress,
                    "https://189.113.15.118:3101/");
                ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.Directory,"api");

                NetworkManager.sendRequest(new Login());

            }
        });
    }


}
