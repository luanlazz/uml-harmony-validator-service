package com.inconsistency.javakafka.kafkajava.uml.reader;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.uml2.uml.Element;

public final class ReaderUtils {

	public static String getXMLId(Element element) {
		String id = "";

		if (element.eResource() != null) {
			XMLResource xmlResource = (XMLResource) element.eResource();
			id = xmlResource.getID(element);
		}

		return id;
	}
}
