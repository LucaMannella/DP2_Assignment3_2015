package it.polito.dp2.WF.sol3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import com.oracle.xmlns.internal.webservices.jaxws_databinding.ObjectFactory;

import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.Refreshable;
import it.polito.dp2.WF.lab3.gen.UnknownNames_Exception;
import it.polito.dp2.WF.lab3.gen.Workflow;
import it.polito.dp2.WF.lab3.gen.WorkflowInfo;
import it.polito.dp2.WF.lab3.gen.WorkflowInfoService;

/**
 * This is a concrete implementation of the interface WorkflowMonitor based on the JAXB framework.<BR><BR>
 * If you want more detail about the interface look to {@link it.polito.dp2.WF.WorkflowMonitor}
 * 
 * @author Luca
 */
public class ConcreteWorkflowMonitor implements WorkflowMonitor, Refreshable {
	
	private Map<String, WorkflowReader> workflows;
	private Set<ProcessReader> processes;
	
	public ConcreteWorkflowMonitor() throws MalformedURLException, WorkflowMonitorException {		// TODO ConcreteWorkflowMonitor Constructor
		workflows = new HashMap<String, WorkflowReader>();
		processes = new HashSet<ProcessReader>();		// it must remains empty
		
		// taking the URL of the Web Service
		String webServiceString = System.getProperty("it.polito.dp2.WF.sol3.URL");
		URL webServiceURL = new URL(webServiceString);
		
		// taking the port (proxy) from the service
		WorkflowInfoService service = new WorkflowInfoService(webServiceURL);
		WorkflowInfo proxy = service.getWorkflowInfoPort();
		
		System.out.println("Retrieving the names of the workflows...");
		Holder<XMLGregorianCalendar> calendarHolder = new Holder<XMLGregorianCalendar>();
		Holder<List<String>> workflowNamesHolder = new Holder<List<String>>();
		proxy.getWorkflowNames(calendarHolder, workflowNamesHolder);
		
		Holder<List<Workflow>> workflowsHolder = new Holder<List<Workflow>>();
		try {
			System.out.println("Retrieving the workflows...");
			proxy.getWorkflows(workflowNamesHolder.value, calendarHolder, workflowsHolder);
		} catch (UnknownNames_Exception e) {
			throw new WorkflowMonitorException("Error retrieving the workflows: "+e.getMessage());
		}
		
		//build the WorkflowReaderSet
		for( Workflow wf: workflowsHolder.value ) {
			WorkflowReader wfr = new ConcreteWorkflowReader(wf);
			workflows.put(wfr.getName(), wfr);
		}
		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		/* 
		 * This interface includes the refresh() method, 
		 * which must align the local information about workflows in the client 
		 * with the information currently provided by the service. 
		 */
		// -------------------------------
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
		return new TreeSet<WorkflowReader>(workflows.values());
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer("Inside this WorkflowMonitor there are:\n");
		
		if((workflows==null) || (workflows.isEmpty()))
			buf.append("\tNo Workflows\n");
		else {
			for(WorkflowReader wfr : workflows.values())
				buf.append("\t"+wfr.toString()+"\n");
		}
				
		return buf.toString();
	}

}
