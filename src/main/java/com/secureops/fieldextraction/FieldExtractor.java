package com.secureops.fieldextraction;

import java.io.Serializable;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FieldExtractor implements Serializable{
	private static final long serialVersionUID = 1751327608568541090L;
	public abstract void addExtractor(IFieldExtractorItem extractor);
	protected abstract Collection<IFieldExtractorItem> getExtractors();
	@SuppressWarnings("unused")
	private static Logger abstractClassLogger = LoggerFactory.getLogger(FieldExtractor.class);
	
	/*
	 * Use extract() as this function does not respect Immutablility for Spark serialization
	 */
	@Deprecated
	public ExtractorResult match(String sourceString) throws Exception {
		return this.extract(sourceString);
	}
	
	/*
	 * Spark Friendly function that takes into account Immutable
	 */
	public ExtractorResult extract(String sourceString) throws Exception {
		if(sourceString == null) {
			throw new FieldExtractionNoMatchException("Source String is null, no match found");
		}
		if(sourceString != null) {
			for(IFieldExtractorItem extractor: this.getExtractors()) {
				if(extractor.quickCheck(sourceString)) {
				ExtractorResult doesItMatch = extractor.extract(sourceString);
					if (doesItMatch != null && !doesItMatch.getMatches().isEmpty()) {
						return doesItMatch;
					}
				}
			}
		}

		throw new FieldExtractionNoMatchException("Source string didn't match any parsers: " + sourceString);
	}
}
