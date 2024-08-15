package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import java.util.ArrayList;
import java.util.List;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class ClassStructure extends UMLElement {

	private boolean _abstract;
	private boolean _final;
	private List<String> imports = new ArrayList<>();
	private List<String> rules = new ArrayList<>();
	private List<ClassInstance> instances = new ArrayList<>();
	private List<ClassStructure> superClasses = new ArrayList<>();
	private List<ClassAttribute> attributes = new ArrayList<>();
	private List<ClassOperation> operations = new ArrayList<>();
	private List<ClassRelation> relationships = new ArrayList<>();

	public void addRelationship(ClassRelation relationship) {
		relationships.add(relationship);
	}

	public void addImport(String _import) {
		this.imports.add(_import);
	}

	public void addRules(String rule) {
		this.rules.add(rule);
	}

	public void addOperation(ClassOperation operation) {
		this.operations.add(operation);
	}

	public void addAttribute(ClassAttribute attribute) {
		this.attributes.add(attribute);
	}

	public void addInstance(ClassInstance instance) {
		this.instances.add(instance);
	}

	public void addSuperClass(ClassStructure superClass) {
		this.superClasses.add(superClass);
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	public List<String> getRules() {
		return rules;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	public boolean isAbstract() {
		return _abstract;
	}

	public void setAbstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public boolean isFinal() {
		return _final;
	}

	public void setFinal(boolean _final) {
		this._final = _final;
	}

	public List<ClassStructure> getSuperClasses() {
		return superClasses;
	}

	public void setSuperClasses(List<ClassStructure> superClasses) {
		this.superClasses = superClasses;
	}

	public List<ClassAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<ClassAttribute> attributes) {
		this.attributes = attributes;
	}

	public List<ClassOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<ClassOperation> operations) {
		this.operations = operations;
	}

	public List<ClassRelation> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<ClassRelation> relationships) {
		this.relationships = relationships;
	}

	public List<ClassInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<ClassInstance> instances) {
		this.instances = instances;
	}

	@Override
	public String toString() {
		String output = super.toString() + "\n parent: " + this.getParentId() + "\n isAbstract: " + this.isAbstract();

		if (this.getImports().size() > 0) {
			output += "\n\n===Imports:";
		}
		for (String imp : this.getImports()) {
			output += "\n name: " + imp;
		}

		if (this.getInstances().size() > 0) {
			output += "\n\n===Instances:";
		}
		for (ClassInstance ci : this.getInstances()) {
			output += " name: " + ci.getName();
		}

		if (this.getRelationships().size() > 0) {
			output += "\n\n===Relations:";
		}
		for (ClassRelation cr : this.getRelationships()) {
			output += cr.toString();
		}

		if (this.getSuperClasses().size() > 0) {
			output += "\n\n===Super classes:";
		}
		for (ClassStructure sc : this.getSuperClasses()) {
			output += "\n name: " + sc.getName();
		}

		if (this.getAttributes().size() > 0) {
			output += "\n\n===Atributes:";
		}
		for (ClassAttribute ca : this.getAttributes()) {
			output += ca.toString();
		}

		if (this.getOperations().size() > 0) {
			output += "\n\n===Functions:";
		}
		for (ClassOperation co : this.getOperations()) {
			output += co.toString();

			if (co.getParameters().size() > 0) {
				output += "\nParameters:";
			}
			for (OperationParameter op : co.getParameters()) {
				output += op.toString();
			}
		}

		return output;
	}
}