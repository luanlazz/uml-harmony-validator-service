package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.inconsistency.javakafka.kafkajava.analyse.model.AnalyseModel;
import com.inconsistency.javakafka.kafkajava.inconsistency.Inconsistency;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyError;
import com.inconsistency.javakafka.kafkajava.inconsistency.InconsistencyType;
import com.inconsistency.javakafka.kafkajava.inconsistency.Severity;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassOperation;
import com.inconsistency.javakafka.kafkajava.uml.models._class.ClassStructure;
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class EpM extends AnalyseModel {
	
	public EpM(KafkaTemplate<String, Object> kafkaTemplate) {
		super(kafkaTemplate, new Inconsistency(InconsistencyType.EpM, Severity.MEDIUM));
	}
	
	@Override
	@KafkaListener(
			topics = "${spring.kafka.topic.model-analyze}", 
			groupId = "epm", 
			clientIdPrefix = "epm",
			containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.handleEvent(payload, ack);
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
        		
        		ClassStructure classReciver = classesMessageMap.get(reciver);
        		
        		boolean operationIsPrivate = false;
        		if (classReciver != null) {
        			for (ClassOperation classOperation : classReciver.getOperations()) {
        				if (classOperation.getName().equals(message) && classOperation.getVisibility().equals("private")) {
        					operationIsPrivate = true;
        					break;
        				}
        			}        			
        		}
        		
        		if (operationIsPrivate) {
        			String errorMessage = "A mensagem " + message + " é privada na classe " + classReciver.getName() + ".";
    				InconsistencyError error = new InconsistencyError("message", message, classReciver.getPackage(), errorMessage);
    				this.addError(error);
        		}
			}  		
			
		}
	}
}
