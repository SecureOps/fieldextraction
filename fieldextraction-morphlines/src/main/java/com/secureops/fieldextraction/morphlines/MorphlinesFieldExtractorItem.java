package com.secureops.fieldextraction.morphlines;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.IFieldExtractorItem;

/*
 * Please note, to serialize this using Kryo, you need to set the following because the MorphlineItem doesn't have
 * an argueless constructor:
 * kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
 */
public class MorphlinesFieldExtractorItem implements IFieldExtractorItem, Serializable {
	private static final long serialVersionUID = 5040219323079069693L;
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(
            MorphlinesFieldExtractorItem.class);
	private static final String HEADER_PREFIX = "HEADER_";

    private int priority = 1000000;

    private Map<String, String> tags = new TreeMap<String, String>();

	private List<MorphlineItem> morphlines = new ArrayList<MorphlineItem>();

	private String config = null;
	private Pattern quickCheckPattern = null;

	public void setConfigText(String configText) {
		this.config = configText;
	}
	public void addMorphline(MorphlineItem entry) {
		this.morphlines.add(entry);
	}

    public String getConfigText() {
        return this.config;
    }

    @Override
    public int compareTo(IFieldExtractorItem o) {
        // Default to greater than
        int ret = 1;

        if (this.getPriority() < o.getPriority()) {
            // if the priority is lower, then return less than
            ret = -1;
        }

        else if (this.getPriority() > o.getPriority()) {
            ret = 1;
        } else {
            // Check if we're the same class type
            if (this.getClass().equals(o.getClass())) {
                // If we're the same class then check if we're the same thing
                // because if we are the map will overwrite us
                // if we aren't then we need to provide a gt or lt
                if (this.getConfigText() == ((MorphlinesFieldExtractorItem) o)
                        .getConfigText()) {
                    ret = 0;
                }
            }
        }
        return ret;
    }


	public ExtractorResult extract(String match) {
    	ExtractorResult er = new ExtractorResult();
    	Map<String, String> matches = null;

        for(MorphlineItem cmd : morphlines) {
            matches = cmd.extract(match);
        	if (matches != null) {

                break;
            }
        }

        if (matches != null && matches.size() > 0) {
        	// ShallowCopy the treeMap to respect Immutablility
        	// Do this first so Morphlines can override entries
        	Map<String, String> headers = new TreeMap<String,String>();
        	for(String headerKey : this.tags.keySet()){
        		headers.put(headerKey, this.tags.get(headerKey));
        	}
        	List<String> headerKeys = new ArrayList<String>();
            for (String entryKey : matches.keySet()) {
            	if(entryKey.startsWith(HEADER_PREFIX)) {
            		String headerKey = entryKey.substring(HEADER_PREFIX.length());
            		headers.put(headerKey, matches.get(entryKey));
            		headerKeys.add(headerKey);
            	}
            }

            //Flush the headers
            for(String headerKey :  headerKeys) {
            	matches.remove(HEADER_PREFIX + headerKey);
            }

            er.setMatches(matches);
            er.setTags(headers);
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

    public void setPattern(String pattern) {
    	this.quickCheckPattern = Pattern.compile(pattern);
    }

	@Override
	public boolean quickCheck(String match) {
		if(this.quickCheckPattern != null) {
			return this.quickCheckPattern.matcher(match).find();
		}
		return false;
	}

}
