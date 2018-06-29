package com.secureops.fieldextraction.grok;

import java.util.Map;

import org.junit.Test;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.FieldExtractorConfigLoader;
import com.secureops.fieldextraction.FieldExtractor;
import com.secureops.fieldextraction.FieldExtractionNoMatchException;

public class TestGrokConfigLoader {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(TestGrokConfigLoader.class);

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigNegative() throws Exception 
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors.properties");
		Assert.assertNotNull(extractor);
		
		ExtractorResult matchItem = extractor.extract("Bob the Builder");
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigEmpty() throws Exception 
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors.properties");
		Assert.assertNotNull(extractor);
		
		ExtractorResult matchItem = extractor.extract("");
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigNull() throws Exception 
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors.properties");
		Assert.assertNotNull(extractor);
		
		ExtractorResult matchItem = extractor.extract(null);
	}

	
	@Test
	public void testLoadBasicConfig() throws Exception 
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors.properties");
		Assert.assertNotNull(extractor);
		
		ExtractorResult matchItem = extractor.extract("07/08/09 12:22:44");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("DATESTAMP"));		
	}
}
