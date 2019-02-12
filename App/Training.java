package trainingSelector;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Training {
	
	String name;
	Date date;
	int entrances;

	public Training(String name) {
		this.name = name;
		this.date = new Date();
		this.entrances = 0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public String getDateString() {
		SimpleDateFormat ft = 
			      new SimpleDateFormat("E, d.M.y");
		return ft.format(this.date);
	}
	
	public int getEntrances() {
		return this.entrances;
	}
	
	public void addEntrance() {
		this.entrances++;
	}
	
	public void deleteEntrance() {
		if(this.entrances > 0)	//when entrances are 0, doesn't make sense to make them negative value
			this.entrances--;
	}
		
}
