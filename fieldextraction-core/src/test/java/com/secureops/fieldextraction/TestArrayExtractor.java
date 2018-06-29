package com.secureops.fieldextraction;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayExtractor {

	@Test(expected = FieldExtractionNoMatchException.class)
	public void testNoExtractorEntries() throws Exception{
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("ArrayExtractorTests/noExtractorItemsTest.properties");		
		extractor.extract("987654321");
	}
	
	@Test
	public void testOneFieldExtractorEntry() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("ArrayExtractorTests/oneFieldTest.properties");		
		
		ExtractorResult match = extractor.extract("987654321");
		assertNotNull(match);
		assertNotNull(match.getMatches());
	}
	
	@Test
	public void testMultipleFieldExtractorEntry() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("ArrayExtractorTests/multipleFieldTest.properties");		
		
		ExtractorResult match = extractor.extract("987654321");
		assertNotNull(match);
		assertNotNull(match.getMatches());
		assertEquals(match.getTags().get("extractor_name"), "numbermatch");
		
		
		match = extractor.extract("abcdefg");
		assertNotNull(match);
		assertNotNull(match.getMatches());
		assertEquals(match.getTags().get("extractor_name"), "lettermatch");
	}	
}
