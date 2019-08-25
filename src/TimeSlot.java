
//Class for different time slots for tutoring 
public class TimeSlot {
	// Each tutoring session has a day and time associated to it
	private char day;
	private int time;
	
	public TimeSlot(char a, int i) {
		day = a;
		time = i;
	}
	
	public char getDay() { return day; }
	public int getTime() { return time ; }
	
	public String toString() {
		String s = "";
		switch (day) {
		case 'M':
			s += "Monday";
			break;
		case 'T':
			s += "Tuesday";
			break;
		case 'W':
			s += "Wednesday";
			break;
		case 'H':
			s += "Thursday";
			break;
		case 'F':
			s += "Friday";
			break;
		}
		
		// Time formatting for tutoring session start time
		String t1 = Integer.toString(time);
		if (t1.equals("900")) { t1 = "0" + t1; } 
		t1 = t1.substring(0,2) + ":" + t1.substring(2);
		
		// Time formatting for tutoring session end time
		String t2 = Integer.toString((time + 100));
		if (t2.equals("900")) { t2 = "0" + t2; } 
		t2 = t2.substring(0,2) + ":" + t2.substring(2);
		
		s += " from " + t1 + " to " + t2;
		return s;
	}
}
