package trainingSelector;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {
	List<String> names;	//list containing names of trainings
	
	//default
	public Configuration() {
		this.names = Arrays.asList(
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
				"Krav Maga");
	}
	
	//not default
	@JsonCreator
	public Configuration(@JsonProperty("names") List<String> names) {
		this.names = names;
	}
	
	public String getName(int i) {
		return this.names.get(i);
	}
	
	public String[] namesArray() {
		String[] temp = new String[names.size()];
		temp = names.toArray(temp);
		return temp;
	}
	
	public int length() {
		return this.names.size();
	}
	
	public void addName(String name) {
		this.names.add(name);
	}
	
	public void clear() {
		this.names.clear();
	}
	
}
