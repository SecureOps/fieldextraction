package com.secureops.fieldextraction;

import java.util.Map;


// This is an interface because the implementations might be dependent on
// scriptable languages that don't necessarily implement abstract classes
// very well.

/*
 * Note, to serialize this interface, in some cases, we're using regeg for the quickCheck below
 * Kryo requires us to register the class as follows (inside a Kryo.Registrator object):
 * kryo.register(java.util.regex.Pattern.class, new com.twitter.chill.java.RegexSerializer())
 */
public interface IFieldExtractorItem extends Comparable<IFieldExtractorItem> {
	public ExtractorResult extract(String match) throws Exception;
	public boolean quickCheck(String match);
	public String getTag(String tagName);
	public Map<String, String> getTags();
	public int getPriority();
	public void addTag(String tagName, String tagValue);
	public void addTag(String tagName, String tagValue, Boolean overwrite) throws Exception;
	public void setPriority(int priority);
}
