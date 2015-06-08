package ucas.dataMining.dao;

public class Node {
	public String name;
	
	public int classValue;
	public int getClassValue() {
		return classValue;
	}
	public void setClassValue(int classValue) {
		this.classValue = classValue;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Node(String name,int classValue)
	{
		this.name = name;
		this.classValue = classValue;
	}
}
