package br.com.pdasolucoes.standardconfig.network.enums;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public enum MessageConfiguration {


    NetworkError(1, R.string.title_error, R.string.message_error_network),
    ExceptionError(2, R.string.title_error),
    NotAuthorizaton(2, R.string.n_autorizado),
    ContextViewError(2, R.string.title_error, R.string.error_view),
    PermissonDeniedError(2, R.string.title_error, R.string.error_permission_denied);


    private int code;
    private int resourceTitle;
    private int resourceMsg;
    private String exceptionErrorMessage;

    MessageConfiguration(int code, int resourceTitle, int resourceMsg) {
        this.code = code;
        this.resourceTitle = resourceTitle;
        this.resourceMsg = resourceMsg;
    }

    MessageConfiguration(int code, int resourceTitle) {
        this.code = code;
        this.resourceTitle = resourceTitle;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return resourceTitle;
    }

    public int getMsg() {
        return resourceMsg;
    }

    public String getExceptionErrorMessage() {
        return exceptionErrorMessage;
    }

    public void setExceptionErrorMessage(String exceptionErrorMessage) {
        this.exceptionErrorMessage = exceptionErrorMessage;
    }

    public JSONObject getJSONResult() {
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("ResultCode", this.getCode());
        } catch (JSONException jsonEx) {
            //Do nothing
        }
        return jsonResponse;
    }

    public static JSONObject getJSONResultMessage(String msg) {
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("Message", msg);
        } catch (JSONException jsonEx) {
            //Do nothing
        }
        return jsonResponse;
    }
}
