package br.com.pdasolucoes.standardconfig.network;

import org.ksoap2.serialization.SoapObject;

import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;
import br.com.pdasolucoes.standardconfig.network.enums.TypeService;

public class UpdateTokenNotificationRequest extends SoapRequestBase {

    private String token;

    public UpdateTokenNotificationRequest(String token) {
        this.token = token;
    }

    @Override
    public SoapObject getBodySoap() {

        SoapObject soapObject = new SoapObject();

        soapObject.addProperty("token", token);
        soapObject.addProperty("codigoFilial", "2");

        return soapObject;
    }

    @Override
    protected RequestInfo getRequestInfo() {
        return new RequestInfo("ReposicaoService.asmx",
                "UpdateTokenNotificacao",
                RequestType.OnLine,
                -1,
                null,
                null,
                null,
                null
                , 0, false);
    }

    @Override
    public void processResult(Object o) {

    }

    @Override
    public void processError(MessageConfiguration messageConfiguration) {

    }
}
