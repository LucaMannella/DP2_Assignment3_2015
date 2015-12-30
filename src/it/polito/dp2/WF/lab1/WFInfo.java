package it.polito.dp2.WF.lab1;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.ActionStatusReader;
import it.polito.dp2.WF.ProcessActionReader;
import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.SimpleActionReader;
import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowMonitorFactory;
import it.polito.dp2.WF.WorkflowReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.List;


public class WFInfo {
	private WorkflowMonitor monitor;
	private DateFormat dateFormat;

	
	/**
	 * Default constructror
	 * @throws WorkflowMonitorException 
	 */
	public WFInfo() throws WorkflowMonitorException {
		WorkflowMonitorFactory factory = WorkflowMonitorFactory.newInstance();
		monitor = factory.newWorkflowMonitor();
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}
	
	public WFInfo(WorkflowMonitor monitor) {
		super();
		this.monitor = monitor;
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WFInfo wf;
		try {
			wf = new WFInfo();
			wf.printAll();
		} catch (WorkflowMonitorException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void printAll() {
		printWorkflows();
		printProcesses();
	}


	private void printProcesses() {

		// Get the list of processes
		Set<ProcessReader> set = monitor.getProcesses();
		
		/* Print the header of the table */
		System.out.println("#");
		System.out.println("#Number of Processes: "+set.size());
		System.out.println("#");
		String header = new String("#List of processes:");
		printHeader(header);
		
		// For each process print related data
		for (ProcessReader wfr: set) {
			System.out.println("Process started at " + 
								dateFormat.format(wfr.getStartTime().getTime()) +
					            " "+"- Workflow " + wfr.getWorkflow().getName());
			System.out.println("Status:");
			List<ActionStatusReader> statusSet = wfr.getStatus();
			printHeader("Action Name\tTaken in charge by\tTerminated");
			for (ActionStatusReader asr : statusSet) {
				System.out.print(asr.getActionName()+"\t");
				if (asr.isTakenInCharge())
					System.out.print(asr.getActor().getName()+"\t\t");
				else
					System.out.print("-"+"\t\t\t");
				if (asr.isTerminated())
					System.out.println(dateFormat.format(asr.getTerminationTime().getTime()));
				else
					System.out.println("-");
			}
			System.out.println("#");
		}
		System.out.println("#End of Processes");
		System.out.println("#");
	}


	private void printWorkflows() {
		// Get the list of workflows
		Set<WorkflowReader> set = monitor.getWorkflows();
		
		/* Print the header of the table */
		System.out.println("#");
		System.out.println("#Number of Workflows: "+set.size());
		System.out.println("#");
		String header = new String("#List of workflows:");
		printHeader(header);	
		
		// For each workflow print related data
		for (WorkflowReader wfr: set) {
			System.out.println();
			System.out.println("Data for Workflow " + wfr.getName());
			System.out.println();

			// Print actions
			System.out.println("Actions:");
			Set<ActionReader> setAct = wfr.getActions();
			printHeader("Action Name\tRole\t\tAutom.Inst.\tSimple/Process\tWorkflow\tNext Possible Actions");
			for (ActionReader ar: setAct) {
				System.out.print(ar.getName()+"\t"+ar.getRole()+"\t"+ar.isAutomaticallyInstantiated()+"\t");
				if (ar instanceof SimpleActionReader) {
					System.out.print("\tSimple\t\t"+"-\t\t");
					// Print next actions
					Set<ActionReader> setNxt = ((SimpleActionReader)ar).getPossibleNextActions();
					for (ActionReader nAct: setNxt)
						System.out.print(nAct.getName()+" ");
					System.out.println();
				}
				else if (ar instanceof ProcessActionReader) {
					System.out.print("\tProcess\t\t");
					// print workflow
					System.out.println(((ProcessActionReader)ar).getActionWorkflow().getName());
				}
			}
			System.out.println("#");
		}	
		System.out.println("#End of Workflows");
		System.out.println("#");
	}

	private void printHeader(String header) {
		StringBuffer line = new StringBuffer(132);
		
		for (int i = 0; i < 132; ++i) {
			line.append('-');
		}
		
		System.out.println(header);
		System.out.println(line);
	}
}
