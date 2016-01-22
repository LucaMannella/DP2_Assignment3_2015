package it.polito.dp2.WF.sol3;

import java.util.Map;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.ProcessActionReader;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.gen.Action;

/**
 * This is a concrete implementation of abstract class {@link AbstractActionReader} (that implements the interface ActionReader).<BR>
 * Another implementation of that abstract class is {@link SimpleActionR}<br>
 * This implementation is based on JAX-WS framework.
 *
 * @see {@link ActionReader}, {@link AbstractActionReader}, {@link SimpleActionR}
 * @author Luca
 */
public class ProcessActionR extends AbstractActionReader implements ProcessActionReader {

	private String workflowName;
	private WorkflowReader nextWorkflow;

	public ProcessActionR(Action action, WorkflowReader workflowReader) {
		super(action, workflowReader);
		
		if(action == null) return;	// safety lock
		
		workflowName = action.getWorkflow();
	}

	@Override
	public WorkflowReader getActionWorkflow() {
		return this.nextWorkflow;
	}
	
	@Override
	public String toString() {
		return super.toString()+"\n\t\tNextWorkflow: "+nextWorkflow.getName();
	}

	public void setNextWorkflow(Map<String,WorkflowReader> workflows) {
		nextWorkflow = workflows.get(workflowName);
	}

}
