package ucas.dataMining.tfidf;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ucas.dataMining.util.MapUtil;

public class TFIDF implements TextFeaturesExtractor {
	
	private Integer corpusSize;
	private Map<String, Integer> dfMap;   // number of documents where term appears

	public TFIDF() {
	}

	public TFIDF(List<List<String>> documents) {
		this.init(documents);
	}

	@Override
	public double[] extractFeatures(List<String> documentTerms) {
		//TODO
		double[] features = null;
		return features;
	}

	/**
	 * 文档集初始化
	 * @param documents
	 */
	public void init(List<List<String>> documents) {
		// Initialize corpus
		dfMap = new HashMap<String, Integer>();
		
		for (List<String> document : documents) {
			documentFrequencyMeasures(document);
		}

		this.corpusSize = documents.size();
	}
	
	/**
	 * 往文档集里添加新文档
	 * @param document
	 */
	public void addNewDocument(List<String> document) {
		documentFrequencyMeasures(document);
		this.corpusSize++;
	}
	
	/**
	 * 往文档集里添加新文档集
	 * @param documents
	 */
	public void addNewDocuments(List<List<String>> documents) {
		for (List<String> document : documents) {
			documentFrequencyMeasures(document);
		}
		
		this.corpusSize += documents.size();
	}
	
	private void documentFrequencyMeasures(List<String> document) {
		Set<String> termsSet = new HashSet<String>();
		for(String term : document) {
			termsSet.add(term);
		}
		
		for(String term : termsSet) {
			Integer docFreq = this.dfMap.get(term);
			if(docFreq == null) {
				docFreq = 1;
			}
			else {
				docFreq++;
			}
			dfMap.put(term, docFreq);
		}
	}
	
	protected int tf(String term, List<String> documentTerms) {
		int tf = 0;

		for (String documentTerm : documentTerms) {
			if (documentTerm.equals(term)) {
				tf++;
			}
		}

		return tf;
	}

	protected double idf(String term) {
		double df;  //number of document contains term
		if(dfMap.get(term) == null) {
			df = 1.0;
		}
		else {
			df = dfMap.get(term);
		}
		return Math.log(this.corpusSize / df) / Math.log(2);
	}

	/**
	 * 返回指定文档各词汇的tf-idf值
	 * @param document
	 * @return
	 */
	public Map<String, Double> getTermsTF_IDF(List<String> document) {
		DecimalFormat df = new DecimalFormat("#.####");
		HashMap<String, Integer> terms = new HashMap<String, Integer>();
		for(String term : document) {
			Integer acutalCount = terms.get(term);
			if(acutalCount == null) {
				terms.put(term, 1);
			}
			else {
				terms.put(term, acutalCount++);
			}
		}
		
		//calculate terms' tf-idf values
		Map<String, Double> tf_idf = new HashMap<String, Double>(terms.size());
		for(String term : terms.keySet()) {
			double value = terms.get(term) * this.idf(term);
			tf_idf.put(term, Double.parseDouble(df.format(value)));
		}
		
		return MapUtil.sortByMapValue(tf_idf);
		
	}

	/**
	 * 返回文档集文档总数
	 * @return
	 */
	public Integer getCorpusSize() {
		return corpusSize;
	}

	public void setCorpusSize(Integer corpusSize) {
		this.corpusSize = corpusSize;
	}

	public Map<String, Integer> getTermsDocumentFrequencies() {
		return this.dfMap;
	}

	public void setTermsDocumentFrequencies(Map<String, Integer> termsDocumentFrequencies) {
		this.dfMap = termsDocumentFrequencies;
	}

}
