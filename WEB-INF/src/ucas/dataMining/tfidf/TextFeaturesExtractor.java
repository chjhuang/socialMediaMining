package ucas.dataMining.tfidf;

import java.util.List;

public interface TextFeaturesExtractor {
	
	double[] extractFeatures(List<String> documentWords);
}
