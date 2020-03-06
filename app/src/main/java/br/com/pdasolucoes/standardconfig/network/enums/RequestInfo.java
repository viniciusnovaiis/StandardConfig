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

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId) {
        this(service, action, requestType, descriptionResourceId, true, TypeService.SOAP);
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, String nameSpace) {
        this(service, action, requestType, descriptionResourceId, nameSpace, TypeService.SOAP);
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, boolean requireAuthentication, TypeService typeService) {
        this.service = service;
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.requireAuthentication = requireAuthentication;
        this.typeService = typeService;
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, String nameSpace, TypeService typeService) {
        this.service = service;
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.typeService = typeService;
        this.nameSpace = nameSpace;
    }

    public RequestInfo(String service, String action, RequestType requestType, int descriptionResourceId, boolean requireAuthentication,
                       String entity, String objectName, Class<?> objectClass, String nameSpace, MarshalType[] marshalTypes, TypeService typeService) {
        this.service = service;
        this.action = action;
        this.descriptionResourceId = descriptionResourceId;
        this.requestType = requestType;
        this.requireAuthentication = requireAuthentication;
        this.typeService = typeService;
        this.nameSpace = nameSpace;
        this.entity = entity;
        this.objectClass = objectClass;
        this.marshal = marshalTypes;
        this.objectName = objectName;
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
}
