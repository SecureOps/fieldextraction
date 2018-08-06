package com.secureops.fieldextraction.grok;

import java.util.Map;

import java.io.IOException;

import java.net.URL;
import java.net.HttpURLConnection;

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
	private static final String strUrl = "https://secureops.github.io/fieldextraction-rules/grokpatterns/test-patterns";

	private static boolean checkWebBasedPatterns() {
    try {
        URL url = new URL(strUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();
				if(HttpURLConnection.HTTP_OK == urlConn.getResponseCode()) {
					return true;
				}

    } catch (IOException e) {
        System.err.println("Error creating HTTP connection");
        LOG.info("Unable to connect to web, skipping web pattern test: " + e.getMessage());
    }
		return false;
}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigNegativeMatch() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-defaults.properties");
		Assert.assertNotNull(extractor);

		ExtractorResult matchItem = extractor.extract("Bob the Builder");
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigEmpty() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-defaults.properties");
		Assert.assertNotNull(extractor);

		ExtractorResult matchItem = extractor.extract("");
	}

	@SuppressWarnings("unused")
	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigNull() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-defaults.properties");
		Assert.assertNotNull(extractor);

		ExtractorResult matchItem = extractor.extract(null);
	}


	@Test
	public void testLoadBasicConfig() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-defaults.properties");
		Assert.assertNotNull(extractor);

		ExtractorResult matchItem = extractor.extract("07/08/09 12:22:44");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("DATESTAMP"));
	}

	@Test
	public void testLoadBasicConfigFromWeb() throws Exception
	{
		if (TestGrokConfigLoader.checkWebBasedPatterns()) {
			FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-external-patterns-web.properties");
			Assert.assertNotNull(extractor);
			ExtractorResult matchItem = extractor.extract("07/08/09 12:22:44");
			Assert.assertNotNull(matchItem);
			Map<String, String> matches = matchItem.getMatches();
			Assert.assertNotNull(matches);
			Assert.assertTrue(matches.containsKey("TESTDATESTAMP"));
		}
	}

	@Test
	public void testLoadBasicConfigFromFile() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-external-patterns-file.properties");
		Assert.assertNotNull(extractor);

		ExtractorResult matchItem = extractor.extract("07/08/09 12:22:44");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("TESTDATESTAMP"));
	}

	@Test(expected = FieldExtractionNoMatchException.class)
	public void testLoadBasicConfigFromFileWithBadPattern() throws Exception
	{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("grok/grokextractors-bad-pattern.properties");
		Assert.assertNotNull(extractor);

		ExtractorResult matchItem = extractor.extract("07/08/09 12:22:44");
		Assert.assertNotNull(matchItem);
		Map<String, String> matches = matchItem.getMatches();
		Assert.assertNotNull(matches);
		Assert.assertTrue(matches.containsKey("TESTDATESTAMP"));
	}
}
