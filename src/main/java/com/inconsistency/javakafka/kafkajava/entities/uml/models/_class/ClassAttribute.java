package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class ClassAttribute extends UMLElement {

	private Object value;
	private Object values[];
	private boolean isClass;
	private boolean isEnum;
	private boolean Static;
	private boolean isCollection;

	public ClassAttribute() {
	}

	public ClassAttribute(String name, String type, Object value, boolean isClass, boolean isEnum,
			boolean isCollection) {
		super(name, null, type);
		this.value = value;
		this.isClass = isClass;
		this.isEnum = isEnum;
		this.isCollection = isCollection;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean getIsCollection() {
		return isCollection;
	}

	public boolean isStatic() {
		return Static;
	}

	public void setStatic(boolean aStatic) {
		Static = aStatic;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean collection) {
		isCollection = collection;
	}

	public boolean isClass() {
		return isClass;
	}

	public void setClass(boolean aClass) {
		isClass = aClass;
	}

	public boolean getIsClass() {
		return isClass;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean anEnum) {
		isEnum = anEnum;
	}

	public boolean getIsEnum() {
		return isEnum;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "\n\n+Attribute: " + super.toString() + "\n Value: " + this.getValue() + "\n isClass: " + this.isClass()
				+ "\n isEnum: " + this.isEnum() + "\n Static: " + this.isStatic() + "\n isCollection: "
				+ this.isCollection();
	}
}
