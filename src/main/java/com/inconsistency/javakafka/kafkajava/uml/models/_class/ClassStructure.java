package com.inconsistency.javakafka.kafkajava.uml.models._class;

import java.util.ArrayList;
import java.util.List;

public class ClassStructure {

    private String _package;
    private List<String> imports = new ArrayList<>();
    private String name;
    private String visibility;
    private String type;
    private boolean _abstract;
    private boolean _final;
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

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
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
    	String output = "\nClass: " + this.getName()
    		+ "\n package: " + this.getPackage()
    		+ "\n type: " + this.getType()
    		+ "\n visibility: " + this.getVisibility()
    		+ "\n isAbstract: " + this.isAbstract();
        	
        if (this.getImports().size() > 0) {
        	output += "\nImports:";
        }
        for (String imp : this.getImports()) {
        	output += "\n name: " + imp;
        }
        
        if (this.getInstances().size() > 0) {
        	output += "\nInstances:";
        }
        for (ClassInstance ci : this.getInstances()) {
        	output += " name: " + ci.getName();
        }
        
        if (this.getRelationships().size() > 0) {
        	output += "\nRelations:";
        }
        for (ClassRelation cr : this.getRelationships()) {
        	output += 
        			"\n class 1: " + cr.getClass_1() +
        			" - class 2: " + cr.getClass_2() +
        			" - role name 1: " + cr.getRole_Name_1() +
        			" - role name 2: " + cr.getRole_Name_2() +
        			"\n multipcity lower 1: " + cr.getMultipcity_Lower_1() +
        			" - multipcity lower 2: " + cr.getMultipcity_Lower_2() +
        			" - multipcity uper 1: " + cr.getMultipcity_Uper_1() +
        			" - multipcity uper 2: " + cr.getMultipcity_Uper_2() +
        			" - isNavigable 1: " + cr.isNavigable_1() +
        			" - isNavigable 2: " + cr.isNavigable_2();
        }
        
        if (this.getSuperClasses().size() > 0) {
        	output += "\nSuper classes:";
        }
        for (ClassStructure sc : this.getSuperClasses()) {
        	output += "\n name: " + sc.getName();     	
        }
        
        if (this.getAttributes().size() > 0) {
        	output += "\nAtributes:";
        }                       
        for (ClassAttribute ca : this.getAttributes()) {
        	output += 
        			"\n name: " + ca.getName() +
        			" - visibility: " + ca.getVisibility() + 
        			" - type: " + ca.getType();
        }
        
        if (this.getOperations().size() > 0) {
        	output += "\nFunctions:";
        }                
        for (ClassOperation co : this.getOperations()) {
        	output += 
        			"\n name: " + co.getName() +
        			" - visibility: " + co.getVisibility() + 
        			" - return type: " + co.getReturnType().getType();
        	
        	if (co.getParameters().size() > 0) {
        		output += "\nParameters:";
        	}                	
        	for (OperationParameter op : co.getParameters()) {
        		output += 
            			"\n name: " + op.getName() +
            			" - visibility: " + op.getVisibility() + 
            			" - type: " + op.getType();
        	}
        }
        
        return output;
    }
}
