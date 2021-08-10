package br.com.pdasolucoes.standardconfig;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.network.JsonRequestBase;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;
import br.com.pdasolucoes.standardconfig.utils.Helper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkManager.sendRequest(new TestApi());
            }
        });


        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.actionError(true, true);

            }
        });
    }


    public class TestApi extends JsonRequestBase {


        public TestApi() {
            ConfigurationHelper.savePreference(ConfigurationHelper.ConfigurationEntry.ServerAddress,"http://192.168.100.241:9015/api");
        }

        @Override
        public JSONObject getBody() {

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("Login", "102030");
                jsonObject.put("PasswordMobile", "909090");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected RequestInfo getRequestInfo() {
            return new RequestInfo("LoginMobile", R.string.configuration_configuration, MethodRequest.POST);
        }

        @Override
        public void processResult(Object data) {

        }

        @Override
        public void processError(MessageConfiguration result) {

        }
    }

}
