package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.uml2.uml.Package;

import com.inconsistency.javakafka.kafkajava.uml.loader.ModelLoader;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.ClassDiagramReader;

public class ClassDiagramReaderService {
	
	public static ClassDiagram classDiagramReader(String filePath) throws IOException {
		File model = new File(filePath);
        Package aPackage = new ModelLoader().loadModel(model);
        return ClassDiagramReader.getRefModelDetails(aPackage);    
    }
	
}
