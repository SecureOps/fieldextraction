package com.secureops.fieldextraction.regex;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RegexFieldExtractorItemFactory {
	private static final Logger logger = LoggerFactory.getLogger(RegexFieldExtractorItemFactory.class);
	private static final String PYTHON_SCRIPT = "jython/RegexFieldExtractorItem.py";

	private static RegexFieldExtractorItemFactory instance = null;
	private static PyObject pyObject = null;
	
	public synchronized static RegexFieldExtractorItemFactory getInstance(){
		if(instance == null){
			instance = new RegexFieldExtractorItemFactory();
		}

		return instance;

	}
	
	protected RegexFieldExtractorItemFactory() {

	}
	
	public static IRegexFieldExtractorItem newRegexFieldExtractorItem() throws Exception {
		StringWriter writer = new StringWriter();
		InputStream regexFieldExtractorItemStream = java.lang.ClassLoader.getSystemResourceAsStream(PYTHON_SCRIPT);		
		try {
			if (regexFieldExtractorItemStream != null) {
				IOUtils.copy(regexFieldExtractorItemStream, writer, "UTF-8");
			}
			else {
				throw new IOException("Stream is null");
			}
		}
		catch (IOException e) {
			logger.error("Error opening Python RegexListItem.py file: %s", e.getMessage());
			throw new Exception("Error opening Python RegexFieldExtractorItem.py file: " + e.getLocalizedMessage());
		}
		
		PythonInterpreter interpreter = new PythonInterpreter();
		
		interpreter.exec("import sys");
		interpreter.exec("import logging");
		interpreter.exec(writer.toString());
		if(logger.isDebugEnabled()) {
			interpreter.exec(
					"logging.basicConfig(datefmt='%y/%m/%d %H:%M:%S', " +
					"format='%(asctime)s %(levelname)s %(module)s: %(message)s', level=logging.DEBUG)");
		}
		else {
			interpreter.exec(
					"logging.basicConfig(datefmt='%y/%m/%d %H:%M:%S', format='%(asctime)s %(levelname)s %(module)s: %(message)s')");
		}

		pyObject = interpreter.get("RegexFieldExtractorItem");
		PyObject jyRegexParserObj = pyObject.__call__();
		return (IRegexFieldExtractorItem) jyRegexParserObj.__tojava__(IRegexFieldExtractorItem.class);
		
	}
	
	public static IRegexFieldExtractorItem newRegexFieldExtractorItem(String RegexPattern) throws Exception {
		IRegexFieldExtractorItem item = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		try {
			item.setRegexString(RegexPattern);
		}
		catch(PyException e) {
			e.normalize();
			PyObject message = e.value.__str__();
			throw new Exception(message.asString());
		}
		
		return item;
	}
	
	public static IRegexFieldExtractorItem newRegexFieldExtractorItem(String RegexPattern, int priority) throws Exception {
		IRegexFieldExtractorItem item = RegexFieldExtractorItemFactory.newRegexFieldExtractorItem();
		try {
			item.setRegexString(RegexPattern);
		} catch (Exception e) {
			logger.error("Regex threw an exception " + e.getMessage());
		}
		item.setPriority(priority);
		return item;
	}

}
