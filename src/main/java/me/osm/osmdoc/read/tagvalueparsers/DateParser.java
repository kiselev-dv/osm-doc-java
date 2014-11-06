package me.osm.osmdoc.read.tagvalueparsers;

import java.util.Date;

import me.osm.osmdoc.util.Strtotime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateParser implements TagValueParser {

	Logger log = LoggerFactory.getLogger(DateParser.class);
	
	@Override
	public Object parse(String rawValue) {
		
		Date date = Strtotime.strtotime(rawValue);
		if(date == null) {
			log.warn("Failed to parse date: {}", rawValue);
		}
		return date;
	}

}
