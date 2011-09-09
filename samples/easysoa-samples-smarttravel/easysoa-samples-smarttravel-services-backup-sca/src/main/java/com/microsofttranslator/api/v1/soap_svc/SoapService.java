
/*
 * 
 */

package com.microsofttranslator.api.v1.soap_svc;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-22T18:52:21.975+02:00
 * Generated source version: 2.3.3
 * 
 */


@WebServiceClient(name = "SoapService", 
                  //wsdlLocation = "file:microsoftTranslatorWebService.test.wsdl",
                  targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc") 
public class SoapService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://api.microsofttranslator.com/v1/soap.svc", "SoapService");
    public final static QName BasicHttpBindingLanguageService = new QName("http://api.microsofttranslator.com/v1/soap.svc", "BasicHttpBindingLanguageService");
    static {
        URL url = null;
        try {
            url = new URL("file:microsoftTranslatorWebService.test.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:microsoftTranslatorWebService.test.wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public SoapService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SoapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SoapService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SoapService(WebServiceFeature ... features) {
        //super(WSDL_LOCATION, SERVICE, features);
    	super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SoapService(URL wsdlLocation, WebServiceFeature ... features) {
        //super(wsdlLocation, SERVICE, features);
    	super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SoapService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        //super(wsdlLocation, serviceName, features);
    	super(wsdlLocation, serviceName);
    }

    /**
     * 
     * @return
     *     returns LanguageService
     */
    @WebEndpoint(name = "BasicHttpBindingLanguageService")
    public LanguageService getBasicHttpBindingLanguageService() {
        return super.getPort(BasicHttpBindingLanguageService, LanguageService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns LanguageService
     */
    @WebEndpoint(name = "BasicHttpBindingLanguageService")
    public LanguageService getBasicHttpBindingLanguageService(WebServiceFeature... features) {
        return super.getPort(BasicHttpBindingLanguageService, LanguageService.class, features);
    }

}
