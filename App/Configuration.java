package trainingSelector;

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
	public Configuration(String[] names) {
		this.names = names;
	}
	
	public String getName(int i) {
		return this.names[i];
	}
	
	public int getLength() {
		return this.names.length;
	}
	
}
