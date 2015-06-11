package ucas.dataMining.application;


public class Flags {
	public static boolean networkBuilt = false;
	public static boolean decisionTree = false;
	public static boolean nbc = false;
	public static boolean knn = false;
	public static boolean kmeans = false;
	public static boolean regression = false;
	public static void reset()
	{
		networkBuilt = false;
		decisionTree = false;
		nbc = false;
		knn = false;
		kmeans = false;
		regression = false;
	}
}
