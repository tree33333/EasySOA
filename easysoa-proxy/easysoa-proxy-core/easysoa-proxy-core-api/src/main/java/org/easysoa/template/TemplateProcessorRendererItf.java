/**
 * 
 */
package org.easysoa.template;

import java.util.List;
import java.util.Map;
import org.easysoa.records.ExchangeRecord;
import com.openwide.easysoa.message.OutMessage;

/**
 * @author jguillemotte
 *
 */
public interface TemplateProcessorRendererItf {

	/**
	 * Render the request template by replacing template expression by provided values
	 * @param templatePath The request template
	 * @param record The associated record
	 * @param fieldValues Provided field values
	 * @return The rendered template
	 * @throws Exception If a problem occurs
	 */
	public OutMessage renderReq(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception;
	
	/**
	 * Render the response template by replacing template expression by provided values 
	 * @param templatePath The response template
	 * @param record The associated record
	 * @param fieldValues Provided field values
	 * @return The rendred template
	 * @throws Exception If a problem occurs
	 */
	public String renderRes(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception;
}
