package ucas.dataMining.decisionTree;

import java.util.*;

/**
 * 决策树决策分支
 */
class Decisions {
	private Map<String, AttributeNode> decisionBranchs;

	public Decisions() {
		decisionBranchs = new HashMap<String, AttributeNode>();
	}

	public Map<String, AttributeNode> getDecicionBranchs() {
		return decisionBranchs;
	}

	public void put(String attributeValue, AttributeNode decision) {
		decisionBranchs.put(attributeValue, decision);
	}

	public void clear() {
		decisionBranchs.clear();
	}

	public AttributeNode makeDecision(String value) throws UnknownDecisionException {
		AttributeNode result = decisionBranchs.get(value);
		if (result == null)
			throw new UnknownDecisionException();
		return result;
	}
}
