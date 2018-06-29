package com.secureops.fieldextraction.morphlines;

import java.io.Serializable;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;

final public class MorphlineRecordEmitter implements Command, Serializable {

	private static final long serialVersionUID = 960566745681410606L;
	private Record record = null;

    @Override
    public Command getParent() {
        return null;
    }

    @Override
    public boolean process(Record record) {
        setRecord(record);
        return true;
    }

    @Override
    public void notify(Record arg0) {
    }

    /**
     * @return the record
     */
    public Record getRecord() {
        return record;
    }

    /**
     * @param record
     *            the record to set
     */
    public void setRecord(Record record) {
        this.record = record;
    }
}