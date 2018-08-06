package com.secureops.fieldextraction.grok;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.IFieldExtractorItem;
import com.secureops.fieldextraction.IFieldExtractorItemConfigLoader;
import com.secureops.fieldextraction.ConfigUtils;

public class GrokFieldExtractorItemConfigLoader implements
		IFieldExtractorItemConfigLoader {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GrokFieldExtractorItemConfigLoader.class);

	private static String GROKEXTRACTORITEMINCLUDEPATTERNFILE_KEY = "grokPatternFiles";
	private static String GROKEXTRACTORITEMPATTERN_KEY = "grokPattern";

	private Map<String, String> availableFiles = new HashMap<String, String>();

	public String typeString() {
		return "grok";
	}

	public void loadGlobals(Configuration config) throws Exception {
		// NO-OP
	}
	public IFieldExtractorItem loadItemConfiguration(Configuration config,
			String section) throws Exception {

		// Get the files required for the Grok patterns
		// Make sure they're in the list so that they don't need to be referenced with full path

		List<String> patternFilesToUse = new ArrayList<String>(Arrays.asList(config.getStringArray(GROKEXTRACTORITEMINCLUDEPATTERNFILE_KEY)));


		// This loaded the pattern dictionary, now we need to compile a match
		String pattern = config.getString(GROKEXTRACTORITEMPATTERN_KEY, null);
		if(pattern == null) {
			throw new Exception("Must provide a pattern to compile against");
		}
		GrokFieldExtractorItem grokItem = new GrokFieldExtractorItem(pattern, patternFilesToUse);

		return grokItem;
	}

	@Override
	public void parseConfiguration(Configuration arg0, String arg1) throws ConfigurationException, IOException {
		// Not required

	}

}
