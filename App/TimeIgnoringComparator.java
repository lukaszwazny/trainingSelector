package app;

import java.util.Calendar;
import java.util.Date;

public class TimeIgnoringComparator {
	
	//checking if two date objects are the same ignoring time
	  public static boolean isSameDay(Date d1, Date d2) {
		  Calendar c1 = Calendar.getInstance();
		  Calendar c2 = Calendar.getInstance();
		  c1.setTime(d1);
		  c1.setTime(d2);
	    if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
	    		c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
	    		c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
	    	return true;
	    } else {
	    	return false;
	    }
	  }
	  
}
