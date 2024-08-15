package com.inconsistency.javakafka.kafkajava.entities.uml.models._enum;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.uml2.uml.Package;

public class EnumStructure {
	private Package _package;
	private String name;
	private List<String> literals = new ArrayList<>();

	public Package getPackage() {
		return _package;
	}

	public void setPackage(Package _package) {
		this._package = _package;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getLiterals() {
		return literals;
	}

	public void setLiterals(List<String> literals) {
		this.literals = literals;
	}

	public void addLiteral(String literal) {
		this.literals.add(literal);
	}
}
