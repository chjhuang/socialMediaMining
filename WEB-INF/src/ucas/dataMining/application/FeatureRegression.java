package ucas.dataMining.application;

import java.io.IOException;
import java.util.Map;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import ucas.dataMining.dataAccess.DataFactory;
import ucas.dataMining.regression.MultipleLinearRegression;
import ucas.dataMining.util.FileIOUtil;

public class FeatureRegression implements Runnable{
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
	
	public static void saveFeatureWeights(String savePath) throws IOException
	{
		double[] featureWeights = getFeatureWeight();
		String[] features = DataFactory.getMovieFeatures();
		
		JSONArray featuresJsonArray = new JSONArray();
		for(int i=0;i<features.length;i++)
		{
			JSONObject feature = new JSONObject();
			feature.put("name", features[i]);
			feature.put("score", featureWeights[i]);
			featuresJsonArray.add(feature);
		}
		
		FileIOUtil.writeToFile(featuresJsonArray.toJSONString(), savePath);
	}
	public static void main(String args[])
	{
		double[] weights = getFeatureWeight();
		for(int i=0;i<weights.length;i++)
		{
			System.out.println();
		}
		
	}
	
	@Override
	public void run() {
		String savePath = FileIOUtil.rootPath+"\\json\\mutiLinearRegression.json";
		try {
			saveFeatureWeights(savePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Flags.regression = true;
	}
}
