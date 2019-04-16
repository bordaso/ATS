package util;

import javafx.scene.control.TextField;

public class TimeCalculator {
	
	
	public static String increaseTime(TextField input, boolean... onlyMinute) {
		// 19:17:42 8db 0-7

		StringBuilder textTime = new StringBuilder(input.getText());
		Integer hour = Integer.valueOf(textTime.substring(0, 2));
		Integer mins = Integer.valueOf(textTime.substring(3, 5));
		Integer secs = Integer.valueOf(textTime.substring(6));
		
		String newHour = secs == 59 && mins == 59
				? String.valueOf(hour + 1).length() < 2 ? "0" + String.valueOf(hour + 1)
						: hour == 23 ? "00" : String.valueOf(hour + 1)
				: String.valueOf(hour).length() < 2 ? "0" + String.valueOf(hour) : String.valueOf(hour);

		if (onlyMinute.length > 0) {

			String newMin = mins == 59 ? "00"
					: String.valueOf(mins + 1).length() < 2 ? "0" + String.valueOf(mins + 1) : String.valueOf(mins + 1);
					
			 newHour = mins == 00
							? String.valueOf(hour + 1).length() < 2 ? "0" + String.valueOf(hour + 1)
									: hour == 23 ? "00" : String.valueOf(hour + 1)
							: String.valueOf(hour).length() < 2 ? "0" + String.valueOf(hour) : String.valueOf(hour);

			textTime.replace(6, 8, "00");
			textTime.replace(3, 5, newMin);
			textTime.replace(0, 2, newHour);

			return textTime.toString();
		}
		
		String newMin = secs == 59
				? String.valueOf(mins + 1).length() < 2 ? "0" + String.valueOf(mins + 1)
						: mins == 59 ? "00" : String.valueOf(mins + 1)
				: String.valueOf(mins).length() < 2 ? "0" + String.valueOf(mins) : String.valueOf(mins);
				

		String newSecs = secs == 59 ? "00"
				: String.valueOf(secs + 1).length() < 2 ? "0" + String.valueOf(secs + 1) : String.valueOf(secs + 1);


		textTime.replace(6, 8, newSecs);
		textTime.replace(3, 5, newMin);
		textTime.replace(0, 2, newHour);

		return textTime.toString();
	}

	public static String decreaseTime(TextField input, boolean... onlyMinute) {
		StringBuilder textTime = new StringBuilder(input.getText());
		Integer hour = Integer.valueOf(textTime.substring(0, 2));
		Integer mins = Integer.valueOf(textTime.substring(3, 5));
		Integer secs = Integer.valueOf(textTime.substring(6));
		
		String newHour = secs == 00 && mins == 00
				? String.valueOf(hour - 1).length() < 2 ? "0" + String.valueOf(hour - 1)
						: hour == 00 ? "23" : String.valueOf(hour - 1)
				: String.valueOf(hour).length() < 2 ? "0" + String.valueOf(hour) : String.valueOf(hour);

		if (onlyMinute.length > 0) {

			String newMin = mins == 00 ? "59"
					: String.valueOf(mins - 1).length() < 2 ? "0" + String.valueOf(mins - 1) : String.valueOf(mins - 1);

			textTime.replace(6, 8, "00");
			textTime.replace(3, 5, newMin);
			textTime.replace(0, 2, newHour);

			return textTime.toString();
		}

		String newMin = secs == 00
				? String.valueOf(mins - 1).length() < 2 ? "0" + String.valueOf(mins - 1)
						: mins == 00 ? "59" : String.valueOf(mins - 1)
				: String.valueOf(mins).length() < 2 ? "0" + String.valueOf(mins) : String.valueOf(mins);
		
		String newSecs = (secs == 0) || (secs == 00) ? "59"
				: String.valueOf(secs - 1).length() < 2 ? "0" + String.valueOf(secs - 1) : String.valueOf(secs - 1);

	

		textTime.replace(6, 8, newSecs);
		textTime.replace(3, 5, newMin);
		textTime.replace(0, 2, newHour);

		return textTime.toString();
	}


}
