package com.secureops.fieldextraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayExtractor extends FieldExtractor{
	private static final long serialVersionUID = -3319162398667633861L;
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ArrayExtractor.class);
	List<IFieldExtractorItem> extractors = new ArrayList<IFieldExtractorItem>();
	
	@Override
	public void addExtractor(IFieldExtractorItem extractor){
		extractors.add(extractor);
	}

	@Override
	protected Collection<IFieldExtractorItem> getExtractors() {
		return this.extractors;
	}
	
}
