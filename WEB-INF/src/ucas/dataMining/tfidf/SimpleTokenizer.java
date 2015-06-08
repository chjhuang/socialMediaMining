package ucas.dataMining.tfidf;

import java.util.ArrayList;
import java.util.List;

public class SimpleTokenizer implements TextTokenizer {
	public SimpleTokenizer() {
		
	}

	@Override
	public List<String> tokenize(String text) {
		List<String> tokens = new ArrayList<String>();
		String[] terms = text.trim().split(" ");
		for(String term : terms) {
			tokens.add(term);
		}
		return tokens;
	}
}
