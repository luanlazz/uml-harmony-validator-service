package com.inconsistency.javakafka.kafkajava.uml;

public class UMLElement {

	private String id;
	private String name;
	private String visibility;
	private String type;

	public UMLElement() {
	}

	public UMLElement(String name, String visibility, String type) {
		this.name = name;
		this.visibility = visibility;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
