package me.osm.osmdoc.read.tagvalueparsers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.osm.osmdoc.model.v2.Feature;
import me.osm.osmdoc.model.v2.Tag;
import me.osm.osmdoc.model.v2.Tag.Val;

public class LogTagsStatisticCollector implements TagsStatisticCollector {
	
	private Logger log = LoggerFactory.getLogger(LogTagsStatisticCollector.class);

	@Override
	public void failed(Tag tag, String rawValue, TagValueParser parser, List<Feature> poiClassess) {
		log.debug("Failed to parse tag key: '{}' value: '{}' with {}.", 
				new Object[]{ tag.getKey().getValue(), rawValue , parser.getClass().getSimpleName()});
	}

	@Override
	public void success(Object pv, Tag tag, Val val, String rawValue,
			TagValueParser parser, List<Feature> poiClassess) {
		
	}

}
