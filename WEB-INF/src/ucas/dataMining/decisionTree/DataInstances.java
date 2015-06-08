package ucas.dataMining.decisionTree;

import java.util.*;

class DataInstances {
	class DataInstance {
		private Map<String, String> values;
		private String classification;

		public DataInstance(String[] attributeNames, String[] attributeValues, String classification) {
			assert (attributeNames.length == attributeValues.length);
			values = new HashMap<String, String>();

			for (int i = 0; i < attributeNames.length; i++) {
				values.put(attributeNames[i], attributeValues[i]);
			}

			this.classification = classification;
		}

		public DataInstance(Map<String, String> attributes, String classification) {
			this.classification = classification;
			this.values = attributes;
		}

		public Set<String> getAttributeNames() {
			return values.keySet();
		}

		public String getAttributeValue(String attribute) {
			return values.get(attribute);
		}

		public boolean isClasslabelMatch(String classification) {
			return classification.equals(this.classification);
		}
	}

	private List<DataInstance> DataInstances;

	public DataInstances() {
		DataInstances = new LinkedList<DataInstance>();
	}

	public void add(String[] attributeNames, String[] attributeValues, String classification) {
		DataInstances.add(new DataInstance(attributeNames, attributeValues, classification));
	}

	public void add(Map<String, String> attributes, String classification) {
		DataInstances.add(new DataInstance(attributes, classification));
	}
	
	/**
	 * 返回数据集中所有属性名集合
	 */
	public Set<String> extractAttributeNames() {
		Set<String> attributes = new HashSet<String>();

		for (DataInstance e : DataInstances) {
			attributes.addAll(e.getAttributeNames());
		}

		return attributes;
	}

	/**
	 * 返回数据集中所有属性名及其可能取值对集合
	 */
	public Map<String, Set<String>> extractAttributes() {
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

		for (String attributeName : extractAttributeNames()) {
			attributes.put(attributeName, extractAttributeValues(attributeName));
		}

		return attributes;
	}
	
	/**
	 * 返回数据集中指定属性名的所有可能取值
	 */
	public Set<String> extractAttributeValues(String attributeName) {
		Set<String> attributeValues = new HashSet<String>();

		for (DataInstance e : DataInstances) {
			attributeValues.add(e.getAttributeValue(attributeName));
		}

		return attributeValues;
	}

	/**
	 * 返回数据集中具有指定属性值的实例个数
	 */
	int countSameValueInstance(String attribute, String value) {
		int count = 0;
		for (DataInstance e : DataInstances) {
			if (e.getAttributeValue(attribute).equals(value)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 返回数据集中与所以给定属性对都匹配的实例个数
	 */
	public int countMatchedInstances(String attributeName, String attributeValue, Map<String, String> attributes) {
		attributes = new HashMap<String, String>(attributes);
		attributes.put(attributeName, attributeValue);

		return countMatchedInstances(attributes);
	}

	/**
	 * 返回数据集中与所以给定属性对都匹配的实例个数
	 */
	public int countMatchedInstances(Map<String, String> attributes) {
		int count = 0;

		nextInstance : for (DataInstance e : DataInstances) {
			for (Map.Entry<String, String> attribute : attributes.entrySet()) {
				if (!(e.getAttributeValue(attribute.getKey()).equals(attribute.getValue()))) {
					continue nextInstance;
				}
			}
			
			//实例与给定的所有属性对都匹配
			count++;
		}
		return count;
	}

	public int countNegative(String attributeName, String attributeValue, Map<String, String> attributes) {
		return countSameLabelInstances("NO", attributeName, attributeValue, attributes);
	}

	public int countPositive(String attributeName, String attributeValue, Map<String, String> attributes) {
		return countSameLabelInstances("YES", attributeName, attributeValue, attributes);
	}

	public int countNegative(Map<String, String> attributes) {
		return countSameLabelInstances("NO", attributes);
	}

	public int countPositive(Map<String, String> attributes) {
		return countSameLabelInstances("YES", attributes);
	}

	/**
	 * 返回数据集中具有指定属性对和指定类别标记的实例个数
	 */
	public int countSameLabelInstances(String classification, String attributeName, String attributeValue, Map<String, String> attributes) {
		attributes = new HashMap<String, String>(attributes);
		attributes.put(attributeName, attributeValue);

		return countSameLabelInstances(classification, attributes);
	}
	
	/**
	 * 返回数据集中具有指定属性对和指定类别标记的实例个数
	 */
	public int countSameLabelInstances(String classification, Map<String, String> attributes) {
		int count = 0;

		nextInstance : for (DataInstance e : DataInstances) {
			for (Map.Entry<String, String> attribute : attributes.entrySet()) {
				if (!(e.getAttributeValue(attribute.getKey()).equals(attribute.getValue()))) {
					continue nextInstance;
				}
			}

			//实例与所有给定的属性对都相匹配
			if (e.isClasslabelMatch(classification)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 数据集中实例个数
	 */
	public int size() {
		return DataInstances.size();
	}
}
