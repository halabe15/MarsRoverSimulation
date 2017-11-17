/**
 * Keeps track of the number of rocks returned to the Mothership during a run
 * of the simulation. 
 * 
 * Provides an easy way to count the number of steps taken to
 * collect all rock samples, through the breakExecutionWhenAllRocksCollected() method.
 * A breakpoint should be set in the method's "all rocks collected" conditional, which allows
 * the user to view the step at which the last rock was collected without manually clicking 
 * through the simulation one step at a time. 
 * 
 * 
 * @author Ben Lawton 
 *
 */
public class ExperimentStats {
	
	// Number of rocks taken to the Mothership during a run of the simluation 
	private static int rocksCollected = 0;
	
	// Total number of rocks at the start of the simulation, before the Vehicles collect them
	private final static int NUMBER_OF_ROCKS_IN_FIELD = 300;
	
	/**
	 * @return Number of rocks returned to the mothership 
	 */
	public static int getRocksCollected() {
		return rocksCollected;
	}
	
	/**
	 * Increment the number of rocks returned to the Mothership by 1 
	 */
	public static void incrementRocksCollected() {
		rocksCollected++;
	}
	
	/**
	 * Empty method with no real function other than to provide a breakpoint the debugger
	 * can access to pause execution after the last rock has been collected, making it easier
	 * quickly & easily measure the exact number of steps required 
	 */
	public static void breakExecutionWhenAllRocksCollected() {
		if (rocksCollected == NUMBER_OF_ROCKS_IN_FIELD) {
			// Breakpoint below 
			return;
		} else {
			return;
		}
	}
}
