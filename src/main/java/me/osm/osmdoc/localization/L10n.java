package me.osm.osmdoc.localization;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class L10n {
	
	public static final String L10N_PREFIX = "l10n.";
	public static final Set<String> supported = new HashSet<String>(
			Arrays.asList("ru", "en"));
	
	private static String catalogPath = null;
	
	private L10n() {
		
	}
	
	private L10n(Locale locale) {
		if(catalogPath == null) {
			strings = ResourceBundle.getBundle("strings", locale);
		}
		else {
			// Access to rbLoader isn't safe here
			strings = ResourceBundle.getBundle("strings", locale, rbLoader);
		}
	}

	private ResourceBundle strings;
	
	private static final Map<String, L10n> instances = new HashMap<String, L10n>();
	private static ClassLoader rbLoader; 
	
	public static String trOrNull(String key, Locale locale) {
		
		if(locale == null) {
			locale = Locale.getDefault();
		}
		
		if(key != null && key.startsWith(L10N_PREFIX)) {
			
			if(!supported.contains(locale.toLanguageTag())) {
				return null;
			}
			
			if(instances.get(locale.getDisplayName()) == null) {
				synchronized (instances) {
					if(instances.get(locale.getDisplayName()) == null) {
						instances.put(locale.getDisplayName(), new L10n(locale));
					}
				}
			}
			
			if(instances.get(locale.getDisplayName()).strings.containsKey(key)) {
				return instances.get(locale.getDisplayName()).strings.getString(key);
			}
			else {
				return null;
			}
		}

		return key;
		
	}
	
	public static String tr(String key, Locale locale) {
		
		String result = trOrNull(key, locale);
		
		return result == null ? key : result;
		
	}
	
	public static synchronized void setCatalogPath(String path) {
		catalogPath = path;
		
		try {
			File file = new File(catalogPath).getParentFile();
			URL[] urls = {file.toURI().toURL()};
			rbLoader = new URLClassLoader(urls, L10n.class.getClassLoader());
		}
		catch (Throwable t) {
			t.printStackTrace();
			catalogPath = null;
		}
	}
}
