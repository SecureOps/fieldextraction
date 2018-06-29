package com.secureops.fieldextraction.regex;

import java.util.Map;

import com.secureops.fieldextraction.IFieldExtractorItem;

public interface IRegexFieldExtractorItem extends IFieldExtractorItem{
	public String getRegexString();
	public void setRegexString(String regexString);
	public Map<String, String> matches(String sourceString);
}
