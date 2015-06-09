package ucas.dataMining.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
	private String[] columnNames;  //属性名
	private List<String[]> instances;  //训练实例
	private DecisionTree decisionTree;  //决策树
	private String testResult;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
		String actionType = request.getParameter("type");  //请求处理类型：测试、训练、预测
		
		String responseMessage = "";
		
		if(actionType.equals("training")) {
			try {
				decisionTreeMaker();
				responseMessage = columnNames[columnNames.length - 1];
				responseMessage += decisionTree.getAttributeNames().toString();
			}
			catch(Exception ex) {
				responseMessage = "Error: " + ex.getMessage();
				System.out.println(responseMessage);
			}
			
			System.out.println("决策树训练完毕！");
		}
		else if(actionType.equals("test")) {
			if(testResult != null) {
				responseMessage = testResult;
			}
			else {
				responseMessage = "Error: 没有训练集!";
			}
			System.out.println("决策树测试完毕！结果： " + responseMessage);
		}
		else {
			// 结果预测
			if (decisionTree == null) {
				responseMessage = "没有可用决策树";
			} 
			else {
				Map<String, String> caseMap = new HashMap<String, String>();

				LinkedHashSet<String> attributeNames = decisionTree.getAttributeNames();
				Iterator<String> iterator = attributeNames.iterator();
				while (iterator.hasNext()) {
					String attributeName = iterator.next();
					caseMap.put(attributeName, request.getParameter(attributeName));
				}

				try {
					responseMessage = decisionTree.classify(caseMap);
					
				} catch (UnknownDecisionException e) {
					responseMessage = "UNKNOWN";
				}
				
				System.out.println("实例预测完成！结果：" + responseMessage);
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
        System.out.println("决策树数据集上传成功！");
        String responseMessage = "";
        //处理传入的文件
        try{
        	//FileIOUtil.saveToLocal(this.getServletContext().getRealPath("/uploadFile/decisionTree.csv"), file.getInputStream());
        	loadTrainingSet(file.getInputStream());
        }
        catch(Exception ex) {
        	responseMessage = "Error: " + ex.getMessage();
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseMessage);
    }
	
	/**
	 * 数据预处理
	 * @throws IOException 
	 */
	private void loadTrainingSet(InputStream dataSet) throws IOException {
		decisionTree = null;
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataSet));
		
		//属性名
		String line =  bufferedReader.readLine();
		if(line == null) {		
			return;
		}
		
		columnNames = line.trim().split(",");		
		instances = new ArrayList<String[]>();
		//添加训练实例
		line = bufferedReader.readLine();
		while(line != null) {
			instances.add(line.trim().split(","));
			line = bufferedReader.readLine();
		}
		
		dataSet.close();
		bufferedReader.close();
	}
	
	/**
	 * 决策树生成
	 */
	private void decisionTreeMaker() throws Exception {
		if(decisionTree != null ) {
			return;
		}
		
		testResult = null;
		
		decisionTree = new DecisionTree();		
		if(instances.size() < 1) {
			throw new Exception("没有训练集!");
		}
		
		decisionTree.setAttributeNames(arrayCut(columnNames));
		
		for(String[] instance : instances) {
			decisionTree.addInstance(arrayCut(instance), instance[instance.length - 1]);
		}
		
		//生成结果
		FileIOUtil.writeToFile(decisionTree.toJson().toJSONString(), this.getServletContext().getRealPath("/json/decision_tree.json"));
		
		testResult = crossValidation();
	}
	
	/**
	 * 算法交叉验证 leave-one-out
	 * @return
	 */
	private String crossValidation() {
		if(instances.size() < 1) {
			return "没有训练集!";
		}
		
		int totalSize = instances.size();  //训练集大小
		int correctCount = 0;   //预测正确实例数
		
		for(int i = 0; i < totalSize; i++) {
			//实例训练
			DecisionTree dTree = new DecisionTree();
			dTree.setAttributeNames(arrayCut(columnNames));
			for(int j = 0; j < totalSize; j++) {
				if(j != i) {
					String[] instance = instances.get(j);
					dTree.addInstance(arrayCut(instance), instance[instance.length - 1]);
				}
			}
			
			Map<String, String> testInstance = new HashMap<String, String>();
			for(int k = 1; k < columnNames.length - 1; k++) {
				testInstance.put(columnNames[k], instances.get(i)[k]);
			}
			
			//测试
			try {
				if(dTree.classify(testInstance).equals(instances.get(i)[columnNames.length - 1])) {
					correctCount++;
				}
			} catch (UnknownDecisionException e) {
				
			}
		}
		
		return "数据样例：" + totalSize + "    错误样例：" + (totalSize - correctCount) + "    正确率：" + (((double)correctCount) / totalSize) * 100 + "%";
	}
	
	private String[] arrayCut(String[] strings) {
		String[] subStrings = new String[strings.length - 2];
		for(int i = 0; i < strings.length - 2; i++) {
			subStrings[i] = strings[i+1];
		}
		
		return subStrings;
	}
}
