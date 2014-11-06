package me.osm.osmdoc.read.tagvalueparsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.osm.osmdoc.model.Tag;
import me.osm.osmdoc.model.Tag.Val;
import me.osm.osmdoc.model.Tag.Val.MatchType;

public class EnumParser implements TagValueParser {
	
	private static final Logger log = LoggerFactory.getLogger(EnumParser.class);

	private Map<String, String> exacts = new HashMap<String, String>();
	private Map<String, String> contains = new HashMap<String, String>();
	private Map<Pattern, String> regexp = new HashMap<Pattern, String>();
	
	private String anyMatch = null;
	
	public EnumParser(Tag tag, boolean strict) {
		List<Val> values = tag.getVal();
		for(Val val : values) {
			String title = val.getTitle();
			MatchType match = val.getMatch();
			
			//TODO: multiple values
			if(MatchType.EXACT == match) {
				exacts.put(val.getValue(), val.getTitle());
			}
			else if(MatchType.CONTAINS == match || MatchType.WITH_NAMESPACE == match) {
				contains.put(val.getValue(), val.getTitle());
			}
			else if(MatchType.ANY == match) {
				anyMatch = title;
			}
			else if(MatchType.REGEXP == match) {
				try{
					regexp.put(Pattern.compile(val.getValue()), val.getTitle());
				}
				catch (PatternSyntaxException e) {
					log.warn("Failed to compile regexp for tag {}. Regexp: {}.", tag.getKey().getValue(), val.getValue());
				}
			}
		}
	}
	
	@Override
	public Object parse(String rawValue) {
		
		String lowerCase = rawValue.toLowerCase();
		String exact = exacts.get(lowerCase);
		if(exact != null) {
			return exact;
		}
		
		for(Entry<String, String> ce : contains.entrySet()) {
			if(lowerCase.contains(ce.getKey())) {
				return ce.getValue();
			}
		}

		for(Entry<Pattern, String> re : regexp.entrySet()) {
			if(re.getKey().matcher(lowerCase).find()) {
				return re.getValue();
			}
		}
		
		if(anyMatch != null && !lowerCase.equals("no") && lowerCase.equals("false")) {
			return anyMatch;
		}
		
		return null;
	}

}
