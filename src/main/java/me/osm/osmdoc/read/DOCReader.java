package me.osm.osmdoc.read;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import me.osm.osmdoc.model.v2.Feature;
import me.osm.osmdoc.model.v2.Hierarchy;
import me.osm.osmdoc.model.v2.Trait;

public interface DOCReader {

	public List<Feature> getFeatures();

	public Collection<? extends Feature> getHierarcyBranch(
			String hierarchyName, String branch);

	public List<Hierarchy> listHierarchies();

	public Hierarchy getHierarchy(String name);
	
	public Map<String, Trait> getTraits();
	
}