package com.secureops.fieldextraction;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestTreeSetExtractor {

	@Test (expected = FieldExtractionNoMatchException.class)
	public void testNoExtractorEntries() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("TreeSetExtractorTests/noExtractorItemsTest.properties");
		extractor.extract("987654321");
	}
	
	@Test
	public void testOneFieldExtractorEntry() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("TreeSetExtractorTests/oneFieldTest.properties");		
		
		ExtractorResult match = extractor.extract("987654321");
		assertNotNull(match);
		assertNotNull(match.getMatches());
	}
	
	@Test
	public void testMultipleFieldExtractorEntryWithDifferentPriority() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("TreeSetExtractorTests/multipleFieldTest.properties");		
		
		ExtractorResult match = extractor.extract("987654321");
		assertNotNull(match);
		assertNotNull(match.getMatches());
		assertEquals(match.getTags().get("extractor_name"), "numbermatch");
		
		match = extractor.extract("abcdefg");
		assertNotNull(match);
		assertNotNull(match.getMatches());
		assertEquals(match.getTags().get("extractor_name"), "lettermatch");
	}

	@Test
	public void testMultipleFieldExtractorEntryWithSamePriority() throws Exception {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("TreeSetExtractorTests/multipleFieldSamePriorityTest.properties");		
				
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
