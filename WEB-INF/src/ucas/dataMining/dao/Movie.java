package ucas.dataMining.dao;

import java.util.List;
import java.util.Map;
/*
 * 
 */
public class Movie {
	
	public String id;
	public String name;
	public String showTime;
	public List<Integer> tags; 
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShowTime() {
		return showTime;
	}
	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}
	public List<Integer> getTags() {
		return tags;
	}
	public void setTags(List<Integer> tags) {
		this.tags = tags;
	}
	
	public Movie(String id,String name,String showTime)
	{
		this.id = id;
		this.name = name;
		this.showTime = showTime;
	}
	public Movie()
	{
		
	}
	
	public String getMovieContent()
	{
		String content="";
		content+="m_id:"+this.id+"\n";
		content+="m_name:"+this.name+"\n";
		String[] features ={ "Mystery", "Romance", "Sci-Fi", "Fantasy", "unknown", "Horror", "Film-Noir", "Crime", "Drama", "Children\'s", "Animation",
				"War", "Adventure", "Action", "Comedy", "Documentary", "Musical", "Thriller", "Western"};
		content+="feature:\n";
		for(int i=0;i<features.length;i++)
		{
			content+=features[i]+":"+this.tags.get(i).toString()+" ";
		}
		
		return content;
	}
}
