package com.inconsistency.javakafka.kafkajava.entities;

public class InconsistencyError {

	private String elId;
	private String diagramId;
	private String message;

	public InconsistencyError(String elId, String diagramId, String message) {
		super();
		this.elId = elId;
		this.diagramId = diagramId;
		this.message = message;
	}

	public String getElId() {
		return elId;
	}

	public void setElId(String elId) {
		this.elId = elId;
	}

	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "\n Message: " + this.getMessage() + "\n elId: " + this.getElId() + "\n diagramId: "
				+ this.getDiagramId();
	}
}
