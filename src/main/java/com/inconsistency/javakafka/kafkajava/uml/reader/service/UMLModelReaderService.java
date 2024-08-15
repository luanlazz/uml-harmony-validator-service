package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.inconsistency.javakafka.kafkajava.entities.uml.dto.UMLModelDTO;
import com.inconsistency.javakafka.kafkajava.entities.uml.loader.ModelLoader;

public class UMLModelReaderService {

	public static UMLModelDTO diagramReader(File modelFile) throws Exception {
		Package aPackage = new ModelLoader().loadModel(modelFile);
		UMLModelDTO umlModel = PackageReaderService.getRefModelDetails(aPackage);

		return umlModel;
	}

	public static Package modelReader(String filePath) throws Exception {
		File model = new File(filePath);
		return new ModelLoader().loadModel(model);
	}

	public static UMLResource modelReaderUmlResource(URI filePath) throws Exception {
		File modelFile = new File(filePath.toFileString());
		return (UMLResource) new ModelLoader().registerModel(modelFile);
	}

	public static UMLResource modelReaderUmlResource(String umlContent) throws Exception {
		return (UMLResource) new ModelLoader().registerModel(umlContent);
	}
}
