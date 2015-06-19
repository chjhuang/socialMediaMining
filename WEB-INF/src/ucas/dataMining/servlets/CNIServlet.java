package ucas.dataMining.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ucas.dataMining.dao.Node;
import ucas.dataMining.dao.Relation;
import ucas.dataMining.dataAccess.DataFactory;
import ucas.dataMining.datapreprocessing.DataPreProcessing;
import ucas.dataMining.regression.ClassficationWithNI;
import ucas.dataMining.util.FileIOUtil;
import Jama.Matrix;

import com.alibaba.fastjson.JSONObject;

@MultipartConfig
public class CNIServlet extends HttpServlet {

	private static final long serialVersionUID = -6485973345643890547L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//获取文件
		Part part = request.getPart("file");
		InputStream file = part.getInputStream();
		String responseMessage = "";
		
		// 处理传入的文件
		try {			
			//将上传的文件存储作为备份	
			String savePath = request.getServletContext().getRealPath(
					"/uploadFile/nodes.json");
			
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
            		"/json/trainedNodes.json"));
           
            
		} catch (Exception ex) {
			responseMessage = "Error: " + ex.getMessage();
		}				
		
		
		//设置返回值参数
		response.setCharacterEncoding("utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(responseMessage);
		out.flush();
		out.close();
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
		JSONObject job = new JSONObject();
		try{
			List<Node> nodes;
			List<Relation> edges;
			nodes = DataFactory.getAllNodes(sourceFile);
			edges = DataFactory.getAllEdges(sourceFile);
			//获取节点的classValue
			double[] classValues = DataPreProcessing.getClassValues(nodes);
			//获取节点的邻接矩阵
			Matrix adjanceMa = DataPreProcessing.getAdjacenceMatrix(edges);
			//开始训练
			double[] newclassValues = ClassficationWithNI.train(adjanceMa, classValues);
			System.out.println("训练结束");
			
			//给节点重新赋值
			for(int i=0;i<nodes.size();i++)
			{
				nodes.get(i).classValue = (int) newclassValues[i];
			}
			
			//构造json
			
			job.put("nodes", nodes);
			job.put("edges",edges);
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return job;
	}			
}