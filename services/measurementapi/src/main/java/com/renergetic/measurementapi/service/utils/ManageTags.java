
package com.renergetic.measurementapi.service.utils;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class ManageTags {

    public String fluxFilter(Map<String, List<String>> tags) {
        return String.format("filter(fn: (r) => %s)",
					tags.keySet().stream()
					.map(key -> '(' + 
                        tags.get(key).stream()
                            .map(value -> {
                                if (value.equalsIgnoreCase("no_tag")) {
                                    return String.format("not exists r[\"%s\"]", key);
                                }
                                return String.format("r[\"%s\"] == \"%s\"", key, value);
                            }).collect(Collectors.joining(" or ")) + 
                    ')')
					.collect(Collectors.joining(" and ")));
        
    }

    public void remove(Map<String, String> tags, String...keys) {
        for (String key : keys) {
            tags.remove(key);
        }
    }

    public Map<String, List<String>> parse(Map<String, String> tags) {
        return tags.entrySet().stream()
				.collect(
						Collectors.toMap(
								Entry::getKey, 
								entry -> Arrays.stream(entry.getValue().split(",")).map(String::trim).collect(Collectors.toList())
								)
						); 
    }
}