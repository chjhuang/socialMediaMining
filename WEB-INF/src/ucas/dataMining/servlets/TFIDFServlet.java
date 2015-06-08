package ucas.dataMining.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.alibaba.fastjson.JSONObject;

import ucas.dataMining.tfidf.TFIDF;
import ucas.dataMining.tfidf.TextTokenizer;
import ucas.dataMining.tfidf.TwitterTokenizer;
import ucas.dataMining.util.FileIOUtil;

@MultipartConfig
@WebServlet("/tfidfServlet")
public class TFIDFServlet extends HttpServlet {
	private static final long serialVersionUID = 4289593384847951612L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part file = request.getPart("file");
        //String filename = file.getSubmittedFileName();
        
        String responseMessage = "";
        //处理传入的文件
        try{
        	tfIdfCalculator(file.getInputStream());
        } 
        catch(Exception ex) {
        	responseMessage = "Error: " + ex.getMessage();
        }
        
        System.out.println(responseMessage);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseMessage);
    }
	
	private void tfIdfCalculator(InputStream dataSet) throws IOException {
		TextTokenizer tokenizer = new TwitterTokenizer();
		
		List<String> corpus = new ArrayList<String>();
		List<List<String>> documents = new ArrayList<List<String>>();
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataSet));
		String line =  bufferedReader.readLine();
		while(line != null) {
			corpus.add(line);
			documents.add(tokenizer.tokenize(line));
			line = bufferedReader.readLine();
		}
		dataSet.close();
		bufferedReader.close();
		
		TFIDF tfidf = new TFIDF();
		tfidf.init(documents);
		
		JSONObject jsonBuilder = new JSONObject();
		jsonBuilder.put("corpusSize", tfidf.getCorpusSize());
		
		List<JSONObject> corpusList = new ArrayList<JSONObject>();
		int i = 0;
		for(String document : corpus) {
			JSONObject corpusJsonObject = new JSONObject();
			corpusJsonObject.put("document", document);
			corpusJsonObject.put("terms", tfidf.getTermsTF_IDF(documents.get(i)));
			corpusList.add(corpusJsonObject);
			i++;
		}
	    jsonBuilder.put("corpus", corpusList);
		
		//生成结果
		FileIOUtil.writeToFile( jsonBuilder.toJSONString(),
				this.getServletContext().getRealPath("/") + "\\json\\tf_idf.json");
	}
}
