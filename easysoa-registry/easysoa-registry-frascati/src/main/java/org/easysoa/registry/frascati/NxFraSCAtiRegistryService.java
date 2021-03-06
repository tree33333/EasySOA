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

/**
 * EasySOA: OW2 FraSCAti in Nuxeo
 * Copyright (C) 2011 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Philippe Merle
 *
 * Contributor(s):
 *
 */

package org.easysoa.registry.frascati;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.IScaRuntimeImporter;
import org.easysoa.sca.frascati.ApiFraSCAtiScaImporter;
import org.easysoa.sca.frascati.FraSCAtiScaImporter;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.bridge.Application;
import org.nuxeo.runtime.model.Adaptable;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.Extension;

/**
 * 
 * @author pmerle, mdutoo
 * 
 *         TODO solve maven deps : <groupId>org.eclipse.jdt</groupId>
 *         <artifactId>core</artifactId> <version>3.3.0.771</version> <!-- TODO
 *         Eclipse m2e says : Overriding managed version 3.1.1-NXP-4284 for core
 *         ?!! -->
 * 
 */
public class NxFraSCAtiRegistryService extends FraSCAtiRegistryServiceBase 
implements org.nuxeo.runtime.model.Component, Adaptable {
	
	// Service component
	public static final ComponentName NAME = new ComponentName("org.easysoa.registry.frascati.FraSCAtiServiceComponent");

	// Logger
    private static Log log = LogFactory.getLog(NxFraSCAtiRegistryService.class);
	
	// Nuxeo Core session
	private CoreSession documentManager;
	
	// List of Easy SOA Apps
	private List<EasySOAApp> apps;
	
	/**
         * 
         */
    public NxFraSCAtiRegistryService()
    {

        super();
        // Instantiate OW2 FraSCAti.
        /*
         * easySOAApp = new FraSCAtiBootstrapApp(); easySOAApp.start();
         */

        // For test only
        // Start the HttpDiscoveryProxy in Nuxeo with embedded FraSCAti
        // does not works because NuxeoFrascati is not yet instanced ....
        /*
         * log.debug("Trying to load Http discovery proxy !");
         * System.out.println("Trying to load Http discovery proxy !"); try {
         * if(easySOAApp.getFrascati() != null){
         * //easySOAApp.getFrascati().processComposite("httpDiscoveryProxy");
         * easySOAApp.getFrascati().processComposite("scaffoldingProxy"); } else
         * { log.debug("Unable to get FraSCAti, null returned !");
         * System.out.println("Unable to get FraSCAti, null returned !"); } }
         * catch (NuxeoFraSCAtiException ex) { // TODO Auto-generated catch
         * block
         * log.debug("Error catched when trying to load Http discovery proxy !",
         * ex); System.out.println(
         * "Error catched when trying to load Http discovery proxy : " +
         * ex.getMessage()); }
         */

        // TODO : Disabled for release building, to uncomment when the
        // integration of Frascati in Nuxeo will works better.
        frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class)
                .getFraSCAtiService();
    }

    /*
     * public void setApps(List<EasySOAApp> apps){ this.apps = apps; }
     */

    /**
     * TODO LATER Move in FraSCAtiAppManager, use EasySOAApp as parameter,
     * remember them to allow to list & stop them 
     * TODO LATER possibly make it async, wrap CoreSession in an 
     * EasySOAIdentity which will have other things (ex. user/pass, jaas...) 
     * on client side
     * 
     * @param scaAppUrl
     * @param documentManager
     * @return
     * @throws Exception
     */
    public String[] startScaApp(URL scaAppUrl, CoreSession documentManager)
            throws Exception
    {
        this.setDocumentManager(documentManager);
        IScaRuntimeImporter runtimeImporter = (IScaRuntimeImporter) 
                newRuntimeScaImporter();
        this.frascati.setScaImporterRecipient(runtimeImporter);
        String[] compositeNames = this.frascati.processContribution(scaAppUrl
                .toString());
        this.frascati.setScaImporterRecipient(null);
        return compositeNames;
    }

    /**
     * 
     * @param documentManager
     * @return
     * @throws Exception
     */
    public IScaRuntimeImporter newRuntimeScaImporter(
            CoreSession documentManager) throws Exception
    {
        LocalBindingVisitorFactory nxBindingVisitorFactory = 
                new LocalBindingVisitorFactory(documentManager);
        IScaRuntimeImporter fraSCAtiScaImporter = (IScaRuntimeImporter)
                new FraSCAtiScaImporter(nxBindingVisitorFactory, null);
        return fraSCAtiScaImporter;
    }
    

    public IScaRuntimeImporter newRuntimeScaImporter(File compositeFile) 
            throws Exception
    {
        return newLocalScaImporter(documentManager,compositeFile);
    }
    
    public IScaRuntimeImporter newRuntimeScaImporter()
    {
        try
        {
            return newRuntimeScaImporter(new File("."){
                
                /**
                 * Generated default serial ID
                 */
                private static final long serialVersionUID = 1L;

                public String getName()
                {
                    return "SCA Runtime Import";
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @param documentManager
     * @param compositeFile
     * @return
     * @throws Exception
     */
    public IScaRuntimeImporter newLocalScaImporter(CoreSession documentManager,
            File compositeFile) throws Exception
    {

        LocalBindingVisitorFactory nxBindingVisitorFactory = new LocalBindingVisitorFactory(
             documentManager);
        IScaRuntimeImporter  fraSCAtiScaImporter = (IScaRuntimeImporter)
             new FraSCAtiScaImporter(nxBindingVisitorFactory, compositeFile);
        return fraSCAtiScaImporter;
    }

    public void activate(ComponentContext context) throws Exception
    {
        this.frascati = Framework.getLocalService(
                FraSCAtiServiceProviderItf.class).getFraSCAtiService(); 
        
        // TODO : don't call  it in constructor else too early

        /*
         * AppComponent appComponent = Framework.getService(AppComponent.class);
         * // Too early, AppComponent not yet started 
         * this.apps = appComponent.getApps();
         */

        log.debug("Starting components");
        // For test only
        // Start the HttpDiscoveryProxy in Nuxeo with embedded FraSCAti
        // log.debug("Trying to load Http discovery proxy !");
        // System.out.println("Trying to load Http discovery proxy (NxFrascatiRegistryService.activate method)!");
        try
        {
            if (this.frascati != null)
            {
                // this.frascati.processComposite("../../easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy/src/main/resources/httpDiscoveryProxy.composite");
                // easySOAApp.getFrascati().processContribution("../../easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy/target/easysoa-proxy-core-httpdiscoveryproxy-0.4-SNAPSHOT.jar");
                // easySOAApp.getFrascati().processComposite("scaffoldingProxy");

                // Start EasySOAApps
                // TODO : Apps variable still null at the moment, How to set ?
                if (apps != null)
                {
                    for (EasySOAApp easySOAApp : apps)
                    {
                        try
                        {
                            easySOAApp.start();
                        } catch (Exception ex)
                        {
                            log.error(
                                    "An error occurs during the start of EasySOAApp",
                                    ex);
                        }
                    }
                }

            } else
            {
                log.debug("Unable to get FraSCAti, null returned !");
                System.out.println("Unable to get FraSCAti, null returned !");
            }
        } catch (Exception ex)
        {
            // TODO Auto-generated catch block
            log.debug("Error catched when trying to load the EasySOA apps !",
                    ex);
            System.out
                    .println("Error catched when trying to load the EasySOA apps  : "
                            + ex.getMessage());
        }

    }

    public void deactivate(ComponentContext context) throws Exception
    {
        log.debug("Closing components");
        
        if (apps != null)
        {
            for (EasySOAApp easySOAApp : apps)
            {
                try
                {
                    easySOAApp.stop();
                } catch (Exception ex)
                {
                    log.error("An error occurs during the stop of EasySOAApp",
                            ex);
                }
            }
        }

        ((Application)Framework.getLocalService(
                FraSCAtiServiceProviderItf.class)).destroy();
    }

    public void registerExtension(Extension extension) throws Exception
    {

        Object[] contribs = extension.getContributions();
        if (contribs == null)
        {
            return;
        }
        for (Object contrib : contribs)
        {
            registerContribution(contrib, extension.getExtensionPoint(),
                    extension.getComponent());
        }
    }

    public void unregisterExtension(Extension extension) throws Exception
    {
        Object[] contribs = extension.getContributions();
        if (contribs == null)
        {
            return;
        }
        for (Object contrib : contribs)
        {
            unregisterContribution(contrib, extension.getExtensionPoint(),
                    extension.getComponent());
        }
    }

    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception
    {

        log.debug("NxFrascatiRegistryService debug (contribution) : "
                + contribution);
        log.debug("NxFrascatiRegistryService debug (extensionPoint) : "
                + extensionPoint);
        log.debug("NxFrascatiRegistryService debug (contributor) : "
                + contributor);
    }

    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception
    {
        log.debug("NxFrascatiRegistryService debug (contribution) : "
                + contribution);
        log.debug("NxFrascatiRegistryService debug (extensionPoint) : "
                + extensionPoint);
        log.debug("NxFrascatiRegistryService debug (contributor) : "
                + contributor);
    }

    public <T> T getAdapter(Class<T> adapter)
    {
        return adapter.cast(this);
    }

    public void applicationStarted(ComponentContext context) throws Exception
    {
        // do nothing by default
    }

    public CoreSession getDocumentManager()
    {
        return documentManager;
    }

    public void setDocumentManager(CoreSession documentManager)
    {
        this.documentManager = documentManager;
    }

    /* (non-Javadoc)
     * @see org.easysoa.registry.frascati.FraSCAtiRegistryServiceItf#newScaImporter(java.io.File)
     */
    public IScaImporter newScaImporter(File compositeFile) throws Exception
    {
        return new ApiFraSCAtiScaImporter(
                new LocalBindingVisitorFactory(documentManager),compositeFile,this);
    }

}
