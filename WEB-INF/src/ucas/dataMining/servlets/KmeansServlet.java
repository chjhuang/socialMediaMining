package ucas.dataMining.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ucas.dataMining.application.MovieKmeans;


public class KmeansServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public KmeansServlet() {
		super();
		
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        
		request.setCharacterEncoding("utf-8");
		String method=request.getParameter("method");
		if(method.equals("Kmeans"))
			Kmeans_servelet(request,response);
		else if(method.equals("Xmeans"))
			Kmeans_servelet(request,response);
		
	}
  
	
	public void Kmeans_servelet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
	 
	  int k;
	  if(request.getParameter("method").equals("Xmeans")){
		  k = 4;
	  }else{
		  k = Integer.parseInt(request.getParameter("k"));
	  }
	  
	  MovieKmeans movieKmeans = new MovieKmeans(k);
	  String result = movieKmeans.getKmeans(k);
	  
	  request.setCharacterEncoding("utf-8");
	  response.setContentType("text/html;charset=utf-8");
	  PrintWriter out = response.getWriter();
	  out.write(result);
	  out.flush();
	  out.close();
  	}	
  
  
}
