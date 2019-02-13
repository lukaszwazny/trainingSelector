package trainingSelector;

import java.util.Scanner;

public class Listener extends Main implements Runnable {
	public void run() {
		Scanner in = new Scanner(System.in);
		int i;
		while(listen) {
			if(in.hasNextInt()) {
				i = in.nextInt();
				trainings.get(i).addEntrance();
				table.refresh();
				//System.out.println(trainings.get(i).getName() + ": " + trainings.get(i).getEntrances());
	        }
		}
		in.close();
	}
}
