package com.inconsistency.javakafka.kafkajava.inconsistency.strategies;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
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
public class ED extends Inconsistency {
	
	public ED() {
		super(InconsistencyType.ED, Severity.MEDIUM);
	}
	
	@Override
	@KafkaListener(topics = "uml.inconsistency.ed", containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.listenTopic(payload, ack);
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
				String sender = sequenceMessage.getSender().getLifelineName();
				
        		ClassStructure classReciver = classesMessageMap.get(reciver);
        		ClassStructure classSender = classesMessageMap.get(sender);
        		
        		boolean hasOperationReciver = false;
        		boolean hasOperationSender = false;
        		if (classReciver != null && classSender != null) {
        			for (ClassOperation classOperation : classReciver.getOperations()) {
        				if (classOperation.getName().equals(message)) {
        					hasOperationReciver = true;
        					break;
        				}
        			}     
        			
        			if (!hasOperationReciver) {
        				for (ClassOperation classOperation : classSender.getOperations()) {
            				if (classOperation.getName().equals(message)) {
            					hasOperationSender = true;
            					break;
            				}
            			}  
        			}
        		}
        		
        		if (!hasOperationReciver && hasOperationSender) {
        			String errorMessage = "A mensagem " + message + " não foi definida na classe " + classReciver.getName() +".";
    				InconsistencyError error = new InconsistencyError("message", message, classReciver.getPackage(), errorMessage);
    				this.addError(error);
        		}        		
			}  					
		}
	}
}
