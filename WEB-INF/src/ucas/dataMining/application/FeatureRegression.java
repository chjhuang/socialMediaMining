package ucas.dataMining.application;

import java.util.Map;

import ucas.dataMining.dataAccess.DataFactory;
import ucas.dataMining.regression.MultipleLinearRegression;

public class FeatureRegression {
	public static double[] getFeatureWeight()
	{
		Map<double[][],double[]> result = DataFactory.getMovieAverageRatingMatrix();
		double[][] featureMatrix = (double[][]) result.keySet().toArray()[0];
		double[] ratings = result.get(featureMatrix);
		MultipleLinearRegression mlr = new MultipleLinearRegression(featureMatrix,ratings);
		String[] features = DataFactory.getMovieFeatures();
		double[] featureWeights = new double[features.length];
		for(int i=0;i< features.length;i++)
		{
			System.out.println("特征："+features[i]+",权重："+mlr.beta(i));
			featureWeights[i] = mlr.beta(i);
		}
		return featureWeights;
	}
	public static void main(String args[])
	{
		double[] weights = getFeatureWeight();
		for(int i=0;i<weights.length;i++)
		{
			System.out.println();
		}
		
	}
}
