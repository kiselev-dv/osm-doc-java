package me.osm.osmdoc.read;

import static me.osm.osmdoc.localization.L10n.tr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.osm.osmdoc.localization.L10n;
import me.osm.osmdoc.model.Choise;
import me.osm.osmdoc.model.Feature;
import me.osm.osmdoc.model.Fref;
import me.osm.osmdoc.model.Group;
import me.osm.osmdoc.model.Hierarchy;
import me.osm.osmdoc.model.LangString;
import me.osm.osmdoc.model.Tag;
import me.osm.osmdoc.model.Tag.Val;
import me.osm.osmdoc.model.Tags;
import me.osm.osmdoc.model.Trait;
import me.osm.osmdoc.read.tagvalueparsers.OpeningHoursParser;
import me.osm.osmdoc.read.tagvalueparsers.TagValueParser;
import me.osm.osmdoc.read.tagvalueparsers.TagValueParsersFactory;
import me.osm.osmdoc.read.tagvalueparsers.TagsStatisticCollector;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSMDocFacade {
	
	private static final Logger log = LoggerFactory.getLogger(OSMDocFacade.class);
	
	private TagsDecisionTreeImpl dTree;
	private DOCReader docReader;
	private Set<Feature> excludedFeatures;
	private Map<String, Feature> featureByName = new HashMap<>();
	
	/*
	 * tagKey -> [tagVal, tagVal, tagVal] 
	 * 	  tagVal -> [featureType, featureType] 
	 * 
	 * eg:
	 * 
	 * amenity:[parking, place_of_worship]
	 * 
	 * place_of_worship: [place_of_worship, place_of_worship_christian, place_of_worship_jewish, ...]
	 * 
	 */
	private Map<String, Map<String, List<Feature>>> key2values = new HashMap<String, Map<String,List<Feature>>>();
	
	public OSMDocFacade(DOCReader reader, List<String> exclude) {
		
		docReader = reader;
		
		List<Feature> features = docReader.getFeatures();
		
		excludedFeatures = getBranches(exclude);
		
		for(Feature f : features) {
			
			if(excludedFeatures.contains(f)){
				continue;
			}
			
			featureByName.put(f.getName(), f);
			
			//synonyms
			for(Tags synonym : f.getTags()) {
				
				//our feature should match all of them
				List<Tag> tagsCombination = synonym.getTag();
				
				for(Tag t : tagsCombination) {
					//TODO: support key match
					String tagKey = t.getKey().getValue();
					if(!t.isExclude()) {
						if(!key2values.containsKey(tagKey)) {
							key2values.put(tagKey, new HashMap<String, List<Feature>>());
						}
						
						for(Tag.Val val : t.getVal()) {
							String tagVal = val.getValue();
							if(key2values.get(tagKey).get(tagVal) == null) {
								key2values.get(tagKey).put(tagVal, new ArrayList<Feature>());
							}
							
							key2values.get(tagKey).get(tagVal).add(f);
						}
					}
				}
			}
		}
		
		dTree = new TagsDecisionTreeImpl(key2values);
	}

	public Set<Feature> getBranches(List<String> exclude) {
		String hierarcyName = null;
		boolean singleHierarcy = docReader.listHierarchies().size() == 1;
		if(singleHierarcy) {
			hierarcyName = docReader.listHierarchies().get(0).getName();
		}
		
		return getBranches(exclude, hierarcyName, singleHierarcy);
	}
	
	private Set<Feature> getBranches(List<String> exclude, String hierarcyName,
			boolean singleHierarcy) {
		Set<Feature> result = new HashSet<Feature>();
		if(exclude != null) {
			for(String ex : exclude) {
				String[] split = StringUtils.split(ex, ':');
				if(singleHierarcy && split.length == 1) {
					result.addAll(docReader.getHierarcyBranch(hierarcyName, ex));
				}
				else {
					result.addAll(docReader.getHierarcyBranch(split[0], split[1]));
				}
			}
		}
		return result;
	}
	
	public JSONObject getHierarchyJSON(String hierarchy, Locale lang) {

		Hierarchy h = docReader.getHierarchy(hierarchy);
		
		if(h != null) {
			JSONObject result = new JSONObject();
			List<JSONObject> groups = new ArrayList<JSONObject>();
			for(Group g : h.getGroup()) {
				JSONObject gjs = new JSONObject();
				gjs.put("name", g.getName());
				gjs.put("icon", g.getIcon());
				gjs.put("title", tr(g.getTitle(), lang));
				groups.add(gjs);
				
				dfsGroup(gjs, g.getGroup(), g.getFref(), lang);
			}
			
			result.put("groups", new JSONArray(groups));
			return result;
		}
		
		return null;
		
	}

	private void dfsGroup(JSONObject gjs, List<Group> groups, List<Fref> fref, Locale lang) {
		
		List<JSONObject> childGroups = new ArrayList<JSONObject>();
		
		for(Group g : groups) {
			JSONObject childG = new JSONObject();
			childG.put("type", "group");
			childG.put("name", g.getName());
			childG.put("icon", g.getIcon());
			childG.put("title", tr(g.getTitle(), lang));
			childGroups.add(childG);

			dfsGroup(childG, g.getGroup(), g.getFref(), lang);
		}
		
		gjs.put("groups", new JSONArray(childGroups));
		
		List<JSONObject> childFeatures = new ArrayList<JSONObject>();
		for(Fref f : fref) {
			Feature feature = getFeature(f.getRef());
			JSONObject childFeature = new JSONObject();
			childFeature.put("type", "feature");
			childFeature.put("name", feature.getName());
			childFeature.put("icon", feature.getIcon());
			childFeature.put("title", tr(feature.getTitle(), lang));
			childFeature.put("description", tr(feature.getDescription(), lang));
			
			childFeatures.add(childFeature);
		}
		
		gjs.put("features", new JSONArray(childFeatures));
		
	}

	public TagsDecisionTreeImpl getPoiClassificator() {
		return dTree;
	}

	public Feature getFeature(String poiClass) {
		return featureByName.get(poiClass);
	}

	public String getTranslatedTitle(Feature fClass, Locale lang) {
		return L10n.tr(fClass.getTitle(), lang);
	}

	public String getTranslatedTitle(Feature fClass, Tag td, Locale lang) {
		return L10n.tr(td.getTitle(), lang);
	}

	public String getTranslatedTitle(Feature fClass, Val valuePattern,
			Locale lang) {
		return L10n.tr(valuePattern.getTitle(), lang);
	}

	public List<String> listPoiClassNames(Feature f) {
		
		List<String> result = new ArrayList<String>();
		for(String lang : L10n.supported) {
			result.add(getTranslatedTitle(f, Locale.forLanguageTag(lang)));
			for(LangString alias : f.getAlias()) {
				result.add(alias.getValue());
			}
		}
		
		return result;
	}

	public LinkedHashMap<String, Tag> getMoreTags(Feature f) {
		LinkedHashMap<String, Tag> result = new LinkedHashMap<>();

		//Get more tags from traits
		for(String trait : f.getTrait()) {
			collectMoreTags(trait, new HashSet<String>(), result);
		}
		
		//Get more tags from feture
		if(f.getMoreTags() != null) {
			for(Tag tag : f.getMoreTags().getTag()) {
				result.put(tag.getKey().getValue(), tag);
			}
		}
		
		return result;
	}

	public JSONObject parseMoreTags(Feature f, Map<String, String> tags, 
			TagsStatisticCollector statistics) {
		LinkedHashMap<String, Tag> moreTags = getMoreTags(f);
		JSONObject result = new JSONObject();
		
		for(Entry<String, Tag> template : moreTags.entrySet()) {

			String key = template.getKey();
			Tag tag = template.getValue();
			
			String rawValue = tags.get(key);

			//value founded
			if(StringUtils.isNotBlank(rawValue)) {
				TagValueParser parser = getTagValueParser(tag);
				
				try{
					Object parsedValue = null;
					
					if(parser instanceof OpeningHoursParser || rawValue.indexOf(';') < 0) {
						parsedValue = parser.parse(rawValue);
					}
					//Multiple values
					else {
						parsedValue = new JSONArray();
						for(String v : StringUtils.split(rawValue, ';')) {
							Object pv = parser.parse(v);
							if(pv != null) {
								((JSONArray)parsedValue).put(pv);
							}
							else {
								log.warn("Failed to parse {} from {} with {}.", 
										new Object[]{pv, rawValue, parser.getClass().getSimpleName()});
							}
						}
						
						if(((JSONArray)parsedValue).length() == 0) {
							parsedValue = 0;
						}
					}
					
					if(parsedValue != null) {
						result.put(key, parsedValue);
					}
					else {
						log.warn("Failed to parse {} with {}.", rawValue , parser.getClass().getSimpleName());
					}
				}
				catch (Throwable t) {
					log.warn("Failed to parse {} with {}.", rawValue , parser.getClass().getSimpleName());
				}
			}
		}
		
		return result;
	}

	private TagValueParser getTagValueParser(Tag tag) {
		return TagValueParsersFactory.getParser(tag);
	}

	private void collectMoreTags(String traitName, HashSet<String> visitedTraits,
			LinkedHashMap<String, Tag> tags) {
		
		Trait trait = docReader.getTraits().get(traitName);
		if(trait != null && visitedTraits.add(traitName)) {
			for(String extend : trait.getExtends()) {
				collectMoreTags(extend, visitedTraits, tags);
			}
			
			if(trait.getMoreTags() != null) {
				for(Tag tag : trait.getMoreTags().getTag()) {
					if(!tags.containsKey(tag.getKey().getValue())) {
						tags.put(tag.getKey().getValue(), tag);
					}
				}
				for(Choise ch : trait.getMoreTags().getChoise()) {
					for(Tag tag : ch.getTag()) {
						if(!tags.containsKey(tag.getKey().getValue())) {
							tags.put(tag.getKey().getValue(), tag);
						}
					}
				}
				
			}
		}
	}
	
}
