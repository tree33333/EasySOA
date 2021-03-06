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

package com.openwide.easysoa.run;

import org.easysoa.records.ExchangeRecord;
import com.openwide.easysoa.monitoring.MonitoringService;

public interface RunManager {

	/**
	 * Returns the current run. if there is no current run and autoStart is set to true, 
	 * a new run is automatically started (autostart), otherwise null is returned
	 * @return The current <code>Run</code>
	 */
	public Run getCurrentRun() throws Exception;
	
	/**
	 * Returns true if there is a current run
	 * @return true if there is a current run
	 */
	public boolean isCurrentRun();
	
	/**
	 * Returns the specified run
	 * @param runName The run name to return
	 * @return The run corresponding to the run name
	 * @throws Exception if the run is not found 
	 */
	//public Run getRun(String runName) throws Exception;
	
	/**
	 * Delete the run
	 * @throws Exception If the run cannot be deleted
	 */
	public String delete() throws Exception;
	
	/**
	 * Starts a new run. A new <code>Run</code> is started only if the current run was stopped before with a call to the stop() method. 
	 * @param runName The name of the run. Must be unique, the name is the id of the run.
	 */
	public String start(String runName) throws Exception;

	/**
	 * Stop the current run
	 * @throws Exception If the run cannot be stopped
	 */
	public String stop() throws Exception;

	/**
	 * Returns the last run
	 * @return The last <code>Run</code>
	 */
	//public Run getLastRun();

	/**
	 * Record a message in the current run
	 * @param message The <code>Message</code> to record
	 */
	public void record(ExchangeRecord exchangeRecord);
	
	/**
	 * Returns the list of all recorded runs in their record order
	 * @return Return the names of all recorded runs
	 */
	//public List<String> getOrderedRunNames();
	
	/**
	 * Rerun a the specified run
	 * @param runName The run to rerun
	 */
	//public void reRun(String runName) throws Exception;
	
	/**
	 * Save the run
	 */
	public String save() throws Exception;
	
	/**
	 * 
	 * @return
	 */
	public MonitoringService getMonitoringService();
}