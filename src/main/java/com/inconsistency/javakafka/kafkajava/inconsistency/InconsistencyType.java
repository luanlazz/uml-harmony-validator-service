package com.inconsistency.javakafka.kafkajava.inconsistency;

public enum InconsistencyType {
	CM("CM"),
	Om("Om"),
	CnSD("CnSD"),
	CnCD("CnCD"),
	EnN("EnN"),
	EcM("EcM"),
	EpM("EpM"),
	ED("ED"),
	CaSD("CaSD");	
	
	private final String tag;
	
	InconsistencyType(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}
}