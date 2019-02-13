package trainingSelector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {
	String[] names;	//array containing names of trainings
	
	//default
	public Configuration() {
		this.names = new String[]{
				"Boks",
				"Muay Thai",
				"MMA pocz.",
				"MMA zaaw.",
				"Grappling",
				"BJJ zaaw.",
				"BJJ pocz.",
				"BJJ 8-12",
				"BJJ 4-7",
				"MDS",
				"Krav Maga"
		};
	}
	
	//not default
	@JsonCreator
	public Configuration(@JsonProperty("names") String[] names) {
		this.names = names;
	}
	
	public String getName(int i) {
		return this.names[i];
	}
	
	public int length() {
		return this.names.length;
	}
	
}
