package br.com.pdasolucoes.standardconfig.network;

import android.text.TextUtils;

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
import org.json.JSONArray;
import org.json.JSONException;
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
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import br.com.pdasolucoes.standardconfig.enums.MarshalType;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
import br.com.pdasolucoes.standardconfig.network.enums.TypeService;
import br.com.pdasolucoes.standardconfig.network.interfaces.IRequest;
import br.com.pdasolucoes.standardconfig.service.Login;
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


            trustAllCertificates();
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

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object requestREST() {
        try {

            int timeout = this.request.getTimeOut();

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setSoTimeout(httpParams, timeout);

            String baseUrl = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.ServerAddress, "");
            String service = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.Directory, "");
            String serverApi = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.ServerAddressApi, "");
            String action = this.request.getAction();
            String token = ConfigurationHelper.loadPreference(ConfigurationHelper.ConfigurationEntry.Token, "");
            String baseUrlApi = serverApi;
            if (!TextUtils.isEmpty(serverApi)) {
//                if (!serverApi.contains("http") && serverApi.contains("https"))
//                    serverApi = "https://" + serverApi;
//                else if (!serverApi.contains("https") && serverApi.contains("http"))
//                    serverApi = "http://" + serverApi;
                baseUrlApi = serverApi;
            } else {
//                if (!baseUrl.contains("http") && baseUrl.contains("https"))
//                    baseUrl = "https://" + baseUrl;
//                else if (!baseUrl.contains("https") && baseUrl.contains("http"))
//                    baseUrl = "http://" + baseUrl;
                baseUrlApi = baseUrl + service;
            }

            if (baseUrlApi.contains("https")) {
                // Configurar SSLContext para confiar em todos os certificados
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                }, new java.security.SecureRandom());

                // Criar URL com IP
                URL url = new URL(baseUrlApi + "/" + action);

                // Abrir conexão HttpsURLConnection
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                // Definir o contexto SSL personalizado
                connection.setSSLSocketFactory(sslContext.getSocketFactory());

                // Desabilitar a verificação do hostname (não recomendado em produção)
                connection.setHostnameVerifier((hostname, session) -> true);

                // Configurar a requisição POST
                connection.setRequestMethod(this.request.getMethodRequest().toString());
                connection.setDoOutput(true);

                // Definir cabeçalho Content-Type
                connection.setRequestProperty("Content-Type", "application/json");

                // Enviar payload JSON
                if (this.request.getMethodRequest() == MethodRequest.POST
                        || this.request.getMethodRequest() == MethodRequest.PUT) {

                    OutputStream os = connection.getOutputStream();
                    byte[] input = EntityUtils.toString(this.request.getRequestEntity()).toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    os.close();
                }

                // Obter o código de resposta
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                }

                MessageConfiguration.ExceptionError.setExceptionErrorMessage("Error: " + responseCode + "\n" + connection.getContentType());
                return MessageConfiguration.ExceptionError;

            }

            if (!baseUrlApi.contains("http")) {
                baseUrlApi = "http://" + baseUrlApi;
            }


            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResp;
            if (this.request.getMethodRequest() == MethodRequest.POST) {
                HttpPost postRequest = new HttpPost(baseUrlApi + "/" + action);
                HttpEntity entity = this.request.getRequestEntity();
                if (!TextUtils.isEmpty(token))
                    postRequest.setHeader("Authorization", "Bearer" + " " + token);
                postRequest.setEntity(entity);
                httpResp = httpClient.execute(postRequest);
            } else if (this.request.getMethodRequest() == MethodRequest.GET) {
                HttpGet geRequest = new HttpGet(baseUrlApi + "/" + action);
                if (!TextUtils.isEmpty(token))
                    geRequest.setHeader("Authorization", "Bearer" + " " + token);
                httpResp = httpClient.execute(geRequest);
            } else if (this.request.getMethodRequest() == MethodRequest.PUT) {
                HttpPut httpPut = new HttpPut(baseUrlApi + "/" + action);
                HttpEntity entity = this.request.getRequestEntity();
                if (!TextUtils.isEmpty(token))
                    httpPut.setHeader("Authorization", "Bearer" + " " + token);
                httpPut.setEntity(entity);
                httpResp = httpClient.execute(httpPut);
            } else if (this.request.getMethodRequest() == MethodRequest.PATCH) {
                HttpPatch patchRequest = new HttpPatch(baseUrlApi + "/" + action);
                HttpEntity entity = this.request.getRequestEntity();
                if (!TextUtils.isEmpty(token))
                    patchRequest.setHeader("Authorization", "Bearer" + " " + token);
                patchRequest.setEntity(entity);
                httpResp = httpClient.execute(patchRequest);
            } else {
                HttpDelete httpDelete = new HttpDelete(baseUrlApi + "/" + action);
                if (!TextUtils.isEmpty(token))
                    httpDelete.setHeader("Authorization", "Bearer" + " " + token);
                httpResp = httpClient.execute(httpDelete);
            }

            return EntityUtils.toString(httpResp.getEntity());
        } catch (JSONException | IllegalStateException | IOException e) {
            MessageConfiguration.ExceptionError.setExceptionErrorMessage(e.getMessage());
            return MessageConfiguration.ExceptionError;
        } catch (IllegalArgumentException e) {
            MessageConfiguration.ExceptionError.
                    setExceptionErrorMessage(Objects.requireNonNull(e.getMessage()).concat(
                            "Volte e inicie a aplicação novamente"));
            return MessageConfiguration.ExceptionError;
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T castToClass(Object obj, Class<T> clazz) {
        try {
            // Se o objeto for da classe esperada, faz o cast e retorna o objeto
            return clazz.cast(obj);
        } catch (ClassCastException e) {
            System.out.println("Não é possível fazer o cast para o tipo " + clazz.getName());
            return null;
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
