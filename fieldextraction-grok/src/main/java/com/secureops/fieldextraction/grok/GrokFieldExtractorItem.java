package com.secureops.fieldextraction.grok;

import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.IFieldExtractorItem;

public class GrokFieldExtractorItem implements IFieldExtractorItem {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GrokFieldExtractorItem.class);
	private int priority = 1000000;
	// Tags set by the user to describe this entry
	private Map<String, String> tags = new TreeMap<String, String>();
	private Grok grok = new Grok();
	private String grokPattern = null;
	
	public int compareTo(IFieldExtractorItem o) 
	{
		// Default to greater than
		int ret = 1;
		
		if (this.getPriority() < o.getPriority()) 
		{
			// if the priority is lower, then return less than
			ret = -1;
		}

		else if (this.getPriority() > o.getPriority()) 
		{
			ret = 1;
		} 
		else 
		{
			// Check if we're the same class type
			if (this.getClass().equals(o.getClass())) 
			{
				// If we're the same class then check if we're the same thing
				// because if we are the map will overwrite us
				// if we aren't then we need to provide a gt or lt
				if(this.getPattern() == ((GrokFieldExtractorItem) o).getPattern()) 
				{
					ret = 0;
				}
			}
		}
		return ret;
	}

	public void loadGrokPatternFromReader(Reader reader) throws Exception {
		this.grok.addPatternFromReader(reader);
	}
	
	public void setPattern(String pattern) throws Exception {
		this.grokPattern = pattern;
		this.grok.compile(pattern);
	}
	
	public String getPattern() {
		return this.grokPattern;
	}
	
	public Map<String, String> matches(String match) 
	{
		Map<String, String> matches = null;
		Match myMatch = this.grok.match(match);
		
		
		myMatch.captures();
		Map<String, Object> objectMap = myMatch.toMap();
		if(!objectMap.isEmpty()) {
			matches = new HashMap<String, String>();
		}
		for(Map.Entry<String, Object> entry : objectMap.entrySet())
		{
		    if (entry.getValue() != null)
		    {
		        matches.put(entry.getKey(), entry.getValue().toString());
		    }
		}
		
		return matches;
	}
	
	public ExtractorResult extract(String match) {
		ExtractorResult er = new ExtractorResult();
		Map<String, String> result = this.matches(match);
		if(result != null) {
			er.setMatches(result);
		}
		return er;
	}

	public String getTag(String tagName) {
		return this.tags.get(tagName);
	}

	public Map<String, String> getTags() {
		return Collections.unmodifiableMap(this.tags);
	}


	public int getPriority() {
		return this.priority;
	}

	public void addTag(String tagName, String tagValue) {
		// We default the "overwrite" to true in this case
		this.tags.put(tagName, tagValue);
	}

	public void addTag(String tagName, String tagValue, Boolean overwrite)
			throws Exception {
		if (this.tags.containsKey(tagName) && overwrite == false) {
			throw new Exception("Tags already contains an item with key "
					+ tagName);
		}
		this.addTag(tagName, tagValue);
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public boolean quickCheck(String match) {
		return true;
	}

}
