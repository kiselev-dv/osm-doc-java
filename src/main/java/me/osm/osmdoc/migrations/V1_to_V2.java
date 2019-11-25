package me.osm.osmdoc.migrations;

import static me.osm.osmdoc.localization.L10n.L10N_PREFIX;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import me.osm.osmdoc.localization.L10n;
import me.osm.osmdoc.model.DocPartNSMapper;
import me.osm.osmdoc.model.v1.DocPart;
import me.osm.osmdoc.model.v1.Feature;
import me.osm.osmdoc.model.v1.Group;
import me.osm.osmdoc.model.v1.Hierarchy;
import me.osm.osmdoc.model.v1.KeyType;
import me.osm.osmdoc.model.v1.Tag;
import me.osm.osmdoc.model.v1.Tag.Val;
import me.osm.osmdoc.model.v1.TagValueType;
import me.osm.osmdoc.model.v1.Tags;
import me.osm.osmdoc.model.v1.Trait;
import me.osm.osmdoc.model.v2.Choise;
import me.osm.osmdoc.model.v2.Fref;
import me.osm.osmdoc.model.v2.LangString;
import me.osm.osmdoc.model.v2.MoreTags;

public class V1_to_V2 {
	
	private static final DocPartNSMapper DOC_PART_NS_MAPPER = new DocPartNSMapper();

	private String catalogPath;
	private Properties properties;

	public static void main(String[] args) {
		new V1_to_V2(args[0]).run();
	}
	
	public V1_to_V2(String catalogPath) {
		this.catalogPath = catalogPath;
	}

	public void run() {
		File root = new File(catalogPath);
		iterateOverFiles(root);
	}
	
	private void iterateOverFiles(File root) {
		if(root.isFile()) {
			if(root.getName().endsWith(".xml")) {
				handleSingleDoc(root);
			}
		}
		else if(root.isDirectory()){
			for(File f : root.listFiles()) {
				iterateOverFiles(f);
			}
		}
	}

	private void handleSingleDoc(File root) {
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("me.osm.osmdoc.model.v1", 
					me.osm.osmdoc.model.v1.ObjectFactory.class.getClassLoader());
			
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			DocPart doc = (DocPart) unmarshaller.unmarshal(root);
			
			String relativePath = new File(this.catalogPath).toURI().relativize(root.toURI()).getPath();
			transformDoc(doc, relativePath);
			
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void transformDoc(DocPart doc, String localPath) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance("me.osm.osmdoc.model.v2", 
				me.osm.osmdoc.model.v2.ObjectFactory.class.getClassLoader());
		
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", DOC_PART_NS_MAPPER);

		me.osm.osmdoc.model.v2.DocPart doc2 = new me.osm.osmdoc.model.v2.DocPart();
		
		for(Feature f : doc.getFeature()) {
			me.osm.osmdoc.model.v2.Feature f2 = new me.osm.osmdoc.model.v2.Feature();
			doc2.getFeature().add(f2);
			
			processFeature(f, f2);
			
			File featureFile = new File("/home/dkiselev/osm/osm-doc/catalog_v2/" + localPath);
			featureFile.mkdirs();
			marshaller.marshal(doc2, featureFile);
		}
		
		for (Hierarchy h : doc.getHierarchy()) {
			
			me.osm.osmdoc.model.v2.Hierarchy h2 = new me.osm.osmdoc.model.v2.Hierarchy();
			doc2.getHierarchy().add(h2);
			
			processHierarchy(h, h2);
			
			marshaller.marshal(doc2, new File("/home/dkiselev/osm/osm-doc/catalog_v2/hierarchies/osm-ru.xml"));
		}
		
		for (Trait t : doc.getTrait()) {
			me.osm.osmdoc.model.v2.Trait t2 = new me.osm.osmdoc.model.v2.Trait();
			doc2.getTrait().add(t2);
			processTrait(t, t2);
			marshaller.marshal(doc2, new File("/home/dkiselev/osm/osm-doc/catalog_v2/" + localPath));
		}
	}

	private void processTrait(Trait t, me.osm.osmdoc.model.v2.Trait t2) {
		t2.setName(t.getName());
		if (t.isGroupTags()) {
			t2.setGroupTags(t.isGroupTags());
		}
		
		processTitleAndDesc(t.getTitle(), t.getDescription(), t2.getTitle(), t2.getDescription());
		
		t2.getExtends().addAll(t.getExtends());
		
		if(t.getMoreTags() != null) {
			me.osm.osmdoc.model.v1.MoreTags moreTags = t.getMoreTags();
			me.osm.osmdoc.model.v2.MoreTags moreTags2 = new me.osm.osmdoc.model.v2.MoreTags();
			t2.setMoreTags(moreTags2);
			
			processMoreTags(moreTags, moreTags2);
		}
	}

	private void processHierarchy(Hierarchy h, me.osm.osmdoc.model.v2.Hierarchy h2) {
		
		h2.setName(h.getName());
		
		for(Group g : h.getGroup()) {
			processGroupInHierarchy(g, h2.getGroup());
		}
	}

	private void processGroupInHierarchy(Group g, List<me.osm.osmdoc.model.v2.Group> target) {
		me.osm.osmdoc.model.v2.Group g2 = new me.osm.osmdoc.model.v2.Group();
		target.add(g2);
		
		g2.setName(g.getName());
		
		processGroup(g, g2);
		
		g.getFref().forEach(ref -> {
			Fref ref2 = new Fref();
			ref2.setRef(ref.getRef());
			g2.getFref().add(ref2); 
		});
		
		g.getGroup().forEach(childGroupV1 -> {
			processGroupInHierarchy(childGroupV1, g2.getGroup());
		});
	}

	private void processGroup(Group g, me.osm.osmdoc.model.v2.Group g2) {
		String title = g.getTitle();
		String description = g.getDescription();
		processTitleAndDesc(title, description, g2.getTitle(), g2.getDescription());
	}

	private void processTitleAndDesc(String title, String description, 
			List<LangString> targetTitle, List<LangString> targetDescription) {
		
		if(title != null && title.startsWith(L10N_PREFIX)) {
			for (String langTag : L10n.supported) {
				LangString titleLangString = new LangString();
				
				String translation = L10n.trOrNull(title, Locale.forLanguageTag(langTag));
				if (translation == null) {
					if (title.endsWith("true.title") || title.endsWith("yes.title")) {
						translation = langTag.equals("ru") ? "Да" : "Yes";
					}
					else if (title.endsWith("false.title") || title.endsWith("no.title")) {
						translation = langTag.equals("ru") ? "Нет" : "No";
					}
					else if (title.endsWith("emergency.title")) {
						translation = langTag.equals("ru") ? "Экстренный" : "Emergency";
					}
					else if (title.endsWith("bicycle_track.tags.sport.title")) {
						translation = langTag.equals("ru") ? "Велотрек" : "Track cycling";
					}
					else if (title.endsWith("running_track.tags.sport.title")) {
						translation = langTag.equals("ru") ? "Бег" : "Running";
					}
					else if (title.endsWith("horse_track.tags.sport.title")) {
						translation = langTag.equals("ru") ? "Конный спорт" : "Horse track";
					}
					else if (title.endsWith("tags.religion.title")) {
						translation = langTag.equals("ru") ? "Религия" : "Religion";
					}
					else if (title.endsWith("trait.drive_in.title")) {
						translation = langTag.equals("ru") ? "Обслуживание не выходя из машины" : "Drivein";
					}
					else {
						System.out.println(title);
					}
				}
				
				titleLangString.setValue(translation);
				titleLangString.setKey(title);
				titleLangString.setLang(langTag);
				
				targetTitle.add(titleLangString);
			}
		}

		
		if(description != null && description.startsWith(L10N_PREFIX)) {
			for (String langTag : L10n.supported) {
				LangString descriptionLangString = new LangString();
				
				String translation = L10n.trOrNull(description, Locale.forLanguageTag(langTag));
				if (translation == null) {
					System.out.println("Can't find translation for " + description);
				}
				
				descriptionLangString.setValue(translation);
				descriptionLangString.setKey(description);
				descriptionLangString.setLang(langTag);
				
				targetDescription.add(descriptionLangString);
			}
		}
	}

	private void processFeature(Feature f, me.osm.osmdoc.model.v2.Feature f2) {
		f2.setName(f.getName());
		
		processTitleAndDesc(f.getTitle(), f.getDescription(), f2.getTitle(), f2.getDescription());
		processTags(f, f2);
		processMoreTags(f, f2);

		
		f.getTrait().forEach(t -> {
			me.osm.osmdoc.model.v2.Feature.Trait t2 = 
					new me.osm.osmdoc.model.v2.Feature.Trait();
			t2.setValue(t.getValue());		
			f2.getTrait().add(t2);
		});
		
		f.getApplyedTo().forEach(type -> {
			f2.getApplyedTo().add(type.name().toLowerCase());
		});
		
		f2.setNote(f.getNote());
		f2.getIcon().addAll(f.getIcon());
		f2.getWiki().addAll(f.getWiki());

		f.getKeyword().forEach(kw -> {
			LangString kw2 = new LangString();
			kw2.setValue(kw.getValue());
			kw2.setLang(kw.getLang());
			f2.getKeyword().add(kw2);
		});
		
	}

	private void processTags(Feature f, me.osm.osmdoc.model.v2.Feature f2) {
		
		for(Tags sinonym : f.getTags()) {
			me.osm.osmdoc.model.v2.Tags synonim2 = new me.osm.osmdoc.model.v2.Tags();
			f2.getTags().add(synonim2);
			
			for(Tag tag : sinonym.getTag()) {
				me.osm.osmdoc.model.v2.Tag tag2 = new me.osm.osmdoc.model.v2.Tag();
				synonim2.getTag().add(tag2);
				processTag(tag, tag2);
			}
		}
	}

	private void processMoreTags(Feature f, me.osm.osmdoc.model.v2.Feature f2) {
		if(f != null && f.getMoreTags() != null) {
			me.osm.osmdoc.model.v1.MoreTags moreTags = f.getMoreTags();
			me.osm.osmdoc.model.v2.MoreTags moreTags2 = new me.osm.osmdoc.model.v2.MoreTags();
			f2.setMoreTags(moreTags2);
			
			processMoreTags(moreTags, moreTags2);
		}
	}

	private void processMoreTags(me.osm.osmdoc.model.v1.MoreTags moreTags,
			me.osm.osmdoc.model.v2.MoreTags moreTags2) {
		
		moreTags.getChoise().forEach(c -> {
			Choise c2 = new Choise();
			c2.setPrimaryKey(c.getPrimaryKey()); 
			moreTags2.getChoise().add(c2);
			
			c.getTag().forEach(choiseTag -> {
				me.osm.osmdoc.model.v2.Tag choiseTag2 = new me.osm.osmdoc.model.v2.Tag();
				processTag(choiseTag, choiseTag2);
				c2.getTag().add(choiseTag2);
			});
		});
		
		for(Tag tag : moreTags.getTag()) {
			me.osm.osmdoc.model.v2.Tag t2 = new me.osm.osmdoc.model.v2.Tag();
			moreTags2.getTag().add(t2);
			
			processTag(tag, t2);
		}
	}

	private void processTag(Tag tag, me.osm.osmdoc.model.v2.Tag tag2) {
		
		processTitleAndDesc(tag.getTitle(), tag.getDescription(), tag2.getTitle(), tag2.getDescription());

		KeyType key = tag.getKey();
		me.osm.osmdoc.model.v2.KeyType key2 = new me.osm.osmdoc.model.v2.KeyType();
		tag2.setKey(key2);
		
		key2.setValue(key.getValue());
		if (key.getMatch() != me.osm.osmdoc.model.v1.Tag.Val.MatchType.EXACT) {
			key2.setMatch(key.getMatch().name().toLowerCase());
		}

		if (tag.getTagValueType() != me.osm.osmdoc.model.v1.Tag.TagValueType.ANY_STRING) {
			tag2.setTagValueType(tag.getTagValueType().name().toLowerCase());
		}
		
		if (tag.isExclude()) {
			tag2.setExclude(true);
		}
		
		for(Val val : tag.getVal()) {
			me.osm.osmdoc.model.v2.Tag.Val val2 = new me.osm.osmdoc.model.v2.Tag.Val();
			tag2.getVal().add(val2);
			
			processTitleAndDesc(val.getTitle(), val.getDescription(), 
					val2.getTitle(), val2.getDescription());
			
			val2.setValue(val.getValue());
			
			val2.setDefault(val.isDefault());

			if (!val.isGroupByValue()) {
				val2.setGroupByValue(val.isGroupByValue());
			}

			if (!val.isUseAsKeyword()) {
				val2.setUseAsKeyword(val.isUseAsKeyword());
			}
			
			if (val.getMatch() != me.osm.osmdoc.model.v1.Tag.Val.MatchType.EXACT) {
				val2.setMatch(val.getMatch().name().toLowerCase());
			}
		}
	}

}
