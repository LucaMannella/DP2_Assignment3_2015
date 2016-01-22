package it.polito.dp2.WF.sol3;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.gen.Action;

/**
 * This is an abstract implementation of the interface ActionReader based on JAX-WS framework.<BR>
 * If you want to use that class you have to instantiate one of the following implementation:<BR>
 * {@link SimpleActionR}, {@link ProcessActionR}.
 * 
 * @see {@link ActionReader}, {@link SimpleActionR}, {@link ProcessActionR}
 * @author Luca
 */
public abstract class AbstractActionReader implements ActionReader {
	
	private String name;
	private String role;
	private boolean automInst;
	private WorkflowReader parent;

	public AbstractActionReader(Action action, WorkflowReader workflowReader) {
		this.parent = workflowReader;
		
		if(action == null) return;
		
		this.name = action.getName();
		this.role = action.getRole();
		this.automInst = action.isAutomaticallyInstantiated();
	}
	
	@Override
	public WorkflowReader getEnclosingWorkflow() {
		return this.parent;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getRole() {
		return this.role;
	}

	@Override
	public boolean isAutomaticallyInstantiated() {
		return automInst;
	}
	
	@Override
	public String toString() {
		return "\tAction: "+name+" - Requested Role: "+role+" - Parent workflow: "+parent.getName()+" - AutomInst: "+automInst;
	}

}
