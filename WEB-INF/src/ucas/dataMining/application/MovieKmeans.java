package ucas.dataMining.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ucas.dataMining.dao.MovieKmeansEvaluate;
import ucas.dataMining.kmeans.KMeansUserFeature;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
  
public class MovieKmeans implements Runnable{  
	
	public int k;
	
	public MovieKmeans(int k)
	{
		this.k = k;
	}
	
	public MovieKmeans()
	{
		
	}
      
	public static ArrayList<ArrayList<Integer>> kmeans_result;
	public static List<ArrayList<Double>> dataList;  
	
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
        //System.out.println("original centers:");  
        //for(int i=0; i<k; i++)  
            //System.out.println(centers.get(i));  
        
        //进行若干次迭代，直到聚类中心稳定
        int count = 100;
        while(count>0){  
        	
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
            //System.out.println("\nnew clusters' centers:\n");  
            //for(int i=0;i<k;i++)  
                //System.out.println(newCenters.get(i));  
            
            //当新旧中心之间距离---小于阈值时，聚类算法结束  
            double distance = 0;  
            for(int i=0;i<k;i++)
                distance += cos(centers.get(i),newCenters.get(i));
                
            //System.out.println("\ndistance: "+distance+"\n\n");
            
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
            count--;
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
    
    /**
     * 返回聚类结果评价指标
     * @return
     */
    public static MovieKmeansEvaluate getKmeansEvaluate(){
        float cohesiveness = computeCoh(kmeans_result,dataList);
        float separateness = computeSep(kmeans_result,dataList);
        float silhouette = computeSil(kmeans_result,dataList);
        
        MovieKmeansEvaluate evaluate = new  MovieKmeansEvaluate(cohesiveness,separateness,silhouette);
		return evaluate;
    }
    
    /**
     * 返回聚类结果
     * @param 聚类数k
     * @return
     */
    public static String getKmeans(int k){
    	 
    	kmeans_result = new ArrayList<ArrayList<Integer>>();
    	dataList = new ArrayList<ArrayList<Double>>();  

    	KMeansUserFeature uf = new KMeansUserFeature();
        //String dir = "/Users/apple/Desktop/njz_python/clusterForMovieUser/src/clusterForMovieUser";
 		uf.loadData("uploadFile/movie_user.json");
 		System.out.println(uf.size); 
 		uf.getFeature();
 		//System.out.println(uf.size); 
 		for(int i=0;i<uf.size;i++){
 			List<Double> tmpList = new ArrayList<Double>();  
 			for(int j=0;j<uf.n;j++){
 				tmpList.add(uf.dataSet[i][j]);  
 			}
 	         dataList.add((ArrayList<Double>) tmpList);  
 		}
 		
        kmeans_result = kmeans_njz(dataList,k);
        
        JSONArray clusterJsonArray = new JSONArray();
    	for(int j=0; j<kmeans_result.size(); j++){
    		String cluster = "cluster"+String.valueOf(j+1);
    		JSONObject clusterJson = new JSONObject();
    		JSONArray userJsons=new JSONArray();
    		
    		clusterJson.put("label", cluster);
        	
        	ArrayList<Integer> userIdList = kmeans_result.get(j);
    		for(int u_id : userIdList){
        		JSONObject temp_json = new JSONObject();
        		temp_json.put("id", u_id);
        		userJsons.add(temp_json);
    		}
    		
    		clusterJson.put("users", userJsons);
    		clusterJsonArray.add(clusterJson);
    	}
       
    	//System.out.println("聚类结果："+clusterJsonArray.toJSONString());
    	//将结果存入文件
    	JSONObject save = new JSONObject();
		save.put("classifications", clusterJsonArray);
    	
        return save.toJSONString();
		
	}
    
    
    public static void main(String[] args) throws IOException{  
        //获取特征进行聚类---943user
    	//System.out.println("test---kmeans");
        List<ArrayList<Double>> dataList = new ArrayList<ArrayList<Double>>();  
        KMeansUserFeature uf = new KMeansUserFeature();
//        String dir = "/Users/apple/Desktop/njz_python/clusterForMovieUser/src/clusterForMovieUser";
//        String dir = "C:/Program Files/apache-tomcat-7.0.62/webapps/socialMediaMining/uploadFile";
//		uf.loadData(dir+"/movie_user.json");
		uf.loadData("uploadFile/movie_user.json");

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
        
        float cohesiveness = computeCoh(kmeans_result,dataList);
        float separateness = computeSep(kmeans_result,dataList);
        float silhouette = computeSil(kmeans_result,dataList);
        System.out.println(cohesiveness+" , "+separateness+" , "+silhouette);
//        System.out.println(kmeans_result);
        
    	JSONArray clusterJsonArray = new JSONArray();
    	for(int j=0; j<kmeans_result.size(); j++){
    		String cluster = "cluster"+String.valueOf(j+1);
    		JSONObject clusterJson = new JSONObject();
    		JSONArray userJsons=new JSONArray();
    		
    		clusterJson.put("label", cluster);
        	
        	ArrayList<Integer> userIdList = kmeans_result.get(j);
    		for(int u_id : userIdList){
        		JSONObject temp_json = new JSONObject();
        		temp_json.put("id", u_id);
        		userJsons.add(temp_json);
    		}
    		
    		clusterJson.put("users", userJsons);
    		clusterJsonArray.add(clusterJson);
    	}
       
      
    }//end-main  

	private static float computeSil(ArrayList<ArrayList<Integer>> kmeans_rs,List<ArrayList<Double>> dataList) {
		float sil = 0;
		int k = kmeans_rs.size();
		int instance_num = dataList.size();
		int instance_len = dataList.get(0).size();
		ArrayList<Double> a = new ArrayList<Double>();
		ArrayList<Double> b = new ArrayList<Double>();
		ArrayList<Double> s = new ArrayList<Double>();
		//初始化
		for (int i=0; i<instance_num; i++){
			a.add(0.0);
			b.add(0.0);
			s.add(0.0);
		}
		
		//簇内计算
		for(int i=0; i<k; i++){  
	        ArrayList<Integer> cluster = kmeans_rs.get(i); 
	        for(int t=0; t<cluster.size(); t++){
	        	int instance = cluster.get(t);
	        	double sum = 0;
	        	 for(int t1=0; t1<cluster.size(); t1++){
	        		 int other_instance = cluster.get(t1);
	        		 if(instance == other_instance) continue;
	        		 sum +=  Math.pow(Math.abs( cos(dataList.get(instance), dataList.get(other_instance))),2.0);
	        	 }
	        	 a.set(instance, sum/(cluster.size()-1));
	        }
	    }  
		
		//簇间计算
		for(int i=0; i<k; i++){  
	        ArrayList<Integer> cur_cluster = kmeans_rs.get(i); 
	        for(int j=0; j<cur_cluster.size(); j++){
        		int instance = cur_cluster.get(j);
        		 for(int i1=0; i1<k; i1++){
     	        	if(i==i1) continue;
     	        	ArrayList<Integer> other_cluster = kmeans_rs.get(i1);
     	        	double sum = 0;
     	        	for (int j1=0; j1<other_cluster.size();j1++){
     	        		sum +=  Math.pow(Math.abs( cos(dataList.get(instance), dataList.get(other_cluster.get(j1)))),2.0);
     	        	}//end-other_cluster
     	        	double temp_b = sum/other_cluster.size();
     	        	if(b.get(instance)==0){
     	        		b.set(instance, temp_b);
     	        	}else{
     	        		if(b.get(instance)>temp_b)
     	        			b.set(instance, temp_b);
     	        	}
     	        }
        	}//end instance
	       
	    }//end cur_cluster
		
		//计算s
		for(int i=0; i<instance_num; i++){
			double max;
			if(b.get(i)>a.get(i)){
				max = b.get(i);
			}else{
				max = a.get(i);
			}
			s.set(i,(b.get(i)-a.get(i))/max);
		}
		
		for(int i=0; i<instance_num; i++){
			sil += s.get(i);
		}
		sil /= instance_num;
		return sil;
	}

	private static float computeSep(ArrayList<ArrayList<Integer>> kmeans_rs,List<ArrayList<Double>> dataList) {
		float sep = 0;
		int k = kmeans_rs.size();
		int instance_len = dataList.get(0).size();
		//所有数据的中心
		ArrayList<Double> CENTER = new ArrayList<Double>();
        for(int i=0; i<instance_len; i++){
        	double sum = 0;  
        	for(int j=0; j<dataList.size(); j++) 
        		sum += dataList.get(j).get(i);
        	CENTER.add(sum/dataList.size());  
        }
        
        //每个簇的中心
        List<ArrayList<Double>> Centers = new ArrayList<ArrayList<Double>>();  
        for(int i=0; i<k; i++){  
	          ArrayList<Integer> cluster = kmeans_rs.get(i); 
              ArrayList<Double> tmp = new ArrayList<Double>();  
	          for(int j=0; j<instance_len; j++){  
	              double sum = 0;  
	              for(int t=0; t<cluster.size(); t++)  
	                  sum += dataList.get(cluster.get(t)).get(j);  
	              tmp.add(sum/cluster.size());  
	          } 
	          Centers.add(tmp);  
	    }  
	
        //簇中心与数据中心计算
		for(int i=0; i<k; i++){
			ArrayList<Double> center = Centers.get(i);
	        	sep += Math.pow(Math.abs( cos(center, CENTER)),2.0);
		}
		return sep;
	}

	private static float computeCoh(ArrayList<ArrayList<Integer>> kmeans_rs, List<ArrayList<Double>> dataList) {
		float coh = 0;
		int k = kmeans_rs.size();
		int instance_len = dataList.get(0).size();
        List<ArrayList<Double>> Centers = new ArrayList<ArrayList<Double>>();  
		for(int i=0; i<k; i++){  
	          ArrayList<Integer> cluster = kmeans_rs.get(i); 
              ArrayList<Double> tmp = new ArrayList<Double>();  
	          for(int j=0; j<instance_len; j++){  
	              double sum = 0;  
	              for(int t=0; t<cluster.size(); t++)  
	                  sum += dataList.get(cluster.get(t)).get(j);  
	              tmp.add(sum/cluster.size());  
	          } 
	          Centers.add(tmp);  
	    }  
	
		for(int i=0; i<k; i++){  
	          ArrayList<Integer> cluster = kmeans_rs.get(i);
	          for(int t=0; t<cluster.size(); t++){
	        	  coh += Math.pow(Math.abs( cos(dataList.get(cluster.get(t)), Centers.get(i))),2.0);
	          }
		}
		return coh;
	}
	
	
	
	@Override
	public void run() {
		System.out.println("开始进行kMeans分类");
		String clusterResultString = getKmeans(k);
		
		System.out.println();
		try {
			FileIOUtil.writeToFile(clusterResultString, 
					FileIOUtil.rootPath+"json\\kmeans.json");
			Flags.kmeans = true;
			System.out.println("kmeans聚类结束");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}//end-class