package com.inconsistency.javakafka.kafkajava.entities.uml.models._sequence;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class SequenceAttribute extends UMLElement {

	private String attributeName;
	private String attributeType;

	public SequenceAttribute() {
		super();
	}

	public SequenceAttribute(String attributeName, String attributeType) {
		super();
		this.attributeName = attributeName;
		this.attributeType = attributeType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	@Override
	public String toString() {
		return "\n+Attribute:" + super.toString() + "\n attributeName: " + this.getAttributeName()
				+ "\n attributeType: " + this.getAttributeType();
	}
}
