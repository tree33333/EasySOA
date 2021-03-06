/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package com.openwide.easysoa.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * A class to store message seen in the proxy
 * @author jguillemotte
 *
 */
@Deprecated
public class Message {
	
	/**
	 *	Message types 
	 */
	public enum MessageType {
	    WSDL, REST, SOAP 
	}

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(Message.class.getName());	
	
	// Message data
	private String protocol;
	private String host;
	private int port;
	private String pathName;
	private String parameters;
	private String content;
	private String method;
	private MessageType type;
	private String url;
	private String body;
	// Response info
	private String response;
	
	/**
	 * Initialize a new message
	 * @param request The HttpServletRequest
	 */
	public Message(HttpServletRequest request){
		this.pathName = request.getRequestURI();
		this.parameters = request.getQueryString();
		this.host = request.getServerName();
		this.port = request.getServerPort();
		if(request.getProtocol().toLowerCase().contains("http")){
			this.protocol = "http";
		} else {
			this.protocol = request.getProtocol();
		}
		this.method = request.getMethod();
		this.content = "";
	    char[] charArray = new char[8192];
	    StringBuffer requestBody = new StringBuffer();
	    BufferedReader requestBodyReader = null;
		try {
			requestBodyReader = request.getReader();
			int nbCharRead;
		    while((nbCharRead = requestBodyReader.read(charArray)) != -1){
		    	requestBody = requestBody.append(new String(charArray), 0, nbCharRead);
		    }
			this.body = requestBody.toString();
		} catch (IOException ex) {
			logger.error("Error while reading request body !", ex);
		} finally {	    
			try {
				requestBodyReader.close();
			} catch (IOException ex) {
				logger.warn("Error while closing the requestBodyReader !", ex);
			}
		}
		this.url = request.getRequestURL().toString();
	}
	
	/**
	 * Constructor
	 * @param host
	 * @param port
	 * @param pathName
	 * @param parameters
	 */
	public Message(String url, String protocol, String host, int port, String pathName, String parameters, MessageType type){
		this.url = url;
		if(protocol.toLowerCase().contains("http")){
			this.protocol = "http";
		} else {
			this.protocol = protocol;
		}
		this.host = host;
		this.port = port;
		this.pathName = pathName;
		this.parameters = parameters;
		this.type = type;
		this.content = "";
		this.method = "";
		this.body = "";
	}
	
	/**
	 * Constructor
	 * @param url Url
	 * @param type Type
	 */
	public Message(URL url, MessageType type) {
		this(url.toString(), url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), type);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return
	 */
	public String getParameters(){
		if(parameters == null){
			parameters = "";
		}
		return parameters;
	}

	/**
	 * 
	 * @return
	 */
	public String getHost() {
		if(host == null){
			host = "";
		}		
		return host;
	}

	/**
	 * 
	 * @return
	 */
	public String getPathName() {
		if(pathName == null){
			pathName = "";
		}
		return pathName;
	}

	/**
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * @return
	 */
	public String getProtocol(){
		if(protocol == null){
			protocol = "";
		}		
		return protocol;
	}

	/**
	 * 
	 * @return
	 */
	public String getContent(){
		if(content == null){
			content = "";
		}		
		return content;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMethod(){
		if(method == null){
			method = "";
		}		
		return method;
	}
	
	/**
	 * 
	 * @return
	 */
	public MessageType getType(){
		return type;
	}
	
	/**
	 * 
	 * @param type
	 */
	public void setType(MessageType type){
		this.type = type;
	}
	
	/**
	 * 
	 * @param body
	 */
	public void setBody(String body){
		this.body = body;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBody(){
		if(body == null){
			body = "";
		}
		return this.body;
	}
	
	/**
	 * 
	 * @param response
	 */
	public void setResponse(String response){
		this.response = response;
	}
	
	/**
	 * Returns the response
	 * @return
	 */
	public String getResponse(){
		return response;
	}
	
	/**
	 * Returns the complete message. url + url parameters
	 * @return
	 */
	public String getCompleteMessage(){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append(pathName);
		sb.append("?");
		sb.append(parameters);
		return sb.toString();
	}

	/**
	 * Returns the complete path. Url without url parameters
	 * @return
	 */
	public String getCompletePath(){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append(pathName);
		return sb.toString();
	}	
	
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Message => [");
		sb.append("protocol: ");
		sb.append(protocol);		
		sb.append(", port: ");
		sb.append(port);
		sb.append(", host: ");
		sb.append(host);
		sb.append(", pathname: ");
		sb.append(pathName);
		sb.append(", parameters: ");
		sb.append(parameters);
		sb.append(", content: ");
		sb.append(content);		
		sb.append("]");
		return sb.toString();
	}
	
}
