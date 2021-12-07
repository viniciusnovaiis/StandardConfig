package br.com.pdasolucoes.standardconfig.network;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public abstract class JsonRequestBase extends RequestBase {

    @Override
    public HttpEntity getRequestEntity() throws UnsupportedEncodingException, JSONException {
        String requestContent = "";
        JSONObject requestBody = this.getBody();

        if (requestBody != null) {
            requestContent = requestBody.toString();
            if (requestBody.has("array")) {
                requestContent = requestBody.getJSONArray("array").toString();
                
            }
        }


        StringEntity entity = new StringEntity(requestContent, "UTF-8");
        entity.setContentType("application/json");
        return entity;
    }

    public abstract JSONObject getBody() throws JSONException;

}
