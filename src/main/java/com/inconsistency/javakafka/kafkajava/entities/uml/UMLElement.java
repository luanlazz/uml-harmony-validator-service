package com.inconsistency.javakafka.kafkajava.entities.uml;

public class UMLElement {

	private String id;
	private String name;
	private String visibility;
	private String type;
	private String parentId;

	private int inconsistenciesCount = 0;

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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parent) {
		this.parentId = parent;
	}

	public int getInconsistenciesCount() {
		return inconsistenciesCount;
	}

	private void setInconsistenciesCount(final int inconsistenciesCount) {
		this.inconsistenciesCount = inconsistenciesCount;
	}

	public void addCount() {
		this.inconsistenciesCount++;
	}

	@Override
	public String toString() {
		return "\n Id: " + this.getId() + "\n Name: " + this.getName() + "\n Type: " + this.getType() + "\n Parent: "
				+ this.getParentId() + "\n Inconsistencies: " + this.getInconsistenciesCount() + "\n Visibility: "
				+ this.getVisibility();
	}
}
