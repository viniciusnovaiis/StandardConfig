package br.com.pdasolucoes.standardconfig.network.enums;


import androidx.appcompat.app.AppCompatActivity;

import org.ksoap2.serialization.SoapObject;

import br.com.pdasolucoes.standardconfig.enums.MarshalType;
import br.com.pdasolucoes.standardconfig.utils.NavigationHelper;

public class RequestInfo {
    private String service;
    private String action;
    private int descriptionResourceId;
    private RequestType requestType;
    private boolean requireAuthentication;
    private TypeService typeService;
    private String nameSpace;
    private String entity;
    private Class<?> objectClass;
    private MarshalType[] marshal;
    private String objectName;
    private String url;
    private int timeout;
    private boolean objectList;
    private boolean isUniqueReturn;
    private MethodRequest methodRequest;

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, int timeout, boolean isUniqueReturn) {
        this(service, action, requestType, descriptionResourceId, true, TypeService.SOAP, timeout, isUniqueReturn);
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId) {
        this(service, action, requestType, descriptionResourceId, true, TypeService.SOAP, 0, false);
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, String nameSpace, int timeout) {
        this(service, action, requestType, descriptionResourceId, nameSpace, TypeService.SOAP, timeout, false);
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, String nameSpace) {
        this(service, action, requestType, descriptionResourceId, nameSpace, TypeService.SOAP, 0, false);
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, String nameSpace, boolean isUniqueReturn) {
        this(service, action, requestType, descriptionResourceId, nameSpace, TypeService.SOAP, 0, isUniqueReturn);
    }

    public RequestInfo(String action, int descriptionResourceId, MethodRequest methodRequest, int timeout) {
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.timeout = timeout;
        this.typeService = TypeService.REST;
        this.methodRequest = methodRequest;
    }

    public RequestInfo(String action, RequestType requestType, int descriptionResourceId, MethodRequest methodRequest, int timeout) {
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.timeout = timeout;
        this.typeService = TypeService.REST;
        this.methodRequest = methodRequest;
    }

    public RequestInfo(String action, int descriptionResourceId, MethodRequest methodRequest) {
        this(action, descriptionResourceId, methodRequest, 1000);
    }


    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, boolean requireAuthentication, TypeService typeService, int timeout, boolean isUniqueReturn) {
        this.service = service;
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.requireAuthentication = requireAuthentication;
        this.typeService = typeService;
        this.timeout = timeout;
        this.isUniqueReturn = isUniqueReturn;
    }


    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, String nameSpace, TypeService typeService, int timeout, boolean isUniqueReturn) {
        this.service = service;
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.typeService = typeService;
        this.nameSpace = nameSpace;
        this.timeout = timeout;
        this.isUniqueReturn = isUniqueReturn;
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId,
                       String entity, String objectName, Class<?> objectClass, String nameSpace, MarshalType[] marshalTypes, TypeService typeService, int timeout, boolean isObjectList, boolean isUniqueReturn) {
        this.service = service;
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.typeService = typeService;
        this.nameSpace = nameSpace;
        this.entity = entity;
        this.objectClass = objectClass;
        this.marshal = marshalTypes;
        this.objectName = objectName;
        this.timeout = timeout;
        this.objectList = isObjectList;
        this.isUniqueReturn = isUniqueReturn;
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId,
                       String entity, String objectName, Class<?> objectClass, MarshalType[] marshalTypes, int timeout, boolean isObjectList, boolean isUniqueReturn) {
        this(service, action, requestType, descriptionResourceId, entity, objectName, objectClass, "http://tempuri.org/", marshalTypes, TypeService.SOAP, timeout, isObjectList, isUniqueReturn);
    }

    public String getService() {
        return this.service;
    }

    public String getAction() {
        return this.action;
    }

    public RequestType getRequestType() {
        return this.requestType;
    }

    public String getDescription() {

        AppCompatActivity appCompatActivity = NavigationHelper.getCurrentAppCompat();

        if (appCompatActivity == null || descriptionResourceId == -1)
            return "";

        return appCompatActivity.getString(descriptionResourceId);
    }

    public TypeService getTypeService() {
        return typeService;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getEntity() {
        return entity;
    }

    public Class<?> getObject() {
        return objectClass;
    }

    public SoapObject getSoapObjectToList() {
        return new SoapObject(nameSpace, entity);
    }

    public boolean getRequireAuthentication() {
        return this.requireAuthentication;
    }

    public MarshalType[] getMarshal() {
        return marshal;
    }

    public String getObjectName() {
        return objectName;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isUniqueReturn() {
        return isUniqueReturn;
    }

    public boolean isObjectList() {
        return objectList;
    }

    public MethodRequest getMethodRequest() {
        return methodRequest;
    }

    public String getUrl() {
        return url;
    }
}
