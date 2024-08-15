package com.inconsistency.javakafka.kafkajava.entities.uml.models._package;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.uml2.uml.Package;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._enum.EnumStructure;

public class PackageStructure {

	private String id;
	private String name;
	private Package _package;
	private List<ClassStructure> classes = new ArrayList<>();
	private List<ClassInstance> instances = new ArrayList<>();
	private List<EnumStructure> enums = new ArrayList<>();
	private List<PackageStructure> packages = new ArrayList<>();

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

	public Package getPackage() {
		return _package;
	}

	public void setPackage(Package _package) {
		this._package = _package;
	}

	public List<ClassStructure> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassStructure> classes) {
		this.classes = classes;
	}

	public List<ClassInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<ClassInstance> instances) {
		this.instances = instances;
	}

	public List<EnumStructure> getEnums() {
		return enums;
	}

	public void setEnums(List<EnumStructure> enums) {
		this.enums = enums;
	}

	public List<PackageStructure> getPackages() {
		return packages;
	}

	public void setPackages(List<PackageStructure> packages) {
		this.packages = packages;
	}
}
