package com.secureops.fieldextraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ConfigUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigUtils.class);
	
	public static String joinStrings(String basePath, List<String> stringList, String delimiter) {
		return ConfigUtils.joinStrings(basePath, stringList, delimiter, false);
	}
	
	// TODO: This is Java 7+ only!!!
	public static String getTextFileContent(String textFileNameString) throws IOException, URISyntaxException, MalformedURLException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Retrieving contents of " + textFileNameString);
		}
		File textFile =  new File(textFileNameString);
		String ret = null;
		if(textFile == null || !textFile.exists() || !textFile.isFile()) {
			InputStream inStream = ConfigUtils.class.getClassLoader().getResourceAsStream(textFileNameString);
			if (inStream == null) {
				if(LOG.isDebugEnabled()){
					LOG.debug("Loading " + textFileNameString + " as a URL");
				}
				try {
					URL httpURL = new URL(textFileNameString);
					inStream = httpURL.openStream();
				} catch(Exception e) {
					throw new IOException("Unable to load " + textFileNameString + " as a URL");
				}
				
			}
			else {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Loading " + textFileNameString + " as a Java Resource file");
				}
			}
			try {
				ret = IOUtils.toString(inStream);
			}
			catch (Exception e){
				// This shouldn't happen because technically NPE or IO exception shouldn't happen by this point
				throw new IOException("Unable to load " + textFileNameString + " as a Resource file");
			}
			
			// As per JavaDoc, this does nothing, but do it anyways due to convention
			inStream.close();
		}
		else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Loading " + textFileNameString + " as a local filesystem file");
			}
			byte[] encoded = Files.readAllBytes(textFile.toPath());
			ret = new String(encoded, "UTF-8");
		}
		return ret;
	}
	
	public static String joinStrings(String basePath, List<String> stringList, String delimiter, boolean addQuotes) {
		String ret = "";
		String quote = "";
		if(addQuotes) {
			quote = "\"";
		}
		int cnt = 0;
		for(String file: stringList) {
			if(++cnt < stringList.size()) {
				ret += quote + basePath + file + quote + delimiter;
			}
			else {
				ret += quote + basePath + file + quote;
			}
		}
		return ret;
	}

	public static Configuration getTopParent(Configuration conf) {
		if(conf instanceof SubsetConfiguration) {
			Configuration parentConf = ((SubsetConfiguration)conf).getParent();
			if(parentConf != null) {
				return ConfigUtils.getTopParent(parentConf);
			}
		}
		return conf;		
	}
	
	private static void createTempParentDir(File parent, boolean autoDelete){
		LOG.info("Inspecting " + parent.getAbsolutePath());
		// Keep digging down until we finally reach a directory in our path that exists
		if(!parent.exists() && !parent.isDirectory()) {
			if(parent.getParentFile() != null) {
				// See if our parent needs creating too...
				createTempParentDir(parent.getParentFile(), autoDelete);
			}
			
			// We don't exists, so create ourself
			LOG.info("Creating " + parent.getAbsolutePath());
			parent.mkdir();
			if(autoDelete) {
				parent.deleteOnExit();
			}
		}
	}
	
	public static File createFileFromInputStream(InputStream stream, String target, boolean autoDelete) throws IOException{
		// NOTE: This is technically not "Thread" safe. If two threads use the same working directory
		// the first thread to exit will delete all the other's temporary files
		// but since we're only using these files to load extractors, once the extractor is loaded it should
		// no longer be necessary.
		
		File ret = new File(target);
		
		// Check if our target is a path to a file, if so, go up the directory structure to
		// create it
		File parent = ret.getParentFile();
		if(parent != null) {
			// Create the hierarchy, if not, it'll dump our temp filesystem into the working directory
			createTempParentDir(parent, autoDelete);
		}
		
		// "touch" our target
		ret.createNewFile();
		// And make sure we delete our file when the process exits
		if(autoDelete) {
			ret.deleteOnExit();
		}
		
		// Read our stream into our target
		byte[] buffer = new byte[stream.available()];
		stream.read(buffer);

		OutputStream out = new FileOutputStream(ret);
		out.write(buffer);
		out.close();

		// Don't close the inputstream, that's the job of the caller to do with it
		// as he pleases, just return our target
		LOG.info("Created file " + ret.getAbsolutePath());
		return ret;
	}
}
