package me.osm.osmdoc.read.tagvalueparsers;

import org.apache.commons.lang3.StringUtils;


public class BooleanParser implements TagValueParser {
	
	private static final boolean NOT_EMPTY_IS_TRUE = true;

	@Override
	public Object parse(String rawValue) {
		
		String lowerCase = rawValue.toLowerCase();
		
		if("yes".equals(lowerCase) || "true".equals(lowerCase)) {
			return Boolean.TRUE;
		}
		else if("no".equals(lowerCase) || "false".equals(lowerCase)) {
			return Boolean.FALSE;
		}
		
		if("unknown".equals(lowerCase)) {
			return null;
		}
		
		if(NOT_EMPTY_IS_TRUE && StringUtils.isNotBlank(lowerCase)) {
			return true;
		}
		
		return null;
	}

}
