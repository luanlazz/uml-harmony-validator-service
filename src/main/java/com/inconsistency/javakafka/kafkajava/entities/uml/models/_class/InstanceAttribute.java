package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class InstanceAttribute extends UMLElement {

	private Object values[];
	private boolean isClass;
	private boolean isEnum;
	private boolean isCollection;

	public InstanceAttribute() {

	}

	public InstanceAttribute(String name, String type, Object values[], boolean isClass, boolean isEnum,
			boolean isCollection) {
		super(name, null, type);
		this.values = values;
		this.isClass = isClass;
		this.isEnum = isEnum;
		this.isCollection = isCollection;
	}

	public boolean getIsClass() {
		return isClass;
	}

	public boolean getIsEnum() {
		return isEnum;
	}

	public boolean getIsCollection() {
		return isCollection;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object values[]) {
		this.values = values;
	}

	public boolean isClass() {
		return isClass;
	}

	public void setClass(boolean aClass) {
		isClass = aClass;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean anEnum) {
		isEnum = anEnum;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean collection) {
		isCollection = collection;
	}
}
