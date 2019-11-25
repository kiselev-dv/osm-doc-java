package me.osm.osmdoc.model;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class DocPartNSMapper extends NamespacePrefixMapper {
	
	public static final String DOC_PART_NAMESPACE = "http://map.osm.me/osm-doc-part";
	
	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		
		if(namespaceUri.equals(DOC_PART_NAMESPACE)) {
			return "";
		}
		
		return suggestion;
	}
}