package com.tu.thesis.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.tu.thesis.entity.UniTimeSlots;
import com.tu.thesis.generator.DAYS;
import com.tu.thesis.generator.FEObjectForLecureGeneration;
import com.tu.thesis.generator.GeneratorImpl;

public class ConvertToObjectForLecture {

	public static List<FEObjectForLecureGeneration> convertor(String jsonStructure) {

		List<FEObjectForLecureGeneration> toBeReturned = new ArrayList<>();
		
		List<UniTimeSlots> allTimes = GeneratorImpl.timeSlots;

		String realJson = jsonStructure.substring(jsonStructure.indexOf('{'));
		System.out.println("REAL JSON: " + realJson);
		String[] array = realJson.split("}}");
		
		
		
		String firstPAram = "availableTime\":";
		for (int l=0; l<array.length; l++) {

			System.out.println("ARRAY AFTER SPLIT }}: " + array[l]);
			
			String times = array[l].substring((array[l].indexOf(firstPAram) + 15), array[l].indexOf(",\"isSet"));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1Times: " + times);

			Map<DAYS, Set<UniTimeSlots>> availableTime = new HashMap<>();
			if (times.contains("null")) {
				for (DAYS d : DAYS.values()) {
					availableTime.put(d, new HashSet<UniTimeSlots>(allTimes));
				}
			} else {
				
				times = times.substring(1, (times.length()-1)); // mahame kawichkite
				System.out.println("*********************************** " + times);
				String[] separateVal = times.split(",");
				
				DAYS currentDay = null;
				for (int i = 0; i < 5; i++) {
					Set<UniTimeSlots> dailySet = new HashSet<>();
					for (String z : separateVal) {
						System.out.println("Separate value: " + z);
						System.out.println("EEEEEEEEEEEEEEEee " + Character.getNumericValue(z.charAt(0)));
						for (DAYS d1 : DAYS.values()) {
							if (d1.ordinal() == (i)) {
								System.out.println("CURRENT DAY: ?????????????????????? " + d1);
								currentDay = d1;
							}
						}
						if (Character.getNumericValue(z.charAt(0)) == i) {
							String left = z.substring(1);
							System.out.println("QQQQQQQQQQQQQQQQQQQQQQQ " + left);
						
							dailySet.add(allTimes.get(Integer.valueOf(left) - 1));
						}
					}
					Set<UniTimeSlots> toBeAdded = new HashSet<>();
					for(int k=0; k<allTimes.size(); k++) {
						
						UniTimeSlots currK = allTimes.get(k);
						if(!dailySet.contains(currK)) {
							toBeAdded.add(new UniTimeSlots(currK.getId(), currK.getName()));				
						}
					}
									
					availableTime.put(currentDay, toBeAdded);
				}
			}
			
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ " + availableTime.toString());
			if(times.equals("null")) {
				array[l] = array[l].replace(times, availableTime.toString());
			} else {
				array[l] = array[l].replace("\"" +times, availableTime.toString());	
			}
			
			array[l] = array[l] + "}";
			
			array[l] = array[l].replaceAll("MON", "\"MON\"");
			array[l] = array[l].replaceAll("TUE", "\"TUE\"");
			array[l] = array[l].replaceAll("WED", "\"WED\"");
			array[l] = array[l].replaceAll("THU", "\"THU\"");
			array[l] = array[l].replaceAll("FR", "\"FR\"");
			array[l] = array[l].replaceAll("\",\"isSet\":false}", ",\"isSet\":false}");
			
		}
		
		
		
		for(String h: array) {
			System.out.println("222121212" + h);
			String valid = "";
			if(h.startsWith(",")) {
				valid = h.substring(1);
			} else {
				valid = h;
			}
				
			Gson g = new Gson(); 
			FEObjectForLecureGeneration p = g.fromJson(valid, FEObjectForLecureGeneration.class);
			System.out.println(p);
			toBeReturned.add(p);

		}

		return toBeReturned;

	}
}
