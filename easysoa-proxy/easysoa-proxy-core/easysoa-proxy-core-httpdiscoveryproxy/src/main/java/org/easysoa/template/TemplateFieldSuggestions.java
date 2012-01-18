/**
 * 
 */
package org.easysoa.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A set of templateFields to use in the template builder
 * 
 * @author jguillemotte
 *
 */
public class TemplateFieldSuggestions {

	// List of template fields
	private List<TemplateField> templateFields;

	/**
	 * Constructor
	 */
	public TemplateFieldSuggestions() {
		this.templateFields = new ArrayList<TemplateField>();
	}
	
	/**
	 * Add a templateField in the list
	 * @param templateField
	 * @throws IllegalArgumentException If templateField is null
	 */
	public void add(TemplateField templateField) throws IllegalArgumentException {
		if(templateField == null){
			throw new IllegalArgumentException("templateField must not be null !");
		}
		templateFields.add(templateField);
	}
	
	/**
	 * 
	 * @return
	 */
    @XmlElement(name="TemplateField")
    @XmlElementWrapper(name="templateFields")
	public List<TemplateField> getTemplateFields() {
		return templateFields;
	}

	/**
	 * 
	 * @param templateFields
	 */
	public void setTemplateFields(List<TemplateField> templateFields) {
		this.templateFields = templateFields;
	}	
}
