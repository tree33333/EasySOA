package net.webservicex;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.2.1
 * Mon May 18 19:03:28 CEST 2009
 * Generated source version: 2.2.1
 * 
 */
 
@WebService(targetNamespace = "http://www.webserviceX.NET", name = "GlobalWeatherSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface GlobalWeatherSoap {

    @WebResult(name = "GetCitiesByCountryResult", targetNamespace = "http://www.webserviceX.NET")
    @RequestWrapper(localName = "GetCitiesByCountry", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetCitiesByCountry")
    @ResponseWrapper(localName = "GetCitiesByCountryResponse", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetCitiesByCountryResponse")
    @WebMethod(operationName = "GetCitiesByCountry", action = "http://www.webserviceX.NET/GetCitiesByCountry")
    public java.lang.String getCitiesByCountry(
        @WebParam(name = "CountryName", targetNamespace = "http://www.webserviceX.NET")
        java.lang.String countryName
    );

    @WebResult(name = "GetWeatherResult", targetNamespace = "http://www.webserviceX.NET")
    @RequestWrapper(localName = "GetWeather", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetWeather")
    @ResponseWrapper(localName = "GetWeatherResponse", targetNamespace = "http://www.webserviceX.NET", className = "net.webservicex.GetWeatherResponse")
    @WebMethod(operationName = "GetWeather", action = "http://www.webserviceX.NET/GetWeather")
    public java.lang.String getWeather(
        @WebParam(name = "CityName", targetNamespace = "http://www.webserviceX.NET")
        java.lang.String cityName,
        @WebParam(name = "CountryName", targetNamespace = "http://www.webserviceX.NET")
        java.lang.String countryName
    );
}
