package ucas.dataMining.dao;

import java.util.Map;

public class User {
	public String id;
	
	public int age;
	public String gender;
	public String occupation;
	public String zipcode;
	public Map<String, Integer> ratings;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public Map<String, Integer> getRatings() {
		return ratings;
	}
	public void setRatings(Map<String, Integer> ratings) {
		this.ratings = ratings;
	}
	
	public User(String id,int age,String gender, String occupation,String zipcode)
	{
		this.id = id;
		this.age = age;
		this.gender = gender;
		this.occupation = occupation;
		this.zipcode = zipcode;
	}
	public User()
	{}
	
}
