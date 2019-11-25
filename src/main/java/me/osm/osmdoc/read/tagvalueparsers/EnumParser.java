package me.osm.osmdoc.read.tagvalueparsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.osm.osmdoc.model.v2.Tag;
import me.osm.osmdoc.model.v2.Tag.Val;

public class EnumParser implements TagValueParser {
	
	private static final Logger log = LoggerFactory.getLogger(EnumParser.class);

	private Map<String, Val> exacts = new HashMap<>();
	private Map<String, Val> contains = new HashMap<>();
	private Map<Pattern, Val> regexp = new HashMap<>();
	
	private String anyMatch = null;
	
	public EnumParser(Tag tag, boolean strict) {
		List<Val> values = tag.getVal();
		for(Val val : values) {
			String match = val.getMatch();
			
			//TODO: multiple values
			if(match.equals("exact") ) {
				exacts.put(val.getValue().toLowerCase(), val);
			}
			else if(match.equals("contains") || match.equals("with_namespace")) {
				contains.put(val.getValue().toLowerCase(), val);
			}
			else if(match.equals("any")) {
				anyMatch = val.getValue();
			}
			else if(match.equals("regexp")) {
				try{
					regexp.put(Pattern.compile(val.getValue()), val);
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
		Val exact = exacts.get(lowerCase);
		if(exact != null) {
			return exact;
		}
		
		for(Entry<String, Val> ce : contains.entrySet()) {
			if(lowerCase.contains(ce.getKey())) {
				return ce.getValue();
			}
		}

		for(Entry<Pattern, Val> re : regexp.entrySet()) {
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
