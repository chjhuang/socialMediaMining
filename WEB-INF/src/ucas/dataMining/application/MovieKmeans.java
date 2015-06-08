package ucas.dataMining.application;

import java.io.IOException;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Random;  

import ucas.dataMining.kmeans.KMeansUserFeature;

import com.alibaba.fastjson.JSON;
  
public class MovieKmeans {  
      
    public static List<ArrayList<ArrayList<Double>>>   
    initHelpCenterList(List<ArrayList<ArrayList<Double>>> CenterList,int k){  
        for(int i=0;i<k;i++){  
            CenterList.add(new ArrayList<ArrayList<Double>>());  
        }     
        return CenterList;  
    }  
    
    public static ArrayList<ArrayList<Integer>>   
    initHelpResultList(ArrayList<ArrayList<Integer>> resultlist,int k){  
        for(int i=0;i<k;i++){  
        	resultlist.add(new ArrayList<Integer>());  
        }     
        return resultlist;  
    }  
    
     
    //计算余弦相似度
    public static double cos(ArrayList<Double> arrayList,ArrayList<Double> arrayList2){
    	double dis = 0.0;
    	int len = arrayList.size();
    	double fenzi_sum = 0.0;
    	double fenmu_a = 0.0;
    	double fenmu_b = 0.0;
    	for (int i=0; i<len; i++){
    		fenzi_sum += arrayList.get(i)*arrayList2.get(i);
    		fenmu_a += Math.pow(arrayList.get(i), 2);
    		fenmu_b += Math.pow(arrayList2.get(i), 2);
    	}
    	dis = fenzi_sum/(Math.sqrt(fenmu_a)*Math.sqrt(fenmu_b));
    	return dis;
    }
    
    
    public static ArrayList<ArrayList<Integer>> kmeans_njz(List<ArrayList<Double>> dataList, int k){
    	
    	int data_len = dataList.size();
    	int instance_len = dataList.get(0).size();
    	List<ArrayList<ArrayList<Double>>> CenterList = new ArrayList<ArrayList<ArrayList<Double>>>();  
    	ArrayList<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();
        
    	//随机确定K个初始聚类中心
        ArrayList<ArrayList<Double>> min_max_per_word = new ArrayList<ArrayList<Double>>();
        for(int i=0; i<instance_len; i++){
        	double max = dataList.get(0).get(i);
        	double min = dataList.get(0).get(i);
        	for(int j=0; j<data_len; j++){
        		if (dataList.get(j).get(i)>max)
        			max = dataList.get(j).get(i);
        		else if (dataList.get(j).get(i)<min)
        			min = dataList.get(j).get(i);
        	}
        	ArrayList<Double> curInstence = new ArrayList<Double>();
        	curInstence.add(min);
        	curInstence.add(max);
        	min_max_per_word.add(curInstence);
        }
        
        Random rd = new Random();  
    	List<ArrayList<Double>> centers = new ArrayList<ArrayList<Double>>();  
        for(int i=0; i<k; i++){
//        	ArrayList<Double> center = new ArrayList<Double>();
//            for(int j=0; j<instance_len; j++){
//            	double min = min_max_per_word.get(j).get(0);
//            	double max = min_max_per_word.get(j).get(1);
//            	double instance_random = rd.nextDouble()*(max-min) + min;
//            	center.add(instance_random);
//            }
//            centers.add(center);
        	int Random_center_index = rd.nextInt(data_len);
        	centers.add(dataList.get(Random_center_index));
            CenterList.add( new ArrayList<ArrayList<Double>>() );
            resultList.add(new ArrayList<Integer>());
        }
        
        //输出k个初始中心  
        System.out.println("original centers:");  
        for(int i=0; i<k; i++)  
            System.out.println(centers.get(i));  
        
        //进行若干次迭代，直到聚类中心稳定
        while(true){  
        	
            for(int i=0; i<dataList.size(); i++){ 
                double maxDistance = 0;  
                int centerIndex = -1;  
                for(int j=0; j<k; j++){//离0~k之间哪个中心最近  
                    double currentDistance = 0;  
                    currentDistance =  cos(centers.get(j), dataList.get(i));   
                    if(maxDistance < currentDistance){  
                        maxDistance = currentDistance;  
                        centerIndex = j;  
                    }
                }  
//                System.out.println(centerIndex);
                resultList.get(centerIndex).add(i);
                CenterList.get(centerIndex).add(dataList.get(i));  
            }  
             
            List<ArrayList<Double>> newCenters = new ArrayList<ArrayList<Double>>();  
            //计算新的k个聚类中心  
            for(int i=0; i<k; i++){  
                ArrayList<Double> tmp = new ArrayList<Double>();  
                for(int j=0; j<instance_len; j++){  
                    double sum = 0;  
                    for(int t=0; t<CenterList.get(i).size(); t++)  
                        sum += CenterList.get(i).get(t).get(j);  
                    tmp.add(sum/CenterList.get(i).size());  
                } 
                newCenters.add(tmp);  
            }  
            System.out.println("\nnew clusters' centers:\n");  
            for(int i=0;i<k;i++)  
                System.out.println(newCenters.get(i));  
            
            //当新旧中心之间距离---小于阈值时，聚类算法结束  
            double distance = 0;  
            for(int i=0;i<k;i++)
                distance += cos(centers.get(i),newCenters.get(i));
                
            System.out.println("\ndistance: "+distance+"\n\n");
            
            if(distance==k)//小于阈值时，结束循环  
                break;  
            else{
            	//否则，新的中心来代替旧的中心，进行下一轮迭代  
                centers = new ArrayList<ArrayList<Double>>(newCenters);  
                newCenters = new ArrayList<ArrayList<Double>>();  
                resultList = new ArrayList<ArrayList<Integer>>();
                CenterList = new ArrayList<ArrayList<ArrayList<Double>>>();  
                CenterList = initHelpCenterList(CenterList,k);  
                resultList = initHelpResultList(resultList,k);
            }  
        }//end while
        
        //输出最后聚类结果  
//        for(int i=0;i<k;i++){  
//            System.out.println("\n\nCluster: "+(i+1)+"   size: "+CenterList.get(i).size()+" :\n\n");  
//            for(int j=0;j<CenterList.get(i).size();j++){  
//                System.out.println(CenterList.get(i).get(j));  
//            }  
//        } 
        
    	//输出聚类的user结果
//        for(int i=0;i<k;i++){  
//            System.out.println("\n\nCluster: "+(i+1)+"   size: "+resultList.get(i).size()+" :\n\n");  
//            for(int j=0; j<resultList.get(i).size(); j++){
//            	System.out.print(resultList.get(i).get(j)+" ");  
//            }
//        } 
        return resultList;
    }//end kmeans_njz
    
//    public ArrayList<ArrayList<Integer>> getKmeans(int k){
    public static String getKmeans(int k){
    	
    	 List<ArrayList<Double>> dataList = new ArrayList<ArrayList<Double>>();  
         KMeansUserFeature uf = new KMeansUserFeature();
         //String dir = "/Users/apple/Desktop/njz_python/clusterForMovieUser/src/clusterForMovieUser";
 		uf.loadData("./data/movie_user.json");
 		System.out.println(uf.size); 
 		uf.getFeature();
 		System.out.println(uf.size); 
 		for(int i=0;i<uf.size;i++){
 			List<Double> tmpList = new ArrayList<Double>();  
 			for(int j=0;j<uf.n;j++){
 				tmpList.add(uf.dataSet[i][j]);  
 			}
 	         dataList.add((ArrayList<Double>) tmpList);  
 		}
 		ArrayList<ArrayList<Integer>> kmeans_result = new ArrayList<ArrayList<Integer>>();
        kmeans_result = kmeans_njz(dataList,k);
        String result = JSON.toJSONString(kmeans_result);
        return result;
		
	}
    
    public static void main(String[] args) throws IOException{  
        //获取特征进行聚类---943user
        List<ArrayList<Double>> dataList = new ArrayList<ArrayList<Double>>();  
        KMeansUserFeature uf = new KMeansUserFeature();
        String dir = "/Users/apple/Desktop/njz_python/clusterForMovieUser/src/clusterForMovieUser";
		uf.loadData(dir+"/movie_user.json");
		uf.getFeature();
		for(int i=0;i<uf.size;i++){
			List<Double> tmpList = new ArrayList<Double>();  
			for(int j=0;j<uf.n;j++){
				tmpList.add(uf.dataSet[i][j]);  
			}
	         dataList.add((ArrayList<Double>) tmpList);  
		}
		ArrayList<ArrayList<Integer>> kmeans_result = new ArrayList<ArrayList<Integer>>();
        kmeans_result = kmeans_njz(dataList,4);
        String result = JSON.toJSONString(kmeans_result);
       // System.out.println(result);

      
    }//end-main  
}//end-class