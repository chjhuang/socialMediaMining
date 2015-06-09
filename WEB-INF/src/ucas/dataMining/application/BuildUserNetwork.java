package ucas.dataMining.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ucas.dataMining.dao.Movie;
import ucas.dataMining.dao.SimilarUser;
import ucas.dataMining.dao.User;
import ucas.dataMining.dataAccess.DataFactory;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


class mycmp implements Comparator<User>  
{  
	//实现对userid排序
  public int compare(User A, User B)   
  {  
	  int tmp1 =  Integer.parseInt(A.getId());
	  int tmp2 = Integer.parseInt(B.getId());
        if(tmp1<tmp2)
        	return -1;
        else if(tmp1>tmp2)
        	return 1;
        else
        	return 0;
  }  
} 
public class BuildUserNetwork implements Runnable{
	/*
	 *buildSocialJson函数为求构建网咯social的json文件
	 *users为原始的用户的list，susers为用户余弦关系对list 
	 *name为输入的电影的名字 
	 *返回为JSONObject
	 */
	private static JSONObject buildSocialJson(
						List<User> users,List<Movie>movies,List<SimilarUser> susers){
		JSONObject jsonBuilder = new JSONObject();
		int countnode = 0;
		//对用户排序
		Collections.sort(users, new mycmp());
		
		JSONArray nodesList = new JSONArray();  //求nodes(对某一电影所有用户nodes)信息
		for (int i = 0; i < users.size(); i++){
			if (!users.get(i).ratings.keySet().isEmpty()) {
				ArrayList<String> strarray1 = new ArrayList<String>(users.get(i).ratings.keySet());
				HashMap<String, Integer> tmpmap = new HashMap<String, Integer>();
				for (int k = 0; k < strarray1.size(); k++) {
					tmpmap.put(strarray1.get(k), users.get(i).ratings.get(strarray1.get(k)));
				}
//				System.out.println(users.get(i).id);

				JSONObject node = new JSONObject();
				node.put("id", Integer.parseInt(users.get(i).id));
				node.put("age", users.get(i).age);
				node.put("gender", users.get(i).gender);
				node.put("occupation", users.get(i).occupation);
				node.put("zip", users.get(i).zipcode);
				node.put("rating", 0);
				node.put("label", 0);
				nodesList.add(node);
				countnode++;
			}	
		}	
			
		jsonBuilder.put("nodes", nodesList);
		
		JSONArray edgesList = new JSONArray();  //添加edges信息，根据susers中产生的信息对
		for (int i1 = 0; i1 < susers.size(); i1++){
				JSONObject edge = new JSONObject();
				edge.put("source",Integer.parseInt(susers.get(i1).id1));
				edge.put("target",Integer.parseInt(susers.get(i1).id2));
				edgesList.add(edge);
		}
		jsonBuilder.put("edges",edgesList);
		return jsonBuilder;
		
	}
	
	
	public static void buildAndSave(String savaPath)
	{
		//doSimilar求用户对，users为@郭郭，原始处理的，20（超过）为相同的评价电影数目，0.9（超过）为余弦值,
		//返回值为susers<list>表,u_id1,u_id2,cos值
		DataFactory.doSimilar(DataFactory.getAllUsers(), 80,0.9); 
		
		JSONObject socialnetwork = new JSONObject();
		//buildSocialJson为求social_network.json，users，movies，susers为所求list，"Barbarella"为电影名
		//返回值为某个特定电影的网络(所有用户的id)json文件.
		socialnetwork = buildSocialJson(DataFactory.getAllUsers(),DataFactory.getAllMovies(),
				DataFactory.getSimilarUser());
		try {
			FileIOUtil.writeToFile(JSONObject.toJSONString(socialnetwork),savaPath);//写出json文件
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public void run() {
		System.out.println("开始构建网络");
		String savePath = FileIOUtil.rootPath+"\\json\\social_network.json";
		buildAndSave(savePath);
		System.out.println("用户网络构建完成");
		Flags.networkBuilt = true;
	}
	
}
