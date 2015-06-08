package ucas.dataMining.decisionTree;

import java.util.*;

public class ID3Algorithm implements Algorithm {
	private DataInstances dataInstances;

	public ID3Algorithm(DataInstances dataInstances) {
		this.dataInstances = dataInstances;
	}

	/**
	 * 返回下一决策节点
	 */
	public AttributeNode nextAttributeNode(Map<String, String> chosenAttributes, Set<String> usedAttributes) {
		double currentGain = 0.0, bestGain = 0.0;
		String bestAttribute = "";

		/*
		 * 所有实例的类别标记一致，生成一个叶子节点
		 */
		if (dataInstances.countPositive(chosenAttributes) == 0) {
			return new AttributeNode("NO", NodeType.CLASSIFICATION);
		}
		else if (dataInstances.countNegative(chosenAttributes) == 0) {
			return new AttributeNode("YES", NodeType.CLASSIFICATION);
		}
		
		double groundEntropy = entropy(chosenAttributes);  //参考信息熵
		for (String attribute : remainingAttributes(usedAttributes)) {
			//在剩下的属性集中找到一个区分度最好的属性，作为决策树下一分支节点
			currentGain = groundEntropy - entropy(attribute, chosenAttributes); //信息增益
			if (currentGain > bestGain) {
				bestAttribute = attribute;
				bestGain = currentGain;
			}
		}

		//如果所有节点都没有信息增益
		if (bestGain == 0.0) {
			return new AttributeNode("UNKNOWN", NodeType.CLASSIFICATION);
		} else {
			return new AttributeNode(bestAttribute, NodeType.ATTRIBUTE);
		}
	}

	/**
	 * 未用过的属性名
	 */
	private Set<String> remainingAttributes(Set<String> usedAttributes) {
		Set<String> result = dataInstances.extractAttributeNames();
		result.removeAll(usedAttributes);
		return result;
	}

	/**
	 * 参考信息熵
	 */
	private double entropy(Map<String, String> specifiedAttributes) {
		double totalInstances = dataInstances.countMatchedInstances(specifiedAttributes);
		int positiveInstances = dataInstances.countPositive(specifiedAttributes);
		int negativeInstances = dataInstances.countNegative(specifiedAttributes);

		return -nlog2(positiveInstances / totalInstances) - nlog2(negativeInstances / totalInstances);
	}

	private double entropy(String attributeName, String attributeValue, Map<String, String> specifiedAttributes) {
		Map<String, String> comAttributes = new HashMap<String, String>(specifiedAttributes);
		comAttributes.put(attributeName, attributeValue);
		
		return entropy(comAttributes);
	}

	/**
	 * 指定属性的信息熵
	 */
	private double entropy(String attributeName, Map<String, String> specifiedAttributes) {
		double sum = 0.0;
		double matchedCount = dataInstances.countMatchedInstances(specifiedAttributes);
		
		Set<String> attributeValues = dataInstances.extractAttributeValues(attributeName);
		for (String attributeValue : attributeValues) {
			double entropyPart = entropy(attributeName, attributeValue, specifiedAttributes);
			double attributeValueCount = dataInstances.countSameValueInstance(attributeName, attributeValue);

			sum += (attributeValueCount / matchedCount) * entropyPart;
		}

		return sum;
	}

	/**
	 * value * log2(value)
	 */
	private double nlog2(double value) {
		if (value == 0)
			return 0;

		return value * Math.log(value) / Math.log(2);
	}
}
