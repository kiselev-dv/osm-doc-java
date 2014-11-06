package me.osm.osmdoc.read.tagvalueparsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpeningHoursParser implements TagValueParser {

	
	private static final String[] DAYS_ARRAY = new String[]{"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
	
	private static final Set<String> DAYS = new HashSet<String>(Arrays.asList(DAYS_ARRAY));

	@Override
	public Object parse(String rawValue) {
		
		
		JSONObject result = new JSONObject();
		
		if(rawValue.equals("24/7")) {
			result.put("24_7", true);
			return result;
		}

		if(rawValue.equals("sunrise-sunset")) {
			result.put("sunrise_sunset", true);
			return result;
		}

		//say hello to russian post :)
		//Mo-Fr 08:00-13:00,14:00-20:00; Sa 09:00-13:00,14:00-18:00; Su off
		//09:00-21:00
		//Mo-Su␣09:00-21:00
		String[] days = StringUtils.split(rawValue, ';');

		for(String byDay : days) {
			byDay = StringUtils.strip(byDay);
			if(byDay.length() > 2) {
				
				String day = byDay.substring(0, 2);
				
				if (DAYS.contains(day)) {
					
					String[] split = StringUtils.split(byDay, ' ');
					if(split.length < 2) {
						return null;
					}
					
					String[] daysOfWeek = getDays(split[0]);
					
					if(daysOfWeek == null) {
						return null;
					}
					
					int[] hours = parseHours(byDay);
					if(hours == null) {
						return null;
					}
						
					for(String d : daysOfWeek) {
						result.put(d, new JSONArray(hours));
					}
				}
				//just time
				else {
					int[] hours = parseHours(byDay);
					if(hours == null) {
						return null;
					}
					
					for(String d : DAYS_ARRAY) {
						result.put(d, new JSONArray(hours));
					}
				}
			}
			
		}
		
		
		
		return result;
	}

	private String[] getDays(String string) {
		if(string.contains("-")) {
			String[] split = StringUtils.split(string, '-');
			
			if(split.length != 2) {
				return null;
			}
			
			int firstIndex = ArrayUtils.indexOf(DAYS_ARRAY, split[0]);
			int lastIndex = ArrayUtils.indexOf(DAYS_ARRAY, split[1]);
			
			if(firstIndex == ArrayUtils.INDEX_NOT_FOUND || lastIndex == ArrayUtils.INDEX_NOT_FOUND ){
				return null;
			}
			
			List<String> result = new ArrayList<String>(7);
			
			int i = firstIndex;
			while(true) {
				result.add(DAYS_ARRAY[i]);
				
				if(i == lastIndex) {
					break;
				}
				
				i++;
				if(i == DAYS_ARRAY.length) {
					i = 0;
				}
			}
			
			return (String[]) result.toArray();
			
		}
		else {
			String strip = StringUtils.strip(string);
			if(DAYS.contains(strip)) {
				return new String[]{strip};
			}
		}
		
		return null;
	}

	private int[] parseHours(String string) {
		
		List<Integer> result = new ArrayList<Integer>();
		
		String[] periods = StringUtils.split(string, ',');
		for(String period : periods) {
			period = StringUtils.strip(period);
			
			for(String s : StringUtils.split(string, '-')) {
				result.add(Integer.decode(s.substring(0, 2)));
			}
		}
		
		return ArrayUtils.toPrimitive((Integer[]) result.toArray());
	}

}
