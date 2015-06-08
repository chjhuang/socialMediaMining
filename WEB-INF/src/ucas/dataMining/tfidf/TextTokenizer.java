package ucas.dataMining.tfidf;

import java.util.List;

public interface TextTokenizer {

	List<String> tokenize(String text);
}
