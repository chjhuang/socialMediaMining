package ucas.dataMining.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ucas.dataMining.dao.Movie;
import ucas.dataMining.dao.User;
import ucas.dataMining.dataAccess.DataFactory;


public class KMeansUserFeature {
	
	List<User> users;
    HashMap<String,Movie>movieMap;
    public double [][] dataSet;//特征集合
    public int size;//样本个数
    public int n;//特征个数
    
	public void loadData(String filename){
		users = DataFactory.getAllUsers();
	    List<Movie>movies=DataFactory.getAllMovies();
	    movieMap = new HashMap<String,Movie>();
	    for(Movie m : movies){
	    	movieMap.put(m.getId(), m);
	    }
	}
	
	//返回用户的职业和编号的map
	public HashMap<String,Integer> process_occupation(){
		HashMap<String,Integer> result=new HashMap<String,Integer>();
		int count=0;
		for(int i=0;i<users.size();i++){
			String user_occupation=users.get(i).getOccupation();
			if(result.get(user_occupation)==null)
				result.put(user_occupation, count++);
		}
		System.out.println(result);
		System.out.println(result.size());
		return result;
	}
	//返回电影类别，评分数组的map
	public HashMap<Integer,ArrayList<Integer>> process_movie(User u){
	    HashMap<Integer,ArrayList<Integer>> map=new HashMap<Integer,ArrayList<Integer>>();
	    for(int i=0;i<19;i++)
	    	map.put(i, new ArrayList<Integer>());
	    for(Map.Entry<String,Integer>entry:u.getRatings().entrySet()){
	    	String movieid=entry.getKey();
	    	Movie m=movieMap.get(movieid);
	    	List<Integer>l=m.getTags();
	    	for(int i=0;i<l.size();i++){
	    		if(l.get(i)==1){
	    			ArrayList<Integer>alist=map.get(i);
	    			alist.add(entry.getValue());
	    			map.put(i, alist);
	    		}
	    	}
	    }
	    return map;
	//    for(Entry<Integer,ArrayList<Integer>>entry:map.entrySet()){
	//    	System.out.println(entry.getKey());
	//    	System.out.println(entry.getValue());
	//    }
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
		size=userSize;
		n=63;//63个特征
	    dataSet=new double[size][n];
	
	  //处理职业
		HashMap<String,Integer>occupation_map=process_occupation();
	    for(int i=0;i<userSize;i++){
	    	User u=users.get(i);
	    	//性别特征
	    	if(u.getGender().equals("M"))
	    		dataSet[i][0]=1.0;
	    	else
	    		dataSet[i][0]=0;
	    	//年龄特征
	    	dataSet[i][1]=u.getAge();
	    	//职业特征，处理成0,1变量 
	    	int index = occupation_map.get(u.getOccupation());
	    	dataSet[i][2+index]=1;
	    	int feature_count=2+occupation_map.size();
	    	//电影类别的平均分和方差
	    	HashMap<Integer,ArrayList<Integer>> umap=process_movie(u);
	    	for(Entry<Integer,ArrayList<Integer>>entry:umap.entrySet()){
	    		ArrayList<Integer>score=entry.getValue();
	    		double scoreAverage=getAverage(score,score.size());
	    		double var=getVar(score,score.size(),scoreAverage);
	    		dataSet[i][feature_count++]=scoreAverage;
	    		dataSet[i][feature_count++]=var;
	    	}
	    	//电影的平均分和方差
	    	ArrayList<Integer>scorelist=new ArrayList<Integer>(u.getRatings().values());
	    	double allAver=getAverage(scorelist,scorelist.size());
	    	double var=getVar(scorelist,scorelist.size(),allAver);
	    	dataSet[i][feature_count++]=allAver;
	    	dataSet[i][feature_count++]=var;

	    }
	}
	
	
	public static  void main(String []args){
		KMeansUserFeature uf = new KMeansUserFeature();
        DataFactory.Init();
		uf.getFeature();
		System.out.println(uf.size);
		for(int i=0;i<uf.size;i++){
			for(int j=0;j<uf.n;j++)
				System.out.print(uf.dataSet[i][j]+" ");
		    System.out.println("\n");	
		}
	}


}
