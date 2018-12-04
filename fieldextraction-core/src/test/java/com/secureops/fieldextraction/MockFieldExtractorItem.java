package com.secureops.fieldextraction;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockFieldExtractorItem implements Serializable, IFieldExtractorItem {
	private static final long serialVersionUID = 5138317545152690130L;

	private static Logger LOG = LoggerFactory.getLogger(MockFieldExtractorItem.class);
	private static final String extractor_type = "mockExtractor";
	
	private Map<String, String> tags = new HashMap<String, String>();
	private int priority = 5;
	private String matchString = "";
	
	public MockFieldExtractorItem() {
		this.matchString = extractor_type;
		this.tags.put("extractor_name", "numbermatch");
	}
	
	public MockFieldExtractorItem(String mockString) {
		this.matchString = mockString;
		this.tags.put("extractor_name", "numbermatch");
	}
	
	public String getMatchString() {
		return this.matchString;
	}
	
	@Override
	public boolean quickCheck(String match) {
		return true;
	}
	
	@Override
	public int compareTo(IFieldExtractorItem o) {
		int test = o.getPriority();
		if (test > this.priority) return 1;
		if (test < this.priority) return -1;
		// Check if we're the same class type
		if (this.getClass().equals(o.getClass())) 
		{
			// If we're the same class then check if we're the same thing
			// because if we are the map will overwrite us
			// if we aren't then we need to provide a gt or lt
			if(this.getMatchString() == ((MockFieldExtractorItem) o).getMatchString()) 
			{
				return 0;
			}
		}
		return 1;
	}

	public Map<String, String> matches(String match) {
		Map<String, String> ret = null;
		if(this.getMatchString().equalsIgnoreCase(match)) {
			ret = new HashMap<String, String>();
			ret.put("test", match);		
		}
		return ret;
	}

	@Override
	public ExtractorResult extract(String match) {
		LOG.debug("Extracting " + match + " and comparing to " + this.getMatchString());
		ExtractorResult res = new ExtractorResult();
		Map<String, String> m = this.matches(match);
		if(m != null) {
			LOG.debug(match + " matches " + this.getMatchString());
			res.setMatches(m);
			res.setTags(this.getTags());
		}
		return res;
	}

	@Override
	public String getTag(String tagName) {
		return this.tags.get(tagName);
	}

	@Override
	public Map<String, String> getTags() {
		return Collections.unmodifiableMap(this.tags);
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void addTag(String tagName, String tagValue) {
		this.tags.put(tagName, tagValue);
	}

	@Override
	public void addTag(String tagName, String tagValue, Boolean overwrite) throws FieldExtractionItemException {
		if(this.tags.containsKey(tagName)) {
			if(overwrite) {
				this.addTag(tagName, tagValue);
			}
		}
		else {
			this.addTag(tagName, tagValue);
		}
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setMatchString(String ms) {
		if(ms != null)
			this.matchString = ms;
	}

}
