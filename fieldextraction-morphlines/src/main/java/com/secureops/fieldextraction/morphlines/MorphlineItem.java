package com.secureops.fieldextraction.morphlines;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Compiler;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class MorphlineItem implements Serializable{

	private static final long serialVersionUID = -3543667435252415550L;
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MorphlineItem.class);

	private String config = "";
	
	// These get reset to NULL each time the class is serialized
	private transient Command morphline;
	private transient MorphlineRecordEmitter collector;
	

	public MorphlineItem(String config) {
		this.config = config;
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		Compiler compiler = new Compiler();
		collector = new MorphlineRecordEmitter();
		Config hcon = ConfigFactory.parseString(config);
		morphline = compiler.compile(hcon, morphlineContext, collector);
	}
	
	public Map<String, String> extract(String matchString) {
		
		// In the case where the class was serialized, Kryo won't call the constructor since
		// it requires a "config" string that just isn't there. So instead, we "rebuild" the morphline
		// the first time it gets called by the target thread
		if(morphline == null) {
			MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
			Compiler compiler = new Compiler();
			collector = new MorphlineRecordEmitter();
			Config hcon = ConfigFactory.parseString(config);
			morphline = compiler.compile(hcon, morphlineContext, collector);		
		}
        Record inputRecord = new Record();
        inputRecord.put(Fields.MESSAGE, matchString);
        
        Map<String, String> matches = null;
        
        boolean success = morphline.process(inputRecord);
        if (success) {
        	Record outputRecord = collector.getRecord();
        	outputRecord.removeAll(Fields.MESSAGE);
            if (outputRecord.getFields().size() > 0) {
            	matches = new HashMap<String, String>();            	            	
                for (Map.Entry<String, Object> entry : outputRecord.getFields().entries()) {
                		matches.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }
		return matches;
	}
	
}
