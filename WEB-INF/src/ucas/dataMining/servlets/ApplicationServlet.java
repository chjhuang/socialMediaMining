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
		
		String movieId = request.getParameter("id"); //选择的电影ID
		System.out.println("选择的电影id是:"+movieId);
		String responseMessage;
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
					ratingUser.put("user_id", user.getId());
					ratingUser.put("rating", rating);
					ratingUserArray.add(ratingUser);
				}
			}
			
			jsonBuilder.put("movie", movie);
			jsonBuilder.put("ratingUsers", ratingUserArray);
			
			responseMessage = jsonBuilder.toJSONString();
			System.out.println(responseMessage);

		} catch (Exception ex) {
			responseMessage = "Error: " + ex.getMessage();
		}

		System.out.println(responseMessage);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(responseMessage);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// String filename = file.getSubmittedFileName();
		//计时
		long start = System.currentTimeMillis();	
		
		//获取上传文件
		Part file = request.getPart("file");
//		String movieID = request.getParameter("movieID");
//		System.out.println("请求的movieID:"+movieID);
		
		InputStream fileStream = file.getInputStream();
		//将输入流写入内存
		String fileContent = FileIOUtil.readInputStream(fileStream);
		
		//将写入内存中的json格式的内容加载为对象
		DataFactory.LoadData(fileContent);
		//利用加载的对象进行算法的实现,将执行结果存入指定的文件夹
		
		//1、构建网络
		BuildUserNetwork.buildAndSave(request.getServletContext().getRealPath(
				".\\json\\social_network.json"));
		
		long buildNetwork = System.currentTimeMillis();
		long time1 =buildNetwork-start;
		System.out.println("网络构建时间："+time1+"ms");
		
//		//2、Bayes算法
//		MovieBayes mk=new MovieBayes();
//		mk.init(".\\uploadFile\\movie_user.json");
//		//在这输入想要存储的路径
//		String bayesPath = request.getServletContext().getRealPath(
//				".\\json\\bayes.json");
//		mk.getClassfiledResult("1014",bayesPath);
//		
//		long movieBayes = System.currentTimeMillis();
//		long time2 =movieBayes-start;
//		System.out.println("Bayes时间："+time2+"ms");
//		
//		//3、KNN算法
//		MovieKnn mknn=new MovieKnn();
//		mknn.init(".\\json\\movie_user.json");
//		
//		String knnPath = request.getServletContext().getRealPath(
//				".\\json\\knn.json");
//		mknn.getClassfiledResult("1014",knnPath);
//		long end = System.currentTimeMillis();
//		long time3 =end-start;
//		System.out.println("运行时间："+time3+"ms");
	}
	
	
	
}
