package it.polito.dp2.WF.sol3;

import java.util.HashMap;

import it.polito.dp2.WF.ProcessActionReader;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.gen.Action;

/**
 * This is a concrete implementation of abstract class AbstractActionReader (that implements the interface ActionReader).<BR>
 * Another implementation of that abstract class is {@link it.polito.dp2.WF.sol3.SimpleActionR}<br>
 * This implementation is based on JAX-WS framework.<BR><BR>
 * If you want more detail about the interface look to<BR>
 * {@link it.polito.dp2.WF.sol3.AbstractActionReader}<BR>
 * {@link it.polito.dp2.WF.ActionReader}
 * 
 * @author Luca
 */
public class ProcessActionR extends AbstractActionReader implements ProcessActionReader {

	private String workflowName;
	private WorkflowReader nextWorkflow;

	public ProcessActionR(Action action, WorkflowReader workflowReader) {
		super(action, workflowReader);
//TODO: if(action == null)	return;
		
		//TODO: auto-generated constructor stub	
	}

	@Override
	public WorkflowReader getActionWorkflow() {
		return this.nextWorkflow;
	}
	
	@Override
	public String toString() {
		return super.toString()+"\n\t\tNextWorkflow: "+nextWorkflow.getName();
	}

	public void setNextWorkflow(HashMap<String,WorkflowReader> workflows) {
		nextWorkflow = workflows.get(workflowName);
	}

}
