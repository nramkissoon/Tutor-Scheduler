import java.util.ArrayList;

//Class for storing availability for a specific student 
public class StudentRecord {
	private String id; 
	private ArrayList<TimeSlot> availableTimes;
	
	public StudentRecord(String n) {
		id = n;
		availableTimes = new ArrayList<TimeSlot>();
	}
	
	public StudentRecord() {
	}

	public void addAvailaleTime(TimeSlot t) {
		boolean isDuplicate = false; // ignores duplicate times in the same record, a student can game the system if this were not accounted for :)
		for (TimeSlot i: availableTimes) {
			if (i.getDay() == t.getDay() && i.getTime() == t.getTime()) { isDuplicate = true; }
		}
		if (!isDuplicate) { availableTimes.add(t); } 
	}
	public void removeAvailableTime(TimeSlot t) {
		availableTimes.remove(t);
	}
	
	public String getId() { return id; }
	public ArrayList<TimeSlot> getAvailableTimes() { return availableTimes; }
}
