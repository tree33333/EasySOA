package org.easysoa.rest.servicefinder;

import java.io.FileInputStream;
import java.net.URL;

import org.easysoa.services.HttpDownloader;
import org.easysoa.services.HttpDownloaderService;
import org.nuxeo.runtime.api.Framework;

public class BrowsingContext {

	private URL url = null;
	
	private String data = null;

	/**
	 * 
	 * @param data The response produced when the user browsed to the URL.
	 */
	public BrowsingContext(URL url, String data) throws Exception {
		this.url = url;
		this.data = data;
	}
	
	public BrowsingContext(URL url) throws Exception {
		if (url != null) {
			this.url = url;
			
			// Download the file at the given URL
	    	HttpDownloaderService httpDownloaderService = Framework.getService(HttpDownloaderService.class);
	        HttpDownloader file = httpDownloaderService.createHttpDownloader(url);
	        FileInputStream fis = null;
	        try {
		        file.download();
		        fis = new FileInputStream(file.getFile());
		        StringBuffer dataBuffer = new StringBuffer();
		        int c;
		        while ((c = fis.read()) != -1) {
		        	dataBuffer.append((char) c);
		        }
		        data = dataBuffer.toString();
	        }
	        catch (Exception e) {
	        	data = null;
	        }
	        finally {
	            if (fis != null) {
	                fis.close();
	            }
	        	file.delete();
	        }
		}
	}
	
	/**
	 * @return The browsed URL
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * @return The response produced when the user browsed to the URL.
	 */
	public String getData() {
		return data;
	}
	
}
