package com.inconsistency.javakafka.kafkajava.inconsistency.strategies;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceLifeline;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class CnSD extends Inconsistency {
	
	public CnSD() {
		super(InconsistencyType.CnSD, Severity.HIGH);
	}
	
	@Override
	@KafkaListener(topics = ("${tpd.topic-name}" + "." + "CnSD"), clientIdPrefix = "CnSD",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenTopic(ConsumerRecord<String, DiagramProperties> cr,
                               @Payload DiagramProperties payload) {
		super.listenTopic(cr, payload);        	
	}
	
	@Override
	public void analyse() {				
		Map<String, SequenceLifeline> sequenceLifelineMap = new HashMap<>();
		
		for (SequenceLifeline lifeline : this.getSequenceDiagram().getLifelines()) {
			sequenceLifelineMap.put(lifeline.getLifelineName(), lifeline);
		}
		
		for (ClassStructure classStructure: this.getClassDiagram().getClasses()) {
			if (sequenceLifelineMap.get(classStructure.getName()) == null && !classStructure.isAbstract()) {
				String errorMessage = "A classe " + classStructure.getName() + " não foi instanciada no diagrama de sequencia.";
				InconsistencyError error = new InconsistencyError("class", classStructure.getName(), classStructure.getPackage(), errorMessage);
				this.addError(error);			
			}
		}
	}
}
