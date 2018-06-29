package com.secureops.fieldextraction.csv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequiredField {
	private String field;
	private boolean matched = false;
	private Pattern regex;

	// Force the constructor to have the required fields
	public RequiredField(String fieldName, String regex) {
		this.field = fieldName;
		this.regex = Pattern.compile(regex);
	}

	public String getFieldName() {
		return field;
	}

	public void setFieldName(String field) {
		this.field = field;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public String getRegex() {
		return regex.pattern();
	}

	public void setRegex(String regex) {
		this.regex = Pattern.compile(regex);
	}

	public boolean matches(String fieldVal) {
		Matcher m = regex.matcher(fieldVal);
		if (m.matches()) {
			this.matched = true;
			return true;
		}
		return false;
	}
}
