package ucas.dataMining.dao;

public class SimilarUser {
	public String id1;
	public String id2;
	double socre;
	public String getId1() {
		return id1;
	}
	public void setId1(String id1) {
		this.id1 = id1;
	}
	public String getId2() {
		return id2;
	}
	public void setId2(String id2) {
		this.id2 = id2;
	}
	public double getSocre() {
		return socre;
	}
	public void setSocre(double socre) {
		this.socre = socre;
	}
	
	public SimilarUser(String id1,String id2,double score){
		this.id1 = id1;
		this.id2 = id2;
		this.socre = score;
		
	}

	public SimilarUser(){
		
	}
}
