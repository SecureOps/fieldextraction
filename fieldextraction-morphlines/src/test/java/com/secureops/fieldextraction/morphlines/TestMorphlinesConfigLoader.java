package com.secureops.fieldextraction.morphlines;

import java.util.Map;

import org.junit.Test;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.FieldExtractorConfigLoader;
import com.secureops.fieldextraction.FieldExtractor;
import com.secureops.fieldextraction.FieldExtractionNoMatchException;

public class TestMorphlinesConfigLoader {
	private static final Logger LOG = LoggerFactory.getLogger(TestMorphlinesConfigLoader.class);
	
	@SuppressWarnings("unused")
	private void dumpMap(Map<String, String> matches) {
		for(String myKey: matches.keySet()) {
			LOG.info("Key: " + myKey + " value: " + matches.get(myKey));
		}		
	}
	
	@Test
	public void testRenderBasicConfig() throws Exception
	{
		Configuration conf = FieldExtractorConfigLoader.parseConfiguration("morphlines/renderConfig.properties");
		Assert.assertNotNull(conf.subset("test_morphlines"));
		Assert.assertTrue(conf.subset("test_morphlines").containsKey(MorphlinesFieldExtractorItemConfigLoader.MORPHLINES_CONFIG_ENCODED_KEY));
		Assert.assertFalse(conf.subset("test_morphlines").containsKey(MorphlinesFieldExtractorItemConfigLoader.MORPHLINES_CONFIG_FILE_KEY));
	}

	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigNegative() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadBasicConfigNegative()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinebob.properties");
		Assert.assertNotNull(extractor);
		ExtractorResult matchItem = extractor.extract("fred");
		
		
		// Empty Match Test
		matchItem = extractor.extract("");
		Assert.assertNull(matchItem);
		
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigEmpty() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadBasicConfigEmpty()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinebob.properties");
		Assert.assertNotNull(extractor);
		ExtractorResult matchItem = extractor.extract("");		
	}
	
	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigNull() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadBasicConfigNull()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinebob.properties");
		Assert.assertNotNull(extractor);
		ExtractorResult matchItem = extractor.extract(null);
	}
	
	
	@Test
	public void testLoadBasicConfigPositive() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadBasicConfig()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinebob.properties");
		Assert.assertNotNull(extractor);
		
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract("bob");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));

		
		// Positive Match Test
		matchItem = extractor.extract("bob");
		Assert.assertNotNull(matchItem);
		Assert.assertTrue(matchItem.getMatches().containsKey("test"));		
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadTwoExtractorsNegative() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadTwoExtractorsNegative()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinemultipleextractors.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract("nomatch");
	}

	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadTwoExtractorsEmpty() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadTwoExtractorsEmpty()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinemultipleextractors.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		@SuppressWarnings("unused")
		ExtractorResult matchItem = extractor.extract("");

	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadTwoExtractorsNull() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadTwoExtractorsNull()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinemultipleextractors.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract(null);
	}
	
	@Test
	public void testLoadTwoExtractorsPositive() throws Exception 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting test: testLoadTwoExtractors()");
		}
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/singlemorphlinemultipleextractors.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract("bob");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
	}
	
	@Test
	public void testLoadOneExtractorTwoMorphlines() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/multiplemorphlinesingleextractor.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract("bob");
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue(matchItem.getTags().get("extractor_name").equals("MORPHLINE_BOB"));
	}
	
	@Test
	public void testGrokPatterns() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/grokpatterntest.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract("bob");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue("MORPHLINE_TEST_GROK".equals(matchItem.getTags().get("extractor_name")));


		matchItem = extractor.extract("fred");
		Assert.assertNotNull(matchItem);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue(matchItem.getTags().get("extractor_name").equals("MORPHLINE_TEST_GROK"));
	}
	
	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testFailQuickCheck() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/quickcheckmorphlines.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test but fail quickcheck
		ExtractorResult matchItem = extractor.extract("bob");
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testQuickCheckNegative() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/quickcheckmorphlines.properties");
		Assert.assertNotNull(extractor);
		// No Match
		ExtractorResult matchItem = extractor.extract("fred");
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testQuickCheckEmpty() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/quickcheckmorphlines.properties");
		Assert.assertNotNull(extractor);
		// Empty Match
		ExtractorResult matchItem = extractor.extract("");
	}
	
	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testQuickCheckNull() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/quickcheckmorphlines.properties");
		Assert.assertNotNull(extractor);
		// Null Match
		ExtractorResult matchItem = extractor.extract(null);
	}
	
	@Test
	public void testQuickCheckPositive() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/quickcheckmorphlines.properties");
		Assert.assertNotNull(extractor);
		// Positive Match Test
		ExtractorResult matchItem = extractor.extract(",bob,");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue("MORPHLINE_BOB".equals(matchItem.getTags().get("extractor_name")));

		// Positive Match Test with spaces
		matchItem = extractor.extract(", bob ,");
		Assert.assertNotNull(matchItem);
		matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue("MORPHLINE_BOB".equals(matchItem.getTags().get("extractor_name")));
		
		// Positive Match Test with spaces + prefix and suffix
		matchItem = extractor.extract("boo, bob ,bou");
		Assert.assertNotNull(matchItem);
		matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue("MORPHLINE_BOB".equals(matchItem.getTags().get("extractor_name")));

		// Positive Match Test with spaces
		matchItem = extractor.extract(", fred ,");
		Assert.assertNotNull(matchItem);
		matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue("MORPHLINE_FRED".equals(matchItem.getTags().get("extractor_name")));
		
		// Positive Match Test with spaces + prefix and suffix
		matchItem = extractor.extract("boo, fred ,bou");
		Assert.assertNotNull(matchItem);
		matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("test"));
		Assert.assertTrue("MORPHLINE_FRED".equals(matchItem.getTags().get("extractor_name")));
	}
	
	@Ignore
	@Test
	public void testMemoryLeak() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("morphlines/multiplemorphlinesingleextractor.properties");
		Assert.assertNotNull(extractor);
		for(long i = 0; i < 1000000L; i++) {
			// Positive Match Test
			@SuppressWarnings("unused")
			ExtractorResult matchItem = extractor.extract("bob");
		}
	}
}
