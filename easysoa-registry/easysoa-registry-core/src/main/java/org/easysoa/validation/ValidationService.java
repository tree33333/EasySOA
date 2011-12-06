package org.easysoa.validation;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface ValidationService {
    
    /**
     * Validates all of the services contained in the given document
     * (or the one service given in parameter), against the workspace's reference environment. 
     * @param session
     * @param model
     * @throws Exception
     */
    void validateServices(CoreSession session, DocumentModel model) throws Exception;

}
