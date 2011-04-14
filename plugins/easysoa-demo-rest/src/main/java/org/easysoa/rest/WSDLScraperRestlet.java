package org.easysoa.rest;

import java.net.URL;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jboss.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseStatelessNuxeoRestlet;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.StringRepresentation;

/**
 * REST service to found WSDLs from given URL.
 * 
 * Use:
 * .../nuxeo/restAPI/wsdlupload/{url}
 * Params:
 * {url} The page to consider, without the "http://" prefix (other protocols not supported), and not encoded
 * 
 * @author mkalam-alami
 *
 */
public class WSDLScraperRestlet extends BaseStatelessNuxeoRestlet {
	private static final Logger log = Logger
			.getLogger(WSDLScraperRestlet.class);
	private static final String REPOSITORY = "default";
	private static final String HTTP = "http://";

	public void handle(Request request, Response response) {
		super.initRepository(response, REPOSITORY);

		// Initialization
		String failure = null;
		JSONObject result = new JSONObject();
		try {
			result.put("html", "");
			result.put("foundLinks", "");
		} catch (JSONException e) {
			log.error("Cannot initialize JSON object.", e);
			failure = e.getMessage();
		}
		
		// URL parsing 
		String url = null;
		try {
			url = RequestURL.parse(request);
			result.put("url", url);
		} catch (Exception e) {
			failure = e.getMessage();
			log.error("Cannot rebuild URL", e);
		}

		// Web page download
		HttpFile f = new HttpFile(url);
		if (failure == null) {
			try {
				f.download();
			} catch (Exception e) {
				log.info("WSDL download problem : " + e.getMessage());
				failure = e.getMessage();
			}
		}

		// Web page parsing
		JSONObject foundLinks = new JSONObject();
		try {
			String host = HTTP + new URL(url).getHost();
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode cleanHtml = cleaner.clean(f.getFile());
			Object[] links = cleanHtml.evaluateXPath("//a");

			for (Object o : links) {
				TagNode link = (TagNode) o;
				String ref = link.getAttributeByName("href");
				String name = (link.getText() != null) ? link.getText().toString() : ref;
				int i = 1;
				if (ref != null && ref.toLowerCase().endsWith("wsdl")) {
					while (foundLinks.has(name)) {
						name = (i == 1 ? name + i++ : name.substring(0, name
								.length() - 1))
								+ i++;
					}
					foundLinks.put(name, absolute(ref, host));
				}
			}
			result.append("step", "parsing3");
			result.put("foundLinks", foundLinks);

			changeToAbsolutePath(links, "href", host);
			changeToAbsolutePath(cleanHtml.evaluateXPath("//script"), "href",
					host);
			changeToAbsolutePath(cleanHtml.evaluateXPath("//link"), "href",
					host);
			result.put("html", cleaner.getInnerHtml(cleanHtml));

			result.append("step", "parsing ok");
		} catch (Exception e) {
			try {
				result.append("step", "parsing "+e);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			log.info("Page download problem : " + e.getMessage());
			failure = e.getMessage();
		}

		// Format result
		String resultJSONP = null;
		try {
			result.append("error", failure);
			resultJSONP = JSONP.format(result, request);
		} catch (Exception e) {
			log.warn("Cannot format JSONP message : " + e.getMessage());
			failure = e.getMessage();
		}
		
		// Send result
		try {
			if (failure != null)
				result.append("error", failure);
			
			if (resultJSONP != null)
				response.setEntity(new StringRepresentation(JSONP.format(result,
						request), MediaType.APPLICATION_JAVASCRIPT, Language.ALL,
						CharacterSet.UTF_8));
			else
				response.setEntity(new StringRepresentation(result.toString(2),
						MediaType.APPLICATION_JAVASCRIPT, Language.ALL,
						CharacterSet.UTF_8));
		} catch (Exception e) {
			log.warn("Cannot send message : " + e.getMessage());
		}

		f.delete();
	}

	private static void changeToAbsolutePath(Object[] tagNodes, String attribute,
			String domain) {
		for (Object o : tagNodes) {
			TagNode tag = (TagNode) o;
			String attrValue = tag.getAttributeByName(attribute);
			if ((attrValue != null) && (!attrValue.startsWith(HTTP)))
				tag.setAttribute(attribute, domain + attrValue);
		}
	}
	
	private static String absolute(String url, String domain) {
		if (url.startsWith(HTTP))
			return url;
		else if (domain.startsWith(HTTP))
			return domain + url;
		else
			return HTTP + domain + url;
	}
}