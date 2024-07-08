package com.inconsistency.javakafka.kafkajava.inconsistency;

public class Inconsistency {

	private InconsistencyType inconsistencyType;
	private Severity severity;
	private Context context;
	private String elementType;
	private String consistenciesRules;

	public Inconsistency(InconsistencyType inconsistencyType, Severity severity, Context context, String elementType,
			String cr) {
		this.setInconsistencyType(inconsistencyType);
		this.setSeverity(severity);
		this.setContext(context);
		this.setElementType(elementType);
		this.setConsistenciesRules(cr);
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

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
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
