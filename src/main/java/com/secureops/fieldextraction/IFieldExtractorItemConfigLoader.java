package com.secureops.fieldextraction;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
public interface IFieldExtractorItemConfigLoader {
	public String typeString();
	public void loadGlobals(Configuration config) throws Exception;
	public IFieldExtractorItem loadItemConfiguration(Configuration config, String section) throws Exception;
	public void parseConfiguration(Configuration config, String section) throws ConfigurationException, IOException;
}
