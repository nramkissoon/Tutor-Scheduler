import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CallingClass {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		
		final ArrayList<String> validInputs = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "q"));
		
		Scanner input = new Scanner(System.in);
		String userIn = "";
		
		while(!userIn.equals("q")) {
			System.out.println("\nEnter a dataset number (1-9) ('q' to quit): ");	
			userIn = input.nextLine(); 
			
			
			// error handling for invalid inputs
			if (!validInputs.contains(userIn)) {
				while (!validInputs.contains(userIn)) {
					System.out.println("\nInvalid Input!\nEnter a dataset number (1-9) ('q' to quit): ");	
					userIn = input.nextLine();
				}
			}
			
			// quit command
			if (userIn.equals("q")) {
				System.out.println("\nQuitting program... \n\nProgram has been ended.");
			}
			
			
			else {
				String datasetPath = "data/proj2_set" + userIn + ".txt";
				System.out.println("\n" + datasetPath.substring(5) + " selected.\n");
				
				// read in text data into a Dataset object
				Dataset selected = new Dataset(datasetPath);
				
				if (selected.getValidRecords().isEmpty()) {
					System.out.println(datasetPath + " contains no valid records.");
					continue;
				}
				
				// Code for getting n best tutoring times
				Scanner nBestTimes = new Scanner (System.in);
				String n = "";
				int nBest;
				boolean validN = false;
				System.out.println("How many time slots will tutoring be held?");
				System.out.println("Students are available for " + selected.getNumDifferentTimeSlots() + " different times.");
				System.out.println("Enter a number from: 1 to " + selected.getNumDifferentTimeSlots() + " or 'q' to quit:");
				n = nBestTimes.nextLine();
				
				if (n.equals("q")) {
					userIn = "q";
					System.out.println("\nQuitting program... \n\nProgram has been ended.");
				}
				
				else if (!n.equals("q")){
					// error handling for invalid n inputs 
					while (!validN) {
						validN = validateN(selected, n);
						if (validN) { continue; }
						n = nBestTimes.nextLine();
					}
					nBest = Integer.parseInt(n);
					
					// Create schedule object for outputting data
					Schedule s = new Schedule(selected.getRecords(), nBest);
					
					System.out.println(s.toString());
					
					
					System.out.println("\nBest times for tutoring: " + s.getNBestTimes().toString().substring(1, s.getNBestTimes().toString().length()-1));
					System.out.println("Students served at these times: " + s.getStudentsServedAtBestTime().toString().substring(1, s.getStudentsServedAtBestTime().toString().length() - 1));
					
					System.out.println();
					
					System.out.println(s.getStudentsServedAtBestTime().size() + " out of a total of " + selected.getRecords().size() + 
							" students can be tutored given these times.");
					System.out.println(selected.getNumBadRecordsDelim() + " records with bad delimiters were encountered when processing this data set.");
					System.out.println(selected.getNumBadRecordsData() + " records contained invalid day/time entries.");
					
					if (!selected.getSubmittedDuplicates().isEmpty()) {
						String str = "";
						for (String name: selected.getSubmittedDuplicates()) {
							str += name + ", ";
						}
						str = str.substring(0, str.length() - 2) + " submitted duplicate records.";
						System.out.println(str);
					}
					
					System.out.println("____________________________________________________________________________\n");
					
				}
				
			}
		}
			
	}
	
	// function for checking if an input is valid for selecting how many tutoring sessions will be held
	private static boolean validateN(Dataset d, String n) {
		int maxN = d.getNumDifferentTimeSlots();
		int num;
		try {
			num = Integer.parseInt(n);
		}
		catch (NumberFormatException err) {
			System.out.println("Invalid Input: Please input an integer from 1 to " + maxN);
			return false;
		}
		if(num <= maxN && num > 0) {
			return true;
		}
		System.out.println("Invalid Integer range: Please input an integer from 1 to " + maxN);
		return false;
	}
}
