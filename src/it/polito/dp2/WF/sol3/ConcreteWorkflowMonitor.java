package it.polito.dp2.WF.sol3;

import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.Refreshable;
import it.polito.dp2.WF.lab3.gen.UnknownNames_Exception;
import it.polito.dp2.WF.lab3.gen.Workflow;
import it.polito.dp2.WF.lab3.gen.WorkflowInfo;
import it.polito.dp2.WF.lab3.gen.WorkflowInfoService;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

/**
 * This is a concrete implementation of the interface {@link WorkflowMonitor} based on the JAX-WS framework.
 * 
 * @author Luca
 */
public class ConcreteWorkflowMonitor implements WorkflowMonitor, Refreshable {
	
	private Map<String, WorkflowReader> workflows;
	private Set<ProcessReader> processes;
	
	private WorkflowInfo proxy;
	private XMLGregorianCalendar lastUpdateTime;
	
	public ConcreteWorkflowMonitor() throws MalformedURLException, WorkflowMonitorException {
		
		// taking the URL of the Web Service
		String webServiceString = System.getProperty("it.polito.dp2.WF.sol3.URL");
		URL webServiceURL = new URL(webServiceString);
		
		// taking the port (proxy) from the service
		WorkflowInfoService service = new WorkflowInfoService(webServiceURL);
		proxy = service.getWorkflowInfoPort();
		
		System.out.println("...Building the WorkflowMonitor...");
		buildWorkflowMonitor();
		System.out.println(workflows.size()+" workflows were created.");
		
	}

	/**
	 * This method creates the {@link WorkflowMonitor} taking the data from SOAP a Web Service.
	 * 
	 * @throws WorkflowMonitorException - If it is not possible to instantiate the {@link ConcreteWorkflowMonitor}
	 */
	private void buildWorkflowMonitor() throws WorkflowMonitorException {
		workflows = new HashMap<String, WorkflowReader>();
		processes = new HashSet<ProcessReader>();		// it must remains empty
		
		Holder<XMLGregorianCalendar> calendarHolder = new Holder<XMLGregorianCalendar>();
		Holder<List<String>> workflowNamesHolder = new Holder<List<String>>();
		proxy.getWorkflowNames(calendarHolder, workflowNamesHolder);
		
		Holder<List<Workflow>> workflowsHolder = new Holder<List<Workflow>>();
		try {
			System.out.println("...Retrieving the workflows...");
			proxy.getWorkflows(workflowNamesHolder.value, calendarHolder, workflowsHolder);
			lastUpdateTime = calendarHolder.value;
		} catch (UnknownNames_Exception e) {
			throw new WorkflowMonitorException("Error retrieving the workflows: "+e.getMessage());
		}
		
		// build the WorkflowReaderSet
		for( Workflow wf: workflowsHolder.value ) {
			WorkflowReader wfr = new ConcreteWorkflowReader(wf);
			workflows.put(wfr.getName(), wfr);
		}
		// this loop is to managing the ProcessActions
		for( WorkflowReader wf : workflows.values() ) {
			if(wf instanceof ConcreteWorkflowReader)
				((ConcreteWorkflowReader)wf).setWfsInsideProcessActions(workflows);
		}
	}

	/**
	 * The refresh() method must align the local information about workflows in the client 
	 * with the information currently provided by the service.  
	 */
	@Override
	public void refresh() {
		System.out.println("...Starting the update procedure...");
		try {
			updateWorkflowMonitor();
		} catch (WorkflowMonitorException e) {
			System.err.println("Error! Impossible to retrieve the workflows: "+e.getMessage());
			System.out.println("Refresh aborted!");
			return;
		}
		System.out.println(workflows.size()+" workflows were updated.");
	}
	
	/**
	 * This method updates the {@link WorkflowMonitor} taking the data from SOAP a Web Service.
	 * 
	 * @throws WorkflowMonitorException - If it is not possible to instantiate the {@link ConcreteWorkflowMonitor}
	 */
	private void updateWorkflowMonitor() throws WorkflowMonitorException {
		// --- Taking the names of the workflows --- //
		Holder<XMLGregorianCalendar> calendarHolder = new Holder<XMLGregorianCalendar>();
		Holder<List<String>> workflowNamesHolder = new Holder<List<String>>();
		proxy.getWorkflowNames(calendarHolder, workflowNamesHolder);
		
		// --- Taking the workflows and the last update time --- //
		Holder<List<Workflow>> workflowsHolder = new Holder<List<Workflow>>();
		try {
			System.out.println("...Retrieving the workflows...");
			proxy.getWorkflows(workflowNamesHolder.value, calendarHolder, workflowsHolder);
		} catch (UnknownNames_Exception e) {
			throw new WorkflowMonitorException("Error retrieving the workflows: "+e.getMessage());
		}
		
		if( calendarHolder.value.compare(lastUpdateTime) == DatatypeConstants.GREATER ) {
			
			workflows = new HashMap<String, WorkflowReader>();
			processes = new HashSet<ProcessReader>();		// it must remains empty
			
			// build the WorkflowReaderSet
			for( Workflow wf: workflowsHolder.value ) {
				WorkflowReader wfr = new ConcreteWorkflowReader(wf);
				workflows.put(wfr.getName(), wfr);
			}
			// this loop is to managing the ProcessActions
			for( WorkflowReader wf : workflows.values() ) {
				if(wf instanceof ConcreteWorkflowReader)
					((ConcreteWorkflowReader)wf).setWfsInsideProcessActions(workflows);
			}
			
			System.out.println("All the workflows were updated!");
		}
		else {
			System.out.println("There is nothing to update!");
		}
	}

	@Override
	public Set<ProcessReader> getProcesses() {
		return processes;
	}

	@Override
	public WorkflowReader getWorkflow(String name) {
		return workflows.get(name);
	}

	@Override
	public Set<WorkflowReader> getWorkflows() {
		return new LinkedHashSet<WorkflowReader>(workflows.values());
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("Inside this WorkflowMonitor there are:\n");
		
		buf.append("--- Workflows ---\n");
		if((workflows==null) || (workflows.isEmpty()))
			buf.append("\tNo Workflows\n");
		else {
			for(WorkflowReader wfr : workflows.values())
				buf.append(wfr.toString()+"\n");
		}
		buf.append("\n");
		
		buf.append("--- Processes ---\n");
		if((processes==null) || (processes.isEmpty()))
			buf.append("\tNo Processes\n");
		else {
			for(ProcessReader pr : processes)
				buf.append(pr.toString()+"\n");
		}
		buf.append("\n\n");
		
		return buf.toString();
	}

	/**
	 * This method is a shorter version of the toString method.
	 * @return A string that represent the object.
	 */
	public String toShortString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		
		StringBuffer buf = new StringBuffer("Inside this WorkflowMonitor there are:\n");
		
		buf.append("--- Workflows ---\n");
		if((workflows==null) || (workflows.isEmpty()))
			buf.append("\tNo Workflows\n");
		else {
			for(WorkflowReader wfr : workflows.values()) {
				buf.append("\t"+wfr.getName()
						+" has "+wfr.getActions().size()+" actions and "
						+wfr.getProcesses().size()+" processes \n");
			}
		}
		buf.append("\n");
		
		buf.append("--- Processes ---\n");
		if((processes==null) || (processes.isEmpty()))
			buf.append("\tNo Processes\n");
		else {
			for(ProcessReader pr : processes) {
				buf.append("\t prosses belonging to <"+pr.getWorkflow().getName()
					+"> started at <"+dateFormat.format(pr.getStartTime().getTime())
					+"> has "+pr.getStatus().size()+" action status\n");
			}
		}
		buf.append("\n\n");
		
		return buf.toString();
	}

}
