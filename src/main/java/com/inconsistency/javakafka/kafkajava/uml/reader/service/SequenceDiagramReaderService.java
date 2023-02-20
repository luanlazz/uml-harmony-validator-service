package com.inconsistency.javakafka.kafkajava.uml.reader.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.uml2.uml.Package;

import com.inconsistency.javakafka.kafkajava.uml.loader.ModelLoader;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.SequenceDiagramReader;

public class SequenceDiagramReaderService {

	public static SequenceDiagram sequenceDiagramReader(String filePath) throws IOException {
		File model = new File(filePath);
        Package aPackage = new ModelLoader().loadModel(model);
        return SequenceDiagramReader.getRefModelDetails(aPackage);        
    }
}
