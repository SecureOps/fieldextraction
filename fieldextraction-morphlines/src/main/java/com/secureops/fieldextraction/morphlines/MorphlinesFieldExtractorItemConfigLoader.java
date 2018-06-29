package com.secureops.fieldextraction.morphlines;

import java.io.IOException;

import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.kitesdk.morphline.base.Compiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.ConfigUtils;
import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.FieldExtractorConfigLoader;
import com.secureops.fieldextraction.IFieldExtractorItem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;

public class MorphlinesFieldExtractorItemConfigLoader implements
        com.secureops.fieldextraction.IFieldExtractorItemConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MorphlinesFieldExtractorItemConfigLoader.class);

    protected static String MORPHLINES_CONFIG_FILE_KEY = "configFile";
    protected static String MORPHLINES_CONFIG_STRING_KEY = "morphlinesJSONString";
    protected static String MORPHLINES_CONFIG_ENCODED_KEY = "morphlinesB64String";
    private static String MORPHLINES_TYPE_STRING_KEY = "morphlines";
    private static String MORPHLINES_TEST_STRING_KEY = "testString";
    private static String MORPHLINES_TEST_ON_LOAD_KEY = "testOnLoad";
    private static boolean MORPHLINES_TEST_ON_LOAD_DEFAULT = false;
    private static String MORPHLINES_CONFIG_QUICKCHECK_DEFAULT = ".*";

    
    private String section = null;
    public String typeString() {
        return MORPHLINES_TYPE_STRING_KEY;
    }

    public void loadGlobals(Configuration config) throws Exception {

    }

    /**
     * This function will load retrieve the contents of the morphlines config file
     * 
     * If the configuration library supports remote filesystems, it will copy the
     * Morphline configuration file locally as well as inspect the contents for all
     * the "definititionFile" directives in grok commands and copy those locally as
     * well.
     * 
     * @param config
     * @return a String containing the contents of the morphlines config file
     * @throws ConfigurationException 
     * @throws IOException 
     * @throws DecoderException 
     * @throws Exception 
     */
    public static String getMorphlineConfigString(Configuration config, String section) throws ConfigurationException, IOException, DecoderException {
		String morphlinesConfigString = null;
		if(config.containsKey(MORPHLINES_CONFIG_STRING_KEY)) {
			morphlinesConfigString = config.getString(MORPHLINES_CONFIG_STRING_KEY);
		}
		else if(config.containsKey(MORPHLINES_CONFIG_ENCODED_KEY)) {
			morphlinesConfigString = new String(Hex.decodeHex(config.getString(MORPHLINES_CONFIG_ENCODED_KEY).toCharArray()));
		}
		else if(config.containsKey(MORPHLINES_CONFIG_FILE_KEY)) {
			String morphlinesConfigFileString = config.getString(MORPHLINES_CONFIG_FILE_KEY);	
	        LOG.info("Loading morphlines configuration file via: "
	                + morphlinesConfigFileString);
	        try {
	        	morphlinesConfigString = ConfigUtils.getTextFileContent(morphlinesConfigFileString);
	        }
	        catch(Exception e) {
	        	LOG.error("Unable to retrieve morphline file " + morphlinesConfigFileString + ": " + e.getMessage());
	        	throw new IOException("Unable to retrieve morphline file " + morphlinesConfigFileString + ": " + e.getMessage());
	        }
		}
 

		if(morphlinesConfigString == null || morphlinesConfigString.isEmpty()) {
			throw new ConfigurationException("Unable to load Morphline string, it is either emtpy or null");
		}
		return morphlinesConfigString;
    }
    
    @Override
    public IFieldExtractorItem loadItemConfiguration(Configuration config,
            String section) throws IOException, ConfigurationException{
    	if(LOG.isDebugEnabled()) {
    		LOG.debug("Loading section " + section);
    	}

		this.section = section;
		String morphlinesConfigString = null;
		try {
			morphlinesConfigString = getMorphlineConfigString(config, section);
		}
		catch(DecoderException e) {
			throw new ConfigurationException(e);
		}
		com.typesafe.config.Config typesafeConf = null;
		try {
			typesafeConf = com.typesafe.config.ConfigFactory.parseString(morphlinesConfigString);
		}
		catch (Exception e) {
			throw new ConfigurationException("Unable to load morphline string : " + morphlinesConfigString + ": " + e.getMessage());
		}
		
		com.typesafe.config.Config kiteConf = null;
		try {
			kiteConf = typesafeConf.resolve();
		}
		catch (Exception e) {
			throw new ConfigurationException("Unable to resolve morphline string: " + morphlinesConfigString);
		}
		List<? extends Config> mls = kiteConf.getConfigList("morphlines");

        MorphlinesFieldExtractorItem morphlinesItem = new MorphlinesFieldExtractorItem();
		
		for(Config ml: mls) {
				if(ml.hasPath("id")) {
		        	String id = ml.getString("id");
					Compiler compiler = new Compiler();
			        try {
			        	String tConf = compiler.find(ml.getString("id"), kiteConf, "Unable to parse morphline for section " + id).root().render(ConfigRenderOptions.concise());
				        morphlinesItem.addMorphline(new MorphlineItem(tConf));
				        if(LOG.isDebugEnabled()) {
				        	LOG.debug("Adding Morphline ID: " + id);
				        }
			        }
			        catch(Exception e) {
			        	throw new ConfigurationException("Unable to compile morphline for section " + ml.getString("id"));
			        }
				}				
		}
		
		// Store the config text since it's the only way to differentiate between two ExtractorItems
		morphlinesItem.setConfigText(kiteConf.root().render(ConfigRenderOptions.concise()));
        
        // We can test the morphline to see if it matches
        if(config.getBoolean(MORPHLINES_TEST_ON_LOAD_KEY, MORPHLINES_TEST_ON_LOAD_DEFAULT)){
        	String testString = config.getString(MORPHLINES_TEST_STRING_KEY);
        	if(testString == null || testString.isEmpty()) {
        		LOG.error("ExtractorItem " + this.section + " requires to test the item but doesn't have a " + MORPHLINES_TEST_STRING_KEY + " set.");
        		throw new ConfigurationException("ExtractorItem " + section + " requires to test the item but doesn't have a " + MORPHLINES_TEST_STRING_KEY + " set.");
        	}
        	
        	ExtractorResult match = morphlinesItem.extract(testString);
        	
        	if(match == null || match.getMatches().isEmpty()) {
        		LOG.error("Test String: " + testString + " | doesn't match morphlines in  " + morphlinesConfigString);
        		throw new ConfigurationException("Test String: " + testString + " | doesn't morphlines in " + morphlinesConfigString);
        	}
        }
        
        morphlinesItem.setPattern(config.getString(FieldExtractorConfigLoader.EXTRACTOR_ITEM_QUICKCHECK_KEY, MORPHLINES_CONFIG_QUICKCHECK_DEFAULT));
        return morphlinesItem;
    }

	@Override
	public void parseConfiguration(Configuration config, String section) throws ConfigurationException, IOException {
		if(!config.containsKey(MORPHLINES_CONFIG_STRING_KEY)) {
			if(config.containsKey(MORPHLINES_CONFIG_FILE_KEY)) {
				try {
					config.setProperty(MORPHLINES_CONFIG_ENCODED_KEY, Hex.encodeHexString(getMorphlineConfigString(config, section).getBytes()));
				}
				catch (DecoderException e) {
					throw new ConfigurationException(e);
				}
				config.clearProperty(MORPHLINES_CONFIG_FILE_KEY);
			}
			else {
				LOG.error("Morphline defined in extractor " + section + " must include " 
					       + MORPHLINES_CONFIG_FILE_KEY + " if " + MORPHLINES_CONFIG_STRING_KEY + " is not present");
				throw new ConfigurationException("Morphline defined in extractor " + section + " must include " 
			       + MORPHLINES_CONFIG_FILE_KEY + " if " + MORPHLINES_CONFIG_STRING_KEY + " is not present");
			}
		}
		
	}
}
