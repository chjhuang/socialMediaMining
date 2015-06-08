package ucas.dataMining.decisionTree;

import java.util.*;

import com.alibaba.fastjson.JSONObject;

/**
 * 决策树树枝节点
 */
public class AttributeNode {
	private boolean leaf; // 叶子节点
	private String attributeName; // 属性名
	private Decisions decisions; // 分支判定结果
	private String classification; // 类别
	
	public AttributeNode(String name, NodeType type) {
		switch (type) {
		case ATTRIBUTE: {
			this.leaf = false;
			this.attributeName = name;
			this.classification = null;
			break;
		}
		case CLASSIFICATION: {
			this.leaf = true;
			this.attributeName = null;
			this.classification = name;
			break;
		}
		default: 
			break;
		}
		
		this.decisions = new Decisions();
	}

	public String getAttributeName() {
		return attributeName;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setClassification(String classification) {
		assert (leaf);
		this.classification = classification;
	}

	public String getClassification() {
		assert (leaf);
		return classification;
	}
	
	/**
	 * 运用决策树对实例进行分类
	 * @param data
	 * @return
	 * @throws UnknownDecisionException
	 */
	public String classify(Map<String, String> data) throws UnknownDecisionException {
		if (isLeaf())
			return getClassification();

		AttributeNode nextAttribute = decisions.makeDecision(data.get(attributeName));
		return nextAttribute.classify(data);
	}

	public void addDecisionBranch(String attributeValue, AttributeNode decision) {
		assert (!leaf);

		decisions.put(attributeValue, decision);
	}
	
	public Map<String, AttributeNode> getDecisionBranchs() {
		return decisions.getDecicionBranchs();
	}

	public String toString() {
		StringBuffer b = new StringBuffer();

		for (Map.Entry<String, AttributeNode> e : decisions.getDecicionBranchs().entrySet()) {
			b.append(this.getAttributeName());
			b.append(" -> ");
			if (e.getValue().isLeaf()) {
				b.append(e.getValue().getClassification());
			}
			else {
				b.append(e.getValue().getAttributeName());
			}
			b.append(" [label=\"");
			b.append(e.getKey());
			b.append("\"]\n");

			b.append(e.getValue().toString());
		}

		return b.toString();
	}
	
	/**
	 * 将决策树节点转换成Json对象
	 * @param decisionBasis 决策依据
	 * @return
	 */
	public JSONObject toJson(String decisionBasis) {
		JSONObject jsonObject = new JSONObject();
		
		if(decisionBasis != null) {
		    jsonObject.put("decisionBasis", decisionBasis);
		}
		
		if(this.isLeaf()) {
			jsonObject.put("name", this.getClassification());
			return jsonObject;
		}
		else {
			jsonObject.put("name", this.getAttributeName());
			List<JSONObject> childrenList = new ArrayList<JSONObject>();
			for(Map.Entry<String, AttributeNode> e : decisions.getDecicionBranchs().entrySet()) {
				childrenList.add(e.getValue().toJson(e.getKey()));
			}
			jsonObject.put("children", childrenList);
			return jsonObject;
		}
	}
}
