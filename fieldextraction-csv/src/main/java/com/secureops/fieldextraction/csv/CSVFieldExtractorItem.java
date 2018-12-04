package com.secureops.fieldextraction.csv;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secureops.fieldextraction.ExtractorResult;
import com.secureops.fieldextraction.IFieldExtractorItem;
import com.secureops.fieldextraction.FieldExtractionItemException;

public class CSVFieldExtractorItem implements IFieldExtractorItem {
	private final Logger logger = LoggerFactory.getLogger(CSVFieldExtractorItem.class);

	private boolean matchWidth = false;

	private Map<Integer, String> fieldMap = new TreeMap<Integer, String>();

	// List of required fields with their associated regex
	private Map<String, RequiredField> requiredFieldMap = new HashMap<String, RequiredField>();

	// Tags set by the user to describe this entry
	private Map<String, String> tags = new TreeMap<String, String>();

	private int priority = 10000000;

	public CSVFieldExtractorItem() {

	}

	public CSVFieldExtractorItem(boolean matchWidth) {
		if (matchWidth) {
			this.setMatchWidth();
		} else {
			this.unsetMatchWidth();
		}
	}

	public void addField(int offset, String fieldName) throws FieldExtractionItemException{
		this.addField(offset, fieldName, null);
	}

	public void addField(int offset, String fieldName, String regex)
			throws FieldExtractionItemException {
		if (offset < 0) {
			throw new FieldExtractionItemException("Field offset must be 0 or positive");
		}

		if (this.fieldMap.containsKey(offset)) {
			throw new FieldExtractionItemException("CSV Extractor already has an offet of "
					+ String.valueOf(offset) + " defined");
		}

		if (fieldName == null || fieldName.isEmpty()) {
			throw new FieldExtractionItemException("fieldName must have a value");
		}

		if (this.fieldMap.containsValue(fieldName)) {
			throw new FieldExtractionItemException("CSV Extractor already has a field named "
					+ fieldName);
		}

		if (regex != null && !regex.isEmpty()) {
			this.addRequiredField(fieldName, regex);
		}
		this.fieldMap.put(offset, fieldName);
	}

	public void addRequiredField(String fieldName, String regex)
			throws FieldExtractionItemException {
		if (!requiredFieldMap.containsKey(fieldName)) {
			RequiredField field = new RequiredField(fieldName, regex);
			requiredFieldMap.put(fieldName, field);
		} else {
			throw new FieldExtractionItemException("Fieldname " + fieldName
					+ " has already been declare as a required field");
		}
	}

	public boolean matchWidth() {
		return this.matchWidth;
	}

	public void setMatchWidth() {
		this.matchWidth = true;
	}

	public void unsetMatchWidth() {
		this.matchWidth = false;
	}

	public String getRequiredFieldExpression(String fieldname) {
		return requiredFieldMap.get(fieldname).getRegex();
	}

	public Map<Integer, String> getFieldList() {
		return Collections.unmodifiableMap(fieldMap);
	}

	private Map<String, String> matches(CSVRecord record) {
		// Contain the fieldname/value pairs
		Map<String, String> matches = new HashMap<String, String>();
		
		// int to record how many required fields we matched
		int matchedRequiredFields = 0;

		if (this.matchWidth() && this.fieldMap.size() != record.size()) {
			StringBuilder message = new StringBuilder();
			
			message.append("Amount of fields don't match, Map entries ");
			message.append(String.valueOf(this.fieldMap.size()));
			message.append(" - record entries ");
			message.append(String.valueOf(record.size()));
			message.append(" - \nOriginal msg: (");
			message.append(String.valueOf(record.toString()));
			message.append(")");
			logger.debug(message.toString());
			return null;
		}

		// Iterate through the CSV records. As we go along, check required
		// fields and other conditions
		for (int i = 0; i < record.size(); i++) {
			// Note, we may actually skip out on some fieldEntries so it could
			// happen
			// that the fieldList does not contain a field to match at a certain
			// offset
			if (this.fieldMap.get(i) != null) {
				String fieldName = this.fieldMap.get(i);
				String value = record.get(i);
				logger.debug("FieldName is {}", this.fieldMap.get(i));
				RequiredField matchme = requiredFieldMap.get(fieldName);
				if (matchme != null) {
					if (!matchme.matches(value)) {
						logger.error("Value {} doesn't match regex {}", value,
								matchme.getRegex());
						return null;
					}
					matchedRequiredFields++;
				}
				matches.put(fieldName, value);
				logger.debug("Name: {}, Value {}", fieldName, value);
			}
		}

		// Check if we hit all the required fields by comparing our
		// matchedRequiredFields counter and the size of the requireFieldMap
		if (requiredFieldMap.size() != matchedRequiredFields) {
			return null;
		}
		return matches;
	}

	public Map<String, String> matches(String s) {
		logger.debug("Matching CSV {}", s);

		CSVFormat myFormat = CSVFormat.DEFAULT;
		Reader myReader = new StringReader(s);
		Map<String, String> matchedFieldMap = null;
		try {
			// !!!FIX!!!
			// CSVParser library changed and they didn't bother to update their
			// documentation.
			// This no longer works:
			// CSVParser parser = new CSVParser(s, CSVFormat.DEFAULT);
			// but this does:
			CSVParser parser = new CSVParser(myReader, myFormat);
	
			// There should always be one record since each event should have
			// just one line
			// But if that changes the logic below won't work
			// TODO multi-line input!
			List<CSVRecord> records = parser.getRecords();
			if (records.isEmpty()) {
				logger.debug("Records returned empty");
			} else {
				for (CSVRecord record : records) {
					// Like we mentioned above, this will only process the first
					// line!
					matchedFieldMap = this.matches(record);
				}
			}
			parser.close();
		}
		catch (Exception e) {
			logger.error("Matching CSV failed: " + e.getLocalizedMessage());
		}
		return matchedFieldMap;
	}

	public ExtractorResult extract(String s) {
		ExtractorResult er = new ExtractorResult();
		Map<String, String> result = this.matches(s);
		if(result != null) {
			er.setMatches(this.matches(s));
		}
		return er;
	}
	
	public String getTag(String tagName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getTags() {
		return Collections.unmodifiableMap(this.tags);
	}

	public int getPriority() {
		return this.priority;
	}

	public void addTag(String tagName, String tagValue) {
		// We default the "overwrite" to true in this case
		this.tags.put(tagName, tagValue);
	}

	public void addTag(String tagName, String tagValue, Boolean overwrite)
			throws FieldExtractionItemException {
		if (this.tags.containsKey(tagName) && overwrite == false) {
			throw new FieldExtractionItemException("Tags already contains an item with key "
					+ tagName);
		}
		this.addTag(tagName, tagValue);
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int compareTo(IFieldExtractorItem o) {
		int ret = 1;
		if (this.getPriority() < o.getPriority()) {
			ret = -1;
		}

		else if (this.getPriority() > o.getPriority()) {
			ret = 1;
		} else {
			// Check if we're the same class type
			if (this.getClass().equals(o.getClass())) {
				if (this.getFieldList().size() == ((CSVFieldExtractorItem) o)
						.getFieldList().size()) {
					ret = 0;
					for (int key : this.getFieldList().keySet()) {
						if (this.getFieldList().get(key) != ((CSVFieldExtractorItem) o)
								.getFieldList().get(key)) {
							ret = 1;
						}
					}
				}
			}
		}
		return ret;
	}

	@Override
	public boolean quickCheck(String match) {
		return true;
	}

}
