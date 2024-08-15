package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class OperationParameter extends UMLElement {

	private String direction;
	private Object value;
	private boolean Class;
	private boolean collection;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isClass() {
		return Class;
	}

	public void setClass(boolean aClass) {
		Class = aClass;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	@Override
	public String toString() {
		String output = super.toString() + "\n direction: " + this.getDirection() + "\n value: " + this.getValue()
				+ "\n Class: " + this.isClass() + "\n collection: " + this.isCollection();

		return output;
	}
}
