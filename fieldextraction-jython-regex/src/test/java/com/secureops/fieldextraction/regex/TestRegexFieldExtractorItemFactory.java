package com.secureops.fieldextraction.regex;

import java.util.Map;

import org.python.core.PyException;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRegexFieldExtractorItemFactory {

	@Test
	public void simpleMatchTest() throws Exception {
		IRegexFieldExtractorItem matcher = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		matcher.setRegexString("(?P<numbers>[0-9]+)");
		Map<String, String> matches = matcher.matches("987654321");
		assertNotNull(matches);
	}
	
	@Test
	public void doesNotMatchTest() throws Exception {
		IRegexFieldExtractorItem matcher = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		matcher.setRegexString("(?P<numbers>[a-z]+)");
		Map<String, String> matches = matcher.matches("987654321");
		assertNull(matches);
	}
	
	@Test (expected = PyException.class)
	public void badRegexString() throws Exception {
		IRegexFieldExtractorItem matcher = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		matcher.setRegexString("(?P<numbers>[0-9]+");
	}
	
	@Test
	public void testCompareTo() throws Exception {
		IRegexFieldExtractorItem priority2 = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		priority2.setPriority(2);
		
		IRegexFieldExtractorItem priority1 = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		priority1.setPriority(1);
		
		assertEquals(priority1.compareTo(priority2), -1);
		assertEquals(priority2.compareTo(priority1), 1);

		assertEquals(priority1.compareTo(priority1), 0);
		
		
	}

}
