package it.polito.dp2.WF.sol3;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.SimpleActionReader;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.lab3.gen.Action;

/**
 * This is a concrete implementation of abstract class {@link AbstractActionReader} (that implements the interface ActionReader).<BR>
 * Another implementation of that abstract class is {@link ProcessActionR}<br>
 * This implementation is based on JAX-WS framework.
 * 
 * @see {@link ActionReader}, {@link AbstractActionReader}, {@link ProcessActionR}
 * @author Luca
 */
public class SimpleActionR extends AbstractActionReader implements SimpleActionReader {

	private HashMap<String, ActionReader> nextPossibleActions;

	public SimpleActionR(Action action, WorkflowReader workflowReader) {
		super(action, workflowReader);
		
		nextPossibleActions = new HashMap<String, ActionReader>();
	}



	@Override
	public Set<ActionReader> getPossibleNextActions() {
		return new LinkedHashSet<ActionReader>(nextPossibleActions.values());
	}
	

	public void addPossibleNextAction(ActionReader ar) {
		nextPossibleActions.put(ar.getName(), ar);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("NextActions: ");
		for(ActionReader ar : nextPossibleActions.values())
			buf.append(ar.getName());
		
		return super.toString()+"\n\t\t"+buf.toString();
	}

	public void setPossibleNextActions(List<String> nextActionNames, Map<String,ActionReader> actions) {
		for(String actionName : nextActionNames) {
			ActionReader nextAction = actions.get(actionName);
			nextPossibleActions.put(nextAction.getName(), nextAction);
		}
	}

}
