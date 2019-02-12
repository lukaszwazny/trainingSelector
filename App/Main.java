package trainingSelector;

public class Main {

	public static void main(String[] args) {
		//read configuration
		Configuration conf = Parser.readConfiguration();
		//if configuration doesn't exist - make it default
		boolean changedConf = false;
		if(conf == null) {
			conf = new Configuration();
			changedConf = true;
		}
		
		Training[] trainings = new Training[conf.getLength()];
		for(int i = 0; i < trainings.length; i++) {
			trainings[i] = new Training(conf.getName(i));
		}
		
	}

}
