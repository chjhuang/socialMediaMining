package ucas.dataMining.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ucas.dataMining.dao.KNNUserFeature;
import ucas.dataMining.knn.KnnClassfied;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MovieKnn {

KnnClassfied knn=new KnnClassfied();
KNNUserFeature user_feature=new KNNUserFeature();

public void init(String filename){
	this.user_feature.loadData(filename);
	user_feature.getFeature();
}

public void getClassfiledResult(String movieId,String savePath) throws IOException{
	HashMap<Integer,Integer>testSet=user_feature.getTrain_Test(movieId);
	double [][]trainData=user_feature.trainData;
	double [][]testData=user_feature.testData;
	String []label=user_feature.label;
	HashMap<Integer,Integer> userIdMap=user_feature.userIdMap;
	//训练集归一化
	double [][]autoNormTrain=user_feature.autoNorm(trainData);
	//开始分类
	for(int i=0;i<testData.length;i++){
		int uid=userIdMap.get(i);
		double [] autoNormTest=user_feature.autoNorm(testData[i]);
		String rate=knn.classify(autoNormTest, autoNormTrain, label, 10);//测试集归一化
		testSet.put(uid, Integer.parseInt(rate));
	}
	//保存每种类别对于的用户链表
	int []sizeOfLabel=new int[6];
	HashMap<Integer,ArrayList<Integer>>result=new HashMap<Integer,ArrayList<Integer>>();
	for(Entry<Integer,Integer>entry:testSet.entrySet()){
		int uid=entry.getKey();
		int rate=entry.getValue();
		ArrayList<Integer>lists=result.get(rate);
		if(lists==null)
			lists=new ArrayList<Integer>();
		lists.add(uid);
		result.put(rate, lists);
		sizeOfLabel[rate]=lists.size();
	}
	//获取元素最多的label
	int maxLabelIndex=0;int maxLabelValue=0;
	for(int i=1;i<=5;i++){
		if(sizeOfLabel[i]>maxLabelValue)
			maxLabelValue=sizeOfLabel[maxLabelIndex=i];
	System.out.println(i+" "+sizeOfLabel[i]);	
	}
	//将最多的label先保存为JSON对象
	JSONArray jsonList=new JSONArray();
	JSONArray jsonBigest=new JSONArray();
	JSONObject jblabel=new JSONObject();
	jblabel.put("label", "star "+maxLabelIndex);
	jsonBigest.add(jblabel);
	ArrayList<JSONObject> jlists=new ArrayList<JSONObject>();
	ArrayList<Integer> lb=result.get(maxLabelIndex);
	for(int j=0;j<lb.size();j++){
		JSONObject temp=new JSONObject();
		temp.put("id", lb.get(j));
		jlists.add(temp);
	}
	JSONObject userJsons=new JSONObject();
	userJsons.put("users",jlists);
	jsonBigest.add(userJsons);
	jsonList.add(jsonBigest);
	//保存其它的label元素
	ArrayList<Integer>labelsAll=new ArrayList<Integer>(result.keySet());
	for(int i=0;i<labelsAll.size();i++){
		int key=labelsAll.get(i);
		if(key==maxLabelIndex)
			continue;
		JSONObject Jlabel=new JSONObject();
		Jlabel.put("label", "star "+key);
		JSONArray jlist=new JSONArray();
		jlist.add(Jlabel);
		ArrayList<JSONObject> jlist2=new ArrayList<JSONObject>();
		ArrayList<Integer> l=result.get(key);
		for(int j=0;j<l.size();j++){
			JSONObject temp=new JSONObject();
			temp.put("id", l.get(j));
			jlist2.add(temp);
		}
		JSONObject userJson=new JSONObject();
		userJson.put("users",jlist2);
		jlist.add(userJson);
		jsonList.add(jlist);
	}
	JSONObject save=new JSONObject();
	save.put("classifications", jsonList);
	System.out.println(save.toJSONString());
	FileIOUtil.writeToFile(save.toJSONString(),savePath);
	
	
	
	
}
public void printArray(double [][]content){
	int column=content[0].length;
	for(int i=0;i<content.length;i++){
		for(int j=0;j<column;j++)
			System.out.print(content[i][j]+" ");
		System.out.println();
	}
}
public static void main(String []args)throws IOException{
	long start = System.currentTimeMillis();
	MovieKnn mk=new MovieKnn();
	mk.init(".\\json\\movie_user.json");
//	mk.getClassfiledResult("1014");
	long end = System.currentTimeMillis();
	long time =end-start;
	System.out.println("运行时间："+time+"ms");

	
}
}
