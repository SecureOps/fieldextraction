package com.secureops.fieldextraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FieldExtractorConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(FieldExtractorConfigLoader.class);

    // Global section settings

    // Extractor configuration classes
    private static final String EXTRACTOR_TYPE_KEY = "extractorType";
    private static final String EXTRACTOR_TYPE_DEFAULT = "array";
    private static final String EXTRACTOR_TYPE_TREE = "tree";
    private static final String EXTRACTOR_TYPE_ARRAY = "array";

    private static final String BREAK_ON_ERROR_KEY = "breakOnError";
    private static final String ACTIVE_EXTRACTORS_KEY = "activeExtractors";

    // Config loaders
    private static final String EXTRACTOR_ITEM_CONFIG_LOADER_KEY = "extractorClasses";

    // General IFieldExtractorItem settings (for each extractor section)
    private static final String EXTRACTOR_ITEM_NAME_KEY = "name";
    private static final String EXTRACTOR_ITEM_VERSION_KEY = "version";
    private static final String EXTRACTOR_ITEM_PRIORITY_KEY = "priority";
    private static final int EXTRACTOR_ITEM_PRIORITY_DEFAULT = 10000000;

    static final String EXTRACTOR_ITEM_TYPE_KEY = "type";
    public static final String EXTRACTOR_ITEM_QUICKCHECK_KEY = "quickcheck";

    // Not used in the configuration, only used to set the tag names on the objects
    static final String EXTRACTOR_ITEM_NAME_TAG = "extractor_name";
    static final String EXTRACTOR_ITEM_VERSION_TAG = "extractor_version";
    
    private static Configuration openConfigurationFile(String configFileName) throws ConfigurationException {
        // We may have situations where we don't want to load a configuration
        // file, in that case return null
        if (configFileName == null || configFileName.isEmpty()) {
            throw new ConfigurationException("No configuration file provided");
        }
        PropertiesConfiguration confFile = new PropertiesConfiguration(configFileName);
        logger.info("Opening configuration file " + confFile.getPath());
        // Load the actual root file
        return confFile;
    }
    public static Configuration parseConfiguration(String configFileName) throws ConfigurationException {

    		logger.debug(configFileName);
        Configuration config = FieldExtractorConfigLoader.openConfigurationFile(configFileName);
        boolean throwOnBreak = config.getBoolean(BREAK_ON_ERROR_KEY, false);
        if (throwOnBreak) {
            logger.info("Flag to throw errors in config file is set");
        }
        
    	// List of FieldExtractorItem configuration loader classes to use
        Map<String, IFieldExtractorItemConfigLoader> IFEILoaders = FieldExtractorConfigLoader.getLoaders(config);
        
        List<Object> activeExtractors = config.getList(ACTIVE_EXTRACTORS_KEY);
        for (Object section : activeExtractors) {
            String sectionName = (String) section;
            if (sectionName.isEmpty()) {
                logger.warn("Unable to load active extractor item, section name is blank");
                continue;
            }
            try {
                FieldExtractorConfigLoader.parseSection(config.subset(sectionName), sectionName, IFEILoaders);
            } catch (Exception e) {
                if (throwOnBreak) {
                    logger.error(e.getMessage());
                    throw new ConfigurationException(e.getMessage());
                }
                logger.warn("Unable to load section " + sectionName + ": " + e.getMessage());
                continue;
            }
        }

        return config;
    }

    public static FieldExtractor loadConf(Configuration config) throws ConfigurationException {

        // The field extractor item we will be returning
        FieldExtractor ret = null;

        boolean throwOnBreak = config.getBoolean(BREAK_ON_ERROR_KEY, false);
        if (throwOnBreak) {
            logger.info("Flag to throw errors in config file is set");
        }

        String extractor_type = config.getString(EXTRACTOR_TYPE_KEY,
                EXTRACTOR_TYPE_DEFAULT);

        switch (extractor_type) {
            case EXTRACTOR_TYPE_TREE:
                ret = new TreeSetExtractor();
                break;
            case EXTRACTOR_TYPE_ARRAY:
            default:
                ret = new ArrayExtractor();
        }
        Map<String, IFieldExtractorItemConfigLoader> IFEILoaders = FieldExtractorConfigLoader.getLoaders(config);
        
        List<Object> activeExtractors = config.getList(ACTIVE_EXTRACTORS_KEY);
        for (Object section : activeExtractors) {
            String sectionName = (String) section;
            if (sectionName.isEmpty()) {
                logger.warn("Unable to load active extractor item, section name is blank");
                continue;
            }
            try {
                ret.addExtractor(loadSection(config.subset(sectionName),
                        sectionName, IFEILoaders));
            } catch (Exception e) {
                if (throwOnBreak) {
                    logger.error(e.getMessage());
                    throw new ConfigurationException(e.getMessage());
                }
                logger.warn("Unable to load section " + sectionName + ": " + e.getMessage());
                continue;
            }
        }
        return ret;
    	
    }
    
    public static FieldExtractor loadConf(String configFileName)
            throws ConfigurationException {

        return FieldExtractorConfigLoader.loadConf(FieldExtractorConfigLoader.openConfigurationFile(configFileName));
    }

    private static Map<String, IFieldExtractorItemConfigLoader> getLoaders(Configuration config) throws ConfigurationException {
    	// List of FieldExtractorItem configuration loader classes to use
        Map<String, IFieldExtractorItemConfigLoader> IFEILoaders = new HashMap<String, IFieldExtractorItemConfigLoader>();
        // Process the supported extractor type and their loader classes
        List<String> extractorClasses = new ArrayList<String>(
                Arrays.asList(config
                        .getStringArray(EXTRACTOR_ITEM_CONFIG_LOADER_KEY)));

        for (String extractorClass : extractorClasses) {
        	    logger.info(extractorClass);
            try {
                IFieldExtractorItemConfigLoader confLoader = (IFieldExtractorItemConfigLoader) Class
                        .forName(extractorClass).newInstance();
                logger.info(
                        "Loaded extractor conf loader class {} with type key {}",
                        extractorClass, confLoader.typeString());
                // Load global values into config
                confLoader.loadGlobals(config);
                IFEILoaders.put(confLoader.typeString(), confLoader);
            } catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }
        return IFEILoaders;
    }
    
    private static IFieldExtractorItem loadSection(Configuration config,
            String section,
            Map<String, IFieldExtractorItemConfigLoader> IFEILoaders)
            throws Exception {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Loading section " + section);
    	}
        IFieldExtractorItem ret = null;
        String extractor_name = config
                .getString(EXTRACTOR_ITEM_NAME_KEY, section);
        if (extractor_name == null || extractor_name.isEmpty()) {
            logger.error(
                    "Unable to load active extractor {}, {} is blank or null",
                    section, EXTRACTOR_ITEM_NAME_KEY);
            return null;
        }

        float extractor_version = config.getFloat(EXTRACTOR_ITEM_VERSION_KEY, 0);
        if (extractor_version == 0) {
            logger.warn(
                    "We strongly suggest setting a version number for active extractor {}",
                    section);
        }

        String extractor_type = config.getString(EXTRACTOR_ITEM_TYPE_KEY, "");
        logger.debug("Loading extractor type {}", extractor_type);
        if (IFEILoaders.containsKey(extractor_type)) {
            ret = IFEILoaders.get(extractor_type).loadItemConfiguration(config,
                    section);
        }
        if (ret == null) {
            throw new Exception("Unable to load Active Extractor " + section
                    + ", missing valid key " + EXTRACTOR_ITEM_TYPE_KEY);
        }

        int extractor_priority = config.getInt(EXTRACTOR_ITEM_PRIORITY_KEY,
                EXTRACTOR_ITEM_PRIORITY_DEFAULT);
        ret.setPriority(extractor_priority);
        ret.addTag(EXTRACTOR_ITEM_NAME_TAG, extractor_name);
        ret.addTag(EXTRACTOR_ITEM_VERSION_TAG, String.valueOf(extractor_version));
        return ret;
    }
    
    private static void parseSection(Configuration config, String section, Map<String, IFieldExtractorItemConfigLoader> IFEILoaders) throws ConfigurationException, IOException {
        String extractor_type = config.getString(EXTRACTOR_ITEM_TYPE_KEY, "");
        logger.debug("Loading extractor type {}", extractor_type);
        if (IFEILoaders.containsKey(extractor_type)) {
            IFEILoaders.get(extractor_type).parseConfiguration(config, section);
        }   	
    }
}
