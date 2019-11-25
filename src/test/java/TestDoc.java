import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.junit.Test;

import me.osm.osmdoc.model.v2.Fref;
import me.osm.osmdoc.model.v2.Group;
import me.osm.osmdoc.model.v2.Hierarchy;
import me.osm.osmdoc.read.OSMDocFacade;

public class TestDoc {

	@Test
	public void testCanReadDocFromJar() {
		OSMDocFacade docFacade = new OSMDocFacade("jar");
		assertTrue("Can't read features form local osm doc facade", 
				docFacade.getReader().getFeatures().size() > 0);
		assertTrue("Can't read traits form local osm doc facade", 
				docFacade.getReader().getTraits().size() > 0);
	}
	
	@Test
	public void testHierarchyFeaturesRefs() {
		OSMDocFacade docFacade = new OSMDocFacade("jar");
		List<Hierarchy> hierarchies = docFacade.getReader().listHierarchies();
		for (Hierarchy h : hierarchies) {
			for(Fref fref : h.getFref()) {
				assertNotNull("Can't resolve feature by name: " + fref.getRef(), 
						docFacade.getFeature(fref));
			}
			
			for(Group g : h.getGroup()) {
				checkGroupLinks(g, docFacade);
			}
		}
	}
	
	@Test
	public void testHierarchyAsJSON() {
		OSMDocFacade docFacade = new OSMDocFacade("jar");
		
		for (Hierarchy h : docFacade.getReader().listHierarchies()) {
			JSONObject hierarchyJSONNull = docFacade.getHierarchyJSON(h.getName(), null);
			JSONObject hierarchyJSONRu = docFacade.getHierarchyJSON(h.getName(), Locale.forLanguageTag("ru"));
			JSONObject hierarchyJSONEn = docFacade.getHierarchyJSON(h.getName(), Locale.forLanguageTag("en"));
		}
	}

	private void checkGroupLinks(Group g, OSMDocFacade docFacade) {
		for(Group g2 : g.getGroup()) {
			checkGroupLinks(g2, docFacade);
		}
		
		for(Fref fref : g.getFref()) {
			assertNotNull("Can't resolve feature by name: " + fref.getRef(), 
					docFacade.getFeature(fref));
		}
	}

}
