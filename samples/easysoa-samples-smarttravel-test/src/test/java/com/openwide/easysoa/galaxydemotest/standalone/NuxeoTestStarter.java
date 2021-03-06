package com.openwide.easysoa.galaxydemotest.standalone;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.test.EasySOAServerFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.inject.Inject;
import com.openwide.easysoa.galaxydemotest.EasySOARepositoryInit;

/**
 * Unit test for Galaxy Demo.
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAServerFeature.class)
@Jetty(port = EasySOAConstants.NUXEO_TEST_PORT)
@RepositoryConfig(type = BackendType.H2, user = "Administrator", init = EasySOARepositoryInit.class)
public class NuxeoTestStarter {

    @Inject CoreSession session;
    
    @Inject ResourceService resourceService;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(getInvokingClassName());

    /**
     * 
     * @return
     */
    public static String getInvokingClassName() {
        return Thread.currentThread().getStackTrace()[1].getClassName();
    }

    /**
     * 
     */
    static {
        System.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
    }

    /**
     * Sends a query to the document repository to make sure Nuxeo is launched.
     * 
     * @throws Exception
     * 
     */
    @Test
    public final void testNuxeoStarted() throws Exception {
        DocumentModelList resDocList = session.query("SELECT * FROM Document");
        Iterator<DocumentModel> iter = resDocList.iterator();
        while (iter.hasNext()) {
            DocumentModel doc = iter.next();
            logger.debug("Doc name : " + doc.getName());
        }
        assertEquals(resDocList.size(), 3);
    }
}