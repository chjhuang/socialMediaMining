package ucas.dataMining.dataAccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ucas.dataMining.dao.Movie;
import ucas.dataMining.dao.Node;
import ucas.dataMining.dao.Relation;
import ucas.dataMining.dao.SimilarUser;
import ucas.dataMining.dao.User;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class DataFactory {
	private static List<User> users = new ArrayList<User>();
	private static List<Movie> movies = new ArrayList<Movie>();
	private static List<SimilarUser> susers = new ArrayList<SimilarUser>();
	private static String dataPath = "uploadFile/movie_user.json";
	private static boolean loadedData = false;
	private static int FEATURE_COUNT=19;

	
	public static void Init()
	{
		if(!loadedData)
		{
			File file = new File(dataPath);
			String content="";
			try {
				content = FileIOUtil.readFile(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LoadData(content);
			loadedData = true;
		}
		else{
			System.out.println("数据已加载");
		}
	}
	
	// 首先要加载文件中的数据
	public static void LoadData(String content) {
		// 将加载的旧数据置空,是不是也要将相似用户置空
		DataFactory.users = new ArrayList<User>();
		DataFactory.movies = new ArrayList<Movie>();
		// 将内容读取为json格式的对象并解析
		JSONObject job = JSONObject.parseObject(content);
		// 获取用户内容
		JSONObject users = job.getJSONObject("user");
		Object[] uids = (Object[]) (users.keySet().toArray());
		for (int i = 0; i < uids.length; i++) {
			JSONObject userJson = users.getJSONObject(uids[i].toString());
			String u_id = userJson.getString("u_id");
			Integer temp_id = Integer.parseInt(u_id)-1;
			u_id = temp_id.toString();
			User user = new User(u_id,
					userJson.getInteger("u_age"),
					userJson.getString("u_gender"),
					userJson.getString("u_occupation"),
					userJson.getString("u_zipcode"));
			JSONArray ratings = userJson.getJSONArray("u_rated");
			Map<String, Integer> ratingMap = new HashMap<String, Integer>();
			for (Object ja : ratings) {
				String movieId = (String) ((JSONArray) ja).get(0);
				Integer score = Integer.parseInt((String) ((JSONArray) ja)
						.get(1));
				ratingMap.put(movieId, score);
			}
			user.ratings = ratingMap;
			DataFactory.users.add(user);

		}
		// 获取电影内容
		JSONObject movies = job.getJSONObject("movie");
		Object[] mids = (Object[]) (movies.keySet().toArray());
		for (int i = 0; i < mids.length; i++) {
			JSONObject movieJson = movies.getJSONObject(mids[i].toString());
			Movie movie = new Movie(movieJson.getString("m_id"),
					movieJson.getString("m_name").trim(),
					movieJson.getString("m_time"));
			JSONObject genre = movieJson.getJSONObject("m_genre");
			List<Integer> tags = new ArrayList<Integer>();
			String[] features = { "Mystery", "Romance", "Sci-Fi", "Fantasy",
					"unknown", "Horror", "Film-Noir", "Crime", "Drama",
					"Children\'s", "Animation", "War", "Adventure", "Action",
					"Comedy", "Documentary", "Musical", "Thriller", "Western" };
			for (String feature : features) {
				int tag = Integer.parseInt(genre.getString(feature));
				tags.add(tag);
			}
			movie.tags = tags;
			DataFactory.movies.add(movie);

		}
		
		DataFactory.loadedData = true;

	}

	public static List<User> getAllUsers() {
		DataFactory.Init();
		return DataFactory.users;
	}

	public static List<Movie> getAllMovies() {
		DataFactory.Init();
		return DataFactory.movies;
	}
	
	public static String getMovieFeatureString(List<Integer> tags)
	{
		String[] features = { "Mystery", "Romance", "Sci-Fi", "Fantasy",
				"unknown", "Horror", "Film-Noir", "Crime", "Drama",
				"Children\'s", "Animation", "War", "Adventure", "Action",
				"Comedy", "Documentary", "Musical", "Thriller", "Western" };
		String featureString = "";
		for(int i=0;i<FEATURE_COUNT;i++)
		{
			if(tags.get(i)==1)
			{
				featureString+=features[i]+",";
			}else {
				continue;
			}
		}
		return featureString.substring(0, featureString.length()-1);
	}
	
	public static List<SimilarUser>getSimilarUser(){
		return susers;
	}
	
	
	public static List<Node> getAllNodes(String jsonPath)
	{
		List<Node> nodes = new ArrayList<Node>();
		try {
			File file = new File(jsonPath); 
			String content = FileIOUtil.readFile(file);
			JSONObject job = JSONObject.parseObject(content);
			JSONArray nodeArray = job.getJSONArray("nodes");
			for(Object nodeJson:nodeArray)
			{
				Node node = new Node(((JSONObject)nodeJson).getString("name"), 
						((JSONObject)nodeJson).getInteger("classValue"));
				nodes.add(node);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return nodes;
	}
	
	
	public static List<Relation> getAllEdges(String jsonPath)
	{
		List<Relation> relations = new ArrayList<Relation>();
		try {
			File file = new File(jsonPath); 
			String content = FileIOUtil.readFile(file);
			JSONObject job = JSONObject.parseObject(content);
			JSONArray relationArray = job.getJSONArray("edges");
			for(Object relationJson:relationArray)
			{
				Relation relation = new Relation( 
						((JSONObject)relationJson).getInteger("source"),
						((JSONObject)relationJson).getInteger("target"),
						((JSONObject)relationJson).getString("relation"));
				relations.add(relation);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return relations;
	}
	
	
	public static void doSimilar(List<User> users, int sum,double cossum) {
		/*
		 * sum为用户评过分的共同的电影数目，
		 * cossum为两用户的cos余弦相似度
		 *两次循环，找i用户与j用户电影评分的id，超过设定的sum值时与cossum值，
		 *计算两用户的余弦相似度，保存在List<SimilarUser> susers中； 
		*/
		int j,count=0,coscount=0;
		int tmpsum = 0;
		for (int i = 0; i < users.size(); i++) {
			if (!users.get(i).ratings.keySet().isEmpty()) {
				for (j = i+1; j < users.size(); j++) {
					if (!users.get(j).ratings.keySet().isEmpty()) {
						ArrayList<String> strarray1 = new ArrayList<String>(users.get(i).ratings.keySet());

						HashMap<String, Integer> tmpmap = new HashMap<String, Integer>();
						
						for (int k = 0; k < strarray1.size(); k++) {
							tmpmap.put(strarray1.get(k), 0);
						}
						StringBuilder score1=new StringBuilder();
						StringBuilder score2=new StringBuilder();
						ArrayList<String> strarray2 = new ArrayList<String>(users.get(j).ratings.keySet());
						tmpsum=0;		
						for (int k = 0; k < strarray2.size(); k++) {
							if (tmpmap.get(strarray2.get(k)) != null) {
								String u1=users.get(i).ratings.get(strarray2.get(k))+",";
								String u2=users.get(j).ratings.get(strarray2.get(k))+",";
								score1.append(u1);
								score2.append(u2);
								tmpsum++;
							}
						}
						 if(tmpsum>sum){
							    int len1=score1.toString().length();
							 	String tmp1=score1.toString().substring(0, len1-1);
							 	int len2=score2.toString().length();
							 	String tmp2=score2.toString().substring(0, len2-1);
							 	
							 	double dtmp = getSimilarDegree(tmp1,tmp2);
							 	if(dtmp>cossum){
							 		SimilarUser susers1 = new SimilarUser(users.get(i).id,users.get(j).id,dtmp);
							 		susers.add(susers1);
							 		coscount++;
							 	}
							 	count++;
						 }
						 
					}
				}

			}
		}
	//count为评分m_id数相同的超过sum的u_id对
	System.out.println("m_id数相同的超过"+sum+"的u_id对:"+count);        
	//是coscount为u_id余弦相似度超过cossum的u_id对
	System.out.println("超过sum且余弦相似度超过"+cossum+"的u_id对:"+coscount);       
	
}

	/*
	 * 计算两个字符串()的相似度，简单的余弦计算，未添权重
	 */
	public static double getSimilarDegree(String str1, String str2) {
		// 创建向量空间模型，使用map实现，主键为词项，值为长度为2的数组，存放着对应词项在字符串中的出现次数
		Map<String, int[]> vectorSpace = new HashMap<String, int[]>();
		int[] itemCountArray = null;// 为了避免频繁产生局部变量，所以将itemCountArray声明在此

		// 以,为分隔符，分解字符串
		String strArray[] = str1.split(",");
		for (int i = 0; i < strArray.length; ++i) {
			if (vectorSpace.containsKey(strArray[i]))
				++(vectorSpace.get(strArray[i])[0]);
			else {
				itemCountArray = new int[2];
				itemCountArray[0] = 1;
				itemCountArray[1] = 0;
				vectorSpace.put(strArray[i], itemCountArray);
			}
		}

		strArray = str2.split(",");
		for (int i = 0; i < strArray.length; ++i) {
			if (vectorSpace.containsKey(strArray[i]))
				++(vectorSpace.get(strArray[i])[1]);
			else {
				itemCountArray = new int[2];
				itemCountArray[0] = 0;
				itemCountArray[1] = 1;
				vectorSpace.put(strArray[i], itemCountArray);
			}
		}

		// 计算相似度
		double vector1Modulo = 0.00;// 向量1的模
		double vector2Modulo = 0.00;// 向量2的模
		double vectorProduct = 0.00; // 向量积
		Iterator iter = vectorSpace.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			itemCountArray = (int[]) entry.getValue();

			vector1Modulo += itemCountArray[0] * itemCountArray[0];
			vector2Modulo += itemCountArray[1] * itemCountArray[1];

			vectorProduct += itemCountArray[0] * itemCountArray[1];
		}

		vector1Modulo = Math.sqrt(vector1Modulo);
		vector2Modulo = Math.sqrt(vector2Modulo);

		// 返回相似度
		return (vectorProduct / (vector1Modulo * vector2Modulo));
	}
	
	
	
	
	public static Map<Integer,Double> getMovieAverageRatingAndCount(String movieId)
	{
		List<User> users = DataFactory.getAllUsers();
		Map<Integer,Double> ratingAndCount = new HashMap<Integer,Double>();
		Integer sumRating = 0;
		int ratingCount = 0;
		double averageRating = 0.0;
		for(User user:users)
		{
			if(user.getRatings().containsKey(movieId))
			{
				Integer userRating = user.getRatings().get(movieId);
				sumRating += userRating;
				ratingCount++;
			}
			else
			{
				continue;
			}
		}
		//System.out.println("对电影"+movieId+"进行评分的用户有"+ratingCount+"人次");
		
		averageRating = (double)sumRating/ratingCount;
		
		ratingAndCount.put(ratingCount, averageRating);
		
		return ratingAndCount;
	}
	public static Map<double[][],double[]> getMovieAverageRatingMatrix()
	{
		DataFactory.Init();
		Map<double[][],double[]> result = new HashMap<double[][], double[]>();
		List<Movie> movies = DataFactory.getAllMovies();
		double[][] movieFeatureMtrix = new double[movies.size()][FEATURE_COUNT+1];
		double[] averageRatings = new double[movies.size()];
		for(int i=0;i<movies.size();i++)
		{
			Movie movie = movies.get(i);
			Map<Integer,Double> averageRatingAndCount = getMovieAverageRatingAndCount(movie.getId());
			Integer ratingCount = (Integer) averageRatingAndCount.keySet().toArray()[0];
			Double averageRating = averageRatingAndCount.get(ratingCount);
			averageRatings[i] = averageRating;
			for(int j=0;j<FEATURE_COUNT;j++)
			{
				movieFeatureMtrix[i][j] = Double.parseDouble(movie.getTags().toArray()[j].toString());
			}
			movieFeatureMtrix[i][FEATURE_COUNT] = ratingCount;
		}
		
		result.put(movieFeatureMtrix, averageRatings);
		return result;
	}

	public static String[] getMovieFeatures()
	{
		String[] movieFeatures ={ "Mystery", "Romance", "Sci-Fi", "Fantasy", "unknown", "Horror", "Film-Noir", "Crime",
				"Drama", "Children\'s", "Animation","War", "Adventure", "Action", "Comedy",
				"Documentary", "Musical", "Thriller", "Western","RatingCount"};
		
		return movieFeatures;
	}
	public static void main(String[] args) {
		long start = System.currentTimeMillis();	
		
		DataFactory.Init();
		
		long end = System.currentTimeMillis();
		long time =end-start;
		System.out.println("运行时间："+time+"ms");
	}

}
