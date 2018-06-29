package com.secureops.fieldextraction.csv;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.FieldExtractor;
import com.secureops.fieldextraction.TreeSetExtractor;
import com.secureops.fieldextraction.FieldExtractionNoMatchException;

public class TestCSVFieldExtractorItem {
	public final Logger logger = LoggerFactory.getLogger(TestCSVFieldExtractorItem.class);

	@Test
	public void simpleMatchTest() throws Exception {
		CSVFieldExtractorItem matcher = new CSVFieldExtractorItem();
		matcher.addField(0, "field1");
		Map<String, String> matches = matcher.matches("987654321");
		assertNotNull(matches);
	}
	
	@Test
	public void setMatchWidthTest() throws Exception {
		CSVFieldExtractorItem matcher = new CSVFieldExtractorItem(true);
		matcher.addField(0, "field1");
		Map<String, String> matches = matcher.matches("987654321,54321");
		assertNull(matches);
	}
	
	@Test
	public void unsetMatchWidthTest() throws Exception {
		CSVFieldExtractorItem matcher = new CSVFieldExtractorItem();
		matcher.addField(0, "field1");
		Map<String, String> matches = matcher.matches("987654321,54321");
		assertNotNull(matches);
	}
	
	@Test
	public void requiredPositiveMatchTest() throws Exception {
		CSVFieldExtractorItem matcher = new CSVFieldExtractorItem();
		matcher.addField(0, "field1", "[0-9]+");
		Map<String, String> matches = matcher.matches("987654321");
		assertNotNull(matches);
	}
	
	@Test
	public void requiredNegativeMatchTest() throws Exception {
		CSVFieldExtractorItem matcher = new CSVFieldExtractorItem();
		matcher.addField(0, "field1", "[0-9]+");
		Map<String, String> matches = matcher.matches("abcdefg");
		assertNull(matches);
	}
	
	
	/*	 Test based on description of the problem as a Use Case
	 * Inputs:
	 * pharell,usa,male,happy
 	 * pharell,,male,happy
 	 * pharell,usa,happy
 	 * pharell,usa,male,happy,a,b,c,d,e
 	 * pharell,usa,happy,number1,tacky
 	 * 
	 * We want to extract the following:
	 *
	 * name, state, gender, status
	 * first_name, country, song

	 * Expected outcome:
	 * The first would "addField" for name,state,gender and status, put the required field on "gender" to match (male|female) and not set an attribute width match...
	 * So this would match the first three patterns.
	 * The second extractor item "addField" name, country, status and simply set an attribute for the width (3) to match.
	 * 
	 * Results:

	 * {"name": "pharell", "state": "usa", "gender": "male", "status": "happy"} (first extractor)
	 * {"name": "pharell", "gender": "male", "status":happy} (still first extractor because the field "gender" matched)
	 * {"first_name": "pharell", "country": "usa", "song": "happy"} (second matched)
	 * {"name":"pharell", "state": "usa", "gender": "male", "status": "happy"} (still the first extractor because the gender matched and all extra fields are ignored because of the lack of "match width"
	 * The last row wouldn't match anything..
	 */	 
	@Test(expected = FieldExtractionNoMatchException.class)
	public void multipleMatchesTestNegative() throws Exception {
		FieldExtractor extractor = new TreeSetExtractor();
		
		CSVFieldExtractorItem matcher1 = new CSVFieldExtractorItem();
		matcher1.addField(0, "Name");
		matcher1.addField(1, "Country");
		matcher1.addField(2, "Gender", "(male|female)");
		matcher1.addField(3, "Status");
		extractor.addExtractor(matcher1);
		
		CSVFieldExtractorItem matcher2 = new CSVFieldExtractorItem(true);
		matcher2.addField(0, "first_name");
		matcher2.addField(1, "country");
		matcher2.addField(2, "song");
		matcher2.setMatchWidth();
		extractor.addExtractor(matcher2);
		
		String test5 = "pharell,usa,happy,number1,tacky";
		ExtractorResult result5 = extractor.extract(test5);
		assertNull(result5);
	}
	
	@Test
	public void multipleMatchesTest() throws Exception {
		FieldExtractor extractor = new TreeSetExtractor();
		
		CSVFieldExtractorItem matcher1 = new CSVFieldExtractorItem();
		matcher1.addField(0, "Name");
		matcher1.addField(1, "Country");
		matcher1.addField(2, "Gender", "(male|female)");
		matcher1.addField(3, "Status");
		extractor.addExtractor(matcher1);
		
		CSVFieldExtractorItem matcher2 = new CSVFieldExtractorItem(true);
		matcher2.addField(0, "first_name");
		matcher2.addField(1, "country");
		matcher2.addField(2, "song");
		matcher2.setMatchWidth();
		extractor.addExtractor(matcher2);
		
		String test1 = "pharell,usa,male,happy";
		ExtractorResult result1 = extractor.extract(test1);
		assertNotNull(result1);
		Map<String, String> matches1 = result1.getMatches();
		assertNotNull(matches1);
		assertEquals(matches1.get("Gender"), "male");
		
		String test2 = "pharell,,male,happy";
		ExtractorResult result2 = extractor.extract(test2);
		assertNotNull(result2);
		Map<String, String> matches2 = result2.getMatches();
		assertNotNull(matches2);
		assertEquals(matches2.get("Gender"), "male");
		
		String test3 = "pharell,usa,happy";
		ExtractorResult result3 = extractor.extract(test3);
		assertNotNull(result3);
		Map<String, String> matches3 = result3.getMatches();
		assertNotNull(matches3);
		assertEquals(matches3.get("first_name"), "pharell");
		
		String test4 = "pharell,usa,male,happy,a,b,c,d,e";
		ExtractorResult result4 = extractor.extract(test4);
		assertNotNull(result4);
		Map<String, String> matches4 = result4.getMatches();
		assertNotNull(matches4);
		assertEquals(matches4.get("Gender"), "male");		
	}

	@Test (expected = FieldExtractionNoMatchException.class)
	public void skipOffsetsTestNegative() throws Exception {
		FieldExtractor extractor = new TreeSetExtractor();		
		CSVFieldExtractorItem matcher1 = new CSVFieldExtractorItem();
		matcher1.addField(0, "Name");
		matcher1.addField(2, "Gender", "(male|female)");
		matcher1.addField(3, "Status");
		extractor.addExtractor(matcher1);

		String test4 = "pharell,usa,happy,number1,tacky";
		ExtractorResult result4 = extractor.extract(test4);
		assertNull(result4);	
	}
	
	@Test
	public void skipOffsetsTest() throws Exception {
		FieldExtractor extractor = new TreeSetExtractor();		
		CSVFieldExtractorItem matcher1 = new CSVFieldExtractorItem();
		matcher1.addField(0, "Name");
		matcher1.addField(2, "Gender", "(male|female)");
		matcher1.addField(3, "Status");
		extractor.addExtractor(matcher1);
		
		String test1 = "pharell,usa,male,happy";
		ExtractorResult result1 = extractor.extract(test1);
		assertNotNull(result1);
		Map<String, String> matches1 = result1.getMatches();
		assertNotNull(matches1);
		assertEquals(matches1.get("Gender"), "male");
		
		String test2 = "pharell,,male,happy";
		ExtractorResult result2 = extractor.extract(test2);
		assertNotNull(result2);
		Map<String, String> matches2 = result2.getMatches();
		assertNotNull(matches2);
		assertEquals(matches2.get("Gender"), "male");

		String test3 = "pharell,usa,male,happy,a,b,c,d,e";
		ExtractorResult result3 = extractor.extract(test3);
		assertNotNull(result3);
		Map<String, String> matches3 = result3.getMatches();
		assertNotNull(matches3);
		assertEquals(matches3.get("Gender"), "male");
	}
	
	@Test  (expected = Exception.class)
	public void overrideOffsetTest() throws Exception {
		CSVFieldExtractorItem matcher1 = new CSVFieldExtractorItem();
		matcher1.addField(0, "Name");
		matcher1.addField(0, "Gender");

	}
	
	@Test (expected = Exception.class)
	public void overrideFieldNameTest() throws Exception {
		CSVFieldExtractorItem matcher1 = new CSVFieldExtractorItem();
		matcher1.addField(0, "Name");
		matcher1.addField(1, "Name");
	}
}
