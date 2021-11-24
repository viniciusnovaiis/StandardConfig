package br.com.pdasolucoes.standardconfig.network;

import android.app.Activity;
import android.app.ProgressDialog;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Date;

import br.com.pdasolucoes.standardconfig.R;
import br.com.pdasolucoes.standardconfig.enums.MarshalType;
import br.com.pdasolucoes.standardconfig.managers.NetworkManager;
import br.com.pdasolucoes.standardconfig.network.enums.MessageConfiguration;
import br.com.pdasolucoes.standardconfig.network.enums.MethodRequest;
import br.com.pdasolucoes.standardconfig.network.enums.RequestInfo;
import br.com.pdasolucoes.standardconfig.network.enums.RequestType;
import br.com.pdasolucoes.standardconfig.network.enums.TypeService;
import br.com.pdasolucoes.standardconfig.network.interfaces.IRequest;
import br.com.pdasolucoes.standardconfig.network.interfaces.IRequestHandler;
import br.com.pdasolucoes.standardconfig.utils.MyApplication;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public abstract class RequestBase implements IRequest {

    private ProgressDialog progressDialog;
    private WeakReference<Activity> progressDialogContext;

    private Activity context;

    private IRequestHandler handler = null;
    private Date requestTimestamp;

    public RequestBase() {
        this.context = NavigationHelper.getCurrentAppCompat();
    }

    public RequestBase(Activity context) {
        this.context = context;
    }

    @Override
    public void taskStart() {
        if (this.getRequestType() == RequestType.OnLine) {
            this.showProgressDialog(this.context, this.getDescription());
        }

        this.requestTimestamp = new Date();
    }

    @Override
    public void taskCompleted(Object result) {
        if (this.getRequestType() == RequestType.OnLine) {
            this.closeProgressDialog();
        }

        MessageConfiguration messageConfiguration = null;

        if (result instanceof MessageConfiguration) {
            messageConfiguration = (MessageConfiguration) result;
        }

        if (this.handleError(messageConfiguration)) {
            this.processError(messageConfiguration);
            if (this.handler != null) {
                this.handler.onError();
            }
            return;
        }

        this.processResult(result);
        if (this.handler != null) {
            this.handler.onSuccess();
        }
    }

    private void showProgressDialog(Activity context, String message) {
        if (context == null || context.isFinishing()) {
            return;
        }

        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setTitle(R.string.dialog_progress_title);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(message);

        this.progressDialogContext = new WeakReference<>(context);

        this.progressDialog.show();
    }

    private void closeProgressDialog() {
        if (this.progressDialogContext == null) {
            return;
        }

        Activity context = this.progressDialogContext.get();

        if (context != null && !context.isFinishing()) {
            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        }
    }

    private boolean handleError(MessageConfiguration messageConfiguration) {

        if (messageConfiguration == null)
            return false;

        if (messageConfiguration == MessageConfiguration.ExceptionError) {
            if (this.getRequestType() == RequestType.OnLine)
                NavigationHelper.showDialog(context.getString(R.string.title_error), messageConfiguration.getExceptionErrorMessage(), null, null);
            return true;
        }

        if (messageConfiguration == MessageConfiguration.NetworkError &&
                this.getRequestType() == RequestType.OffLine) {
            NetworkManager.saveOffLineRequest((SoapRequestBase) this);
            return false;
        }

        if (messageConfiguration == MessageConfiguration.NetworkError
                || messageConfiguration == MessageConfiguration.PermissonDeniedError) {
            if (this.getRequestType() == RequestType.OnLine)
                NavigationHelper.showDialog(context.getString(R.string.title_error), context.getString(messageConfiguration.getMsg()), null, null);
            return true;
        }

        this.processError(messageConfiguration);
        return true;
    }

    @Override
    public void taskProgress(Void... progress) {
        // Do nothing
    }

    protected abstract RequestInfo getRequestInfo();


    @Override
    public String getService() {
        return this.getRequestInfo().getService();
    }

    @Override
    public String getAction() {
        return this.getRequestInfo().getAction();
    }

    @Override
    public RequestType getRequestType() {
        return this.getRequestInfo().getRequestType();
    }

    @Override
    public String getNameSpace() {
        return this.getRequestInfo().getNameSpace();
    }

    @Override
    public String getDescription() {
        return this.getRequestInfo().getDescription();
    }

    @Override
    public TypeService getTypeService() {
        return this.getRequestInfo().getTypeService();
    }

    @Override
    public String getEntity() {
        return this.getRequestInfo().getEntity();
    }

    @Override
    public Class<?> getObject() {
        return this.getRequestInfo().getObject();
    }

    @Override
    public MarshalType[] getMarshalTypes() {
        return this.getRequestInfo().getMarshal();
    }

    @Override
    public String getObjectName() {
        return this.getRequestInfo().getObjectName();
    }

    @Override
    public HttpEntity getRequestEntity() throws UnsupportedEncodingException, JSONException {
        return null;
    }

    @Override
    public SoapObject getRequestSoapObject() {
        return this.getRequestSoapObject();
    }

    @Override
    public int getTimeOut() {
        return this.getRequestInfo().getTimeout();
    }

    @Override
    public boolean isUniqueReturn() {
        return this.getRequestInfo().isUniqueReturn();
    }

    @Override
    public boolean isObjectList() {
        return this.getRequestInfo().isObjectList();
    }

    @Override
    public MethodRequest getMethodRequest() {
        return this.getRequestInfo().getMethodRequest();
    }
}
