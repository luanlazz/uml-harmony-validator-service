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
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassOperation;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class EcM extends Inconsistency {
	
	public EcM() {
		super(InconsistencyType.EcM, Severity.MEDIUM);
	}
	
	@Override
	@KafkaListener(topics = ("${tpd.topic-name}" + "." + "EcM"), clientIdPrefix = "EcM",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenTopic(ConsumerRecord<String, DiagramProperties> cr,
                               @Payload DiagramProperties payload) {
		super.listenTopic(cr, payload);        	
	}
	
	@Override
	public void analyse() {	
		Map<String, ClassStructure> classesMessageMap = new HashMap<>();
		
		for (ClassStructure classStructure: this.getClassDiagram().getClasses()) {
			classesMessageMap.put(classStructure.getName(), classStructure);
		}
		
		for (SequenceMessage sequenceMessage: this.getSequenceDiagram().getMessages()) {
			if (sequenceMessage.getMessageType() != null 
					&& (
							sequenceMessage.getMessageType().equals("createMessage")
							|| sequenceMessage.getMessageType().equals("synchCall")
							|| sequenceMessage.getMessageType().equals("asynchCall") 
						)
			) {
				String message = sequenceMessage.getMessageName();
				String reciver = sequenceMessage.getReciver().getLifelineName();
        		
        		ClassStructure classRececiver = classesMessageMap.get(reciver);
        		
        		boolean hasOperation = false;
        		if (classRececiver != null) {
        			for (ClassOperation classOperation : classRececiver.getOperations()) {
        				if (classOperation.getName().equals(message)) {
        					hasOperation = true;
        					break;
        				}
        			}        			
        		}
        		
        		if (!hasOperation) {
        			String errorMessage = "A mensagem " + message + " não foi definida na classe " + classRececiver.getName() + ".";
    				InconsistencyError error = new InconsistencyError("message", message, classRececiver.getPackage(), errorMessage);
    				this.addError(error);
        		}
        		
			}  		
			
		}
	}
}
