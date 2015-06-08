package ucas.dataMining.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ucas.dataMining.decisionTree.DecisionTree;
import ucas.dataMining.decisionTree.UnknownDecisionException;
import ucas.dataMining.util.FileIOUtil;

@MultipartConfig
@WebServlet("/decisionTreeServlet")
public class DecisionTreeServlet extends HttpServlet {
	private static final long serialVersionUID = 7365238949032150065L;
	private DecisionTree decisionTree;
	private String classificationName = "";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
		String responseMessage = "";
		
		//结果预测
		if(decisionTree == null) {
			responseMessage = "没有可用决策树";
		}
		else {
			Map<String, String> caseMap = new HashMap<String, String>();
			
			LinkedHashSet<String> attributeNames = decisionTree.getAttributeNames();
			Iterator<String> iterator = attributeNames.iterator();
			while(iterator.hasNext()) {
				String attributeName = iterator.next();
				caseMap.put(attributeName, request.getParameter(attributeName));
				System.out.println(attributeName + ": " + request.getParameter(attributeName));
			}
			
			try {
				responseMessage = decisionTree.classify(caseMap);
			} catch (UnknownDecisionException e) {
				responseMessage = "UNKNOWN";
			}
		}
		
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseMessage);
	}
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part file = request.getPart("file");
        //String filename = file.getSubmittedFileName();
        
        String responseMessage;
        //处理传入的文件
        try{
        	decisionTreeMaker(file.getInputStream());
        	responseMessage = classificationName;
        	responseMessage += decisionTree.getAttributeNames().toString();
        } 
        catch(Exception ex) {
        	responseMessage = "Error: " + ex.getMessage();
        }
        
        System.out.println(responseMessage);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseMessage);
    }
	
	/**
	 * 决策树生成
	 */
	private void decisionTreeMaker(InputStream dataSet) throws IOException {
		decisionTree = new DecisionTree();
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataSet));
		
		//属性名
		String line =  bufferedReader.readLine();
		if(line == null) {		
			return;
		}
		String[] columnNames = line.trim().split(",");
		classificationName = columnNames[columnNames.length - 1];
		decisionTree.setAttributeNames(arrayCut(columnNames));
		
		//添加训练实例
		line = bufferedReader.readLine();
		while(line != null) {
			String[] instance = line.trim().split(",");
			decisionTree.addInstance(arrayCut(instance), instance[instance.length - 1]);
			line = bufferedReader.readLine();
		}
		
		dataSet.close();
		bufferedReader.close();
		
		//生成结果
		FileIOUtil.writeToFile(decisionTree.toJson().toJSONString(),
				this.getServletContext().getRealPath("/") + "\\json\\decision_tree.json"
				);
	}
	
	private String[] arrayCut(String[] strings) {
		String[] subStrings = new String[strings.length - 2];
		for(int i = 0; i < strings.length - 2; i++) {
			subStrings[i] = strings[i+1];
		}
		
		return subStrings;
	}
}
