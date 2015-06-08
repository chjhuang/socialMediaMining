package ucas.dataMining.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ucas.dataMining.dao.DataPoint;
import ucas.dataMining.regression.SimpleLinearRegression;
import ucas.dataMining.util.FileIOUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@MultipartConfig
public class NSLRServlet extends HttpServlet {
	

	private static final long serialVersionUID = -6485973345643890547L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Part part = request.getPart("file");
		InputStream file = part.getInputStream();
		String responseMessage = "";
		// 处理传入的文件
		try {
			//获取文件名
			Collection<String> headers = part.getHeaders("Content-Disposition");
			String fileName = "";
			if(headers.size()==1)
				fileName = getFileName((String)(headers.toArray()[0]));
			
			//存储文件	
			String savePath = request.getServletContext().getRealPath(
					"/uploadFile/linearRegression.json");
			
			File saveFile = new File(savePath);
			FileOutputStream fos = new FileOutputStream(saveFile);
			
			byte b[] = new byte[1024];
            int n;
            while ((n = file.read(b)) != -1) {
            	fos.write(b, 0, n);
            }
            file.close();
            fos.close();
            
            //将文件交给算法，进行训练,返回json文件所在路径
            JSONObject trainedJob = train(savePath);
            FileIOUtil.writeToFile(trainedJob.toJSONString(), request.getServletContext().getRealPath(
					"/uploadFile/trainedLinearRegression.json"));
            
		} catch (Exception ex) {
			responseMessage = "Error: " + ex.getMessage();
		}

		System.out.println(responseMessage);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(responseMessage);
	}

	public String getFileName(String header) {
		/**
		 * String[] tempArr1 =
		 * header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
		 * 火狐或者google浏览器下：
		 * tempArr1={form-data,name="file",filename="snmp4j--api.zip"}
		 * IE浏览器下：tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
		 */
		String[] tempArr1 = header.split(";");
		/**
		 * 火狐或者google浏览器下：tempArr2={filename,"snmp4j--api.zip"}
		 * IE浏览器下：tempArr2={filename,"E:\snmp4j--api.zip"}
		 */
		String[] tempArr2 = tempArr1[2].split("=");
		// 获取文件名，兼容各种浏览器的写法
		String fileName = tempArr2[1].substring(
				tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
		return fileName;
	}
	
	public JSONObject train(String sourceFile)
	{
		JSONObject trainJob = new JSONObject();
		try{
			//读取文件，获取数据
			File file = new File(sourceFile); 
			String content = FileIOUtil.readFile(file);
			JSONArray ja = JSONArray.parseArray(content);
			//将其中的数据转化为散点对象List
			List<DataPoint> points = new ArrayList<DataPoint>();
			for(int i=0;i<ja.size();i++)
			{
				String pointStr = ja.get(i).toString().substring(1, ja.get(i).toString().length()-1);
				DataPoint point = new DataPoint(Float.parseFloat(pointStr.split(",")[0]), Float.parseFloat(pointStr.split(",")[1]));
				points.add(point);
			}
			
			//调用回归算法进行拟合
			SimpleLinearRegression slr= new SimpleLinearRegression(points);
			
			trainJob.put("a", slr.getA0());
			trainJob.put("b", slr.getA1());
			trainJob.put("r", slr.getR());
			trainJob.put("points", points);
			
			String jsonStr = JSONObject.toJSONString(trainJob,true);
			System.out.println(jsonStr);
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return trainJob;
	}
	
	
	
	
    
}