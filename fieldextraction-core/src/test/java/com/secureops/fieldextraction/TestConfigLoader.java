package com.secureops.fieldextraction;

import static org.junit.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class TestConfigLoader {
	
	private static final String basicConfigurationFileName = "fieldextraction.properties";
	private static final String missingConfig = "missing.properties";
	private static final String includeTopGood = "includetopgood.properties";
	private static final String includeTopBad = "includetopbad.properties";
	private static final String includeCyclic = "includecyclic1.properties";
	private static final String breakOnError = "breakonerror.properties";
	
	private static final String basicConfigSectionName = "extractor1";
	private static final String basicConfigMockAddedKey = "newprop";


	@Test
	public void testLoadBasicConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf(basicConfigurationFileName);
		assertNotNull(extractor);
	}
	
	@SuppressWarnings("unused")
	@Test (expected = ConfigurationException.class)
	public void testLoadBlankConfigFileString() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("");		
	}
	
	@SuppressWarnings("unused")
	@Test (expected = ConfigurationException.class)
	public void testLoadEmptyConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf("");

	}
	
	@Test
	public void testLoadIncludeGoodConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf(includeTopGood);
		assertNotNull(extractor);
	}
	
	@SuppressWarnings("unused")
	@Test (expected=StackOverflowError.class)
	public void testLoadIncludeCyclicConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf(includeCyclic);
	}
	
	@SuppressWarnings("unused")
	@Test (expected=ConfigurationException.class)
	public void testLoadBreakOnErrorConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf(breakOnError);
	}
	
	@SuppressWarnings("unused")
	@Test (expected=ConfigurationException.class)
	public void testLoadMissingConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf(missingConfig);
	}
	
	@SuppressWarnings("unused")
	@Test (expected=ConfigurationException.class)
	public void testLoadIncludeWithMissingConfig() throws ConfigurationException {
		FieldExtractor extractor = FieldExtractorConfigLoader.loadConf(includeTopBad);
	}

	@Test
	public void testGetConf() throws ConfigurationException {
		Configuration conf = FieldExtractorConfigLoader.parseConfiguration(basicConfigurationFileName);
		assertNotNull(conf.subset(basicConfigSectionName));
		assertTrue(conf.subset(basicConfigSectionName).getBoolean(basicConfigMockAddedKey));
	}
}
