package de.daenet.webservices.currencyserver;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.1
 * Tue May 19 16:39:17 CEST 2009
 * Generated source version: 2.2.1
 * 
 */
 
@WebService(targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", name = "CurrencyServerWebServiceHttpGet")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CurrencyServerWebServiceHttpGet {

    @WebResult(name = "double", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public double getDollarValue(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider,
        @WebParam(partName = "currency", name = "currency", targetNamespace = "")
        java.lang.String currency
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getProviderTimestamp(
        @WebParam(partName = "providerId", name = "providerId", targetNamespace = "")
        java.lang.String providerId,
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getProviderList();

    @WebResult(name = "DataSet", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public DataSet getDataSet(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getXmlStream(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );

    @WebResult(name = "double", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public double getCurrencyValue(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider,
        @WebParam(partName = "srcCurrency", name = "srcCurrency", targetNamespace = "")
        java.lang.String srcCurrency,
        @WebParam(partName = "dstCurrency", name = "dstCurrency", targetNamespace = "")
        java.lang.String dstCurrency
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getProviderDescription(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );
}
