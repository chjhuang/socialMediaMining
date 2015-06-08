package ucas.dataMining.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

@MultipartConfig
@WebServlet("/applicationServlet")
public class ApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 7873646937656665027L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String movieId = request.getParameter("id"); //选择的电影ID
		
		String responseMessage;
		// 处理传入的文件
		try {
			JSONObject movie = new JSONObject();

			movie.put("movie_name",
					"Romy and Michele's High School Reunion (1997)");
			movie.put("release_data", "25-Apr-1997");
			movie.put(
					"movie_url",
					"http://us.imdb.com/M/title-exact?Romy%20and%20Michele%27s%20High%20School%20Reunion%20%281997%29");
			movie.put("movie_type", "Horror");

			responseMessage = movie.toJSONString();

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
	}
}
