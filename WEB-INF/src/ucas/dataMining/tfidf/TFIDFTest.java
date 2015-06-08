package ucas.dataMining.tfidf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class TFIDFTest {
	public static void main(String[] args) throws IOException {
		TextTokenizer tokenizer = new TwitterTokenizer();
		
		List<String> corpus = new ArrayList<String>();
		List<List<String>> documents = new ArrayList<List<String>>();
		
		BufferedReader br = new BufferedReader(new FileReader(".\\uploadFile\\twitter-sentiment.txt"));
		String line =  br.readLine();
		while(line != null) {
			corpus.add(line);
			documents.add(tokenizer.tokenize(line));
			line = br.readLine();
		}
		br.close();
		
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
	    
	    System.out.println(jsonBuilder.toJSONString());
		
		
		//写json文件
//		try {
//			FileWriter fw = new FileWriter("C:\\Program Files\\apache-tomcat-8.0.21\\webapps\\d3-example\\tf_idf.json");
//			PrintWriter out = new PrintWriter(fw);
//			out.write(jsonBuilder.toString());
//            out.println();
//            fw.close();
//            out.close();
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		} 
	}
}
