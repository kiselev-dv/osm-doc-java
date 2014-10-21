package me.osm.osmdoc.localization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class L10n {
	
	public static final String L10N_PREFIX = "l10n.";
	public static Set<String> supported = new HashSet<String>(Arrays.asList(
			"ru"));
	
	private L10n() {
		
	}
	
	private L10n(Locale locale) {
		rbundle = ResourceBundle.getBundle("localization.strings", locale);
	}

	private ResourceBundle rbundle;
	
	private static final Map<String, L10n> instances = new HashMap<String, L10n>(); 
	
	public static String tr(String key, Locale locale) {
		
		if(locale == null) {
			locale = Locale.getDefault();
		}
		
		if(key != null && key.startsWith(L10N_PREFIX)) {
			
			if(instances.get(locale.getDisplayName()) == null) {
				synchronized (instances) {
					if(instances.get(locale.getDisplayName()) == null) {
						instances.put(locale.getDisplayName(), new L10n(locale));
					}
				}
			}
			
			return instances.get(locale.getDisplayName()).rbundle.getString(key);
		}
		else {
			return key;
		}
		
	}
}
