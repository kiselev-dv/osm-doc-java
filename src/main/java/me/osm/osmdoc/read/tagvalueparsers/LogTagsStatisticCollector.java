package me.osm.osmdoc.read.tagvalueparsers;

import java.util.List;

import me.osm.osmdoc.model.Feature;
import me.osm.osmdoc.model.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTagsStatisticCollector implements TagsStatisticCollector {
	
	private Logger log = LoggerFactory.getLogger(LogTagsStatisticCollector.class);

	@Override
	public void failed(Tag tag, String rawValue, TagValueParser parser, List<Feature> poiClassess) {
		log.warn("Failed to parse tag key: '{}' value: '{}' with {}.", 
				new Object[]{ tag.getKey().getValue(), rawValue , parser.getClass().getSimpleName()});
	}

	@Override
	public void success(Object pv, Tag tag, String rawValue,
			TagValueParser parser, List<Feature> poiClassess) {
		
	}

}
