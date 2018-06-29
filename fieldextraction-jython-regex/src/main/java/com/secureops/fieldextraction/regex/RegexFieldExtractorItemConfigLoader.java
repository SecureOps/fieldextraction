package com.secureops.fieldextraction.regex;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

import com.secureops.fieldextraction.IFieldExtractorItem;
import com.secureops.fieldextraction.IFieldExtractorItemConfigLoader;

public class RegexFieldExtractorItemConfigLoader implements
		IFieldExtractorItemConfigLoader {

	// Regex specific settings
	private static final String REGEXEXTRACTORITEMPATTERN_KEY = "pattern";
	private static final String REGEXEXTRACTORITEMTYPESTRING = "regex";

	@Override
	public String typeString() {
		return REGEXEXTRACTORITEMTYPESTRING;
	}

	@Override
	public IFieldExtractorItem loadItemConfiguration(Configuration config,
			String section) throws Exception {
		String regex = config.getString(REGEXEXTRACTORITEMPATTERN_KEY, null);
		if(regex == null || regex.isEmpty()) {
			throw new Exception("A value must be supplied for the " + REGEXEXTRACTORITEMPATTERN_KEY + " key of active extractor " + section);
		}
		
		return RegexFieldExtractorItemFactory.newRegexFieldExtractorItem(regex);
	}

	@Override
	public void loadGlobals(Configuration config) throws Exception {
		// Not required in this case
	}

	@Override
	public void parseConfiguration(Configuration arg0, String arg1) throws ConfigurationException, IOException {
		// Not required in this case
		
	}

}
