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
package org.easysoa.records.replay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;
import org.easysoa.records.assertions.AssertionEngine;
import org.easysoa.records.assertions.AssertionEngineImpl;
import org.easysoa.records.assertions.StringAssertion;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.template.setters.CustomParamSetter;
import org.easysoa.template.setters.RestFormParamSetter;
import org.easysoa.template.setters.RestPathParamSetter;
import org.easysoa.template.setters.RestQueryParamSetter;
import org.easysoa.template.setters.WSDLParamSetter;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * This service allows a user (ex. through a web UI) to choose, load, replay 
 * an exchange and check the response. 
 * 
 * If the user wants to change the entry parameters, he should rather
 * - change them in the client business application and record another exchange
 * - or make some templates out of the exchange
 * 
 * The recorded response is used to provide an idea of how much the actual response is OK :
 * - by doing a diff (on server or client) and displaying it
 * - LATER by running asserts gotten from correlator
 * 
 * REST HOWTO
 * 
 * How to return collections in jaxrs / jaxb / cxf (without going to Spring conf) : create a strong-typed collection class ex. ExchangeRecords
 * http://dhruba.name/2008/12/08/rest-service-example-using-cxf-22-jax-rs-10-jaxb-and-spring/#comment-781
 * NB. resteasy provides additional non-standard annotations for that : @Wrapped http://stackoverflow.com/questions/6192389/root-element-name-in-collections-returned-by-resteasy
 * 
 * How to handle a reference to another object not as an object but as its id : XmlJavaTypeAdapter
 * http://docs.oracle.com/javase/6/docs/api/javax/xml/bind/annotation/adapters/XmlJavaTypeAdapter.html
 * 
 * @author jguillemotte
 *
 */
// TODO REST JAXRS service, web UI
@Scope("composite")
public class ExchangeReplayServiceImpl implements ExchangeReplayService {
	
    // SCA Reference to replay engine
    @Reference
    public ReplayEngine replayEngine;
    
	// Logger
	private static Logger logger = Logger.getLogger(ExchangeReplayServiceImpl.class.getName());	
	
	// TODO Enable the environment data
	// Running environment
	//private String environment;

	/**
	 * Default constructor 
	 */
	public ExchangeReplayServiceImpl(){}
	
	@Override
	@GET
	@Path("/getExchangeRecordList/{storeName}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public RecordCollection getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName) throws Exception {
	    return replayEngine.getExchangeRecordlist(exchangeRecordStoreName);
	}

	@Override
	@GET
	@Path("/getExchangeRecord/{storeName}/{exchangeID}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ExchangeRecord getExchangeRecord(@PathParam("storeName") String exchangeRecordStoreName, @PathParam("exchangeID") String exchangeID) throws Exception {
	    return replayEngine.getExchangeRecord(exchangeRecordStoreName, exchangeID);
	}
	
	
	@Override
	@GET
	@Path("/getExchangeRecordStorelist")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public StoreCollection getExchangeRecordStorelist() throws Exception {
	    return replayEngine.getExchangeRecordStorelist();
	}
	
	// To replay an exchange record directly without any modifications
	@Override
	@Path("/replay/{exchangeRecordStoreName}/{exchangeRecordId}")
	@Produces("application/json")
	public String replay(@PathParam("exchangeRecordStoreName") String exchangeRecordStoreName, @PathParam("exchangeRecordId") String exchangeRecordId) throws Exception {
	    OutMessage message = replayEngine.replay(exchangeRecordStoreName, exchangeRecordId);
	    return message.getMessageContent().getContent();
	}
	
	@Override
	@Produces("application/json")
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment) {
		// LATER
		// requires to extract service in request & response
	}

	@GET
	@Path("/templates/")
	//@Produces("")
	@Override
	public String getTemplateRecordList() {
		// TODO Auto-generated method stub
		return null;
	}	
	
	// To generate the form with the default values
	@GET
	@Path("/templates/getTemplate/{storeName}/{templateName}")
	@Produces("application/json")
	@Override
	public TemplateFieldSuggestions getTemplateFieldSuggestions(@PathParam("storeName") String storeName, @PathParam("recordID") String recordID) throws Exception {
    	return replayEngine.getTemplateFieldSuggestions(storeName, recordID);
	}

	// To process the replay action with the custom parameters get form HTML generated form
	/*@Override
	@POST
	@Path("/templates/replayWithTemplate/{exchangeStoreName}/{exchangeRecordID}/{templateName}")
	//@Consumes("multipart/form-data")
	@Consumes("application/x-www-form-urlencoded")
	public String replayWithTemplate(MultivaluedMap<String, String> formData, @PathParam("exchangeStoreName") String exchangeStoreName, @PathParam("exchangeRecordID") String exchangeRecordID, @PathParam("templateName") String templateName) throws Exception {
	    return replayEngine.replayWithTemplate(formData, exchangeStoreName, exchangeRecordID, templateName);
	}*/

	
}
