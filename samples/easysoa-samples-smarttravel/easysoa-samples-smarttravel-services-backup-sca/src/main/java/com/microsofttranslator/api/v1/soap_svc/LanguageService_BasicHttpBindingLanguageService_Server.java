
package com.microsofttranslator.api.v1.soap_svc;

import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-22T18:52:21.972+02:00
 * Generated source version: 2.3.3
 * 
 */
 
public class LanguageService_BasicHttpBindingLanguageService_Server{

    protected LanguageService_BasicHttpBindingLanguageService_Server() throws Exception {
        System.out.println("Starting Server");
        Object implementor = new LanguageServiceImpl();
        String address = "http://api.microsofttranslator.com/V1/soap.svc";
        Endpoint.publish(address, implementor);
    }
    
    public static void main(String args[]) throws Exception { 
        new LanguageService_BasicHttpBindingLanguageService_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}