package ucas.dataMining.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ucas.dataMining.dataAccess.DataFactory;

public class KNNUserFeature {
	
	List<User> users;
	HashMap<String,Movie>movieMap;
    double [][] dataSet;//数据集
    int size;//样本个数
    int n;//特征维数
    private static Graph userGraph=new Graph();
    public double[]max;
	public double[]min;
	public double[][]trainData;//训练集
	public double[][]testData;//测试集
	public String []label;
	public HashMap<Integer,Integer> userIdMap;
//初始化一些数据
public void loadData(String filename){
	DataFactory.Init();
	users=DataFactory.getAllUsers();
    List<Movie>movies=DataFactory.getAllMovies();
    movieMap=new HashMap<String,Movie>();
    for(Movie m:movies){
    	movieMap.put(m.getId(), m);
    	
    }
	List<SimilarUser> su=DataFactory.getSimilarUser();
	System.out.println("相似用户个数为:"+su.size());
	userGraph.init(su); 
}




/*
 *****************获得alist的前size个数的平均数************
 */
    double getAverage(ArrayList<Integer>alist,int size){
        if(size==0)
        	return 0;
    	int sum=0;
    	for(int i=0;i<size;i++)
    		sum+=alist.get(i);
    	return sum/size;
    }

//获取前size个数的方差
    double getVar(ArrayList<Integer>alist,int size,double aver){
    	if(size<=1)
    		return 0;
    	double sum=0.0;
    	for(int i=0;i<size;i++)
    		sum+=((double)alist.get(i)-aver)*((double)alist.get(i)-aver);
    	return sum/(size-1);
    }
    
//获取用户特征
public void getFeature(){
	int userSize=users.size();
    dataSet=new double[userSize][65];
    size=userSize;
    n=6;
  //处理职业
    for(int i=0;i<size;i++){
    	User u=users.get(i);
    	int userId=Integer.parseInt(u.getId());
    	//性别特征
    	if(u.getGender().equals("M"))
    		dataSet[userId][0]=1.0;
    	else
    		dataSet[userId][0]=0;
    	//年龄特征
    	dataSet[userId][1]=u.getAge();
    	//电影的平均分和方差
    	ArrayList<Integer>scorelist=new ArrayList<Integer>(u.getRatings().values());
    	double allAver=getAverage(scorelist,scorelist.size());
    	double var=getVar(scorelist,scorelist.size(),allAver);
    	dataSet[userId][2]=allAver;
    	dataSet[userId][3]=var;
    //	System.out.println(feature_count);
    }
}
//训练集归一化
public double[][] autoNorm(double data[][]){
	int column=data[0].length;
	max=new double[column];
	min=new double[column];
	double [][]result=new double[data.length][column];
	for(int i=0;i<column;i++){
		max[i]=-1;
		min[i]=655350;		
	}
	for(int i=0;i<data.length;i++)
		for(int j=0;j<column;j++){
			if(data[i][j]<min[j])
				min[j]=data[i][j];
			if(data[i][j]>max[j])
				max[j]=data[i][j];
		}

	for(int i=0;i<data.length;i++)
		for(int j=0;j<column;j++){
			result[i][j]=(double)(data[i][j]-min[j])/(max[j]-min[j]);
		}
	return result;		
}

public HashMap<Integer,Integer> getTrain_Test(String movieId){
	HashMap<Integer,Integer>testSet=new HashMap<Integer,Integer>();
	//筛选已经评过分的用户
	for(User u:users)
		if(u.getRatings().containsKey(movieId))
			testSet.put(Integer.parseInt(u.getId()), u.getRatings().get(movieId));
	int N=n;
	//补充网络特征
	double [] aut=new double[dataSet.length];
	for(Entry<Integer,Integer> entry:testSet.entrySet()){
		int t=entry.getKey();
		int []dist=userGraph.getDist(t);
		for(int j=0;j<dataSet.length;j++){
			if(dist[j]!=Graph.MAXINT&&dist[j]!=0){
				dataSet[j][N-2]+=1;
			    dataSet[j][N-1]+=((double)entry.getValue()/(dist[j]*dist[j]));
			    aut[j]+=(double)1/(dist[j]*dist[j]);
			}
		}
	}
	for(int j=0;j<dataSet.length;j++)
		if(dataSet[j][N-2]!=0)
			dataSet[j][N-1]/=aut[j];
	//新建训练集
	trainData=new double[testSet.size()][N];
	label=new String[testSet.size()];
	//新建测试集
	testData=new double[users.size()-testSet.size()][N];
	//划分训练集和测试集
	int counts1,counts2;
	counts1=counts2=0;
	userIdMap=new HashMap<Integer,Integer>();
	
	for(int i=0;i<dataSet.length;i++)
		if(testSet.get(i)!=null){
			int score=testSet.get(i);
			for(int j=0;j<N;j++)
				trainData[counts1][j]=dataSet[i][j];
			label[counts1]=score+"";
			counts1++;
		}else{
			for(int j=0;j<N;j++)
				testData[counts2][j]=dataSet[i][j];
			userIdMap.put(counts2, i);
			counts2++;
		}
	return testSet;
}
//测试集归一化
public double[] autoNorm(double[]data){
	int column=data.length;
	double []result=new double[column];
	for(int i=0;i<column;i++)
		result[i]=(double)(data[i]-min[i])/(max[i]-min[i]);
	return result;
}

public List<User> getUsers() {
		return users;
	}

public int getN(){
	return this.n;
}
public double[][] getDataSet(){
	return this.dataSet;
}

public Graph getUserGraph() {
	return userGraph;
}

public static  void main(String []args){
	KNNUserFeature uf=new KNNUserFeature();
	uf.loadData("movie_user.json");
	uf.getFeature();

	System.out.println(uf.users.size());
}


}
