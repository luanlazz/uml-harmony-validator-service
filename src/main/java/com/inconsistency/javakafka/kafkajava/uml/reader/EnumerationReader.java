package com.inconsistency.javakafka.kafkajava.uml.reader;

import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;

import com.inconsistency.javakafka.kafkajava.entities.uml.models._enum.EnumStructure;

public class EnumerationReader {
	public static EnumStructure readEnumeration(PackageableElement element, Package _package) {

		EnumStructure structure = new EnumStructure();
		Enumeration enumeration = (Enumeration) element;
		structure.setName(enumeration.getName());
		structure.setPackage(_package);
		for (EnumerationLiteral literal : enumeration.getOwnedLiterals()) {
			structure.addLiteral(literal.getName());
		}

		return structure;
	}
}
