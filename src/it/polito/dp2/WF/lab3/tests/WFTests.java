package it.polito.dp2.WF.lab3.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.WF.*;
import it.polito.dp2.WF.lab3.Refreshable;
import it.polito.dp2.WF.lab3.client.ToggleWorkflowInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class WFTests {
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    
    class ActionReaderComparator implements Comparator<ActionReader> {
        public int compare(ActionReader f0, ActionReader f1) {
        	return f0.getName().compareTo(f1.getName());
        }
    }
    
    class WorkflowReaderComparator implements Comparator<WorkflowReader> {
        public int compare(WorkflowReader f0, WorkflowReader f1) {
        	return f0.getName().compareTo(f1.getName());
        }
    }
    
    class ProcessReaderComparator implements Comparator<ProcessReader> {
        public int compare(ProcessReader f0, ProcessReader f1) {
        	return f0.getStartTime().compareTo(f1.getStartTime());
        }
    }

    
	private static WorkflowMonitor referenceWorkflowMonitor;	// reference data generator
	private static WorkflowMonitor testWorkflowMonitor;			// implementation under test
	private static long testcase;
	
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	// Create reference data generator
        System.setProperty("it.polito.dp2.WF.WorkflowMonitorFactory", "it.polito.dp2.WF.Random.WorkflowMonitorFactoryImpl");

        referenceWorkflowMonitor = WorkflowMonitorFactory.newInstance().newWorkflowMonitor();

        // Create implementation under test
        System.setProperty("it.polito.dp2.WF.WorkflowMonitorFactory", "it.polito.dp2.WF.sol3.WorkflowMonitorFactory");

        testWorkflowMonitor = WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
        
        // read testcase property
        Long testcaseObj = Long.getLong("it.polito.dp2.WF.Random.testcase");
        if (testcaseObj == null)
        	testcase = 0;
        else
        	testcase = testcaseObj.longValue();
    }
    
    @Before
    public void setUp() throws Exception {
        assertNotNull("Internal tester error during test setup: null reference", referenceWorkflowMonitor);
        assertNotNull("Could not run tests: the implementation under test generated a null WorkflowMonitor", testWorkflowMonitor);
    }

	// method for comparing two non-null strings    
	private void compareString(String rs, String ts, String meaning) {
		assertNotNull("NULL "+meaning, ts);
        assertEquals("Wrong "+meaning, rs, ts);		
	}
	
    @Test
    public final void testGetWorkflows() {
    		// call getWorkflows on the two implementations
			Set<WorkflowReader> rs = referenceWorkflowMonitor.getWorkflows();
			Set<WorkflowReader> ts = testWorkflowMonitor.getWorkflows();
			
			// if one of the two calls returned null while the other didn't return null, the test fails
	        if ((rs == null) && (ts != null) || (rs != null) && (ts == null)) {
	            fail("getWorkflows returns null when it should return non-null or vice versa");
	            return;
	        }

	        // if both calls returned null, there are no workflows, and the test passes
	        if ((rs == null) && (ts == null)) {
	            assertTrue("There are no Workflows!", true);
	            return;
	        }
	        
	        // check that the number of workflows matches
	        assertEquals("Wrong Number of Workflows", rs.size(), ts.size());
	        
	        // create treesets of workflows, using the comparator for sorting, one for reference and one for impl. under test 
	        TreeSet<WorkflowReader> rts = new TreeSet<WorkflowReader>(new WorkflowReaderComparator());
	        TreeSet<WorkflowReader> tts = new TreeSet<WorkflowReader>(new WorkflowReaderComparator());
	    
	        rts.addAll(rs);
	        tts.addAll(ts);
	        
	        // check that all workflows match one by one
	        Iterator<WorkflowReader> ri = rts.iterator();
	        Iterator<WorkflowReader> ti = tts.iterator();

	        while (ri.hasNext() && ti.hasNext()) {
	        	compareWorkflowReader(ri.next(),ti.next());
	        }
    }

    // private method for comparing two non-null WorkflowReader objects
	private void compareWorkflowReader(WorkflowReader rwr, WorkflowReader twr) {
		// check the WorkflowReaders are not null
		assertNotNull("Internal tester error: null workflow reader", rwr);
        assertNotNull("Unexpected null workflow reader", twr);
        System.out.println("Comparing workflow "+rwr.getName());

        // check the WorkflowReaders return the same data
        compareString(rwr.getName(), twr.getName(), "workflow name");
	}
	
    @Test
    public final void testRefresh() {
    	// check Refreshable has been implemented
    	assertTrue("The Refreshable interface is not implemented by the WorkflowMonitor under test",
    			testWorkflowMonitor instanceof Refreshable);
    	Refreshable refreshable = (Refreshable) testWorkflowMonitor;
    	
    	// check test and reference implementations give the same value for isAutomaticallyInstantiated of action Writing in ArticleProduction
    	WorkflowReader wfr = referenceWorkflowMonitor.getWorkflow("ArticleProduction");
    	WorkflowReader wft = testWorkflowMonitor.getWorkflow("ArticleProduction");
    	assertNotNull("Nothing to test", wfr); // no ArticleProduction
    	assertNotNull("Test is not possible because of another error (no ArticleProduction)", wft);
    	ActionReader ar = wfr.getAction("Writing");
    	ActionReader at = wft.getAction("Writing");
    	assertNotNull("Test is not possible because of another error (no Writing action)", at);
    	if (ar.isAutomaticallyInstantiated() != at.isAutomaticallyInstantiated())
    		fail("Test is not possible because of another error (wrong isAutomaticallyInstantiated for Writing action)");
    	
    	try {
	    	// change value in server
	    	ToggleWorkflowInfo t = new ToggleWorkflowInfo();
	    	t.run();
	    	
	    	// call refresh in client
	    	refreshable.refresh();
	    	
	    	// check value has changed in client
	    	wft = testWorkflowMonitor.getWorkflow("ArticleProduction");
	    	at = wft.getAction("Writing");
	    	assertTrue("Refresh did not change value of isAutomaticallyInstantiated as expected", ar.isAutomaticallyInstantiated() != at.isAutomaticallyInstantiated());
	    	
	    	// reset value in server
	    	t.run();
	    	
	    	// call refresh in client
	    	refreshable.refresh();
	
	    	// check value has changed again in client
	    	wft = testWorkflowMonitor.getWorkflow("ArticleProduction");
	    	at = wft.getAction("Writing");
	    	assertTrue("Refresh did not change value of isAutomaticallyInstantiated as expected", ar.isAutomaticallyInstantiated() == at.isAutomaticallyInstantiated());
    	} catch (Exception e) {
    		fail("Unexpected exception when running the test: " + e.getMessage());
    	}
    }
}
