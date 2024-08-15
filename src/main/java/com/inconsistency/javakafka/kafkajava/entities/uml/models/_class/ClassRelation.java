package com.inconsistency.javakafka.kafkajava.entities.uml.models._class;

import com.inconsistency.javakafka.kafkajava.entities.uml.UMLElement;

public class ClassRelation extends UMLElement {

	private String Class_1;
	private String Class_2;
	private String Role_Name_1;
	private String Role_Name_2;
	private int multipcity_Lower_1;
	private int multipcity_Lower_2;
	private int multipcity_Uper_1;
	private int multipcity_Uper_2;
	private boolean navigable_1;
	private boolean Navigable_2;

	public String getClass_1() {
		return Class_1;
	}

	public void setClass_1(String class_1) {
		Class_1 = class_1;
	}

	public String getClass_2() {
		return Class_2;
	}

	public void setClass_2(String class_2) {
		Class_2 = class_2;
	}

	public String getRole_Name_1() {
		return Role_Name_1;
	}

	public void setRole_Name_1(String role_Name_1) {
		Role_Name_1 = role_Name_1;
	}

	public String getRole_Name_2() {
		return Role_Name_2;
	}

	public void setRole_Name_2(String role_Name_2) {
		Role_Name_2 = role_Name_2;
	}

	public boolean isNavigable_1() {
		return navigable_1;
	}

	public void setNavigable_1(boolean navigable_1) {
		this.navigable_1 = navigable_1;
	}

	public boolean isNavigable_2() {
		return Navigable_2;
	}

	public void setNavigable_2(boolean navigable_2) {
		Navigable_2 = navigable_2;
	}

	public int getMultipcity_Lower_1() {
		return multipcity_Lower_1;
	}

	public void setMultipcity_Lower_1(int multipcity_Lower_1) {
		this.multipcity_Lower_1 = multipcity_Lower_1;
	}

	public int getMultipcity_Lower_2() {
		return multipcity_Lower_2;
	}

	public void setMultipcity_Lower_2(int multipcity_Lower_2) {
		this.multipcity_Lower_2 = multipcity_Lower_2;
	}

	public int getMultipcity_Uper_1() {
		return multipcity_Uper_1;
	}

	public void setMultipcity_Uper_1(int multipcity_Uper_1) {
		this.multipcity_Uper_1 = multipcity_Uper_1;
	}

	public int getMultipcity_Uper_2() {
		return multipcity_Uper_2;
	}

	public void setMultipcity_Uper_2(int multipcity_Uper_2) {
		this.multipcity_Uper_2 = multipcity_Uper_2;
	}

	@Override
	public String toString() {
		return "\n+Relation:" + super.toString() + "\n class 1: " + this.getClass_1() + " - class 2: "
				+ this.getClass_2() + " - role name 1: " + this.getRole_Name_1() + " - role name 2: "
				+ this.getRole_Name_2() + "\n multipcity lower 1: " + this.getMultipcity_Lower_1()
				+ " - multipcity lower 2: " + this.getMultipcity_Lower_2() + " - multipcity uper 1: "
				+ this.getMultipcity_Uper_1() + " - multipcity uper 2: " + this.getMultipcity_Uper_2()
				+ " - isNavigable 1: " + this.isNavigable_1() + " - isNavigable 2: " + this.isNavigable_2();
	}
}
