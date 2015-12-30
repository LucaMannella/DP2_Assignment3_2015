package it.polito.dp2.WF.sol3;

import java.util.Set;

import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.Refreshable;

public class ConcreteWorkflowMonitor implements WorkflowMonitor, Refreshable {
	
	public ConcreteWorkflowMonitor() {
		// TODO Auto-generated constructor stub
		// for taking the URL use this system property = "it.polito.dp2.WF.sol3.URL"
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkflowReader getWorkflow(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<WorkflowReader> getWorkflows() {
		// TODO Auto-generated method stub
		return null;
	}

}
