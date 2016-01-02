package it.polito.dp2.WF.sol3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.gen.Action;
import it.polito.dp2.WF.lab3.gen.Workflow;
import it.polito.dp2.WF.sol3.ProcessActionR;
import it.polito.dp2.WF.sol3.SimpleActionR;

/**
 * This is a concrete implementation of the interface WorkflowReader based on the JAX-WS framework.<BR><BR>
 * If you want more detail about the interface look to {@link it.polito.dp2.WF.WorkflowReader}
 * 
 * @author Luca
 */
public class ConcreteWorkflowReader implements WorkflowReader, Comparable<WorkflowReader> {

	private String name;
	private Map<String, ActionReader> actionReaders;
	private Set<ProcessReader> processes;
	
	public ConcreteWorkflowReader(Workflow workflow) {
		this.actionReaders = new HashMap<String, ActionReader>();
		this.processes = new HashSet<ProcessReader>();		//it must remains empty
		
		this.name = workflow.getName();
		
		for( Action action : workflow.getAction() ){
			ActionReader ar;
			
			if( (action.getWorkflow() != null) && (! action.getWorkflow().equals("") ) )
				ar = new ProcessActionR(action, this);
			else
				ar = new SimpleActionR(action, this);
			
			actionReaders.put(ar.getName(), ar);
		}
		
		for( Action action : workflow.getAction() ){
			ActionReader actReader = actionReaders.get(action.getName());
			
			if(actReader instanceof SimpleActionR) {
				List<String> nextActions = action.getNextAction();
				
				SimpleActionR sar = (SimpleActionR)actReader;
				sar.setPossibleNextActions(nextActions, actionReaders);
			}
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Set<ActionReader> getActions() {
		return new TreeSet<ActionReader>(actionReaders.values());
	}

	@Override
	public ActionReader getAction(String actionName) {
		return actionReaders.get(actionName);
	}

	@Override
	public Set<ProcessReader> getProcesses() {
		return processes;
	}

	@Override
	public int compareTo(WorkflowReader o) {
		return this.name.compareTo(o.getName());
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("Workflow: "+name+"\n");
		
		buf.append("Actions:\n");
		for(ActionReader ar : actionReaders.values()) {
			buf.append("\t"+ar.toString()+"\n");
		}
		buf.append("Processes:\n");
		for(ProcessReader pr : processes) {
			buf.append("\t"+pr.toString()+"\n");
		}
		
		return buf.toString();
	}
	
	public void setWfsInsideProcessActions(Map<String, WorkflowReader> workflows) {
		for( ActionReader actReader : actionReaders.values() ) {
			
			if(actReader instanceof ProcessActionR) {
				ProcessActionR par = (ProcessActionR)actReader;
				par.setNextWorkflow(workflows);
			}
		}
	}

}
