package ucas.dataMining.dao;
  
public class MovieKmeansEvaluate {  
	public float cohesiveness;
    public float separateness;
    public float silhouette;

    public MovieKmeansEvaluate(float cohesiveness, float separateness,
			float silhouette) {
		super();
		this.cohesiveness = cohesiveness;
		this.separateness = separateness;
		this.silhouette = silhouette;
	}
    
}//end-class