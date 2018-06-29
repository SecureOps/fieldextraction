package com.secureops.fieldextraction;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

public class MockFieldExtractorItemConfigLoader implements IFieldExtractorItemConfigLoader {

	private static final String MOCK_MATCH_STRING_KEY = "match_string";
	private static final String MOCK_MATCH_STRING_DEFAULT = "";
	private static final String MOCK_NEWPROP_BOOL_KEY = "newprop";
	private static final boolean MOCK_NEWPROP_BOOL_DEFAULT = true;
	@Override
	public String typeString() {
		return "mock";
	}

	@Override
	public IFieldExtractorItem loadItemConfiguration(Configuration config, String section) throws Exception {
		String mockString = config.getString(MOCK_MATCH_STRING_KEY, MOCK_MATCH_STRING_DEFAULT);
		if(mockString.equals(MOCK_MATCH_STRING_DEFAULT))
			throw new ConfigurationException("match_string key must be set in the configuration");
		return new MockFieldExtractorItem(mockString);
	}

	@Override
	public void loadGlobals(Configuration config) throws Exception {
		// NoOP
	}
	
	@Override
	public void parseConfiguration(Configuration config, String section) {
		config.setProperty(MOCK_NEWPROP_BOOL_KEY, MOCK_NEWPROP_BOOL_DEFAULT);
	}

}
