package com.inconsistency.javakafka.kafkajava.entities;

public class InconsistencyError {

	private String propertyName;
	private String umlPackage;
	private String message;

	public InconsistencyError(String propertyName, String umlPackage, String message) {
		super();
		this.propertyName = propertyName;
		this.umlPackage = umlPackage;
		this.message = message;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getUmlPackage() {
		return umlPackage;
	}

	public void setUmlPackage(String umlPackage) {
		this.umlPackage = umlPackage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return " - Property: " + this.getPropertyName() + " - Package: " + this.getUmlPackage() + "\nMessage: "
				+ this.getMessage();
	}
}
