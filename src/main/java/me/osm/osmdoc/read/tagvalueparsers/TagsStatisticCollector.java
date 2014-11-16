package me.osm.osmdoc.read.tagvalueparsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.osm.osmdoc.model.Tag;

public class TagsStatisticCollector {
	
	private Logger log = LoggerFactory.getLogger(TagsStatisticCollector.class);

	public void failed(Tag tag, String rawValue, TagValueParser parser) {
		log.warn("Failed to parse tag key: '{}' value: '{}' with {}.", 
				new Object[]{ tag.getKey().getValue(), rawValue , parser.getClass().getSimpleName()});
	}

	public void success(Object pv, Tag tag, String rawValue,
			TagValueParser parser) {
		
	}

}
