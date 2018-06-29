package com.secureops.fieldextraction.csv;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.IFieldExtractorItem;
import com.secureops.fieldextraction.IFieldExtractorItemConfigLoader;

public class CSVFieldExtractorItemConfigLoader implements IFieldExtractorItemConfigLoader{
	private static final Logger LOG = LoggerFactory
			.getLogger(CSVFieldExtractorItemConfigLoader.class);
	// CSV specific settings
	private static final String CSVEXTRACTORITEMMATCHWIDTH_KEY = "matchwidth";
	private static final String CSVEXTRACTORITEMFIELDS_KEY = "fields";
	private static final String CSVEXTRACTORITEMFIELDLABEL_KEY = "fieldLabel";
	private static final String CSVEXTRACTORITEMOFFSET_KEY = "offset";
	private static final String CSVEXTRACTORITEMPREGEX_KEY = "jregex";
	private static final String CSVEXTRACTORITEMTYPESTRING = "csv";
	private static final int CSVEXTRACTORITEMBADOFFSETVALUE = 1000000;

	// This should be a static, but the interface pre-Java8 doesn't permit it
	// in the IDL. This is the simplest workaround I could find and does what we want
	@Override
	public String typeString() {
		return CSVEXTRACTORITEMTYPESTRING;
	}

	@Override
	public IFieldExtractorItem loadItemConfiguration(Configuration config,
			String section) throws ConfigurationException {
		boolean matchWidth = config.getBoolean(CSVEXTRACTORITEMMATCHWIDTH_KEY, false);
		List<Object> fields = config.getList(CSVEXTRACTORITEMFIELDS_KEY);
		if(fields.size() == 0) {
			throw new ConfigurationException("No fields key or value is empty for active extractor " + section);
		}
		CSVFieldExtractorItem csvItem = new CSVFieldExtractorItem(matchWidth);
		
		for(Object field : fields) {
			String fieldSection = (String) field;
			LOG.debug("Processing CSV field {}", fieldSection);
			if(fieldSection.isEmpty()) {
				LOG.warn("Field section is blank in section {}", section);
				continue;
			}
			int offset = config.getInt(field + "." + CSVEXTRACTORITEMOFFSET_KEY, CSVEXTRACTORITEMBADOFFSETVALUE);
			if (offset == CSVEXTRACTORITEMBADOFFSETVALUE) {
				throw new ConfigurationException("Missing offset value for active extractor " + section);
			}
			String name = config.getString(field + "." + CSVEXTRACTORITEMFIELDLABEL_KEY, null);
			if (name == null || name.isEmpty()) {
				name = fieldSection;
			}
			String regex = config.getString(field + "." + CSVEXTRACTORITEMPREGEX_KEY, null);
			if (regex == null || regex.isEmpty()) {
				try {
					csvItem.addField(offset, name);
				} catch (Exception e) {
					throw new ConfigurationException("Exception encountered while configuring active extractor " + section + " : " + e.getMessage());

				}
			}
			else {
				try {
					csvItem.addField(offset, name, regex);
				} catch (Exception e) {
					throw new ConfigurationException("Exception encountered while configuring active extractor " + section + " : " + e.getMessage());
				}
			}
		}
		return csvItem;
	}

	@Override
	public void loadGlobals(Configuration config) throws Exception {
		// Not implemented in this case		
	}

	@Override
	public void parseConfiguration(Configuration arg0, String arg1) throws ConfigurationException, IOException {
		// Not implemented in this case
		
	}

}
