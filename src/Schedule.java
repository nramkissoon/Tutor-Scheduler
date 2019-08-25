import java.util.ArrayList;

//Class containing 2D-array Data structure for storing all student availabilities
public class Schedule {
	
	// this is a 2D-array with each sell containing a list of strings (names of students)
	@SuppressWarnings("unchecked")
	private ArrayList<String>[][] schedule = new ArrayList[5][13]; //5 days, 13 hours
	
	private ArrayList<String> studentsServedAtBestTime = new ArrayList<String>(); // contains the names of students served at the best times
	
	private ArrayList<TimeSlot> nBestTimes = new ArrayList<TimeSlot>(); // contains the best times that serve the most students
	
	// constructor
	public Schedule(ArrayList<StudentRecord> records, int n) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 13; j++) {
				schedule[i][j] = new ArrayList<String>();
			}
		}
		
		// populate 2D array with data form data set
		for (StudentRecord record: records) {
			for (TimeSlot t: record.getAvailableTimes()) {
				int[] indices = indexMapper(t);
				schedule[indices[0]][indices[1]].add(record.getId());
			}
		}
		
		determineNBestTime(n);
	}
	
	// function that analyzes 2D array and determines optimal tutoring times based on number of students available
	private void determineNBestTime(int n) {
		ArrayList<String> hasSeen = new ArrayList<String>(); // ensures we do not double count any time slots
		while (n > 0) {
			int mostServed = 0;
			int indexI = 0;
			int indexJ = 0;
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 13; j++) {
					// the '>' sign ensures the earliest time is chosen if there is a tie
					// would not be the case with a '>='
					if (schedule[i][j].size() > mostServed && !hasSeen.contains(timeSlotMapper(i,j).toString())) {
						mostServed = schedule[i][j].size();
						indexI = i;
						indexJ = j;
					}
				}
			}
			TimeSlot best = timeSlotMapper(indexI, indexJ);
			hasSeen.add(best.toString());
			for (String name: schedule[indexI][indexJ]) { // ensures we do not double count students available at 2 or more "best" times
				if (!studentsServedAtBestTime.contains(name)) {
					studentsServedAtBestTime.add(name);
				}
			}
			nBestTimes.add(best);
			n--;
		}
	}
	
	// function to map indices of the 2D array onto day/time values
	private static TimeSlot timeSlotMapper(int indexI, int indexJ) {
		char day = ' ';
		int time;
		switch (indexI) { //map integer indices onto characters
		case 0:
			day = 'M';
			break;
		case 1:
			day = 'T';
			break;
		case 2:
			day = 'W';
			break;
		case 3:
			day = 'H';
			break;
		case 4:
			day = 'F';
			break;
		}
		time = (indexJ * 100) + 900; //map integer indices onto time 
		TimeSlot t = new TimeSlot(day, time);
		return t;
	}
	
	// function to map day/time values onto indicies of 2D array
	private static int[] indexMapper(TimeSlot t) {
		char day = t.getDay();
		int time = t.getTime();
		int[] mapping = new int[2];
		switch (day) { //map day values onto integer indices
		case 'M':
			mapping[0] = 0;
			break;
		case 'T':
			mapping[0] = 1;
			break;
		case 'W':
			mapping[0] = 2;
			break;
		case 'H':
			mapping[0] = 3;
			break;
		case 'F':
			mapping[0] = 4;
			break;
		}
		mapping[1] = (time - 900)/100; //map time values onto integer indices
		return mapping;
	}
	
	
	public ArrayList<TimeSlot> getNBestTimes() { return nBestTimes; }
	public ArrayList<String> getStudentsServedAtBestTime() { return studentsServedAtBestTime; }
	
	
	// Print data from 2D array
	public String toString() {
		String day = "";
		int time;
		String s = "";
		s += "\n";
		s += "\nStudent Schedules:\n";
		for (int i = 0; i < 5; i++) {
			switch(i) {
			case 0:
				day = "Monday";
				break;
			case 1:
				day = "Tuesday";
				break;
			case 2:
				day = "Wednesday";
				break;
			case 3:
				day = "Thursday";
				break;
			case 4:
				day = "Friday";
				break;
			}
			s += "______________________________\n\n";
			s += day + "\n\n";
			s += "Time  | Available for Tutoring\n";
			s += "------|-----------------------\n";
			for (int j = 0; j < 13; j++) {
				time = (j * 100) + 900;
				
				// Time formatting
				String t = Integer.toString(time);
				if (t.equals("900")) { t = "0" + t; } 
				t = t.substring(0,2) + ":" + t.substring(2);
				
				if (schedule[i][j].size() != 0) {
					s += t + " | " + schedule[i][j].toString().substring(1, schedule[i][j].toString().length() - 1) + "\n";
				}
			}
			s += "\n";
				
		}
		s += "____________________________________________________";
		return s;
	}
}
