package com.secureops.fieldextraction;

public enum UUIDType {
	UUID("uuid"),
	RANDOM("random"),
	TS("timestamp"),
	TSNANO("tsnano"),
	TSUNIXDOTMILLIS("tsdotmilli"),
	INVERTEDTSDOTMILLISSUFFIX("invertedts"),
	TIMEHASH("timehash"),
	EVENTHASH("eventhash"),
	UNSUPPORTED("unsupported");
	
	private final String text;
	
	private UUIDType(final String text) {
		this.text = text;
	}
	
	@Override
	public String toString(){
		return this.text;
	}
	
	public static UUIDType stringToUUIDType(String uuidType){
		for(UUIDType val : UUIDType.values()){
			if(val.toString().equalsIgnoreCase(uuidType)) {
				return val;
			}
		}
		return UUIDType.UNSUPPORTED;
	}
}
