package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassInstance;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._enum.EnumStructure;
import com.inconsistency.javakafka.kafkajava.entities.uml.models._package.PackageStructure;
import com.inconsistency.javakafka.kafkajava.uml.reader.PackageReader;

public class ClassDiagramReader implements Serializable {
	private static final long serialVersionUID = 1L;

	public static List<ClassDiagram> getRefModelDetails(Package _package) throws Exception {
		if (_package == null) {
			throw new Exception("[Model] Package is null");
		}

		EList<PackageableElement> packageableElements = _package.getPackagedElements();
		PackageStructure packageStructure = PackageReader.readPackage(packageableElements, _package);

		List<ClassDiagram> classDiagrams = new ArrayList<ClassDiagram>();

		if (packageStructure.getPackages().size() > 0) {
			for (PackageStructure pkgStructure : packageStructure.getPackages()) {
				ClassDiagram classDiagram = new ClassDiagram();
				classDiagram.setId(pkgStructure.getId());
				classDiagram.setName(pkgStructure.getName());
				classDiagram.setVisibility(pkgStructure.getPackage().getVisibility().toString());
				classDiagram.setType(ClassDiagram.class.toString());
				classDiagram.setParentId(pkgStructure.getId());
				classDiagram.getClasses().addAll(classStructures(pkgStructure));
				classDiagram.getInstances().addAll(classInstances(pkgStructure));

				classDiagrams.add(classDiagram);
			}
		}

		if (packageStructure.getClasses().size() > 0) {
			ClassDiagram classDiagram = new ClassDiagram();
			classDiagram.setId(packageStructure.getId());
			classDiagram.setName(packageStructure.getName());
			classDiagram.setVisibility(packageStructure.getPackage().getVisibility().toString());
			classDiagram.setType(ClassDiagram.class.toString());
			classDiagram.setParentId(packageStructure.getId());
			classDiagram.getClasses().addAll(classStructures(packageStructure));
			classDiagram.getInstances().addAll(classInstances(packageStructure));

			classDiagrams.add(classDiagram);
		}

		return classDiagrams;
	}

	protected static ArrayList<ClassInstance> classInstances(PackageStructure packageStructure) {
		ArrayList<ClassInstance> instances = new ArrayList<>();

		for (ClassInstance classInstance : packageStructure.getInstances()) {
			instances.add(classInstance);
		}

		for (PackageStructure ps : packageStructure.getPackages()) {
			instances.addAll(classInstances(ps));
		}
		return instances;
	}

	protected static ArrayList<ClassStructure> classStructures(PackageStructure packageStructure) {
		ArrayList<ClassStructure> classes = new ArrayList<>();

		for (ClassStructure classStructure : packageStructure.getClasses()) {
			classes.add(classStructure);
		}

		for (PackageStructure ps : packageStructure.getPackages()) {
			classes.addAll(classStructures(ps));
		}
		return classes;
	}

	protected static ClassStructure getClassByName(List<ClassStructure> classes, String className) {
		for (ClassStructure classStructure : classes) {
			if (classStructure.getName().equals(className)) {
				return classStructure;
			}
		}

		return null;
	}

	protected static ArrayList<EnumStructure> enumStructure(PackageStructure packageStructure) {
		ArrayList<EnumStructure> enums = new ArrayList<>();

		for (EnumStructure classStructure : packageStructure.getEnums()) {
			enums.add(classStructure);
		}

		for (PackageStructure ps : packageStructure.getPackages()) {
			enums.addAll(enumStructure(ps));
		}
		return enums;
	}
}
