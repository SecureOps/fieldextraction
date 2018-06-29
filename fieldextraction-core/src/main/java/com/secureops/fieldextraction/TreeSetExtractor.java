package com.secureops.fieldextraction;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeSetExtractor extends FieldExtractor implements Serializable{
	private static final long serialVersionUID = -7801651186041975076L;
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(TreeSetExtractor.class);
	private Set<IFieldExtractorItem> extractors = new TreeSet<IFieldExtractorItem>();	
	
	@Override
	public void addExtractor(IFieldExtractorItem item) {
		this.extractors.add(item);
	}

	@Override
	protected Collection<IFieldExtractorItem> getExtractors() {		
		return this.extractors;
	}
	
}
