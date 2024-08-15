package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import java.util.ArrayList;
import java.util.List;

public class ClassInstance {

	private String name;
	private List<ClassStructure> classes = new ArrayList<>();
	private List<InstanceAttribute> attributes = new ArrayList<>();

	public void addAttribute(InstanceAttribute attribute) {
		this.attributes.add(attribute);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClassStructure> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassStructure> classes) {
		this.classes = classes;
	}

	public List<InstanceAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<InstanceAttribute> attributes) {
		this.attributes = attributes;
	}
}
