package com.secureops.fieldextraction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExtractorResult {
	private Map<String, String> resultMap;
	private Map<String, String> tagMap;
	
	protected ExtractorResult(Map<String, String> matches, Map<String, String> tags) {
		this.resultMap = Collections.unmodifiableMap(matches);
		this.tagMap = Collections.unmodifiableMap(tags);
	}
	
	public ExtractorResult(){
		this.resultMap = new HashMap<String,String>();
		this.tagMap = new HashMap<String,String>();
	}
	
	public void setMatches(Map<String, String> matches) {
		this.resultMap = Collections.unmodifiableMap(matches);
	}
	
	public void setTags(Map<String,String> tags) {
		this.tagMap = Collections.unmodifiableMap(tags);
	}
	
	public Map<String, String> getMatches(){
		return this.resultMap;
	}
	
	public Map<String, String> getTags() {
		return this.tagMap;
	}

}
