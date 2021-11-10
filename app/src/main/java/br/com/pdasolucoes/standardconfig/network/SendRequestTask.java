package br.com.pdasolucoes.standardconfig.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
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
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
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
        String bodyInRetorno = "";
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

            if (this.request.isUniqueReturn()) {
                bodyInRetorno = envelope.bodyIn.toString();
                response = (SoapObject) envelope.bodyIn;
            } else
                response = (SoapObject) envelope.getResponse();

        } catch (IOException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        } catch (XmlPullParserException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        } catch (ClassCastException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(bodyInRetorno);
            return MessageConfiguration.ExceptionError;
        } catch (Exception e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        }

        return response;
    }


    private Object requestREST() {
        try {

            int timeout = this.request.getTimeOut();
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setSoTimeout(httpParams, timeout);

            String baseUrl = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "");
            String service = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.Directory, "");
            String action = this.request.getAction();


            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResp;
            if (this.request.getMethodRequest() == MethodRequest.POST) {
                HttpPost postRequest = new HttpPost(baseUrl + service + "/" + action);
                HttpEntity entity = this.request.getRequestEntity();
                postRequest.setEntity(entity);
                httpResp = httpClient.execute(postRequest);
            } else if (this.request.getMethodRequest() == MethodRequest.GET) {
                HttpGet geRequest = new HttpGet(baseUrl + service + "/" + action);
                httpResp = httpClient.execute(geRequest);
            } else if (this.request.getMethodRequest() == MethodRequest.PUT) {
                HttpPut httpPut = new HttpPut(baseUrl + service + "/" + action);
                httpResp = httpClient.execute(httpPut);
            } else if (this.request.getMethodRequest() == MethodRequest.PATCH) {
                HttpPatch patchRequest = new HttpPatch(baseUrl + service + "/" + action);
                HttpEntity entity = this.request.getRequestEntity();
                patchRequest.setEntity(entity);
                httpResp = httpClient.execute(patchRequest);
            } else {
                HttpDelete httpDelete = new HttpDelete(baseUrl + service + "/" + action);
                httpResp = httpClient.execute(httpDelete);
            }
            JSONObject jsonResponse;

            try {
                jsonResponse = new JSONObject(EntityUtils.toString(httpResp.getEntity()));
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
        } catch (IllegalStateException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        }
    }

    public class HttpPatch extends HttpPost {

        public static final String METHOD_PATCH = "PATCH";

        public HttpPatch(final String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return METHOD_PATCH;
        }
    }
}
