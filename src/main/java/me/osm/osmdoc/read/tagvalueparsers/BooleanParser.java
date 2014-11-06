package me.osm.osmdoc.read.tagvalueparsers;


public class BooleanParser implements TagValueParser {

	@Override
	public Object parse(String rawValue) {
		
		String lowerCase = rawValue.toLowerCase();
		
		if("yes".equals(lowerCase) || "true".equals(lowerCase)) {
			return Boolean.TRUE;
		}
		else if("no".equals(lowerCase) || "false".equals(lowerCase)) {
			return Boolean.FALSE;
		}
		
		return null;
	}

}
