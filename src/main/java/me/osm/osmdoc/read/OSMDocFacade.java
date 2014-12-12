package me.osm.osmdoc.read;

import static me.osm.osmdoc.localization.L10n.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
			if(feature != null) {
				JSONObject childFeature = featureAsJSON(feature, lang);

				childFeature.put("icon", feature.getIcon());
				childFeature.put("description", tr(feature.getDescription(), lang));
				
				childFeatures.add(childFeature);
			}
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

	public List<String> listPoiClassNames(Collection<Feature> poiClassess) {
		
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for(Feature f : poiClassess) {
			for(String lang : L10n.supported) {
				//Translated title
				result.add(getTranslatedTitle(f, Locale.forLanguageTag(lang)));
			}
		}
		
		return new ArrayList<String>(result);
		
	}

	public LinkedHashMap<String, Tag> getMoreTags(Collection<Feature> poiClassess) {
		
		return getMoreTags(poiClassess, new HashSet<String>());
	}
	
	public LinkedHashMap<String, Tag> getMoreTags(Collection<Feature> poiClassess, Set<String> visitedTraits) {
		
		LinkedHashMap<String, Tag> result = new LinkedHashMap<>();

		for(Feature pc : poiClassess) {
			//Get more tags from traits
			for(String trait : pc.getTrait()) {
				trait = StringUtils.strip(trait);
				collectMoreTags(trait, new HashSet<String>(), result);
			}
			
			//Get more tags from feture
			if(pc.getMoreTags() != null) {
				for(Tag tag : pc.getMoreTags().getTag()) {
					result.put(tag.getKey().getValue(), tag);
				}
			}
		}
		
		return result;
	}

	public JSONObject parseMoreTags(List<Feature> poiClassess, JSONObject properties, 
			TagsStatisticCollector statistics, Map<String, List<Val>> fillVals) {
		
		LinkedHashMap<String, Tag> moreTags = getMoreTags(poiClassess);
		JSONObject result = new JSONObject();
		
		for(Entry<String, Tag> template : moreTags.entrySet()) {

			String key = template.getKey();
			Tag tag = template.getValue();
			
			Object o = properties.opt(key);
			String rawValue = (o != null ? o.toString() : null);

			//value founded
			if(StringUtils.isNotBlank(rawValue)) {
				TagValueParser parser = getTagValueParser(tag);
				
				try{
					Object parsedValue = null;
					
					// Symbol ';' already used in working_hours to split
					// different time periods
					if(parser instanceof OpeningHoursParser || rawValue.indexOf(';') < 0) {
						
						parsedValue = parser.parse(rawValue);
						
						
						if(parsedValue != null) {
							Val val = null;
							
							if(parsedValue instanceof Val) {
								val = (Val) parsedValue;
								parsedValue = ((Val) parsedValue).getValue();
							}

							statistics.success(parsedValue, tag, val, rawValue, parser, poiClassess);
							
							fillVals(fillVals, tag, parsedValue);
						}
						
					}
					//Multiple values
					else {
						parsedValue = new JSONArray();
						
						//For now we use ; as values separator
						for(String v : StringUtils.split(rawValue, ';')) {
							Object pv = parser.parse(v);
							if(pv != null) {
								
								Val val = null;
								
								if(pv instanceof Val) {
									val = (Val) pv;
									pv = ((Val) pv).getValue();
								}
								
								((JSONArray)parsedValue).put(pv);
								
								statistics.success(pv, tag, val, rawValue, parser, poiClassess);
								
								fillVals(fillVals, tag, pv);
							}
							else {
								statistics.failed(tag, rawValue, parser, poiClassess);
							}
						}
						
						if(((JSONArray)parsedValue).length() == 0) {
							parsedValue = null;
						}
					}
					
					if(parsedValue != null) {
						result.put(key, parsedValue);
					}
					else {
						statistics.failed(tag, rawValue, parser, poiClassess);
					}
				}
				catch (Throwable t) {
					statistics.failed(tag, rawValue, parser, poiClassess);
				}
			}
		}
		
		return result;
	}

	private void fillVals(Map<String, List<Val>> fillVals, Tag tag,
			Object parsedValue) {
		
		if(parsedValue instanceof Val && fillVals != null) {
			
			String keyKey = tag.getKey().getValue();
			
			if(fillVals.get(keyKey) == null) {
				fillVals.put(keyKey, new ArrayList<Tag.Val>());
			}
			
			fillVals.get(keyKey).add((Val) parsedValue);
		}
	}

	private TagValueParser getTagValueParser(Tag tag) {
		return TagValueParsersFactory.getParser(tag);
	}

	private void collectMoreTags(String traitName, HashSet<String> visitedTraits,
			LinkedHashMap<String, Tag> tags) {
		
		Trait trait = docReader.getTraits().get(traitName);
		if(trait != null && visitedTraits.add(traitName)) {
			for(String extend : trait.getExtends()) {
				extend = StringUtils.strip(extend);
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
	
	public JSONObject featureAsJSON(Feature f, Locale lang) {
		
		JSONObject result = new JSONObject();
		
		result.put("name", f.getName());
		result.put("title", f.getTitle());
		result.put("translated_title", getTranslatedTitle(f, lang));
		
		JSONArray keywords = new JSONArray();
		for(LangString ls : f.getKeyword()) {
			String keywordLang = ls.getLang();
			String keyword = StringUtils.strip(ls.getValue()); 
			
			JSONObject keywordJS = new JSONObject();
			keywordJS.put("alias", keyword);
			keywordJS.put("lang", keywordLang);
			
			keywords.put(keywordJS);
		}
		result.put("keywords", keywords);
		
		LinkedHashSet<String> traits = new LinkedHashSet<String>();
		LinkedHashMap<String, Tag> moreTags = getMoreTags(Arrays.asList(f), traits);

		result.put("traits", new JSONArray(traits));
		
		JSONObject moreTagsJS = new JSONObject();
		for(Entry<String, Tag> tagE : moreTags.entrySet()) {
			moreTagsJS.put(tagE.getKey(), translateTagValues(tagE.getValue(), lang));
		}
		
		result.put("more_tags", moreTagsJS);
			
		return result;
	}

	private JSONObject translateTagValues(Tag tag, Locale lang) {
		JSONObject tagJS = new JSONObject();
		
		tagJS.put("name", L10n.tr(tag.getTitle(), lang));
		
		JSONObject valuesJS = new JSONObject();
		tagJS.put("valueType", tag.getTagValueType());
		
		for(Val val : tag.getVal()) {
			JSONObject value = new JSONObject();
			String valTrKey = StringUtils.strip(val.getTitle());
			value.put("name", L10n.tr(valTrKey, lang));
			value.put("group", val.isGroupByValue());
			
			valuesJS.put(valTrKey, value);
		}
		
		tagJS.put("values", valuesJS);
		
		return tagJS;
	}
	
	public List<JSONObject> listTranslatedFeatures(Locale lang) {
		
		List<JSONObject> reult = new ArrayList<JSONObject>();
		
		for(Feature f : this.docReader.getFeatures()) {
			reult.add(featureAsJSON(f, lang));
		}
		
		return reult;
	}

	public void collectKeywords(Collection<Feature> poiClassess, 
			Map<String, List<Val>> moreTagsVals,
			Collection<String> keywords, Collection<String> langs) {
		
		for(Feature f : poiClassess) {
			for(LangString kw : f.getKeyword()) {
				addKeyword(keywords, kw, langs);
			}
		}
		
		if(moreTagsVals != null) {
			for(List<Val> vl : moreTagsVals.values()) {
				for(Val v : vl) {
					for(LangString kw : v.getKeyword()) {
						addKeyword(keywords, kw, langs);
					}
				}
			}
		}
	}

	private void addKeyword(Collection<String> keywords, LangString kw, 
			Collection<String> langs) {
		
		String val = kw.getValue();
		
		if(val.startsWith(L10n.L10N_PREFIX)) {
		
			if(langs != null && !langs.isEmpty()) {
				for(String l : langs) {
					if(L10n.supported.contains(l)) {
						keywords.add(L10n.tr(val, Locale.forLanguageTag(l)));
					}
				}
			}
			else {
				for(String l : L10n.supported) {
					keywords.add(L10n.tr(val, Locale.forLanguageTag(l)));
				}
			}
		}
		else if(langs == null || langs.isEmpty()) {
			keywords.add(val);
		}
		else {
			String lang = kw.getLang();
			if(langs.contains(lang)) {
				keywords.add(val);
			}
		}
	}
	
	public Map<String, String> listMoreTagsTypes() {
		Map<String, String> result = new HashMap<String, String>();

		LinkedHashMap<String, Tag> moreTags = getMoreTags(featureByName.values());
		
		for(Entry<String, Tag> e : moreTags.entrySet()) {
			result.put(e.getKey(), e.getValue().getTagValueType().name());
		}
		
		return result;
	}
	
}
