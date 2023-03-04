package com.inconsistency.javakafka.kafkajava.inconsistency;

import java.util.ArrayList;

import org.apache.commons.lang.NotImplementedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;

import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassDiagram;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceDiagram;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;
import com.inconsistency.javakafka.kafkajava.uml.utils.JSONHelper;

public abstract class Inconsistency implements AnalyseInconsistency {
	
	private static final Logger logger = LoggerFactory.getLogger(Inconsistency.class);
	private ClassDiagram classDiagram;
	private SequenceDiagram sequenceDiagram;
	private InconsistencyType inconsistencyType;
	private Severity severity;
	private ArrayList<InconsistencyError> errors;
		
	public Inconsistency(
			InconsistencyType inconsistencyType, 
			Severity severity
	) {
		this.inconsistencyType = inconsistencyType;
		this.severity = severity;
		this.errors = new ArrayList<InconsistencyError>();
	}	

	public void setClassDiagram(ClassDiagram classDiagram) {
		this.classDiagram = classDiagram;
	}

	public void setSequenceDiagram(SequenceDiagram sequenceDiagram) {
		this.sequenceDiagram = sequenceDiagram;
	}

	public ClassDiagram getClassDiagram() {
		return classDiagram;
	}

	public SequenceDiagram getSequenceDiagram() {
		return sequenceDiagram;
	}	
	
	public ArrayList<InconsistencyError> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<InconsistencyError> errors) {
		this.errors = errors;
	}

	public void addError(InconsistencyError error) {
		this.errors.add(error);
	}

	public InconsistencyType getInconsistencyType() {
		return inconsistencyType;
	}
	
	public void setInconsistencyType(InconsistencyType inconsistencyType) {
		this.inconsistencyType = inconsistencyType;
	}
	
	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	
	public boolean hasError() { 
		return this.getErrors().size() > 0;
	}
	
	public void listenTopic(
			ConsumerRecord<String, DiagramProperties> cr,
            @Payload DiagramProperties payload
	) {
		try {
			logger.info("Logger strategy: {} | received key {}", this.getInconsistencyType(), cr.key());            	
			
			ClassDiagram classDiagram = JSONHelper.classDiagramFromJSON(payload.classDiagram());            
			SequenceDiagram sequenceDiagram = JSONHelper.sequenceDiagramFromJSON(payload.sequenceDiagram());
			
			this.setClassDiagram(classDiagram);
			this.setSequenceDiagram(sequenceDiagram);
			
			this.analyse();			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}        
	}
	
	@Override
	public void analyse() {
		throw new NotImplementedException();		
	}
	
	@Override
	public String toString() {
		String output = "Inconsistency: " + this.getInconsistencyType() +
				" - severity: " + this.getSeverity();
		
		for (InconsistencyError error : this.getErrors()) {
			output += "\n" + error.toString();			
		}
		
		return output;
	}
}

