package ucas.dataMining.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ucas.dataMining.application.BuildUserNetwork;
import ucas.dataMining.application.FeatureRegression;
import ucas.dataMining.application.Flags;
import ucas.dataMining.application.MovieBayes;
import ucas.dataMining.application.MovieDecisionTree;
import ucas.dataMining.application.MovieKmeans;
import ucas.dataMining.application.MovieKnn;
import ucas.dataMining.dao.Movie;
import ucas.dataMining.dao.User;
import ucas.dataMining.dataAccess.DataFactory;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@MultipartConfig
@WebServlet("/applicationServlet")
public class ApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 7873646937656665027L;
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String responseMessage = "";
		String requestType =  request.getParameter("type");
		System.out.println("请求类型是:"+requestType);
		
		if(requestType.equals("buildNetwork"))
		{
			while(!Flags.networkBuilt)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(requestType.equals("kMeans"))
		{
			String kString =  request.getParameter("k");
			int k = Integer.parseInt(kString);
			System.out.println("kmeans参数：k="+k);
			
			Thread kmeansThread = new Thread(new MovieKmeans(k));
			kmeansThread.start();
		}
		else if(requestType.equals("movieSelect"))
		{
			String movieId = request.getParameter("id"); //选择的电影ID
			System.out.println("选择的电影id是:"+movieId);
			Thread bayesThread = new Thread(new MovieBayes(movieId));
			Thread knnThread = new Thread(new MovieKnn(movieId));
			Thread regressionThread = new Thread(new FeatureRegression());
			Thread decisionTreeThread = new Thread(new MovieDecisionTree(movieId));
	
			bayesThread.start();
			knnThread.start();
			regressionThread.start();
			decisionTreeThread.start();
			
			Movie targetMovie = new Movie();
			// 处理传入的文件
			try {
				List<Movie> movies = DataFactory.getAllMovies();
				List<User> users = DataFactory.getAllUsers();
				for (Movie movie : movies) {
					if(movie.getId().equals(movieId))
					{
						targetMovie = movie;
					}
				}
				JSONObject jsonBuilder = new JSONObject(); 
				JSONObject movie = new JSONObject();
				
				JSONArray ratingUserArray = new JSONArray();
				movie.put("movie_id", targetMovie.getId());
				movie.put("movie_name",targetMovie.getName());
				movie.put("release_date", targetMovie.getShowTime());
				movie.put("movie_type", DataFactory.getMovieFeatureString(targetMovie.getTags()));
				
				for(User user:users)
				{
					if(user.getRatings().containsKey(movieId))
					{
						Integer rating = user.getRatings().get(movieId);
						JSONObject ratingUser = new JSONObject();
						ratingUser.put("userId", user.getId());
						ratingUser.put("rating", rating);
						ratingUserArray.add(ratingUser);
					}
				}
				
				jsonBuilder.put("movie", movie);
				jsonBuilder.put("ratingUsers", DataFactory.getOrderedUserRatings(ratingUserArray));
				
				responseMessage = jsonBuilder.toJSONString();
			} catch (Exception ex) {
				responseMessage = "Error: " + ex.getMessage();
			}

		}else if(requestType.equals("decisionTree"))
		{
			while(!Flags.decisionTree)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if (requestType.equals("NBC")) {
			while(!Flags.nbc)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if (requestType.equals("kNN")) {
			while(!Flags.knn)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			while(!Flags.regression)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println(responseMessage);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(responseMessage);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// String filename = file.getSubmittedFileName();
		Flags.reset();
		//计时
		long start = System.currentTimeMillis();	
		
		//设置根目录
		FileIOUtil.rootPath = request.getServletContext().getRealPath("/");
		//获取上传文件
		Part file = request.getPart("file");
		
		InputStream fileStream = file.getInputStream();
		//将输入流写入内存
		String fileContent = FileIOUtil.readInputStream(fileStream);
		
		//将写入内存中的json格式的内容加载为对象
		DataFactory.LoadData(fileContent);
		//利用加载的对象进行算法的实现,将执行结果存入指定的文件夹
		
		//1、构建网络
		Thread buildNetwork = new Thread(new BuildUserNetwork());
		buildNetwork.start();
		System.out.println("");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("");		
	}
	
	
	
}
