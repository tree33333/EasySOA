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

package org.easysoa.registry.frascati;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;

import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;

/**
 * TODO pb : now wrongly depends on Nuxeo through FraSCAtiServiceItf
 * 
 * @author jguillemotte
 *
 */
public abstract class FraSCAtiRegistryServiceBase implements FraSCAtiRegistryServiceItf {

	private static Log log = LogFactory.getLog(FraSCAtiRegistryServiceBase.class);
    
    protected FraSCAtiServiceItf frascati; // TODO make it independent from nuxeo by reimplementing it also directly on top of FraSCAti ??!!?????

        
	public FraSCAtiRegistryServiceBase() {
	}

	/**
	 * Get an SCA composite.
	 * 
	 * @param composite 
	 *     the composite to get.
	 * @return 
	 *     the composite.
	 */
	public Composite getComposite(String composite) throws Exception { //TODO prettier ApiException ??
		return frascati.getComposite(composite);
	}

	/**
	 * Added for convenience in a first time. TODO To improve usability and
	 * modularity, calls to its methods should be replaced by calls to
	 * FraSCAtiService methods calling them.
	 * 
	 * @return
	 */
	public FraSCAtiServiceItf getFraSCAti() {
		return frascati;
	}
	
	/**
         * 
         * @param compositeUrl
         * @param scaZipUrls
         * @return
         * @throws Exception
         */
        public Composite readComposite(URL compositeUrl, URL... scaZipUrls)   throws Exception {
                
                return readComposite(compositeUrl, FraSCAtiServiceItf.check, scaZipUrls);
        }
        
	/**
	 * 
	 * @param compositeUrl
	 * @param scaZipUrls
	 * @return
	 * @throws Exception
	 */
	public Composite readComposite(URL compositeUrl, int mode, URL... scaZipUrls) throws Exception {
	    
	        if(frascati == null)
	        {
	            String msg = "No FraSCAtiService attached to this FraSCAti Registry Service "
	                    + "when reading composite " + compositeUrl;
	            log.debug(msg);
	            throw new Exception(msg);
	        }
		// Create a processing context with where to find ref'd classes
		log.debug("composite URL = " + compositeUrl);
		log.debug("scaZipUrls = " + scaZipUrls);
		
		String compositeName = null;
		
		try {
			compositeName = frascati.processComposite(
			        compositeUrl.toString(), mode,scaZipUrls);
		} 
		catch (FraSCAtiServiceException fe) { 
			log.error("The number of checking errors is equals to " + frascati.getErrors());
			log.error("The number of checking warnings is equals to " + frascati.getWarnings());
			log.error(fe);	
		}
		// TODO feed parsing errors / warnings up to UI ?!
		log.warn("\nErrors while parsing " + compositeUrl + ":\n" + frascati.getErrorMessages());
		log.info("\nWarnings while parsing " + compositeUrl + ":\n" + frascati.getWarningMessages());
		
		Composite composite = frascati.getComposite(compositeName);
		
		if(composite == null){
			throw new FraSCAtiRegistryException("Composite '" + compositeUrl + "' can not be loaded");
		}
		return composite;
	}

	/**
	 * Reads a zip (or jar), parses and returns the composites within. Classes
	 * they references are resolved within the zip. Only known (i.e. in the
	 * classpath / maven) extensions can be parsed (impls, bindings...).
	 * 
	 * TODO pbs : processContribution() and processComposite() return null in
	 * check mode, so has to use a separate ProcessingContext for each read (and
	 * alter each one's classloader)
	 * s
	 * @param scaZipFile
	 * @return
	 */
	public Set<Composite> readScaZip(File scaZipFile) throws Exception {
		// NB. can't use processContribution() because in check mode returns
		// nothing
		// and puts in the context only the single root composite

		URL scaZipFileUrl = scaZipFile.toURI().toURL();

		// if sca zip : (still fails to load sub composite, i.e. intent, because
		// looks for it at .)
		Set<URL> scaZipCompositeURLSet = FileUtils.unzipAndGetFileUrls(scaZipFile);
		Set<Composite> scaZipCompositeSet = new HashSet<Composite>();
		for (URL scaZipCompositeURL : scaZipCompositeURLSet) {
			if (scaZipCompositeURL.getPath().endsWith(".composite")) {
				log.info("Found composite to parse " + scaZipCompositeURL);
				Composite scaComposite = readComposite(scaZipCompositeURL, scaZipFileUrl);
				if (scaComposite != null) {
					scaZipCompositeSet.add(scaComposite);
				}
			}
		}
		return scaZipCompositeSet;
	}
	
}
