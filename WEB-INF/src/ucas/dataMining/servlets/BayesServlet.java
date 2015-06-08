package ucas.dataMining.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ucas.dataMining.bayes.naiveBayes;
import ucas.dataMining.dao.dataProcess;

public class BayesServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private naiveBayes bayes;
	private dataProcess d;

	/**
	 * Constructor of the object.
	 */
	public BayesServlet() {
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
		if(method.equals("predict"))
			predict(request,response);
		else if(method.equals("cross_validation"))
			cross_validation(request,response);
	}
  
  public void cross_validation(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
	  request.setCharacterEncoding("utf-8");
	  response.setContentType("text/html;charset=utf-8");
	  boolean[]number={true,true,true,true};
	  double[]result=bayes.cross_validation(d.getDataSet(),d.getLabel(),number);
	  PrintWriter out = response.getWriter();
	  StringBuilder strResult=new StringBuilder();
	
	  for(int i=0;i<result.length;i++){		 		 
		  strResult.append(result[i]);
		  strResult.append(",");
	  }
	  out.write(strResult.toString());
	  bayes.train(d.getDataSet(), d.getLabel(),number);
	  out.flush();
	  out.close();
}	
  
  
  public void predict(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
	    request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		double []list=new double[4];
		String length1=request.getParameter("length1");
		String height1=request.getParameter("height1");
		String length2=request.getParameter("length2");
		String height2=request.getParameter("height2");
		list[0]=Double.parseDouble(length1);
		list[1]=Double.parseDouble(height1);
		list[2]=Double.parseDouble(length2);
		list[3]=Double.parseDouble(height2);
		PrintWriter out = response.getWriter();
		boolean[]number={true,true,true,true};
		String result=bayes.classify(list, number);
		out.write(result);
		out.flush();
		out.close();
	  
  }
	
	public void init() throws ServletException {
		System.out.println("servlet is initing");
		try{
			d=new dataProcess();
			String path=this.getServletContext().getRealPath("/");
			d.loadData(path+"\\data\\irisData.txt");
			bayes=new naiveBayes();
			boolean[]number={true,true,true,true};
			bayes.train(d.getDataSet(), d.getLabel(),number);
			}catch(Exception e){System.out.println(e.fillInStackTrace());}
		
	}

}
