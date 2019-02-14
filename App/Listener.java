package trainingSelector;

import java.util.Scanner;

public class Listener extends Main implements Runnable {
	
	//thread listening to input stream
	public void run() {
		Scanner in = new Scanner(System.in);
		int i;
		while(listen) {
			if(in.hasNextInt()) {
				i = in.nextInt();
				if(i < trainings.size()) {
					//adding entrance to training specified by index from input stream
					trainings.get(i).addEntrance();
					table.refresh();
				}
	        }
		}
		in.close();
	}
	
}
