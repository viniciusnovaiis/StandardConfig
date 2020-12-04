package br.com.pdasolucoes.standardconfig.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import br.com.pdasolucoes.standardconfig.enums.MarshalType;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.TypeService;
import br.com.pdasolucoes.standardconfig.network.interfaces.IRequest;
import br.com.pdasolucoes.standardconfig.utils.ConfigurationHelper;

public class SendRequestTask extends AsyncTaskRunner<Void, Void, Object> {

    private IRequest request;

    public SendRequestTask(IRequest request) {
        super(request);
        this.request = request;
    }

    protected Object doInBackground(Void... params) {

        if (!NetworkManager.isNetworkOnline())
            return MessageConfiguration.NetworkError;

        TypeService typeService = this.request.getTypeService();

        if (TypeService.SOAP == typeService)
            return requestSOAP();
        else
            return requestREST();

    }

    private Object requestSOAP() {
        SoapObject response;

        try {

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(this.request.getRequestSoapObject());

            String baseUrl =
                    ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "").
                            concat("/")
                            .concat(ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.Directory, ""));

            if (this.request.getObjectName() != null)
                envelope.addMapping(this.request.getNameSpace(), this.request.getObjectName(), this.request.getObject());


            if (this.request.getMarshalTypes() != null) {
                for (MarshalType m : this.request.getMarshalTypes()) {
                    switch (m) {
                        case FLOAT:
                            new MarshalFloat().register(envelope);
                            break;
                        case BASE64:
                            new MarshalBase64().register(envelope);
                            break;
                        case DATETIME:
                            new MarshalDate().register(envelope);
                            break;
                    }
                }
            }


            HttpTransportSE transportSE;
            if (this.request.getTimeOut() != 0)
                transportSE = new HttpTransportSE(baseUrl.concat("/").concat(this.request.getService()), this.request.getTimeOut());
            else
                transportSE = new HttpTransportSE(baseUrl.concat("/").concat(this.request.getService()));


            transportSE.call(this.request.getNameSpace() + this.request.getAction(), envelope);

            if (this.request.getObjectName() != null || this.request.isUniqueReturn())
                response = (SoapObject) envelope.bodyIn;
            else
                response = (SoapObject) envelope.getResponse();

        } catch (IOException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        } catch (XmlPullParserException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        }catch (Exception e){
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        }

        return response;
    }


    private Object requestREST() {
        try {
            int timeout = 1000;
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setSoTimeout(httpParams, timeout);

            String service = this.request.getService();
            String action = this.request.getAction();

            String baseUrl = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "");

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(baseUrl + service + "/" + action);

            HttpEntity entity = this.request.getRequestEntity();
            postRequest.setEntity(entity);

            HttpResponse httpResp = httpClient.execute(postRequest);

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(), "UTF-8"));
            String response = reader.readLine();
            reader.close();

            JSONObject jsonResponse;

            try {
                jsonResponse = new JSONObject(response);
            } catch (Exception e) {
                return MessageConfiguration.getJSONResultMessage(e.getMessage());
            }

            return jsonResponse;
        } catch (SocketTimeoutException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        } catch (MalformedURLException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        } catch (IOException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        }
    }
}
