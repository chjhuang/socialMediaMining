package ucas.dataMining.decisionTree;

import java.util.*;

import com.alibaba.fastjson.JSONObject;

public class DecisionTree {
	
	private LinkedHashSet<String> attributeNames;
	private Map<String, Set<String>> attributePairs;
	
	private boolean attributeValuesRanged;  //是否规定了属性的取值范围

	private DataInstances dataInstances;
	
	//标记是否已经生成决策树
	private boolean compiled;

	private AttributeNode rootAttribute;

	private Algorithm algorithm;

	public DecisionTree() {
		algorithm = null;
		dataInstances = new DataInstances();
		attributeNames = new LinkedHashSet<String>();
		attributePairs = new HashMap<String, Set<String>>();
		attributeValuesRanged = false;
	}

	private void setDefaultAlgorithm() {
		if (algorithm == null)
			setAlgorithm(new ID3Algorithm(dataInstances));
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * 设置属性名
	 */
	public DecisionTree setAttributeNames(String[] attributeNames) {
		compiled = false;

		this.attributePairs.clear();
		attributeValuesRanged = false;

		this.attributeNames.clear();

		for (int i = 0; i < attributeNames.length; i++)
			this.attributeNames.add(attributeNames[i]);

		return this;
	}

	/**
	 * 设置指定属性的取值范围
	 */
	public DecisionTree setAttributes(String attributeName, String[] attributeValues) {
		if (!this.attributeNames.contains(attributeName)) {
			//TODO 反馈预处理不当信息
			return this;
		}

		compiled = false;
		attributeValuesRanged = true;

		Set<String> valuesSet = new HashSet<String>();
		for (int i = 0; i < attributeValues.length; i++)
			valuesSet.add(attributeValues[i]);

		this.attributePairs.put(attributeName, valuesSet);

		return this;
	}

	/**
	 * 为决策树添加训练实例
     */
	public DecisionTree addInstance(String[] attributeValues, String classification) throws IllegalArgumentException {
		String[] attributeNames = this.attributeNames.toArray(new String[0]);

		if (attributeValuesRanged) {
			for (int i = 0; i < attributeValues.length; i++) {
				if (!attributePairs.get(attributeNames[i]).contains(attributeValues[i])) {
					throw new IllegalArgumentException("范围外数据，属性值不在取值范围内");
				}
			}
		}
		
		compiled = false;
		dataInstances.add(attributeNames, attributeValues, classification);
		return this;
	}

	public DecisionTree addInstance(Map<String, String> attributePairs, String classification) {
		compiled = false;
		dataInstances.add(attributePairs, classification);
		return this;
	}

	/**
	 * 利用决策树做决策
	 */
	public String classify(Map<String, String> data) throws UnknownDecisionException {
		compile();
		return rootAttribute.classify(data);
	}

	private AttributeNode compileWalk(AttributeNode current,
			Map<String, String> chosenAttributes, Set<String> usedAttributes) {
		
		if (current.isLeaf())
			return current;

		//当前节点属性名
		String attributeName = current.getAttributeName();

		//将当前节点属性加入到已用属性
		usedAttributes.add(attributeName);

		for (String attributeValue : this.attributePairs.get(attributeName)) {
			chosenAttributes.put(attributeName, attributeValue);

			// 为当前节点添加决策分支
			current.addDecisionBranch(attributeValue,
					compileWalk(algorithm.nextAttributeNode(chosenAttributes,
							usedAttributes), chosenAttributes, usedAttributes));
		}

		//移除分配过分支节点的属性
		chosenAttributes.remove(attributeName);

		//返回当前分支节点
		return current;
	}

	public void compile() {
		if (compiled)
			return;

		//如果没有给定决策树算法，设置默认决策树算法
		setDefaultAlgorithm();

		//选定的属性对
		Map<String, String> chosenAttributes = new HashMap<String, String>();
		//已经划分过的属性
		Set<String> usedAttributes = new HashSet<String>();

		if (!this.attributeValuesRanged)
			this.attributePairs = this.dataInstances.extractAttributes();

		//遍历，生成决策树
		rootAttribute = compileWalk(algorithm.nextAttributeNode(chosenAttributes, usedAttributes), 
				chosenAttributes, usedAttributes);

		compiled = true;
	}

	/**
	 * 决策树根节点
	 */
	public AttributeNode getRoot() {
		return rootAttribute;
	}
	
	public LinkedHashSet<String> getAttributeNames() {
		return this.attributeNames;
	}
	
	public String toString() {
		compile();

		if (rootAttribute != null) {
			return rootAttribute.toString();
		}
		else {
			return "";
		}
	}
	
	/**
	 * 将决策树转化成Json对象
	 * @return
	 */
	public JSONObject toJson() {
		compile();
		
		if(rootAttribute != null) {
			return rootAttribute.toJson(null);
		}
		else {
		    return null;
		}
	}
}
