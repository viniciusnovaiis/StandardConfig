package br.com.pdasolucoes.standardconfig.network;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;


public abstract class SoapRequestBase extends RequestBase {
    @Override
    public SoapObject getRequestSoapObject() {
        SoapObject soapObject = new SoapObject(this.getNameSpace(), this.getAction());

        SoapObject requestBody = this.getBodySoap();

        if (requestBody != null) {
            if (this.getObjectName() == null) {
                for (int i = 0; i < requestBody.getPropertyCount(); i++) {
                    PropertyInfo propertyInfo = new PropertyInfo();
                    requestBody.getPropertyInfo(i, propertyInfo);
                    soapObject.addProperty(propertyInfo.name, requestBody.getProperty(i).toString());
                }
            } else {
                if (!this.isObjectList()) {
                    for (int i = 0; i < requestBody.getPropertyCount(); i++) {
                        PropertyInfo propertyInfo = new PropertyInfo();
                        requestBody.getPropertyInfo(i, propertyInfo);
                        soapObject.addProperty(propertyInfo.name, requestBody.getProperty(i));
                    }
                    return soapObject;
                }
                soapObject.addSoapObject(requestBody);
            }
        }
        return soapObject;
    }

    public abstract SoapObject getBodySoap();
}
