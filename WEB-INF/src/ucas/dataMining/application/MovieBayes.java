package ucas.dataMining.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ucas.dataMining.bayes.naiveBayes;
import ucas.dataMining.dao.KNNUserFeature;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONObject;

public class MovieBayes implements Runnable {

	public String movieId;
	public MovieBayes(String movieId)
	{
		this.movieId = movieId;
	}
	
	public MovieBayes()
	{}
	naiveBayes bayes = new naiveBayes();
	KNNUserFeature user_feature = new KNNUserFeature();

	public void init(String filename) {
		this.user_feature.loadData(filename);
		user_feature.getFeature();
	}
	
	

	public void getClassfiledResult(String movieId, String savePath)
			throws IOException {
		HashMap<Integer, Integer> testSet = user_feature.getTrain_Test(movieId);
		double[][] trainData = user_feature.trainData;
		double[][] testData = user_feature.testData;
		String[] label = user_feature.label;
		HashMap<Integer, Integer> userIdMap = user_feature.userIdMap;
		int N = user_feature.getN();
		// 开始训练
		boolean[] number = new boolean[N];// 记录哪些特征连续true，哪些特征离散false
		number[0] = false;
		for (int i = 1; i < N; i++)
			number[1] = true;
		bayes.train(trainData, label, number);// 训练

		// 开始分类
		for (int i = 0; i < testData.length; i++) {
			int uid = userIdMap.get(i);
			String rate = bayes.classify(testData[i], number);
			testSet.put(uid, Integer.parseInt(rate));
		}
		// 保存每种类别对于的用户链表
		int[] sizeOfLabel = new int[6];
		HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
		for (Entry<Integer, Integer> entry : testSet.entrySet()) {
			int uid = entry.getKey();
			int rate = entry.getValue();
			ArrayList<Integer> lists = result.get(rate);
			if (lists == null)
				lists = new ArrayList<Integer>();
			lists.add(uid);
			result.put(rate, lists);
			sizeOfLabel[rate] = lists.size();
		}
		// 获取元素最多的label
		int maxLabelIndex = 0;
		int maxLabelValue = 0;
		for (int i = 1; i <= 5; i++) {
			System.out.println(i + " " + sizeOfLabel[i]);
			if (sizeOfLabel[i] > maxLabelValue)
				maxLabelValue = sizeOfLabel[maxLabelIndex = i];
		}
		// 将最多的label先保存为JSON对象
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		JSONObject jblabel = new JSONObject();
		jblabel.put("label", "star " + maxLabelIndex);
		ArrayList<JSONObject> jlists = new ArrayList<JSONObject>();
		ArrayList<Integer> lb = result.get(maxLabelIndex);
		for (int j = 0; j < lb.size(); j++) {
			JSONObject temp = new JSONObject();
			temp.put("id", lb.get(j));
			jlists.add(temp);
		}
		jblabel.put("users", jlists);

		jsonList.add(jblabel);
		// 保存其它的label元素
		ArrayList<Integer> labelsAll = new ArrayList<Integer>(result.keySet());
		for (int i = 0; i < labelsAll.size(); i++) {
			int key = labelsAll.get(i);
			if (key == maxLabelIndex)
				continue;
			JSONObject Jlabel = new JSONObject();
			Jlabel.put("label", "star " + key);
			ArrayList<JSONObject> jlist2 = new ArrayList<JSONObject>();
			ArrayList<Integer> l = result.get(key);
			for (int j = 0; j < l.size(); j++) {
				JSONObject temp = new JSONObject();
				temp.put("id", l.get(j));
				jlist2.add(temp);
			}
			Jlabel.put("users", jlist2);
			jsonList.add(Jlabel);
		}
		JSONObject save = new JSONObject();
		save.put("classifications", jsonList);
		System.out.println(save.toJSONString());
		FileIOUtil.writeToFile(save.toJSONString(), savePath);

	}

	public void printArray(double[][] content) {
		int column = content[0].length;
		for (int i = 0; i < content.length; i++) {
			for (int j = 0; j < column; j++)
				System.out.print(content[i][j] + " ");
			System.out.println();
		}
	}

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		MovieBayes mk = new MovieBayes();
		mk.init(".\\uploadFile\\movie_user.json");
		mk.getClassfiledResult("1014", ".\\json\\bayes.json");
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("运行时间：" + time + "ms");

	}

	@Override
	public void run() {
		String savePath = FileIOUtil.rootPath+"\\json\\bayes.json";
		try {
			this.getClassfiledResult(movieId, savePath);
			Flags.nbc = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
