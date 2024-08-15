package com.inconsistency.javakafka.kafkajava.entities;

public class Inconsistency {

	private InconsistencyType inconsistencyType;
	private String elementType;
	private String consistenciesRules;

	public Inconsistency(InconsistencyType inconsistencyType, String elementType, String cr) {
		this.setInconsistencyType(inconsistencyType);
		this.setElementType(elementType);
		this.setConsistenciesRules(cr);
	}

	public InconsistencyType getInconsistencyType() {
		return inconsistencyType;
	}

	public void setInconsistencyType(InconsistencyType inconsistencyType) {
		this.inconsistencyType = inconsistencyType;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getConsistenciesRules() {
		return consistenciesRules;
	}

	public void setConsistenciesRules(String consistenciesRules) {
		this.consistenciesRules = consistenciesRules;
	}
}
