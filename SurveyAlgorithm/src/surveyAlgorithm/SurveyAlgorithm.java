package surveyAlgorithm;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SurveyAlgorithm {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Integer> intList = new ArrayList<Integer>();
		ArrayList<Integer> resultList;
		Scanner scan = new Scanner(System.in);
		String regex = "^[0-9]+$";
		Pattern pattern = Pattern.compile(regex);
		boolean end = false;
		System.out.println("Enter numbers into the list. Enter \"End\" to stop.");
		while(!end) {
			//enter numbers into the list until
			String input = scan.nextLine();
			if (input.equalsIgnoreCase("end")) {
				end = true;
			}
			else if (pattern.matcher(input).find()) {
				intList.add(new Integer(Integer.parseInt(input)));
			}
			else {
				System.out.println("Invalid entry. Please enter one number per line.");
			}
		}
		
		resultList = findDuplicates(intList);
		
		for (int i = 0; i < resultList.size(); i++) {
			System.out.println(resultList.get(i).intValue());
		}
	}

	private static ArrayList<Integer> findDuplicates (ArrayList<Integer> inputList) {
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		ArrayList<Integer> foundList = new ArrayList<Integer>();
		for (int i = 0; i < inputList.size(); i++) {
			boolean isFound = false;
			for (int j = 0; j < foundList.size(); j++) {
				if (foundList.get(j).intValue() == inputList.get(i).intValue()) {
					isFound = true;
					resultList.add(foundList.get(j));
					break;
				}
			}
			if (!isFound) {
				foundList.add(inputList.get(i));
			}
		}
		return resultList;
	}
}
