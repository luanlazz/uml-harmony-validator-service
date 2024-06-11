package com.inconsistency.javakafka.kafkajava.analyse.model.strategies;

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
import com.inconsistency.javakafka.kafkajava.uml.models._sequence.SequenceMessage;
import com.inconsistency.javakafka.kafkajava.uml.reader.diagram.DiagramProperties;

@Component
public class EnN extends AnalyseModel {
	
	public EnN(KafkaTemplate<String, Object> kafkaTemplate) {
		super(kafkaTemplate, new Inconsistency(InconsistencyType.EnN, Severity.HIGH));
	}
	
	@Override
	@KafkaListener(
			topics = "${spring.kafka.topic.model-analyze}", 
			groupId = "enn", 
			clientIdPrefix = "enn",
			containerFactory = "UMLAnalyseContainerFactory")
	public void listenTopic(@Payload DiagramProperties payload, Acknowledgment ack) {
		super.handleEvent(payload, ack);
	}
	
	@Override
	public void analyse() {
		for (SequenceMessage sequenceMessage: this.getSequenceDiagram().getMessages()) {
			if (sequenceMessage.getMessageName() == null) {				
				String errorMessage = "O objeto " + sequenceMessage.getSender().getLifelineName()  + " possui uma mensagem sem nome.";
				InconsistencyError error = new InconsistencyError("object", sequenceMessage.getSender().getLifelineName(), 
						null, errorMessage);
				this.addError(error);
			}			
		}
	}
}
