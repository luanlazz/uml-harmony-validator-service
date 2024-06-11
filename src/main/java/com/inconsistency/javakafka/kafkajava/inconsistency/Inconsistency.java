package com.inconsistency.javakafka.kafkajava.inconsistency;

public class Inconsistency {

	private InconsistencyType inconsistencyType;
	private Severity severity;

	public Inconsistency(InconsistencyType inconsistencyType, Severity severity) {
		this.inconsistencyType = inconsistencyType;
		this.severity = severity;
	}

	public InconsistencyType getInconsistencyType() {
		return inconsistencyType;
	}

	public void setInconsistencyType(InconsistencyType inconsistencyType) {
		this.inconsistencyType = inconsistencyType;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
}
