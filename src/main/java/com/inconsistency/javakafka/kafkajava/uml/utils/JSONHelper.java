package com.inconsistency.javakafka.kafkajava.uml.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;

public class JSONHelper {
	
	public static String classDiagramToJSON(ClassDiagram diagram) throws IOException {     
        try {
        	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        	return ow.writeValueAsString(diagram);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
        
        return null;
    }

    public static ClassDiagram classDiagramFromJSON(String jsonContent) {
    	try {    	        	
        	ObjectMapper mapper = new ObjectMapper();
        	ClassDiagram classDiagram = mapper.readValue(jsonContent, ClassDiagram.class);
        	return classDiagram;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	
		return null;
    }
    
    public static String sequenceDiagramToJSON(SequenceDiagram diagram) throws IOException {      
        try {
        	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        	return ow.writeValueAsString(diagram);        	 
		} catch (Exception e) {
			System.out.println(e.toString());
		}
        
        return null;
    }
    
    public static SequenceDiagram sequenceDiagramFromJSON(String jsonContent) {
    	try {    	        	
        	ObjectMapper mapper = new ObjectMapper();
        	SequenceDiagram sequenceDiagram = mapper.readValue(jsonContent, SequenceDiagram.class);
        	return sequenceDiagram;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	
		return null;
    }
}
