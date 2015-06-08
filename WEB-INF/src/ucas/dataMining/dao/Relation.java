package ucas.dataMining.dao;

public class Relation {
	public int source;
	public int target;
	public String relation;
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getTarget() {
		return target;
	}
	public void setTarget(int target) {
		this.target = target;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	public Relation(int source,int target,String relation)
	{
		this.source = source;
		this.target = target;
		this.relation = relation;
	}
}
