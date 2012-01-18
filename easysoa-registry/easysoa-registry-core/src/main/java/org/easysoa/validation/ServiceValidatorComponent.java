/**
 * EasySOA Registry
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

package org.easysoa.validation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.easysoa.services.ServiceValidationService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

public class ServiceValidatorComponent extends DefaultComponent implements ServiceValidationService {

    public static final ComponentName NAME = new ComponentName(ComponentName.DEFAULT_TYPE, "org.easysoa.core.service.ServiceValidatorComponent");
    
    public static final String EXTENSION_POINT_VALIDATORS = "validators";
    
    private static final Log log = LogFactory.getLog(ServiceValidatorComponent.class);
    
    private static List<ServiceValidator> validators = new LinkedList<ServiceValidator>();

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) throws Exception {
        if (EXTENSION_POINT_VALIDATORS.equals(extensionPoint)) {
            ServiceValidatorDescriptor descriptor = (ServiceValidatorDescriptor) contribution;
            Class<?> descriptorClass = Class.forName(descriptor.className);
            if (ServiceValidator.class.isAssignableFrom(descriptorClass)) {
                synchronized (validators) {
                    ServiceValidator serviceValidator = (ServiceValidator) descriptorClass.newInstance();
                    serviceValidator.setName(descriptor.name);
                    serviceValidator.setLabel(descriptor.label);
                    validators.add(serviceValidator);
                }
            }
            else {
                throw new Exception("Failed to register contribution: " + descriptor.className + " is not a ServiceValidator");
            }
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (EXTENSION_POINT_VALIDATORS.equals(extensionPoint)) {
            ServiceValidatorDescriptor descriptor = (ServiceValidatorDescriptor) contribution;
            ServiceValidator validatorToRemove = null;
            synchronized (validators) {
                for (ServiceValidator validator : validators) {
                    if (validator.getClass().getName().equals(descriptor.className)
                            || validator.getName().equals(descriptor.name)) {
                        validatorToRemove = validator;
                        break;
                    }
                }
                if (validatorToRemove != null) {
                    validators.remove(validatorToRemove);
                }
                else {
                    log.info("Tried to unregister non-registered class " + descriptor.className);
                }
            }
        }
    }

    public List<ServiceValidator> getValidators() {
        return validators;
    }

    @Override
    public List<String> validateServices(CoreSession session, DocumentModel model) throws Exception {

        // Find model's workspace and reference environment
        DocumentService docService = Framework.getService(DocumentService.class);
        boolean modelIsWorkspace = Workspace.DOCTYPE.equals(model.getType());
        DocumentModel workspace = (modelIsWorkspace) ? model : docService.getWorkspace(session, model);
        
        DocumentModel referenceEnv = docService.findEnvironment(session, 
                (String) workspace.getProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDENVIRONMENT));

        // Start validation
        DocumentModelList services = null;
        List<String> result, errors = new LinkedList<String>();
        
        if (referenceEnv != null) {

            // Find services to match
            DocumentModelList referenceServices = getAllServices(session, referenceEnv);
            if (Service.DOCTYPE.equals(model.getType())) {
                services = new DocumentModelListImpl();
                services.add(model);
            }
            else {
                services = getAllServices(session, model);
            }
            
            // Validate services
            if (services != null) {
                for (DocumentModel service : services) {
                	Object isValidationStateDirty = service.getContextData(Service.CONTEXT_VALIDATIONSTATEDIRTY);
                	if (isValidationStateDirty == null || ((Boolean) isValidationStateDirty)) {
	                    DocumentModel matchingService = null;
	                    SortedSet<CorrelationMatch> matches = findCorrelatedServices(session, service, referenceServices);
	                    if (!matches.isEmpty()) {
	                        matchingService = matches.first().getDocumentModel();
	                    }
	                    result = validateService(session, service, matchingService);
	                    if (!result.isEmpty()) {
	                        errors.addAll(result);
	                    }
                	}
                }
                
            }

        }
        else {
            errors.add("No valid reference environment");
        }

        return errors;
    }
    
    /**
     * 
     * @param service
     * @param referenceService
     * @return A list of errors
     * @throws ClientException 
     */
    private List<String> validateService(CoreSession session, DocumentModel service, DocumentModel referenceService) throws ClientException {

        service.putContextData(Service.CONTEXT_VALIDATIONSTATEDIRTY, false);
        
        // Init
        List<String> allErrors = new LinkedList<String>();
        List<Map<String, Object>> newValidationState = new LinkedList<Map<String, Object>>(); 
        boolean isNowValidated = true;
		
        // Run validations
        if (referenceService != null) {
            synchronized (validators) {
                for (ServiceValidator validator : validators) {
                    try {
                        // Validation
                        List<String> validatorErrors = validator.validateService(null, service, referenceService);
                        isNowValidated = isNowValidated && validatorErrors.isEmpty();
                        
                        // Fill validation state
                        Map<String, Object> validatorResult = new HashMap<String, Object>();
                        validatorResult.put(Service.SUBPROP_VALIDATORNAME, validator.getName());
                        validatorResult.put(Service.SUBPROP_ISVALIDATED, validatorErrors.isEmpty());
                        validatorResult.put(Service.SUBPROP_VALIDATIONLOG, formatErrors(validatorErrors));
                        newValidationState.add(validatorResult);
                        
                    } catch (Exception e) {
                        
                        // Ignore validator
                        Map<String, Object> validatorResult = new HashMap<String, Object>();
                        validatorResult.put(Service.SUBPROP_VALIDATORNAME, validator.getName());
                        validatorResult.put(Service.SUBPROP_VALIDATIONLOG, "(ignored)");
                        newValidationState.add(validatorResult);
                    }
                }
            }
        }
        else {
            allErrors.add("Service has no reference service.");
        }
        
        // Update service validation state
        service.setProperty(Service.SCHEMA, Service.PROP_ISVALIDATED, isNowValidated);
        service.setProperty(Service.SCHEMA, Service.PROP_VALIDATIONSTATE, newValidationState);
        session.saveDocument(service); 
        
        return allErrors;
    }
    
    private String formatErrors(List<String> errors) {
        StringBuilder result = new StringBuilder();
        for (String error : errors) {
            result.append(error + '\n');
        }
        return result.toString();
    }

    public SortedSet<CorrelationMatch> findCorrelatedServices(CoreSession session, DocumentModel service) throws Exception {
        DocumentService docService = Framework.getService(DocumentService.class);
        DocumentModel workspaceModel = docService.getWorkspace(session, service);
        String referencedEnvironmentTitle = (String) workspaceModel.getProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDENVIRONMENT);
        if (referencedEnvironmentTitle != null) {
            DocumentModel referencedEnvironmentModel = docService.findEnvironment(session, referencedEnvironmentTitle);
            return findCorrelatedServices(session, service, getAllServices(session, referencedEnvironmentModel));
        }
        else {
            return null;
        }
    }

    private SortedSet<CorrelationMatch> findCorrelatedServices(CoreSession session, DocumentModel service,
            DocumentModelList referenceServices) throws ClientException, MalformedURLException {
        
        SortedSet<CorrelationMatch> matches = new TreeSet<CorrelationMatch>();
        DocumentModel referenceModel = null;
        
        // Fetch reference service
        String referenceId = (String) service.getProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE);
        if (referenceId != null) {
            DocumentRef referenceRef = new IdRef(referenceId);
            if (session.exists(referenceRef)) {
                referenceModel = session.getDocument(new IdRef(referenceId));
                matches.add(new CorrelationMatch(referenceModel, 1.0));
            }
        }
       
        /*
         * Guess reference service by correlation on WSDLs
         * Correlation scale (if results reach > 1.0, they are truncated to 1.0):
         * - WSDL Service name = 0.7
         * - WSDL Namespace = 0.3
         * - Service name = 0.3
         * - Service URL paths = 0.3
         */
        if (referenceModel == null) {
            String namespace = (String) service.getProperty(Service.SCHEMA, Service.PROP_WSDLNAMESPACE);
            String serviceName = (String) service.getProperty(Service.SCHEMA, Service.PROP_WSDLSERVICENAME);
            String title = service.getTitle();
            String urlPath = new URL((String) service.getProperty(Service.SCHEMA, Service.PROP_URL)).getPath();
            for (DocumentModel potentialMatch : referenceServices) {
                double correlationGrade = 0;
                correlationGrade += getComparisonGrade(serviceName, 
                        (String) potentialMatch.getProperty(Service.SCHEMA, Service.PROP_WSDLSERVICENAME), 0.7);
                correlationGrade += getComparisonGrade(namespace, 
                        (String) potentialMatch.getProperty(Service.SCHEMA, Service.PROP_WSDLNAMESPACE), 0.3);
                correlationGrade += getComparisonGrade(title, 
                        (String) potentialMatch.getTitle(), 0.3);
                correlationGrade += getComparisonGrade(urlPath, 
                        new URL((String) potentialMatch.getProperty(Service.SCHEMA, Service.PROP_URL)).getPath(), 0.3);
                
                if (correlationGrade >= 0.3) {
                    matches.add(new CorrelationMatch(potentialMatch, Math.min(correlationGrade, 1.0)));
                }
            }
        }
        
        return matches;
    }
    
    private double getComparisonGrade(String value1, String value2, double maxGrade) {
        if (value1 == null || value2 == null) {
            return 0;
        }
        if (value1.equals(value2)) {
            return maxGrade;
        }
        else if (value1.contains(value2) || value2.contains(value1)) {
            return maxGrade / 2; 
        }
        else {
            return maxGrade * Math.max(value1.length() - LevenshteinDistance.getLevenshteinDistance(value1, value2), 0) 
                    / value1.length();
        }
    }
    
    private DocumentModelList getAllServices(CoreSession session, DocumentModel model) throws ClientException {
        DocumentModelList services = new DocumentModelListImpl();
        for (DocumentModel child : session.getChildren(model.getRef(), null, new DeletedDocumentFilter(), null)) {
            if (child.getType().equals(Service.DOCTYPE)) {
                services.add(child);
            }
            else {
                services.addAll(getAllServices(session, child));
            }
        }
        return services;
    }
    
}
