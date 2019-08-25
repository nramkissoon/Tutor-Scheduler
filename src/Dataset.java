import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import net.datastructures.ArrayStack;


// Class that will store information from the data set, verifies delimiters using a stack and performs some other processing 
public class Dataset {
	private String filename;
	private String content; //holds contents of the text file
	private ArrayList<String> validRecords = new ArrayList<String>();
	private ArrayList<StudentRecord> records = new ArrayList<StudentRecord>();
	private ArrayList<String> submittedDuplicates = new ArrayList<String>();
	private int badRecordsDelim = 0; // count number of records in which there was a delimiter error
	private int badRecordsData = 0; // count number of records in which there was an error in day/time/id data
	
	
	public Dataset(String filePath) throws FileNotFoundException {
		filename = filePath.substring(5);
		Scanner s = new Scanner(new File(filePath));
		content = s.useDelimiter("\\Z").next();
		s.close();
		validateRecordDelimiters();
		parseRecords();
		processDuplicates();
	}
	
	
	// This is where stack is used 
	private void validateRecordDelimiters() {
		String openers = "(<["; 
		String closers = ")>]";
		String input = content;
		String[] separatedRecords = input.split("\\("); //separate text into a list of possible records
		for (String s: separatedRecords) {
			s = "(" + s; //lost the '(' in splitting --> add it back
			ArrayStack<Character> validateStack = new ArrayStack<Character>();
			boolean isValid = true; 
			for (char c: s.toCharArray()) {
				if (openers.indexOf(c) != -1) {
					switch (c) {
					case '(':
						if (validateStack.isEmpty()) {
							validateStack.push(c);
							break;
						}
						else {
							isValid = false; break;
						}
					case '<': // these rules are stricter than for general expression validation
						if (validateStack.isEmpty()) {
							isValid = false; break;
						}
						// a '<' can never follow a '[' or '<' in a record
						else if (validateStack.top() != '[' && validateStack.top() != '<') {
							validateStack.push(c);
							break;
						}
						else {
							isValid = false; break;
						}
					case '[': // similar logic as '<'
						if (validateStack.isEmpty()) {
							isValid = false; break;
						}
						else if (validateStack.top() != '(' && validateStack.top() != '[') {
							validateStack.push(c);
							break;
						}
						else {
							isValid = false; break;
						}
					}	
				}
				else if (closers.indexOf(c) != -1) {
					if (validateStack.isEmpty()) {
						isValid = false;
						break;
					}
					// conditional that checks if record has ended, ignore any delimiters after closing ')'
					if (c == ')' && closers.indexOf(c) == openers.indexOf(validateStack.top())) {
						validateStack.pop();
						break;
					}
					else if (closers.indexOf(c) != openers.indexOf(validateStack.pop())) {
						isValid = false;
						break;
					}
				}
			}
			if (!isValid) {
				badRecordsDelim ++;
				continue; //ignore invalid record, continue to next record
			}
			isValid = validateStack.isEmpty();
			if (isValid) {
				s = s.substring(0, s.indexOf(')') + 1); //cuts off random errors in between records
				validRecords.add(s); 
			}
		}
	}
	
	// method to parse records for data, these records have already been validated in terms of delimiters by previous method
	// creates StudentRecord objects by finding names and available time slots in the record
	private void parseRecords() throws NumberFormatException{
		for (String record: validRecords) {
			StudentRecord r = new StudentRecord();
			String id = "";
			char day = ' ';
			String time = "";
			boolean nameField = false;
			boolean dayField = false;
			boolean timeField = false;
			boolean error = false;
			for (char c: record.toCharArray()) {
				if (c == '(') {
					nameField = true;
				}
				
				// build id (name) string
				else if (nameField && c != '<') {
					id += c;
				}
				
				// check if end of nameField
				else if (c == '<' && nameField) {
					r = new StudentRecord(id);
					nameField = false;
					dayField = true;
					timeField = false;
				}
				
				// check if new dayField
				else if (c == '<') {
					nameField = false;
					dayField = true;
					timeField = false;
				}
				
				// get character value of day
				else if (dayField && c != '[') {
					ArrayList<Character> valid = new ArrayList<Character>(Arrays.asList('M', 'T', 'W', 'H', 'F'));
					if (!valid.contains(c)) { day = ' '; dayField = false; error = true;}
					else { day = c; }
				}
				
				// check if new TimeField in the same day (e.g. <M[1000][1200]>, gets the [1200])
				else if (c == '[') {
					nameField = false;
					dayField = false;
					timeField = true;
				}
				
				// gets the time 
				else if (timeField && c != ']') {
					time += c;
				}
				
				// end of timeField
				else if (c == ']') {
					int t = 0;
					try { t = Integer.parseInt(time); }
					catch (NumberFormatException e) {
						time = "";
						timeField = false;
						error = true;
					}
					if (t >= 900 && t <= 2100 && (t % 100 == 0)) {
						TimeSlot a = new TimeSlot(day, t);
						r.addAvailaleTime(a);
						time = "";
						timeField = false;
					}
					
					else { time = ""; timeField = false; error = true;} 
				}
				
				// end of dayField
				else if (c == '>') {
					day = ' ';
					dayField = false;
				}
			}
			
			// Check if record has a name associated to it
			if (error) { badRecordsData ++; }
			if (r.getId() != null && !r.getAvailableTimes().isEmpty() && r.getId() != "") {
				records.add(r);
			}
		}
	}
	
	
	//function to deal with duplicates:
	//duplicate records from the same student --> condense available times into 1 record, ignoring duplicate times
	private void processDuplicates() {
		ArrayList<StudentRecord> removedDuplicates = new ArrayList<StudentRecord>(); 
		for (StudentRecord record: records) {
			boolean isDuplicate = false;
			for (StudentRecord check: removedDuplicates) {
				if (record.getId().equals(check.getId())) {
					isDuplicate = true;
				}
			}
			if (!isDuplicate) {
				removedDuplicates.add(record);
			}
			else { // we have a duplicate
				if (!submittedDuplicates.contains(record.getId())) {
					submittedDuplicates.add(record.getId());
				}
				for (StudentRecord check: removedDuplicates) {
					if (record.getId().equals(check.getId())) {
						for (TimeSlot t: record.getAvailableTimes()) {
							check.addAvailaleTime(t); // add all available times from the duplicate to the new record
							//addAvailableTimes already has handling for duplicate Time Slots
						}
					}
				}
			}
		}
		records = new ArrayList<StudentRecord>();
		records = removedDuplicates;
	}
	
	// return the number of different time slots students of selected
	public int getNumDifferentTimeSlots() {
		int num = 0;
		ArrayList<String> hasSeen = new ArrayList<String>();
		for (StudentRecord record: records) {
			for (TimeSlot t: record.getAvailableTimes()) {
				if (!hasSeen.contains(t.toString())) {
					hasSeen.add(t.toString());
					num ++;
				}
			}
		}
		return num;
	}

	
	public String getFilename() { return filename; }
	public ArrayList<StudentRecord> getRecords() { return records; }
	public ArrayList<String> getValidRecords() { return validRecords; }
	public int getNumBadRecordsDelim() { return badRecordsDelim; } 
	public int getNumBadRecordsData() { return badRecordsData; } 
	public ArrayList<String> getSubmittedDuplicates() { return submittedDuplicates; }
}
