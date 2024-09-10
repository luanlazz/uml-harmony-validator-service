package com.inconsistency.javakafka.kafkajava.entities;

public class InconsistencyConcentration {

	private String id;
	private String parentId;
	private String name;
	private int numInconsistencies;
	private double concentration;
	private String concentrationStr;

	private int severity;
	private String severityLabel;

	public InconsistencyConcentration(String id, String parentId, String name, int numInconsistencies,
			double concentration) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.numInconsistencies = numInconsistencies;
		this.setConcentration(concentration);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration) {
		this.concentration = concentration;
		this.setConcentrationStr(String.format("%.1f", concentration * 100));
		this.calculateConcentration();
	}

	public void calculateConcentration() {
		if (concentration > 0.7) {
			setSeverity(Severity.HIGH);
		} else if (concentration > 0.3 && concentration <= 0.7) {
			setSeverity(Severity.MEDIUM);
		} else {
			setSeverity(Severity.LOW);
		}
	}

	public String getConcentrationStr() {
		return concentrationStr;
	}

	public void setConcentrationStr(String concentrationStr) {
		this.concentrationStr = concentrationStr;
	}

	public int getNumInconsistencies() {
		return numInconsistencies;
	}

	public void setNumInconsistencies(int numInconsistencies) {
		this.numInconsistencies = numInconsistencies;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity.getValue();
		this.severityLabel = severity.name();
	}

	public String getSeverityLabel() {
		return severityLabel;
	}

	@Override
	public String toString() {
		return "\nID: " + getId() + "\nName: " + getName() + "\nNum inconsistencies: " + getNumInconsistencies()
				+ "\nConcentration: " + getConcentrationStr() + "\nSeverity: " + getSeverityLabel();
	}
}
