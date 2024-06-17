package com.inconsistency.javakafka.kafkajava.uml.models._class;

import com.inconsistency.javakafka.kafkajava.uml.UMLElement;

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
		String output = "\nId: " + this.getId() + "\nname: " + this.getName() + "\ntype: " + this.getType()
				+ "\nvisibility: " + this.getVisibility() + "\ndirection: " + this.getDirection() + "\nvalue: "
				+ this.getValue() + "\nClass: " + this.isClass() + "\ncollection: " + this.isCollection();

		return output;
	}
}
