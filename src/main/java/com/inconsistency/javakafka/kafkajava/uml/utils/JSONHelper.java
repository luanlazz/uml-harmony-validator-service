package com.inconsistency.javakafka.kafkajava.uml.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;

public class JSONHelper {
	
	public static String classDiagramToJSON(ClassDiagram diagram) throws Exception {     
        try {
        	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        	return ow.writeValueAsString(diagram);
		} catch (Exception e) {
			throw new Exception("Erro converter diagrama de classe em JSON.");
		}
    }

    public static ClassDiagram classDiagramFromJSON(String jsonContent) throws Exception {
    	try {    	        	
        	ObjectMapper mapper = new ObjectMapper();
        	ClassDiagram classDiagram = mapper.readValue(jsonContent, ClassDiagram.class);
        	return classDiagram;
		} catch (Exception e) {
			throw new Exception("Erro ao carregar classe de JSON.");
		}
    }
    
    public static String sequenceDiagramToJSON(SequenceDiagram diagram) throws Exception {      
        try {
        	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        	return ow.writeValueAsString(diagram);        	 
		} catch (Exception e) {
			throw new Exception("Erro ao converter diagrama de sequencia em JSON.");
		}
    }
    
    public static SequenceDiagram sequenceDiagramFromJSON(String jsonContent) throws Exception {
    	try {    	        	
        	ObjectMapper mapper = new ObjectMapper();
        	SequenceDiagram sequenceDiagram = mapper.readValue(jsonContent, SequenceDiagram.class);
        	return sequenceDiagram;
		} catch (Exception e) {
			throw new Exception("Erro ao carregar diagrama de sequencia de JSON.");
		}
    }
}
