
public class ExperimentStats {
	private static int rocksGathered = 0;
	
	public static int getRocksGathered() {
		return rocksGathered;
	}
	
	public static void incrementRocksGathered() {
		rocksGathered++;
	}
}
