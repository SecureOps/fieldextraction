package com.secureops.fieldextraction.grok;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.Match;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.IFieldExtractorItem;
import com.secureops.fieldextraction.ConfigUtils;
import com.secureops.fieldextraction.FieldExtractionItemException;


public class GrokFieldExtractorItem implements IFieldExtractorItem {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GrokFieldExtractorItem.class);
	private int priority = 1000000;
	// Tags set by the user to describe this entry
	private Map<String, String> tags = new TreeMap<String, String>();
	private Grok grok = null;
	private String grokPattern = null;

	public GrokFieldExtractorItem(String compileString) throws FieldExtractionItemException {
		this(compileString, null);
	}

	public GrokFieldExtractorItem(String compileString, List<String> patternFilesToUse) throws FieldExtractionItemException {
		GrokCompiler grokCompiler = GrokCompiler.newInstance();
		grokCompiler.registerDefaultPatterns();

		if (patternFilesToUse != null) {
			for(String patternFile : patternFilesToUse)
			try {
				StringReader reader = new StringReader(ConfigUtils.getTextFileContent(patternFile));
				grokCompiler.register(reader);
				reader.close();
			}
			catch (Exception e) {
				LOG.error("Unable to load contents of grok pattern file " + patternFile + ": " + e.getMessage());
				throw new FieldExtractionItemException("Unable to load contents of " + patternFile + ": "+ e.getMessage());
			}
		}
		this.grokPattern = compileString;
		this.grok = grokCompiler.compile(this.grokPattern);
	}

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

	public String getPattern() {
		return this.grokPattern;
	}

	public Map<String, String> matches(String match)
	{
		Map<String, String> matches = null;
		Match myMatch = this.grok.match(match);


		final Map<String, Object> objectMap = myMatch.capture();
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
			throws FieldExtractionItemException {
		if (this.tags.containsKey(tagName) && overwrite == false) {
			throw new FieldExtractionItemException("Tags already contains an item with key "
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
