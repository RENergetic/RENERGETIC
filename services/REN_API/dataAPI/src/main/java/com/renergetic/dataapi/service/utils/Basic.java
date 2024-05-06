package com.renergetic.dataapi.service.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Basic {

    @SafeVarargs
	public static <T, S> Map<T, List<S>> combineMaps(Map<T, S>... maps) {
    	Map<T, List<S>> combined = new LinkedHashMap<>();
    	
    	for (Map<T,S> map: maps) {
	        for (T key : map.keySet()) {
	            S value = map.get(key);
	
	            if (!combined.containsKey(key)) {
	            	combined.put(key, new ArrayList<>());
	            }
	
	            List<S> values = combined.get(key);
	            values.add(value);
	        }
    	}
    	
    	return combined;
    }

	public static <T, S> Map<T, List<S>> combineMaps(Collection<Map<T, S>> maps) {
    	Map<T, List<S>> combined = new LinkedHashMap<>();
    	
    	for (Map<T,S> map: maps) {
	        for (T key : map.keySet()) {
	            S value = map.get(key);
	
	            if (!combined.containsKey(key)) {
	            	combined.put(key, new ArrayList<>());
	            }
	
	            List<S> values = combined.get(key);
	            values.add(value);
	        }
    	}
    	
    	return combined;
    }
}
